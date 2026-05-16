/** Repositorio JPA para la entidad Usuario. @author RADJ */
package com.acacioswork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acacioswork.model.Usuario;

/** Repositorio JPA para la entidad Usuario. @author RADJ */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca un usuario por su nombre de usuario. @author RADJ */
    Optional<Usuario> findByUsuario(String usuario);

    /** Busca un usuario por su número de identificación. @author RADJ */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u WHERE CAST(u.numeroDocumento AS string) = :numeroDocumento")
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    /** Busca un usuario por nombre y clave. @author RADJ */
    Optional<Usuario> findByUsuarioAndClave(String usuario, String clave);
}
