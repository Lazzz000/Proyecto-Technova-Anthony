package com.demo.controlador;

import com.demo.dto.PromocionDescRequest;
import com.demo.modelo.Descuento;
import com.demo.modelo.Promocion;
import com.demo.service.PromocionDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/mantenimientoDescuentos")
public class DescuentoControlador {



    private final PromocionDescService promocionDescService;

    @Autowired
    public DescuentoControlador(PromocionDescService promocionDescService) {
        this.promocionDescService = promocionDescService;
    }


    @GetMapping("/listaDescuentos")
    public String listaDescuentos(
            Model model,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String promocion,
            @AuthenticationPrincipal UserDetails userdetails) {

        model.addAttribute("adminNombre", userdetails.getUsername());
        model.addAttribute("listapromociones",
                promocionDescService.listarPromociones());


        List<Descuento> lista = "en-curso".equals(estado) ? promocionDescService.descuentosCurso():
                "no-en-curso".equals(estado) ? promocionDescService.descuentosNoEnCurso(): promocionDescService.listarDescuentos();
//        if ("en-curso".equals(estado)) {
//            lista = promocionDescService.descuentosCurso();
//        } else if ("no-en-curso".equals(estado)) {
//            lista = promocionDescService.descuentosNoEnCurso();
//        } else {
//            lista = promocionDescService.listarDescuentos();
//        }

        if(promocion != null && !promocion.isEmpty() && !promocion.equals("Todos")){
            lista = lista.stream().filter(d->d.getPromocion().getNombrePromo().equals(promocion)).toList();
        }
        model.addAttribute("listaDescuentos", lista);

        model.addAttribute("descuentosCurso",
                promocionDescService.descuentosCurso().size());
        model.addAttribute("descuentosTotal",
                promocionDescService.listarDescuentos().size());

        return "/mantenimientoDescuentos/lista-descuentos";
    }


	//Guardar o actualizar Producto
	@PostMapping ("/guardar")
    @ResponseBody
	public ResponseEntity<String> guardarPromoDesc(@RequestBody PromocionDescRequest promocionDescRequest) {

        System.out.println(promocionDescRequest);
		try {
            promocionDescService.guardarPromocionDesc(promocionDescRequest.getPromocion(),promocionDescRequest.getDescuentos());


            return ResponseEntity.ok("Se guardo correctamente");
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}



    //Editar Producto
    @GetMapping(value="/editar/{id}")
    public String editarPromoDesc(@PathVariable("id") Integer id, Model model) {
        Descuento descuento= promocionDescService.findByDescuento(id);

        model.addAttribute("descuento",descuento);
        model.addAttribute("promociones", promocionDescService.listarPromociones());
        return "/mantenimientoDescuentos/form-descuento";
    }

    @PutMapping("/editar")
    public String editarPromoDesc(
            @ModelAttribute Descuento descuento,
            @RequestParam BigDecimal porcentajeDescuento
    ) {

        descuento.setPorcentajeDescuento(
                porcentajeDescuento.divide(BigDecimal.valueOf(100))
        );

        promocionDescService.editarDescuento(descuento);

        return "redirect:/mantenimientoDescuentos/listaDescuentos";
    }
    @GetMapping("/ver/{id}")
    public String verDescuento(@PathVariable("id") Integer id, Model model) {
        Descuento descuento= promocionDescService.findByDescuento(id);
        model.addAttribute("descuento",descuento);
        model.addAttribute("promociones", promocionDescService.listarPromociones());
        System.out.println("descuento11: "+descuento);
        return "mantenimientoDescuentos/ver-descuento";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminarPromoDesc(@PathVariable("id") int id) {

        promocionDescService.eliminarDescuento(id);
        return "redirect:/mantenimientoDescuentos/listaDescuentos";
    }


    @GetMapping("/promociones")
    @ResponseBody
    public List<Promocion> listaPromociones() {
        return promocionDescService.listarPromociones();
    }



}
