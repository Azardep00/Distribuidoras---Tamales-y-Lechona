package tamaleslechona.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tamaleslechona.model.Producto;

// JpaRepository ya trae save(), findById(), findAll(), deleteById(), etc.
// Solo agregamos lo que no viene por defecto.
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstadoTrue();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
