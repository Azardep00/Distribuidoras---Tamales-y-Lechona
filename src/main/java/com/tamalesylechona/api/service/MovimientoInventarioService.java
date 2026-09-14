package main.java.com.tamalesylechona.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tamalesylechona.api.exception.RecursoNoEncontradoException;
import com.tamalesylechona.api.model.MovimientoInventario;
import com.tamalesylechona.api.model.Producto;
import com.tamalesylechona.api.model.Proveedor;
import com.tamalesylechona.api.model.TipoMovimiento;
import com.tamalesylechona.api.observer.InventarioObserver;
import com.tamalesylechona.api.repository.MovimientoInventarioRepository;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repo;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    // Spring inyecta aquí, automáticamente, TODOS los @Component que implementen
    // InventarioObserver (hoy: AuditoriaInventario y AlertaStock). Si mañana
    // agregas un tercer observador, con anotarlo @Component ya queda enganchado,
    // sin tocar esta clase — esa es la gracia del patrón Observer + Spring.
    private final List<InventarioObserver> observers;

    public MovimientoInventarioService(
            MovimientoInventarioRepository repo,
            ProductoService productoService,
            ProveedorService proveedorService,
            List<InventarioObserver> observers) {
        this.repo = repo;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
        this.observers = observers;
    }

    public MovimientoInventario registrarEntrada(
            int idProducto, int idProveedor, int cantidad, String motivo) {
        validarCantidad(cantidad);
        Producto producto = productoService.buscarPorId(idProducto);
        Proveedor proveedor = proveedorService.buscarPorId(idProveedor);

        producto.aumentarStock(cantidad);
        productoService.guardar(producto);

        MovimientoInventario m =
                new MovimientoInventario(TipoMovimiento.ENTRADA, cantidad, motivo, producto, proveedor);
        return guardarYNotificar(m);
    }

    // Entrada sin proveedor asociado (por ejemplo, devolución de un pedido cancelado)
    public MovimientoInventario registrarReversion(int idProducto, int cantidad, String motivo) {
        validarCantidad(cantidad);
        Producto producto = productoService.buscarPorId(idProducto);

        producto.aumentarStock(cantidad);
        productoService.guardar(producto);

        MovimientoInventario m =
                new MovimientoInventario(TipoMovimiento.ENTRADA, cantidad, motivo, producto, null);
        return guardarYNotificar(m);
    }

    public MovimientoInventario registrarSalida(int idProducto, int cantidad, String motivo) {
        validarCantidad(cantidad);
        Producto producto = productoService.buscarPorId(idProducto);

        // descontarStock ya valida disponibilidad y lanza IllegalArgumentException si no alcanza
        producto.descontarStock(cantidad);
        productoService.guardar(producto);

        MovimientoInventario m =
                new MovimientoInventario(TipoMovimiento.SALIDA, cantidad, motivo, producto, null);
        return guardarYNotificar(m);
    }

    public List<MovimientoInventario> listar() {
        return repo.findAll();
    }

    public MovimientoInventario buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado."));
    }

    public List<MovimientoInventario> listarPorProducto(int idProducto) {
        return repo.findByProducto_IdProducto(idProducto);
    }

    public List<MovimientoInventario> listarPorTipo(TipoMovimiento tipo) {
        return repo.findByTipo(tipo);
    }

    private MovimientoInventario guardarYNotificar(MovimientoInventario m) {
        MovimientoInventario guardado = repo.save(m);
        observers.forEach(o -> o.actualizar(guardado));
        return guardado;
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
    }
}
