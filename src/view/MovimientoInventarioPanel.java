package view;

import controller.MovimientoInventarioController;
import controller.ProductoController;
import controller.ProveedorController;
import model.MovimientoInventario;
import model.Producto;
import model.Proveedor;
import model.TipoMovimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista gráfica de MovimientoInventario. Replica el menú 2 (MOVIMIENTO INVENTARIO)
 * de Main.java: registrar entrada/salida (con proveedor solo en ENTRADA),
 * consultar stock y ver movimientos.
 */
public class MovimientoInventarioPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final MovimientoInventarioController movimientoController;
    private final ProveedorController proveedorController;

    private final DefaultTableModel modeloStock;
    private final DefaultTableModel modeloMovimientos;

    private final JComboBox<Producto> comboProducto = new JComboBox<>();
    private final JComboBox<Proveedor> comboProveedor = new JComboBox<>();
    private final JTextField txtCantidad = new JTextField(5);
    private final JTextField txtMotivo = new JTextField(15);

    public MovimientoInventarioPanel(MovimientoInventarioController movimientoController,
                                     ProveedorController proveedorController) {
        this.movimientoController = movimientoController;
        this.proveedorController = proveedorController;

        // Los combos no dependen de toString() del modelo: se define aquí cómo se ven,
        // sin tocar las clases Producto/Proveedor.
        comboProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto p) {
                    setText("#" + p.getIdProducto() + " - " + p.getNombre() + " (stock: " + p.getStock() + ")");
                }
                return this;
            }
        });
        comboProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proveedor p) {
                    setText("#" + p.getIdProveedor() + " - " + p.getNombre());
                }
                return this;
            }
        });

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulario para registrar Entrada/Salida -----
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(formulario, gbc, fila++, "Producto:", comboProducto);
        agregarCampo(formulario, gbc, fila++, "Cantidad:", txtCantidad);
        agregarCampo(formulario, gbc, fila++, "Motivo:", txtMotivo);
        agregarCampo(formulario, gbc, fila++, "Proveedor (solo entradas):", comboProveedor);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEntrada = new JButton("1. Registrar entrada");
        JButton btnSalida = new JButton("2. Registrar salida");
        JButton btnStock = new JButton("3. Consultar stock");
        JButton btnMovimientos = new JButton("4. Ver movimientos");
        botones.add(btnEntrada);
        botones.add(btnSalida);
        botones.add(btnStock);
        botones.add(btnMovimientos);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        // ----- Panel central con dos tablas: Stock y Movimientos -----
        modeloStock = new DefaultTableModel(new Object[]{"ID", "Nombre", "Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaStock = new JTable(modeloStock);

        modeloMovimientos = new DefaultTableModel(
                new Object[]{"ID Mov.", "Tipo", "Producto", "Cantidad", "Fecha", "Motivo", "Proveedor"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaMovimientos = new JTable(modeloMovimientos);

        JTabbedPane tablas = new JTabbedPane();
        tablas.addTab("Stock de productos", new JScrollPane(tablaStock));
        tablas.addTab("Movimientos", new JScrollPane(tablaMovimientos));
        add(tablas, BorderLayout.CENTER);

        // ----- Acciones -----

        btnEntrada.addActionListener(e -> registrarMovimiento(TipoMovimiento.ENTRADA));
        btnSalida.addActionListener(e -> registrarMovimiento(TipoMovimiento.SALIDA));
        btnStock.addActionListener(e -> { refrescarStock(); tablas.setSelectedIndex(0); });
        btnMovimientos.addActionListener(e -> { refrescarMovimientos(); tablas.setSelectedIndex(1); });

        refrescarCombos();
        refrescarStock();
        refrescarMovimientos();
    }

    /** Igual que el bloque "if (opcion == 1 || opcion == 2)" de menuMovimientoInventario(...). */
    private void registrarMovimiento(TipoMovimiento tipo) {
        refrescarCombos();

        Producto producto = (Producto) comboProducto.getSelectedItem();
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "No hay productos registrados.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad numérica válida.",
                    "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String motivo = txtMotivo.getText();

        MovimientoInventario movimiento;
        if (tipo == TipoMovimiento.ENTRADA) {
            Proveedor proveedor = (Proveedor) comboProveedor.getSelectedItem();
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró un proveedor con ese ID. Se registrará sin proveedor.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
            movimiento = new MovimientoInventario(0, tipo, cantidad, motivo, producto, proveedor);
        } else {
            movimiento = new MovimientoInventario(0, tipo, cantidad, motivo, producto);
        }

        boolean resultado = movimientoController.registrarMovimiento(movimiento);

        if (resultado) {
            JOptionPane.showMessageDialog(this,
                    (tipo == TipoMovimiento.ENTRADA ? "Entrada" : "Salida") +
                            " registrada correctamente.\nNuevo stock: " + producto.getStock());
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el movimiento (stock insuficiente o tipo inválido).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        txtCantidad.setText("");
        txtMotivo.setText("");
        refrescarStock();
        refrescarMovimientos();
        refrescarCombos();
    }

    private void refrescarCombos() {
        Object productoSeleccionado = comboProducto.getSelectedItem();
        comboProducto.removeAllItems();
        for (Producto p : ProductoController.listarProductos()) {
            comboProducto.addItem(p);
        }
        if (productoSeleccionado != null) comboProducto.setSelectedItem(productoSeleccionado);

        Object proveedorSeleccionado = comboProveedor.getSelectedItem();
        comboProveedor.removeAllItems();
        for (Proveedor p : proveedorController.listarProveedores()) {
            comboProveedor.addItem(p);
        }
        if (proveedorSeleccionado != null) comboProveedor.setSelectedItem(proveedorSeleccionado);
    }

    /** Igual que Main.mostrarProductosDisponibles() (opción 3, "Consultar stock"). */
    public void refrescarStock() {
        modeloStock.setRowCount(0);
        List<Producto> productos = ProductoController.listarProductos();
        for (Producto p : productos) {
            modeloStock.addRow(new Object[]{p.getIdProducto(), p.getNombre(), p.getStock()});
        }
    }

    /** Igual que MovimientoInventarioView.mostrarMovimientos(...) (opción 4, "Ver movimientos"). */
    public void refrescarMovimientos() {
        modeloMovimientos.setRowCount(0);
        List<MovimientoInventario> movimientos = movimientoController.listarMovimientos();
        for (MovimientoInventario m : movimientos) {
            modeloMovimientos.addRow(new Object[]{
                    m.getIdMovimiento(),
                    m.getTipo(),
                    m.getProducto().getNombre(),
                    m.getCantidad(),
                    m.getFecha().format(FORMATO_FECHA),
                    m.getMotivo(),
                    m.getProveedor() != null ? m.getProveedor().getNombre() : "-"
            });
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }
}
