/** Repositorio JPA para la entidad MovimientoInventario. @author RADJ */
package com.acacioswork.repository;

import com.acacioswork.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/** Repositorio para la gestión de persistencia de movimientos de inventario. @author RADJ */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    
    /** Obtiene todos los movimientos asociados a un producto. @author RADJ */
    List<MovimientoInventario> findByIdProducto(Long idProducto);
}
