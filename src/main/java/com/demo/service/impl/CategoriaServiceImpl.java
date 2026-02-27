package com.demo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.modelo.Categoria;
import com.demo.repositorio.CategoriasRepositorio;
import com.demo.service.CategoriaService;
@Service
public class CategoriaServiceImpl implements CategoriaService{
	
	 @Autowired
	    private CategoriasRepositorio categoriasRepositorio;

	
	@Override
	public List<Categoria> listarTodas() {
		
		return categoriasRepositorio.findAll();
	}

	@Override
	public Categoria buscarPorId(Integer id) {
		   return categoriasRepositorio.findById(id).orElse(null);
	}

	@Override
	public List<Categoria> listarCategoriasPadre() {
		
		return categoriasRepositorio.findByCategoriaPadreIsNull();
	}

	@Override
	public List<Categoria> listarSubcategorias(Integer idCategoria) {
		
		return categoriasRepositorio.findByCategoriaPadre_IdCategoria(idCategoria);
	}

	@Override
	public boolean esCategoriaPadre(Integer idCategoria) {
	    return categoriasRepositorio
	            .findByCategoriaPadre_IdCategoria(idCategoria)
	            .size() > 0;
	}
	
	@Override
	public List<Integer> obtenerIdsSubcategorias(Integer idPadre) {

	    return categoriasRepositorio
	            .findByCategoriaPadre_IdCategoria(idPadre)
	            .stream()
	            .map(Categoria::getIdCategoria)
	            .toList();
	}
	@Override
	public Integer obtenerCategoriaPadreId(Integer idCategoria) {

	    Categoria categoria = categoriasRepositorio
	            .findById(idCategoria)
	            .orElse(null);

	    if (categoria == null) {
	        return null;
	    }

	    // Si NO tiene padre → es categoría padre
	    if (categoria.getCategoriaPadre() == null) {
	        return categoria.getIdCategoria();
	    }

	    // Si tiene padre → devolver el ID del padre
	    return categoria.getCategoriaPadre().getIdCategoria();
	}
	
}
