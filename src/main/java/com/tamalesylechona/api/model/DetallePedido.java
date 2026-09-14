package main.java.com.tamalesylechona.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// No referencia de vuelta a Pedido (relación unidireccional: el dueño de la
// FK es Pedido.detalles con @JoinColumn). Así evitamos el ciclo
// Pedido -> DetallePedido -> Pedido -> ... al serializar a JSON.
@Entity
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetallePedido;

    @ManyToOne
    private Producto producto;

    private int cantidad;

    // Precio del producto en el momento de crear el pedido. Se guarda aparte
    // del precio actual del producto porque este último puede cambiar después
    // (el pedido histórico no debe verse afectado).
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    protected DetallePedido() {
        // requerido por JPA
    }

    public DetallePedido(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Integer getIdDetallePedido() {
        return idDetallePedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}