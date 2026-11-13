package com.tamaleslechona.tamaleslechona.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pagos_wompi")
public class PagoWompi {

    @Id
    private String idPagoWompi;

    @Indexed
    private String idPedido;

    @Indexed(unique = true)
    private String referencia;

    private long montoEnCentavos;
    private String moneda;
    private String idTransaccion;
    private EstadoPagoWompi estado;

    protected PagoWompi() {}

    public PagoWompi(Pedido pedido, String referencia, long montoEnCentavos, String moneda) {
        this.idPedido = pedido.getIdPedido();
        this.referencia = referencia;
        this.montoEnCentavos = montoEnCentavos;
        this.moneda = moneda;
        this.estado = EstadoPagoWompi.PENDING;
    }

    public String getIdPagoWompi() { return idPagoWompi; }
    public String getIdPedido() { return idPedido; }
    public String getReferencia() { return referencia; }
    public long getMontoEnCentavos() { return montoEnCentavos; }
    public String getMoneda() { return moneda; }
    public String getIdTransaccion() { return idTransaccion; }
    public EstadoPagoWompi getEstado() { return estado; }

    public void actualizar(String idTransaccion, EstadoPagoWompi estado) {
        this.idTransaccion = idTransaccion;
        this.estado = estado;
    }
}