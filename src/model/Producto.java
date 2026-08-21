package Model;

import java.math.BigDecimal;

public abstract class Producto {

    // Atributos

    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private boolean estado;

    // Constructor

    public Producto(int idProducto, String nombre, String descripcion, BigDecimal precio, int stock, boolean estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    // Metodos

    /**
     * Metodo para Consultar disponibilidad en el Stock y de acuerdo a lo que se evalue,
     * tomar una decisión acertada, si el retorno de estado y stock es mayor a cero significa
     * que hay stock, de lo contrario no hay nada.
     * @return true si el producto está disponible, false en caso contrario.
     */
    public boolean consultarDisponibilidad()
    {
        return estado && stock > 0;
    }

    /**
     * Metodo para actualizar el precio de un producto, para ello se declara una nueva
     * variable de nuevoPrecio
     * @param nuevoPrecio nuevo precio del producto.
     */
    public void actualizarPrecio(BigDecimal nuevoPrecio)
    {
        if (nuevoPrecio != null && nuevoPrecio.compareTo(BigDecimal.ZERO) >= 0)
        {
            this.precio = nuevoPrecio;
        }
    }

    /**
     * Metodo para cambiar el estado de un producto
     * @param nuevoEstado nuevo estado del producto.
     */
    public void cambiarEstado(boolean nuevoEstado)
    {
        this.estado = nuevoEstado;
    }

    public boolean descontarStock(int cantidad) {
        if (cantidad > 0 && cantidad <= this.stock) {
            this.stock -= cantidad;
            return true;
        }
        return false;
    }

    public boolean validarProducto() {
        return nombre != null && !nombre.isBlank()
                && idProducto != 0
                && precio != null && precio.compareTo(BigDecimal.ZERO) >= 0
                && stock >= 0;
    }


    // Getters

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public boolean isEstado() {
        return estado;
    }

    // Setters

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // to String

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