package com.tamaleslechona.tamaleslechona.model;

import java.math.BigDecimal;

public class Lechona extends Producto {

    private TamanoLechona tamano;

    private int numeroPorciones;

    protected Lechona() {
        // requerido por Spring Data
    }

    public Lechona(
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stock,
            boolean estado,
            TamanoLechona tamano,
            int numeroPorciones) {
        super(nombre, descripcion, precio, stock, estado);
        this.tamano = tamano;
        this.numeroPorciones = numeroPorciones;
    }

    public TamanoLechona getTamano() {
        return tamano;
    }

    public void setTamano(TamanoLechona v) {
        tamano = v;
    }

    public int getNumeroPorciones() {
        return numeroPorciones;
    }

    public void setNumeroPorciones(int v) {
        numeroPorciones = v;
    }

    @Override
    public String getTipoProducto() {
        return "Lechona";
    }

    @Override
    public String getDetalleEspecifico() {
        return tamano + " · " + numeroPorciones + " porciones";
    }
}
