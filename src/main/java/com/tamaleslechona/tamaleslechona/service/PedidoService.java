package com.tamaleslechona.tamaleslechona.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tamaleslechona.tamaleslechona.client.NotificacionClient;
import com.tamaleslechona.tamaleslechona.dto.DetallePedidoRequest;
import com.tamaleslechona.tamaleslechona.event.PedidoEstadoEvent;
import com.tamaleslechona.tamaleslechona.event.PedidoEventPublisher;
import com.tamaleslechona.tamaleslechona.exception.RecursoNoEncontradoException;
import com.tamaleslechona.tamaleslechona.model.Cliente;
import com.tamaleslechona.tamaleslechona.model.DetallePedido;
import com.tamaleslechona.tamaleslechona.model.EstadoPagoWompi;
import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.PagoWompi;
import com.tamaleslechona.tamaleslechona.model.Pedido;
import com.tamaleslechona.tamaleslechona.model.Producto;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import com.tamaleslechona.tamaleslechona.repository.PedidoRepository;
import com.tamaleslechona.tamaleslechona.repository.PagoWompiRepository;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final PagoWompiRepository pagoWompiRepo;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    // Reutilizamos MovimientoInventarioService para descontar/devolver stock:
    // así un Pedido queda automáticamente enganchado a los observadores de
    // inventario (AuditoriaInventario, AlertaStock) sin duplicar esa lógica.
    private final MovimientoInventarioService movimientoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    private final NotificacionClient notificacionClient;
    private final PedidoEventPublisher pedidoEventPublisher;

    // Interruptor del transporte de notificaciones. En false (por defecto) se
    // notifica por REST como siempre; en true se publica un evento en Kafka.
    @Value("${notificaciones.kafka.habilitado:false}")
    private boolean kafkaHabilitado;

    public PedidoService(
            PedidoRepository repo,
            PagoWompiRepository pagoWompiRepo,
            UsuarioService usuarioService,
            ProductoService productoService,
            MovimientoInventarioService movimientoService,
            NotificacionClient notificacionClient,
            PedidoEventPublisher pedidoEventPublisher) {
        this.repo = repo;
        this.pagoWompiRepo = pagoWompiRepo;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.movimientoService = movimientoService;
        this.notificacionClient = notificacionClient;
        this.pedidoEventPublisher = pedidoEventPublisher;
    }

    @Transactional
    public Pedido crear(Integer idCliente, List<DetallePedidoRequest> detallesReq) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El id del cliente es obligatorio.");
        }
        if (detallesReq == null || detallesReq.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }

        Cliente cliente = buscarCliente(idCliente);

        // Se valida disponibilidad de TODOS los productos antes de descontar
        // cualquier stock: si uno solo no alcanza, el pedido completo se
        // rechaza y no queda ningún producto descontado a medias.
        for (DetallePedidoRequest d : detallesReq) {
            if (d.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
            }
            Producto producto = productoService.buscarPorId(d.idProducto());
            if (!producto.consultarDisponibilidad(d.cantidad())) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre() + ".");
            }
        }

        Pedido pedido = repo.save(new Pedido(cliente));

        for (DetallePedidoRequest d : detallesReq) {
            Producto producto = productoService.buscarPorId(d.idProducto());
            BigDecimal precioUnitario = producto.getPrecio();

            // Descuenta el stock y deja el registro de salida correspondiente.
            movimientoService.registrarSalida(
                    d.idProducto(), d.cantidad(), "Venta - Pedido #" + pedido.getIdPedido());

            pedido.agregarDetalle(new DetallePedido(producto, d.cantidad(), precioUnitario));
        }

        return repo.save(pedido);
    }

    public List<Pedido> listar() {
        return incluirEstadoPagoWompi(repo.findAll());
    }

    public Pedido buscarPorId(int id) {
        Pedido pedido = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado."));
        incluirEstadoPagoWompi(List.of(pedido));
        return pedido;
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        return incluirEstadoPagoWompi(repo.findByCliente_IdUsuario(idCliente));
    }

    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return repo.findByEstado(estado);
    }

    // Solo permite avanzar un paso dentro de la secuencia normal
    // (PENDIENTE -> CONFIRMADO -> EN_PREPARACION -> ENTREGADO); ni saltar
    // estados ni retroceder. Cancelar tiene su propio método/endpoint.
    public Pedido cambiarEstado(int id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(id);
        EstadoPedido actual = pedido.getEstado();

        if (actual == EstadoPedido.CANCELADO || actual == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Un pedido " + actual + " ya no puede cambiar de estado.");
        }
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("Para cancelar un pedido usa DELETE /api/pedidos/{id}.");
        }
        if (nuevoEstado == EstadoPedido.CONFIRMADO
                && tienePagoWompi(pedido)
                && !pagoWompiRepo.existsByPedido_IdPedidoAndEstado(
                        id, EstadoPagoWompi.APPROVED)) {
            throw new IllegalArgumentException("El pedido solo se puede confirmar cuando Wompi apruebe el pago.");
        }

        EstadoPedido siguiente = siguienteEstado(actual);
        if (siguiente != nuevoEstado) {
            throw new IllegalArgumentException("Un pedido " + actual + " solo puede pasar a " + siguiente + ".");
        }

        pedido.setEstado(nuevoEstado);
        Pedido guardado = repo.save(pedido);

        // Efecto secundario. Dos caminos segun configuracion:
        //  - Kafka (notificaciones.kafka.habilitado=true): se publica un evento
        //    y el consumidor decide cuando procesarlo.
        //  - REST (por defecto): se llama al microservicio como siempre.
        if (kafkaHabilitado) {
            pedidoEventPublisher.publicar(new PedidoEstadoEvent(
                    guardado.getIdPedido(),
                    guardado.getCliente().getCorreo(),
                    guardado.getEstado().name(),
                    Instant.now()));
        } else {
            // Best effort: si el microservicio de notificaciones falla o no
            // esta disponible, el cambio de estado ya quedo guardado.
            try {
                notificacionClient.notificarCambioEstado(
                        guardado.getIdPedido(),
                        guardado.getCliente().getCorreo(),
                        guardado.getEstado().name());
            } catch (Exception e) {
                log.warn("No se pudo notificar el pedido #{}: {}", guardado.getIdPedido(), e.getMessage());
            }
        }
        return guardado;
    }

    @Transactional
    public void cancelar(int id) {
        Pedido pedido = buscarPorId(id);

        if (pagoWompiRepo.existsByPedido_IdPedidoAndEstadoIn(id, List.of(
                EstadoPagoWompi.PENDING,
                EstadoPagoWompi.APPROVED))) {
            throw new IllegalArgumentException(
                    "No se puede cancelar un pago Wompi pendiente o aprobado. Gestiona primero el pago o su reembolso.");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Un pedido ya entregado no se puede cancelar.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya está cancelado.");
        }

        // Devuelve al inventario todo lo que este pedido había descontado,
        // dejando también el registro de reversión correspondiente.
        for (DetallePedido detalle : pedido.getDetalles()) {
            movimientoService.registrarReversion(
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad(),
                    "Cancelación - Pedido #" + pedido.getIdPedido());
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        repo.save(pedido);
    }

    private Cliente buscarCliente(int idCliente) {
        Usuario usuario = usuarioService.buscarPorId(idCliente);
        if (!(usuario instanceof Cliente cliente)) {
            throw new IllegalArgumentException("El usuario indicado no es un cliente.");
        }
        if (!cliente.isEstado()) {
            throw new IllegalArgumentException("El cliente está inactivo.");
        }
        return cliente;
    }

    private EstadoPedido siguienteEstado(EstadoPedido actual) {
        return switch (actual) {
            case PENDIENTE -> EstadoPedido.CONFIRMADO;
            case CONFIRMADO -> EstadoPedido.EN_PREPARACION;
            case EN_PREPARACION -> EstadoPedido.ENTREGADO;
            default -> null;
        };
    }

    private boolean tienePagoWompi(Pedido pedido) {
        return pagoWompiRepo.existsByPedido_IdPedidoAndEstadoIn(
                pedido.getIdPedido(),
                List.of(
                        EstadoPagoWompi.PENDING,
                        EstadoPagoWompi.APPROVED,
                        EstadoPagoWompi.DECLINED,
                        EstadoPagoWompi.VOIDED,
                        EstadoPagoWompi.ERROR));
    }

    private List<Pedido> incluirEstadoPagoWompi(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) return pedidos;

        List<Integer> idsPedido = pedidos.stream().map(Pedido::getIdPedido).toList();
        Map<Integer, PagoWompi> pagosRecientes = new HashMap<>();
        for (var pago : pagoWompiRepo.findByPedido_IdPedidoIn(idsPedido)) {
            pagosRecientes.merge(
                    pago.getPedido().getIdPedido(),
                    pago,
                    (anterior, actual) -> anterior.getIdPagoWompi() > actual.getIdPagoWompi()
                            ? anterior
                            : actual);
        }
        for (Pedido pedido : pedidos) {
            var pago = pagosRecientes.get(pedido.getIdPedido());
            if (pago != null) pedido.setEstadoPagoWompi(pago.getEstado());
        }
        return pedidos;
    }
}