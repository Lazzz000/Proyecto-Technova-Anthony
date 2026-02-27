package com.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.modelo.Mensaje;
import java.util.List;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Long> {
	 long countByLeidoFalse();
    List<Mensaje> findByLeidoFalse();
    List<Mensaje> findAllByOrderByFechaDesc();
}
