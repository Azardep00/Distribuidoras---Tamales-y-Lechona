package com.tamaleslechona.tamaleslechona.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

// Nota: la relación con Pedido (lista de pedidos del cliente) se agrega
// cuando migremos el recurso Pedido; por ahora Cliente solo guarda sus datos.
@Entity
public class Cliente extends Usuario {

    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    private String direccion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    protected Cliente() {
        // requerido por JPA
    }

    public Cliente(
            String nombre,
            String apellido,
            String telefono,
            String correo,
            String contrasena,
            boolean estado,
            LocalDate fechaNacimiento,
            TipoCliente tipoCliente,
            String direccion,
            LocalDate fechaRegistro) {
        super(nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.tipoCliente = tipoCliente;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente v) {
        tipoCliente = v;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String v) {
        direccion = v;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate v) {
        fechaRegistro = v;
    }

    @Override
    public String getTipoUsuario() {
        return "Cliente";
    }
}
