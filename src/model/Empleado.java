package model;

import java.util.Date;

public class Empleado extends Usuario implements IActualizableUsuario {

    private String cargo;
    private Date fechaContratacion;

    public Empleado(int idUsuario, String nombre, String apellido, String telefono,
                    String correo, String contrasena, boolean estado, java.time.LocalDate fechaNacimiento,
                    String cargo, Date fechaContratacion) {
        super(idUsuario, nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Date getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    // Actualiza solo lo propio de Empleado (los datos comunes los actualiza Usuario.actualizarDatos())
    @Override
    public void actualizarDatos(Usuario usuarioConNuevosDatos) {
        if (usuarioConNuevosDatos instanceof Empleado) {
            Empleado empleadoNuevo = (Empleado) usuarioConNuevosDatos;
            this.cargo = empleadoNuevo.getCargo();
            this.fechaContratacion = empleadoNuevo.getFechaContratacion();
        }
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "cargo='" + cargo + '\'' +
                ", fechaContratacion=" + fechaContratacion +
                "} " + super.toString();
    }
}