package com.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.demo.modelo.CheckoutDTO;
import com.demo.modelo.Comprobante;
import com.demo.modelo.Pedido;
import com.demo.modelo.PedidoDetalle;

public interface PedidoService {
	
	List<Pedido> listarTodos();
	
	Pedido buscarPorId(Integer id);
	
	List<Pedido> listarUltimos(int limite);
	
	double ingresosDelMes();
	
	List<Pedido> listarPorCliente(Integer idCliente);
	
	List<Pedido> listarPorEstado(String estado);
	
	long contarPedidos();
	
	// Negocio
	Pedido crearPedido(Pedido pedido, List<PedidoDetalle> detalles);

	Pedido actualizarEstado(Integer idPedido, String nuevoEstado);
	 
	void cancelarPedido(Integer idPedido);
	  
	Comprobante guardarComprobante(Comprobante comprobante);
	  
	Pedido procesarPedidoCompleto(CheckoutDTO checkout,
	                              Authentication authentication);
	
	List<Pedido> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
}
