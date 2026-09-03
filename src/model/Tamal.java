package model;

import java.math.BigDecimal;

public class Tamal extends Producto implements IActualizableProducto {

    // Atributos

    private TipoTamal tipo;
    private TamanoTamal tamaño;

    // Constructor

    public Tamal(int idProducto, String nombre, String descripcion, BigDecimal precio,
                 int stock, boolean estado, TipoTamal tipo, TamanoTamal tamaño)
    {
        super(idProducto, nombre, descripcion, precio, stock, estado);
        this.tipo = tipo;
        this.tamaño = tamaño;
    }

    // Getters

    public TipoTamal getTipo() {
        return tipo;
    }

    public TamanoTamal getTamaño() {
        return tamaño;
    }

    // Setters

    public void setTipo(TipoTamal tipo) {
        this.tipo = tipo;
    }

    public void setTamaño(TamanoTamal tamaño) {
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