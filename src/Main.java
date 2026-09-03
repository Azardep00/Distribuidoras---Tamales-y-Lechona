import controller.*;
import model.*;
import repository.*;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class Main extends JFrame {

    public Main() {
        super("Distribuidora de Tamales y Lechona");
        Distribuidora d = Distribuidora.getInstancia();

        ProveedorController pc = new ProveedorController(new ProveedorRepositoryMemoria());
        MovimientoInventarioController mc = new MovimientoInventarioController(new MovimientoInventarioRepositoryMemoria());
        UsuarioController uc = new UsuarioController();
        sembrarProductos();

        ProveedorPanel pp = new ProveedorPanel(pc);
        MovimientoInventarioPanel mp = new MovimientoInventarioPanel(mc, pc);
        ProductoPanel prod = new ProductoPanel();
        UsuarioPanel up = new UsuarioPanel(uc);
        PedidoPanel ped = new PedidoPanel(uc);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Proveedores", pp);
        tabs.addTab("Inventario", mp);
        tabs.addTab("Productos", prod);
        tabs.addTab("Usuarios", up);
        tabs.addTab("Pedidos", ped);

        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c == pp) {
                pp.refrescarTabla();
            } else if (c == mp) {
                mp.refrescarCombos();
                mp.refrescarStock();
                mp.refrescarMovimientos();
            } else if (c == prod) {
                prod.refrescarTabla();
            } else if (c == up) {
                up.refrescarTabla();
            } else if (c == ped) {
                ped.refrescarClientes();
                ped.refrescarProductos();
                ped.refrescarTabla();
            }
        });

        JLabel head = new JLabel("  " + d.getNombre() + "  ·  " + d.getDireccion());
        head.setFont(head.getFont().deriveFont(Font.BOLD, 15f));
        head.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));

        setLayout(new BorderLayout());
        add(head, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(980, 650));
        setLocationRelativeTo(null);
    }

    private void sembrarProductos() {
        ProductoController.agregarProducto(new Tamal(1, "Tamal normal grande", "Tamal tradicional tamaño grande",
                new BigDecimal("8000"), 20, true, TipoTamal.NORMAL, TamanoTamal.GRANDE));
        ProductoController.agregarProducto(new Tamal(2, "Tamal normal mediano", "Tamal tradicional tamaño mediano",
                new BigDecimal("6500"), 25, true, TipoTamal.NORMAL, TamanoTamal.MEDIANO));
        ProductoController.agregarProducto(new Tamal(3, "Tamal normal pequeño", "Tamal tradicional tamaño pequeño",
                new BigDecimal("5000"), 30, true, TipoTamal.NORMAL, TamanoTamal.PEQUEÑO));
        ProductoController.agregarProducto(new Tamal(4, "Tamal picante grande", "Tamal picante tamaño grande",
                new BigDecimal("9000"), 15, true, TipoTamal.PICANTE, TamanoTamal.GRANDE));
        ProductoController.agregarProducto(new Tamal(5, "Tamal picante mediano", "Tamal picante tamaño mediano",
                new BigDecimal("7500"), 15, true, TipoTamal.PICANTE, TamanoTamal.MEDIANO));
        ProductoController.agregarProducto(new Tamal(6, "Tamal picante pequeño", "Tamal picante tamaño pequeño",
                new BigDecimal("6000"), 20, true, TipoTamal.PICANTE, TamanoTamal.PEQUEÑO));
        ProductoController.agregarProducto(new Lechona(7, "Lechona grande", "Lechona tradicional tolimense",
                new BigDecimal("150000"), 5, true, TamanoLechona.GRANDE, 20));
        ProductoController.agregarProducto(new Lechona(8, "Lechona mediana", "Lechona tradicional tamaño mediano",
                new BigDecimal("100000"), 8, true, TamanoLechona.MEDIANA, 12));
        ProductoController.agregarProducto(new Lechona(9, "Lechona pequeña", "Lechona ideal para reuniones pequeñas",
                new BigDecimal("60000"), 8, true, TamanoLechona.PEQUEÑA, 8));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new Main().setVisible(true);
        });
    }
}