package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoAccionHistorial;
import com.iesfernandoaguilar.solsonafuentes.model.Historial;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistorialDTO {
    private Long idHistorial;
    private TipoAccionHistorial tipoAccion;
    private String descripcion;
    private LocalDateTime fechaHora;
    private Long usuarioId;
    private String usuarioNombre;
    private Long grupoId;
    private Long entidadRelacionadaId;
    private String tipoEntidad;
    private String valorAnterior;
    private String valorNuevo;
    private Long realizadoPorId;
    private String realizadoPorNombre;

    // Constructores
    public HistorialDTO() {
    }

    // Método estático para convertir desde entidad
    public static HistorialDTO fromEntity(Historial historial) {
        HistorialDTO dto = new HistorialDTO();
        dto.setIdHistorial(historial.getIdHistorial());
        dto.setTipoAccion(historial.getTipoAccion());
        dto.setDescripcion(historial.getDescripcion());
        dto.setFechaHora(historial.getFechaHora());
        dto.setEntidadRelacionadaId(historial.getEntidadRelacionadaId());
        dto.setTipoEntidad(historial.getTipoEntidad());
        dto.setValorAnterior(historial.getValorAnterior());
        dto.setValorNuevo(historial.getValorNuevo());

        if (historial.getUsuario() != null) {
            dto.setUsuarioId(historial.getUsuario().getIdUsuario());
            dto.setUsuarioNombre(historial.getUsuario().getNombre());
        }

        if (historial.getGrupo() != null) {
            dto.setGrupoId(historial.getGrupo().getIdGrupo());
        }

        if (historial.getRealizadoPor() != null) {
            dto.setRealizadoPorId(historial.getRealizadoPor().getIdUsuario());
            dto.setRealizadoPorNombre(historial.getRealizadoPor().getNombre());
        }

        return dto;
    }

    // Getters y setters
    public Long getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public TipoAccionHistorial getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(TipoAccionHistorial tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getEntidadRelacionadaId() {
        return entidadRelacionadaId;
    }

    public void setEntidadRelacionadaId(Long entidadRelacionadaId) {
        this.entidadRelacionadaId = entidadRelacionadaId;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public Long getRealizadoPorId() {
        return realizadoPorId;
    }

    public void setRealizadoPorId(Long realizadoPorId) {
        this.realizadoPorId = realizadoPorId;
    }

    public String getRealizadoPorNombre() {
        return realizadoPorNombre;
    }

    public void setRealizadoPorNombre(String realizadoPorNombre) {
        this.realizadoPorNombre = realizadoPorNombre;
    }
}
