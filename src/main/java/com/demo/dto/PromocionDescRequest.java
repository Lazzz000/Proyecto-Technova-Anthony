package com.demo.dto;

import com.demo.modelo.Descuento;
import com.demo.modelo.Promocion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PromocionDescRequest {
    Promocion promocion;
    List<Descuento> descuentos;
}
