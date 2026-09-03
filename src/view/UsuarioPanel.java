package view;

import controller.UsuarioController;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    private final JButton togglePassword = UI.compactButton("Mostrar");
    private final JPanel passwordPanel = new JPanel(new BorderLayout(6, 0));
    private final DatePicker nacimiento = new DatePicker();
    private final JComboBox<TipoCliente> tipoCliente = new JComboBox<>(TipoCliente.values());
    private final JTextField direccion = new JTextField(20), cargo = new JTextField(16);
    private final DatePicker contratacion = new DatePicker();
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
        passwordPanel.setOpaque(false);
        passwordPanel.add(password, BorderLayout.CENTER);
        passwordPanel.add(togglePassword, BorderLayout.EAST);
        togglePassword.setPreferredSize(new Dimension(100, 40));
        togglePassword.addActionListener(e -> {
            boolean visible = password.getEchoChar() == 0;
            password.setEchoChar(visible ? '\u2022' : 0);
            togglePassword.setText(visible ? "Mostrar" : "Ocultar");
            password.requestFocusInWindow();
        });
        password.setEchoChar('\u2022');
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
        row(f, g, 5, "Contraseña", passwordPanel);
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

    private static final class DatePicker extends JPanel {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private final JTextField text = new JTextField();
        private final JButton toggle = UI.compactButton("▾");
        private final JPopupMenu popup = new JPopupMenu();
        private final JButton[] cells = new JButton[42];
        private final JComboBox<String> monthCombo;
        private final JComboBox<Integer> yearCombo;
        private final JButton prev = UI.compactButton("◀");
        private final JButton next = UI.compactButton("▶");
        private LocalDate currentMonth = LocalDate.now();
        private LocalDate selected = LocalDate.now();

        DatePicker() {
            super(new BorderLayout(6, 0));
            setOpaque(false);
            text.setEditable(false);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            UI.styleField(text);
            text.setBackground(Color.WHITE);
            toggle.setPreferredSize(new Dimension(36, 36));
            add(text, BorderLayout.CENTER);
            add(toggle, BorderLayout.EAST);
            JPanel calendar = new JPanel(new BorderLayout(6, 6));
            calendar.setBorder(BorderFactory.createLineBorder(UI.LINE));
            calendar.setBackground(Color.WHITE);
            String[] monthNames = new String[12];
            for (int i = 0; i < 12; i++) {
                monthNames[i] = Month.of(i + 1).getDisplayName(java.time.format.TextStyle.FULL, new Locale("es", "ES"));
            }
            Integer[] years = new Integer[111];
            int cy = LocalDate.now().getYear();
            for (int i = 0; i < years.length; i++) years[i] = cy - 100 + i;
            monthCombo = new JComboBox<>(monthNames);
            yearCombo = new JComboBox<>(years);
            monthCombo.setFocusable(false);
            yearCombo.setFocusable(false);
            monthCombo.addActionListener(e -> {
                int m = monthCombo.getSelectedIndex() + 1;
                int y = (Integer) yearCombo.getSelectedItem();
                currentMonth = LocalDate.of(y, m, 1);
                renderCalendar();
            });
            yearCombo.addActionListener(e -> {
                int m = monthCombo.getSelectedIndex() + 1;
                int y = (Integer) yearCombo.getSelectedItem();
                currentMonth = LocalDate.of(y, m, 1);
                renderCalendar();
            });
            JPanel header = new JPanel(new BorderLayout(6, 0));
            header.setOpaque(false);
            prev.setPreferredSize(new Dimension(36, 28));
            next.setPreferredSize(new Dimension(36, 28));
            header.add(prev, BorderLayout.WEST);
            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            centerPanel.setOpaque(false);
            centerPanel.add(monthCombo);
            centerPanel.add(yearCombo);
            header.add(centerPanel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            JPanel days = new JPanel(new GridLayout(0, 7, 4, 4));
            String[] names = {"D", "L", "M", "M", "J", "V", "S"};
            for (String n : names) {
                JLabel d = new JLabel(n, SwingConstants.CENTER);
                d.setFont(new Font("SansSerif", Font.BOLD, 11));
                d.setForeground(UI.MUTED);
                days.add(d);
            }
            for (int i = 0; i < cells.length; i++) {
                JButton cell = new JButton();
                cell.setFocusPainted(false);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                cell.setBackground(Color.WHITE);
                cell.setForeground(UI.INK);
                cell.addActionListener(e -> {
                    LocalDate picked = (LocalDate) ((JButton) e.getSource()).getClientProperty("day");
                    if (picked == null) return;
                    selected = picked;
                    text.setText(selected.format(FORMATTER));
                    popup.setVisible(false);
                    renderCalendar();
                });
                cells[i] = cell;
                days.add(cell);
            }
            calendar.add(header, BorderLayout.NORTH);
            calendar.add(days, BorderLayout.CENTER);
            popup.add(calendar);
            prev.addActionListener(e -> {
                currentMonth = currentMonth.minusMonths(1);
                renderCalendar();
            });
            next.addActionListener(e -> {
                currentMonth = currentMonth.plusMonths(1);
                renderCalendar();
            });
            toggle.addActionListener(e -> {
                if (popup.isVisible()) {
                    popup.setVisible(false);
                } else {
                    currentMonth = selected.withDayOfMonth(1);
                    renderCalendar();
                    popup.show(DatePicker.this, 0, getHeight());
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !popup.isVisible()) {
                        currentMonth = selected.withDayOfMonth(1);
                        renderCalendar();
                        popup.show(DatePicker.this, 0, getHeight());
                    }
                }
            });
            renderCalendar();
        }

        public void setValue(Date date) {
            selected = date == null ? LocalDate.now() : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            currentMonth = selected.withDayOfMonth(1);
            renderCalendar();
        }

        public Date getValue() {
            return Date.from(selected.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        private void renderCalendar() {
            // update month and year combos
            monthCombo.setSelectedIndex(currentMonth.getMonthValue() - 1);
            yearCombo.setSelectedItem(currentMonth.getYear());
            LocalDate first = currentMonth.withDayOfMonth(1);
            int offset = (first.getDayOfWeek().getValue() % 7);
            for (int i = 0; i < cells.length; i++) {
                LocalDate day = first.plusDays(i - offset);
                JButton cell = cells[i];
                cell.setText(String.valueOf(day.getDayOfMonth()));
                cell.setEnabled(day.getMonth().equals(currentMonth.getMonth()));
                boolean isSelected = day.equals(selected);
                cell.setBackground(isSelected ? new Color(30, 30, 30) : (day.getMonth().equals(currentMonth.getMonth()) ? Color.WHITE : new Color(245, 245, 245)));
                cell.setForeground(isSelected ? Color.WHITE : (day.getMonth().equals(currentMonth.getMonth()) ? UI.INK : new Color(160, 160, 165)));
                cell.setFont(new Font("SansSerif", Font.PLAIN, 12));
                cell.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                cell.putClientProperty("day", day);
            }
            text.setText(selected.format(FORMATTER));
        }
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

    private LocalDate ld(DatePicker s) {
        Date v = s.getValue();
        return v == null ? null : v.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
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
           nacimiento.setValue(Date.from(
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
        password.setEchoChar('\u2022');
        togglePassword.setText("Mostrar");
        nacimiento.setValue(new Date());
        contratacion.setValue(new Date());
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
