package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Anotaciones;

@Repository
public interface AnotacionesRepository extends JpaRepository<Anotaciones, Long> {

    Optional<Anotaciones> findByIdAnotacion(Long idAnotacion);

    @Query("SELECT a FROM Anotaciones a WHERE a.usuario.idUsuario = :idUsuario ORDER BY a.fecha DESC")
    List<Anotaciones> obtenerAnotacionesPorUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT a FROM Anotaciones a WHERE a.usuario.idUsuario = :idUsuario AND a.fecha = :fecha")
    List<Anotaciones> obtenerAnotacionesPorUsuarioYFecha(@Param("idUsuario") Long idUsuario, @Param("fecha") java.time.LocalDate fecha);

    void deleteByIdAnotacion(Long idAnotacion);
}
