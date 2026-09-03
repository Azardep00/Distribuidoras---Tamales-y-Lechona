package view;

import controller.ProveedorController;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Proveedor;

public class ProveedorPanel extends JPanel {
    private final ProveedorController c;
    private final JTextField nombre = new JTextField(18),
            telefono = new JTextField(14),
            correo = new JTextField(20),
            direccion = new JTextField(20),
            buscar = new JTextField(18);
    private final DefaultTableModel m =
            new DefaultTableModel(
                    new Object[]{"ID", "Proveedor", "Teléfono", "Correo", "Dirección", "Estado"}, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable table = new JTable(m);
    private Integer selected;

    public ProveedorPanel(ProveedorController c) {
        this.c = c;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel form = UI.card("Datos del proveedor");
        JPanel f = new JPanel(new GridBagLayout());
        f.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        row(f, g, 0, "Empresa *", nombre);
        row(f, g, 1, "Teléfono *", telefono);
        row(f, g, 2, "Correo", correo);
        row(f, g, 3, "Dirección", direccion);
        form.add(f, BorderLayout.CENTER);
        JPanel a = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton save = UI.button("Registrar proveedor"),
                edit = UI.button("Guardar cambios"),
                off = UI.button("Desactivar"),
                clear = UI.button("Nuevo / limpiar"),
                consult = UI.button("Abrir consulta");
        a.add(save);
        a.add(edit);
        a.add(off);
        a.add(clear);
        a.add(consult);
        form.add(a, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);
        JPanel cat = UI.card("Proveedores activos");
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.setOpaque(false);
        search.add(new JLabel("Buscar:"));
        search.add(buscar);
        JButton b = UI.button("Buscar");
        search.add(b);
        cat.add(search, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true);
        UI.styleTable(table);
        cat.add(new JScrollPane(table), BorderLayout.CENTER);
        add(cat, BorderLayout.CENTER);
        save.addActionListener(e -> registrar());
        edit.addActionListener(e -> editar());
        off.addActionListener(e -> desactivar());
        clear.addActionListener(e -> limpiar());
        b.addActionListener(e -> refrescar(buscar.getText()));
        consult.addActionListener(e -> mostrarConsulta());
        table.getSelectionModel().addListSelectionListener(e -> cargar());
        UI.styleField(nombre);
        UI.styleField(telefono);
        UI.styleField(correo);
        UI.styleField(direccion);
        UI.styleField(buscar);
        refrescar("");
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

    private void validar() {
        if (nombre.getText().isBlank() || telefono.getText().isBlank())
            throw new IllegalArgumentException("Empresa y teléfono son obligatorios.");
    }

    private void registrar() {
        try {
            validar();
            c.registrar(
                    new Proveedor(
                            0,
                            nombre.getText().trim(),
                            telefono.getText().trim(),
                            correo.getText().trim(),
                            direccion.getText().trim(),
                            true));
            UI.info(this, "Proveedor registrado.");
            limpiar();
            refrescar("");
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void editar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un proveedor.");
            return;
        }
        try {
            validar();
            c.actualizar(
                    selected, nombre.getText(), telefono.getText(), correo.getText(), direccion.getText());
            UI.info(this, "Proveedor actualizado.");
            limpiar();
            refrescar(buscar.getText());
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void desactivar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un proveedor.");
            return;
        }
        if (UI.confirm(this, "¿Desactivar el proveedor? Los movimientos históricos se conservarán.")) {
            c.desactivar(selected);
            limpiar();
            refrescar(buscar.getText());
        }
    }

    private void cargar() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int mr = table.convertRowIndexToModel(r);
        selected = (Integer) m.getValueAt(mr, 0);
        Proveedor p = c.buscarPorId(selected);
        if (p != null) {
            nombre.setText(p.getNombre());
            telefono.setText(p.getTelefono());
            correo.setText(p.getCorreo());
            direccion.setText(p.getDireccion());
        }
    }

    private void limpiar() {
        selected = null;
        nombre.setText("");
        telefono.setText("");
        correo.setText("");
        direccion.setText("");
        table.clearSelection();
    }

    private void refrescar(String q) {
        m.setRowCount(0);
        var ps = q.isBlank() ? c.listarActivos() : c.buscar(q);
        for (Proveedor p : ps)
            m.addRow(
                    new Object[]{
                            p.getIdProveedor(),
                            p.getNombre(),
                            p.getTelefono(),
                            p.getCorreo(),
                            p.getDireccion(),
                            p.isEstado() ? "Activo" : "Inactivo"
                    });
    }

    public void refrescarTabla() {
        refrescar("");
    }

    public void mostrarConsulta() {
        JDialog d =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Consulta de proveedores",
                        Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout(8, 8));
        DefaultTableModel cm =
                new DefaultTableModel(
                        new Object[]{"ID", "Proveedor", "Teléfono", "Correo", "Dirección"}, 0);
        JTable t = new JTable(cm);
        var ps = c.listarActivos();
        if (ps.isEmpty()) cm.addRow(new Object[]{"-", "No hay proveedores registrados", "", "", ""});
        else
            for (Proveedor p : ps)
                cm.addRow(
                        new Object[]{
                                p.getIdProveedor(), p.getNombre(), p.getTelefono(), p.getCorreo(), p.getDireccion()
                        });
        d.add(new JScrollPane(t));
        d.setSize(760, 380);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
