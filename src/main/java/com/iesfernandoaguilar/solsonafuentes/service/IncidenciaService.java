package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;
import com.iesfernandoaguilar.solsonafuentes.model.Incidencia;
import com.iesfernandoaguilar.solsonafuentes.repository.IncidenciaRepository;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> obtenerIncidenciasPorGrupo(Long idGrupo) {
        return incidenciaRepository.obtenerIncidenciasPorGrupo(idGrupo);
    }

    public List<Incidencia> obtenerIncidenciasPorUsuario(Long idUsuario) {
        return incidenciaRepository.obtenerIncidenciasPorUsuario(idUsuario);
    }

    public Optional<Incidencia> obtenerIncidenciaPorId(Long idIncidencia) {
        return incidenciaRepository.findByIdIncidencia(idIncidencia);
    }

    @Transactional
    public Incidencia crearIncidencia(Incidencia incidencia) {
        if (incidencia.getFechaCreacion() == null) {
            incidencia.setFechaCreacion(LocalDateTime.now());
        }
        if (incidencia.getPrioridad() == null) {
            incidencia.setPrioridad(Prioridad.MEDIA);
        }
        if (incidencia.getEstado() == null) {
            incidencia.setEstado(EstadoTarea.PENDIENTE);
        }
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public void cambiarEstado(Long idIncidencia, EstadoTarea nuevoEstado) {
        Optional<Incidencia> incidenciaOpt = incidenciaRepository.findByIdIncidencia(idIncidencia);
        if (incidenciaOpt.isPresent()) {
            Incidencia incidencia = incidenciaOpt.get();
            incidencia.setEstado(nuevoEstado);
            incidenciaRepository.save(incidencia);
        }
    }

    @Transactional
    public void eliminarIncidencia(Long idIncidencia) {
        incidenciaRepository.deleteByIdIncidencia(idIncidencia);
    }
}
