package main.java.com.tamalesylechona.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @ManyToOne
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private LocalDateTime fecha;

    // Relación unidireccional: Pedido es el dueño de la FK (id_pedido en la
    // tabla detalle_pedido). cascade=ALL + orphanRemoval hace que guardar o
    // borrar un Pedido guarde/borre sus detalles automáticamente.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_pedido")
    private List<DetallePedido> detalles = new ArrayList<>();

    private BigDecimal total;

    protected Pedido() {
        // requerido por JPA
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.estado = EstadoPedido.PENDIENTE;
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    // El total se recalcula solo a medida que se agregan detalles; así nunca
    // queda desincronizado con la suma real de los subtotales.
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        total = total.add(detalle.getSubtotal());
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido v) {
        estado = v;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public BigDecimal getTotal() {
        return total;
    }
}