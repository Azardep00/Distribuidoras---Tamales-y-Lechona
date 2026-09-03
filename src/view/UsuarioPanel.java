package view;

import controller.UsuarioController;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.*;

public class UsuarioPanel extends JPanel {
    private final UsuarioController c;
    private final JComboBox<String> tipo = new JComboBox<>(new String[]{"Cliente", "Empleado"});
    private final JTextField nombre = new JTextField(16),
            apellido = new JTextField(16),
            telefono = new JTextField(14),
            correo = new JTextField(20);
    private final JPasswordField password = new JPasswordField(16);
    private final JSpinner nacimiento = fecha();
    private final JComboBox<TipoCliente> tipoCliente = new JComboBox<>(TipoCliente.values());
    private final JTextField direccion = new JTextField(20), cargo = new JTextField(16);
    private final JSpinner contratacion = fecha();
    private final CardLayout cards = new CardLayout();
    private final JPanel specific = new JPanel(cards);
    private final JTextField buscar = new JTextField(18);
    private final DefaultTableModel m =
            new DefaultTableModel(
                    new Object[]{"ID", "Tipo", "Nombre", "Teléfono", "Correo", "Información", "Estado"}, 0) {
                public boolean isCellEditable(int r, int col) {
                    return false;
                }
            };
    private final JTable table = new JTable(m);
    private Integer selected;

    public UsuarioPanel(UsuarioController c) {
        this.c = c;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        nacimiento.setEditor(new JSpinner.DateEditor(nacimiento, "dd/MM/yyyy"));
        contratacion.setEditor(new JSpinner.DateEditor(contratacion, "dd/MM/yyyy"));
        JPanel form = UI.card("Usuarios — un único flujo para Cliente y Empleado");
        JPanel f = new JPanel(new GridBagLayout());
        f.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        row(f, g, 0, "Tipo de usuario", tipo);
        row(f, g, 1, "Nombre *", nombre);
        row(f, g, 2, "Apellido *", apellido);
        row(f, g, 3, "Teléfono *", telefono);
        row(f, g, 4, "Correo *", correo);
        row(f, g, 5, "Contraseña", password);
        row(f, g, 6, "Fecha de nacimiento", nacimiento);
        JPanel cl = new JPanel(new GridLayout(1, 4, 6, 6));
        cl.setOpaque(false);
        cl.add(new JLabel("Tipo cliente"));
        cl.add(tipoCliente);
        cl.add(new JLabel("Dirección"));
        cl.add(direccion);
        JPanel em = new JPanel(new GridLayout(1, 4, 6, 6));
        em.setOpaque(false);
        em.add(new JLabel("Cargo"));
        em.add(cargo);
        em.add(new JLabel("Fecha contratación"));
        em.add(contratacion);
        specific.setOpaque(false);
        specific.add(cl, "Cliente");
        specific.add(em, "Empleado");
        row(f, g, 7, "Datos específicos", specific);
        form.add(f, BorderLayout.CENTER);
        JPanel a = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton save = UI.button("Registrar usuario"),
                edit = UI.button("Guardar cambios"),
                off = UI.button("Desactivar"),
                clear = UI.button("Nuevo / limpiar");
        a.add(save);
        a.add(edit);
        a.add(off);
        a.add(clear);
        form.add(a, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);
        JPanel list = UI.card("Directorio de usuarios");
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT));
        s.setOpaque(false);
        s.add(new JLabel("Buscar:"));
        s.add(buscar);
        JButton b = UI.button("Buscar"), all = UI.button("Mostrar todos");
        s.add(b);
        s.add(all);
        list.add(s, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true);
        UI.styleTable(table);
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        add(list, BorderLayout.CENTER);
        tipo.addActionListener(e -> cards.show(specific, (String) tipo.getSelectedItem()));
        save.addActionListener(e -> registrar());
        edit.addActionListener(e -> editar());
        off.addActionListener(e -> desactivar());
        clear.addActionListener(e -> limpiar());
        b.addActionListener(e -> refrescar(buscar.getText()));
        all.addActionListener(
                e -> {
                    buscar.setText("");
        refrescar("");
                });
        table.getSelectionModel().addListSelectionListener(e -> cargar());
        cards.show(specific, "Cliente");
        UI.styleField(nombre);
        UI.styleField(apellido);
        UI.styleField(telefono);
        UI.styleField(correo);
        UI.styleField(password);
        UI.styleField(tipoCliente);
        UI.styleField(direccion);
        UI.styleField(cargo);
        UI.styleField(contratacion);
        UI.styleField(buscar);
        refrescar("");
    }

    private JSpinner fecha() {
        return new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
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

    private LocalDate ld(JSpinner s) {
        return ((Date) s.getValue()).toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
    }

    private Usuario construir(int id) {
        String t = (String) tipo.getSelectedItem(),
                n = nombre.getText().trim(),
                a = apellido.getText().trim(),
                tel = telefono.getText().trim(),
                co = correo.getText().trim(),
                pw = new String(password.getPassword());
        if (t.equals("Cliente"))
            return new Cliente(
                    id,
                    n,
                    a,
                    tel,
                    co,
                    pw,
                    true,
                    ld(nacimiento),
                    0,
                    (TipoCliente) tipoCliente.getSelectedItem(),
                    direccion.getText().trim(),
                    LocalDate.now());
        return new Empleado(
                id,
                n,
                a,
                tel,
                co,
                pw,
                true,
                ld(nacimiento),
                cargo.getText().trim(),
                (Date) contratacion.getValue());
    }

    private void validar(boolean nuevo) {
        if (nombre.getText().isBlank()
                || apellido.getText().isBlank()
                || telefono.getText().isBlank()
                || correo.getText().isBlank())
            throw new IllegalArgumentException("Completa nombre, apellido, teléfono y correo.");
        if (nuevo && password.getPassword().length == 0)
            throw new IllegalArgumentException("La contraseña es obligatoria al registrar.");
    }

    private void registrar() {
        try {
            validar(true);
            Usuario u = construir(0);
            c.registrar(u);
            UI.info(this, "Usuario registrado con ID " + u.getIdUsuario());
            limpiar();
            refrescar("");
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void editar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un usuario.");
            return;
        }
        try {
            validar(false);
            c.actualizar(selected, construir(selected));
            UI.info(this, "Cambios guardados.");
            limpiar();
            refrescar(buscar.getText());
        } catch (Exception e) {
            UI.warn(this, e.getMessage());
        }
    }

    private void desactivar() {
        if (selected == null) {
            UI.warn(this, "Selecciona un usuario.");
            return;
        }
        if (UI.confirm(
                this, "¿Desactivar el usuario seleccionado? Sus registros históricos se conservarán.")) {
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
        Usuario u = c.buscarPorId(selected).orElse(null);
        if (u == null) return;
        nombre.setText(u.getNombre());
        apellido.setText(u.getApellido());
        telefono.setText(u.getTelefono());
        correo.setText(u.getCorreo());
        if (u.getFechaNacimiento() != null)
            nacimiento.setValue(
                    Date.from(
                            u.getFechaNacimiento().atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant()));
        if (u instanceof Cliente x) {
            tipo.setSelectedItem("Cliente");
            tipoCliente.setSelectedItem(x.getTipoCliente());
            direccion.setText(x.getDireccion());
            cards.show(specific, "Cliente");
        } else if (u instanceof Empleado x) {
            tipo.setSelectedItem("Empleado");
            cargo.setText(x.getCargo());
            if (x.getFechaContratacion() != null) contratacion.setValue(x.getFechaContratacion());
            cards.show(specific, "Empleado");
        }
    }

    private void limpiar() {
        selected = null;
        nombre.setText("");
        apellido.setText("");
        telefono.setText("");
        correo.setText("");
        password.setText("");
        direccion.setText("");
        cargo.setText("");
        table.clearSelection();
    }

    public void refrescarTabla() {
        refrescar("");
    }

    private void refrescar(String q) {
        m.setRowCount(0);
        String s = q == null ? "" : q.trim().toLowerCase();
        for (Usuario u : c.listar()) {
            if (!s.isBlank()
                    && !((u.getNombre() + " " + u.getApellido() + " " + u.getCorreo() + " " + u.getTelefono())
                    .toLowerCase()
                    .contains(s))) continue;
            String info =
                    u instanceof Cliente x
                            ? x.getTipoCliente() + " · " + x.getDireccion()
                            : ((Empleado) u).getCargo();
            m.addRow(
                    new Object[]{
                            u.getIdUsuario(),
                            u instanceof Cliente ? "Cliente" : "Empleado",
                            u.getNombre() + " " + u.getApellido(),
                            u.getTelefono(),
                            u.getCorreo(),
                            info,
                            u.isEstado() ? "Activo" : "Inactivo"
                    });
        }
    }
}
