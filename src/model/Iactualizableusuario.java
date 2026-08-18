package model;

/**
 * Interfaz que deben implementar los subtipos de Usuario (Cliente, Empleado)
 * para permitir que el Controller actualice sus datos especificos
 * sin necesidad de conocer el tipo concreto (principio de sustitucion / DIP).
 */
public interface Iactualizableusuario {
    void actualizarDatos(Usuario usuarioConNuevosDatos);
}