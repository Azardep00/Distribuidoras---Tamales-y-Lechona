package model;

public class Proveedor {
  private int idProveedor;
  private String nombre, telefono, correo, direccion;
  private boolean estado;

  public Proveedor(int id, String n, String t, String c, String d, boolean e) {
    idProveedor = id;
    nombre = n;
    telefono = t;
    correo = c;
    direccion = d;
    estado = e;
  }

  public int getIdProveedor() {
    return idProveedor;
  }

  public void setIdProveedor(int v) {
    idProveedor = v;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String v) {
    nombre = v;
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

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String v) {
    direccion = v;
  }

  public boolean isEstado() {
    return estado;
  }

  public void setEstado(boolean v) {
    estado = v;
  }

  @Override
  public String toString() {
    return nombre;
  }
}
