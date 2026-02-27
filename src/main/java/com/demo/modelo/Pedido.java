package com.demo.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.demo.modelo.enums.EstadoPedido;
import com.demo.modelo.enums.MetodoPago;

@Entity
@Table(name = "Pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedido")
    private Integer idPedido;

   
    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    
    @ManyToOne
    @JoinColumn(name = "idDocumento", nullable = false)
    private DocumentoIdentificacion documento;

    @Column(name = "nombre_Receptor", nullable = false, length = 100)
    private String nombreReceptor;

    @Column(name = "fecha_Pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "direccion_Envio", nullable = false, length = 255)
    private String direccionEnvio;

    @Column(length = 50)
    private String ciudad = "Lima";

    @Column(name = "telefono_Contacto", nullable = false, length = 15)
    private String telefonoContacto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Column(name = "ultimos4_tarjeta")
    private String ultimos4Tarjeta;

    @Column(name = "marca_tarjeta")
    private String marcaTarjeta;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoDetalle> detalles = new ArrayList<>();
    
    public List<PedidoDetalle> getDetalles() {
		return detalles;
	}



	public void setDetalles(List<PedidoDetalle> detalles) {
		this.detalles = detalles;
	}



	public String getUltimos4Tarjeta() {
		return ultimos4Tarjeta;
	}



	public void setUltimos4Tarjeta(String ultimos4Tarjeta) {
		this.ultimos4Tarjeta = ultimos4Tarjeta;
	}



	public String getMarcaTarjeta() {
		return marcaTarjeta;
	}



	public void setMarcaTarjeta(String marcaTarjeta) {
		this.marcaTarjeta = marcaTarjeta;
	}



	//  Constructor vacío 
    public Pedido() {
    }

 

	public Integer getIdPedido() {
		return idPedido;
	}

	public Pedido(Integer idPedido, Cliente cliente, DocumentoIdentificacion documento, String nombreReceptor,
			LocalDateTime fechaPedido, String direccionEnvio, String ciudad, String telefonoContacto,
			MetodoPago metodoPago, BigDecimal total, EstadoPedido estado) {
		super();
		this.idPedido = idPedido;
		this.cliente = cliente;
		this.documento = documento;
		this.nombreReceptor = nombreReceptor;
		this.fechaPedido = fechaPedido;
		this.direccionEnvio = direccionEnvio;
		this.ciudad = ciudad;
		this.telefonoContacto = telefonoContacto;
		this.metodoPago = metodoPago;
		this.total = total;
		this.estado = estado;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public DocumentoIdentificacion getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoIdentificacion documento) {
		this.documento = documento;
	}

	public String getNombreReceptor() {
		return nombreReceptor;
	}

	public void setNombreReceptor(String nombreReceptor) {
		this.nombreReceptor = nombreReceptor;
	}

	public LocalDateTime getFechaPedido() {
		return fechaPedido;
	}

	public void setFechaPedido(LocalDateTime fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	public String getDireccionEnvio() {
		return direccionEnvio;
	}

	public void setDireccionEnvio(String direccionEnvio) {
		this.direccionEnvio = direccionEnvio;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getTelefonoContacto() {
		return telefonoContacto;
	}

	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}

	public MetodoPago getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}

	
	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public EstadoPedido getEstado() {
		return estado;
	}

	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}
    
    
}
