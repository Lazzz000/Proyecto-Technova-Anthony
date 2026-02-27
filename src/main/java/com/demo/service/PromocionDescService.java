package com.demo.service;

import com.demo.modelo.Descuento;
import com.demo.modelo.Promocion;

import java.util.List;

public interface PromocionDescService {

    List<Promocion> listarPromociones();
    List<Descuento> listarDescuentos();

    void guardarPromocionDesc(Promocion promocion,List<Descuento> descuento);
    void editarDescuento(Descuento descuento);
    Descuento findByDescuento(int idDescuento);

    void eliminarDescuento(int idDescuento);
    void eliminarPromocion(int idPromocion);

    List<Descuento> descuentosCurso();

    List<Descuento> descuentosNoEnCurso();

    Promocion findByPromocion(int idPromo);

    void guardarPromocion(Promocion promocion);

    boolean existeNombrePromo(String nombrePromo);
}
