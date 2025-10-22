package com.iesfernandoaguilar.solsonafuentes.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Estadistica;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {

    // Obtener estadística por usuario
    @Query("SELECT e FROM Estadistica e WHERE e.usuario.idUsuario = :idUsuario ORDER BY e.fecha DESC")
    List<Estadistica> obtenerEstadisticasPorUsuario(@Param("idUsuario") Long idUsuario);

    // Obtener estadística por grupo
    @Query("SELECT e FROM Estadistica e WHERE e.grupo.idGrupo = :idGrupo ORDER BY e.fecha DESC")
    List<Estadistica> obtenerEstadisticasPorGrupo(@Param("idGrupo") Long idGrupo);

    // Obtener estadística del día actual para un usuario
    @Query("SELECT e FROM Estadistica e WHERE e.usuario.idUsuario = :idUsuario AND e.fecha = :fecha")
    Optional<Estadistica> obtenerEstadisticaActualUsuario(@Param("idUsuario") Long idUsuario, @Param("fecha") LocalDate fecha);

    // Obtener estadística del día actual para un grupo
    @Query("SELECT e FROM Estadistica e WHERE e.grupo.idGrupo = :idGrupo AND e.fecha = :fecha")
    Optional<Estadistica> obtenerEstadisticaActualGrupo(@Param("idGrupo") Long idGrupo, @Param("fecha") LocalDate fecha);

    // Obtener estadísticas en un rango de fechas para un usuario
    @Query("SELECT e FROM Estadistica e WHERE e.usuario.idUsuario = :idUsuario " +
           "AND e.fecha BETWEEN :fechaDesde AND :fechaHasta ORDER BY e.fecha DESC")
    List<Estadistica> obtenerEstadisticasUsuarioPorRango(
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener estadísticas en un rango de fechas para un grupo
    @Query("SELECT e FROM Estadistica e WHERE e.grupo.idGrupo = :idGrupo " +
           "AND e.fecha BETWEEN :fechaDesde AND :fechaHasta ORDER BY e.fecha DESC")
    List<Estadistica> obtenerEstadisticasGrupoPorRango(
        @Param("idGrupo") Long idGrupo,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener estadísticas de todos los usuarios de un grupo
    @Query("SELECT e FROM Estadistica e WHERE e.grupo.idGrupo = :idGrupo AND e.usuario IS NOT NULL " +
           "ORDER BY e.usuario.nombre ASC, e.fecha DESC")
    List<Estadistica> obtenerEstadisticasUsuariosDelGrupo(@Param("idGrupo") Long idGrupo);

    // Obtener la última estadística de cada usuario del grupo
    @Query("SELECT e FROM Estadistica e WHERE e.grupo.idGrupo = :idGrupo AND e.usuario IS NOT NULL " +
           "AND e.fecha = (SELECT MAX(e2.fecha) FROM Estadistica e2 WHERE e2.usuario.idUsuario = e.usuario.idUsuario) " +
           "ORDER BY e.usuario.nombre ASC")
    List<Estadistica> obtenerUltimasEstadisticasUsuariosDelGrupo(@Param("idGrupo") Long idGrupo);
}
