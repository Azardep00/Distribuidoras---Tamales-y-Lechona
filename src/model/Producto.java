package model;

import java.math.BigDecimal;

public abstract class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private boolean estado;

    protected Producto(
            int idProducto,
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stock,
            boolean estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int v) {
        idProducto = v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        nombre = v;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String v) {
        descripcion = v;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal v) {
        precio = v;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int v) {
        stock = v;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean v) {
        estado = v;
    }

    public boolean consultarDisponibilidad(int cantidad) {
        return estado && cantidad > 0 && stock >= cantidad;
    }

    public void descontarStock(int cantidad) {
        if (!consultarDisponibilidad(cantidad))
            throw new IllegalArgumentException("Stock insuficiente para " + nombre + ".");
        stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        stock += cantidad;
    }

    public abstract BigDecimal calcularPrecio();

    public abstract String getTipoProducto();

    public abstract String getDetalleEspecifico();

    @Override
    public String toString() {
        return nombre;
    }
}
