package com.demo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.modelo.Mensaje;
import com.demo.repositorio.MensajeRepositorio;

@Controller
public class MensajeController {

    @Autowired
    private MensajeRepositorio mensajeRepositorio;

    @PostMapping("/enviarMensaje")
    public String enviarMensaje(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String mensaje) {

        Mensaje nuevo = new Mensaje();
        nuevo.setNombre(nombre);
        nuevo.setEmail(email);
        nuevo.setMensaje(mensaje);

        mensajeRepositorio.save(nuevo);

        return "redirect:/contacto?enviado";
    }
}
