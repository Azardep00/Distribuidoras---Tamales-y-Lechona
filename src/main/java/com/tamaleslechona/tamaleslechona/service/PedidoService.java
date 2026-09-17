package com.tamaleslechona.tamaleslechona.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tamaleslechona.tamaleslechona.dto.DetallePedidoRequest;
import com.tamaleslechona.tamaleslechona.exception.RecursoNoEncontradoException;
import com.tamaleslechona.tamaleslechona.model.Cliente;
import com.tamaleslechona.tamaleslechona.model.DetallePedido;
import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.Pedido;
import com.tamaleslechona.tamaleslechona.model.Producto;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import com.tamaleslechona.tamaleslechona.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    // Reutilizamos MovimientoInventarioService para descontar/devolver stock:
    // así un Pedido queda automáticamente enganchado a los observadores de
    // inventario (AuditoriaInventario, AlertaStock) sin duplicar esa lógica.
    private final MovimientoInventarioService movimientoService;

    public PedidoService(
            PedidoRepository repo,
            UsuarioService usuarioService,
            ProductoService productoService,
            MovimientoInventarioService movimientoService) {
        this.repo = repo;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.movimientoService = movimientoService;
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
        return repo.findAll();
    }

    public Pedido buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado."));
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        return repo.findByCliente_IdUsuario(idCliente);
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

        EstadoPedido siguiente = siguienteEstado(actual);
        if (siguiente != nuevoEstado) {
            throw new IllegalArgumentException("Un pedido " + actual + " solo puede pasar a " + siguiente + ".");
        }

        pedido.setEstado(nuevoEstado);
        return repo.save(pedido);
    }

    @Transactional
    public void cancelar(int id) {
        Pedido pedido = buscarPorId(id);

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
}