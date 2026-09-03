package controller;

import java.util.*;
import model.Proveedor;
import repository.IProveedorRepository;

public class ProveedorController {
  private final IProveedorRepository repo;
  private int siguienteId = 1;

  public ProveedorController(IProveedorRepository repo) {
    this.repo = repo;
  }

  public void registrar(Proveedor p) {
    validar(p);
    p.setIdProveedor(siguienteId++);
    p.setEstado(true);
    repo.guardar(p);
  }

  public List<Proveedor> listarActivos() {
    return repo.listarTodos().stream().filter(Proveedor::isEstado).toList();
  }

  public List<Proveedor> listarTodos() {
    return repo.listarTodos();
  }

  public Proveedor buscarPorId(int id) {
    return repo.buscarPorId(id);
  }

  public List<Proveedor> buscar(String q) {
    String s = q == null ? "" : q.trim().toLowerCase();
    return listarActivos().stream()
        .filter(
            p ->
                p.getNombre().toLowerCase().contains(s)
                    || p.getTelefono().contains(s)
                    || p.getCorreo().toLowerCase().contains(s))
        .toList();
  }

  public void actualizar(int id, String n, String t, String c, String d) {
    Proveedor p = buscarPorId(id);
    if (p == null) throw new IllegalArgumentException("Proveedor no encontrado.");
    validar(n, t, c);
    p.setNombre(n.trim());
    p.setTelefono(t.trim());
    p.setCorreo(c.trim());
    p.setDireccion(d == null ? "" : d.trim());
  }

  public void desactivar(int id) {
    Proveedor p = buscarPorId(id);
    if (p == null) throw new IllegalArgumentException("Proveedor no encontrado.");
    p.setEstado(false);
  }

  private void validar(Proveedor p) {
    if (p == null) throw new IllegalArgumentException("Proveedor inválido.");
    validar(p.getNombre(), p.getTelefono(), p.getCorreo());
  }

  private void validar(String n, String t, String c) {
    if (n == null || n.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
    if (t == null || t.isBlank()) throw new IllegalArgumentException("El teléfono es obligatorio.");
    if (c != null && !c.isBlank() && !c.contains("@"))
      throw new IllegalArgumentException("Correo inválido.");
  }
}
