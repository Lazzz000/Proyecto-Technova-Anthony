package com.demo.dto;

import java.math.BigDecimal;
import com.demo.modelo.Productos;

public class ProductoPromoDTO {
	 private BigDecimal precioConDescuento; 
    private Productos producto;
    private BigDecimal porcentaje;

   public ProductoPromoDTO(Productos producto, BigDecimal porcentaje, BigDecimal precioConDescuento) {
        this.producto = producto;
        this.porcentaje = porcentaje;
        this.precioConDescuento = precioConDescuento;
    }


    public Productos getProducto() {
        return producto;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }
    public BigDecimal getPrecioConDescuento() { 
        return precioConDescuento;
    }
}
