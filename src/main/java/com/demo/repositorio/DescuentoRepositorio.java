package com.demo.repositorio;

import com.demo.modelo.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescuentoRepositorio extends JpaRepository<Descuento, Integer> {

    @Query("""
        SELECT d
        FROM Descuento d
        JOIN d.promocion p
        WHERE d.deletedAt IS NULL AND p.deletedAt IS NULL
            AND (d.fechaInicio <= CURRENT_DATE AND d.fechaFin >= CURRENT_DATE)
      """)
    List<Descuento> descuentosCurso();

    @Query("""
        SELECT d
        FROM Descuento d
        JOIN d.promocion p
        WHERE d.deletedAt IS NULL AND p.deletedAt IS NULL
          AND (d.fechaInicio > CURRENT_DATE
               OR d.fechaFin < CURRENT_DATE)
    """)
    List<Descuento> descuentosNoEnCurso();

    List<Descuento> findByDeletedAtIsNullAndPromocionDeletedAtIsNull();
    

    @Query("""
        SELECT d FROM Descuento d
        WHERE d.deletedAt IS NULL
        AND d.fechaInicio <= CURRENT_TIMESTAMP
        AND d.fechaFin >= CURRENT_TIMESTAMP
    """)
    List<Descuento> findDescuentosActivos();
    
    @Query("""
    	    SELECT DISTINCT p.nombrePromo
    	    FROM Descuento d
    	    JOIN d.promocion p
    	    WHERE d.deletedAt IS NULL
    	      AND p.deletedAt IS NULL
    	      AND d.fechaInicio <= CURRENT_TIMESTAMP
    	      AND d.fechaFin >= CURRENT_TIMESTAMP
    	""")
    	List<String> obtenerNombresPromocionesActivas();
}
