package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Estilo único y neutro de la aplicación. La interfaz usa blanco, negro y
 * grises para priorizar legibilidad y operación por encima de decoración.
 */
public final class UI {
    public static final Color INK = new Color(28, 28, 30);
    public static final Color MUTED = new Color(105, 105, 110);
    public static final Color BG = new Color(247, 247, 247);
    public static final Color SURFACE = Color.WHITE;
    public static final Color CARD = Color.WHITE;
    public static final Color LINE = new Color(215, 215, 218);
    public static final Color OK = new Color(70, 70, 74);
    public static final Color WARN = new Color(90, 90, 94);
    public static final Color DANGER = new Color(45, 45, 48);

    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TITLE = new Font("Serif", Font.BOLD, 31);

    private UI() {}

    public static void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("control", SURFACE);
        UIManager.put("Panel.background", BG);
        UIManager.put("Label.foreground", INK);
        UIManager.put("Label.font", BODY);
        UIManager.put("Button.font", BODY_BOLD);
        UIManager.put("TextField.font", BODY);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("Spinner.font", BODY);
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("TableHeader.font", BODY_BOLD);
        UIManager.put("TabbedPane.font", BODY_BOLD);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE);
        label.setForeground(INK);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY);
        label.setForeground(MUTED);
        return label;
    }

    public static JButton button(String text) {
        return styledButton(text, 40, false);
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, 42, true);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, 40, false);
    }

    public static JButton compactButton(String text) {
        return styledButton(text, 38, false);
    }

    public static JButton topButton(String text) {
        JButton b = styledButton(text, 44, false);
        b.setPreferredSize(new Dimension(132, 44));
        b.setMinimumSize(new Dimension(110, 44));
        return b;
    }

    private static JButton styledButton(String text, int height, boolean strong) {
        JButton b = new FlatButton(text);
        b.setFont(new Font("SansSerif", strong ? Font.BOLD : Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setMinimumSize(new Dimension(96, height));
        b.setPreferredSize(new Dimension(Math.max(112, 40 + text.length() * 8), height));
        b.setBorder(new EmptyBorder(0, 16, 0, 16));
        return b;
    }

    public static JPanel card(String title) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(14, 14, 14, 14)));
        if (title != null && !title.isBlank()) {
            JLabel l = new JLabel(title);
            l.setFont(new Font("SansSerif", Font.BOLD, 16));
            l.setForeground(INK);
            p.add(l, BorderLayout.NORTH);
        }
        return p;
    }

    public static JLabel stat(String value, String label) {
        JLabel l = new JLabel(
                "<html><div style='text-align:center'><span style='font-size:27px;font-weight:bold;color:#1C1C1E'>"
                        + value
                        + "</span><br><span style='font-size:12px;color:#69696E'>"
                        + label
                        + "</span></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 232, 234));
        table.setSelectionForeground(INK);
        table.setRowHeight(35);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setBackground(new Color(238, 238, 240));
        header.setForeground(INK);
        header.setFont(BODY_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 250));
                setForeground(INK);
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static void styleField(JComponent c) {
        c.setFont(BODY);
        c.setOpaque(true);
        c.setBackground(Color.WHITE);
        c.setForeground(INK);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(7, 10, 7, 10)));
    }

    public static ImageIcon logoIcon(int width, int height) {
        String[] candidates = {
                "/assets/logo.png",
                "/main/assets/logo.png",
                "src/main/resources/assets/logo.png",
                "src/assets/logo.png"
        };
        for (String path : candidates) {
            java.net.URL resource = UI.class.getResource(path);
            if (resource == null && path.startsWith("/")) {
                resource = UI.class.getClassLoader().getResource(path.substring(1));
            }
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        }
        return null;
    }

    public static void info(Component c, String s) {
        JOptionPane.showMessageDialog(c, s, "Distribuidora", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component c, String s) {
        JOptionPane.showMessageDialog(c, s, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    public static <T extends JComponent> T border(T c) {
        c.setBorder(new EmptyBorder(4, 4, 4, 4));
        return c;
    }

    private static final class FlatButton extends JButton {
        FlatButton(String text) {
            super(text);
            setUI(new BasicButtonUI());
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Color fill = getModel().isRollover() ? new Color(236, 236, 238) : Color.WHITE;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 9, 9);
            g2.setColor(getModel().isPressed() ? new Color(180, 180, 184) : LINE);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 9, 9);
            setForeground(getModel().isEnabled() ? INK : new Color(160, 160, 165));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
