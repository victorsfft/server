package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Incidencia;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    Optional<Incidencia> findByIdIncidencia(Long idIncidencia);

    @Query("SELECT i FROM Incidencia i WHERE i.usuario.grupo.idGrupo = :idGrupo")
    List<Incidencia> obtenerIncidenciasPorGrupo(@Param("idGrupo") Long idGrupo);

    @Query("SELECT i FROM Incidencia i WHERE i.usuario.idUsuario = :idUsuario")
    List<Incidencia> obtenerIncidenciasPorUsuario(@Param("idUsuario") Long idUsuario);

    void deleteByIdIncidencia(Long idIncidencia);
}
