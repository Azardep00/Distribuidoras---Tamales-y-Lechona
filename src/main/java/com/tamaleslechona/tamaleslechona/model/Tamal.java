package com.tamaleslechona.tamaleslechona.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;

@Entity
public class Tamal extends Producto {

    @Enumerated(EnumType.STRING)
    private TipoTamal tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano_tamal") 
    private TamanoTamal tamano;

    protected Tamal() {
        // requerido por JPA
    }

    public Tamal(
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stock,
            boolean estado,
            TipoTamal tipo,
            TamanoTamal tamano) {
        super(nombre, descripcion, precio, stock, estado);
        this.tipo = tipo;
        this.tamano = tamano;
    }

    public TipoTamal getTipo() {
        return tipo;
    }

    public void setTipo(TipoTamal v) {
        tipo = v;
    }

    public TamanoTamal getTamano() {
        return tamano;
    }

    public void setTamano(TamanoTamal v) {
        tamano = v;
    }

    @Override
    public String getTipoProducto() {
        return "Tamal";
    }

    @Override
    public String getDetalleEspecifico() {
        return tipo + " · " + tamano;
    }
}
