package view;

import controller.*;
import java.awt.*;
import javax.swing.*;
import model.*;

public class MainPanel extends JPanel {
  public MainPanel(
      ResumenPanel dashboard,
      ProveedorPanel proveedores,
      MovimientoInventarioPanel inventario,
      ProductoPanel productos,
      UsuarioPanel usuarios,
      PedidoPanel pedidos) {
    setLayout(new BorderLayout());
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Inicio", dashboard);
    tabs.addTab("Pedidos", pedidos);
    tabs.addTab("Inventario", inventario);
    tabs.addTab("Productos", productos);
    tabs.addTab("Proveedores", proveedores);
    tabs.addTab("Usuarios", usuarios);
    tabs.addChangeListener(
        e -> {
          dashboard.refrescar();
          if (tabs.getSelectedComponent() == inventario) {
            inventario.refrescarCombos();
            inventario.refrescarTablas();
          }
          if (tabs.getSelectedComponent() == productos) productos.refrescarTabla();
          if (tabs.getSelectedComponent() == proveedores) proveedores.refrescarTabla();
          if (tabs.getSelectedComponent() == usuarios) usuarios.refrescarTabla();
          if (tabs.getSelectedComponent() == pedidos) {
            pedidos.refrescarClientes();
            pedidos.refrescarProductos();
            pedidos.refrescarTabla();
          }
        });
    add(tabs, BorderLayout.CENTER);
  }
}
