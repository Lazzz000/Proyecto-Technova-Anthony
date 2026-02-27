package com.demo.controlador;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.modelo.Categoria;
import com.demo.modelo.Cliente;
import com.demo.modelo.Pedido;
import com.demo.modelo.Usuario;
import com.demo.repositorio.PedidoRepositorio;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ClienteService;


@Controller
@RequestMapping("/perfil")
public class PerfilController {
		@Autowired
		private ClienteService clienteservice;
		
		@Autowired
		private UsuarioRepositorio usuarioRepositorio;
		
		@Autowired
		private PasswordEncoder passwordEncoder;
		
		@Autowired
		private PedidoRepositorio pedidoRepositorio;
		
		@Autowired
		CategoriaService categoriaService;
		
		@GetMapping
		public String verPerfil(Model model, Principal principal) {

		    String correo = principal.getName();

		    Usuario usuario = usuarioRepositorio.findByCorreo(correo);

		    if (usuario == null) {
		        return "redirect:/login";
		    }

		    Cliente cliente = clienteservice.buscarPorUsuario(usuario);
		    
		    List<Pedido> pedidos = pedidoRepositorio.findByClienteIdClienteOrderByFechaPedidoDesc(cliente.getIdCliente());
		    
		    pedidos.forEach(p -> p.getDetalles().size());
		    
		    List<Categoria> categorias = categoriaService.listarTodas();
		    
		    model.addAttribute("pedidos", pedidos);
		    model.addAttribute("usuario", usuario);
		    model.addAttribute("cliente", cliente);
		    model.addAttribute("categorias", categorias);
		    model.addAttribute("nombreCom", usuario.getNombreUsuario());

		    return "PaginaCliente/miPerfil";
		}
		
		@PostMapping("/guardar")
		public String guardarPerfil(Cliente clienteForm) {

		    clienteservice.actualizarCliente(clienteForm);

		    return "redirect:/perfil?ok";
		}
		
		@PostMapping("/cambiar-password")
		public String cambiarPassword(String actual,
		                              String nueva,
		                              Principal principal) {

		    Usuario usuario = usuarioRepositorio.findByCorreo(principal.getName());

		    // validar clave actual
		    if (!passwordEncoder.matches(actual, usuario.getClave())) {
		        return "redirect:/perfil?errorPass";
		    }

		    // guardar nueva clave encriptada
		    usuario.setClave(passwordEncoder.encode(nueva));

		    usuarioRepositorio.save(usuario);

		    return "redirect:/perfil?passOk";
		}
		
		@PostMapping("/desactivar")
		public String desactivarCuenta(Principal principal) {

		    String correo = principal.getName();

		    Usuario usuario = usuarioRepositorio.findByCorreo(correo);

		    Cliente cliente = clienteservice.buscarPorUsuario(usuario);

		    clienteservice.eliminarCliente(cliente.getIdCliente());

		    return "redirect:/logout";
		}
}
