package model;

import java.time.LocalDate;

public abstract class Usuario {

    // Atributos (privados, con - en el diagrama)
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String contrasena;
    private boolean estado;
    private LocalDate fechaNacimiento;

    // Constructor
    public Usuario(int idUsuario, String nombre, String apellido, String telefono,
                   String correo, String contrasena, boolean estado, LocalDate fechaNacimiento) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        setContrasena(contrasena); // guarda el hash
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Métodos del diagrama
    public boolean iniciarSesion(String correo, String contrasena) {
        // lógica de validación de credenciales (compara hash)
        boolean ok = this.correo.equals(correo) && this.contrasena.equals(hashPassword(contrasena));
        if (ok) this.estado = true; // marcar sesión iniciada
        return ok;
    }

    public void cerrarSesion() {
        this.estado = false;
        // lógica de cierre de sesión
    }

    public void actualizarDatos(String nombre, String apellido, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    // Setter para la contraseña: recibe texto plano y guarda el hash
    public void setContrasena(String contrasena) { this.contrasena = hashPassword(contrasena); }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    // Helper: hash password with SHA-256
    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idUsuario);
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