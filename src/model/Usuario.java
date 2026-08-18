package model;

import java.time.LocalDate;

public abstract class Usuario {

    private int idUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String contrasena;
    private boolean estado;
    private LocalDate fechaNacimiento;

    public Usuario(int idUsuario, String nombre, String apellido, String telefono,
                   String correo, String contrasena, boolean estado, LocalDate fechaNacimiento) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.contrasena = contrasena;
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
    }

    // ---- Getters y Setters ----
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // ---- Metodos del diagrama ----

    /**
     * Valida credenciales para permitir el uso del sistema (pedidos, gestion, etc).
     * Aqui se centraliza la seguridad: nadie deberia poder usar Cliente/Empleado
     * sin pasar por este metodo primero.
     */
    public boolean iniciarSesion(String correoIngresado, String contrasenaIngresada) {
        return this.estado
                && this.correo != null
                && this.correo.equalsIgnoreCase(correoIngresado)
                && this.contrasena != null
                && this.contrasena.equals(contrasenaIngresada);
    }

    public void cerrarSesion() {
        System.out.println("Sesion cerrada para el usuario: " + nombre + " " + apellido);
    }

    /**
     * Actualiza los datos comunes de Usuario. Los subtipos (Cliente, Empleado)
     * implementan IActualizableUsuario para actualizar ademas sus datos propios.
     */
    public void actualizarDatos(Usuario usuarioConNuevosDatos) {
        this.nombre = usuarioConNuevosDatos.getNombre();
        this.apellido = usuarioConNuevosDatos.getApellido();
        this.telefono = usuarioConNuevosDatos.getTelefono();
        this.correo = usuarioConNuevosDatos.getCorreo();
        this.fechaNacimiento = usuarioConNuevosDatos.getFechaNacimiento();
    }

    public boolean validarUsuario() {
        return nombre != null && !nombre.isBlank()
                && correo != null && !correo.isBlank()
                && contrasena != null && !contrasena.isBlank()
                && idUsuario != 0;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", estado=" + estado +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}