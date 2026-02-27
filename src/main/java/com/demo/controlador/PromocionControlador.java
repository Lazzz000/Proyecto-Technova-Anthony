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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/mantenimientoPromociones")
public class PromocionControlador {

    private final PromocionDescService promocionDescService;

    @Autowired
    public PromocionControlador(PromocionDescService promocionDescService) {
        this.promocionDescService = promocionDescService;
    }
    @GetMapping("/listaPromociones")
    public String listaPromociones(
            Model model,
            @AuthenticationPrincipal UserDetails userdetails) {
        model.addAttribute("adminNombre", userdetails.getUsername());

        model.addAttribute("listaPromociones",
                promocionDescService.listarPromociones());


        return "/mantenimientoPromociones/lista-promociones";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<Void> guardarPromo(@RequestBody Promocion promocion
    ) {

        if(promocion.getIdPromocion()!=null){
            return ResponseEntity.badRequest().build();
        }
        if (promocionDescService.existeNombrePromo(promocion.getNombrePromo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        this.promocionDescService.guardarPromocion(promocion);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editar")
    @ResponseBody
    public ResponseEntity<Void> editarPromo(@RequestBody Promocion promocion
    ) {

        if (this.promocionDescService.findByPromocion(promocion.getIdPromocion())==null){
            return ResponseEntity.badRequest().build();
        }
        System.out.println("promocion123: "+promocion);
        this.promocionDescService.guardarPromocion(promocion);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ver/{id}")
    @ResponseBody
    public ResponseEntity<Promocion> verPromocion(@PathVariable("id") Integer id) {
        Promocion promocion = this.promocionDescService.findByPromocion(id);
        if(promocion==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(promocion);
    }


    @PostMapping("/eliminar/{id}")
    public String eliminarPromoDesc(@PathVariable("id") int id) {

        promocionDescService.eliminarPromocion(id);
        return "redirect:/mantenimientoPromociones/listaPromociones";
    }


}
