package com.demo.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name="Categorias")
@Builder
public class Categoria {
	@Id
	@GeneratedValue (strategy= GenerationType.IDENTITY)
	public Integer idCategoria;
	@Column(name = "nombre_Categoria") 
	private String nombreCategoria;
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name = "categoria_padre_id")
	private Categoria categoriaPadre;

	@JsonIgnore
	@OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
	private List<Categoria> subcategorias;
	
	
	public Categoria() {
		super();
	}
	

	public Categoria(Integer idCategoria, String nombreCategoria, String descripcion, Categoria categoriaPadre,
			List<Categoria> subcategorias) {
		super();
		this.idCategoria = idCategoria;
		this.nombreCategoria = nombreCategoria;
		this.descripcion = descripcion;
		this.categoriaPadre = categoriaPadre;
		this.subcategorias = subcategorias;
	}
	
	
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	public String getNombre_Categoria() {
		return nombreCategoria;
	}
	public void setNombre_Categoria(String nombre_Categoria) {
		this.nombreCategoria = nombre_Categoria;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNombreCategoria() {
		return nombreCategoria;
	}
	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}
	public Categoria getCategoriaPadre() {
		return categoriaPadre;
	}
	public void setCategoriaPadre(Categoria categoriaPadre) {
		this.categoriaPadre = categoriaPadre;
	}
	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}
	public void setSubcategorias(List<Categoria> subcategorias) {
		this.subcategorias = subcategorias;
	}


	
}
