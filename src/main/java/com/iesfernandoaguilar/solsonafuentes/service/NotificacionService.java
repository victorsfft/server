package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;

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
    
}
