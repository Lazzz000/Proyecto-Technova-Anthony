package com.demo.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.demo.modelo.Categoria;
import com.demo.modelo.CheckoutDTO;
import com.demo.modelo.Descuento;
import com.demo.modelo.TipoDocumento;
import com.demo.modelo.Usuario;
import com.demo.repositorio.TipoDocumentoRepositorio;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CarritoService;
import com.demo.service.CategoriaService;
import com.demo.service.PromocionDescService;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final TipoDocumentoRepositorio tipoDocumentoRepositorio;
	@Autowired
	UsuarioRepositorio usuariorepositorio;
	@Autowired
	CategoriaService categoriaService;
	
	@Autowired
	PromocionDescService promocionService;
	
	


    @Autowired
    public CarritoController(CarritoService carritoService, TipoDocumentoRepositorio tipoDocumentoRepositorio) {
        this.carritoService = carritoService;
        this.tipoDocumentoRepositorio = tipoDocumentoRepositorio;
    }

    @GetMapping
    public String verCarrito(Model model, Authentication au) {

    	  List<Descuento> productoDescuento= promocionService.descuentosCurso();
    	  
    	  for (Descuento descuento : productoDescuento) {
			carritoService.aplicarDescuentos(descuento.getProducto().getIdProducto(), descuento.getPorcentajeDescuento() );
		}
    	
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setItems(new ArrayList<>(carritoService.obtenerItems()));
        checkoutDTO.setTotal(carritoService.calcularTotal());
        checkoutDTO.setTotalItems(carritoService.totalItems());

        List<TipoDocumento> tiposDocumento = tipoDocumentoRepositorio.findAll();
        
        List<Categoria> categorias = categoriaService.listarTodas();
       
        		
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("checkoutDTO", checkoutDTO);

        if (au != null && au.isAuthenticated()
                && !(au instanceof AnonymousAuthenticationToken)) {

            Usuario usu = usuariorepositorio.findByCorreo(au.getName());
            
            model.addAttribute("categorias", categorias);

            if (usu != null) {
                model.addAttribute("usuarioLogueado", usu);
                model.addAttribute("nombreCom", usu.getNombreUsuario());
            }
        }

        return "PaginaWeb/checkout";
    }
    @PostMapping("/agregar/{idProducto}")
    public String agregarProducto(@PathVariable Integer idProducto,
                                  @RequestParam(defaultValue = "1") int cantidad) {
        carritoService.agregarProducto(idProducto, cantidad);
        return "redirect:/carrito"; 
    }

    @PostMapping("/eliminar/{idProducto}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Integer idProducto) {
        carritoService.eliminarProducto(idProducto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer idProducto,
                                     @RequestParam int cantidad) {
        carritoService.actualizarCantidad(idProducto, cantidad);
        return "redirect:/carrito";
    }
}
