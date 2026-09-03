package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;

public abstract class Usuario {
  private int idUsuario;
  private String nombre, apellido, telefono, correo, contrasena;
  private boolean estado;
  private LocalDate fechaNacimiento;

  protected Usuario(
      int id, String n, String a, String t, String c, String p, boolean e, LocalDate f) {
    idUsuario = id;
    nombre = n;
    apellido = a;
    telefono = t;
    correo = c;
    setContrasena(p == null ? "" : p);
    estado = e;
    fechaNacimiento = f;
  }

  public boolean iniciarSesion(String correo, String contrasena) {
    return estado
        && this.correo.equalsIgnoreCase(correo)
        && this.contrasena.equals(hashPassword(contrasena == null ? "" : contrasena));
  }

  public void cerrarSesion() {
    estado = false;
  }

  public void actualizarDatos(String n, String a, String t, String c) {
    nombre = n;
    apellido = a;
    telefono = t;
    correo = c;
  }

  public int getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(int v) {
    idUsuario = v;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String v) {
    nombre = v;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String v) {
    apellido = v;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String v) {
    telefono = v;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String v) {
    correo = v;
  }

  public void setContrasena(String v) {
    contrasena = hashPassword(v == null ? "" : v);
  }

  public boolean isEstado() {
    return estado;
  }

  public void setEstado(boolean v) {
    estado = v;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate v) {
    fechaNacimiento = v;
  }

  private static String hashPassword(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] h = md.digest(password.getBytes(StandardCharsets.UTF_8));
      StringBuilder s = new StringBuilder();
      for (byte b : h) s.append(String.format("%02x", b));
      return s.toString();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Usuario u && idUsuario == u.idUsuario;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(idUsuario);
  }

  @Override
  public String toString() {
    return nombre + " " + apellido;
  }
}
