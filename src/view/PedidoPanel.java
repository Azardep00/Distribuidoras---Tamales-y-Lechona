package view;

import controller.*;
import java.awt.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import pagos.*;

public class PedidoPanel extends JPanel {
  private final UsuarioController uc;
  private final ProductoController pc;
  private final PedidoController c;
  private final JComboBox<Cliente> cliente = new JComboBox<>();
  private final JComboBox<Producto> producto = new JComboBox<>();
  private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
  private final JComboBox<MetodoPago> metodo = new JComboBox<>(MetodoPago.values());
  private final JTextField direccion = new JTextField(25);
  private final JLabel total = new JLabel("Total: $0");
  private final DefaultTableModel carritoM =
      new DefaultTableModel(new Object[] {"Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
        public boolean isCellEditable(int r, int c) {
          return false;
        }
      };
  private final DefaultTableModel pedidosM =
      new DefaultTableModel(
          new Object[] {"ID", "Cliente", "Total", "Estado", "Pago", "Método", "Fecha"}, 0) {
        public boolean isCellEditable(int r, int c) {
          return false;
        }
      };
  private final JTable pedidos = new JTable(pedidosM);
  private final Map<Integer, DetallePedido> carrito = new LinkedHashMap<>();
  private Integer pedidoSeleccionado;
  private final JTable carritoTable = new JTable(carritoM);

  public PedidoPanel(UsuarioController uc, ProductoController pc, PedidoController c) {
    this.uc = uc;
    this.pc = pc;
    this.c = c;
    setLayout(new BorderLayout(12, 12));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    cliente.setRenderer((l, v, i, s, f) -> cell(v, i, s, f));
    producto.setRenderer((l, v, i, s, f) -> cell(v, i, s, f));
    JPanel header = UI.card("1 · Crear pedido");
    JPanel f = new JPanel(new GridBagLayout());
    f.setOpaque(false);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(4, 4, 4, 4);
    g.fill = GridBagConstraints.HORIZONTAL;
    row(f, g, 0, "Cliente *", cliente);
    row(f, g, 1, "Producto *", producto);
    row(f, g, 2, "Cantidad *", cantidad);
    row(f, g, 3, "Dirección entrega *", direccion);
    row(f, g, 4, "Método de pago", metodo);
    header.add(f, BorderLayout.CENTER);
    JPanel act = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton add = UI.button("Agregar producto"),
        remove = UI.button("Quitar seleccionado"),
        create = UI.button("Crear pedido"),
        confirm = UI.button("Confirmar y descontar stock"),
        pay = UI.button("Registrar pago"),
        wompi = UI.button("Generar enlace Wompi"),
        cancel = UI.button("Cancelar pedido");
    act.add(add);
    act.add(remove);
    act.add(create);
    act.add(confirm);
    act.add(pay);
    act.add(wompi);
    act.add(cancel);
    act.add(total);
    header.add(act, BorderLayout.SOUTH);
    add(header, BorderLayout.NORTH);
    JPanel cart = UI.card("2 · Resumen del pedido");
    carritoTable.setAutoCreateRowSorter(true);
    cart.add(new JScrollPane(carritoTable), BorderLayout.CENTER);
    JPanel list = UI.card("3 · Pedidos registrados");
    JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
    search.setOpaque(false);
    JTextField find = new JTextField(12);
    JButton b = UI.button("Buscar ID"),
        all = UI.button("Mostrar todos"),
        advance = UI.button("Avanzar estado");
    search.add(new JLabel("Pedido:"));
    search.add(find);
    search.add(b);
    search.add(all);
    search.add(advance);
    list.add(search, BorderLayout.NORTH);
    pedidos.setAutoCreateRowSorter(true);
    list.add(new JScrollPane(pedidos), BorderLayout.CENTER);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cart, list);
    split.setResizeWeight(.36);
    add(split, BorderLayout.CENTER);
    add.addActionListener(e -> agregar());
    remove.addActionListener(e -> quitar());
    create.addActionListener(e -> crear());
    confirm.addActionListener(e -> accionConfirmar());
    pay.addActionListener(e -> registrarPago());
    wompi.addActionListener(e -> wompi());
    cancel.addActionListener(e -> cancelar());
    advance.addActionListener(e -> avanzar());
    b.addActionListener(e -> buscar(find.getText()));
    all.addActionListener(
        e -> {
          find.setText("");
          refrescarTabla();
        });
    pedidos.getSelectionModel().addListSelectionListener(e -> seleccionarPedido());
    refrescarClientes();
    refrescarProductos();
    refrescarTabla();
  }

  private Component cell(Object v, int i, boolean s, boolean f) {
    Component cmp =
        new DefaultListCellRenderer().getListCellRendererComponent(new JList<>(), v, i, s, f);
    JLabel l = (JLabel) cmp;
    if (v instanceof Cliente c)
      l.setText("#" + c.getIdUsuario() + " · " + c.getNombre() + " " + c.getApellido());
    else if (v instanceof Producto p) l.setText(p.getNombre() + " · $" + p.getPrecio());
    return l;
  }

  private void row(JPanel p, GridBagConstraints g, int y, String l, JComponent x) {
    g.gridy = y;
    g.gridx = 0;
    g.weightx = 0;
    p.add(new JLabel(l), g);
    g.gridx = 1;
    g.weightx = 1;
    g.gridwidth = 2;
    p.add(x, g);
    g.gridwidth = 1;
  }

  private void agregar() {
    try {
      Producto p = (Producto) producto.getSelectedItem();
      if (p == null) throw new IllegalArgumentException("Selecciona un producto.");
      int q = (Integer) cantidad.getValue();
      DetallePedido d = carrito.get(p.getIdProducto());
      if (d == null) carrito.put(p.getIdProducto(), new DetallePedido(p, q, p.getPrecio()));
      else d.setCantidad(d.getCantidad() + q);
      refrescarCarrito();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void quitar() {
    int r = carritoTable.getSelectedRow();
    if (r < 0) {
      UI.warn(this, "Selecciona un producto del resumen.");
      return;
    }
    int mr = carritoTable.convertRowIndexToModel(r);
    String nombre = String.valueOf(carritoM.getValueAt(mr, 0));
    carrito.entrySet().removeIf(e -> e.getValue().getProducto().getNombre().equals(nombre));
    refrescarCarrito();
  }

  private void crear() {
    try {
      Cliente cl = (Cliente) cliente.getSelectedItem();
      java.util.List<DetallePedido> ds = new ArrayList<>(carrito.values());
      Pedido p =
          c.crearPedido(cl, (MetodoPago) metodo.getSelectedItem(), direccion.getText().trim(), ds);
      UI.info(
          this,
          "Pedido #"
              + p.getIdPedido()
              + " creado en estado Pendiente. Aún no descuenta inventario.");
      carrito.clear();
      refrescarCarrito();
      pedidoSeleccionado = p.getIdPedido();
      refrescarTabla();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private Pedido seleccionado() {
    if (pedidoSeleccionado == null) {
      UI.warn(this, "Selecciona un pedido de la tabla.");
      return null;
    }
    return c.buscar(pedidoSeleccionado);
  }

  private void accionConfirmar() {
    try {
      Pedido p = seleccionado();
      if (p == null) return;
      c.confirmar(p);
      UI.info(this, "Pedido #" + p.getIdPedido() + " confirmado y stock descontado.");
      refrescarTabla();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void registrarPago() {
    try {
      Pedido p = seleccionado();
      if (p == null) return;
      c.registrarPago(p);
      UI.info(this, "Pago registrado para el pedido #" + p.getIdPedido() + ".");
      refrescarTabla();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void wompi() {
    try {
      Pedido p = seleccionado();
      if (p == null) return;
      if (p.getMetodoPago() != MetodoPago.WOMPI)
        throw new IllegalStateException("El pedido debe tener Wompi como método de pago.");
      String url =
          new AdaptadorPagoWompi()
              .procesarPago(
                  p.getTotal().multiply(BigDecimal.valueOf(100)).longValueExact(),
                  String.valueOf(p.getIdPedido()));
      Desktop.getDesktop().browse(URI.create(url));
      UI.info(
          this,
          "Checkout Wompi generado y abierto en el navegador. El pedido no se marca como pagado hasta confirmar el pago.");
      refrescarTabla();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void cancelar() {
    try {
      Pedido p = seleccionado();
      if (p == null) return;
      if (UI.confirm(this, "¿Cancelar el pedido #" + p.getIdPedido() + "?")) {
        c.cancelar(p);
        UI.info(this, "Pedido cancelado. Las existencias reservadas fueron revertidas.");
        refrescarTabla();
      }
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void avanzar() {
    try {
      Pedido p = seleccionado();
      if (p == null) return;
      if (p.getEstado() == EstadoPedido.CONFIRMADO && !p.isPago())
        throw new IllegalStateException(
            "Registra el pago antes de enviar el pedido a preparación.");
      EstadoPedido siguiente = siguiente(p.getEstado());
      if (siguiente == null)
        throw new IllegalStateException(
            "El pedido no tiene un siguiente estado disponible. Usa 'Registrar pago' cuando corresponda.");
      if (siguiente == EstadoPedido.PAGADO) c.registrarPago(p);
      else c.avanzarEstado(p, siguiente);
      UI.info(this, "Pedido #" + p.getIdPedido() + " → " + p.getEstado());
      refrescarTabla();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private EstadoPedido siguiente(EstadoPedido e) {
    return switch (e) {
      case PENDIENTE -> EstadoPedido.CONFIRMADO;
      case CONFIRMADO -> EstadoPedido.EN_PREPARACION;
      case PAGADO -> EstadoPedido.EN_PREPARACION;
      case EN_PREPARACION -> EstadoPedido.LISTO;
      case LISTO -> EstadoPedido.ENTREGADO;
      default -> null;
    };
  }

  private void seleccionarPedido() {
    int r = pedidos.getSelectedRow();
    if (r >= 0)
      pedidoSeleccionado = (Integer) pedidosM.getValueAt(pedidos.convertRowIndexToModel(r), 0);
  }

  private void buscar(String id) {
    try {
      int n = Integer.parseInt(id.trim());
      Pedido p = c.buscar(n);
      if (p == null) {
        UI.warn(this, "No se encontró el pedido #" + n);
        return;
      }
      pedidoSeleccionado = p.getIdPedido();
      for (int i = 0; i < pedidosM.getRowCount(); i++)
        if ((Integer) pedidosM.getValueAt(i, 0) == n) {
          int vr = pedidos.convertRowIndexToView(i);
          pedidos.setRowSelectionInterval(vr, vr);
          break;
        }
    } catch (Exception e) {
      UI.warn(this, "Escribe un ID de pedido válido.");
    }
  }

  public void refrescarClientes() {
    Object s = cliente.getSelectedItem();
    cliente.removeAllItems();
    for (Cliente x : uc.listarClientes()) cliente.addItem(x);
    if (s != null) cliente.setSelectedItem(s);
    if (cliente.getSelectedItem() instanceof Cliente x && direccion.getText().isBlank())
      direccion.setText(x.getDireccion());
  }

  public void refrescarProductos() {
    Object s = producto.getSelectedItem();
    producto.removeAllItems();
    for (Producto p : pc.listar(false)) producto.addItem(p);
    if (s != null) producto.setSelectedItem(s);
  }

  private void refrescarCarrito() {
    carritoM.setRowCount(0);
    BigDecimal t = BigDecimal.ZERO;
    for (DetallePedido d : carrito.values()) {
      BigDecimal sub = d.calcularSubtotal();
      t = t.add(sub);
      carritoM.addRow(
          new Object[] {d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario(), sub});
    }
    total.setText("Total: $" + t);
  }

  public void refrescarTabla() {
    pedidosM.setRowCount(0);
    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    for (Pedido p : c.listar())
      pedidosM.addRow(
          new Object[] {
            p.getIdPedido(),
            p.getCliente().getNombre() + " " + p.getCliente().getApellido(),
            p.getTotal(),
            p.getEstado(),
            p.isPago() ? "Sí" : "No",
            p.getMetodoPago(),
            p.getFecha().format(f)
          });
  }
}
