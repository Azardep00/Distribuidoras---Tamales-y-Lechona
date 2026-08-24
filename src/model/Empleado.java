package model;

import java.util.Date;

public class Empleado extends Usuario {

    // Atributos propios de Empleado
    private String cargo;
    private Date fechaContratacion;

    // Constructor
    public Empleado(int idUsuario, String nombre, String apellido, String telefono,
                    String correo, String contrasena, boolean estado, java.time.LocalDate fechaNacimiento,
                    String cargo, Date fechaContratacion) {
        // Llama al constructor de la clase padre (Usuario)
        super(idUsuario, nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
    }

    // Métodos propios de Empleado
    public void gestionarPedido() {
        // lógica para gestionar pedidos
    }

    public void gestionarInventario() {
        // lógica para gestionar inventario
    }

    // Getters y Setters
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Date getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }
}