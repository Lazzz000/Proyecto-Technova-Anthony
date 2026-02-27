package com.demo.service;

import java.util.List;

import com.demo.modelo.Cliente;
import com.demo.modelo.Usuario;

public interface ClienteService {
	List<Cliente> listarActivos();
	List<Cliente> listarInactivos();
	List<Cliente> listarTodos();
	long contarClientes();
	void registrarUsuarioCliente(Usuario usu, Cliente cli);
	boolean actualizarCliente(Cliente clienteActualizado);
	boolean eliminarCliente(Integer idCliente);
	List<Cliente> ultimosClientes();
	
	Cliente buscarPorUsuario(Usuario usuario);
	Cliente buscarPorId(Integer id);
	
	//Contadores
	long clientesActivos();
	long clientesInactivos();
	long clientesTodos();
	
	//Activar y desactivar
	void activarCliente(Integer id);
	void desactivarPorUsuario(Usuario usuario);
	void desactivarCliente(Integer id);
	

	
}
