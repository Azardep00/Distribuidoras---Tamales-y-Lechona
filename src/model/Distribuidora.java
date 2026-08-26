package model;

/**
 * PATRÓN SINGLETON
 * Solo debe existir UNA distribuidora en todo el sistema (representa la empresa).
 * Por eso el constructor es privado y el único acceso a la instancia es
 * a través de getInstancia().
 */
public class Distribuidora {

    private static Distribuidora instancia;

    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;

    // Constructor privado: nadie fuera de la clase puede hacer "new Distribuidora()"
    private Distribuidora() {
        this.nombre = "Distribuidora de Tamales y Lechona";
        this.nit = "900000000-1";
        this.direccion = "Ibagué, Tolima";
        this.telefono = "3000000000";
    }

    // Único punto de acceso a la instancia (se crea solo la primera vez que se pide)
    public static Distribuidora getInstancia() {
        if (instancia == null) {
            instancia = new Distribuidora();
        }
        return instancia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Distribuidora{" +
                "nombre='" + nombre + '\'' +
                ", nit='" + nit + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
