package com.demo.controlador;

import com.demo.modelo.Categoria;
import com.demo.modelo.Productos;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.demo.modelo.*;
import com.demo.modelo.enums.EstadoPedido;
import com.demo.modelo.enums.MetodoPago;
import com.demo.repositorio.*;

@RestController
@RequestMapping("/api/v1")
public class ApiMovilController {

    @Autowired
    private ProductosService productoService;
    @Autowired private PedidoRepositorio pedidoRepo;
    @Autowired private PedidoDetalleRepositorio detalleRepo;
    @Autowired private ClienteRepositorio clienteRepo;
    @Autowired private ProductosRepositorio productosRepo;
    @Autowired private DocumentoIdentificacionRepositorio docRepo;

    @Autowired
    private CategoriaService categoriaService;

    // 1. Endpoint para obtener el catálogo de productos activos
    @GetMapping("/productos")
    public ResponseEntity<List<Productos>> obtenerCatalogo() {
        // Traemos solo los productos que están activos para la tienda
        List<Productos> productos = productoService.listarPorEstado("ACTIVO");
        return ResponseEntity.ok(productos);
    }

    // 2. Endpoint para obtener las categorías principales
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        List<Categoria> categorias = categoriaService.listarCategoriasPadre();
        return ResponseEntity.ok(categorias);
    }

    // 3. Endpoint para obtener subcategorías (ej. Laptops -> Gamers)
    @GetMapping("/categorias/{id}/subcategorias")
    public ResponseEntity<List<Categoria>> obtenerSubcategorias(@PathVariable Integer id) {
        List<Categoria> subcategorias = categoriaService.listarSubcategorias(id);
        return ResponseEntity.ok(subcategorias);
    }
    
 // --- ENDPOINT PARA RECIBIR EL CHECKOUT MÓVIL Y GUARDAR EN MYSQL ---
    @PostMapping("/checkout")
    @Transactional // Esto asegura que si algo falla, no se guarde información a medias
    public ResponseEntity<Map<String, Object>> recibirPedidoMovil(@RequestBody PedidoMovilRequest pedidoReq) {
        
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // 1. Buscamos al Cliente (Por ahora asumimos que el usuarioId de la app coincide con un Cliente)
            Cliente cliente = clienteRepo.findById(pedidoReq.getUsuarioId()).orElse(null);
            if(cliente == null) {
                // Si no existe, usamos el cliente ID 1 por defecto para que no falle la prueba
                cliente = clienteRepo.findById(1).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            }

            // 2. Buscamos un Documento de Identificación por defecto (ej. DNI ID 1)
            DocumentoIdentificacion doc = docRepo.findById(1).orElseThrow(() -> new RuntimeException("Documento no encontrado"));

            // 3. CREAMOS EL PEDIDO MAESTRO
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setCliente(cliente);
            nuevoPedido.setDocumento(doc);
            // Llenamos los campos obligatorios que definiste en Pedido.java
            nuevoPedido.setNombreReceptor("Cliente desde App Móvil"); 
            nuevoPedido.setFechaPedido(LocalDateTime.now());
            nuevoPedido.setDireccionEnvio("Compra gestionada vía Nexus App");
            nuevoPedido.setCiudad("Lima");
            nuevoPedido.setTelefonoContacto("999888777");
            nuevoPedido.setMetodoPago(MetodoPago.TARJETA); // O el método que prefieras
            nuevoPedido.setEstado(EstadoPedido.PENDIENTE);

            // Calculamos el Total sumando los detalles
            BigDecimal totalPedido = BigDecimal.ZERO;
            for(DetalleMovilRequest d : pedidoReq.getDetalles()) {
                BigDecimal subtotal = BigDecimal.valueOf(d.getPrecio()).multiply(BigDecimal.valueOf(d.getCantidad()));
                totalPedido = totalPedido.add(subtotal);
            }
            nuevoPedido.setTotal(totalPedido);

            // Guardamos el pedido maestro para generar su ID (idPedido)
            Pedido pedidoGuardado = pedidoRepo.save(nuevoPedido);

            // 4. CREAMOS LOS DETALLES Y DESCONTAMOS STOCK
            for(DetalleMovilRequest d : pedidoReq.getDetalles()) {
                // Buscamos el producto real en la BD
                Productos productoReal = productosRepo.findById(d.getProductoId()).orElseThrow();

                // Actualizamos el stock real en la nube
                productoReal.setStock(productoReal.getStock() - d.getCantidad());
                productosRepo.save(productoReal);

                // Creamos el detalle
                PedidoDetalle detalle = new PedidoDetalle();
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(productoReal);
                detalle.setCantidad(d.getCantidad());
                detalle.setPrecioUnitario(BigDecimal.valueOf(d.getPrecio()));

                // Guardamos el detalle
                detalleRepo.save(detalle);
            }

            // 5. ¡ÉXITO! Respondemos a la App Móvil
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Orden #" + pedidoGuardado.getIdPedido() + " generada con éxito");
            
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Error crítico en el servidor: " + e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }
    
 // --- CLASES AUXILIARES PARA LEER EL JSON MÓVIL ---
    public static class PedidoMovilRequest {
        private Integer usuarioId;
        private java.util.List<DetalleMovilRequest> detalles;

        public Integer getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
        public java.util.List<DetalleMovilRequest> getDetalles() { return detalles; }
        public void setDetalles(java.util.List<DetalleMovilRequest> detalles) { this.detalles = detalles; }
    }

    public static class DetalleMovilRequest {
        private Integer productoId;
        private Integer cantidad;
        private Double precio;

        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
    }
}