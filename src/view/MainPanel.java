package view;

import java.awt.*;
import javax.swing.*;

/** Ventana principal: logo arriba y navegación simple, centrada y adaptable. */
public class MainPanel extends JPanel {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JButton[] navButtons = new JButton[6];

    public MainPanel(
            ResumenPanel dashboard,
            ProveedorPanel proveedores,
            MovimientoInventarioPanel inventario,
            ProductoPanel productos,
            UsuarioPanel usuarios,
            PedidoPanel pedidos) {
        setLayout(new BorderLayout(0, 10));
        setBackground(UI.BG);

        add(buildTopBar(), BorderLayout.NORTH);

        content.setOpaque(true);
        content.setBackground(UI.BG);
        content.add(dashboard, "inicio");
        content.add(pedidos, "pedidos");
        content.add(inventario, "inventario");
        content.add(productos, "productos");
        content.add(proveedores, "proveedores");
        content.add(usuarios, "usuarios");
        add(content, BorderLayout.CENTER);

        dashboard.setNavigation(this::show);
        show("inicio");
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UI.LINE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel logo = new JLabel();
        ImageIcon icon = UI.logoIcon(58, 58);
        if (icon != null) {
            logo.setIcon(icon);
        }
        logo.setPreferredSize(new Dimension(62, 58));
        bar.add(logo, BorderLayout.WEST);

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        menu.setOpaque(false);
        navButtons[0] = nav("Inicio", "inicio");
        navButtons[1] = nav("Pedidos", "pedidos");
        navButtons[2] = nav("Inventario", "inventario");
        navButtons[3] = nav("Productos", "productos");
        navButtons[4] = nav("Proveedores", "proveedores");
        navButtons[5] = nav("Usuarios", "usuarios");
        for (JButton b : navButtons) menu.add(b);
        bar.add(menu, BorderLayout.CENTER);

        return bar;
    }

    private JButton nav(String text, String key) {
        JButton b = UI.topButton(text);
        b.addActionListener(e -> show(key));
        return b;
    }

    private void show(String key) {
        cards.show(content, key);
        int index = switch (key) {
            case "inicio" -> 0;
            case "pedidos" -> 1;
            case "inventario" -> 2;
            case "productos" -> 3;
            case "proveedores" -> 4;
            case "usuarios" -> 5;
            default -> 0;
        };
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setFont(new Font("SansSerif", i == index ? Font.BOLD : Font.PLAIN, 14));
        }
        refresh(key);
    }

    private void refresh(String key) {
        Component selected = switch (key) {
            case "inicio" -> content.getComponent(0);
            case "pedidos" -> content.getComponent(1);
            case "inventario" -> content.getComponent(2);
            case "productos" -> content.getComponent(3);
            case "proveedores" -> content.getComponent(4);
            case "usuarios" -> content.getComponent(5);
            default -> content.getComponent(0);
        };
        if (selected instanceof ResumenPanel x) x.refrescar();
        if (selected instanceof MovimientoInventarioPanel x) {
            x.refrescarCombos();
            x.refrescarTablas();
        }
        if (selected instanceof ProductoPanel x) x.refrescarTabla();
        if (selected instanceof ProveedorPanel x) x.refrescarTabla();
        if (selected instanceof UsuarioPanel x) x.refrescarTabla();
        if (selected instanceof PedidoPanel x) {
            x.refrescarClientes();
            x.refrescarProductos();
            x.refrescarTabla();
        }
    }
}
