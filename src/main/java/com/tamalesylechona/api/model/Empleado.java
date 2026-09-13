package main.java.com.tamalesylechona.api.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;

@Entity
public class Empleado extends Usuario {

    private String cargo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaContratacion;

    protected Empleado() {
        // requerido por JPA
    }

    public Empleado(
            String nombre,
            String apellido,
            String telefono,
            String correo,
            String contrasena,
            boolean estado,
            LocalDate fechaNacimiento,
            String cargo,
            LocalDate fechaContratacion) {
        super(nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String v) {
        cargo = v;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate v) {
        fechaContratacion = v;
    }

    @Override
    public String getTipoUsuario() {
        return "Empleado";
    }
}
