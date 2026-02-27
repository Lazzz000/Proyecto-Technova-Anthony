package com.demo.service;

import java.util.List;

import com.demo.modelo.Comprobante;
import com.demo.modelo.DatosFacturacion;
import com.demo.modelo.Pedido;

public interface ComprobanteService {
	
	Comprobante buscarPorPedido(Integer idPedido);
	List<Comprobante> listar();
	Comprobante generarComprobante(Integer idPedido, Integer idDatosFacturacion);
	
	Comprobante generarComprobante(Pedido pedido,
            DatosFacturacion datos);
	
	 Integer generarNumeroComprobante();
	 
	 Comprobante buscarPorId(Integer idComprobante);
	 
}
