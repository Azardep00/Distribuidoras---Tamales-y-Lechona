package repository;

import java.util.*;

import model.Usuario;

public interface IUsuarioRepository {
    void guardar(Usuario u);

    Optional<Usuario> buscarPorId(int id);

    List<Usuario> listarTodos();

    void eliminar(Usuario u);

    boolean existeCorreo(String correo, int excluirId);
}
