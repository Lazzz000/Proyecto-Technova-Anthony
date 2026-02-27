package com.demo.service.impl;

import com.demo.modelo.Descuento;
import com.demo.modelo.Promocion;
import com.demo.repositorio.DescuentoRepositorio;
import com.demo.repositorio.PromocionRepository;
import com.demo.service.PromocionDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromocionDescServiceImpl implements PromocionDescService {

    private final PromocionRepository promocionRepository;
    private final DescuentoRepositorio descuentoRepository;

    @Autowired
    public PromocionDescServiceImpl(PromocionRepository promocionRepository, DescuentoRepositorio descuentoRepository) {
        this.promocionRepository = promocionRepository;
        this.descuentoRepository = descuentoRepository;
    }


    @Override
    public List<Promocion> listarPromociones() {
        return promocionRepository.findAll().stream().filter(p ->p.getDeletedAt()==null).toList();
    }

    @Override
    public List<Descuento> listarDescuentos() {
        return descuentoRepository.findByDeletedAtIsNullAndPromocionDeletedAtIsNull();
    }

    @Override
    public void guardarPromocionDesc(Promocion promocion,List<Descuento> descuentos) {

        Promocion promoFinal = promocionRepository.findByNombrePromo(promocion.getNombrePromo())
                .orElseGet(() -> promocionRepository.save(promocion));

        descuentos.forEach(d ->{
            d.setPromocion(promoFinal);
            descuentoRepository.save(d);
        });
    }

    @Override
    public void editarDescuento(Descuento descuento) {
        descuentoRepository.save(descuento);
    }

    @Override
    public Descuento findByDescuento(int idDescuento) {
        return this.descuentoRepository.findById(idDescuento).orElse(null);
    }

    @Override
    public void eliminarDescuento(int idDescuento) {
        Descuento descuento = this.descuentoRepository.findById(idDescuento)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        descuento.setDeletedAt(LocalDateTime.now());
        this.descuentoRepository.save(descuento);
    }

    @Override
    public void eliminarPromocion(int idPromocion) {
        Promocion promocion = this.promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        promocion.setNombrePromo(promocion.getNombrePromo()+"-eliminado-"+promocion.getIdPromocion());
        promocion.setDeletedAt(LocalDateTime.now());
        this.promocionRepository.save(promocion);
    }

    @Override
    public List<Descuento> descuentosCurso() {
        return this.descuentoRepository.descuentosCurso();
    }

    @Override
    public List<Descuento> descuentosNoEnCurso() {
        return this.descuentoRepository.descuentosNoEnCurso();
    }

    @Override
    public Promocion findByPromocion(int idPromo) {
        return this.promocionRepository.findById(idPromo).orElse(null);
    }

    @Override
    public void guardarPromocion(Promocion promocion) {
        this.promocionRepository.save(promocion);
    }

    @Override
    public boolean existeNombrePromo(String nombrePromo) {
        return this.promocionRepository.existsByNombrePromo(nombrePromo);
    }

}
