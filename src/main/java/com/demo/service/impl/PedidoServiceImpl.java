package com.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.modelo.*;
import com.demo.modelo.enums.EstadoPedido;
import com.demo.modelo.enums.MetodoPago;
import com.demo.modelo.enums.TipoComprobante;
import com.demo.modelo.enums.TipoDocumentoEnum;
import com.demo.repositorio.*;
import com.demo.service.ComprobanteService;
import com.demo.service.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private PedidoDetalleRepositorio pedidoDetalleRepositorio;

    @Autowired
    private ProductosRepositorio productosRepositorio;

    @Autowired
    private ComprobanteRepositorio comprobanteRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;

    @Autowired
    private DocumentoIdentificacionRepositorio documentoIdentificacionRepositorio;
    
    @Autowired
    private DatosFacturacionRepositorio datosFacturacionRepositorio;
    
    @Autowired
    private ComprobanteService comprobanteService;


    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return pedidoRepositorio.findById(id).orElse(null);
    }

    @Override
    public List<Pedido> listarPorCliente(Integer idCliente) {
        return pedidoRepositorio.findByCliente_IdCliente(idCliente);
    }

    @Override
    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepositorio.findByEstado(estado);
    }

    @Override
    public long contarPedidos() {
        return pedidoRepositorio.count();
    }

    @Override
    public List<Pedido> listarUltimos(int limite) {
        return pedidoRepositorio.findTop5ByOrderByFechaPedidoDesc();
    }

    @Override
    public double ingresosDelMes() {
        return pedidoRepositorio.ingresosDelMes();
    }


    @Override
    @Transactional
    public Pedido crearPedido(Pedido pedido, List<PedidoDetalle> detalles) {

        BigDecimal total = BigDecimal.ZERO;

        pedido.setTotal(BigDecimal.ZERO);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

        for (PedidoDetalle detalle : detalles) {

            Productos producto = productosRepositorio
                    .findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productosRepositorio.save(producto);

            BigDecimal precioUnitario = producto.getPrecio();
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setPedido(pedidoGuardado);

            BigDecimal subtotal = precioUnitario.multiply(
                    BigDecimal.valueOf(detalle.getCantidad()));

            total = total.add(subtotal);

            pedidoDetalleRepositorio.save(detalle);
        }

        pedidoGuardado.setTotal(total);

        return pedidoRepositorio.save(pedidoGuardado);
    }


    @Override
    @Transactional
    public Pedido procesarPedidoCompleto(CheckoutDTO checkout,
                                         Authentication authentication) {

        if (authentication == null ||
            authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // 1️ Obtener usuario
        Usuario usuario = usuarioRepositorio
                .findByCorreo(authentication.getName());

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 2️ Obtener cliente
        Cliente cliente = clienteRepositorio.findByUsuario(usuario);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        // 3️ Obtener o crear documento
        TipoDocumento tipo = tipoDocumentoRepositorio
                .findById(checkout.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Tipo documento no encontrado"));

        DocumentoIdentificacion documento =
                documentoIdentificacionRepositorio
                .findByTipoDocumentoAndNumeroDocumento(
                        tipo,
                        checkout.getNumeroDocumento())
                .orElseGet(() -> {

                    DocumentoIdentificacion nuevo = new DocumentoIdentificacion();
                    nuevo.setTipoDocumento(tipo);
                    nuevo.setNumeroDocumento(checkout.getNumeroDocumento());
                    nuevo.setNombreTitular(checkout.getNombreTitular());

                    return documentoIdentificacionRepositorio.save(nuevo);
                });

        // 4️ Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDocumento(documento);
        pedido.setNombreReceptor(checkout.getNombreReceptor());
        pedido.setDireccionEnvio(checkout.getDireccionEnvio());
        pedido.setCiudad(checkout.getCiudad());
        pedido.setTelefonoContacto(checkout.getTelefonoContacto());
        pedido.setMetodoPago(checkout.getMetodoPago());
        pedido.setFechaPedido(LocalDateTime.now());

        // Estado automático
        if (checkout.getMetodoPago() == MetodoPago.TARJETA) {
            pedido.setEstado(EstadoPedido.PAGADO);
        } else {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }
        	
        if (checkout.getMetodoPago() == MetodoPago.TARJETA) {
            pedido.setUltimos4Tarjeta(checkout.getUltimos4Tarjeta());
            pedido.setMarcaTarjeta(checkout.getMarcaTarjeta());
        }
        

        pedido.setTotal(BigDecimal.ZERO);

        pedido = pedidoRepositorio.save(pedido);

        // 5️ Procesar detalles + stock
        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem item : checkout.getItems()) {

            Productos producto = productosRepositorio
                    .findById(item.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productosRepositorio.save(producto);

            // Crear detalle
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            pedidoDetalleRepositorio.save(detalle);

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            total = total.add(subtotal);
        }

        // Actualizar total real
        pedido.setTotal(total);
        pedidoRepositorio.save(pedido);

        // 6️ Crear Datos de Facturación
        DatosFacturacion datosFacturacion = new DatosFacturacion();
        TipoDocumentoEnum tipoEnum = TipoDocumentoEnum.valueOf(
                documento.getTipoDocumento().getSigla()
        );

        datosFacturacion.setTipoDocumento(tipoEnum);
        datosFacturacion.setNumeroDocumento(documento.getNumeroDocumento());
        datosFacturacion.setNombreRazonSocial(documento.getNombreTitular());
        datosFacturacion.setDireccion(checkout.getDireccionEnvio());
        datosFacturacion.setTelefono(checkout.getTelefonoContacto());
        datosFacturacion.setCorreo(usuario.getCorreo());

        datosFacturacion = datosFacturacionRepositorio.save(datosFacturacion);

        // 7️ Generar comprobante usando el service correcto
        comprobanteService.generarComprobante(pedido, datosFacturacion);

        return pedido;
    }

    // GENERAR COMPROBANTE
    public Comprobante generarComprobante(Pedido pedido,
            DocumentoIdentificacion doc) {

			Comprobante comprobante = new Comprobante();
			
			comprobante.setPedido(pedido);
			comprobante.setFechaEmision(LocalDateTime.now());
			comprobante.setTotal(pedido.getTotal());
			comprobante.setSerie("B001"); 

			return comprobante;
		}


    @Override
    public Pedido actualizarEstado(Integer idPedido, String nuevoEstado) {

        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());

        pedido.setEstado(estado);
        return pedidoRepositorio.save(pedido);
    }

    @Override
    @Transactional
    public void cancelarPedido(Integer idPedido) {

        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getEstado().equals(EstadoPedido.PENDIENTE)) {
            throw new RuntimeException("Solo se pueden cancelar pedidos PENDIENTES");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepositorio.save(pedido);
    }

	@Override
	public Comprobante guardarComprobante(Comprobante comprobante) {
		  return comprobanteRepositorio.save(comprobante);
	}

	@Override
	public List<Pedido> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
		 return pedidoRepositorio.findByFechaPedidoBetween(
		            inicio.atStartOfDay(),
		            fin.atTime(23,59,59));
	}

}
