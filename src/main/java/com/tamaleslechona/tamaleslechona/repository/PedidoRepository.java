package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Navega Pedido -> cliente -> idUsuario (idUsuario vive en Usuario, la
    // clase base de Cliente).
    List<Pedido> findByCliente_IdUsuario(int idCliente);

    List<Pedido> findByEstado(EstadoPedido estado);
}