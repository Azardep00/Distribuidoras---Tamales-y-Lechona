package view;

import controller.ProveedorController;
import model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista gráfica del CRUD de Proveedor.
 * Reutiliza EXACTAMENTE la misma lógica que el menú 1 (PROVEEDOR) de Main.java,
 * solo que en vez de leer con Scanner y mostrar con System.out, usa
 * JTextField/JTable. El controller y el modelo no se tocan.
 */
public class ProveedorPanel extends JPanel {

    private final ProveedorController proveedorController;

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private final JTextField txtId = new JTextField(5);
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtTelefono = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(15);
    private final JTextField txtDireccion = new JTextField(15);

    public ProveedorPanel(ProveedorController proveedorController) {
        this.proveedorController = proveedorController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulario (equivalente a lo que pedía el Scanner) -----
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(formulario, gbc, fila++, "ID (para actualizar/eliminar):", txtId);
        agregarCampo(formulario, gbc, fila++, "Nombre:", txtNombre);
        agregarCampo(formulario, gbc, fila++, "Teléfono:", txtTelefono);
        agregarCampo(formulario, gbc, fila++, "Correo:", txtCorreo);
        agregarCampo(formulario, gbc, fila++, "Dirección:", txtDireccion);

        // ----- Botones = opciones del menú (1. Registrar 2. Consultar 3. Actualizar 4. Eliminar) -----
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("1. Registrar proveedor");
        JButton btnConsultar = new JButton("2. Consultar proveedores");
        JButton btnActualizar = new JButton("3. Actualizar proveedor");
        JButton btnEliminar = new JButton("4. Eliminar proveedor");
        botones.add(btnRegistrar);
        botones.add(btnConsultar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        // ----- Tabla = ProveedorView.mostrarProveedores(...) -----
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Teléfono", "Correo", "Dirección", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Acciones: llaman al mismo controller que usaba Main -----

        btnRegistrar.addActionListener(e -> {
            // Igual que "opcion == 1" en menuProveedor(...)
            String nombre = txtNombre.getText();
            String telefono = txtTelefono.getText();
            String correo = txtCorreo.getText();
            String direccion = txtDireccion.getText();

            Proveedor proveedor = new Proveedor(0, nombre, telefono, correo, direccion, true);
            proveedorController.registrarProveedor(proveedor);

            JOptionPane.showMessageDialog(this, "Proveedor registrado correctamente.");
            limpiarCampos();
            refrescarTabla();
        });

        btnConsultar.addActionListener(e -> refrescarTabla());

        btnActualizar.addActionListener(e -> {
            // Igual que "opcion == 3"
            Integer id = leerId();
            if (id == null) return;

            boolean actualizado = proveedorController.actualizarProveedor(
                    id, txtNombre.getText(), txtTelefono.getText(),
                    txtCorreo.getText(), txtDireccion.getText());

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro un proveedor con ese ID.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            refrescarTabla();
        });

        btnEliminar.addActionListener(e -> {
            // Igual que "opcion == 4"
            Integer id = leerId();
            if (id == null) return;

            boolean eliminado = proveedorController.eliminarProveedor(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro un proveedor con ese ID.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            refrescarTabla();
        });

        // Al hacer click en una fila de la tabla, se cargan sus datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila2 = tabla.getSelectedRow();
            if (fila2 >= 0) {
                txtId.setText(modeloTabla.getValueAt(fila2, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila2, 1).toString());
                txtTelefono.setText(modeloTabla.getValueAt(fila2, 2).toString());
                txtCorreo.setText(modeloTabla.getValueAt(fila2, 3).toString());
                txtDireccion.setText(modeloTabla.getValueAt(fila2, 4).toString());
            }
        });

        refrescarTabla();
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
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
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
    }

    /** Refresca la tabla leyendo la lista actual del controller (igual que ProveedorView). */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Proveedor> proveedores = proveedorController.listarProveedores();
        for (Proveedor p : proveedores) {
            modeloTabla.addRow(new Object[]{
                    p.getIdProveedor(), p.getNombre(), p.getTelefono(),
                    p.getCorreo(), p.getDireccion(), p.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }
}
