/** Servicio de lógica de negocio para ventas. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.acacioswork.model.Venta;
import com.acacioswork.repository.VentaRepository;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    /** Recupera todas las ventas. @author RADJ */
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    /** Busca una venta por ID. @author RADJ */
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    /** Registra una venta. @author RADJ */
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    /** Elimina una venta por ID. @author RADJ */
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
}
