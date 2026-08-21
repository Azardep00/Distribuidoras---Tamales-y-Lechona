package model;

public class DetallePedido
{

    private Producto producto;
    private int cantidad;
    private double precioUnitario;

    public DetallePedido(Producto producto, int cantidad, double precioUnitario)
    {
        if (cantidad <= 0)
        {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Producto getProducto()
    {
        return producto;
    }

    public int getCantidad()
    {
        return cantidad;
    }

    public double getPrecioUnitario()
    {
        return precioUnitario;
    }

    public void setCantidad(int cantidad)
    {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad;
    }

    public double calcularSubtotal()
    {
        return cantidad * precioUnitario;
    }
}
