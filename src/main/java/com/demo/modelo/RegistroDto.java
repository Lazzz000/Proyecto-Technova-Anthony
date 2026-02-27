package com.demo.modelo;

import com.demo.modelo.Cliente;
import com.demo.modelo.Usuario;

public class RegistroDto {

	Usuario usuario;
	Cliente cliente;
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public RegistroDto(Usuario usuario, Cliente cliente) {
		super();
		this.usuario = usuario;
		this.cliente = cliente;
	}
	public RegistroDto() {
		super();
	}
	
	
}
