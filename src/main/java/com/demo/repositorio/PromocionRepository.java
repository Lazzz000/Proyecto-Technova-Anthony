package com.demo.repositorio;

import com.demo.modelo.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion,Integer> {

    Optional<Promocion> findByNombrePromo(String promo);

    boolean existsByNombrePromo(String nombrePromo);
}
