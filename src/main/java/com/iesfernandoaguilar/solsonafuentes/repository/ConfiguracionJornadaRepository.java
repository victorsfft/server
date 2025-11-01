package com.iesfernandoaguilar.solsonafuentes.repository;

import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar configuraciones de jornada laboral
 */
@Repository
public interface ConfiguracionJornadaRepository extends JpaRepository<ConfiguracionJornada, Long> {

    /**
     * Busca una configuración por su ID
     */
    Optional<ConfiguracionJornada> findByIdConfig(Long idConfig);

    /**
     * Obtiene todas las configuraciones de un grupo
     */
    List<ConfiguracionJornada> findByGrupo(Grupo grupo);

    /**
     * Obtiene todas las configuraciones de un grupo por su ID (con relaciones cargadas)
     */
    @Query("SELECT DISTINCT c FROM ConfiguracionJornada c LEFT JOIN FETCH c.horarios WHERE c.grupo.idGrupo = :idGrupo")
    List<ConfiguracionJornada> findByGrupoId(@Param("idGrupo") Long idGrupo);

    /**
     * Busca configuración por nombre dentro de un grupo
     */
    @Query("SELECT c FROM ConfiguracionJornada c WHERE c.nombreConfig = :nombre AND c.grupo.idGrupo = :idGrupo")
    Optional<ConfiguracionJornada> findByNombreAndGrupoId(@Param("nombre") String nombre, @Param("idGrupo") Long idGrupo);

    /**
     * Obtiene configuraciones activas de un grupo (con relaciones cargadas)
     */
    @Query("SELECT DISTINCT c FROM ConfiguracionJornada c LEFT JOIN FETCH c.horarios WHERE c.grupo.idGrupo = :idGrupo AND c.estado = 'ACTIVA'")
    List<ConfiguracionJornada> findActivasByGrupoId(@Param("idGrupo") Long idGrupo);

    /**
     * Cuenta cuántas configuraciones tiene un grupo
     */
    @Query("SELECT COUNT(c) FROM ConfiguracionJornada c WHERE c.grupo.idGrupo = :idGrupo")
    long countByGrupoId(@Param("idGrupo") Long idGrupo);

    /**
     * Verifica si existe una configuración con ese nombre en el grupo
     */
    @Query("SELECT COUNT(c) > 0 FROM ConfiguracionJornada c WHERE c.nombreConfig = :nombre AND c.grupo.idGrupo = :idGrupo")
    boolean existsByNombreAndGrupoId(@Param("nombre") String nombre, @Param("idGrupo") Long idGrupo);
}
