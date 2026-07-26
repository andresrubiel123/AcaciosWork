/** repositorio jpa para la entidad movimientoinventario. @author RADJ */
package com.acacioswork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.MovimientoInventario;

/** repositorio para la gestión de persistencia de movimientos de inventario. @author RADJ */
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    
    /** obtiene todos los movimientos asociados a un producto. @author RADJ */
    List<MovimientoInventario> findByIdProducto(Long idProducto);
}
