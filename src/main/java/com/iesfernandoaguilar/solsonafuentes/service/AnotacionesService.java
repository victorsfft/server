package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Anotaciones;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.AnotacionesRepository;

import jakarta.transaction.Transactional;

@Service
public class AnotacionesService {

    @Autowired
    private AnotacionesRepository anotacionesRepository;

    @Transactional
    public Anotaciones crearAnotacion(Anotaciones anotacion) {
        if (anotacion.getFechaCreacion() == null) {
            anotacion.setFechaCreacion(LocalDateTime.now());
        }
        return anotacionesRepository.save(anotacion);
    }

    public Optional<Anotaciones> findByIdAnotacion(Long idAnotacion) {
        return anotacionesRepository.findByIdAnotacion(idAnotacion);
    }

    public List<Anotaciones> obtenerAnotacionesPorUsuario(Long idUsuario) {
        // Use the method that eagerly fetches the associated user
        return anotacionesRepository.findByUsuarioIdWithUsuario(idUsuario);
    }

    @Transactional
    public void eliminarAnotacion(Long idAnotacion) {
        anotacionesRepository.deleteById(idAnotacion);
    }

    @Transactional
    public Anotaciones actualizarAnotacion(Anotaciones anotacion) {
        // Ensure the annotation exists before updating
        Optional<Anotaciones> existingAnotacionOpt = anotacionesRepository.findByIdAnotacion(anotacion.getIdAnotacion());
        if (existingAnotacionOpt.isPresent()) {
            Anotaciones existingAnotacion = existingAnotacionOpt.get();
            existingAnotacion.setTitulo(anotacion.getTitulo());
            existingAnotacion.setTexto(anotacion.getTexto());
            existingAnotacion.setFecha(anotacion.getFecha());
            // No update for creadoPor, usuario, fechaCreacion as they are set on creation
            return anotacionesRepository.save(existingAnotacion);
        } else {
            throw new IllegalArgumentException("Anotación con ID " + anotacion.getIdAnotacion() + " no encontrada.");
        }
    }
}