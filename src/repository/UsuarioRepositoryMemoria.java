package repository;

import java.util.*;
import model.Usuario;

public class UsuarioRepositoryMemoria implements IUsuarioRepository {
  private final List<Usuario> datos = new ArrayList<>();

  public void guardar(Usuario u) {
    datos.add(u);
  }

  public Optional<Usuario> buscarPorId(int id) {
    return datos.stream().filter(u -> u.getIdUsuario() == id).findFirst();
  }

  public List<Usuario> listarTodos() {
    return new ArrayList<>(datos);
  }

  public void eliminar(Usuario u) {
    datos.remove(u);
  }

  public boolean existeCorreo(String correo, int excluirId) {
    return datos.stream()
        .anyMatch(u -> u.getIdUsuario() != excluirId && u.getCorreo().equalsIgnoreCase(correo));
  }
}
