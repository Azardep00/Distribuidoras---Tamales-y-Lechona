package controller;
import model.Proveedor;
import java.util.ArrayList;

public class ProveedorController {

    private ArrayList<Proveedor> proveedores;
    private int siguienteId;

    // CONSTRUCTOR

    public ProveedorController(){

        proveedores = new ArrayList<>();
        siguienteId = 1;

    }

    // METODO

    public void registrarProveedor(Proveedor proveedor){

        proveedor.setIdProveedor(siguienteId);

        proveedores.add(proveedor);

        siguienteId++;

    }

    public void mostrarProveedores(){

        for (Proveedor proveedor : proveedores){

            System.out.println("ID: " + proveedor.getIdProveedor());
            System.out.println("Nombre: " + proveedor.getNombre());
            System.out.println("Telefono: " + proveedor.getTelefono());
            System.out.println("Correo: " + proveedor.getCorreo());
            System.out.println("Direccion: " + proveedor.getDireccion());
            System.out.println("Estado: " + proveedor.isEstado());

            System.out.println("-----------------------------------------");

        }
    }

    public Proveedor buscarProveedor(int id){

        for(Proveedor proveedor : proveedores){

            if(proveedor.getIdProveedor()==id){
                return proveedor;
            }
        }

        return null;
    }

    public void actualizarProveedor(int id, String nombre, String telefono, String correo, String direccion){

        Proveedor proveedor = buscarProveedor(id);

        if(proveedor != null){

            proveedor.setNombre(nombre);
            proveedor.setTelefono(telefono);
            proveedor.setCorreo(correo);
            proveedor.setDireccion(direccion);

            System.out.println("Proveedor actualizado correctamente.");
        }
        else{

            System.out.println("No se encontro un proveedor con ese ID.");

        }
    }

    public void eliminarProveedor(int id){

        Proveedor provedor = buscarProveedor(id);

        if(provedor != null){

            proveedores.remove(provedor);

            System.out.println("Proveedor eliminado correctamente.");
        }
        else{

            System.out.println("No se encontro un proveedor con ese ID");
        }
    }



}