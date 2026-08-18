package model;

import java.math.BigDecimal;

public class Lechona extends Producto {

    private BigDecimal peso;
    private int porciones;
    private String tipoPresentacion; // ej: "entera", "media", "por porcion"

    public Lechona(int idProducto, String nombre, String descripcion, BigDecimal precio,
                   int stock, boolean estado, BigDecimal peso, int porciones, String tipoPresentacion) {
        super(idProducto, nombre, descripcion, precio, stock, estado);
        this.peso = peso;
        this.porciones = porciones;
        this.tipoPresentacion = tipoPresentacion;
    }

    // ---- Getters y Setters ----
    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public int getPorciones() {
        return porciones;
    }

    public void setPorciones(int porciones) {
        this.porciones = porciones;
    }

    public String getTipoPresentacion() {
        return tipoPresentacion;
    }

    public void setTipoPresentacion(String tipoPresentacion) {
        this.tipoPresentacion = tipoPresentacion;
    }

    // ---- Metodo del diagrama ----

    @Override
    public BigDecimal calcularPrecio() {
        if (porciones <= 0) {
            return getPrecio();
        }
        return getPrecio().multiply(BigDecimal.valueOf(porciones));
    }

    @Override
    public String toString() {
        return "Lechona{" +
                "peso=" + peso +
                ", porciones=" + porciones +
                ", tipoPresentacion='" + tipoPresentacion + '\'' +
                "} " + super.toString();
    }
}