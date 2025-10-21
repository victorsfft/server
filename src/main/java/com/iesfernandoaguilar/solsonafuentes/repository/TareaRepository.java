package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    Optional<Tarea> findByIdTarea(Long idTarea);

    @Query("SELECT DISTINCT t FROM Tarea t LEFT JOIN FETCH t.usuariosAsignados WHERE t.idTarea = :idTarea")
    Optional<Tarea> findByIdTareaWithUsuarios(@Param("idTarea") Long idTarea);

    @Query("SELECT DISTINCT t FROM Tarea t " +
           "LEFT JOIN t.usuariosAsignados ua " +
           "LEFT JOIN t.departamentosAsignados da " +
           "WHERE ua.grupo.idGrupo = :idGrupo OR da.subgrupo.grupo.idGrupo = :idGrupo OR t.creadoPor.grupo.idGrupo = :idGrupo")
    List<Tarea> obtenerTareasPorGrupo(@Param("idGrupo") Long idGrupo);

    @Query("SELECT DISTINCT t FROM Tarea t " +
           "LEFT JOIN t.usuariosAsignados ua " +
           "WHERE ua.idUsuario = :idUsuario")
    List<Tarea> obtenerTareasAsignadasAUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT DISTINCT t FROM Tarea t " +
           "LEFT JOIN t.departamentosAsignados da " +
           "WHERE da.idDepartamento = :idDepartamento")
    List<Tarea> obtenerTareasAsignadasADepartamento(@Param("idDepartamento") Long idDepartamento);

    @Query("SELECT DISTINCT t FROM Tarea t WHERE t.creadoPor.idUsuario = :idUsuario")
    List<Tarea> obtenerTareasCreadasPorUsuario(@Param("idUsuario") Long idUsuario);

    void deleteByIdTarea(Long idTarea);
}
