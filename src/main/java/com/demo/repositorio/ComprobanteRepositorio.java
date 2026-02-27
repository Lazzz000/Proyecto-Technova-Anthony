package com.demo.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.modelo.Comprobante;

@Repository
public interface ComprobanteRepositorio 
        extends JpaRepository<Comprobante, Integer> {

    Optional<Comprobante> findBySerieAndNumero(String serie, Integer numero);
    Comprobante findByPedido_IdPedido(Integer idPedido);
    

    @Query("SELECT MAX(c.numero) FROM Comprobante c WHERE c.serie = :serie")
    Integer obtenerUltimoNumeroPorSerie(@Param("serie") String serie);
    
    @Query("SELECT MAX(c.numero) FROM Comprobante c WHERE c.serie = :serie")
    Integer findMaxNumeroBySerie(@Param("serie") String serie);
    
    @Query("SELECT MAX(c.numero) FROM Comprobante c")
    Integer obtenerUltimoNumero();
}
