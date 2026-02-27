package com.demo.repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.modelo.Pedido;

@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByCliente_IdCliente(Integer idCliente);

    List<Pedido> findByEstado(String estado);

	List<Pedido> findTop5ByOrderByFechaPedidoDesc();
    
	@Query("""
			SELECT COALESCE(SUM(p.total), 0)
			FROM Pedido p
			WHERE MONTH(p.fechaPedido) = MONTH(CURRENT_DATE)
			AND YEAR(p.fechaPedido) = YEAR(CURRENT_DATE)
			""")
			Double ingresosDelMes();
	
	List<Pedido> findByClienteIdClienteOrderByFechaPedidoDesc(Integer idCliente);
	
	
	List<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);

}
