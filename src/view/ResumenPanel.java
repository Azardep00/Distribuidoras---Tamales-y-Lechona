package view;

import controller.*;
import java.awt.*;
import javax.swing.*;

public class ResumenPanel extends JPanel {
  private final JLabel productos = UI.stat("0", "Productos activos"),
      stockBajo = UI.stat("0", "Stock bajo"),
      pedidos = UI.stat("0", "Pedidos pendientes"),
      proveedores = UI.stat("0", "Proveedores activos");
  private final ProductoController pc;
  private final PedidoController ped;
  private final ProveedorController prov;

  public ResumenPanel(ProductoController pc, PedidoController ped, ProveedorController prov) {
    this.pc = pc;
    this.ped = ped;
    this.prov = prov;
    setLayout(new BorderLayout(16, 16));
    setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    setBackground(UI.BG);
    JLabel title = new JLabel("Centro de operaciones");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
    JLabel sub = new JLabel("Visión general del negocio y sus operaciones.");
    sub.setForeground(UI.MUTED);
    JPanel head = new JPanel(new BorderLayout());
    head.setOpaque(false);
    head.add(title, BorderLayout.NORTH);
    head.add(sub, BorderLayout.SOUTH);
    add(head, BorderLayout.NORTH);
    JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
    cards.setOpaque(false);
    cards.add(wrap(productos));
    cards.add(wrap(stockBajo));
    cards.add(wrap(pedidos));
    cards.add(wrap(proveedores));
    add(cards, BorderLayout.CENTER);
    JLabel hint =
        new JLabel(
            "Los IDs son internos y se generan automáticamente. Usa las tablas para buscar, editar y operar.");
    hint.setForeground(UI.MUTED);
    add(hint, BorderLayout.SOUTH);
    refrescar();
  }

  private JPanel wrap(JComponent c) {
    JPanel p = UI.card(null);
    p.add(c, BorderLayout.CENTER);
    return p;
  }

  public void refrescar() {
    productos.setText(stat(pc.listar(false).size(), "Productos activos"));
    long low = pc.listar(true).stream().filter(p -> p.isEstado() && p.getStock() <= 5).count();
    stockBajo.setText(stat(low, "Stock bajo"));
    pedidos.setText(stat(ped.pendientes(), "Pedidos pendientes"));
    proveedores.setText(stat(prov.listarActivos().size(), "Proveedores activos"));
  }

  private String stat(Object v, String l) {
    return "<html><div style='text-align:center'><span style='font-size:24px;font-weight:bold'>"
        + v
        + "</span><br><span style='color:#666'>"
        + l
        + "</span></div></html>";
  }
}
