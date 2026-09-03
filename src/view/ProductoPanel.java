package view;

import controller.ProductoController;

import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.*;

public class ProductoPanel extends JPanel {
    private final ProductoController c;
    private final JComboBox<String> tipo = new JComboBox<>(new String[]{"Tamal", "Lechona"});
    private final JTextField nombre = new JTextField(22),
            descripcion = new JTextField(28),
            precio = new JTextField(12);
    private final JComboBox<TipoTamal> tipoTamal = new JComboBox<>(TipoTamal.values());
    private final JComboBox<TamanoTamal> tamTamal = new JComboBox<>(TamanoTamal.values());
    private final JComboBox<TamanoLechona> tamLechona = new JComboBox<>(TamanoLechona.values());
    private final JSpinner porciones = new JSpinner(new SpinnerNumberModel(8, 1, 1000, 1));
    private final CardLayout cards = new CardLayout();
    private final JPanel specific = new JPanel(cards);
    private final JTextField buscar = new JTextField(20);
    private final DefaultTableModel m =
            new DefaultTableModel(
                    new Object[]{"ID", "Tipo", "Nombre", "Detalle", "Precio", "Stock", "Estado"}, 0) {
                public boolean isCellEditable(int r, int col) {
                    return false;
                }
            };
    private final JTable table = new JTable(m);
    private Integer selected;

    public ProductoPanel(ProductoController c) {
        this.c = c;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel form = UI.card("Nuevo producto / editar seleccionado");
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        row(fields, g, 0, "Tipo", tipo);
        row(fields, g, 1, "Nombre comercial *", nombre);
        row(fields, g, 2, "Descripción", descripcion);
        row(fields, g, 3, "Precio COP *", precio);
        JPanel tamal = new JPanel(new GridLayout(1, 4, 8, 4));
        tamal.setOpaque(false);
        tamal.add(new JLabel("Tipo tamal"));
        tamal.add(tipoTamal);
        tamal.add(new JLabel("Tamaño"));
        tamal.add(tamTamal);
        JPanel lechona = new JPanel(new GridLayout(1, 4, 8, 4));
        lechona.setOpaque(false);
        lechona.add(new JLabel("Tamaño"));
        lechona.add(tamLechona);
        lechona.add(new JLabel("Porciones"));
        lechona.add(porciones);
        specific.add(tamal, "Tamal");
        specific.add(lechona, "Lechona");
        row(fields, g, 4, "Características", specific);
        form.add(fields, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton save = UI.button("Registrar producto"),
                edit = UI.button("Guardar cambios"),
                off = UI.button("Desactivar"),
                clear = UI.button("Nuevo / limpiar");
        actions.add(save);
        actions.add(edit);
        actions.add(off);
        actions.add(clear);
        form.add(actions, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);
        JPanel center = UI.card("Catálogo");
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.setOpaque(false);
        search.add(new JLabel("Buscar:"));
        search.add(buscar);
        JButton b = UI.button("Buscar");
        search.add(b);
        JButton all = UI.button("Mostrar todos");
        search.add(all);
        center.add(search, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true);
        UI.styleTable(table);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        tipo.addActionListener(e -> cards.show(specific, (String) tipo.getSelectedItem()));
        save.addActionListener(e -> registrar());
        edit.addActionListener(e -> editar());
        off.addActionListener(e -> desactivar());
        clear.addActionListener(e -> limpiar());
        b.addActionListener(e -> refrescarTabla(buscar.getText()));
        all.addActionListener(
                e -> {
                    buscar.setText("");
                    refrescarTabla("");
                });
        table.getSelectionModel().addListSelectionListener(e -> cargar());
        UI.styleField(tipo);
        UI.styleField(nombre);
        UI.styleField(descripcion);
        UI.styleField(precio);
        UI.styleField(tipoTamal);
        UI.styleField(tamTamal);
        UI.styleField(tamLechona);
        UI.styleField(porciones);
        UI.styleField(buscar);
        refrescarTabla("");
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

    private Producto construir(int id) {
        String n = nombre.getText().trim(), d = descripcion.getText().trim();
        BigDecimal pr = new BigDecimal(precio.getText().trim().replace(",", "."));
        if ("Tamal".equals(tipo.getSelectedItem()))
            return new Tamal(
                    id,
                    n,
                    d,
                    pr,
                    0,
                    true,
                    (TipoTamal) tipoTamal.getSelectedItem(),
                    (TamanoTamal) tamTamal.getSelectedItem());
        return new Lechona(
                id,
                n,
                d,
                pr,
                0,
                true,
                (TamanoLechona) tamLechona.getSelectedItem(),
                (Integer) porciones.getValue());
    }

    private void registrar() {
        try {
            c.registrar(construir(0));
            UI.info(this, "Producto registrado. El stock inicial se gestiona desde Inventario.");
            limpiar();
            refrescarTabla("");
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void editar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un producto.");
            return;
        }
        try {
            Producto p = construir(selected);
            c.actualizar(p);
            UI.info(this, "Producto actualizado.");
            limpiar();
            refrescarTabla(buscar.getText());
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void desactivar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un producto.");
            return;
        }
        if (UI.confirm(
                this, "¿Desactivar el producto seleccionado? Seguirá visible en el historial.")) {
            c.desactivar(selected);
            limpiar();
            refrescarTabla(buscar.getText());
        }
    }

    private void cargar() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int mr = table.convertRowIndexToModel(r);
        selected = (Integer) m.getValueAt(mr, 0);
        Producto p = c.buscarPorId(selected);
        if (p == null) return;
        tipo.setSelectedItem(p.getTipoProducto());
        nombre.setText(p.getNombre());
        descripcion.setText(p.getDescripcion());
        precio.setText(p.getPrecio().toPlainString());
        if (p instanceof Tamal t) {
            tipoTamal.setSelectedItem(t.getTipo());
            tamTamal.setSelectedItem(t.getTamaño());
        } else if (p instanceof Lechona l) {
            tamLechona.setSelectedItem(l.getTamaño());
            porciones.setValue(l.getNumeroPorciones());
        }
        cards.show(specific, p.getTipoProducto());
    }

    private void limpiar() {
        selected = null;
        nombre.setText("");
        descripcion.setText("");
        precio.setText("");
        table.clearSelection();
    }

    public void refrescarTabla() {
        refrescarTabla("");
    }

    private void refrescarTabla(String q) {
        m.setRowCount(0);
        var ps = q.isBlank() ? c.listar(true) : c.buscarPorNombre(q);
        for (Producto p : ps)
            m.addRow(
                    new Object[]{
                            p.getIdProducto(),
                            p.getTipoProducto(),
                            p.getNombre(),
                            p.getDetalleEspecifico(),
                            p.getPrecio(),
                            p.getStock(),
                            p.isEstado() ? "Activo" : "Inactivo"
                    });
    }
}
