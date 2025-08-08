package com.edutech.api.domain.usuario.repository;

import com.edutech.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
            SELECT u FROM Usuario u 
            WHERE u.login = :usernameOrEmail 
            OR 
            u.email = :usernameOrEmail
            """)
    Optional<Usuario> findByLoginOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}
