package model;

import java.time.LocalDate;

public class Empleado extends Usuario implements Iactualizableusuario {

    private int idEmpleado;
    private String cargo;
    private LocalDate fechaContratacion;

    public Empleado(int idUsuario, String nombre, String apellido, String telefono,
                    String correo, String contrasena, boolean estado, LocalDate fechaNacimiento,
                    int idEmpleado, String cargo, LocalDate fechaContratacion) {
        super(idUsuario, nombre, apellido, telefono, correo, contrasena, estado, fechaNacimiento);
        this.idEmpleado = idEmpleado;
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
    }

    // ---- Getters y Setters ----
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    // ---- Metodos del diagrama ----

    public void gestionarPedido(Pedido pedido, EstadoPedido nuevoEstado) {
        if (pedido != null) {
            pedido.cambiarEstado(nuevoEstado);
            System.out.println("Empleado " + getNombre() + " gestiono el pedido #" + pedido.getIdPedido());
        }
    }

    public void gestionarInventario(Inventario inventario) {
        if (inventario != null) {
            inventario.actualizarInventario();
            System.out.println("Empleado " + getNombre() + " actualizo el inventario: " + inventario.getNombre());
        }
    }

    public Compra registrarCompra(int idCompra, Proveedor proveedor) {
        Compra compra = new Compra(idCompra, java.time.LocalDateTime.now(), 0, EstadoCompra.PENDIENTE);
        compra.setProveedor(proveedor);
        compra.setEmpleado(this);
        System.out.println("Compra registrada por: " + getNombre());
        return compra;
    }

    @Override
    public void actualizarDatos(Usuario usuarioConNuevosDatos) {
        if (usuarioConNuevosDatos instanceof Empleado) {
            Empleado empleadoNuevo = (Empleado) usuarioConNuevosDatos;
            this.cargo = empleadoNuevo.getCargo();
        }
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idEmpleado=" + idEmpleado +
                ", cargo='" + cargo + '\'' +
                ", fechaContratacion=" + fechaContratacion +
                "} " + super.toString();
    }
}