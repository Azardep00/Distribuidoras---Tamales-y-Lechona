package com.tamaleslechona.tamaleslechona.model;

import java.math.BigDecimal;

// Se guarda DENTRO del documento Pedido, con una copia del nombre y del
// precio: si luego editas el producto, el pedido histórico no cambia.
public class DetallePedido {

    private String idProducto;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    protected DetallePedido() {}

    public DetallePedido(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = producto.getIdProducto();
        this.nombreProducto = producto.getNombre();
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getIdProducto() { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
}