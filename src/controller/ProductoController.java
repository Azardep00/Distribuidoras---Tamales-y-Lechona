package controller;

import java.util.*;
import model.*;
import repository.*;

public class ProductoController {
  private final IProductoRepository repo;
  private int siguienteId = 1;

  public ProductoController(IProductoRepository repo) {
    this.repo = repo;
  }

  public void registrar(Producto p) {
    validar(p);
    p.setIdProducto(siguienteId++);
    repo.guardar(p);
  }

  public List<Producto> listar(boolean incluirInactivos) {
    return repo.listarTodos().stream().filter(p -> incluirInactivos || p.isEstado()).toList();
  }

  public Producto buscarPorId(int id) {
    return repo.buscarPorId(id).orElse(null);
  }

  public List<Producto> buscarPorNombre(String texto) {
    String q = texto == null ? "" : texto.trim().toLowerCase();
    return listar(false).stream().filter(p -> p.getNombre().toLowerCase().contains(q)).toList();
  }

  public void actualizar(Producto nuevo) {
    if (nuevo == null || nuevo.getIdProducto() <= 0)
      throw new IllegalArgumentException("Producto inválido.");
    validar(nuevo);
    Producto actual = buscarPorId(nuevo.getIdProducto());
    if (actual == null) throw new IllegalArgumentException("Producto no encontrado.");
    if (!actual.getClass().equals(nuevo.getClass()))
      throw new IllegalArgumentException("No puedes cambiar el tipo de producto; crea uno nuevo.");
    actual.setNombre(nuevo.getNombre());
    actual.setDescripcion(nuevo.getDescripcion());
    actual.setPrecio(nuevo.getPrecio());
    if (nuevo instanceof Tamal t && actual instanceof Tamal a) {
      a.setTipo(t.getTipo());
      a.setTamaño(t.getTamaño());
    }
    if (nuevo instanceof Lechona l && actual instanceof Lechona a) {
      a.setTamaño(l.getTamaño());
      a.setNumeroPorciones(l.getNumeroPorciones());
    }
  }

  public void desactivar(int id) {
    if (buscarPorId(id) == null) throw new IllegalArgumentException("Producto no encontrado.");
    repo.desactivar(id);
  }

  private void validar(Producto p) {
    if (p == null || p.getNombre() == null || p.getNombre().isBlank())
      throw new IllegalArgumentException("El nombre del producto es obligatorio.");
    if (p.getPrecio() == null || p.getPrecio().signum() <= 0)
      throw new IllegalArgumentException("El precio debe ser mayor que 0.");
    if (p.getStock() < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
  }
}
