package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.Pedido;

public interface PedidoRepository extends MongoRepository<Pedido, String> {

    // El pedido guarda el id del cliente como referencia (campo idCliente).
    List<Pedido> findByIdCliente(String idCliente);

    List<Pedido> findByEstado(EstadoPedido estado);
}
