package com.iesfernandoaguilar.solsonafuentes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.DescansoJornada;

@Repository
public interface DescansoJornadaRepository extends JpaRepository<DescansoJornada, Long> {

    // Obtener descansos de una jornada específica
    @Query("SELECT d FROM DescansoJornada d WHERE d.jornada.idJornada = :idJornada ORDER BY d.horaInicio ASC")
    List<DescansoJornada> obtenerDescansosPorJornada(@Param("idJornada") Long idJornada);

    // Obtener descansos de un usuario en una fecha
    @Query("SELECT d FROM DescansoJornada d WHERE d.jornada.usuario.idUsuario = :idUsuario " +
           "AND d.jornada.fecha = :fecha ORDER BY d.horaInicio ASC")
    List<DescansoJornada> obtenerDescansosPorUsuarioYFecha(
        @Param("idUsuario") Long idUsuario,
        @Param("fecha") LocalDate fecha
    );

    // Obtener tiempo total de descansos por jornada en minutos
    @Query("SELECT COALESCE(SUM(d.duracionMinutos), 0) FROM DescansoJornada d WHERE d.jornada.idJornada = :idJornada")
    Integer obtenerTiempoTotalDescansos(@Param("idJornada") Long idJornada);

    // Obtener descansos de un usuario en un rango de fechas
    @Query("SELECT d FROM DescansoJornada d WHERE d.jornada.usuario.idUsuario = :idUsuario " +
           "AND d.jornada.fecha BETWEEN :fechaDesde AND :fechaHasta " +
           "ORDER BY d.jornada.fecha DESC, d.horaInicio ASC")
    List<DescansoJornada> obtenerDescansosPorUsuarioYRango(
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );
}
