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
}
