package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario implements IActualizableUsuario {
    private int idCliente;
    private TipoCliente tipoCliente;
    private String direccion;
    private LocalDate fechaRegistro;
    private final List<Pedido> pedidos = new ArrayList<>();

    public Cliente(
            int id,
            String n,
            String a,
            String t,
            String c,
            String p,
            boolean e,
            LocalDate fn,
            int idc,
            TipoCliente tc,
            String d,
            LocalDate fr) {
        super(id, n, a, t, c, p, e, fn);
        idCliente = idc;
        tipoCliente = tc;
        direccion = d;
        fechaRegistro = fr;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int v) {
        idCliente = v;
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

    public List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public void agregarPedido(Pedido p) {
        if (p != null && !pedidos.contains(p)) pedidos.add(p);
    }

    public void actualizarDatos(Usuario u) {
        if (u instanceof Cliente c) {
            tipoCliente = c.tipoCliente;
            direccion = c.direccion;
            fechaRegistro = c.fechaRegistro;
        }
    }
}
