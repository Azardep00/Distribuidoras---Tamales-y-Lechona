package model;

import java.math.BigDecimal;

public class Lechona extends Producto implements IActualizableProducto{

    // Atributos

    private TamanoLechona tamaño;
    private int numeroPorciones;

    // CONSTRUCTOR

    public Lechona(int idProducto, String nombre, String descripcion,
                   BigDecimal precio, int stock, boolean estado, TamanoLechona tamaño,
                   int numeroPorciones) {
        super(idProducto, nombre, descripcion, precio, stock, estado);
        this.tamaño = tamaño;
        this.numeroPorciones = numeroPorciones;
    }

    public TamanoLechona getTamaño() {
        return tamaño;
    }

    public void setTamaño(TamanoLechona tamaño) {
        this.tamaño = tamaño;
    }

    public int getNumeroPorciones() {
        return numeroPorciones;
    }

    public void setNumeroPorciones(int numeroPorciones) {
        this.numeroPorciones = numeroPorciones;
    }

    @Override
    public void actualizarDatos(Producto producto) {
        if (producto instanceof Lechona) {
            Lechona lechona = (Lechona) producto;
            this.tamaño = lechona.getTamaño();
            this.numeroPorciones = lechona.getNumeroPorciones();
        }
    }

    @Override
    public BigDecimal calcularPrecio() {
        return getPrecio();
    }
}