package com.iesfernandoaguilar.solsonafuentes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.enums.TipoAccionHistorial;
import com.iesfernandoaguilar.solsonafuentes.model.Historial;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {

    // Obtener historial por grupo
    @Query("SELECT h FROM Historial h WHERE h.grupo.idGrupo = :idGrupo ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorGrupo(@Param("idGrupo") Long idGrupo);

    // Obtener historial por usuario (afectado)
    @Query("SELECT h FROM Historial h WHERE h.usuario.idUsuario = :idUsuario ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorUsuario(@Param("idUsuario") Long idUsuario);

    // Obtener historial por quien lo realizó
    @Query("SELECT h FROM Historial h WHERE h.realizadoPor.idUsuario = :idUsuario ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialRealizadoPor(@Param("idUsuario") Long idUsuario);

    // Obtener historial por tipo de acción
    @Query("SELECT h FROM Historial h WHERE h.grupo.idGrupo = :idGrupo AND h.tipoAccion = :tipoAccion ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorTipo(@Param("idGrupo") Long idGrupo, @Param("tipoAccion") TipoAccionHistorial tipoAccion);

    // Obtener historial por rango de fechas
    @Query("SELECT h FROM Historial h WHERE h.grupo.idGrupo = :idGrupo AND h.fechaHora BETWEEN :fechaDesde AND :fechaHasta ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorFechas(
        @Param("idGrupo") Long idGrupo,
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta
    );

    // Obtener historial con filtros múltiples
    @Query("SELECT h FROM Historial h WHERE h.grupo.idGrupo = :idGrupo " +
           "AND (:tipoAccion IS NULL OR h.tipoAccion = :tipoAccion) " +
           "AND (:idUsuario IS NULL OR h.usuario.idUsuario = :idUsuario) " +
           "AND (:fechaDesde IS NULL OR h.fechaHora >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR h.fechaHora <= :fechaHasta) " +
           "ORDER BY h.fechaHora DESC")
    List<Historial> buscarHistorialConFiltros(
        @Param("idGrupo") Long idGrupo,
        @Param("tipoAccion") TipoAccionHistorial tipoAccion,
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta
    );

    // Obtener historial por departamento (usuarios del departamento)
    @Query("SELECT h FROM Historial h WHERE h.grupo.idGrupo = :idGrupo " +
           "AND h.usuario.departamento.idDepartamento = :idDepartamento " +
           "ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorDepartamento(
        @Param("idGrupo") Long idGrupo,
        @Param("idDepartamento") Long idDepartamento
    );

    // Obtener historial por entidad relacionada
    @Query("SELECT h FROM Historial h WHERE h.entidadRelacionadaId = :entidadId " +
           "AND h.tipoEntidad = :tipoEntidad ORDER BY h.fechaHora DESC")
    List<Historial> obtenerHistorialPorEntidad(
        @Param("entidadId") Long entidadId,
        @Param("tipoEntidad") String tipoEntidad
    );
}
