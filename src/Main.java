import controller.*;

import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;

import model.*;
import repository.*;
import view.*;

public class    Main extends JFrame {
    public Main() {
        super("El Lechon · Tamales y Lechona | Centro de operaciones");
        UI.init();
        getContentPane().setBackground(UI.BG);
        Distribuidora d = Distribuidora.getInstancia();
        var productoRepo = new ProductoRepositoryMemoria();
        var usuarioRepo = new UsuarioRepositoryMemoria();
        var pedidoRepo = new PedidoRepositoryMemoria();
        var proveedorRepo = new ProveedorRepositoryMemoria();
        var movimientoRepo = new MovimientoInventarioRepositoryMemoria();
        ProductoController pc = new ProductoController(productoRepo);
        ProveedorController prov = new ProveedorController(proveedorRepo);
        UsuarioController uc = new UsuarioController(usuarioRepo);
        MovimientoInventarioController inv = new MovimientoInventarioController(movimientoRepo);
        PedidoController pedidos = new PedidoController(pedidoRepo, inv);
        cargarCatalogo(pc);
        ProveedorPanel vProv = new ProveedorPanel(prov);
        MovimientoInventarioPanel vInv = new MovimientoInventarioPanel(inv, prov, pc);
        ProductoPanel vProd = new ProductoPanel(pc);
        UsuarioPanel vUsr = new UsuarioPanel(uc);
        PedidoPanel vPed = new PedidoPanel(uc, pc, pedidos);
        ResumenPanel dash = new ResumenPanel(pc, pedidos, prov);
        MainPanel root = new MainPanel(dash, vProv, vInv, vProd, vUsr, vPed);
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
    }

    private void cargarCatalogo(ProductoController pc) {
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal normal grande",
                        "Tamal tradicional tamaño grande",
                        new BigDecimal("8000"),
                        0,
                        true,
                        TipoTamal.NORMAL,
                        TamanoTamal.GRANDE));
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal normal mediano",
                        "Tamal tradicional tamaño mediano",
                        new BigDecimal("6500"),
                        0,
                        true,
                        TipoTamal.NORMAL,
                        TamanoTamal.MEDIANO));
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal normal pequeño",
                        "Tamal tradicional tamaño pequeño",
                        new BigDecimal("5000"),
                        0,
                        true,
                        TipoTamal.NORMAL,
                        TamanoTamal.PEQUEÑO));
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal picante grande",
                        "Tamal picante tamaño grande",
                        new BigDecimal("9000"),
                        0,
                        true,
                        TipoTamal.PICANTE,
                        TamanoTamal.GRANDE));
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal picante mediano",
                        "Tamal picante tamaño mediano",
                        new BigDecimal("7500"),
                        0,
                        true,
                        TipoTamal.PICANTE,
                        TamanoTamal.MEDIANO));
        pc.registrar(
                new Tamal(
                        0,
                        "Tamal picante pequeño",
                        "Tamal picante tamaño pequeño",
                        new BigDecimal("6000"),
                        0,
                        true,
                        TipoTamal.PICANTE,
                        TamanoTamal.PEQUEÑO));
        pc.registrar(
                new Lechona(
                        0,
                        "Lechona grande",
                        "Lechona tradicional tolimense",
                        new BigDecimal("150000"),
                        0,
                        true,
                        TamanoLechona.GRANDE,
                        20));
        pc.registrar(
                new Lechona(
                        0,
                        "Lechona mediana",
                        "Lechona tradicional tamaño mediano",
                        new BigDecimal("100000"),
                        0,
                        true,
                        TamanoLechona.MEDIANA,
                        12));
        pc.registrar(
                new Lechona(
                        0,
                        "Lechona pequeña",
                        "Lechona ideal para reuniones pequeñas",
                        new BigDecimal("60000"),
                        0,
                        true,
                        TamanoLechona.PEQUEÑA,
                        8));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
