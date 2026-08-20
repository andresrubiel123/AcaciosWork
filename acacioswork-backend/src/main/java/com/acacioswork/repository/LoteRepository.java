package com.acacioswork.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.acacioswork.model.Lote;

/** Repositorio JPA para la entidad Lote. @author RADJ / Antigravity */
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(Long idProducto);
    List<Lote> findByIdProducto(Long idProducto);
}
