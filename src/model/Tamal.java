package model;

import java.math.BigDecimal;

public class Tamal extends Producto implements IActualizableProducto {

    // Atributos

    private TipoTamal tipo;
    private TamañoTamal tamaño;

    // Constructor

    public Tamal(int idProducto, String nombre, String descripcion, BigDecimal precio,
                 int stock, boolean estado, TipoTamal tipo, TamañoTamal tamaño)
    {
        super(idProducto, nombre, descripcion, precio, stock, estado);
        this.tipo = tipo;
        this.tamaño = tamaño;
    }

    // Getters

    public TipoTamal getTipo() {
        return tipo;
    }

    public TamañoTamal getTamaño() {
        return tamaño;
    }

    // Setters

    public void setTipo(TipoTamal tipo) {
        this.tipo = tipo;
    }

    public void setTamaño(TamañoTamal tamaño) {
        this.tamaño = tamaño;
    }

    @Override
    public void actualizarDatos(Producto producto) {
        if (producto instanceof Tamal) {
            Tamal tamal = (Tamal) producto;
            this.tipo = tamal.getTipo();
            this.tamaño = tamal.getTamaño();
        }
    }

    @Override
    public BigDecimal calcularPrecio() {
        return getPrecio();
    }
}