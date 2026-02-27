package com.demo.controlador;

import com.demo.modelo.Cliente;
import com.demo.modelo.RegistroDto;
import com.demo.modelo.Usuario;
import com.demo.service.impl.ClienteServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioControlador {

    @Autowired
    private ClienteServiceImpl clienteService;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String mostrarRegistrarCliente(Model model) {
        // Evita errores de nullpointer en Thymeleaf
        RegistroDto registroDto = new RegistroDto();
        registroDto.setCliente(new Cliente());
        registroDto.setUsuario(new Usuario());
        model.addAttribute("registroDto", registroDto);

        return "Registro/registrar"; // ruta de tu HTML
    }

    // Procesar formulario de registro
    @PostMapping("/register")
    public String registrarCliente(@ModelAttribute("registroDto") RegistroDto registroDto, Model model) {

        try {
            Usuario usuario = registroDto.getUsuario();
            Cliente cliente = registroDto.getCliente();

            // Registrar ambos (usuario + cliente)
            clienteService.registrarUsuarioCliente(usuario, cliente);

            model.addAttribute("success", "Registro exitoso. Ya puedes iniciar sesión.");
            // Limpiar los campos del formulario
            RegistroDto nuevoRegistro = new RegistroDto();
            nuevoRegistro.setCliente(new Cliente());
            nuevoRegistro.setUsuario(new Usuario());
            model.addAttribute("registroDto", nuevoRegistro);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al registrar el cliente: " + e.getMessage());
        }

        return "Registro/registrar";
    }

}
