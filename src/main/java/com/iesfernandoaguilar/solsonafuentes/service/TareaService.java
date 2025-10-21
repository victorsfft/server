package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Tarea;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.TareaRepository;

import jakarta.transaction.Transactional;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    public Optional<Tarea> findByIdTarea(Long idTarea) {
        return tareaRepository.findByIdTarea(idTarea);
    }

    public Optional<Tarea> findByIdTareaWithUsuarios(Long idTarea) {
        return tareaRepository.findByIdTareaWithUsuarios(idTarea);
    }

    public List<Tarea> obtenerTareasPorGrupo(Long idGrupo) {
        return tareaRepository.obtenerTareasPorGrupo(idGrupo);
    }

    public List<Tarea> obtenerTareasAsignadasAUsuario(Long idUsuario) {
        return tareaRepository.obtenerTareasAsignadasAUsuario(idUsuario);
    }

    public List<Tarea> obtenerTareasAsignadasADepartamento(Long idDepartamento) {
        return tareaRepository.obtenerTareasAsignadasADepartamento(idDepartamento);
    }

    public List<Tarea> obtenerTareasCreadasPorUsuario(Long idUsuario) {
        return tareaRepository.obtenerTareasCreadasPorUsuario(idUsuario);
    }

    @Transactional
    public Tarea crearTarea(Tarea tarea) {
        if (tarea.getFechaCreacion() == null) {
            tarea.setFechaCreacion(LocalDateTime.now());
        }
        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        if (tarea.getPrioridad() == null) {
            tarea.setPrioridad(Prioridad.MEDIA);
        }
        return tareaRepository.save(tarea);
    }

    @Transactional
    public Tarea actualizarTarea(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    @Transactional
    public Tarea cambiarEstado(Long idTarea, EstadoTarea nuevoEstado) {
        Optional<Tarea> tareaOpt = tareaRepository.findByIdTarea(idTarea);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.setEstado(nuevoEstado);
            return tareaRepository.save(tarea);
        }
        return null;
    }

    @Transactional
    public Tarea asignarUsuario(Long idTarea, Usuario usuario) {
        Optional<Tarea> tareaOpt = tareaRepository.findByIdTareaWithUsuarios(idTarea);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.addUsuarioAsignado(usuario);
            return tareaRepository.save(tarea);
        }
        return null;
    }

    @Transactional
    public Tarea asignarDepartamento(Long idTarea, Departamento departamento) {
        Optional<Tarea> tareaOpt = tareaRepository.findByIdTarea(idTarea);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.addDepartamentoAsignado(departamento);
            return tareaRepository.save(tarea);
        }
        return null;
    }

    @Transactional
    public void eliminarTarea(Long idTarea) {
        tareaRepository.deleteByIdTarea(idTarea);
    }
}
