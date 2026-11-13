package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;


import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.Pedido;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByIdCliente(String idCliente);
    List<Pedido> findByEstado(EstadoPedido estado);
}