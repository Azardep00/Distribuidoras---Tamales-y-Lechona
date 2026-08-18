package model;

import java.math.BigDecimal;

public class Tamal extends Producto {

    private TipoTamal tipo;
    private BigDecimal peso;
    private String unidadVenta; // ej: "unidad", "docena", "media docena"

    public Tamal(int idProducto, String nombre, String descripcion, BigDecimal precio,
                 int stock, boolean estado, TipoTamal tipo, BigDecimal peso, String unidadVenta) {
        super(idProducto, nombre, descripcion, precio, stock, estado);
        this.tipo = tipo;
        this.peso = peso;
        this.unidadVenta = unidadVenta;
    }

    // ---- Getters y Setters ----
    public TipoTamal getTipo() {
        return tipo;
    }

    public void setTipo(TipoTamal tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(String unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    // ---- Metodo del diagrama ----

    /**
     * Ejemplo simple: precio base definido en Producto, ajustado segun el peso.
     * Ajusten esta formula a la logica real del negocio.
     */
    @Override
    public BigDecimal calcularPrecio() {
        if (peso == null) {
            return getPrecio();
        }
        return getPrecio().multiply(peso);
    }

    @Override
    public String toString() {
        return "Tamal{" +
                "tipo=" + tipo +
                ", peso=" + peso +
                ", unidadVenta='" + unidadVenta + '\'' +
                "} " + super.toString();
    }
}
