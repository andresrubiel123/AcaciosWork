/** repositorio jpa para la entidad usuario. @author RADJ */
package com.acacioswork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.Usuario;

/** repositorio jpa para la entidad usuario. @author RADJ */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** busca un usuario por su nombre de usuario. @author RADJ */
    Optional<Usuario> findByUsuario(String usuario);

    /** busca un usuario por su número de identificación. @author RADJ */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u WHERE CAST(u.numeroDocumento AS string) = :numeroDocumento")
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    /** busca un usuario por nombre y clave. @author RADJ */
    Optional<Usuario> findByUsuarioAndClave(String usuario, String clave);
}
