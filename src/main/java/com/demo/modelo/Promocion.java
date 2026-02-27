package com.demo.modelo;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name="promocion")
@Data
@NoArgsConstructor
@ToString
public class Promocion {
	@Id
	@GeneratedValue (strategy= GenerationType.IDENTITY)
    @Column(name = "id_promocion")
	private Integer idPromocion;

	@Column(name = "nombre_promo",unique = true)
    private String nombrePromo;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deletedAt;

}
