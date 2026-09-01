package view;

import controller.ProductoController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vista gráfica del CRUD de Producto (Tamal y Lechona).
 * Replica el menú 3 (PRODUCTO) de Main.java: capturarProducto(...) pedía
 * por consola tipo, nombre, descripción, precio, stock, estado y los
 * campos propios de Tamal (tipo/tamaño) o Lechona (tamaño/porciones).
 * Aquí se pide lo mismo pero con combos y campos de texto.
 */
public class ProductoPanel extends JPanel {

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private final JTextField txtId = new JTextField(5);
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtDescripcion = new JTextField(15);
    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtStock = new JTextField(5);
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);

    private final JComboBox<String> comboTipoProducto = new JComboBox<>(new String[]{"Tamal", "Lechona"});

    // Campos específicos de Tamal
    private final JComboBox<TipoTamal> comboTipoTamal = new JComboBox<>(TipoTamal.values());
    private final JComboBox<TamañoTamal> comboTamañoTamal = new JComboBox<>(TamañoTamal.values());

    // Campos específicos de Lechona
    private final JComboBox<TamañoLechona> comboTamañoLechona = new JComboBox<>(TamañoLechona.values());
    private final JTextField txtPorciones = new JTextField(5);

    private final JPanel panelTamal = new JPanel();
    private final JPanel panelLechona = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelEspecifico = new JPanel();

    public ProductoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(formulario, gbc, fila++, "ID (actualizar/eliminar):", txtId);
        agregarCampo(formulario, gbc, fila++, "Tipo de producto:", comboTipoProducto);
        agregarCampo(formulario, gbc, fila++, "Nombre:", txtNombre);
        agregarCampo(formulario, gbc, fila++, "Descripción:", txtDescripcion);
        agregarCampo(formulario, gbc, fila++, "Precio:", txtPrecio);
        agregarCampo(formulario, gbc, fila++, "Stock:", txtStock);
        agregarCampo(formulario, gbc, fila++, "Estado:", chkActivo);

        panelTamal.setLayout(new GridLayout(2, 2, 4, 4));
        panelTamal.add(new JLabel("Tipo de tamal:"));
        panelTamal.add(comboTipoTamal);
        panelTamal.add(new JLabel("Tamaño:"));
        panelTamal.add(comboTamañoTamal);

        panelLechona.setLayout(new GridLayout(2, 2, 4, 4));
        panelLechona.add(new JLabel("Tamaño:"));
        panelLechona.add(comboTamañoLechona);
        panelLechona.add(new JLabel("Número de porciones:"));
        panelLechona.add(txtPorciones);

        panelEspecifico.setLayout(cardLayout);
        panelEspecifico.add(panelTamal, "Tamal");
        panelEspecifico.add(panelLechona, "Lechona");

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        formulario.add(panelEspecifico, gbc);

        comboTipoProducto.addActionListener(e ->
                cardLayout.show(panelEspecifico, (String) comboTipoProducto.getSelectedItem()));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("1. Registrar producto");
        JButton btnListar = new JButton("2. Listar productos");
        JButton btnActualizar = new JButton("3. Actualizar producto");
        JButton btnEliminar = new JButton("4. Eliminar producto");
        botones.add(btnRegistrar);
        botones.add(btnListar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Tipo", "Precio", "Stock", "Activo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> {
            // Igual que "opcion == 1" en menuProducto(...): usa siguienteIdProducto()
            Producto nuevo = construirProducto(siguienteIdProducto());
            if (nuevo == null) return;
            try {
                ProductoController.agregarProducto(nuevo);
                JOptionPane.showMessageDialog(this, "Producto registrado correctamente.");
                limpiarCampos();
                refrescarTabla();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnListar.addActionListener(e -> refrescarTabla());

        btnActualizar.addActionListener(e -> {
            // Igual que "opcion == 3": no se puede cambiar el tipo de producto
            Integer id = leerId();
            if (id == null) return;

            Producto existente = ProductoController.buscarProducto(id);
            if (existente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un producto con ese ID.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto actualizado = construirProducto(id);
            if (actualizado == null) return;

            if (!actualizado.getClass().equals(existente.getClass())) {
                JOptionPane.showMessageDialog(this,
                        "El tipo de producto no puede cambiar (era " +
                                existente.getClass().getSimpleName() + "). Actualización cancelada.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProductoController.actualizarProducto(actualizado);
            JOptionPane.showMessageDialog(this, "Producto actualizado.");
            refrescarTabla();
        });

        btnEliminar.addActionListener(e -> {
            // Igual que "opcion == 4"
            Integer id = leerId();
            if (id == null) return;
            ProductoController.eliminarProducto(id);
            refrescarTabla();
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila2 = tabla.getSelectedRow();
            if (fila2 < 0) return;
            int id = (int) modeloTabla.getValueAt(fila2, 0);
            Producto p = ProductoController.buscarProducto(id);
            if (p == null) return;

            txtId.setText(String.valueOf(p.getIdProducto()));
            txtNombre.setText(p.getNombre());
            txtDescripcion.setText(p.getDescripcion());
            txtPrecio.setText(p.getPrecio().toString());
            txtStock.setText(String.valueOf(p.getStock()));
            chkActivo.setSelected(p.isEstado());

            if (p instanceof Tamal tamal) {
                comboTipoProducto.setSelectedItem("Tamal");
                cardLayout.show(panelEspecifico, "Tamal");
                comboTipoTamal.setSelectedItem(tamal.getTipo());
                comboTamañoTamal.setSelectedItem(tamal.getTamaño());
            } else if (p instanceof Lechona lechona) {
                comboTipoProducto.setSelectedItem("Lechona");
                cardLayout.show(panelEspecifico, "Lechona");
                comboTamañoLechona.setSelectedItem(lechona.getTamaño());
                txtPorciones.setText(String.valueOf(lechona.getNumeroPorciones()));
            }
        });

        refrescarTabla();
    }

    /** Equivalente exacto a Main.capturarProducto(scanner, idProducto). */
    private Producto construirProducto(int idProducto) {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido, se usará 0.");
            precio = BigDecimal.ZERO;
        }

        int stock;
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        boolean estado = chkActivo.isSelected();
        String tipoSeleccionado = (String) comboTipoProducto.getSelectedItem();

        if ("Tamal".equals(tipoSeleccionado)) {
            TipoTamal tipoTamal = (TipoTamal) comboTipoTamal.getSelectedItem();
            TamañoTamal tamañoTamal = (TamañoTamal) comboTamañoTamal.getSelectedItem();
            return new Tamal(idProducto, nombre, descripcion, precio, stock, estado, tipoTamal, tamañoTamal);
        } else {
            TamañoLechona tamañoLechona = (TamañoLechona) comboTamañoLechona.getSelectedItem();
            int porciones;
            try {
                porciones = Integer.parseInt(txtPorciones.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número de porciones inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new Lechona(idProducto, nombre, descripcion, precio, stock, estado, tamañoLechona, porciones);
        }
    }

    /** Igual a Main.siguienteIdProducto(). */
    private int siguienteIdProducto() {
        int max = 0;
        for (Producto p : ProductoController.listarProductos()) {
            if (p.getIdProducto() > max) {
                max = p.getIdProducto();
            }
        }
        return max + 1;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private Integer leerId() {
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numérico válido.",
                    "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        chkActivo.setSelected(true);
        txtPorciones.setText("");
    }

    /** Equivalente a Main.mostrarProductosDisponibles() / ProductoView.mostrarProductos(...). */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = ProductoController.listarProductos();
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                    p.getIdProducto(), p.getNombre(), p.getClass().getSimpleName(),
                    p.getPrecio(), p.getStock(), p.isEstado() ? "Sí" : "No"
            });
        }
    }
}
