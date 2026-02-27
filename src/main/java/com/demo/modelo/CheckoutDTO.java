package com.demo.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.demo.modelo.CarritoItem;
import com.demo.modelo.enums.MetodoPago;

public class CheckoutDTO {

    // Step 1: Resumen del carrito
    private List<CarritoItem> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO.setScale(2);
    private int totalItems;

    // Step 2: Datos del receptor y envío
    private String nombreReceptor;
    private String direccionEnvio;
    private String ciudad = "Lima";     
    private String telefonoContacto;
    private MetodoPago metodoPago;

    // Step 3: Datos de documento
    private Integer idTipoDocumento;    
    private String numeroDocumento;   
    private String nombreTitular;       

    
    private String ultimos4Tarjeta;
    private String marcaTarjeta;
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

	// Constructores
    public CheckoutDTO() {}

    // 🔹 Getters y Setters
    public List<CarritoItem> getItems() { return items; }
    public void setItems(List<CarritoItem> items) { 
        this.items = items; 
        recalcularTotales(); 
    }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public Integer getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(Integer idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    // Métodos de ayuda
    public void agregarItem(CarritoItem item) {
        this.items.add(item);
        recalcularTotales();
    }

    public void eliminarItem(CarritoItem item) {
        this.items.remove(item);
        recalcularTotales();
    }

    private void recalcularTotales() {
        this.total = items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
        this.totalItems = items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    public void vaciar() {
        items.clear();
        total = BigDecimal.ZERO.setScale(2);
        totalItems = 0;
        nombreReceptor = null;
        direccionEnvio = null;
        ciudad = "Lima";
        telefonoContacto = null;
        metodoPago = null;
        idTipoDocumento = null;
        numeroDocumento = null;
        nombreTitular = null;
    }
}
