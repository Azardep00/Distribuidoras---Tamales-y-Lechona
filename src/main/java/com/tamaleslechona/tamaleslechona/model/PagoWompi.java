package com.tamaleslechona.tamaleslechona.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class PagoWompi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPagoWompi;

    @ManyToOne(optional = false)
    private Pedido pedido;

    @Column(nullable = false, unique = true)
    private String referencia;

    @Column(nullable = false)
    private long montoEnCentavos;

    @Column(nullable = false, length = 3)
    private String moneda;

    private String idTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPagoWompi estado;

    protected PagoWompi() {
    }

    public PagoWompi(Pedido pedido, String referencia, long montoEnCentavos, String moneda) {
        this.pedido = pedido;
        this.referencia = referencia;
        this.montoEnCentavos = montoEnCentavos;
        this.moneda = moneda;
        this.estado = EstadoPagoWompi.PENDING;
    }

    public Integer getIdPagoWompi() {
        return idPagoWompi;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getReferencia() {
        return referencia;
    }

    public long getMontoEnCentavos() {
        return montoEnCentavos;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public EstadoPagoWompi getEstado() {
        return estado;
    }

    public void actualizar(String idTransaccion, EstadoPagoWompi estado) {
        this.idTransaccion = idTransaccion;
        this.estado = estado;
    }
}
