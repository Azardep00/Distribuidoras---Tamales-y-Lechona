package com.tamaleslechona.tamaleslechona.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.EstadoPagoWompi;
import com.tamaleslechona.tamaleslechona.model.PagoWompi;

public interface PagoWompiRepository extends MongoRepository<PagoWompi, String> {

    Optional<PagoWompi> findFirstByIdPedidoOrderByIdPagoWompiDesc(String idPedido);

    List<PagoWompi> findByIdPedidoIn(Collection<String> idPedidos);

    boolean existsByIdPedidoAndEstado(String idPedido, EstadoPagoWompi estado);

    boolean existsByIdPedidoAndEstadoIn(String idPedido, Collection<EstadoPagoWompi> estados);

    Optional<PagoWompi> findFirstByReferencia(String referencia);

    // Mongo no tiene bloqueo pesimista (@Lock). Se conserva el nombre para no
    // tocar el servicio; la unicidad de la referencia la garantiza el índice.
    default Optional<PagoWompi> buscarPorReferenciaParaActualizar(String referencia) {
        return findFirstByReferencia(referencia);
    }
}