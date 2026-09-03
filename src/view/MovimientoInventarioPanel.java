package view;

import controller.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class MovimientoInventarioPanel extends JPanel {
  private final MovimientoInventarioController c;
  private final ProveedorController pc;
  private final ProductoController prod;
  private final JComboBox<Producto> producto = new JComboBox<>();
  private final JComboBox<Proveedor> proveedor = new JComboBox<>();
  private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
  private final JTextField motivo = new JTextField(22);
  private final JLabel modo = new JLabel("Entrada seleccionada: el proveedor es obligatorio.");
  private final DefaultTableModel stock =
      new DefaultTableModel(new Object[] {"ID", "Producto", "Tipo", "Stock", "Estado"}, 0) {
        public boolean isCellEditable(int r, int col) {
          return false;
        }
      };
  private final DefaultTableModel mov =
      new DefaultTableModel(
          new Object[] {"ID", "Tipo", "Producto", "Cantidad", "Fecha", "Motivo", "Proveedor"}, 0) {
        public boolean isCellEditable(int r, int col) {
          return false;
        }
      };

  public MovimientoInventarioPanel(
      MovimientoInventarioController c, ProveedorController pc, ProductoController prod) {
    this.c = c;
    this.pc = pc;
    this.prod = prod;
    setLayout(new BorderLayout(12, 12));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    producto.setRenderer((l, v, i, s, f) -> cell("", v, i, s, f));
    proveedor.setRenderer((l, v, i, s, f) -> cell("", v, i, s, f));
    JPanel form = UI.card("Operación de inventario");
    JPanel f = new JPanel(new GridBagLayout());
    f.setOpaque(false);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(4, 4, 4, 4);
    g.fill = GridBagConstraints.HORIZONTAL;
    row(f, g, 0, "Producto *", producto);
    row(f, g, 1, "Cantidad *", cantidad);
    row(f, g, 2, "Motivo", motivo);
    row(f, g, 3, "Proveedor (entrada)", proveedor);
    form.add(f, BorderLayout.CENTER);
    modo.setForeground(UI.MUTED);
    JPanel a = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton en = UI.button("Registrar entrada"), sa = UI.button("Registrar salida");
    a.add(en);
    a.add(sa);
    a.add(modo);
    form.add(a, BorderLayout.SOUTH);
    add(form, BorderLayout.NORTH);
    JTabbedPane tabs = new JTabbedPane();
    JTable ts = new JTable(stock), tm = new JTable(mov);
    UI.styleTable(ts);
    UI.styleTable(tm);
    ts.setAutoCreateRowSorter(true);
    tm.setAutoCreateRowSorter(true);
    tabs.addTab("Stock actual", new JScrollPane(ts));
    tabs.addTab("Historial", new JScrollPane(tm));
    add(tabs, BorderLayout.CENTER);
    en.addActionListener(e -> entrada());
    sa.addActionListener(e -> salida());
    producto.addActionListener(
        e -> modo.setText("Entrada seleccionada: el proveedor es obligatorio."));
        UI.styleField(producto);
        UI.styleField(proveedor);
        UI.styleField(cantidad);
        UI.styleField(motivo);
    refrescarCombos();
    refrescarTablas();
  }

  private Component cell(String x, Object v, int i, boolean s, boolean f) {
    Component cmp =
        new DefaultListCellRenderer().getListCellRendererComponent(new JList<>(), v, i, s, f);
    JLabel l = (JLabel) cmp;
    if (v == null) l.setText("—");
    else if (v instanceof Producto p) l.setText(p.getNombre() + " · stock " + p.getStock());
    else if (v instanceof Proveedor p) l.setText(p.getNombre());
    return l;
  }

  private void row(JPanel p, GridBagConstraints g, int y, String l, JComponent x) {
    g.gridy = y;
    g.gridx = 0;
    g.weightx = 0;
    p.add(new JLabel(l), g);
    g.gridx = 1;
    g.weightx = 1;
    p.add(x, g);
  }

  private void entrada() {
    try {
      Producto p = (Producto) producto.getSelectedItem();
      Proveedor pr = (Proveedor) proveedor.getSelectedItem();
      c.registrarEntrada(p, (Integer) cantidad.getValue(), pr, motivo.getText().trim());
      UI.info(this, "Entrada registrada. Stock actual: " + p.getStock());
      reset();
      refrescarTablas();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void salida() {
    try {
      Producto p = (Producto) producto.getSelectedItem();
      c.registrarSalida(p, (Integer) cantidad.getValue(), motivo.getText().trim());
      UI.info(this, "Salida registrada. Stock actual: " + p.getStock());
      reset();
      refrescarTablas();
    } catch (Exception e) {
      UI.warn(this, e.getMessage());
    }
  }

  private void reset() {
    cantidad.setValue(1);
    motivo.setText("");
    refrescarCombos();
  }

  public void refrescarCombos() {
    Object sel = producto.getSelectedItem();
    producto.removeAllItems();
    for (Producto p : prod.listar(false)) producto.addItem(p);
    if (sel != null) producto.setSelectedItem(sel);
    proveedor.removeAllItems();
    proveedor.addItem(null);
    for (Proveedor p : pc.listarActivos()) proveedor.addItem(p);
    proveedor.setSelectedIndex(0);
  }

  public void refrescarTablas() {
    stock.setRowCount(0);
    for (Producto p : prod.listar(true))
      stock.addRow(
          new Object[] {
            p.getIdProducto(),
            p.getNombre(),
            p.getTipoProducto(),
            p.getStock(),
            p.isEstado() ? "Activo" : "Inactivo"
          });
    mov.setRowCount(0);
    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    for (MovimientoInventario x : c.listar())
      mov.addRow(
          new Object[] {
            x.getIdMovimiento(),
            x.getTipo(),
            x.getProducto().getNombre(),
            x.getCantidad(),
            x.getFecha().format(f),
            x.getMotivo(),
            x.getProveedor() == null ? "—" : x.getProveedor().getNombre()
          });
  }
}
