package com.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarClaveBCrypt {
    public static void main(String[] args) {
        // Cambia esta contraseña por la que quieras encriptar
        String claveOriginal = "123456";

        // Crear el encriptador
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Encriptar
        String claveEncriptada = encoder.encode(claveOriginal);
        
        // Mostrar resultado
        System.out.println("🔑 Clave original: " + claveOriginal);
        System.out.println("🔒 Clave encriptada: " + claveEncriptada);
    }
}
