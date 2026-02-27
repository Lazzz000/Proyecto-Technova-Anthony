package com.demo.modelo;

import java.time.LocalDate;

import com.demo.modelo.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="Clientes")
@Builder
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCliente;
	
	
	 @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "idUsuario")
	    private Usuario usuario;
	 	
	 	private String nombres;
	    private String apellidos;
	    private String genero; 
	    @Column(name = "fecha_nacimiento")
	    private java.time.LocalDate fechaNacimiento;
	    private String telefono;
	    @Column(length = 10, nullable = false)
	    private String estado = "Activo";
	    
	    public Integer getIdCliente() {
			return idCliente;
		}
		public void setIdCliente(Integer idCliente) {
			this.idCliente = idCliente;
		}
		public Usuario getUsuario() {
			return usuario;
		}
		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}
		public String getNombres() {
			return nombres;
		}
		public void setNombres(String nombres) {
			this.nombres = nombres;
		}
		public String getApellidos() {
			return apellidos;
		}
		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}
		public String getGenero() {
			return genero;
		}
		public void setGenero(String genero) {
			this.genero = genero;
		}
		public java.time.LocalDate getFechaNacimiento() {
			return fechaNacimiento;
		}
		public void setFechaNacimiento(java.time.LocalDate fechaNacimiento) {
			this.fechaNacimiento = fechaNacimiento;
		}
		public String getTelefono() {
			return telefono;
		}
		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public Cliente(Integer idCliente, Usuario usuario, String nombres, String apellidos, String genero,
				LocalDate fechaNacimiento, String telefono, String estado) {
			super();
			this.idCliente = idCliente;
			this.usuario = usuario;
			this.nombres = nombres;
			this.apellidos = apellidos;
			this.genero = genero;
			this.fechaNacimiento = fechaNacimiento;
			this.telefono = telefono;
			this.estado = estado;
		}
		public Cliente() {
			super();
		} 
	     
	 
}
