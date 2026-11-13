package com.tamaleslechona.tamaleslechona.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;
import com.tamaleslechona.tamaleslechona.exception.RecursoNoEncontradoException;
import com.tamaleslechona.tamaleslechona.model.Lechona;
import com.tamaleslechona.tamaleslechona.model.Producto;
import com.tamaleslechona.tamaleslechona.model.Tamal;
import com.tamaleslechona.tamaleslechona.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final MongoTemplate mongo;

    public ProductoService(ProductoRepository repo, MongoTemplate mongo) {
        this.repo = repo;
        this.mongo = mongo;
    }

    public Producto registrar(Producto p) {
        validar(p);
        return repo.save(p);
    }

    public List<Producto> listar(boolean incluirInactivos) {
        return incluirInactivos ? repo.findAll() : repo.findByEstadoTrue();
    }

    public Producto buscarPorId(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));
    }

    public List<Producto> buscarPorNombre(String texto) {
        String q = texto == null ? "" : texto.trim();
        return repo.findByNombreContainingIgnoreCase(q);
    }

    public Producto actualizar(String id, Producto nuevo) {
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

    // Guarda explícitamente un producto ya modificado (Mongo no rastrea cambios
    // automáticamente como JPA: hay que llamar a save()).
    public Producto guardar(Producto p) {
        return repo.save(p);
    }

    // Resta stock en UNA sola operación atómica de Mongo: "si estado=activo y
    // stock >= cantidad, entonces stock = stock - cantidad". Comprobar y restar
    // ocurren juntos, así dos clientes no pueden vender la misma última unidad.
    // Devuelve false si no había stock suficiente (o el producto está inactivo).
    public boolean descontarStockAtomico(String idProducto, int cantidad) {
        Query q = new Query(Criteria.where("idProducto").is(idProducto)
                .and("estado").is(true)
                .and("stock").gte(cantidad));
        UpdateResult r = mongo.updateFirst(q, new Update().inc("stock", -cantidad), Producto.class);
        return r.getModifiedCount() == 1;
    }

    // Suma stock con $inc (también atómico, no lee antes de escribir).
    public void aumentarStockAtomico(String idProducto, int cantidad) {
        Query q = new Query(Criteria.where("idProducto").is(idProducto));
        UpdateResult r = mongo.updateFirst(q, new Update().inc("stock", cantidad), Producto.class);
        if (r.getMatchedCount() == 0) {
            throw new RecursoNoEncontradoException("Producto no encontrado.");
        }
    }

    public void desactivar(String id) {
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
}
