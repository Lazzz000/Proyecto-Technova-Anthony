package com.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.*;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Usuario findByCorreo(String correo);
    
}	
