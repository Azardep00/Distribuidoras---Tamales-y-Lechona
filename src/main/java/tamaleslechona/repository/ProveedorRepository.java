package tamaleslechona.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tamaleslechona.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByEstadoTrue();

    List<Proveedor> findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCase(
            String nombre, String telefono, String correo);
}
