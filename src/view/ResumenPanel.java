package view;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

/** Inicio: resumen operativo y accesos directos grandes. */
public class ResumenPanel extends JPanel {
    private final JLabel productos = UI.stat("0", "Productos activos");
    private final JLabel stockBajo = UI.stat("0", "Alertas de stock");
    private final JLabel pedidos = UI.stat("0", "Pedidos pendientes");
    private final JLabel proveedores = UI.stat("0", "Proveedores activos");
    private final controller.ProductoController pc;
    private final controller.PedidoController ped;
    private final controller.ProveedorController prov;
    private Consumer<String> navigation = key -> {};

    public ResumenPanel(controller.ProductoController pc, controller.PedidoController ped,
                        controller.ProveedorController prov) {
        this.pc = pc;
        this.ped = ped;
        this.prov = prov;
        setLayout(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 22, 24));
        setBackground(UI.BG);
        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(buildStats(), BorderLayout.NORTH);
        center.add(buildQuickAccess(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        refrescar();
    }

    public void setNavigation(Consumer<String> navigation) { this.navigation = navigation; }

    private JPanel buildHeader() {
        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);

        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = UI.logoIcon(150, 150);
        if (icon != null) {
            logo.setIcon(icon);
        }
        head.add(logo);
        head.add(Box.createVerticalStrut(6));

        JLabel title = UI.title("Bienvenido a El Lechon");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        head.add(title);
        head.add(Box.createVerticalStrut(4));
        JLabel sub = UI.subtitle("Tamales y Lechona · Centro de operaciones");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        head.add(sub);
        return head;
    }

    private JPanel buildStats() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 14));
        cards.setOpaque(false);
        cards.add(statCard(productos));
        cards.add(statCard(stockBajo));
        cards.add(statCard(pedidos));
        cards.add(statCard(proveedores));
        return cards;
    }

    private JPanel statCard(JComponent content) {
        JPanel card = UI.card(null);
        card.setPreferredSize(new Dimension(160, 88));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickAccess() {
        JPanel block = new JPanel(new BorderLayout(0, 10));
        block.setOpaque(false);
        JLabel heading = new JLabel("Accesos rápidos");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(UI.INK);
        block.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(menu("Pedidos", "Crear y gestionar ventas", "pedidos"));
        grid.add(menu("Inventario", "Entradas, salidas y stock", "inventario"));
        grid.add(menu("Productos", "Catálogo y precios", "productos"));
        grid.add(menu("Proveedores", "Contactos y compras", "proveedores"));
        grid.add(menu("Usuarios", "Clientes y empleados", "usuarios"));
        grid.add(new JPanel());
        ((JPanel) grid.getComponent(5)).setOpaque(false);
        block.add(grid, BorderLayout.CENTER);
        return block;
    }

    private JButton menu(String title, String caption, String key) {
        JButton b = new JButton("<html><div style='text-align:center'><b style='font-size:16px'>"
                + title + "</b><br><span style='font-size:11px;color:#69696E'>"
                + caption + "</span></div></html>");
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(Color.WHITE);
        b.setForeground(UI.INK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UI.LINE),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> navigation.accept(key));
        return b;
    }

    public void refrescar() {
        productos.setText(stat(pc.listar(false).size(), "Productos activos"));
        long low = pc.listar(true).stream().filter(p -> p.isEstado() && p.getStock() <= 5).count();
        stockBajo.setText(stat(low, "Alertas de stock"));
        pedidos.setText(stat(ped.pendientes(), "Pedidos pendientes"));
        proveedores.setText(stat(prov.listarActivos().size(), "Proveedores activos"));
    }

    private String stat(Object v, String l) {
        return "<html><div style='text-align:center'><span style='font-size:27px;font-weight:bold;color:#1C1C1E'>"
                + v + "</span><br><span style='font-size:12px;color:#69696E'>" + l + "</span></div></html>";
    }
}
