package model;

import java.time.LocalDate;
import java.util.Date;

public class Empleado extends Usuario implements IActualizableUsuario {
  private String cargo;
  private Date fechaContratacion;

  public Empleado(
          int id,
          String n,
          String a,
          String t,
          String c,
          String p,
          boolean e,
          LocalDate fn,
          String cargo,
          Date fc) {
    super(id, n, a, t, c, p, e, fn);
    this.cargo = cargo;
    fechaContratacion = fc;
  }

  public String getCargo() {
    return cargo;
  }

  public void setCargo(String v) {
    cargo = v;
  }

  public Date getFechaContratacion() {
    return fechaContratacion;
  }

  public void setFechaContratacion(Date v) {
    fechaContratacion = v;
  }

  public void actualizarDatos(Usuario u) {
    if (u instanceof Empleado e) {
      cargo = e.cargo;
      fechaContratacion = e.fechaContratacion;
    }
  }
}
