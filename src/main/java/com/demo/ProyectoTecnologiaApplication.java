package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class ProyectoTecnologiaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoTecnologiaApplication.class, args);
        System.out.println("✅ Proyecto Tecnologia iniciado correctamente...");
    }
    
}	
