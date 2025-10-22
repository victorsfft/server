package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Anotaciones;
import com.iesfernandoaguilar.solsonafuentes.repository.AnotacionesRepository;

import jakarta.transaction.Transactional;

@Service
public class AnotacionesService {

    @Autowired
    private AnotacionesRepository anotacionesRepository;

    public Optional<Anotaciones> findByIdAnotacion(Long idAnotacion) {
        return anotacionesRepository.findByIdAnotacion(idAnotacion);
    }

    public List<Anotaciones> obtenerAnotacionesPorUsuario(Long idUsuario) {
        return anotacionesRepository.obtenerAnotacionesPorUsuario(idUsuario);
    }

    public List<Anotaciones> obtenerAnotacionesPorUsuarioYFecha(Long idUsuario, LocalDate fecha) {
        return anotacionesRepository.obtenerAnotacionesPorUsuarioYFecha(idUsuario, fecha);
    }

    @Transactional
    public Anotaciones crearAnotacion(Anotaciones anotacion) {
        if (anotacion.getFechaCreacion() == null) {
            anotacion.setFechaCreacion(LocalDateTime.now());
        }
        return anotacionesRepository.save(anotacion);
    }

    @Transactional
    public Anotaciones actualizarAnotacion(Anotaciones anotacion) {
        return anotacionesRepository.save(anotacion);
    }

    @Transactional
    public void eliminarAnotacion(Long idAnotacion) {
        anotacionesRepository.deleteByIdAnotacion(idAnotacion);
    }
}
