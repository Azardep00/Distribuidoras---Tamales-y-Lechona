package view;

import controller.ProveedorController;
import model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Gestión de proveedores. El ID es interno: solo se muestra/usa al editar o eliminar. */
public class ProveedorPanel extends JPanel {
    private final ProveedorController controller;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtTelefono = new JTextField(14);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(20);
    private Integer proveedorSeleccionado;

    public ProveedorPanel(ProveedorController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx=0; g.gridy=0; formulario.add(new JLabel("Nombre / empresa:*"),g); g.gridx=1; formulario.add(txtNombre,g);
        g.gridx=0; g.gridy=1; formulario.add(new JLabel("Teléfono:*"),g); g.gridx=1; formulario.add(txtTelefono,g);
        g.gridx=0; g.gridy=2; formulario.add(new JLabel("Correo:"),g); g.gridx=1; formulario.add(txtCorreo,g);
        g.gridx=0; g.gridy=3; formulario.add(new JLabel("Dirección:"),g); g.gridx=1; formulario.add(txtDireccion,g);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton registrar = new JButton("Registrar proveedor");
        JButton editar = new JButton("Guardar cambios");
        JButton eliminar = new JButton("Eliminar");
        JButton consultar = new JButton("Consultar proveedores");
        JButton limpiar = new JButton("Limpiar");
        acciones.add(registrar); acciones.add(consultar); acciones.add(editar); acciones.add(eliminar); acciones.add(limpiar);

        JPanel norte = new JPanel(new BorderLayout(8,8));
        norte.setBorder(BorderFactory.createTitledBorder("Datos del proveedor"));
        norte.add(formulario, BorderLayout.CENTER); norte.add(acciones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID","Proveedor","Teléfono","Correo","Dirección","Estado"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tabla = new JTable(modelo); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); tabla.setRowHeight(25);
        add(new JScrollPane(tabla),BorderLayout.CENTER);

        registrar.addActionListener(e -> registrar());
        consultar.addActionListener(e -> mostrarConsulta());
        editar.addActionListener(e -> editar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiar());
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        refrescarTabla();
    }

    private void registrar(){
        if(!validarBasico()) return;
        controller.registrarProveedor(new Proveedor(0,txtNombre.getText().trim(),txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),txtDireccion.getText().trim(),true));
        mensaje("Proveedor registrado correctamente."); limpiar(); refrescarTabla();
    }
    private void editar(){
        if(proveedorSeleccionado==null){ aviso("Selecciona un proveedor de la tabla para editar."); return; }
        if(!validarBasico()) return;
        if(controller.actualizarProveedor(proveedorSeleccionado,txtNombre.getText().trim(),txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),txtDireccion.getText().trim())){ mensaje("Cambios guardados."); refrescarTabla(); }
    }
    private void eliminar(){
        if(proveedorSeleccionado==null){ aviso("Selecciona un proveedor de la tabla para eliminar."); return; }
        int r=JOptionPane.showConfirmDialog(this,"¿Eliminar el proveedor seleccionado?","Confirmar",JOptionPane.YES_NO_OPTION);
        if(r==JOptionPane.YES_OPTION){ controller.eliminarProveedor(proveedorSeleccionado); mensaje("Proveedor eliminado."); limpiar(); refrescarTabla(); }
    }
    private boolean validarBasico(){
        if(txtNombre.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()){
            aviso("Nombre y teléfono son obligatorios."); return false;
        } return true;
    }
    private void cargarSeleccion(){
        int row=tabla.getSelectedRow(); if(row<0) return;
        proveedorSeleccionado=(Integer)modelo.getValueAt(row,0);
        Proveedor p=controller.buscarProveedor(proveedorSeleccionado); if(p==null)return;
        txtNombre.setText(p.getNombre()); txtTelefono.setText(p.getTelefono()); txtCorreo.setText(p.getCorreo()); txtDireccion.setText(p.getDireccion());
    }
    private void limpiar(){proveedorSeleccionado=null; txtNombre.setText("");txtTelefono.setText("");txtCorreo.setText("");txtDireccion.setText("");tabla.clearSelection();}
    private void mensaje(String s){JOptionPane.showMessageDialog(this,s,"Proveedores",JOptionPane.INFORMATION_MESSAGE);}
    private void aviso(String s){JOptionPane.showMessageDialog(this,s,"Revisa los datos",JOptionPane.WARNING_MESSAGE);}
    public void refrescarTabla(){
        modelo.setRowCount(0); List<Proveedor> ps=controller.listarProveedores();
        for(Proveedor p:ps) modelo.addRow(new Object[]{p.getIdProveedor(),p.getNombre(),p.getTelefono(),p.getCorreo(),p.getDireccion(),p.isEstado()?"Activo":"Inactivo"});
    }
    /** Vista separada de consulta: no obliga a mezclar la consulta con el formulario. */
    public void mostrarConsulta(){
        JDialog d=new JDialog(SwingUtilities.getWindowAncestor(this),"Proveedores registrados",Dialog.ModalityType.APPLICATION_MODAL);
        JTable t=new JTable(new DefaultTableModel(new Object[]{"ID","Proveedor","Teléfono","Correo","Dirección"},0){public boolean isCellEditable(int r,int c){return false;}});
        DefaultTableModel m=(DefaultTableModel)t.getModel(); List<Proveedor> ps=controller.listarProveedores();
        if(ps.isEmpty()) m.addRow(new Object[]{"-","No hay proveedores registrados","","",""});
        else for(Proveedor p:ps)m.addRow(new Object[]{p.getIdProveedor(),p.getNombre(),p.getTelefono(),p.getCorreo(),p.getDireccion()});
        d.add(new JScrollPane(t)); d.setSize(700,350); d.setLocationRelativeTo(this); d.setVisible(true);
    }
}