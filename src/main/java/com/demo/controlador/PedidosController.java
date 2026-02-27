package com.demo.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.demo.modelo.Pedido;
import com.demo.repositorio.MensajeRepositorio;
import com.demo.service.PedidoService;

@Controller
@RequestMapping("/admin/pedidos")
public class PedidosController {

    @Autowired
    private PedidoService pedidoService;
    
	@Autowired
	private MensajeRepositorio mensajeRepositorio;

    @GetMapping
    public String listarPedidos(Model model,@AuthenticationPrincipal UserDetails userdetails) {

        List<Pedido> pedidos = pedidoService.listarTodos();
        long mensajesNoLeidos = mensajeRepositorio.countByLeidoFalse();
        model.addAttribute("pedidos", pedidos);

    	model.addAttribute("adminNombre", userdetails.getUsername());
    	model.addAttribute("cantidadMensajesNoLeidos", mensajesNoLeidos);
        return "PaginaAdmin/verPedidos";
    }

    @GetMapping("/ver/{id}")
    public String verPedido(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userdetails) {

        Pedido pedido = pedidoService.buscarPorId(id);

        long mensajesNoLeidos = mensajeRepositorio.countByLeidoFalse();	
        model.addAttribute("pedido", pedido);
    	model.addAttribute("adminNombre", userdetails.getUsername());
    	model.addAttribute("cantidadMensajesNoLeidos", mensajesNoLeidos);

        return "PaginaAdmin/verPedidosDetalle";
    }

    @GetMapping("/buscar")
    public String buscarPorFechas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            Model model) {

        List<Pedido> pedidos;

        if (fechaInicio != null && fechaFin != null &&
            !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {

            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            pedidos = pedidoService.buscarPorRangoFechas(inicio, fin);
        } else {
            pedidos = pedidoService.listarTodos();
        }

        model.addAttribute("pedidos", pedidos);

        return "PaginaAdmin/verPedidos";
    }
}
