package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT DISTINCT n FROM Notificacion n WHERE n.usuarioDestino.idUsuario = :idUsuario AND n.estado = :estado")
    List<Notificacion> obtenerNotificaciones(@Param("idUsuario") Long idUsuario, @Param("estado") EstadoNotificacion estado);

}