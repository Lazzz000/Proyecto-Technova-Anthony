package com.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dto.ProductoPromoDTO;
import com.demo.modelo.Descuento;
import com.demo.modelo.Productos;
import com.demo.repositorio.DescuentoRepositorio;
import com.demo.repositorio.ProductosRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
@Service
public class ProductosServiceImpl implements ProductosService{
	  @Autowired
	    private ProductosRepositorio productosRepositorio;
	  
	  @Autowired
	  private CategoriaService categoriaService;
	  
	  @Autowired
	  private DescuentoRepositorio descuentoRepository;

	
	@Override
	public List<Productos> listarTodos() {
		
		return productosRepositorio.findAll();
	}

	@Override
	public Productos guardar(Productos producto) {
		
		return	productosRepositorio.save(producto);
	}

	@Override
	public Productos buscarPorId(Integer id) {
		
		return productosRepositorio.findById(id).orElse(null);
	}

	@Override
	public void eliminar(Integer id) {
		 productosRepositorio.deleteById(id);
		
	}

	@Override
	public List<Productos> buscarPorCategoria(List<Integer> idsCategorias) {
		
		return productosRepositorio.findByCategoria_IdCategoriaIn(idsCategorias);
	}

	@Override
	public List<Productos> listarActivas() {
		
		return productosRepositorio.findByEstado("ACTIVO");
	}

	@Override
	public void cambiarEstado(Integer id) {
	    Productos producto = buscarPorId(id);
	    if (producto != null) {
	        producto.setEstado(
	            producto.getEstado().equals("ACTIVO") ? "INACTIVO" : "ACTIVO"
	        );
	        guardar(producto);
	    }
		
	}

	@Override
	public long contarActivos() {
		
		return productosRepositorio.countByEstado("ACTIVO");
	}

	@Override
	public long contarInactivos() {
		return productosRepositorio.countByEstado("INACTIVO");
	}

	public long contarTotal() {
		
		return productosRepositorio.count();
	}

	@Override
	public List<Productos> listarPorEstado(String estado) {
		return productosRepositorio.findByEstado(estado);
	}

	@Override
	public long contarVecesVendido(Integer idProducto) {
	    return productosRepositorio.contarVecesVendido(idProducto);
	}

	@Override
	public List<Productos> filtrarPorCategoria(Integer idCategoria) {
		  // Verificar si es categoría padre
	    if (categoriaService.esCategoriaPadre(idCategoria)) {

	        // Obtener subcategorías
	        List<Integer> idsSubcategorias =
	                categoriaService.obtenerIdsSubcategorias(idCategoria);

	        return productosRepositorio
	                .findByCategoria_IdCategoriaIn(idsSubcategorias)
	                .stream()
	                .filter(p -> "ACTIVO".equals(p.getEstado()))
	                .toList();
	    }

	    // Si es subcategoría
	    return productosRepositorio
	            .findByCategoria_IdCategoriaIn(List.of(idCategoria))
	            .stream()
	            .filter(p -> "ACTIVO".equals(p.getEstado()))
	            .toList();
	}
	
	@Override
	public Page<Productos> filtrar(
	        Integer categoria,
	        String buscar,
	        Pageable pageable) {

	    boolean tieneCategoria = categoria != null;
	    boolean tieneBusqueda = buscar != null && !buscar.isEmpty();

	    if (tieneCategoria) {

	        List<Integer> ids;

	        //  Si es PADRE → traer padre + subcategorías
	        if (categoriaService.esCategoriaPadre(categoria)) {

	            ids = categoriaService.obtenerIdsSubcategorias(categoria);

	        } else {

	            //  Si es SUBCATEGORIA → traer subcategoría + su padre
	            Integer padreId = categoriaService.obtenerCategoriaPadreId(categoria);

	            ids = List.of(categoria, padreId);
	        }

	        // Categoría + búsqueda
	        if (tieneBusqueda) {
	            return productosRepositorio
	                    .findByEstadoAndCategoria_IdCategoriaInAndNombreContainingIgnoreCase(
	                            "ACTIVO",
	                            ids,
	                            buscar,
	                            pageable);
	        }

	        // Solo categoría
	        return productosRepositorio
	                .findByEstadoAndCategoria_IdCategoriaIn(
	                        "ACTIVO",
	                        ids,
	                        pageable);
	    }

	    if (tieneBusqueda) {

	        return productosRepositorio
	                .findByEstadoAndNombreContainingIgnoreCase(
	                        "ACTIVO",
	                        buscar,
	                        pageable);
	    }

	    return productosRepositorio
	            .findByEstado("ACTIVO", pageable);
	}

	@Override
    public List<Productos> buscarPorNombre(String nombre) {
        return productosRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

	@Override
	public Map<String, List<ProductoPromoDTO>> obtenerProductosConPromocion() {
		  List<Descuento> descuentosActivos =
		            descuentoRepository.findDescuentosActivos();

		    Map<String, List<ProductoPromoDTO>> promocionesMap = new HashMap<>();

		    for (Descuento d : descuentosActivos) {

		        String nombrePromo = d.getPromocion().getNombrePromo();

		        ProductoPromoDTO dto =
		                new ProductoPromoDTO(
		                        d.getProducto(),
		                        d.getPorcentajeDescuento(),
		                        d.getPrecioConDescuento()
		                );

		        promocionesMap
		            .computeIfAbsent(nombrePromo, k -> new ArrayList<>())
		            .add(dto);
		    }

		    return promocionesMap;
	}

	@Override
	public List<Productos> obtenerProductosPorPromocion(String nombrePromo) {

	    List<Descuento> descuentosActivos =
	            descuentoRepository.findDescuentosActivos();

	    List<Productos> productosPromo = new ArrayList<>();

	    for (Descuento d : descuentosActivos) {

	        if (d.getPromocion().getNombrePromo()
	                .equalsIgnoreCase(nombrePromo)) {

	            productosPromo.add(d.getProducto());
	        }
	    }

	    return productosPromo;
	}
	
	@Override
	public Page<Productos> obtenerProductosPorPromocion(String promo, Pageable pageable) {
	    List<Productos> productos = productosRepositorio.findProductosByPromocionNombre(promo);

	    // Paginar manualmente
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), productos.size());
	    List<Productos> sublist = productos.subList(start, end);

	    return new PageImpl<>(sublist, pageable, productos.size());
	}
}
