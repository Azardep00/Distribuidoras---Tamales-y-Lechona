package model;

public class Distribuidora {
  private static final Distribuidora INSTANCIA = new Distribuidora();
  private String nombre = "Distribuidora de Tamales y Lechona";
  private String nit = "900000000-1";
  private String direccion = "Ibagué, Tolima";
  private String telefono = "3000000000";

  private Distribuidora() {}

  public static Distribuidora getInstancia() {
    return INSTANCIA;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String v) {
    nombre = v;
  }

  public String getNit() {
    return nit;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String v) {
    direccion = v;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String v) {
    telefono = v;
  }
}
