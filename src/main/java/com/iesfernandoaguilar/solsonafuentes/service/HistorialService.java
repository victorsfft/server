package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.TipoAccionHistorial;
import com.iesfernandoaguilar.solsonafuentes.model.*;
import com.iesfernandoaguilar.solsonafuentes.repository.*;

import jakarta.transaction.Transactional;

@Service
public class HistorialService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private SubgrupoRepository subgrupoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Construye el historial dinámicamente desde las tablas reales
     */
    @Transactional
    public List<Historial> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros, Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Historial");
            return new ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        System.out.println("DEBUG: Construyendo historial desde tablas reales con filtros: " + filtros);

        List<Historial> historialCompleto = new ArrayList<>();

        // Obtener historial de cada tabla
        historialCompleto.addAll(obtenerHistorialTareas(filtros));
        historialCompleto.addAll(obtenerHistorialIncidencias(filtros));
        historialCompleto.addAll(obtenerHistorialDepartamentos(filtros));
        historialCompleto.addAll(obtenerHistorialSubgrupos(filtros));
        historialCompleto.addAll(obtenerHistorialEventos(filtros));

        // Aplicar filtros adicionales
        historialCompleto = aplicarFiltrosAdicionales(historialCompleto, filtros);

        // Ordenar por fecha descendente
        historialCompleto.sort(Comparator.comparing(Historial::getFechaHora).reversed());

        System.out.println("DEBUG: Historial construido con " + historialCompleto.size() + " entradas");

        return historialCompleto;
    }

    private List<Historial> obtenerHistorialTareas(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        List<Tarea> tareas = tareaRepository.findAll();
        List<Historial> historial = new ArrayList<>();

        for (Tarea tarea : tareas) {
            // Obtener grupo desde el creador
            Grupo grupo = tarea.getCreadoPor() != null ? tarea.getCreadoPor().getGrupo() : null;
            if (grupo == null || !grupo.getIdGrupo().equals(filtros.getIdGrupo())) {
                continue;
            }

            if (tarea.getFechaCreacion() != null) {
                Historial h = crearHistorialVirtual(
                    TipoAccionHistorial.TAREA_CREADA,
                    "Tarea creada: " + tarea.getTitulo(),
                    tarea.getFechaCreacion(),
                    grupo,
                    null,
                    tarea.getCreadoPor(),
                    tarea.getIdTarea(),
                    "TAREA"
                );
                historial.add(h);
            }
        }

        return historial;
    }

    private List<Historial> obtenerHistorialIncidencias(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        List<Incidencia> incidencias = incidenciaRepository.findAll();
        List<Historial> historial = new ArrayList<>();

        for (Incidencia incidencia : incidencias) {
            // Obtener grupo desde el usuario afectado
            Grupo grupo = incidencia.getUsuario() != null ? incidencia.getUsuario().getGrupo() : null;
            if (grupo == null || !grupo.getIdGrupo().equals(filtros.getIdGrupo())) {
                continue;
            }

            if (incidencia.getFechaCreacion() != null) {
                Historial h = crearHistorialVirtual(
                    TipoAccionHistorial.INCIDENCIA_CREADA,
                    "Incidencia creada: " + incidencia.getTitulo(),
                    incidencia.getFechaCreacion(),
                    grupo,
                    incidencia.getUsuario(),
                    incidencia.getUsuario(),
                    incidencia.getIdIncidencia(),
                    "INCIDENCIA"
                );
                historial.add(h);
            }
        }

        return historial;
    }

    private List<Historial> obtenerHistorialDepartamentos(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        List<Departamento> departamentos = departamentoRepository.findAll();
        List<Historial> historial = new ArrayList<>();

        for (Departamento depto : departamentos) {
            if (depto.getSubgrupo() == null || depto.getSubgrupo().getGrupo() == null ||
                !depto.getSubgrupo().getGrupo().getIdGrupo().equals(filtros.getIdGrupo())) {
                continue;
            }

            if (depto.getFechaCreacion() != null) {
                Historial h = crearHistorialVirtual(
                    TipoAccionHistorial.DEPARTAMENTO_CREADO,
                    "Departamento creado: " + depto.getNombre(),
                    depto.getFechaCreacion(),
                    depto.getSubgrupo().getGrupo(),
                    null,
                    depto.getCreadoPor(),
                    depto.getIdDepartamento(),
                    "DEPARTAMENTO"
                );
                historial.add(h);
            }
        }

        return historial;
    }

    private List<Historial> obtenerHistorialSubgrupos(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        List<Subgrupo> subgrupos = subgrupoRepository.findAll();
        List<Historial> historial = new ArrayList<>();

        for (Subgrupo subgrupo : subgrupos) {
            if (subgrupo.getGrupo() == null || !subgrupo.getGrupo().getIdGrupo().equals(filtros.getIdGrupo())) {
                continue;
            }

            if (subgrupo.getFechaCreacion() != null) {
                Historial h = crearHistorialVirtual(
                    TipoAccionHistorial.SUBGRUPO_CREADO,
                    "Subgrupo creado: " + subgrupo.getNombre(),
                    subgrupo.getFechaCreacion(),
                    subgrupo.getGrupo(),
                    null,
                    subgrupo.getCreadoPor(),
                    subgrupo.getIdSubgrupo(),
                    "SUBGRUPO"
                );
                historial.add(h);
            }
        }

        return historial;
    }

    private List<Historial> obtenerHistorialEventos(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        List<Evento> eventos = eventoRepository.findAll();
        List<Historial> historial = new ArrayList<>();

        for (Evento evento : eventos) {
            // Obtener grupo desde el creador
            Grupo grupo = evento.getCreadoPor() != null ? evento.getCreadoPor().getGrupo() : null;
            if (grupo == null || !grupo.getIdGrupo().equals(filtros.getIdGrupo())) {
                continue;
            }

            if (evento.getFechaCreacion() != null) {
                Historial h = crearHistorialVirtual(
                    TipoAccionHistorial.EVENTO_CREADO,
                    "Evento creado: " + evento.getTitulo(),
                    evento.getFechaCreacion(),
                    grupo,
                    null,
                    evento.getCreadoPor(),
                    evento.getIdEvento(),
                    "EVENTO"
                );
                historial.add(h);
            }
        }

        return historial;
    }

    private Historial crearHistorialVirtual(
            TipoAccionHistorial tipoAccion,
            String descripcion,
            LocalDateTime fechaHora,
            Grupo grupo,
            Usuario usuario,
            Usuario realizadoPor,
            Long entidadId,
            String tipoEntidad) {

        Historial h = new Historial();
        h.setTipoAccion(tipoAccion);
        h.setDescripcion(descripcion);
        h.setFechaHora(fechaHora);
        h.setGrupo(grupo);
        h.setUsuario(usuario);
        h.setRealizadoPor(realizadoPor);
        h.setEntidadRelacionadaId(entidadId);
        h.setTipoEntidad(tipoEntidad);

        return h;
    }

    private List<Historial> aplicarFiltrosAdicionales(List<Historial> historial,
            com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {

        return historial.stream()
            .filter(h -> {
                // Filtro de usuario
                if (filtros.tieneUsuario()) {
                    boolean esRealizadoPor = h.getRealizadoPor() != null &&
                            h.getRealizadoPor().getIdUsuario().equals(filtros.getIdUsuario());
                    boolean esAfectado = h.getUsuario() != null &&
                            h.getUsuario().getIdUsuario().equals(filtros.getIdUsuario());
                    if (!esRealizadoPor && !esAfectado) {
                        return false;
                    }
                }

                // Filtro de departamento
                if (filtros.tieneDepartamento()) {
                    boolean perteneceAlDepartamento = false;

                    // Si es un historial de departamento, verificar directamente
                    if ("DEPARTAMENTO".equals(h.getTipoEntidad()) &&
                        h.getEntidadRelacionadaId() != null &&
                        h.getEntidadRelacionadaId().equals(filtros.getIdDepartamento())) {
                        perteneceAlDepartamento = true;
                    }

                    // Verificar si el usuario afectado pertenece al departamento
                    if (!perteneceAlDepartamento && h.getUsuario() != null &&
                        h.getUsuario().getDepartamento() != null &&
                        h.getUsuario().getDepartamento().getIdDepartamento().equals(filtros.getIdDepartamento())) {
                        perteneceAlDepartamento = true;
                    }

                    // Verificar si quien realizó la acción pertenece al departamento
                    if (!perteneceAlDepartamento && h.getRealizadoPor() != null &&
                        h.getRealizadoPor().getDepartamento() != null &&
                        h.getRealizadoPor().getDepartamento().getIdDepartamento().equals(filtros.getIdDepartamento())) {
                        perteneceAlDepartamento = true;
                    }

                    // Si es una tarea, verificar si está asignada al departamento
                    if (!perteneceAlDepartamento && "TAREA".equals(h.getTipoEntidad()) &&
                        h.getEntidadRelacionadaId() != null) {
                        Optional<Tarea> tareaOpt = tareaRepository.findByIdTareaWithDepartamentos(h.getEntidadRelacionadaId());
                        if (tareaOpt.isPresent()) {
                            Tarea tarea = tareaOpt.get();
                            if (tarea.getDepartamentosAsignados() != null) {
                                perteneceAlDepartamento = tarea.getDepartamentosAsignados().stream()
                                    .anyMatch(d -> d.getIdDepartamento().equals(filtros.getIdDepartamento()));
                            }
                        }
                    }

                    if (!perteneceAlDepartamento) {
                        return false;
                    }
                }

                // Filtro de tipo de acción
                if (filtros.tieneTipoAccion()) {
                    try {
                        TipoAccionHistorial tipo = TipoAccionHistorial.valueOf(filtros.getTipoAccion().toUpperCase());
                        if (!h.getTipoAccion().equals(tipo)) {
                            return false;
                        }
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }

                // Filtro de entidad
                if (filtros.tieneEntidad()) {
                    if (!filtros.getEntidad().equals(h.getTipoEntidad())) {
                        return false;
                    }
                }

                // Filtro de fechas
                if (filtros.tieneFechas()) {
                    if (filtros.getFechaDesde() != null) {
                        LocalDateTime fechaDesde = filtros.getFechaDesde().atStartOfDay();
                        if (h.getFechaHora().isBefore(fechaDesde)) {
                            return false;
                        }
                    }
                    if (filtros.getFechaHasta() != null) {
                        LocalDateTime fechaHasta = filtros.getFechaHasta().plusDays(1).atStartOfDay();
                        if (!h.getFechaHora().isBefore(fechaHasta)) {
                            return false;
                        }
                    }
                } else if (filtros.tienePeriodo()) {
                    LocalDateTime fechaInicio = getFechaInicioFromPeriodo(filtros.getPeriodo());
                    if (fechaInicio != null && h.getFechaHora().isBefore(fechaInicio)) {
                        return false;
                    }
                }

                // Filtro de búsqueda
                if (filtros.tieneBusqueda()) {
                    String busqueda = filtros.getTextoBusqueda().toLowerCase();
                    if (!h.getDescripcion().toLowerCase().contains(busqueda)) {
                        return false;
                    }
                }

                return true;
            })
            .collect(Collectors.toList());
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private LocalDateTime getFechaInicioFromPeriodo(String periodo) {
        LocalDateTime now = LocalDateTime.now();
        switch (periodo) {
            case "ultimos_30_dias":
                return now.minusDays(30);
            case "ultimos_7_dias":
                return now.minusDays(7);
            case "hoy":
                return now.toLocalDate().atStartOfDay();
            default:
                return null;
        }
    }

    // ========== MÉTODOS DE COMPATIBILIDAD CON VERSIONES ANTERIORES ==========

    /**
     * Método de compatibilidad: Obtiene el historial de un grupo
     */
    public List<Historial> obtenerHistorialGrupo(Long idGrupo) {
        com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros =
            new com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial();
        filtros.setIdGrupo(idGrupo);

        // Necesitamos un usuario del grupo para poder llamar a obtenerConFiltros
        // Buscar cualquier usuario del grupo
        List<Usuario> usuarios = usuarioRepository.findAll();
        Usuario usuarioDelGrupo = usuarios.stream()
            .filter(u -> u.getGrupo() != null && u.getGrupo().getIdGrupo().equals(idGrupo))
            .findFirst()
            .orElse(null);

        if (usuarioDelGrupo == null) {
            System.err.println("No se encontró ningún usuario para el grupo " + idGrupo);
            return new ArrayList<>();
        }

        return obtenerConFiltros(filtros, usuarioDelGrupo);
    }

    /**
     * Método de compatibilidad: Busca historial con filtros
     */
    public List<Historial> buscarHistorialConFiltros(
            Long idGrupo,
            TipoAccionHistorial tipoAccion,
            Long idUsuario,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta) {

        com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros =
            new com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial();
        filtros.setIdGrupo(idGrupo);

        if (tipoAccion != null) {
            filtros.setTipoAccion(tipoAccion.name());
        }
        if (idUsuario != null) {
            filtros.setIdUsuario(idUsuario);
        }
        if (fechaDesde != null) {
            filtros.setFechaDesde(fechaDesde.toLocalDate());
        }
        if (fechaHasta != null) {
            filtros.setFechaHasta(fechaHasta.toLocalDate());
        }

        // Buscar un usuario del grupo
        List<Usuario> usuarios = usuarioRepository.findAll();
        Usuario usuarioDelGrupo = usuarios.stream()
            .filter(u -> u.getGrupo() != null && u.getGrupo().getIdGrupo().equals(idGrupo))
            .findFirst()
            .orElse(null);

        if (usuarioDelGrupo == null) {
            System.err.println("No se encontró ningún usuario para el grupo " + idGrupo);
            return new ArrayList<>();
        }

        return obtenerConFiltros(filtros, usuarioDelGrupo);
    }
}
