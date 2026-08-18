package model;

import java.time.LocalDateTime;

public class Movimientoinventario {

    private int idMovimiento;
    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    private Producto producto;

    public Movimientoinventario(int idMovimiento, TipoMovimiento tipo, int cantidad,
                                LocalDateTime fecha, String motivo, Producto producto) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
        this.producto = producto;
    }

    // ---- Getters y Setters ----
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

    // ---- Metodos del diagrama ----

    public void registrarEntrada() {
        this.tipo = TipoMovimiento.ENTRADA;
        if (producto != null) {
            producto.setStock(producto.getStock() + cantidad);
        }
        System.out.println("Entrada registrada: +" + cantidad + " de " + (producto != null ? producto.getNombre() : ""));
    }

    public void registrarSalida() {
        this.tipo = TipoMovimiento.SALIDA;
        if (producto != null && producto.getStock() >= cantidad) {
            producto.setStock(producto.getStock() - cantidad);
        } else {
            System.out.println("Stock insuficiente para registrar la salida");
        }
    }

    public void registrarAjuste(int nuevoStock) {
        this.tipo = TipoMovimiento.AJUSTE;
        if (producto != null) {
            this.cantidad = nuevoStock - producto.getStock();
            producto.setStock(nuevoStock);
        }
    }

    @Override
    public String toString() {
        return "MovimientoInventario{" +
                "idMovimiento=" + idMovimiento +
                ", tipo=" + tipo +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", motivo='" + motivo + '\'' +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                '}';
    }
}