package com.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.modelo.Categoria;

@Repository
public interface CategoriasRepositorio extends JpaRepository<Categoria, Integer> {
	
    // Traer solo categorías principales (padre = null)
    List<Categoria> findByCategoriaPadreIsNull();

    // Traer subcategorías por id del padre
    List<Categoria> findByCategoriaPadre_IdCategoria(Integer idCategoria);

}
