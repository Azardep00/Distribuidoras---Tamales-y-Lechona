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

/** Gestión de pedidos con acciones separadas y una distribución estable en pantallas pequeñas. */
public class PedidoPanel extends JPanel {
  private final UsuarioController uc;
  private final ProductoController pc;
  private final PedidoController c;
  private final JComboBox<Cliente> cliente = new JComboBox<>();
  private final JComboBox<Producto> producto = new JComboBox<>();
  private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
  private final JComboBox<MetodoPago> metodo = new JComboBox<>(MetodoPago.values());
  private final JTextField direccion = new JTextField();
  private final JLabel total = new JLabel("Total: $0");
  private final JLabel seleccionadoLabel = new JLabel("Ningún pedido seleccionado");

  private final DefaultTableModel carritoM = new DefaultTableModel(
      new Object[] {"Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
    public boolean isCellEditable(int r, int c) { return false; }
  };
  private final DefaultTableModel pedidosM = new DefaultTableModel(
      new Object[] {"ID", "Cliente", "Total", "Estado", "Pago", "Método", "Fecha"}, 0) {
    public boolean isCellEditable(int r, int c) { return false; }
  };

  private final JTable pedidos = new JTable(pedidosM);
  private final JTable carritoTable = new JTable(carritoM);
  private final Map<Integer, DetallePedido> carrito = new LinkedHashMap<>();
  private Integer pedidoSeleccionado;
  private JButton confirmButton, payButton, wompiButton, advanceButton, cancelButton;

  public PedidoPanel(UsuarioController uc, ProductoController pc, PedidoController c) {
    this.uc = uc;
    this.pc = pc;
    this.c = c;
    setLayout(new BorderLayout(12, 12));
    setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));
    setBackground(UI.BG);

    cliente.setRenderer((l, v, i, s, f) -> cell(v, i, s, f));
    producto.setRenderer((l, v, i, s, f) -> cell(v, i, s, f));
    configurarCampos();

    JPanel nuevo = UI.card("Nuevo pedido");
    nuevo.add(buildForm(), BorderLayout.CENTER);
    nuevo.add(buildNewOrderActions(), BorderLayout.SOUTH);

    JPanel carritoCard = UI.card("Productos del pedido");
    UI.styleTable(carritoTable);
    carritoTable.getColumnModel().getColumn(0).setPreferredWidth(300);
    carritoCard.add(new JScrollPane(carritoTable), BorderLayout.CENTER);
    carritoCard.add(buildCartFooter(), BorderLayout.SOUTH);

    JPanel registrados = UI.card("Pedidos registrados");
    registrados.add(buildOrderToolbar(), BorderLayout.NORTH);
    UI.styleTable(pedidos);
    registrados.add(new JScrollPane(pedidos), BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, carritoCard, registrados);
    split.setResizeWeight(.32);
    split.setBorder(null);
    split.setDividerSize(8);

    JPanel center = new JPanel(new BorderLayout(0, 12));
    center.setOpaque(false);
    center.add(nuevo, BorderLayout.NORTH);
    center.add(split, BorderLayout.CENTER);
    add(center, BorderLayout.CENTER);

    wireActions();
    refrescarClientes();
    refrescarProductos();
    refrescarCarrito();
    refrescarTabla();
  }

  private void configurarCampos() {
    UI.styleField(cliente);
    UI.styleField(producto);
    UI.styleField(cantidad);
    UI.styleField(metodo);
    UI.styleField(direccion);
    direccion.setColumns(30);
  }

  private JPanel buildForm() {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(4, 5, 4, 5);
    g.fill = GridBagConstraints.HORIZONTAL;

    // En la misma rejilla, los campos se reducen de forma natural cuando la ventana se achica.
    fullField(form, g, 0, "Cliente *", cliente);
    fullField(form, g, 1, "Producto *", producto);

    g.gridy = 2;
    g.gridx = 0;
    g.weightx = 0;
    g.gridwidth = 1;
    form.add(new JLabel("Cantidad *"), g);
    g.gridx = 1;
    g.weightx = .4;
    form.add(cantidad, g);
    g.gridx = 2;
    g.weightx = 0;
    form.add(new JLabel("Método de pago"), g);
    g.gridx = 3;
    g.weightx = .6;
    form.add(metodo, g);

    fullField(form, g, 3, "Dirección de entrega *", direccion);
    return form;
  }

  private void fullField(JPanel form, GridBagConstraints g, int row, String label, JComponent field) {
    g.gridy = row;
    g.gridx = 0;
    g.gridwidth = 1;
    g.weightx = 0;
    form.add(new JLabel(label), g);
    g.gridx = 1;
    g.gridwidth = 3;
    g.weightx = 1.0;
    form.add(field, g);
    g.gridwidth = 1;
  }

  private JPanel buildNewOrderActions() {
    JPanel area = new JPanel(new BorderLayout(8, 7));
    area.setOpaque(false);

    JPanel row1 = new JPanel(new GridLayout(1, 3, 8, 8));
    row1.setOpaque(false);
    JButton add = UI.primaryButton("Agregar producto");
    JButton remove = UI.button("Quitar seleccionado");
    JButton create = UI.primaryButton("Crear pedido");
    row1.add(add);
    row1.add(remove);
    row1.add(create);

    JPanel row2 = new JPanel(new GridLayout(1, 5, 8, 8));
    row2.setOpaque(false);
    confirmButton = UI.button("Confirmar");
    payButton = UI.button("Registrar pago");
    wompiButton = UI.button("Abrir Wompi");
    advanceButton = UI.button("Siguiente estado");
    cancelButton = UI.dangerButton("Cancelar");
    row2.add(confirmButton);
    row2.add(payButton);
    row2.add(wompiButton);
    row2.add(advanceButton);
    row2.add(cancelButton);

    area.add(row1, BorderLayout.NORTH);
    area.add(row2, BorderLayout.CENTER);

    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    seleccionadoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    seleccionadoLabel.setForeground(UI.MUTED);
    total.setFont(new Font("SansSerif", Font.BOLD, 16));
    total.setForeground(UI.INK);
    footer.add(seleccionadoLabel, BorderLayout.WEST);
    footer.add(total, BorderLayout.EAST);
    area.add(footer, BorderLayout.SOUTH);

    add.putClientProperty("pedido-action", "add");
    remove.putClientProperty("pedido-action", "remove");
    create.putClientProperty("pedido-action", "create");
    confirmButton.putClientProperty("pedido-action", "confirm");
    payButton.putClientProperty("pedido-action", "pay");
    wompiButton.putClientProperty("pedido-action", "wompi");
    advanceButton.putClientProperty("pedido-action", "advance");
    cancelButton.putClientProperty("pedido-action", "cancel");
    return area;
  }

  private JPanel buildCartFooter() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
    p.setOpaque(false);
    JLabel label = new JLabel("Subtotal del pedido: ");
    label.setFont(new Font("SansSerif", Font.BOLD, 13));
    p.add(label);
    p.add(total);
    return p;
  }

  private JPanel buildOrderToolbar() {
    JPanel toolbar = new JPanel(new BorderLayout(8, 6));
    toolbar.setOpaque(false);

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
    left.setOpaque(false);
    JTextField find = new JTextField(10);
    UI.styleField(find);
    JButton search = UI.compactButton("Buscar");
    JButton all = UI.compactButton("Mostrar todos");
    left.add(new JLabel("ID de pedido:"));
    left.add(find);
    left.add(search);
    left.add(all);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    right.setOpaque(false);
    JLabel note = new JLabel("Selecciona un pedido para habilitar sus acciones");
    note.setForeground(UI.MUTED);
    note.setFont(new Font("SansSerif", Font.PLAIN, 12));
    right.add(note);

    toolbar.add(left, BorderLayout.WEST);
    toolbar.add(right, BorderLayout.EAST);
    search.addActionListener(e -> buscar(find.getText()));
    all.addActionListener(e -> { find.setText(""); refrescarTabla(); });
    return toolbar;
  }

  private void wireActions() {
    Component[] components = findActionComponents(this);
    for (Component component : components) {
      if (!(component instanceof JButton b)) continue;
      String action = String.valueOf(b.getClientProperty("pedido-action"));
      switch (action) {
        case "add" -> b.addActionListener(e -> agregar());
        case "remove" -> b.addActionListener(e -> quitar());
        case "create" -> b.addActionListener(e -> crear());
        case "confirm" -> b.addActionListener(e -> accionConfirmar());
        case "pay" -> b.addActionListener(e -> registrarPago());
        case "wompi" -> b.addActionListener(e -> wompi());
        case "advance" -> b.addActionListener(e -> avanzar());
        case "cancel" -> b.addActionListener(e -> cancelar());
      }
    }
    pedidos.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        seleccionarPedido();
        updateActionState();
      }
    });
    cliente.addActionListener(e -> {
      if (cliente.getSelectedItem() instanceof Cliente x && direccion.getText().isBlank())
        direccion.setText(x.getDireccion());
    });
  }

  private Component[] findActionComponents(Container root) {
    java.util.List<Component> all = new ArrayList<>();
    for (Component component : root.getComponents()) {
      if (component instanceof JButton) all.add(component);
      if (component instanceof Container c) all.addAll(Arrays.asList(findActionComponents(c)));
    }
    return all.toArray(new Component[0]);
  }

  private Component cell(Object v, int i, boolean s, boolean f) {
    Component cmp = new DefaultListCellRenderer().getListCellRendererComponent(new JList<>(), v, i, s, f);
    JLabel l = (JLabel) cmp;
    if (v instanceof Cliente c) l.setText("#" + c.getIdUsuario() + " · " + c.getNombre() + " " + c.getApellido());
    else if (v instanceof Producto p) l.setText(p.getNombre() + " · $" + p.getPrecio());
    return l;
  }

  private void agregar() {
    try {
      Producto p = (Producto) producto.getSelectedItem();
      if (p == null) throw new IllegalArgumentException("Selecciona un producto.");
      int q = (Integer) cantidad.getValue();
      if (q <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
      if (q > p.getStock()) throw new IllegalArgumentException("La cantidad supera el stock disponible (" + p.getStock() + ").");
      DetallePedido d = carrito.get(p.getIdProducto());
      int nueva = d == null ? q : d.getCantidad() + q;
      if (nueva > p.getStock()) throw new IllegalArgumentException("La cantidad acumulada supera el stock disponible (" + p.getStock() + ").");
      if (d == null) carrito.put(p.getIdProducto(), new DetallePedido(p, q, p.getPrecio()));
      else d.setCantidad(nueva);
      cantidad.setValue(1);
      refrescarCarrito();
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private void quitar() {
    int r = carritoTable.getSelectedRow();
    if (r < 0) { UI.warn(this, "Selecciona un producto del pedido."); return; }
    int mr = carritoTable.convertRowIndexToModel(r);
    String nombre = String.valueOf(carritoM.getValueAt(mr, 0));
    carrito.entrySet().removeIf(e -> e.getValue().getProducto().getNombre().equals(nombre));
    refrescarCarrito();
  }

  private void crear() {
    try {
      Cliente cl = (Cliente) cliente.getSelectedItem();
      if (cl == null) throw new IllegalArgumentException("Selecciona un cliente.");
      if (carrito.isEmpty()) throw new IllegalArgumentException("Agrega al menos un producto al pedido.");
      String dir = direccion.getText().trim();
      if (dir.isBlank()) throw new IllegalArgumentException("La dirección de entrega es obligatoria.");
      java.util.List<DetallePedido> ds = new ArrayList<>();
      for (DetallePedido d : carrito.values())
        ds.add(new DetallePedido(d.getProducto(), d.getCantidad(), d.getPrecioUnitario()));
      Pedido p = c.crearPedido(cl, (MetodoPago) metodo.getSelectedItem(), dir, ds);
      carrito.clear();
      refrescarCarrito();
      pedidoSeleccionado = p.getIdPedido();
      refrescarTabla();
      seleccionarFilaPorId(pedidoSeleccionado);
      UI.info(this, "Pedido #" + p.getIdPedido() + " creado.\n\nPaso siguiente: confirma el pedido para reservar y descontar stock.");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private Pedido seleccionado() {
    if (pedidoSeleccionado == null) { UI.warn(this, "Selecciona un pedido de la tabla."); return null; }
    Pedido p = c.buscar(pedidoSeleccionado);
    if (p == null) {
      pedidoSeleccionado = null;
      updateActionState();
      UI.warn(this, "El pedido seleccionado ya no está disponible.");
    }
    return p;
  }

  private void accionConfirmar() {
    try {
      Pedido p = seleccionado(); if (p == null) return;
      c.confirmar(p);
      refrescarTabla();
      UI.info(this, "Pedido #" + p.getIdPedido() + " confirmado.\nEl stock ya fue descontado.");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private void registrarPago() {
    try {
      Pedido p = seleccionado(); if (p == null) return;
      c.registrarPago(p);
      refrescarTabla();
      UI.info(this, "Pago registrado para el pedido #" + p.getIdPedido() + ".");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private void wompi() {
    try {
      Pedido p = seleccionado(); if (p == null) return;
      if (p.getMetodoPago() != MetodoPago.WOMPI)
        throw new IllegalStateException("Este pedido no usa Wompi.");
      String url = new AdaptadorPagoWompi().procesarPago(
          p.getTotal().multiply(BigDecimal.valueOf(100)).longValueExact(),
          String.valueOf(p.getIdPedido()));
      Desktop.getDesktop().browse(URI.create(url));
      UI.info(this, "Se abrió el checkout de Wompi. El pago se registra cuando corresponda desde la operación del pedido.");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private void cancelar() {
    try {
      Pedido p = seleccionado(); if (p == null) return;
      if (!UI.confirm(this, "¿Cancelar el pedido #" + p.getIdPedido() + "?")) return;
      c.cancelar(p);
      refrescarTabla();
      UI.info(this, "Pedido #" + p.getIdPedido() + " cancelado.");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private void avanzar() {
    try {
      Pedido p = seleccionado(); if (p == null) return;
      EstadoPedido siguiente = siguiente(p);
      if (siguiente == null) throw new IllegalStateException("Este pedido no tiene un siguiente estado.");
      c.avanzarEstado(p, siguiente);
      refrescarTabla();
      UI.info(this, "Pedido #" + p.getIdPedido() + " actualizado a: " + p.getEstado() + ".");
    } catch (Exception e) { UI.warn(this, e.getMessage()); }
  }

  private EstadoPedido siguiente(Pedido p) {
    if (p.getEstado() == EstadoPedido.CONFIRMADO && !p.isPago())
      throw new IllegalStateException("Registra el pago antes de pasar el pedido a preparación.");
    return switch (p.getEstado()) {
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
    if (r >= 0) pedidoSeleccionado = (Integer) pedidosM.getValueAt(pedidos.convertRowIndexToModel(r), 0);
  }

  private void seleccionarFilaPorId(Integer id) {
    if (id == null) return;
    for (int i = 0; i < pedidosM.getRowCount(); i++) {
      if (Objects.equals(pedidosM.getValueAt(i, 0), id)) {
        int vr = pedidos.convertRowIndexToView(i);
        if (vr >= 0) pedidos.setRowSelectionInterval(vr, vr);
        break;
      }
    }
  }

  private void updateActionState() {
    Pedido p = pedidoSeleccionado == null ? null : c.buscar(pedidoSeleccionado);
    if (p == null) {
      seleccionadoLabel.setText("Ningún pedido seleccionado");
    } else {
      seleccionadoLabel.setText("Pedido seleccionado: #" + p.getIdPedido() + " · " + p.getEstado()
          + (p.isPago() ? " · Pagado" : " · Pendiente de pago"));
    }
    boolean has = p != null;
    confirmButton.setEnabled(has && p.getEstado() == EstadoPedido.PENDIENTE);
    payButton.setEnabled(has && !p.isPago() && p.getEstado() != EstadoPedido.CANCELADO && p.getEstado() != EstadoPedido.ENTREGADO);
    wompiButton.setEnabled(has && p.getMetodoPago() == MetodoPago.WOMPI && !p.isPago() && p.getEstado() != EstadoPedido.CANCELADO && p.getEstado() != EstadoPedido.ENTREGADO);
    advanceButton.setEnabled(has && p.getEstado() != EstadoPedido.ENTREGADO && p.getEstado() != EstadoPedido.CANCELADO);
    cancelButton.setEnabled(has && !p.isPago() && p.getEstado() != EstadoPedido.CANCELADO && p.getEstado() != EstadoPedido.ENTREGADO);
  }

  private void buscar(String id) {
    try {
      int n = Integer.parseInt(id.trim());
      Pedido p = c.buscar(n);
      if (p == null) { UI.warn(this, "No se encontró el pedido #" + n); return; }
      pedidoSeleccionado = p.getIdPedido();
      seleccionarFilaPorId(pedidoSeleccionado);
      updateActionState();
    } catch (Exception e) { UI.warn(this, "Escribe un ID de pedido válido."); }
  }

  public void refrescarClientes() {
    Object s = cliente.getSelectedItem();
    cliente.removeAllItems();
    for (Cliente x : uc.listarClientes()) cliente.addItem(x);
    if (s != null) cliente.setSelectedItem(s);
    if (cliente.getSelectedItem() instanceof Cliente x && direccion.getText().isBlank()) direccion.setText(x.getDireccion());
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
      carritoM.addRow(new Object[] {d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario(), sub});
    }
    total.setText("Total: $" + t);
  }

  public void refrescarTabla() {
    pedidosM.setRowCount(0);
    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    for (Pedido p : c.listar()) {
      pedidosM.addRow(new Object[] {
          p.getIdPedido(),
          p.getCliente().getNombre() + " " + p.getCliente().getApellido(),
          p.getTotal(),
          p.getEstado(),
          p.isPago() ? "Sí" : "No",
          p.getMetodoPago(),
          p.getFecha().format(f)
      });
    }
    seleccionarFilaPorId(pedidoSeleccionado);
    updateActionState();
  }
}
