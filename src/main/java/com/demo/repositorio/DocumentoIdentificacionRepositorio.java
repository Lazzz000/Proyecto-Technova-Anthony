package com.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.DocumentoIdentificacion;
import com.demo.modelo.TipoDocumento;

@Repository
public interface DocumentoIdentificacionRepositorio extends JpaRepository<DocumentoIdentificacion, Integer> {

    Optional<DocumentoIdentificacion> 
    findByTipoDocumento_IdTipoDocumentoAndNumeroDocumento(Integer idTipoDocumento, String numeroDocumento);
    
    Optional<DocumentoIdentificacion> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

}
