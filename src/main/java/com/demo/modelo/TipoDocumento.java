package com.demo.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table (name="tipodocumento")
@Builder
public class TipoDocumento {
@Id
@GeneratedValue (strategy= GenerationType.IDENTITY)
public Integer idTipoDocumento;

public String nombre;
public String sigla;
public TipoDocumento(Integer idTipoDocumento, String nombre, String sigla) {
	super();
	this.idTipoDocumento = idTipoDocumento;
	this.nombre = nombre;
	this.sigla = sigla;
}
public TipoDocumento() {
	super();
}
public Integer getIdTipoDocumento() {
	return idTipoDocumento;
}
public void setIdTipoDocumento(Integer idTipoDocumento) {
	this.idTipoDocumento = idTipoDocumento;
}
public String getNombre() {
	return nombre;
}
public void setNombre(String nombre) {
	this.nombre = nombre;
}
public String getSigla() {
	return sigla;
}
public void setSigla(String sigla) {
	this.sigla = sigla;
}


	
}
