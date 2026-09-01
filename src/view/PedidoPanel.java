package view;

import controller.PedidoController;
import controller.UsuarioController;
import model.Cliente;
import model.MetodoPago;
import model.Pedido;
import pagos.AdaptadorPagoWompi;
import pagos.ProcesadorPago;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista gráfica de Pedido. Replica el menú 5 (PEDIDO) de Main.java
 * (listar / actualizar / eliminar), y además reproduce cómo se crea un
 * pedido: en Main.java un pedido solo nace a través de
 * Cliente.realizarPedido(...) (ver Main.probarPagoWompi()), así que aquí
 * se elige un cliente ya registrado en Usuario y se usa ese mismo método.
 * También se deja el botón "Probar pago (Adapter Wompi)" con la misma
 * lógica del menú principal (opción 6).
 */
public class PedidoPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final UsuarioController usuarioController;

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    // ----- Crear pedido (Cliente.realizarPedido) -----
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JTextField txtIdPedido = new JTextField(5);
    private final JComboBox<MetodoPago> comboMetodoPagoNuevo = new JComboBox<>(MetodoPago.values());
    private final JTextField txtDireccionNueva = new JTextField(15);

    // ----- Actualizar pedido -----
    private final JTextField txtIdActualizar = new JTextField(5);
    private final JCheckBox chkPago = new JCheckBox("Pedido pagado");
    private final JComboBox<MetodoPago> comboMetodoPagoActualizar = new JComboBox<>(MetodoPago.values());
    private final JTextField txtDireccionActualizar = new JTextField(15);

    public PedidoPanel(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;

        comboCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente c) {
                    setText("#" + c.getIdUsuario() + " - " + c.getNombre() + " " + c.getApellido());
                }
                return this;
            }
        });

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Panel: crear pedido -----
        JPanel panelCrear = new JPanel(new GridBagLayout());
        panelCrear.setBorder(BorderFactory.createTitledBorder("Crear pedido (Cliente.realizarPedido)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(panelCrear, gbc, fila++, "Cliente:", comboCliente);
        agregarCampo(panelCrear, gbc, fila++, "ID pedido:", txtIdPedido);
        agregarCampo(panelCrear, gbc, fila++, "Método de pago:", comboMetodoPagoNuevo);
        agregarCampo(panelCrear, gbc, fila++, "Dirección de entrega:", txtDireccionNueva);

        JButton btnCrear = new JButton("Crear pedido");
        JButton btnPagarWompi = new JButton("Probar pago (Adapter Wompi)");
        JPanel botonesCrear = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botonesCrear.add(btnCrear);
        botonesCrear.add(btnPagarWompi);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        panelCrear.add(botonesCrear, gbc);

        // ----- Panel: actualizar pedido -----
        JPanel panelActualizar = new JPanel(new GridBagLayout());
        panelActualizar.setBorder(BorderFactory.createTitledBorder("Actualizar / Eliminar pedido"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(4, 4, 4, 4);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        int fila2 = 0;
        agregarCampo(panelActualizar, gbc2, fila2++, "ID pedido:", txtIdActualizar);
        agregarCampo(panelActualizar, gbc2, fila2++, "¿Pagado?:", chkPago);
        agregarCampo(panelActualizar, gbc2, fila2++, "Método de pago:", comboMetodoPagoActualizar);
        agregarCampo(panelActualizar, gbc2, fila2++, "Nueva dirección:", txtDireccionActualizar);

        JButton btnListar = new JButton("1. Listar pedidos");
        JButton btnActualizar = new JButton("2. Actualizar pedido");
        JButton btnEliminar = new JButton("3. Eliminar pedido");
        JPanel botonesActualizar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botonesActualizar.add(btnListar);
        botonesActualizar.add(btnActualizar);
        botonesActualizar.add(btnEliminar);

        gbc2.gridx = 0;
        gbc2.gridy = fila2;
        gbc2.gridwidth = 2;
        panelActualizar.add(botonesActualizar, gbc2);

        JPanel norte = new JPanel(new GridLayout(1, 2, 10, 0));
        norte.add(panelCrear);
        norte.add(panelActualizar);
        add(norte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Estado", "Pago", "Método", "Entrega", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Acciones -----

        btnCrear.addActionListener(e -> {
            Cliente cliente = (Cliente) comboCliente.getSelectedItem();
            if (cliente == null) {
                JOptionPane.showMessageDialog(this,
                        "Primero registre un cliente en la pestaña USUARIO.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer idPedido;
            try {
                idPedido = Integer.parseInt(txtIdPedido.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un ID de pedido numérico válido.",
                        "Entrada inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MetodoPago metodoPago = (MetodoPago) comboMetodoPagoNuevo.getSelectedItem();
            String direccion = txtDireccionNueva.getText().isBlank()
                    ? cliente.getDireccion() : txtDireccionNueva.getText();

            // Exactamente igual que en Main: el pedido nace del cliente...
            Pedido pedido = cliente.realizarPedido(idPedido, metodoPago, direccion);

            // ...y luego se registra en PedidoController para poder listarlo/editarlo aquí.
            try {
                PedidoController.agregarPedido(pedido);
                JOptionPane.showMessageDialog(this, "Pedido creado para el cliente: " + cliente.getNombre());
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Aviso: " + ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            refrescarTabla();
        });

        btnPagarWompi.addActionListener(e -> {
            // Igual que Main.probarPagoWompi(), pero sobre el pedido seleccionado en la tabla
            int fila3 = tabla.getSelectedRow();
            if (fila3 < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla primero.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idPedido = (int) modeloTabla.getValueAt(fila3, 0);

            ProcesadorPago procesador = new AdaptadorPagoWompi();
            String linkDePago = procesador.procesarPago(50000, "PED-" + idPedido);

            if (linkDePago != null) {
                JOptionPane.showMessageDialog(this, "Envíale este link al cliente para pagar:\n" + linkDePago);
            } else {
                JOptionPane.showMessageDialog(this, "Hubo un error generando el pago.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnListar.addActionListener(e -> refrescarTabla());

        btnActualizar.addActionListener(e -> {
            Integer id = leerEntero(txtIdActualizar);
            if (id == null) return;

            Pedido existente = PedidoController.buscarPedido(id);
            if (existente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un pedido con ese ID.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MetodoPago metodoPago = (MetodoPago) comboMetodoPagoActualizar.getSelectedItem();
            Pedido datosNuevos = new Pedido(id, metodoPago, txtDireccionActualizar.getText());
            datosNuevos.setPago(chkPago.isSelected());

            PedidoController.actualizarPedido(datosNuevos);
            JOptionPane.showMessageDialog(this, "Pedido actualizado.");
            refrescarTabla();
        });

        btnEliminar.addActionListener(e -> {
            Integer id = leerEntero(txtIdActualizar);
            if (id == null) return;
            PedidoController.eliminarPedido(id);
            refrescarTabla();
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila3 = tabla.getSelectedRow();
            if (fila3 < 0) return;
            int id = (int) modeloTabla.getValueAt(fila3, 0);
            Pedido p = PedidoController.buscarPedido(id);
            if (p == null) return;

            txtIdActualizar.setText(String.valueOf(p.getIdPedido()));
            chkPago.setSelected(p.isPago());
            comboMetodoPagoActualizar.setSelectedItem(p.getMetodoPago());
            txtDireccionActualizar.setText(p.getDireccionEntrega());
        });

        refrescarClientes();
        refrescarTabla();
    }

    /** Debe llamarse cuando se registre un cliente nuevo en la pestaña Usuario. */
    public void refrescarClientes() {
        Object seleccionado = comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        List<Cliente> clientes = usuarioController.listarClientes();
        for (Cliente c : clientes) {
            comboCliente.addItem(c);
        }
        if (seleccionado != null) comboCliente.setSelectedItem(seleccionado);
    }

    /** Igual que el "opcion == 1" (Listar pedidos) de menuPedido(...). */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Pedido> pedidos = PedidoController.listarPedidos();
        for (Pedido p : pedidos) {
            modeloTabla.addRow(new Object[]{
                    p.getIdPedido(), p.getEstado(), p.isPago(), p.getMetodoPago(),
                    p.getDireccionEntrega(), p.getFecha().format(FORMATO_FECHA)
            });
        }
    }

    private Integer leerEntero(JTextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numérico válido.",
                    "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }
}
