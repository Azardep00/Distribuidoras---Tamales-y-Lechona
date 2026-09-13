package main.java.com.tamalesylechona.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tamalesylechona.api.model.Lechona;
import com.tamalesylechona.api.model.Producto;
import com.tamalesylechona.api.model.Tamal;
import com.tamalesylechona.api.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public Producto registrar(Producto p) {
        validar(p);
        return repo.save(p);
    }

    public List<Producto> listar(boolean incluirInactivos) {
        return incluirInactivos ? repo.findAll() : repo.findByEstadoTrue();
    }

    public Producto buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado."));
    }

    public List<Producto> buscarPorNombre(String texto) {
        String q = texto == null ? "" : texto.trim();
        return repo.findByNombreContainingIgnoreCase(q);
    }

    public Producto actualizar(int id, Producto nuevo) {
        validar(nuevo);
        Producto actual = buscarPorId(id);
        if (!actual.getClass().equals(nuevo.getClass()))
            throw new IllegalArgumentException("No puedes cambiar el tipo de producto; crea uno nuevo.");

        actual.setNombre(nuevo.getNombre());
        actual.setDescripcion(nuevo.getDescripcion());
        actual.setPrecio(nuevo.getPrecio());

        if (nuevo instanceof Tamal t && actual instanceof Tamal a) {
            a.setTipo(t.getTipo());
            a.setTamano(t.getTamano());
        }
        if (nuevo instanceof Lechona l && actual instanceof Lechona a) {
            a.setTamano(l.getTamano());
            a.setNumeroPorciones(l.getNumeroPorciones());
        }
        return repo.save(actual);
    }

    public void desactivar(int id) {
        Producto p = buscarPorId(id);
        p.setEstado(false);
        repo.save(p);
    }

    private void validar(Producto p) {
        if (p == null || p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        if (p.getPrecio() == null || p.getPrecio().signum() <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        if (p.getStock() < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
    }

    public static class NoEncontradoException extends RuntimeException {
        public NoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }
}
