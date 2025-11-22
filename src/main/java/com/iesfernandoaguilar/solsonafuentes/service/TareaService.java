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
import com.iesfernandoaguilar.solsonafuentes.model.TareaUsuario;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.TareaRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.TareaUsuarioRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TareaUsuarioRepository tareaUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
            // Forzar inicialización de tareasUsuarios para asegurar que el campo trabajando esté disponible
            if (t.getTareasUsuarios() != null) {
                t.getTareasUsuarios().size();
            }
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

    @Transactional
    public List<Tarea> obtenerTareasAsignadasAUsuario(Long idUsuario) {
        List<Tarea> tareas = tareaRepository.obtenerTareasAsignadasAUsuario(idUsuario);
        // Forzar inicialización de tareasUsuarios
        for (Tarea t : tareas) {
            if (t.getTareasUsuarios() != null) {
                t.getTareasUsuarios().size();
            }
        }
        return tareas;
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

        // Limpiar y actualizar usuarios asignados (ahora en tareasUsuarios)
        tarea.getTareasUsuarios().clear();
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
            EstadoTarea estadoAnterior = tarea.getEstado();
            tarea.setEstado(nuevoEstado);

            // Si se marca como COMPLETADA, establecer fecha de completación
            if (nuevoEstado == EstadoTarea.COMPLETADA && estadoAnterior != EstadoTarea.COMPLETADA) {
                tarea.setFechaCompletacion(java.time.LocalDateTime.now());
                System.out.println("✅ Tarea completada - Fecha de completación establecida: " + tarea.getFechaCompletacion());
            }
            // Si se desmarca de COMPLETADA, limpiar fecha de completación
            else if (estadoAnterior == EstadoTarea.COMPLETADA && nuevoEstado != EstadoTarea.COMPLETADA) {
                tarea.setFechaCompletacion(null);
                System.out.println("↩️ Tarea desmarcada como completada - Fecha de completación eliminada");
            }

            return tareaRepository.save(tarea);
        }
        return null;
    }

    @Transactional
    public Tarea asignarUsuario(Long idTarea, Usuario usuario) {
        try {
            // Verificar si ya existe la relación
            Optional<TareaUsuario> existente = tareaUsuarioRepository.findByTareaAndUsuario(idTarea, usuario.getIdUsuario());
            if (existente.isPresent()) {
                System.out.println("Usuario ya estaba asignado a la tarea");
                return tareaRepository.findByIdTarea(idTarea).orElse(null);
            }

            Optional<Tarea> tareaOpt = tareaRepository.findByIdTarea(idTarea);
            if (tareaOpt.isPresent()) {
                Tarea tarea = tareaOpt.get();

                // Crear nueva relación TareaUsuario
                TareaUsuario tareaUsuario = new TareaUsuario(tarea, usuario);
                tareaUsuarioRepository.saveAndFlush(tareaUsuario);
                tareaUsuarioRepository.flush();

                System.out.println("✅ Usuario asignado a tarea correctamente");
                return tarea;
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

    @Transactional
    public List<Tarea> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosTarea filtros, com.iesfernandoaguilar.solsonafuentes.model.Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Tareas");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        List<Tarea> resultados = tareaRepository.obtenerTareasPorGrupo(filtros.getIdGrupo());

        return aplicarFiltrosATareas(resultados, filtros, usuarioActual);
    }

    private boolean esAdmin(com.iesfernandoaguilar.solsonafuentes.model.Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Tarea> aplicarFiltrosATareas(List<Tarea> lista, com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosTarea filtros, com.iesfernandoaguilar.solsonafuentes.model.Usuario usuarioActual) {
        return lista.stream()
            .filter(tarea -> {
                if (esAdmin(usuarioActual)) {
                    return !filtros.tieneUsuario() || tarea.getUsuariosAsignados().stream().anyMatch(u -> u.getIdUsuario().equals(filtros.getIdUsuario()));
                }
                // Para usuarios no admin, solo mostrar tareas asignadas a ellos
                return tarea.getUsuariosAsignados().stream().anyMatch(u -> u.getIdUsuario().equals(usuarioActual.getIdUsuario()));
            })
            .filter(tarea -> !filtros.tieneDepartamento() || tarea.getDepartamentosAsignados().stream().anyMatch(d -> d.getIdDepartamento().equals(filtros.getIdDepartamento())))
            .filter(tarea -> !filtros.tienePrioridad() || (tarea.getPrioridad() != null && tarea.getPrioridad().name().equalsIgnoreCase(filtros.getPrioridad())))
            .filter(tarea -> !filtros.tieneEstado() || (tarea.getEstado() != null && tarea.getEstado().name().equalsIgnoreCase(filtros.getEstado())))
            .filter(tarea -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                return tarea.getTitulo().toLowerCase().contains(busqueda) ||
                       tarea.getDescripcion().toLowerCase().contains(busqueda);
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Alterna el estado "trabajando" de un usuario en una tarea.
     * Si el usuario está trabajando, lo desmarca. Si no está trabajando, lo marca.
     * Si no existe la relación TareaUsuario, la crea automáticamente.
     * El usuario puede trabajar en múltiples tareas simultáneamente.
     *
     * @param idTarea ID de la tarea
     * @param idUsuario ID del usuario
     * @return true si ahora está trabajando, false si dejó de trabajar
     */
    @Transactional
    public boolean toggleTrabajando(Long idTarea, Long idUsuario) {
        // Gestionar la tarea actual (permite trabajar en múltiples tareas simultáneamente)
        Optional<TareaUsuario> tareaUsuarioOpt = tareaUsuarioRepository.findByTareaAndUsuario(idTarea, idUsuario);

        if (tareaUsuarioOpt.isPresent()) {
            // Ya existe la relación, alternar el estado.
            TareaUsuario tareaUsuario = tareaUsuarioOpt.get();
            boolean estadoAnterior = tareaUsuario.isTrabajando();
            boolean nuevoEstado = !estadoAnterior;

            System.out.println("🔄 ID TareaUsuario: " + tareaUsuario.getId() + " | Tarea: " + idTarea + " | Usuario: " + idUsuario);
            System.out.println("   Estado anterior: " + estadoAnterior + " -> Nuevo estado: " + nuevoEstado);

            tareaUsuario.setTrabajando(nuevoEstado);
            TareaUsuario saved = tareaUsuarioRepository.saveAndFlush(tareaUsuario);

            System.out.println("💾 GUARDADO EN BD - ID: " + saved.getId() + ", trabajando: " + saved.isTrabajando());

            // Verificar inmediatamente leyendo de la BD
            Optional<TareaUsuario> verificacion = tareaUsuarioRepository.findByTareaAndUsuario(idTarea, idUsuario);
            if (verificacion.isPresent()) {
                System.out.println("✓ VERIFICACION POST-SAVE: trabajando = " + verificacion.get().isTrabajando());
            } else {
                System.err.println("❌ ERROR CRITICO: Registro no encontrado después de guardar!");
            }

            entityManager.clear(); // Limpiar caché de Hibernate
            System.out.println("🧹 Caché de Hibernate limpiado");

            System.out.println((nuevoEstado ? "✅ Usuario trabajando" : "⏸️ Usuario dejó de trabajar") +
                             " en tarea ID: " + idTarea);
            return nuevoEstado;
        } else {
            // No existe la relación, crearla automáticamente con trabajando = true.
            Optional<Tarea> tareaOpt = tareaRepository.findByIdTarea(idTarea);
            if (!tareaOpt.isPresent()) {
                System.err.println("❌ Tarea no encontrada: " + idTarea);
                return false;
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findByIdUsuario(idUsuario);
            if (!usuarioOpt.isPresent()) {
                System.err.println("❌ Usuario no encontrado: " + idUsuario);
                return false;
            }

            Tarea tarea = tareaOpt.get();
            Usuario usuario = usuarioOpt.get();

            // Crear nueva relación con trabajando = true.
            TareaUsuario nuevaTareaUsuario = new TareaUsuario(tarea, usuario);
            nuevaTareaUsuario.setTrabajando(true);
            tareaUsuarioRepository.saveAndFlush(nuevaTareaUsuario);
            entityManager.clear(); // Limpiar caché de Hibernate

            System.out.println("✅ Usuario comenzó a trabajar en tarea ID: " + idTarea + " (relación creada automáticamente)");
            return true;
        }
    }

    /**
     * Obtiene la lista de usuarios que están trabajando en una tarea
     *
     * @param idTarea ID de la tarea
     * @return Lista de TareaUsuario de usuarios trabajando
     */
    public List<TareaUsuario> obtenerUsuariosTrabajando(Long idTarea) {
        return tareaUsuarioRepository.findUsuariosTrabajandoEnTarea(idTarea);
    }


    public Long contarUsuariosTrabajando(Long idTarea) {
        Long count = tareaUsuarioRepository.contarUsuariosTrabajandoEnTarea(idTarea);
        return count != null ? count : 0L;
    }

    public boolean estaUsuarioTrabajando(Long idTarea, Long idUsuario) {
        return tareaUsuarioRepository.estaUsuarioTrabajandoEnTarea(idTarea, idUsuario);
    }
}
