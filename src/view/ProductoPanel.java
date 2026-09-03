package view;

import controller.ProductoController;
import model.*;

import javax.swing.*; import javax.swing.table.DefaultTableModel; import java.awt.*; import java.math.BigDecimal;

/** Alta de producto sin ID manual. El ID se genera internamente y sirve para identificar el registro. */
public class ProductoPanel extends JPanel {
    private final JTextField nombre=new JTextField(20), descripcion=new JTextField(25), precio=new JTextField(10);
    private final JSpinner stock=new JSpinner(new SpinnerNumberModel(0,0,100000,1)), porciones=new JSpinner(new SpinnerNumberModel(1,1,10000,1));
    private final JComboBox<String> tipoProducto=new JComboBox<>(new String[]{"Tamal","Lechona"});
    private final JComboBox<TipoTamal> tipoTamal=new JComboBox<>(TipoTamal.values()); private final JComboBox<TamanoTamal> tamañoTamal=new JComboBox<>(TamanoTamal.values()); private final JComboBox<TamanoLechona> tamañoLechona=new JComboBox<>(TamanoLechona.values());
    private final CardLayout cards=new CardLayout(); private final JPanel especifico=new JPanel(cards); private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Producto","Categoría","Presentación","Precio","Stock"},0){public boolean isCellEditable(int r,int c){return false;}}; private final JTable tabla=new JTable(modelo);
    private Integer idSeleccionado;
    public ProductoPanel(){
        setLayout(new BorderLayout(12,12));setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JPanel form=new JPanel(new GridBagLayout());form.setBorder(BorderFactory.createTitledBorder("Registrar producto"));GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(5,5,5,5);g.fill=GridBagConstraints.HORIZONTAL;
        add(form,g,0,"Tipo:",tipoProducto);add(form,g,1,"Nombre comercial:*",nombre);add(form,g,2,"Descripción:",descripcion);add(form,g,3,"Precio de venta:*",precio);add(form,g,4,"Stock inicial:",stock);
        JPanel tamal=new JPanel(new GridLayout(2,2,5,5));tamal.add(new JLabel("Tipo de tamal:"));tamal.add(tipoTamal);tamal.add(new JLabel("Tamaño:"));tamal.add(tamañoTamal);
        JPanel lechona=new JPanel(new GridLayout(2,2,5,5));lechona.add(new JLabel("Tamaño:"));lechona.add(tamañoLechona);lechona.add(new JLabel("Porciones:"));lechona.add(porciones);
        especifico.add(tamal,"Tamal");especifico.add(lechona,"Lechona");g.gridy=5;g.gridx=0;g.gridwidth=2;form.add(especifico,g);tipoProducto.addActionListener(e->cards.show(especifico,(String)tipoProducto.getSelectedItem()));cards.show(especifico,"Tamal");
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));JButton registrar=new JButton("Registrar producto");JButton guardar=new JButton("Guardar cambios");JButton eliminar=new JButton("Eliminar");JButton limpiar=new JButton("Nuevo / limpiar");actions.add(registrar);actions.add(guardar);actions.add(eliminar);actions.add(limpiar);
        JPanel north=new JPanel(new BorderLayout());north.add(form,BorderLayout.CENTER);north.add(actions,BorderLayout.SOUTH);add(north,BorderLayout.NORTH);
        tabla.setRowHeight(25);add(new JScrollPane(tabla),BorderLayout.CENTER);
        registrar.addActionListener(e->registrar());guardar.addActionListener(e->editar());eliminar.addActionListener(e->eliminar());limpiar.addActionListener(e->limpiar());tabla.getSelectionModel().addListSelectionListener(e->cargar());refrescarTabla();
    }
    private void registrar(){Producto p=construir(0);if(p==null)return;ProductoController.agregarProducto(p);mensaje("Producto registrado con ID "+p.getIdProducto());limpiar();refrescarTabla();}
    private void editar(){if(idSeleccionado==null){aviso("Selecciona un producto de la tabla para editar.");return;}Producto actual=ProductoController.buscarProducto(idSeleccionado);if(actual==null)return;Producto p=construir(idSeleccionado);if(p==null)return;if(p.getClass()!=actual.getClass()){aviso("No se puede cambiar Tamal por Lechona en un mismo registro.");return;}ProductoController.actualizarProducto(p);mensaje("Producto actualizado.");refrescarTabla();}
    private void eliminar(){if(idSeleccionado==null){aviso("Selecciona un producto para eliminar.");return;}if(JOptionPane.showConfirmDialog(this,"¿Eliminar el producto seleccionado?","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){ProductoController.eliminarProducto(idSeleccionado);limpiar();refrescarTabla();}}
    private Producto construir(int id){String n=nombre.getText().trim();if(n.isEmpty()){aviso("El nombre comercial es obligatorio. Ejemplo: Tamal normal grande.");return null;}BigDecimal pr;try{pr=new BigDecimal(precio.getText().trim());if(pr.signum()<=0)throw new NumberFormatException();}catch(NumberFormatException ex){aviso("El precio debe ser un número mayor que 0.");return null;}int st=(Integer)stock.getValue();String t=(String)tipoProducto.getSelectedItem();if("Tamal".equals(t))return new Tamal(id,n,descripcion.getText().trim(),pr,st,true,(TipoTamal)tipoTamal.getSelectedItem(),(TamanoTamal)tamañoTamal.getSelectedItem());return new Lechona(id,n,descripcion.getText().trim(),pr,st,true,(TamanoLechona)tamañoLechona.getSelectedItem(),(Integer)porciones.getValue());}
    private void cargar(){int r=tabla.getSelectedRow();if(r<0)return;idSeleccionado=(Integer)modelo.getValueAt(r,0);Producto p=ProductoController.buscarProducto(idSeleccionado);if(p==null)return;nombre.setText(p.getNombre());descripcion.setText(p.getDescripcion());precio.setText(p.getPrecio().toString());stock.setValue(p.getStock());if(p instanceof Tamal x){tipoProducto.setSelectedItem("Tamal");tipoTamal.setSelectedItem(x.getTipo());tamañoTamal.setSelectedItem(x.getTamaño());}else if(p instanceof Lechona x){tipoProducto.setSelectedItem("Lechona");tamañoLechona.setSelectedItem(x.getTamaño());porciones.setValue(x.getNumeroPorciones());}}
    private void limpiar(){idSeleccionado=null;nombre.setText("");descripcion.setText("");precio.setText("");stock.setValue(0);porciones.setValue(1);tabla.clearSelection();}
    public void refrescarTabla(){modelo.setRowCount(0);for(Producto p:ProductoController.listarProductos()){String presentacion=p instanceof Tamal x?x.getTipo()+" · "+x.getTamaño():((Lechona)p).getTamaño()+" · "+((Lechona)p).getNumeroPorciones()+" porciones";modelo.addRow(new Object[]{p.getIdProducto(),p.getNombre(),p instanceof Tamal?"Tamal":"Lechona",presentacion,p.getPrecio(),p.getStock()});}}
    private void add(JPanel p,GridBagConstraints g,int y,String l,JComponent c){g.gridy=y;g.gridx=0;g.gridwidth=1;g.weightx=0;p.add(new JLabel(l),g);g.gridx=1;g.weightx=1;p.add(c,g);}private void mensaje(String s){JOptionPane.showMessageDialog(this,s,"Productos",JOptionPane.INFORMATION_MESSAGE);}private void aviso(String s){JOptionPane.showMessageDialog(this,s,"Productos",JOptionPane.WARNING_MESSAGE);}
}