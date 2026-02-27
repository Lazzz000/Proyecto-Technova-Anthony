package com.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.modelo.Usuario;
import com.demo.repositorio.UsuarioRepositorio;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            System.out.println("❌ Usuario NO encontrado");
            throw new UsernameNotFoundException("Usuario no encontrado con correo: " + correo);
        }
  
        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getClave()) 
                .roles(usuario.getRol())      
                .build();
    }
}

