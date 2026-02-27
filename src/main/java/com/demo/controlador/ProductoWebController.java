package com.demo.controlador;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.modelo.Categoria;
import com.demo.modelo.Descuento;
import com.demo.modelo.Productos;
import com.demo.modelo.Usuario;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;
import com.demo.service.PromocionDescService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/PaginaWeb")
public class ProductoWebController {
	
	@Autowired
	private UsuarioRepositorio usuariorepositorio;
    @Autowired
    private ProductosService productoservice;
    @Autowired
    private CategoriaService categoriaservice;
    
	@Autowired
	PromocionDescService promocionService;

    @GetMapping("/producto/detalle/{id}")
    public String detalleProducto(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {

        Productos producto = productoservice.buscarPorId(id);

        if (producto == null) {
            return "PaginaWeb/error";
        }

        // Convertir especificaciones JSON//
        Map<String, String> especificacionesMap = new LinkedHashMap<>();

        if (producto.getEspecificacionesTecnicas() != null &&
            !producto.getEspecificacionesTecnicas().isBlank()) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                especificacionesMap = mapper.readValue(
                        producto.getEspecificacionesTecnicas(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
                );
            } catch (Exception e) {
                System.out.println("Error leyendo especificaciones");
            }
        }

        // Construir nombre de categoría jerárquica//
        String categoriaNombre = "Sin categoría";

        if (producto.getCategoria() != null) {

            Categoria categoria = producto.getCategoria();

            if (categoria.getCategoriaPadre() != null) {
                categoriaNombre = categoria.getCategoriaPadre().getNombre_Categoria()
                        + " > "
                        + categoria.getNombre_Categoria();
            } else {
                categoriaNombre = categoria.getNombre_Categoria();
            }
        }

        //Usuario autenticado //
        if (authentication != null) {
            Usuario usuario = usuariorepositorio.findByCorreo(authentication.getName());
            if (usuario != null) {
                model.addAttribute("nombreCom", usuario.getNombreUsuario());
            }
        }
        
        List<Descuento> productoDescuento = promocionService.descuentosCurso();

        for (Descuento descuento : productoDescuento) {
            if (descuento.getProducto().getIdProducto()
                    .equals(producto.getIdProducto())) {

                model.addAttribute("descuento",
                        descuento.getPrecioConDescuento());

                model.addAttribute("porcentaje",
                        descuento.getPorcentajeDescuento());
                break;
            }
        }
        
        model.addAttribute("producto", producto);
        model.addAttribute("categoriaNombre", categoriaNombre);
        model.addAttribute("especificaciones", especificacionesMap);

        return "PaginaWeb/detalleProducto";
    }

}

