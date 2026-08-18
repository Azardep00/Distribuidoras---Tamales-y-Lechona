package model;

import java.math.BigDecimal;

public class Detallecompra {

    private int idDetalle;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public Detallecompra(int idDetalle, Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.idDetalle = idDetalle;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    // ---- Getters y Setters ----
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    // ---- Metodo del diagrama ----
    public BigDecimal calcularSubtotal() {
        if (precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return this.subtotal;
    }

    @Override
    public String toString() {
        return "DetalleCompra{" +
                "idDetalle=" + idDetalle +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}