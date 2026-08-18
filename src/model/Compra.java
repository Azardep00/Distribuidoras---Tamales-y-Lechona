package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    private int idCompra;
    private LocalDateTime fecha;
    private BigDecimal total;
    private EstadoCompra estado;

    // Relaciones
    private Proveedor proveedor;
    private Empleado empleado;
    private List<Detallecompra> detalles; // 1..* (composicion)

    public Compra(int idCompra, LocalDateTime fecha, double total, EstadoCompra estado) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.total = BigDecimal.valueOf(total);
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    // ---- Getters y Setters ----
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<Detallecompra> getDetalles() {
        return detalles;
    }

    // ---- Metodos del diagrama ----

    public void agregarProducto(Producto producto, int cantidad, BigDecimal precioUnitario) {
        int idDetalle = detalles.size() + 1;
        Detallecompra detalle = new Detallecompra(idDetalle, producto, cantidad, precioUnitario);
        detalles.add(detalle);
        calcularTotal();
    }

    public BigDecimal calcularTotal() {
        BigDecimal nuevoTotal = BigDecimal.ZERO;
        for (Detallecompra detalle : detalles) {
            nuevoTotal = nuevoTotal.add(detalle.calcularSubtotal());
        }
        this.total = nuevoTotal;
        return this.total;
    }

    public void confirmarCompra() {
        this.estado = EstadoCompra.RECIBIDA;
        // Aqui, ademas, se deberia actualizar el stock de cada producto (Inventario)
        for (Detallecompra detalle : detalles) {
            Producto producto = detalle.getProducto();
            if (producto != null) {
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }
        }
        System.out.println("Compra #" + idCompra + " confirmada y stock actualizado");
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", fecha=" + fecha +
                ", total=" + total +
                ", estado=" + estado +
                ", proveedor=" + (proveedor != null ? proveedor.getNombre() : "null") +
                '}';
    }
}