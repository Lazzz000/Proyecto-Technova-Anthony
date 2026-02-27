package com.demo.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.modelo.Mensaje;
import com.demo.repositorio.MensajeRepositorio;
import com.demo.service.PedidoService;
import com.demo.service.ProductosService;
import com.demo.service.impl.ClienteServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping ("/PaginaAdmin")
public class AdminController {
	
	@Autowired
	private ProductosService productosService;
	@Autowired
	private PedidoService pedidoService;
	@Autowired
	private ClienteServiceImpl clienteSevice;
	@Autowired
	private MensajeRepositorio mensajeRepositorio;
	
	@GetMapping("/PanelAdmin")
	public String panelAdmin(HttpServletRequest request, Model model, @AuthenticationPrincipal UserDetails userdetails) {
		try {
			int totalProductos =(productosService.listarActivas()!=null)? productosService.listarActivas().size():0;
			int totalClientes= (clienteSevice.listarActivos()!=null)? clienteSevice.listarActivos().size():0;
			int totalPedidos= (pedidoService.listarTodos()!=null) ? pedidoService.listarTodos().size():0;
			double ingresosmes= pedidoService.ingresosDelMes();
			  long mensajesNoLeidos = mensajeRepositorio.countByLeidoFalse();
			
			model.addAttribute("totalProductos" , totalProductos);
			model.addAttribute("totalClientes", totalClientes);
			model.addAttribute("totalPedidos", totalPedidos);
			model.addAttribute("ultimosPedidos",pedidoService.listarUltimos(5));
			model.addAttribute("ingresosMes", ingresosmes);
			model.addAttribute("adminNombre", userdetails.getUsername());
			model.addAttribute("ultimosClientes", clienteSevice.ultimosClientes());
			  model.addAttribute("cantidadMensajesNoLeidos", mensajesNoLeidos);
		} catch (Exception e) {
			model.addAttribute("totalproductos",0);
			model.addAttribute("totalclientes",0);
			model.addAttribute("totalpedidos", 0);			
			model.addAttribute("ingresosMes",0);
			model.addAttribute("currentUri","");
			model.addAttribute("adminNombre", "Admin");
			model.addAttribute("cantidadMensajesNoLeidos", 0);
			model.addAttribute("ultimosPedidos", List.of());
			model.addAttribute("ultimosclientes", List.of());
		}
		return "PaginaAdmin/PanelAdmin";
	}
		
	  @GetMapping("/mensajes")
	    public String verMensajes(Model model,
	                              @AuthenticationPrincipal UserDetails userdetails) {

	        List<Mensaje> mensajes = mensajeRepositorio.findAllByOrderByFechaDesc();
	        long mensajesNoLeidos = mensajeRepositorio.countByLeidoFalse();

	        model.addAttribute("mensajes", mensajes);
	        model.addAttribute("cantidadMensajesNoLeidos", mensajesNoLeidos);

	        if (userdetails != null) {
	            model.addAttribute("adminNombre", userdetails.getUsername());
	        } else {
	            model.addAttribute("adminNombre", "Admin");
	        }

	        return "PaginaAdmin/mensajes";
	    }
	
	  @GetMapping("/mensajes/leido/{id}")
	    public String marcarComoLeido(@PathVariable Long id) {

	        Mensaje mensaje = mensajeRepositorio.findById(id).orElse(null);

	        if (mensaje != null) {
	            mensaje.setLeido(true);
	            mensajeRepositorio.save(mensaje);
	        }

	        return "redirect:/PaginaAdmin/mensajes";
	    }
}
