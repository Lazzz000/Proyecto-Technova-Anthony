package com.demo.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.demo.dto.ProductoPromoDTO;
import com.demo.modelo.Productos;
import com.demo.modelo.Usuario;
import com.demo.repositorio.DescuentoRepositorio;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;
@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

	@Autowired
	CategoriaService categoriaService;
	@Autowired
	ProductosService productoService;
	@Autowired
	UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	private DescuentoRepositorio descuentoRepositorio;
	 @GetMapping
	    public String mostrarCatalogo(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "12") int size,
	            @RequestParam(required = false) Integer categoria,
	            @RequestParam(required = false) String buscar,
	            @RequestParam(required = false) String ordenar,
	            @RequestParam(required = false) String promo, // 🔥 NUEVO
	            Model model,
	            Authentication au) {

	        //  ORDENAMIENTO
	        Sort sort = Sort.by("idProducto").descending();

	        if ("precioAsc".equals(ordenar)) {
	            sort = Sort.by("precio").ascending();
	        } else if ("precioDesc".equals(ordenar)) {
	            sort = Sort.by("precio").descending();
	        }

	        Pageable pageable = PageRequest.of(page, size, sort);

	     // Verificar si hay promoción activa
	        if (promo != null && !promo.isEmpty()) {

	            // Crear pageable igual que en el flujo normal
	            Page<Productos> productosPage = productoService.obtenerProductosPorPromocion(promo, pageable);

	            model.addAttribute("productos", productosPage.getContent());
	            model.addAttribute("currentPage", productosPage.getNumber());
	            model.addAttribute("totalPages", productosPage.getTotalPages());
	            model.addAttribute("promoActiva", promo);
	            model.addAttribute("modoPromocion", true);

	        } else {
	            // Flujo normal con paginación
	            Page<Productos> productosPage = productoService.filtrar(categoria, buscar, pageable);

	            model.addAttribute("productos", productosPage.getContent());
	            model.addAttribute("currentPage", productosPage.getNumber());
	            model.addAttribute("totalPages", productosPage.getTotalPages());
	            model.addAttribute("modoPromocion", false);
	        }

	        //  Usuario autenticado
	        if (au != null) {
	            Usuario usu = usuarioRepositorio.findByCorreo(au.getName());
	            if (usu != null) {
	                model.addAttribute("nombreCom", usu.getNombreUsuario());
	            }
	        }

	        //  Categorías padre
	        model.addAttribute("categorias",
	                categoriaService.listarCategoriasPadre());

	        Integer categoriaPadreSeleccionada = null;

	        if (categoria != null) {

	            categoriaPadreSeleccionada =
	                    categoriaService.obtenerCategoriaPadreId(categoria);

	            model.addAttribute("subcategorias",
	                    categoriaService.listarSubcategorias(categoriaPadreSeleccionada));
	        }

	        model.addAttribute("categoriaPadreSeleccionada", categoriaPadreSeleccionada);

	        //  Promociones (para mostrar preview en catálogo/home)
	        Map<String, List<ProductoPromoDTO>> promociones =
	                productoService.obtenerProductosConPromocion();
	        
	        model.addAttribute("listaPromociones",
	                descuentoRepositorio.obtenerNombresPromocionesActivas());
	        model.addAttribute("promociones", promociones);

	        model.addAttribute("categoriaSeleccionada", categoria);
	     
	        model.addAttribute("buscar", buscar);
	        model.addAttribute("ordenar", ordenar);

	        return "PaginaWeb/catalogo";
	    }
	 
}

