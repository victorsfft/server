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

        List<Historial> resultados = historialRepository.obtenerHistorialPorGrupo(filtros.getIdGrupo());

        return aplicarFiltrosAHistorial(resultados, filtros);
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Historial> aplicarFiltrosAHistorial(List<Historial> lista, com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosHistorial filtros) {
        return lista.stream()
            .filter(h -> !filtros.tieneUsuario() || (h.getRealizadoPor() != null && h.getRealizadoPor().getIdUsuario().equals(filtros.getIdUsuario())))
            .filter(h -> !filtros.tieneTipoAccion() || (h.getTipoAccion() != null && h.getTipoAccion().name().equalsIgnoreCase(filtros.getTipoAccion())))
            .filter(h -> !filtros.tieneEntidad() || (h.getTipoEntidad() != null && h.getTipoEntidad().equalsIgnoreCase(filtros.getEntidad())))
            .filter(h -> {
                if (!filtros.tieneFechas()) return true;
                LocalDateTime fechaHistorial = h.getFechaHora();
                if (filtros.getFechaDesde() != null && fechaHistorial.isBefore(filtros.getFechaDesde().atStartOfDay())) return false;
                if (filtros.getFechaHasta() != null && fechaHistorial.isAfter(filtros.getFechaHasta().plusDays(1).atStartOfDay())) return false;
                return true;
            })
            .filter(h -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                return h.getDescripcion().toLowerCase().contains(busqueda);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
