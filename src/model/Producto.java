package model;

import java.math.BigDecimal;

public abstract class Producto {

    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private boolean estado;

    public Producto(int idProducto, String nombre, String descripcion, BigDecimal precio,
                    int stock, boolean estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    // ---- Getters y Setters ----
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // ---- Metodos del diagrama ----

    public boolean consultarDisponibilidad(int cantidadRequerida) {
        return estado && stock >= cantidadRequerida;
    }

    /**
     * Cada subtipo de Producto (Tamal, Lechona) define su propia forma
     * de calcular el precio (por peso, por porciones, etc). Principio OCP:
     * se pueden agregar nuevos productos sin modificar esta clase.
     */
    public abstract BigDecimal calcularPrecio();

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", estado=" + estado +
                '}';
    }
}