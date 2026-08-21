package view;

import controller.ProductoController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class GUIProducto extends JFrame {

    private JTextField txtId, txtNombre, txtDescripcion, txtPrecio, txtStock;
    private JComboBox<String> cbTipoProducto;
    private JComboBox<TipoTamal> cbTipoTamal;
    private JComboBox<TamañoTamal> cbTamañoTamal;
    private JComboBox<TamañoLechona> cbTamañoLechona;
    private JTextField txtNumeroPorciones;
    private JPanel panelCamposEspecificos;
    private DefaultListModel<String> listModel;
    private JList<String> listaProductos;

    public GUIProducto() {
        setTitle("Gestión de Productos");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(0, 2, 5, 5));

        panelFormulario.add(new JLabel("ID:"));
        txtId = new JTextField();
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelFormulario.add(txtStock);

        panelFormulario.add(new JLabel("Tipo de producto:"));
        cbTipoProducto = new JComboBox<>(new String[]{"Tamal", "Lechona"});
        cbTipoProducto.addActionListener(e -> actualizarCamposEspecificos());
        panelFormulario.add(cbTipoProducto);

        panelCamposEspecificos = new JPanel(new GridLayout(0, 2, 5, 5));
        actualizarCamposEspecificos();

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.addActionListener(e -> agregarProducto());

        JButton btnEliminar = new JButton("Eliminar por ID");
        btnEliminar.addActionListener(e -> eliminarProducto());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        listModel = new DefaultListModel<>();
        listaProductos = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(listaProductos);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.NORTH);
        panelSuperior.add(panelCamposEspecificos, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void actualizarCamposEspecificos() {
        panelCamposEspecificos.removeAll();
        String tipo = (String) cbTipoProducto.getSelectedItem();

        if ("Tamal".equals(tipo)) {
            panelCamposEspecificos.add(new JLabel("Tipo de Tamal:"));
            cbTipoTamal = new JComboBox<>(TipoTamal.values());
            panelCamposEspecificos.add(cbTipoTamal);

            panelCamposEspecificos.add(new JLabel("Tamaño:"));
            cbTamañoTamal = new JComboBox<>(TamañoTamal.values());
            panelCamposEspecificos.add(cbTamañoTamal);
        } else {
            panelCamposEspecificos.add(new JLabel("Tamaño Lechona:"));
            cbTamañoLechona = new JComboBox<>(TamañoLechona.values());
            panelCamposEspecificos.add(cbTamañoLechona);

            panelCamposEspecificos.add(new JLabel("Número de porciones:"));
            txtNumeroPorciones = new JTextField();
            panelCamposEspecificos.add(txtNumeroPorciones);
        }

        panelCamposEspecificos.revalidate();
        panelCamposEspecificos.repaint();
    }

    private void agregarProducto() {
        try {
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText();
            String descripcion = txtDescripcion.getText();
            BigDecimal precio = new BigDecimal(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            String tipo = (String) cbTipoProducto.getSelectedItem();

            Producto producto;

            if ("Tamal".equals(tipo)) {
                TipoTamal tipoTamal = (TipoTamal) cbTipoTamal.getSelectedItem();
                TamañoTamal tamañoTamal = (TamañoTamal) cbTamañoTamal.getSelectedItem();
                producto = new Tamal(id, nombre, descripcion, precio, stock, true, tipoTamal, tamañoTamal);
            } else {
                TamañoLechona tamañoLechona = (TamañoLechona) cbTamañoLechona.getSelectedItem();
                int porciones = Integer.parseInt(txtNumeroPorciones.getText());
                producto = new Lechona(id, nombre, descripcion, precio, stock, true, tamañoLechona, porciones);
            }

            ProductoController.agregarProducto(producto);
            listModel.addElement(producto.toString());
            limpiarCampos();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Revisa que ID, Precio, Stock y Porciones sean numéricos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID del producto a eliminar:"));
            ProductoController.eliminarProducto(id);
            refrescarLista();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
        }
    }

    private void refrescarLista() {
        listModel.clear();
        ProductoController controller = new ProductoController();
        for (Producto p : controller.listarProductos()) {
            listModel.addElement(p.toString());
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }
}