package com.tamaleslechona.tamaleslechona.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tamaleslechona.tamaleslechona.model.EstadoPagoWompi;
import com.tamaleslechona.tamaleslechona.model.PagoWompi;

import jakarta.persistence.LockModeType;

public interface PagoWompiRepository extends JpaRepository<PagoWompi, Integer> {

    Optional<PagoWompi> findFirstByPedido_IdPedidoOrderByIdPagoWompiDesc(Integer idPedido);

    List<PagoWompi> findByPedido_IdPedidoIn(Collection<Integer> idPedidos);

    boolean existsByPedido_IdPedidoAndEstado(Integer idPedido, EstadoPagoWompi estado);

    boolean existsByPedido_IdPedidoAndEstadoIn(Integer idPedido, Collection<EstadoPagoWompi> estados);

    Optional<PagoWompi> findFirstByReferencia(String referencia);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pago from PagoWompi pago where pago.referencia = :referencia")
    Optional<PagoWompi> buscarPorReferenciaParaActualizar(@Param("referencia") String referencia);
}
