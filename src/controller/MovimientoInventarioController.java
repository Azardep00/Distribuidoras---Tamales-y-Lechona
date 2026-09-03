package controller;

import java.util.*;

import model.*;
import observer.*;
import repository.IMovimientoInventarioRepository;

public class MovimientoInventarioController {
    private final IMovimientoInventarioRepository repo;
    private int siguienteId = 1;
    private final List<InventarioObserver> observers = new ArrayList<>();

    public MovimientoInventarioController(IMovimientoInventarioRepository repo) {
        this.repo = repo;
        agregarObserver(new AuditoriaInventario());
        agregarObserver(new AlertaStock(5));
    }

    public void agregarObserver(InventarioObserver o) {
        if (o != null && !observers.contains(o)) observers.add(o);
    }

    public MovimientoInventario registrarEntrada(
            Producto p, int cantidad, Proveedor proveedor, String motivo) {
        if (p == null || proveedor == null)
            throw new IllegalArgumentException("Producto y proveedor son obligatorios para una entrada.");
        validarCantidad(cantidad);
        MovimientoInventario m =
                new MovimientoInventario(
                        siguienteId++, TipoMovimiento.ENTRADA, cantidad, motivo, p, proveedor);
        p.aumentarStock(cantidad);
        guardar(m);
        return m;
    }

    public MovimientoInventario registrarReversion(Producto p, int cantidad, String motivo) {
        if (p == null) throw new IllegalArgumentException("Producto obligatorio.");
        validarCantidad(cantidad);
        MovimientoInventario m =
                new MovimientoInventario(siguienteId++, TipoMovimiento.ENTRADA, cantidad, motivo, p, null);
        p.aumentarStock(cantidad);
        guardar(m);
        return m;
    }

    public MovimientoInventario registrarSalida(Producto p, int cantidad, String motivo) {
        if (p == null) throw new IllegalArgumentException("Producto obligatorio.");
        validarCantidad(cantidad);
        p.descontarStock(cantidad);
        MovimientoInventario m =
                new MovimientoInventario(siguienteId++, TipoMovimiento.SALIDA, cantidad, motivo, p);
        guardar(m);
        return m;
    }

    private void guardar(MovimientoInventario m) {
        repo.guardar(m);
        for (InventarioObserver o : observers) o.actualizar(m);
    }

    private void validarCantidad(int c) {
        if (c <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
    }

    public List<MovimientoInventario> listar() {
        return repo.listarTodos();
    }

    public int totalMovimientos() {
        return repo.listarTodos().size();
    }
}
