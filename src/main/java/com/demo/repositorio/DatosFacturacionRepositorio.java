package com.demo.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.DatosFacturacion;

@Repository
public interface DatosFacturacionRepositorio 
        extends JpaRepository<DatosFacturacion, Integer> {

    Optional<DatosFacturacion> findByNumeroDocumento(String numeroDocumento);
}
