package com.tamaleslechona.tamaleslechona.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimiento;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private int cantidad;

    private LocalDateTime fecha;

    private String motivo;

    @ManyToOne
    private Producto producto;

    // Nulo en salidas y reversiones; obligatorio en entradas.
    @ManyToOne
    private Proveedor proveedor;

    protected MovimientoInventario() {
        // requerido por JPA
    }

    public MovimientoInventario(
            TipoMovimiento tipo, int cantidad, String motivo, Producto producto, Proveedor proveedor) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.producto = producto;
        this.proveedor = proveedor;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public Producto getProducto() {
        return producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }
}
