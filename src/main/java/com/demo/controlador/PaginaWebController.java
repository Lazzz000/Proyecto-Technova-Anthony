package com.demo.controlador;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import com.demo.modelo.Categoria;
import com.demo.modelo.Descuento;
import com.demo.modelo.Productos;
import com.demo.modelo.Usuario;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;
import com.demo.service.PromocionDescService;

@Controller
public class PaginaWebController {
		@Autowired
		UsuarioRepositorio usuariorepositorio;
		
		@Autowired
		ProductosService productoserService;
		
		@Autowired
		CategoriaService categoriaService;
		
		@Autowired
		PromocionDescService promocionService;
		
		
		@GetMapping({"/PaginaWeb/index"})
			public String mostrarIndex(Model model, Authentication au){
				
			List<Productos> productos= productoserService.listarActivas();
			
			 List<Productos> novedades = productos.stream()
		                .sorted((p1, p2) -> p2.getIdProducto().compareTo(p1.getIdProducto()))
		                .limit(15)
		                .collect(Collectors.toList());
			
			   if (au != null) { 
		            Usuario usu = usuariorepositorio.findByCorreo(au.getName());

		            if (usu != null) { 
		                model.addAttribute("nombreCom", usu.getNombreUsuario());
		            }
		        }
			   
			   List<Categoria> categorias = categoriaService.listarTodas();
			   
			   List<Descuento> productoDescuento= promocionService.descuentosCurso();
			   
			   
			   model.addAttribute("productoDescuento", productoDescuento);
			   model.addAttribute("productos", productos);
		        model.addAttribute("novedades", novedades);
		        model.addAttribute("categorias", categorias);
				return "PaginaWeb/index";
			}
		
			//Html Nosotros
		@GetMapping("/nosotros")
		public String mostrarNosotros(Model model, Authentication au) {

		    //  Mantener usuario logueado en navbar
		    if (au != null) {
		        Usuario usu = usuariorepositorio.findByCorreo(au.getName());
		        if (usu != null) {
		            model.addAttribute("nombreCom", usu.getNombreUsuario());
		        }
		    }

		    //  Categorías para el navbar
		    List<Categoria> categorias = categoriaService.listarTodas();
		    model.addAttribute("categorias", categorias);

		    //  Datos empresa ficticia
		    model.addAttribute("direccion", "Av. Wilson 123 - Lima, Perú");
		    model.addAttribute("telefono", "+51 987 654 321");
		    model.addAttribute("horario", "Lunes a Sábado 10:00am - 8:00pm");

		    return "PaginaWeb/nosotros";
		}
		
		@GetMapping("/contacto")
		public String mostrarContacto(Model model, Authentication au) {

		    if (au != null) {
		        Usuario usu = usuariorepositorio.findByCorreo(au.getName());
		        if (usu != null) {
		            model.addAttribute("nombreCom", usu.getNombreUsuario());
		        }
		    }

		    List<Categoria> categorias = categoriaService.listarTodas();
		    model.addAttribute("categorias", categorias);

		    return "PaginaWeb/contacto";
		}
}
