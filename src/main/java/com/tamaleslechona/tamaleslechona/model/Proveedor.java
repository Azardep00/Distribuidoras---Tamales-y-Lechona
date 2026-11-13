package com.tamaleslechona.tamaleslechona.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proveedores")
public class Proveedor {

    @Id
    private String idProveedor;

    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private boolean estado;

    protected Proveedor() {
        // requerido por Spring Data
    }

    public Proveedor(String nombre, String telefono, String correo, String direccion, boolean estado) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String v) {
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
}
