package view;

import controller.MovimientoInventarioController;
import controller.ProductoController;
import controller.ProveedorController;
import controller.UsuarioController;
import model.Distribuidora;
import model.Lechona;
import model.Tamal;
import model.TamañoLechona;
import model.TamañoTamal;
import model.TipoTamal;
import repository.MovimientoInventarioRepositoryMemoria;
import repository.ProveedorRepositoryMemoria;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Ventana principal de la interfaz gráfica.
 *
 * Es el equivalente visual de Main.java: crea exactamente los mismos
 * controllers, siembra los mismos productos de ejemplo (tamales y lechonas)
 * y organiza cada "menú" de la consola (PROVEEDOR, MOVIMIENTO INVENTARIO,
 * PRODUCTO, USUARIO, PEDIDO) como una pestaña con su propio CRUD visual.
 * No se reescribe ninguna regla de negocio: todas las pestañas llaman a los
 * mismos controllers/modelos que ya existían en el proyecto de consola.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Distribuidora de Tamales y Lechona");

        // Singleton, igual que en Main.java
        Distribuidora distribuidora = Distribuidora.getInstancia();
        setTitle(distribuidora.getNombre());

        // Mismos controllers, con las mismas implementaciones de repositorio (DIP)
        ProveedorController proveedorController = new ProveedorController(new ProveedorRepositoryMemoria());
        MovimientoInventarioController movimientoController =
                new MovimientoInventarioController(new MovimientoInventarioRepositoryMemoria());
        UsuarioController usuarioController = new UsuarioController();

        sembrarProductos();

        // ----- Pestañas: una por cada CRUD/menú del Main original -----
        ProveedorPanel proveedorPanel = new ProveedorPanel(proveedorController);
        MovimientoInventarioPanel movimientoPanel =
                new MovimientoInventarioPanel(movimientoController, proveedorController);
        ProductoPanel productoPanel = new ProductoPanel();
        UsuarioPanel usuarioPanel = new UsuarioPanel(usuarioController);
        PedidoPanel pedidoPanel = new PedidoPanel(usuarioController);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("1. Proveedor", proveedorPanel);
        pestañas.addTab("2. Movimiento Inventario", movimientoPanel);
        pestañas.addTab("3. Producto", productoPanel);
        pestañas.addTab("4. Usuario", usuarioPanel);
        pestañas.addTab("5. Pedido", pedidoPanel);

        // Al cambiar de pestaña se refresca la vista con los datos más recientes,
        // igual que cuando en consola se volvía a mostrar un menú.
        pestañas.addChangeListener(e -> {
            Component seleccionado = pestañas.getSelectedComponent();
            if (seleccionado == proveedorPanel) {
                proveedorPanel.refrescarTabla();
            } else if (seleccionado == movimientoPanel) {
                movimientoPanel.refrescarStock();
                movimientoPanel.refrescarMovimientos();
            } else if (seleccionado == productoPanel) {
                productoPanel.refrescarTabla();
            } else if (seleccionado == usuarioPanel) {
                usuarioPanel.refrescarTabla();
            } else if (seleccionado == pedidoPanel) {
                pedidoPanel.refrescarClientes();
                pedidoPanel.refrescarTabla();
            }
        });

        JLabel encabezado = new JLabel(
                "  Bienvenido a " + distribuidora.getNombre() + " — " + distribuidora.getDireccion(), SwingConstants.LEFT);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 14f));
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));

        setLayout(new BorderLayout());
        add(encabezado, BorderLayout.NORTH);
        add(pestañas, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    /** Exactamente los mismos productos que Main.java registraba al arrancar. */
    private void sembrarProductos() {
        ProductoController.agregarProducto(new Tamal(
                1, "Tamal Normal Grande", "Tamal tradicional tamaño grande",
                new BigDecimal("8000"), 20, true,
                TipoTamal.NORMAL, TamañoTamal.GRANDE
        ));
        ProductoController.agregarProducto(new Tamal(
                2, "Tamal Normal Mediano", "Tamal tradicional tamaño mediano",
                new BigDecimal("6500"), 25, true,
                TipoTamal.NORMAL, TamañoTamal.MEDIANO
        ));
        ProductoController.agregarProducto(new Tamal(
                3, "Tamal Normal Pequeño", "Tamal tradicional tamaño pequeño",
                new BigDecimal("5000"), 30, true,
                TipoTamal.NORMAL, TamañoTamal.PEQUEÑO
        ));
        ProductoController.agregarProducto(new Tamal(
                4, "Tamal Picante Grande", "Tamal picante tamaño grande",
                new BigDecimal("9000"), 15, true,
                TipoTamal.PICANTE, TamañoTamal.GRANDE
        ));
        ProductoController.agregarProducto(new Tamal(
                5, "Tamal Picante Mediano", "Tamal picante tamaño mediano",
                new BigDecimal("7500"), 15, true,
                TipoTamal.PICANTE, TamañoTamal.MEDIANO
        ));
        ProductoController.agregarProducto(new Tamal(
                6, "Tamal Picante Pequeño", "Tamal picante tamaño pequeño",
                new BigDecimal("6000"), 20, true,
                TipoTamal.PICANTE, TamañoTamal.PEQUEÑO
        ));
        ProductoController.agregarProducto(new Lechona(
                7, "Lechona Grande", "Lechona tradicional tolimense",
                new BigDecimal("150000"), 5, true,
                TamañoLechona.GRANDE, 20
        ));
        ProductoController.agregarProducto(new Lechona(
                8, "Lechona Mediana", "Lechona tradicional tamaño mediano",
                new BigDecimal("100000"), 8, true,
                TamañoLechona.MEDIANA, 12
        ));
        ProductoController.agregarProducto(new Lechona(
                9, "Lechona Pequeña", "Lechona ideal para reuniones pequeñas",
                new BigDecimal("60000"), 8, true,
                TamañoLechona.PEQUEÑA, 8
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si el look and feel del sistema falla, se usa el por defecto de Swing.
            }
            new MainFrame().setVisible(true);
        });
    }
}
