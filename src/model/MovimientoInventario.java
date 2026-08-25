package model;

import java.time.LocalDateTime;

public class MovimientoInventario {

    private int idMovimiento;
    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;
    private Producto producto;
    private Proveedor proveedor; // Solo aplica para ENTRADA

    // Constructor sin proveedor (usado en SALIDAS)
    public MovimientoInventario(int idMovimiento, TipoMovimiento tipo, int cantidad, String motivo, Producto producto) {
        this(idMovimiento, tipo, cantidad, motivo, producto, null);
    }

    // Constructor con proveedor (usado en ENTRADAS)
    public MovimientoInventario(int idMovimiento, TipoMovimiento tipo, int cantidad, String motivo,
                                Producto producto, Proveedor proveedor) {

        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.producto = producto;
        this.proveedor = proveedor;
    }

    public boolean registrarMovimiento() {

        if (tipo == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + cantidad);
            return true;
        }

        if (tipo == TipoMovimiento.SALIDA) {
            if (producto.consultarDisponibilidad(cantidad)) {
                producto.setStock(producto.getStock() - cantidad);
                return true;
            }
            return false;
        }

        return false;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}