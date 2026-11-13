package com.tamaleslechona.tamaleslechona.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String idPedido;

    // Referencia al cliente: id + correo (el correo evita una consulta extra
    // al notificar).
    @Indexed
    private String idCliente;
    private String correoCliente;

    private EstadoPedido estado;
    private LocalDateTime fecha;

    // Incrustados: un pedido siempre se lee completo, con sus detalles.
    private List<DetallePedido> detalles = new ArrayList<>();

    private BigDecimal total;

    @Transient
    private EstadoPagoWompi estadoPagoWompi;

    protected Pedido() {}

    public Pedido(Cliente cliente) {
        this.idCliente = cliente.getIdUsuario();
        this.correoCliente = cliente.getCorreo();
        this.estado = EstadoPedido.PENDIENTE;
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        total = total.add(detalle.getSubtotal());
    }

    public String getIdPedido() { return idPedido; }
    public String getIdCliente() { return idCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido v) { estado = v; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public BigDecimal getTotal() { return total; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public EstadoPagoWompi getEstadoPagoWompi() { return estadoPagoWompi; }
    public void setEstadoPagoWompi(EstadoPagoWompi v) { this.estadoPagoWompi = v; }
}