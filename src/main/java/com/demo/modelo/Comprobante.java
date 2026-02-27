package com.demo.modelo;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.demo.modelo.enums.TipoComprobante;

import jakarta.persistence.*;

@Entity
@Table(
    name = "Comprobante",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"serie", "numero"})
    }
)
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "serie", length = 4, nullable = false)
    private String serie;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    //  Relación con Pedido
    @ManyToOne
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    //  Relación con DatosFacturacion
    @ManyToOne
    @JoinColumn(name = "id_datos_facturacion", nullable = false)
    private DatosFacturacion datosFacturacion;

    //  Constructor vacío
    public Comprobante() {}

    // Constructor lleno
    public Comprobante(Integer idComprobante, TipoComprobante tipoComprobante,
                       LocalDateTime fechaEmision, String serie, Integer numero,
                       BigDecimal total, Pedido pedido, DatosFacturacion datosFacturacion) {
        this.idComprobante = idComprobante;
        this.tipoComprobante = tipoComprobante;
        this.fechaEmision = fechaEmision;
        this.serie = serie;
        this.numero = numero;
        this.total = total;
        this.pedido = pedido;
        this.datosFacturacion = datosFacturacion;
    }

	public Integer getIdComprobante() {
		return idComprobante;
	}

	public void setIdComprobante(Integer idComprobante) {
		this.idComprobante = idComprobante;
	}

	public TipoComprobante getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(TipoComprobante tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	public LocalDateTime getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDateTime fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public DatosFacturacion getDatosFacturacion() {
		return datosFacturacion;
	}

	public void setDatosFacturacion(DatosFacturacion datosFacturacion) {
		this.datosFacturacion = datosFacturacion;
	}

    
    
}
