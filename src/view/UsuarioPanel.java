package view;

import controller.UsuarioController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Vista gráfica del CRUD de Usuario (Cliente / Empleado).
 * Replica el menú 4 (USUARIO) de Main.java: capturarCliente(...) y
 * capturarEmpleado(...) pedían los mismos campos por consola; aquí se
 * arman con un formulario que cambia según el tipo de usuario elegido.
 */
public class UsuarioPanel extends JPanel {

    private final UsuarioController usuarioController;

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private final JComboBox<String> comboTipoUsuario = new JComboBox<>(new String[]{"Cliente", "Empleado"});

    // Comunes (Usuario)
    private final JTextField txtIdUsuario = new JTextField(5);
    private final JTextField txtNombre = new JTextField(12);
    private final JTextField txtApellido = new JTextField(12);
    private final JTextField txtTelefono = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(15);
    private final JTextField txtContrasena = new JTextField(10);
    private final JTextField txtFechaNacimiento = new JTextField(10); // yyyy-MM-dd

    // Cliente
    private final JTextField txtIdCliente = new JTextField(5);
    private final JComboBox<TipoCliente> comboTipoCliente = new JComboBox<>(TipoCliente.values());
    private final JTextField txtDireccion = new JTextField(15);

    // Empleado
    private final JTextField txtCargo = new JTextField(12);
    private final JTextField txtFechaContratacion = new JTextField(10); // dd/MM/yyyy

    private final JPanel panelCliente = new JPanel(new GridLayout(3, 2, 4, 4));
    private final JPanel panelEmpleado = new JPanel(new GridLayout(2, 2, 4, 4));
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelEspecifico = new JPanel();

    public UsuarioPanel(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(formulario, gbc, fila++, "Tipo de usuario:", comboTipoUsuario);
        agregarCampo(formulario, gbc, fila++, "ID usuario (para buscar/actualizar/eliminar):", txtIdUsuario);
        agregarCampo(formulario, gbc, fila++, "Nombre:", txtNombre);
        agregarCampo(formulario, gbc, fila++, "Apellido:", txtApellido);
        agregarCampo(formulario, gbc, fila++, "Teléfono:", txtTelefono);
        agregarCampo(formulario, gbc, fila++, "Correo:", txtCorreo);
        agregarCampo(formulario, gbc, fila++, "Contraseña:", txtContrasena);
        agregarCampo(formulario, gbc, fila++, "Fecha nacimiento (yyyy-MM-dd):", txtFechaNacimiento);

        panelCliente.add(new JLabel("ID cliente:"));
        panelCliente.add(txtIdCliente);
        panelCliente.add(new JLabel("Tipo de cliente:"));
        panelCliente.add(comboTipoCliente);
        panelCliente.add(new JLabel("Dirección:"));
        panelCliente.add(txtDireccion);

        panelEmpleado.add(new JLabel("Cargo:"));
        panelEmpleado.add(txtCargo);
        panelEmpleado.add(new JLabel("Fecha contratación (dd/MM/yyyy):"));
        panelEmpleado.add(txtFechaContratacion);

        panelEspecifico.setLayout(cardLayout);
        panelEspecifico.add(panelCliente, "Cliente");
        panelEspecifico.add(panelEmpleado, "Empleado");

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        formulario.add(panelEspecifico, gbc);

        comboTipoUsuario.addActionListener(e ->
                cardLayout.show(panelEspecifico, (String) comboTipoUsuario.getSelectedItem()));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrarCliente = new JButton("1. Registrar cliente");
        JButton btnRegistrarEmpleado = new JButton("2. Registrar empleado");
        JButton btnListar = new JButton("3. Listar usuarios");
        JButton btnActualizar = new JButton("4. Actualizar usuario");
        JButton btnEliminar = new JButton("5. Eliminar usuario");
        botones.add(btnRegistrarCliente);
        botones.add(btnRegistrarEmpleado);
        botones.add(btnListar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Tipo", "Nombre", "Apellido", "Teléfono", "Correo", "Detalle"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Acciones (igual que menuUsuario en Main.java) -----

        btnRegistrarCliente.addActionListener(e -> {
            Cliente cliente = capturarCliente();
            if (cliente != null) {
                usuarioController.agregarUsuario(cliente);
                limpiarCampos();
                refrescarTabla();
            }
        });

        btnRegistrarEmpleado.addActionListener(e -> {
            Empleado empleado = capturarEmpleado();
            if (empleado != null) {
                usuarioController.agregarUsuario(empleado);
                limpiarCampos();
                refrescarTabla();
            }
        });

        btnListar.addActionListener(e -> refrescarTabla());

        btnActualizar.addActionListener(e -> {
            Integer id = leerId();
            if (id == null) return;

            Optional<Usuario> existente = usuarioController.buscarPorId(id);
            if (existente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un usuario con ese ID.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario actual = existente.get();
            Usuario datosNuevos;
            // Igual que Main: el tipo de usuario ya existente decide qué formulario capturar
            if (actual instanceof Cliente) {
                comboTipoUsuario.setSelectedItem("Cliente");
                datosNuevos = capturarCliente();
            } else {
                comboTipoUsuario.setSelectedItem("Empleado");
                datosNuevos = capturarEmpleado();
            }

            if (datosNuevos != null) {
                usuarioController.actualizarUsuario(id, datosNuevos);
                refrescarTabla();
            }
        });

        btnEliminar.addActionListener(e -> {
            Integer id = leerId();
            if (id == null) return;
            usuarioController.eliminarUsuario(id);
            refrescarTabla();
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila2 = tabla.getSelectedRow();
            if (fila2 < 0) return;
            int id = (int) modeloTabla.getValueAt(fila2, 0);
            Optional<Usuario> u = usuarioController.buscarPorId(id);
            u.ifPresent(this::cargarEnFormulario);
        });

        refrescarTabla();
    }

    private void cargarEnFormulario(Usuario u) {
        txtIdUsuario.setText(String.valueOf(u.getIdUsuario()));
        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        txtTelefono.setText(u.getTelefono());
        txtCorreo.setText(u.getCorreo());
        txtFechaNacimiento.setText(u.getFechaNacimiento() != null ? u.getFechaNacimiento().toString() : "");

        if (u instanceof Cliente c) {
            comboTipoUsuario.setSelectedItem("Cliente");
            cardLayout.show(panelEspecifico, "Cliente");
            txtIdCliente.setText(String.valueOf(c.getIdCliente()));
            comboTipoCliente.setSelectedItem(c.getTipoCliente());
            txtDireccion.setText(c.getDireccion());
        } else if (u instanceof Empleado emp) {
            comboTipoUsuario.setSelectedItem("Empleado");
            cardLayout.show(panelEspecifico, "Empleado");
            txtCargo.setText(emp.getCargo());
            if (emp.getFechaContratacion() != null) {
                txtFechaContratacion.setText(new SimpleDateFormat("dd/MM/yyyy").format(emp.getFechaContratacion()));
            }
        }
    }

    /** Equivalente exacto a Main.capturarCliente(scanner). */
    private Cliente capturarCliente() {
        Integer idUsuario = leerEnteroCampo(txtIdUsuario, "ID de usuario");
        if (idUsuario == null) return null;

        LocalDate fechaNacimiento = leerFecha(txtFechaNacimiento.getText());
        if (fechaNacimiento == null) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Operación cancelada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Integer idCliente = leerEnteroCampo(txtIdCliente, "ID de cliente");
        if (idCliente == null) return null;

        TipoCliente tipoCliente = (TipoCliente) comboTipoCliente.getSelectedItem();

        return new Cliente(idUsuario, txtNombre.getText(), txtApellido.getText(), txtTelefono.getText(),
                txtCorreo.getText(), txtContrasena.getText(), true, fechaNacimiento,
                idCliente, tipoCliente, txtDireccion.getText(), LocalDate.now());
    }

    /** Equivalente exacto a Main.capturarEmpleado(scanner). */
    private Empleado capturarEmpleado() {
        Integer idUsuario = leerEnteroCampo(txtIdUsuario, "ID de usuario");
        if (idUsuario == null) return null;

        LocalDate fechaNacimiento = leerFecha(txtFechaNacimiento.getText());
        if (fechaNacimiento == null) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Operación cancelada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Date fechaContratacion;
        try {
            fechaContratacion = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaContratacion.getText().trim());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Fecha inválida, se usará la fecha de hoy.");
            fechaContratacion = new Date();
        }

        return new Empleado(idUsuario, txtNombre.getText(), txtApellido.getText(), txtTelefono.getText(),
                txtCorreo.getText(), txtContrasena.getText(), true, fechaNacimiento,
                txtCargo.getText(), fechaContratacion);
    }

    private LocalDate leerFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer leerEnteroCampo(JTextField campo, String nombreCampo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, nombreCampo + " inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Integer leerId() {
        return leerEnteroCampo(txtIdUsuario, "ID de usuario");
    }

    private void limpiarCampos() {
        txtIdUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        txtFechaNacimiento.setText("");
        txtIdCliente.setText("");
        txtDireccion.setText("");
        txtCargo.setText("");
        txtFechaContratacion.setText("");
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    /** Igual que el "opcion == 3" (Listar usuarios) de menuUsuario(...). */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = usuarioController.listarUsuarios();
        for (Usuario u : usuarios) {
            String tipo;
            String detalle;
            if (u instanceof Cliente c) {
                tipo = "Cliente";
                detalle = "Tipo: " + c.getTipoCliente() + " | Dirección: " + c.getDireccion();
            } else if (u instanceof Empleado emp) {
                tipo = "Empleado";
                detalle = "Cargo: " + emp.getCargo();
            } else {
                tipo = "Usuario";
                detalle = "";
            }
            modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(), tipo, u.getNombre(), u.getApellido(),
                    u.getTelefono(), u.getCorreo(), detalle
            });
        }
    }
}
