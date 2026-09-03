package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public final class UI {
  public static final Color INK = new Color(35, 35, 38),
      MUTED = new Color(105, 105, 110),
      ACCENT = new Color(126, 82, 50),
      BG = new Color(248, 246, 242),
      CARD = Color.WHITE,
      OK = new Color(40, 130, 85),
      WARN = new Color(190, 120, 25),
      DANGER = new Color(180, 65, 55);

  private UI() {}

  public static void init() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
    }
    UIManager.put("Button.arc", 10);
    UIManager.put("Component.arc", 10);
    UIManager.put("TextComponent.arc", 10);
    UIManager.put("Table.rowHeight", 28);
  }

  public static JButton button(String text) {
    JButton b = new JButton(text);
    b.setFocusPainted(false);
    b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    return b;
  }

  public static JPanel card(String title) {
    JPanel p = new JPanel(new BorderLayout(10, 10));
    p.setBackground(CARD);
    p.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 222, 216)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
    if (title != null && !title.isBlank()) {
      JLabel l = new JLabel(title);
      l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
      p.add(l, BorderLayout.NORTH);
    }
    return p;
  }

  public static JLabel stat(String value, String label) {
    JLabel l =
        new JLabel(
            "<html><div style='text-align:center'><span style='font-size:24px;font-weight:bold'>"
                + value
                + "</span><br><span style='color:#666'>"
                + label
                + "</span></div></html>");
    l.setHorizontalAlignment(SwingConstants.CENTER);
    return l;
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
    c.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    return c;
  }
}
