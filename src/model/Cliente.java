package model;

import java.util.Date;


public class Cliente extends Usuario {

    // Atributos
    private TipoCliente tipoCliente;
    private String direccion;
    private Date fechaRegistro;

    // Constructor
    public Cliente(int idUsuario, String nombre, String apellido, String telefono,
                   String correo, String contrasena, boolean estado,
                   java.time.LocalDate fechaNacimiento,
                   TipoCliente tipoCliente, String direccion, Date fechaRegistro) {

        super(idUsuario, nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);

        this.tipoCliente = tipoCliente;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
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

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // Métodos del UML
    public void realizarPedido() {
        System.out.println("Pedido realizado correctamente.");
    }

    public void consultarPedidos() {
        System.out.println("Mostrando historial de pedidos...");
    }
}