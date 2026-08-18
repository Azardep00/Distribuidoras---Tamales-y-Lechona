package model;

public class Proveedor {

    private int idProveedor;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private boolean estado;

    public Proveedor(int idProveedor, String nombre, String telefono, String correo,
                     String direccion, boolean estado) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    // ---- Getters y Setters ----
    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // ---- Metodos del diagrama ----

    public boolean registrarProveedor() {
        if (nombre != null && !nombre.isBlank() && telefono != null && !telefono.isBlank()) {
            this.estado = true;
            System.out.println("Proveedor registrado: " + nombre);
            return true;
        }
        System.out.println("Datos invalidos para registrar el proveedor");
        return false;
    }

    public void actualizarDatos(Proveedor proveedorConNuevosDatos) {
        this.nombre = proveedorConNuevosDatos.getNombre();
        this.telefono = proveedorConNuevosDatos.getTelefono();
        this.correo = proveedorConNuevosDatos.getCorreo();
        this.direccion = proveedorConNuevosDatos.getDireccion();
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "idProveedor=" + idProveedor +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", estado=" + estado +
                '}';
    }
}