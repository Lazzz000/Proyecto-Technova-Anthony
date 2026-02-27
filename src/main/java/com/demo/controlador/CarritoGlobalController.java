package com.demo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.demo.service.CarritoService;

@ControllerAdvice
public class CarritoGlobalController {

    @Autowired
    private CarritoService carritoService;

    @ModelAttribute("carritoCount")
    public int obtenerCantidadCarrito() {
        return carritoService.totalItems();
    }
}
