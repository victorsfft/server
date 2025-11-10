package com.iesfernandoaguilar.solsonafuentes.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;

@Repository
public interface JornadaLaboralRepository extends JpaRepository<JornadaLaboral, Long> {

    // Obtener jornada por ID
    Optional<JornadaLaboral> findByIdJornada(Long idJornada);

    // Obtener jornada actual de un usuario (hoy)
    @Query("SELECT j FROM JornadaLaboral j WHERE j.usuario.idUsuario = :idUsuario AND j.fecha = :fecha")
    Optional<JornadaLaboral> obtenerJornadaActual(@Param("idUsuario") Long idUsuario, @Param("fecha") LocalDate fecha);

    // Obtener jornadas de un usuario en un rango de fechas
    @Query("SELECT j FROM JornadaLaboral j LEFT JOIN FETCH j.usuario WHERE j.usuario.idUsuario = :idUsuario " +
           "AND j.fecha BETWEEN :fechaDesde AND :fechaHasta ORDER BY j.fecha DESC")
    List<JornadaLaboral> obtenerJornadasUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener jornadas de un grupo en un rango de fechas
    @Query("SELECT j FROM JornadaLaboral j LEFT JOIN FETCH j.usuario WHERE j.usuario.grupo.idGrupo = :idGrupo " +
           "AND j.fecha BETWEEN :fechaDesde AND :fechaHasta ORDER BY j.fecha DESC, j.usuario.nombre ASC")
    List<JornadaLaboral> obtenerJornadasGrupo(
        @Param("idGrupo") Long idGrupo,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener empleados sin fichar en una fecha específica
    @Query("SELECT u FROM com.iesfernandoaguilar.solsonafuentes.model.Usuario u LEFT JOIN FETCH u.departamento WHERE u.grupo.idGrupo = :idGrupo " +
           "AND u.idUsuario NOT IN (SELECT j.usuario.idUsuario FROM com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral j WHERE j.fecha = :fecha AND j.usuario.grupo.idGrupo = :idGrupo) " +
           "AND u.rol IN ('EMPLEADO', 'ADMINISTRADOR')")
    List<com.iesfernandoaguilar.solsonafuentes.model.Usuario> obtenerEmpleadosSinFichar(@Param("idGrupo") Long idGrupo, @Param("fecha") LocalDate fecha);

    // Obtener jornadas por configuración
    @Query("SELECT j FROM JornadaLaboral j WHERE j.configuracion.idConfig = :idConfig ORDER BY j.fecha DESC")
    List<JornadaLaboral> obtenerJornadasPorConfiguracion(@Param("idConfig") Long idConfig);

    // Obtener total de horas trabajadas por usuario en un período
    @Query("SELECT COALESCE(SUM(j.horasTrabajadas), 0.0) FROM JornadaLaboral j " +
           "WHERE j.usuario.idUsuario = :idUsuario " +
           "AND j.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double obtenerHorasTotalesUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener total de horas extras por usuario en un período
    @Query("SELECT COALESCE(SUM(j.horasExtras), 0.0) FROM JornadaLaboral j " +
           "WHERE j.usuario.idUsuario = :idUsuario " +
           "AND j.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double obtenerHorasExtrasUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Obtener jornadas del día de todo el grupo
    @Query("SELECT j FROM JornadaLaboral j WHERE j.usuario.grupo.idGrupo = :idGrupo AND j.fecha = :fecha")
    List<JornadaLaboral> obtenerJornadasDelDia(@Param("idGrupo") Long idGrupo, @Param("fecha") LocalDate fecha);
}
