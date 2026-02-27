package com.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.dto.ProductoPromoDTO;
import com.demo.modelo.Productos;

public interface ProductosService {
	
	List<Productos> listarTodos();
	Productos guardar(Productos producto);
	Productos buscarPorId (Integer id);
	void eliminar (Integer id);
	List <Productos> buscarPorCategoria(List<Integer> idsCategorias);
	 List<Productos> listarActivas();
	void cambiarEstado(Integer id);
	
	//contar inactivos y activos
	
	long contarActivos();
	long contarInactivos();
	long contarTotal();
	
	List<Productos> listarPorEstado(String estado);
	
	//Contar cuantas veces se vendio el producto
	
	long contarVecesVendido(Integer idProducto);

	
	List<Productos> filtrarPorCategoria(Integer idCategoria);
	
	//Filtrar Producto.
	public Page<Productos> filtrar(Integer categoria, String buscar, Pageable pageable);
	
	  List<Productos> buscarPorNombre(String nombre);
	  
	  Map<String, List<ProductoPromoDTO>> obtenerProductosConPromocion();
	  
	  List<Productos> obtenerProductosPorPromocion(String nombrePromo);
	  
	  Page<Productos> obtenerProductosPorPromocion(String nombrePromo, Pageable pageable);
}
