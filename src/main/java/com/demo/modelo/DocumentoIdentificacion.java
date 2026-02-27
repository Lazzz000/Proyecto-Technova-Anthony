package com.demo.modelo;

import jakarta.persistence.*;

@Entity
@Table(
    name = "DocumentoIdentificacion",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idTipoDocumento", "numero_Documento"})
    }
)
public class DocumentoIdentificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDocumento")
    private Integer idDocumento;

    @ManyToOne
    @JoinColumn(name = "idTipoDocumento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_Documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "nombre_Titular", nullable = false, length = 150)
    private String nombreTitular;
    // Constructor vacío
    public DocumentoIdentificacion() {}

    // Constructor lleno
    public DocumentoIdentificacion(Integer idDocumento, TipoDocumento tipoDocumento,
                                   String numeroDocumento, String nombreTitular) {
        this.idDocumento = idDocumento;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombreTitular = nombreTitular;
    }

	public Integer getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Integer idDocumento) {
		this.idDocumento = idDocumento;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getNombreTitular() {
		return nombreTitular;
	}

	public void setNombreTitular(String nombreTitular) {
		this.nombreTitular = nombreTitular;
	}

    
    
}
