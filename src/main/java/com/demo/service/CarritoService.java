package com.demo.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.demo.modelo.CarritoItem;
import com.demo.modelo.Productos;

@Service
@SessionScope
public class CarritoService {

    private Map<Integer, CarritoItem> items = new HashMap<>();

    @Autowired
    private ProductosService productosService;

    public void agregarProducto(Integer idProducto, int cantidad) {
        Productos producto = productosService.buscarPorId(idProducto);

        if (producto == null || producto.getStock() <= 0) return;

        CarritoItem item = items.get(idProducto);

        if (item == null) {
            items.put(idProducto, new CarritoItem(producto, cantidad));
        } else {
            item.setCantidad(item.getCantidad() + cantidad);
        }
    }

    public void eliminarProducto(Integer idProducto) {
        items.remove(idProducto);
    }

    public void actualizarCantidad(Integer idProducto, int cantidad) {
        CarritoItem item = items.get(idProducto);
        if (item != null && cantidad > 0) {
            item.setCantidad(cantidad);
        }
    }

    public Collection<CarritoItem> obtenerItems() {
        return items.values();
    }

    public void limpiarCarrito() {
        items.clear();
    }

    public int totalItems() {
        return items.values()
                .stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    public BigDecimal calcularTotal() {
        return items.values()
                .stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void aplicarDescuentos(Integer idProducto, BigDecimal descuento) {
        items.replaceAll((clave, valor) -> {

            if (clave.equals(idProducto)) {

                BigDecimal precioActual = valor.getProducto().getPrecio();
                BigDecimal montoDescuento = precioActual.multiply(descuento);

                BigDecimal nuevoPrecio = precioActual
                        .subtract(montoDescuento)
                        .setScale(2, RoundingMode.HALF_UP);

                valor.getProducto().setPrecio(nuevoPrecio);
            }

            return valor;
        });
    }}