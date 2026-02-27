package com.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.Cliente;
import com.demo.modelo.Usuario;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
	 Cliente findByUsuario(Usuario usuario);
	 List<Cliente> findTop5ByOrderByIdClienteDesc();
	 List<Cliente> findByEstado(String estado);
	 long countByEstado(String estado);
	 
	
}