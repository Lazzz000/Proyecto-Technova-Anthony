package com.demo.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.modelo.Cliente;
import com.demo.modelo.Pedido;
import com.demo.service.ClienteService;
import com.demo.service.PedidoService;

@Controller
@RequestMapping("/Cliente")
public class ClienteController {
		@Autowired 
		private ClienteService clienteservice;
		
		@Autowired
		private PedidoService pedidoService;
		
		@GetMapping("/listar")
		public String listarClientes(Model model,@AuthenticationPrincipal UserDetails userDetails) {
			List<Cliente> listaClientes= clienteservice.listarTodos();
			model.addAttribute("listaCliente", listaClientes);
			model.addAttribute("adminNombre", userDetails.getUsername());
			model.addAttribute("totalClientes", clienteservice.clientesTodos());
		    model.addAttribute("clientesActivos", clienteservice.clientesActivos());
		    model.addAttribute("clientesInactivos", clienteservice.clientesInactivos());	
		     
				return "PaginaAdmin/listarClientes";
		}
		
		@GetMapping("/ver/{id}")
		public String verCliente(@PathVariable Integer id,
		                         Model model,
		                         @AuthenticationPrincipal UserDetails userDetails) {

		    Cliente cliente = clienteservice.buscarPorId(id);

		    List<Pedido> pedidos = pedidoService.listarPorCliente(id);

		    model.addAttribute("adminNombre", userDetails.getUsername());
		    model.addAttribute("cliente", cliente);
		    model.addAttribute("pedidos", pedidos);

		    return "PaginaAdmin/verCliente";
		}
		
		 @PostMapping("/activar/{id}")
		    public String activarCliente(@PathVariable Integer id) {
		        clienteservice.activarCliente(id);
		        return "redirect:/Cliente/listar";
		    }
		
		 @GetMapping("/activos")
		 public String listarActivos(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		     model.addAttribute("listaCliente", clienteservice.listarActivos());
		     model.addAttribute("admin", userDetails);
		     model.addAttribute("totalClientes", clienteservice.clientesTodos());
		     model.addAttribute("clientesActivos", clienteservice.clientesActivos());
		     model.addAttribute("clientesInactivos", clienteservice.clientesInactivos());
		     return "PaginaAdmin/listarClientes";
		 }

		 @GetMapping("/inactivos")
		 public String listarInactivos(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		     model.addAttribute("listaCliente", clienteservice.listarInactivos());
		     model.addAttribute("admin", userDetails);
		     model.addAttribute("totalClientes", clienteservice.clientesTodos());
		     model.addAttribute("clientesActivos", clienteservice.clientesActivos());
		     model.addAttribute("clientesInactivos", clienteservice.clientesInactivos());
		     return "PaginaAdmin/listarClientes";
		 }


	
}
