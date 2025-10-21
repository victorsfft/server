package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
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
    
    @Query("SELECT u FROM Usuario u WHERE u.nombre = :nombreOEmailUsuario OR u.email = :nombreOEmailUsuario")
    Optional<Usuario> login(@Param("nombreOEmailUsuario") String nombreOEmailUsuario);

    @Query("SELECT DISTINCT u FROM Usuario u WHERE u.grupo.idGrupo = :idGrupo")
    List<Usuario> obtenerEmpleados(@Param("idGrupo") Long idGrupo);

    @Query("SELECT u FROM Usuario u WHERE u.grupo.idGrupo = :idGrupo AND " +
            "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(CAST(u.rol AS string)) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.departamento.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.departamento.subgrupo.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    List<Usuario> buscarEmpleados(@Param("idGrupo") Long idGrupo, @Param("filtro") String filtro);
}