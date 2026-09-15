package tamaleslechona.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tamaleslechona.exception.RecursoNoEncontradoException;
import tamaleslechona.model.Proveedor;
import tamaleslechona.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository repo;

    public ProveedorService(ProveedorRepository repo) {
        this.repo = repo;
    }

    public Proveedor registrar(Proveedor p) {
        validar(p.getNombre(), p.getTelefono(), p.getCorreo());
        p.setEstado(true);
        return repo.save(p);
    }

    public List<Proveedor> listar(boolean incluirInactivos) {
        return incluirInactivos ? repo.findAll() : repo.findByEstadoTrue();
    }

    public Proveedor buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado."));
    }

    public List<Proveedor> buscar(String q) {
        String texto = q == null ? "" : q.trim();
        return repo.findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCase(
                texto, texto, texto);
    }

    public Proveedor actualizar(int id, Proveedor nuevo) {
        validar(nuevo.getNombre(), nuevo.getTelefono(), nuevo.getCorreo());
        Proveedor actual = buscarPorId(id);
        actual.setNombre(nuevo.getNombre());
        actual.setTelefono(nuevo.getTelefono());
        actual.setCorreo(nuevo.getCorreo());
        actual.setDireccion(nuevo.getDireccion());
        return repo.save(actual);
    }

    public void desactivar(int id) {
        Proveedor p = buscarPorId(id);
        p.setEstado(false);
        repo.save(p);
    }

    private void validar(String nombre, String telefono, String correo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new IllegalArgumentException("Correo inválido.");
        }
    }
}
