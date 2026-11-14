package com.tamaleslechona.tamaleslechona.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tamaleslechona.tamaleslechona.exception.ConflictException;
import com.tamaleslechona.tamaleslechona.exception.RecursoNoEncontradoException;
import com.tamaleslechona.tamaleslechona.model.Proveedor;
import com.tamaleslechona.tamaleslechona.repository.MovimientoInventarioRepository;
import com.tamaleslechona.tamaleslechona.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository repo;
    private final MovimientoInventarioRepository movimientoRepo;

    public ProveedorService(ProveedorRepository repo, MovimientoInventarioRepository movimientoRepo) {
        this.repo = repo;
        this.movimientoRepo = movimientoRepo;
    }

    public Proveedor registrar(Proveedor p) {
        validar(p.getNombre(), p.getTelefono(), p.getCorreo());
        p.setEstado(true);
        return repo.save(p);
    }

    public List<Proveedor> listar(boolean incluirInactivos) {
        return incluirInactivos ? repo.findAll() : repo.findByEstadoTrue();
    }

    public Proveedor buscarPorId(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado."));
    }

    public List<Proveedor> buscar(String q) {
        String texto = q == null ? "" : q.trim();
        return repo.findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCase(
                texto, texto, texto);
    }

    public Proveedor actualizar(String id, Proveedor nuevo) {
        validar(nuevo.getNombre(), nuevo.getTelefono(), nuevo.getCorreo());
        Proveedor actual = buscarPorId(id);
        actual.setNombre(nuevo.getNombre());
        actual.setTelefono(nuevo.getTelefono());
        actual.setCorreo(nuevo.getCorreo());
        actual.setDireccion(nuevo.getDireccion());
        return repo.save(actual);
    }

    // Borrado FÍSICO: el documento desaparece de Mongo. Si el proveedor ya
    // tiene movimientos de inventario no se puede borrar (se perdería el
    // historial): responde 409 y se debe desactivar en su lugar.
    public void eliminar(String id) {
        buscarPorId(id); // 404 si no existe
        if (movimientoRepo.existsByIdProveedor(id)) {
            throw new ConflictException(
                    "No se puede eliminar: el proveedor tiene movimientos de inventario. Desactívalo en su lugar.");
        }
        repo.deleteById(id);
    }

    // Borrado LÓGICO: el proveedor queda inactivo pero se conserva el registro.
    public void desactivar(String id) {
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
