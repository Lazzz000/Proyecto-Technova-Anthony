package com.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.PedidoDetalle;

@Repository
public interface PedidoDetalleRepositorio 
        extends JpaRepository<PedidoDetalle, Integer> {

    List<PedidoDetalle> findByPedidoIdPedido(Integer idPedido);
}
