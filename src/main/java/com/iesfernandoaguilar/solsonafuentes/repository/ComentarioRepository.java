package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Optional<Comentario> findByIdComentario(Long idComentario);

    @Query("SELECT c FROM Comentario c WHERE c.tarea.idTarea = :idTarea ORDER BY c.fechaCreacion ASC")
    List<Comentario> obtenerComentariosPorTarea(@Param("idTarea") Long idTarea);

    @Query("SELECT c FROM Comentario c WHERE c.incidencia.idIncidencia = :idIncidencia ORDER BY c.fechaCreacion ASC")
    List<Comentario> obtenerComentariosPorIncidencia(@Param("idIncidencia") Long idIncidencia);

    @Query("SELECT c FROM Comentario c WHERE c.usuario.idUsuario = :idUsuario ORDER BY c.fechaCreacion DESC")
    List<Comentario> obtenerComentariosPorUsuario(@Param("idUsuario") Long idUsuario);

    void deleteByIdComentario(Long idComentario);
}
