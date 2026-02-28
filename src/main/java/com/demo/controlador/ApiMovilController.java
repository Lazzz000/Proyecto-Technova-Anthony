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

@RestController
@RequestMapping("/api/v1")
public class ApiMovilController {

    @Autowired
    private ProductosService productoService;

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
    
 // --- ENDPOINT PARA RECIBIR EL CHECKOUT MÓVIL ---
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> recibirPedidoMovil(@RequestBody PedidoMovilRequest pedido) {
        
        System.out.println("=========================================");
        System.out.println("¡ALERTA: PEDIDO RECIBIDO DESDE LA APP NEXUS!");
        System.out.println("Usuario ID: " + pedido.getUsuarioId());
        System.out.println("Cantidad de productos distintos: " + pedido.getDetalles().size());
        System.out.println("=========================================");

        // Preparamos la respuesta exitosa en formato JSON para Android
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "El servidor de TechNova recibió tu pedido correctamente.");
        
        return ResponseEntity.ok(respuesta);
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