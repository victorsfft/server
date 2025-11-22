package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.TareaUsuario;

@Repository
public interface TareaUsuarioRepository extends JpaRepository<TareaUsuario, Long> {

    @Query("SELECT tu FROM TareaUsuario tu WHERE tu.tarea.idTarea = :idTarea AND tu.usuario.idUsuario = :idUsuario")
    Optional<TareaUsuario> findByTareaAndUsuario(@Param("idTarea") Long idTarea, @Param("idUsuario") Long idUsuario);

    @Query("SELECT tu FROM TareaUsuario tu LEFT JOIN FETCH tu.usuario WHERE tu.tarea.idTarea = :idTarea AND tu.trabajando = true")
    List<TareaUsuario> findUsuariosTrabajandoEnTarea(@Param("idTarea") Long idTarea);

    @Query("SELECT COUNT(tu) FROM TareaUsuario tu WHERE tu.tarea.idTarea = :idTarea AND tu.trabajando = true")
    Long contarUsuariosTrabajandoEnTarea(@Param("idTarea") Long idTarea);

    @Query("SELECT CASE WHEN COUNT(tu) > 0 THEN true ELSE false END FROM TareaUsuario tu WHERE tu.tarea.idTarea = :idTarea AND tu.usuario.idUsuario = :idUsuario AND tu.trabajando = true")
    boolean estaUsuarioTrabajandoEnTarea(@Param("idTarea") Long idTarea, @Param("idUsuario") Long idUsuario);


    @Query("SELECT tu FROM TareaUsuario tu WHERE tu.tarea.idTarea = :idTarea")
    List<TareaUsuario> findByTarea(@Param("idTarea") Long idTarea);

    List<TareaUsuario> findAllByUsuarioIdUsuario(Long idUsuario);

    void deleteByTareaIdTareaAndUsuarioIdUsuario(Long idTarea, Long idUsuario);
}
