package com.tamaleslechona.tamaleslechona.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

// Colección aparte: crece sin límite, no se incrusta en el producto.
@Document(collection = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    private String idMovimiento;

    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    @Indexed
    private String idProducto;
    private String nombreProducto;

    // Nulo en salidas y reversiones; obligatorio en entradas.
    @Indexed
    private String idProveedor;
    private String nombreProveedor;

    // Solo vive en memoria mientras se crea el movimiento: los observers
    // (AlertaStock, AuditoriaInventario) lo usan y no cambian. No se guarda.
    @Transient
    private Producto producto;

    protected MovimientoInventario() {}

    public MovimientoInventario(
            TipoMovimiento tipo, int cantidad, String motivo, Producto producto, Proveedor proveedor) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.producto = producto;
        this.idProducto = producto.getIdProducto();
        this.nombreProducto = producto.getNombre();
        if (proveedor != null) {
            this.idProveedor = proveedor.getIdProveedor();
            this.nombreProveedor = proveedor.getNombre();
        }
    }

    public String getIdMovimiento() { return idMovimiento; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public String getIdProducto() { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public String getIdProveedor() { return idProveedor; }
    public String getNombreProveedor() { return nombreProveedor; }

    @JsonIgnore
    public Producto getProducto() { return producto; }
}