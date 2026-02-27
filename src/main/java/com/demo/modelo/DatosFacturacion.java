package com.demo.modelo;

import com.demo.modelo.enums.TipoDocumentoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DatosFacturacion")
public class DatosFacturacion {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_datos_facturacion")
	    private Integer idDatosFacturacion;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "tipo_documento", nullable = false)
	    private TipoDocumentoEnum tipoDocumento;

	    @Column(name = "numero_documento", nullable = false, length = 20)
	    private String numeroDocumento;

	    @Column(name = "nombre_razon_social", nullable = false, length = 150)
	    private String nombreRazonSocial;

    private String direccion;
    private String telefono;
    private String correo;
    
	public DatosFacturacion() {
		super();
	}
	
	
	public Integer getIdDatosFacturacion() {
		return idDatosFacturacion;
	}
	public void setIdDatosFacturacion(Integer idDatosFacturacion) {
		this.idDatosFacturacion = idDatosFacturacion;
	}
	
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	public String getNombreRazonSocial() {
		return nombreRazonSocial;
	}
	public void setNombreRazonSocial(String nombreRazonSocial) {
		this.nombreRazonSocial = nombreRazonSocial;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public TipoDocumentoEnum getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoEnum tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}


	public DatosFacturacion(Integer idDatosFacturacion, TipoDocumentoEnum tipoDocumento, String numeroDocumento,
			String nombreRazonSocial, String direccion, String telefono, String correo) {
		super();
		this.idDatosFacturacion = idDatosFacturacion;
		this.tipoDocumento = tipoDocumento;
		this.numeroDocumento = numeroDocumento;
		this.nombreRazonSocial = nombreRazonSocial;
		this.direccion = direccion;
		this.telefono = telefono;
		this.correo = correo;
	}
	
	
	
}
