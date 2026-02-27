package com.demo.controlador;

import com.demo.modelo.Categoria;
import com.demo.modelo.Productos;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}