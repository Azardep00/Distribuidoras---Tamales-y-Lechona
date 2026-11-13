package com.tamaleslechona.tamaleslechona.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

// Nota: la relación con Pedido (lista de pedidos del cliente) se agrega
// cuando migremos el recurso Pedido; por ahora Cliente solo guarda sus datos.
public class Cliente extends Usuario {

    private TipoCliente tipoCliente;

    private String direccion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    protected Cliente() {
        // requerido por Spring Data
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
