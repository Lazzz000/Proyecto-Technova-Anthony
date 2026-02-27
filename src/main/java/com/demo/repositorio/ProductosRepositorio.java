package com.demo.repositorio;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.modelo.Productos;

@Repository
public interface ProductosRepositorio extends JpaRepository<Productos, Integer> {


    // Productos por categorías
    List<Productos> findByCategoria_IdCategoriaIn(List<Integer> idsCategorias);

    //Listar activos
    List<Productos> findByEstado(String estado);
    
    @Query("""
    	    SELECT COALESCE(SUM(dp.cantidad), 0)
    	    FROM PedidoDetalle dp
    	    WHERE dp.producto.idProducto = :idProducto
    	""")
    	Long contarVecesVendido(@Param("idProducto") Integer idProducto);
    
    long countByEstado(String estado);
    
    

    Page<Productos> findByEstado(String estado, Pageable pageable);


Page<Productos> findByEstadoAndCategoria_IdCategoriaIn(
        String estado,
        List<Integer> ids,
        Pageable pageable);
    
    Page<Productos> findByEstadoAndCategoria_IdCategoriaInAndNombreContainingIgnoreCase(
            String estado,
            List<Integer> ids,
            String nombre,
            Pageable pageable);
    
    

Page<Productos> findByEstadoAndNombreContainingIgnoreCase(
        String estado,
        String nombre,
        Pageable pageable);

List<Productos> findByNombreContainingIgnoreCase(String texto);

@Query("""
	    SELECT DISTINCT d.producto 
	    FROM Descuento d 
	    JOIN d.promocion p 
	    WHERE p.nombrePromo = :nombrePromo 
	      AND d.deletedAt IS NULL 
	      AND p.deletedAt IS NULL 
	      AND d.fechaInicio <= CURRENT_DATE 
	      AND d.fechaFin >= CURRENT_DATE
	""")
	List<Productos> findProductosByPromocionNombre(@Param("nombrePromo") String nombrePromo);



}
