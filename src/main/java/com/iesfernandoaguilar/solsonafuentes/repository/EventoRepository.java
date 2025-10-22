package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    Optional<Evento> findByIdEvento(Long idEvento);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN FETCH e.usuariosAsistentes " +
           "WHERE e.idEvento = :idEvento")
    Optional<Evento> findByIdEventoWithUsuarios(@Param("idEvento") Long idEvento);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN FETCH e.departamentosInvitados " +
           "WHERE e.idEvento = :idEvento")
    Optional<Evento> findByIdEventoWithDepartamentos(@Param("idEvento") Long idEvento);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN FETCH e.usuariosAsistentes " +
           "WHERE e.creadoPor.grupo.idGrupo = :idGrupo")
    List<Evento> obtenerEventosPorGrupo(@Param("idGrupo") Long idGrupo);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN FETCH e.departamentosInvitados " +
           "WHERE e IN :eventos")
    List<Evento> fetchDepartamentosInvitados(@Param("eventos") List<Evento> eventos);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN e.usuariosAsistentes ua " +
           "WHERE ua.idUsuario = :idUsuario")
    List<Evento> obtenerEventosDeUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT DISTINCT e FROM Evento e " +
           "LEFT JOIN e.departamentosInvitados di " +
           "WHERE di.idDepartamento = :idDepartamento")
    List<Evento> obtenerEventosPorDepartamento(@Param("idDepartamento") Long idDepartamento);

    void deleteByIdEvento(Long idEvento);
}
