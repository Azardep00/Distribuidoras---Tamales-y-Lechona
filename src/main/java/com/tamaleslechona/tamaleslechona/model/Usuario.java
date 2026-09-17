package com.tamaleslechona.tamaleslechona.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// Misma idea que Producto: clase base abstracta + herencia en una sola tabla,
// con Cliente y Empleado como los dos tipos concretos.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipoUsuario")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Cliente.class, name = "Cliente"),
    @JsonSubTypes.Type(value = Empleado.class, name = "Empleado")
})
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;
    private String apellido;
    private String telefono;

    @Column(unique = true)
    private String correo;

    // WRITE_ONLY: se puede enviar en el JSON de entrada (POST/PUT), pero
    // Jackson nunca la incluye en las respuestas. El valor que se guarda
    // siempre es el hash, nunca el texto plano.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    private boolean estado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    protected Usuario() {
        // requerido por JPA
    }

    protected Usuario(
            String nombre,
            String apellido,
            String telefono,
            String correo,
            String contrasena,
            boolean estado,
            LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        setContrasena(contrasena);
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer v) {
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

    // Valida y hashea de una vez; así una contraseña inválida nunca llega
    // a guardarse, ni siquiera en memoria dentro del objeto.
    public void setContrasena(String plano) {
        String p = plano == null ? "" : plano;
        if (p.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.contrasena = hashPassword(p);
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

    public boolean verificarContrasena(String plano) {
        return contrasena != null && contrasena.equals(hashPassword(plano == null ? "" : plano));
    }

    public void cambiarContrasena(String actual, String nueva) {
        if (!verificarContrasena(actual)) {
            throw new IllegalArgumentException("La contraseña actual no coincide.");
        }
        setContrasena(nueva);
    }

    public abstract String getTipoUsuario();

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
        return o instanceof Usuario u && idUsuario != null && idUsuario.equals(u.idUsuario);
    }

    @Override
    public int hashCode() {
        return idUsuario == null ? 0 : idUsuario.hashCode();
    }
}
