package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByIdUsuario(Long idUsuario);
    
    @Query("SELECT usuario FROM Usuario usuario WHERE usuario.nombre = :nombreOEmailUsuario OR usuario.email = :nombreOEmailUsuario")
    Optional<Usuario> login(@Param("nombreOEmailUsuario") String nombreOEmailUsuario);
    

}