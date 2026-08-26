package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class Cliente extends Usuario implements IActualizableUsuario {

    private int idCliente;
    private TipoCliente tipoCliente;
    private String direccion;
    private LocalDate fechaRegistro;

    // Un cliente puede tener muchos pedidos (0..*)
    private List<Pedido> pedidos;

    public Cliente(int idUsuario, String nombre, String apellido, String telefono,
                   String correo, String contrasena, boolean estado, LocalDate fechaNacimiento,
                   int idCliente, TipoCliente tipoCliente, String direccion, LocalDate fechaRegistro) {
        super(idUsuario, nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.idCliente = idCliente;
        this.tipoCliente = tipoCliente;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.pedidos = new ArrayList<>();
    }

    // ---- Getters y Setters ----
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    // ---- Metodos del diagrama ----

    public Pedido realizarPedido(int idPedido) {
        Pedido nuevoPedido = new Pedido(idPedido, MetodoPago.EFECTIVO, this.direccion);
        pedidos.add(nuevoPedido);
        System.out.println("Pedido creado para el cliente: " + getNombre());
        return nuevoPedido;
    }

    public List<Pedido> consultarPedidos() {
        return pedidos;
    }

    /**
     * Implementacion de IActualizableUsuario: actualiza los datos propios de Cliente
     * (los datos comunes de Usuario ya los actualiza Usuario.actualizarDatos()).
     */
    @Override
    public void actualizarDatos(Usuario usuarioConNuevosDatos) {
        if (usuarioConNuevosDatos instanceof Cliente) {
            Cliente clienteNuevo = (Cliente) usuarioConNuevosDatos;
            this.tipoCliente = clienteNuevo.getTipoCliente();
            this.direccion = clienteNuevo.getDireccion();
        }
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", tipoCliente=" + tipoCliente +
                ", direccion='" + direccion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                "} " + super.toString();
    }
}