package com.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Productos")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProducto")
    private Integer idProducto;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 255)
    private String imagen;
    
    
    @Column(name = "fechaRegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "especificaciones_tecnicas", columnDefinition = "JSON")
    private String especificacionesTecnicas;

    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false)
    private Categoria categoria;

    @Column
    private String estado;

    // Constructor vacío 
    public Productos() {
    }



    public Productos(Integer idProducto, String nombre, String descripcion, BigDecimal precio, Integer stock,
			String imagen, LocalDateTime fechaRegistro, String especificacionesTecnicas, Categoria categoria,
			String estado) {
		super();
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.stock = stock;
		this.imagen = imagen;
		this.fechaRegistro = fechaRegistro;
		this.especificacionesTecnicas = especificacionesTecnicas;
		this.categoria = categoria;
		this.estado = estado;
	}

    // Getters y Setters

	public Integer getIdProducto() {
        return idProducto;
    }

 

	public String getEspecificacionesTecnicas() {
		return especificacionesTecnicas;
	}



	public void setEspecificacionesTecnicas(String especificacionesTecnicas) {
		this.especificacionesTecnicas = especificacionesTecnicas;
	}



	public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

  

    public Categoria getCategoria() {
		return categoria;
	}


	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}


	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}


	public String getEstado() {
        return estado;
    }

    public void setEstado(String string) {
        this.estado = string;
    }
}
