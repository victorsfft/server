package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.TipoAccionHistorial;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Historial;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.HistorialRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    /**
     * Registra una acción en el historial
     */
    @Transactional
    public Historial registrarAccion(
            TipoAccionHistorial tipoAccion,
            String descripcion,
            Grupo grupo,
            Usuario usuario,
            Usuario realizadoPor,
            Long entidadRelacionadaId,
            String tipoEntidad,
            String valorAnterior,
            String valorNuevo) {

        Historial historial = new Historial();
        historial.setTipoAccion(tipoAccion);
        historial.setDescripcion(descripcion);
        historial.setGrupo(grupo);
        historial.setUsuario(usuario);
        historial.setRealizadoPor(realizadoPor);
        historial.setEntidadRelacionadaId(entidadRelacionadaId);
        historial.setTipoEntidad(tipoEntidad);
        historial.setValorAnterior(valorAnterior);
        historial.setValorNuevo(valorNuevo);
        historial.setFechaHora(LocalDateTime.now());

        return historialRepository.save(historial);
    }

    /**
     * Registra una acción simple en el historial
     */
    @Transactional
    public Historial registrarAccionSimple(
            TipoAccionHistorial tipoAccion,
            String descripcion,
            Grupo grupo,
            Usuario realizadoPor) {

        return registrarAccion(tipoAccion, descripcion, grupo, null, realizadoPor, null, null, null, null);
    }

    /**
     * Obtiene el historial completo de un grupo
     */
    public List<Historial> obtenerHistorialGrupo(Long idGrupo) {
        return historialRepository.obtenerHistorialPorGrupo(idGrupo);
    }

    /**
     * Obtiene el historial de un usuario específico
     */
    public List<Historial> obtenerHistorialUsuario(Long idUsuario) {
        return historialRepository.obtenerHistorialPorUsuario(idUsuario);
    }

    /**
     * Obtiene el historial filtrado por tipo de acción
     */
    public List<Historial> obtenerHistorialPorTipo(Long idGrupo, TipoAccionHistorial tipoAccion) {
        return historialRepository.obtenerHistorialPorTipo(idGrupo, tipoAccion);
    }

    /**
     * Obtiene el historial filtrado por rango de fechas
     */
    public List<Historial> obtenerHistorialPorFechas(Long idGrupo, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return historialRepository.obtenerHistorialPorFechas(idGrupo, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene el historial con múltiples filtros
     */
    public List<Historial> buscarHistorialConFiltros(
            Long idGrupo,
            TipoAccionHistorial tipoAccion,
            Long idUsuario,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta) {

        return historialRepository.buscarHistorialConFiltros(idGrupo, tipoAccion, idUsuario, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene el historial de un departamento
     */
    public List<Historial> obtenerHistorialPorDepartamento(Long idGrupo, Long idDepartamento) {
        return historialRepository.obtenerHistorialPorDepartamento(idGrupo, idDepartamento);
    }

    /**
     * Obtiene el historial de una entidad específica (ej: una tarea)
     */
    public List<Historial> obtenerHistorialPorEntidad(Long entidadId, String tipoEntidad) {
        return historialRepository.obtenerHistorialPorEntidad(entidadId, tipoEntidad);
    }

    /**
     * Obtiene el historial de acciones realizadas por un usuario
     */
    public List<Historial> obtenerHistorialRealizadoPor(Long idUsuario) {
        return historialRepository.obtenerHistorialRealizadoPor(idUsuario);
    }

    @Transactional
    public List<Historial> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros, Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Historial");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        System.out.println("DEBUG: Filtros aplicados en HistorialService: " + filtros); // DEBUG

        return historialRepository.findAll(getSpecification(filtros));
    }

    private Specification<Historial> getSpecification(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);
            root.fetch("usuario", JoinType.LEFT);
            root.fetch("realizadoPor", JoinType.LEFT);
            root.fetch("grupo", JoinType.LEFT);
            
            predicates.add(cb.equal(root.get("grupo").get("idGrupo"), filtros.getIdGrupo()));

            if (filtros.tieneUsuario()) {
                Predicate realizadoPor = cb.equal(root.get("realizadoPor").get("idUsuario"), filtros.getIdUsuario());
                Predicate afectado = cb.equal(root.get("usuario").get("idUsuario"), filtros.getIdUsuario());
                predicates.add(cb.or(realizadoPor, afectado));
            }

            if (filtros.tieneTipoAccion()) {
                try {
                    TipoAccionHistorial tipo = TipoAccionHistorial.valueOf(filtros.getTipoAccion().toUpperCase());
                    predicates.add(cb.equal(root.get("tipoAccion"), tipo));
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de acción de historial inválido: " + filtros.getTipoAccion());
                }
            }

            if (filtros.tieneEntidad()) {
                predicates.add(cb.equal(root.get("tipoEntidad"), filtros.getEntidad()));
            }

            if (filtros.tieneFechas()) {
                if (filtros.getFechaDesde() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), filtros.getFechaDesde().atStartOfDay()));
                }
                if (filtros.getFechaHasta() != null) {
                    predicates.add(cb.lessThan(root.get("fechaHora"), filtros.getFechaHasta().plusDays(1).atStartOfDay()));
                }
            } else if (filtros.tienePeriodo()) {
                LocalDateTime fechaInicio = getFechaInicioFromPeriodo(filtros.getPeriodo());
                if (fechaInicio != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), fechaInicio));
                }
            }

            if (filtros.tieneBusqueda()) {
                predicates.add(cb.like(cb.lower(root.get("descripcion")), "%" + filtros.getTextoBusqueda().toLowerCase() + "%"));
            }
            
            query.orderBy(cb.desc(root.get("fechaHora")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
}
