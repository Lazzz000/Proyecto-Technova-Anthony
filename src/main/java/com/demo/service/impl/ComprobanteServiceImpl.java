package com.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.modelo.Comprobante;
import com.demo.modelo.DatosFacturacion;
import com.demo.modelo.Pedido;
import com.demo.modelo.enums.TipoComprobante;
import com.demo.modelo.enums.TipoDocumentoEnum;
import com.demo.repositorio.ComprobanteRepositorio;
import com.demo.repositorio.DatosFacturacionRepositorio;
import com.demo.repositorio.PedidoRepositorio;
import com.demo.service.ComprobanteService;

@Service
public class ComprobanteServiceImpl implements ComprobanteService{
	@Autowired
	ComprobanteRepositorio comprobanteRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private DatosFacturacionRepositorio datosFacturacionRepositorio;

	//Genera un comprobante a partir del idPedido, verifica si es Factura y Boleta
    //Para asi mismo asignar un numero y serie correspondiente.
	  @Override
	public Comprobante generarComprobante(Integer idPedido, Integer idDatosFacturacion) {
		 Pedido pedido = pedidoRepositorio.findById(idPedido)
		            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

		    if (comprobanteRepositorio.findByPedido_IdPedido(idPedido) != null) {
		        throw new RuntimeException("El pedido ya tiene comprobante");
		    }

		    DatosFacturacion datos = datosFacturacionRepositorio.findById(idDatosFacturacion)
		            .orElseThrow(() -> new RuntimeException("Datos de facturación no encontrados"));

		    Comprobante comprobante = new Comprobante();
		    comprobante.setPedido(pedido);
		    comprobante.setDatosFacturacion(datos);
		    comprobante.setFechaEmision(LocalDateTime.now());
		    comprobante.setTotal(
		    	    BigDecimal.valueOf(
		    	        pedido.getTotal() != null ? pedido.getTotal().doubleValue() : 0.0
		    	    )
		    	);

		    //  LÓGICA CORRECTA
		    if (datos.getTipoDocumento() == TipoDocumentoEnum.RUC) {
		        comprobante.setTipoComprobante(TipoComprobante.FACTURA);
		        comprobante.setSerie("F001");
		    } else {
		        comprobante.setTipoComprobante(TipoComprobante.BOLETA);
		        comprobante.setSerie("B001");
		    }

		    Integer ultimo = comprobanteRepositorio.obtenerUltimoNumeroPorSerie(comprobante.getSerie());
		    comprobante.setNumero(ultimo == null ? 1 : ultimo + 1);

		    return comprobanteRepositorio.save(comprobante);
	}
	@Override
	public Comprobante buscarPorPedido(Integer idPedido) {
		
		return comprobanteRepositorio.findByPedido_IdPedido(idPedido);
	}

	@Override
	public List<Comprobante> listar() {
	
		return comprobanteRepositorio.findAll();
	}
	
	@Override
	public Comprobante generarComprobante(Pedido pedido, DatosFacturacion datos) {
	    if (datos == null) {
	        throw new RuntimeException("Datos de facturación no encontrados");
	    }

	    Comprobante comp = new Comprobante();
	    comp.setPedido(pedido);
	    comp.setDatosFacturacion(datos);
	    comp.setFechaEmision(LocalDateTime.now());
	    comp.setTotal(pedido.getTotal());

	    // Determinar tipo de comprobante y serie según tipo de documento
	    if (datos.getTipoDocumento() == TipoDocumentoEnum.RUC) {
	        comp.setTipoComprobante(TipoComprobante.FACTURA);
	        comp.setSerie("F001");
	    } else {
	        comp.setTipoComprobante(TipoComprobante.BOLETA);
	        comp.setSerie("B001");
	    }

	    // Obtener último número según serie
	    Integer ultimoNumero = comprobanteRepositorio.findMaxNumeroBySerie(comp.getSerie());
	    comp.setNumero(ultimoNumero != null ? ultimoNumero + 1 : 1);

	    // Guardar en BD y devolver
	    return comprobanteRepositorio.save(comp);
	}
	@Override
	public Integer generarNumeroComprobante() {
		Integer ultimo = comprobanteRepositorio.obtenerUltimoNumero();
	    return (ultimo == null) ? 1 : ultimo + 1;
	}

	  @Override
	    public Comprobante buscarPorId(Integer idComprobante) {
	        return comprobanteRepositorio.findById(idComprobante)
	                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con ID: " + idComprobante));
	    }

}
