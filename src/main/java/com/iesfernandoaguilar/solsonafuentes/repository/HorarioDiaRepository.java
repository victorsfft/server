package com.iesfernandoaguilar.solsonafuentes.repository;

import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;
import com.iesfernandoaguilar.solsonafuentes.model.HorarioDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar horarios por día de la semana
 */
@Repository
public interface HorarioDiaRepository extends JpaRepository<HorarioDia, Long> {

    /**
     * Busca un horario por su ID
     */
    Optional<HorarioDia> findByIdDia(Long idDia);

    /**
     * Obtiene todos los horarios de una configuración
     */
    List<HorarioDia> findByConfiguracion(ConfiguracionJornada configuracion);

    /**
     * Obtiene todos los horarios de una configuración por su ID
     */
    @Query("SELECT h FROM HorarioDia h WHERE h.configuracion.idConfig = :idConfig ORDER BY FIELD(h.diaSemana, 'LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')")
    List<HorarioDia> findByConfiguracionIdOrderByDia(@Param("idConfig") Long idConfig);

    /**
     * Obtiene un horario específico por día de la semana y configuración
     */
    @Query("SELECT h FROM HorarioDia h WHERE h.configuracion.idConfig = :idConfig AND h.diaSemana = :diaSemana")
    Optional<HorarioDia> findByConfiguracionIdAndDiaSemana(@Param("idConfig") Long idConfig, @Param("diaSemana") String diaSemana);

    /**
     * Obtiene solo los días laborables de una configuración
     */
    @Query("SELECT h FROM HorarioDia h WHERE h.configuracion.idConfig = :idConfig AND h.esLaborable = true")
    List<HorarioDia> findLaborablesByConfiguracionId(@Param("idConfig") Long idConfig);

    /**
     * Elimina todos los horarios de una configuración
     */
    @Query("DELETE FROM HorarioDia h WHERE h.configuracion.idConfig = :idConfig")
    void deleteByConfiguracionId(@Param("idConfig") Long idConfig);
}
