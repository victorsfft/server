package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;
import com.iesfernandoaguilar.solsonafuentes.repository.NotificacionRepository;

import jakarta.transaction.Transactional;



@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Transactional
    public Notificacion save(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificaciones(Long idUsuario,EstadoNotificacion estado) {
        return notificacionRepository.obtenerNotificaciones(idUsuario,estado);
    }

    public Optional<Notificacion> findByIdNotificacion(Long idNotificacion) {
        return notificacionRepository.findByIdNotificacion(idNotificacion);
    }
    
    @Transactional
    public Notificacion crearOActualizarInvitacion(Notificacion notificacion) {
        // Buscar si ya existe una invitación para este usuario y grupo
        Optional<Notificacion> invitacionExistente = notificacionRepository.findInvitacionExistente(
            notificacion.getUsuarioDestino().getIdUsuario(),
            notificacion.getGrupo().getIdGrupo()
        );
        
        if (invitacionExistente.isPresent()) {
            // Si existe, actualizar el estado a PENDIENTE
            Notificacion notifExistente = invitacionExistente.get();
            notifExistente.setEstado(EstadoNotificacion.PENDIENTE);
            return notificacionRepository.save(notifExistente);
        } else {
            // Si no existe, crear nueva
            return notificacionRepository.save(notificacion);
        }
    }
    
}
