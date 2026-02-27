package com.demo.service;

import java.util.List;
import com.demo.modelo.Categoria;

public interface CategoriaService {

    //para formularios
    List<Categoria> listarTodas();   
    
    Categoria buscarPorId(Integer id);
    
    List<Categoria> listarCategoriasPadre();

    List<Categoria> listarSubcategorias(Integer idCategoria);
    
     boolean esCategoriaPadre(Integer idCategoria) ;
     
     List<Integer> obtenerIdsSubcategorias(Integer idPadre);

     Integer obtenerCategoriaPadreId(Integer idCategoria);
}	
