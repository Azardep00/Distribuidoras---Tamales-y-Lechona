package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private int idInventario;
    private String nombre;
    private LocalDateTime fechaActualizacion;
    private boolean estado;

    private List<Producto> productos;

    public Inventario(int idInventario, String nombre, LocalDateTime fechaActualizacion, boolean estado) {
        this.idInventario = idInventario;
        this.nombre = nombre;
        this.fechaActualizacion = fechaActualizacion;
        this.estado = estado;
        this.productos = new ArrayList<>();
    }

    // ---- Getters y Setters ----
    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    // ---- Metodos del diagrama ----

    public int consultarStock(Producto producto) {
        return producto != null ? producto.getStock() : 0;
    }

    public boolean verificarDisponibilidad(Producto producto, int cantidad) {
        return producto != null && producto.consultarDisponibilidad(cantidad);
    }

    public void actualizarInventario() {
        this.fechaActualizacion = LocalDateTime.now();
        System.out.println("Inventario '" + nombre + "' actualizado el " + fechaActualizacion);
    }

    @Override
    public String toString() {
        return "Inventario{" +
                "idInventario=" + idInventario +
                ", nombre='" + nombre + '\'' +
                ", fechaActualizacion=" + fechaActualizacion +
                ", estado=" + estado +
                ", productos=" + productos.size() +
                '}';
    }
}