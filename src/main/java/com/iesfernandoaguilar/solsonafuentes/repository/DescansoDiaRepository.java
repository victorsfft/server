package com.iesfernandoaguilar.solsonafuentes.repository;

import com.iesfernandoaguilar.solsonafuentes.model.DescansoDia;
import com.iesfernandoaguilar.solsonafuentes.model.HorarioDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar descansos de los días laborables
 */
@Repository
public interface DescansoDiaRepository extends JpaRepository<DescansoDia, Long> {

    /**
     * Busca un descanso por su ID
     */
    Optional<DescansoDia> findByIdDescanso(Long idDescanso);

    /**
     * Obtiene todos los descansos de un día
     */
    List<DescansoDia> findByDia(HorarioDia dia);

    /**
     * Obtiene todos los descansos de un día por su ID
     */
    @Query("SELECT d FROM DescansoDia d WHERE d.dia.idDia = :idDia ORDER BY d.horaInicio")
    List<DescansoDia> findByDiaIdOrderByHoraInicio(@Param("idDia") Long idDia);

    /**
     * Elimina todos los descansos de un día
     */
    @Query("DELETE FROM DescansoDia d WHERE d.dia.idDia = :idDia")
    void deleteByDiaId(@Param("idDia") Long idDia);

    /**
     * Cuenta cuántos descansos tiene un día
     */
    @Query("SELECT COUNT(d) FROM DescansoDia d WHERE d.dia.idDia = :idDia")
    long countByDiaId(@Param("idDia") Long idDia);
}
