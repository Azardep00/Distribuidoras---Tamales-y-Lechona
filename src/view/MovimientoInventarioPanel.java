package view;

import controller.MovimientoInventarioController;
import controller.ProductoController;
import controller.ProveedorController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Entradas/salidas con selección explícita. No existe proveedor preseleccionado. */
public class MovimientoInventarioPanel extends JPanel {
    private static final DateTimeFormatter F=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final MovimientoInventarioController movimientoController; private final ProveedorController proveedorController;
    private final JComboBox<Producto> comboProducto=new JComboBox<>(); private final JComboBox<Proveedor> comboProveedor=new JComboBox<>();
    private final JSpinner spinnerCantidad=new JSpinner(new SpinnerNumberModel(1,1,100000,1)); private final JTextField txtMotivo=new JTextField(22);
    private final DefaultTableModel stockModel=new DefaultTableModel(new Object[]{"ID","Producto","Stock"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final DefaultTableModel movModel=new DefaultTableModel(new Object[]{"ID","Tipo","Producto","Cantidad","Fecha","Motivo","Proveedor"},0){public boolean isCellEditable(int r,int c){return false;}};

    public MovimientoInventarioPanel(MovimientoInventarioController mc, ProveedorController pc){
        movimientoController=mc; proveedorController=pc; setLayout(new BorderLayout(12,12)); setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        comboProducto.setRenderer(rendererProducto()); comboProveedor.setRenderer(rendererProveedor());
        comboProducto.setToolTipText("Selecciona el producto al que afecta el movimiento"); comboProveedor.setToolTipText("Solo se usa en entradas");
        JPanel form=new JPanel(new GridBagLayout()); form.setBorder(BorderFactory.createTitledBorder("Registrar movimiento")); GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(5,5,5,5); g.fill=GridBagConstraints.HORIZONTAL;
        add(form,g,0,"Producto:",comboProducto); add(form,g,1,"Cantidad:",spinnerCantidad); add(form,g,2,"Motivo:",txtMotivo); add(form,g,3,"Proveedor (solo entrada):",comboProveedor);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT)); JButton entrada=new JButton("Registrar entrada"); JButton salida=new JButton("Registrar salida"); JButton verProv=new JButton("Consultar proveedores"); actions.add(entrada);actions.add(salida);actions.add(verProv);
        JPanel north=new JPanel(new BorderLayout()); north.add(form,BorderLayout.CENTER);north.add(actions,BorderLayout.SOUTH); add(north,BorderLayout.NORTH);
        JTabbedPane tabs=new JTabbedPane(); JTable st=new JTable(stockModel); JTable mv=new JTable(movModel); st.setRowHeight(24);mv.setRowHeight(24); tabs.addTab("Stock actual",new JScrollPane(st)); tabs.addTab("Historial de movimientos",new JScrollPane(mv)); add(tabs,BorderLayout.CENTER);
        entrada.addActionListener(e->registrar(TipoMovimiento.ENTRADA,tabs)); salida.addActionListener(e->registrar(TipoMovimiento.SALIDA,tabs)); verProv.addActionListener(e->new ProveedorPanel(proveedorController).mostrarConsulta());
        comboProducto.addActionListener(e->actualizarProveedorHabilitado()); refrescarCombos(); refrescarStock(); refrescarMovimientos();
    }
    private DefaultListCellRenderer rendererProducto(){return new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){super.getListCellRendererComponent(l,v,i,s,f); if(v instanceof Producto p)setText(p.getNombre()+" · stock "+p.getStock()); return this;}};}
    private DefaultListCellRenderer rendererProveedor(){return new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){super.getListCellRendererComponent(l,v,i,s,f); if(v instanceof Proveedor p)setText(p.getNombre()+" · #"+p.getIdProveedor()); return this;}};}
    private void add(JPanel p,GridBagConstraints g,int y,String label,JComponent c){g.gridy=y;g.gridx=0;g.weightx=0;p.add(new JLabel(label),g);g.gridx=1;g.weightx=1;p.add(c,g);}
    private void registrar(TipoMovimiento tipo,JTabbedPane tabs){
        Producto producto=(Producto)comboProducto.getSelectedItem(); if(producto==null){aviso("No hay productos disponibles.");return;}
        int cant=(Integer)spinnerCantidad.getValue(); if(cant<=0)return;
        if(tipo==TipoMovimiento.SALIDA && !producto.consultarDisponibilidad(cant)){aviso("No hay stock suficiente para realizar la salida.");return;}
        Proveedor prov=null; if(tipo==TipoMovimiento.ENTRADA){prov=(Proveedor)comboProveedor.getSelectedItem(); if(prov==null){aviso("Selecciona un proveedor para registrar una entrada.");return;}}
        MovimientoInventario m=(prov==null?new MovimientoInventario(0,tipo,cant,txtMotivo.getText().trim(),producto):new MovimientoInventario(0,tipo,cant,txtMotivo.getText().trim(),producto,prov));
        if(!movimientoController.registrarMovimiento(m)){aviso("No se pudo registrar el movimiento.");return;}
        mensaje((tipo==TipoMovimiento.ENTRADA?"Entrada":"Salida")+" registrada. Stock actual: "+producto.getStock()); spinnerCantidad.setValue(1);txtMotivo.setText("");refrescarCombos();refrescarStock();refrescarMovimientos();tabs.setSelectedIndex(0);
    }
    private void actualizarProveedorHabilitado(){comboProveedor.setEnabled(comboProducto.getSelectedItem()!=null);}
    public void refrescarCombos(){
        Producto sel=(Producto)comboProducto.getSelectedItem(); comboProducto.removeAllItems(); for(Producto p:ProductoController.listarProductos())comboProducto.addItem(p); if(sel!=null)comboProducto.setSelectedItem(sel);
        // Nunca se selecciona automáticamente el primer proveedor.
        comboProveedor.removeAllItems(); comboProveedor.addItem(null); for(Proveedor p:proveedorController.listarProveedores())comboProveedor.addItem(p); comboProveedor.setSelectedIndex(0); actualizarProveedorHabilitado();
    }
    public void refrescarStock(){stockModel.setRowCount(0);for(Producto p:ProductoController.listarProductos())stockModel.addRow(new Object[]{p.getIdProducto(),p.getNombre(),p.getStock()});}
    public void refrescarMovimientos(){movModel.setRowCount(0);for(MovimientoInventario m:movimientoController.listarMovimientos())movModel.addRow(new Object[]{m.getIdMovimiento(),m.getTipo(),m.getProducto().getNombre(),m.getCantidad(),m.getFecha().format(F),m.getMotivo(),m.getProveedor()==null?"-":m.getProveedor().getNombre()});}
    private void aviso(String s){JOptionPane.showMessageDialog(this,s,"Inventario",JOptionPane.WARNING_MESSAGE);} private void mensaje(String s){JOptionPane.showMessageDialog(this,s,"Inventario",JOptionPane.INFORMATION_MESSAGE);}
}