package com.demo.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "descuentos")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descuento")
    private int idDescuento;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "fecha_inicio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "id_promocion", nullable = false)
    private Promocion promocion;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Productos producto;
    
    public BigDecimal getPrecioConDescuento() {
        if (producto == null || porcentajeDescuento == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal precio = producto.getPrecio();

        BigDecimal descuento = precio.multiply(porcentajeDescuento);

        return precio
                .subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);
    }

}
