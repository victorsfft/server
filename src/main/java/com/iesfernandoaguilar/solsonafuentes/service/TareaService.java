package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Transactional
    public List<Tarea> obtenerTareasPorGrupo(Long idGrupo) {
        // Primera pasada: obtener tareas con usuarios asignados
        List<Tarea> tareasConUsuarios = tareaRepository.obtenerTareasPorGrupoWithUsuarios(idGrupo);

        // Crear un mapa para acceso rápido por ID
        Map<Long, Tarea> tareaMap = new HashMap<>();
        for (Tarea t : tareasConUsuarios) {
            tareaMap.put(t.getIdTarea(), t);
        }

        // Segunda pasada: cargar departamentos para cada tarea
        List<Tarea> tareasConDepartamentos = tareaRepository.obtenerTareasPorGrupoWithDepartamentos(idGrupo);
        for (Tarea t : tareasConDepartamentos) {
            Tarea tareaOriginal = tareaMap.get(t.getIdTarea());
            if (tareaOriginal != null) {
                // Las colecciones ya están inicializadas por Hibernate
                tareaOriginal.getDepartamentosAsignados().size(); // Forzar inicialización
            }
        }

        return tareasConUsuarios;
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
    public Tarea actualizarTareaConAsignaciones(Long idTarea, String titulo, String descripcion,
                                                Prioridad prioridad, java.time.LocalDate fechaFin,
                                                List<Long> idsUsuarios, List<Long> idsDepartamentos) {
        // Obtener tarea con usuarios inicializados
        Optional<Tarea> tareaOpt = tareaRepository.findByIdTareaWithUsuarios(idTarea);
        if (!tareaOpt.isPresent()) {
            return null;
        }

        Tarea tarea = tareaOpt.get();

        // Actualizar campos básicos
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setPrioridad(prioridad);
        tarea.setFechaFin(fechaFin);

        // Limpiar y actualizar usuarios asignados
        tarea.getUsuariosAsignados().clear();
        tareaRepository.flush(); // Forzar persistencia de la limpieza

        // Obtener nuevamente la tarea con departamentos para limpiarlos
        tareaOpt = tareaRepository.findByIdTareaWithDepartamentos(idTarea);
        if (tareaOpt.isPresent()) {
            tarea = tareaOpt.get();
            tarea.getDepartamentosAsignados().clear();
            tareaRepository.flush();
        }

        // Re-obtener la tarea para asignar nuevos valores
        tareaOpt = tareaRepository.findByIdTarea(idTarea);
        if (tareaOpt.isPresent()) {
            tarea = tareaOpt.get();
        }

        // Guardar cambios básicos
        tarea = tareaRepository.save(tarea);

        return tarea;
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
        try {
            Optional<Tarea> tareaOpt = tareaRepository.findByIdTareaWithUsuarios(idTarea);
            if (tareaOpt.isPresent()) {
                Tarea tarea = tareaOpt.get();
                if (!tarea.getUsuariosAsignados().contains(usuario)) {
                    tarea.addUsuarioAsignado(usuario);
                    Tarea resultado = tareaRepository.save(tarea);
                    tareaRepository.flush(); // Asegurar que se persista inmediatamente
                    return resultado;
                }
                return tarea; // Ya estaba asignado
            } else {
                System.err.println("Error: Tarea con ID " + idTarea + " no encontrada");
            }
        } catch (Exception e) {
            System.err.println("Error al asignar usuario a tarea: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public Tarea asignarDepartamento(Long idTarea, Departamento departamento) {
        try {
            Optional<Tarea> tareaOpt = tareaRepository.findByIdTareaWithDepartamentos(idTarea);
            if (tareaOpt.isPresent()) {
                Tarea tarea = tareaOpt.get();
                if (!tarea.getDepartamentosAsignados().contains(departamento)) {
                    tarea.addDepartamentoAsignado(departamento);
                    Tarea resultado = tareaRepository.save(tarea);
                    tareaRepository.flush(); // Asegurar que se persista inmediatamente
                    return resultado;
                }
                return tarea; // Ya estaba asignado
            } else {
                System.err.println("Error: Tarea con ID " + idTarea + " no encontrada");
            }
        } catch (Exception e) {
            System.err.println("Error al asignar departamento a tarea: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public void eliminarTarea(Long idTarea) {
        tareaRepository.deleteByIdTarea(idTarea);
    }
}
