package model;

import java.time.LocalDateTime;

public class Entrega {

    private int idEntrega;
    private String direccion;
    private LocalDateTime fechaEntrega;
    private EstadoEntrega estado;
    private String observaciones;

    private Empleado empleadoAsignado;

    public Entrega(int idEntrega, String direccion, LocalDateTime fechaEntrega,
                   EstadoEntrega estado, String observaciones) {
        this.idEntrega = idEntrega;
        this.direccion = direccion;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // ---- Getters y Setters ----
    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrega estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Empleado getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    // ---- Metodos del diagrama ----

    public void asignarEmpleado(Empleado empleado) {
        this.empleadoAsignado = empleado;
        this.estado = EstadoEntrega.ASIGNADA;
        System.out.println("Entrega #" + idEntrega + " asignada a " + empleado.getNombre());
    }

    public void actualizarEstado(EstadoEntrega nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void confirmarEntrega() {
        this.estado = EstadoEntrega.ENTREGADA;
        this.fechaEntrega = LocalDateTime.now();
        System.out.println("Entrega #" + idEntrega + " confirmada");
    }

    @Override
    public String toString() {
        return "Entrega{" +
                "idEntrega=" + idEntrega +
                ", direccion='" + direccion + '\'' +
                ", fechaEntrega=" + fechaEntrega +
                ", estado=" + estado +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}