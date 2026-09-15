package tamaleslechona.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// JsonTypeInfo permite que, al convertir a JSON, se sepa si es un Tamal o una Lechona
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipoProducto")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Tamal.class, name = "Tamal"),
    @JsonSubTypes.Type(value = Lechona.class, name = "Lechona")
})
public abstract class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private boolean estado;

    protected Producto() {
        // requerido por JPA
    }

    protected Producto(
            String nombre, String descripcion, BigDecimal precio, int stock, boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer v) {
        idProducto = v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        nombre = v;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String v) {
        descripcion = v;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal v) {
        precio = v;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int v) {
        stock = v;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean v) {
        estado = v;
    }

    // --- Misma lógica de negocio que ya tenías en el proyecto original ---

    public boolean consultarDisponibilidad(int cantidad) {
        return estado && cantidad > 0 && stock >= cantidad;
    }

    public void descontarStock(int cantidad) {
        if (!consultarDisponibilidad(cantidad))
            throw new IllegalArgumentException("Stock insuficiente para " + nombre + ".");
        stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        stock += cantidad;
    }

    public abstract String getTipoProducto();

    public abstract String getDetalleEspecifico();
}
