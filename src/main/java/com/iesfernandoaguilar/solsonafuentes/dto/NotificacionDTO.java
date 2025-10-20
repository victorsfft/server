package com.iesfernandoaguilar.solsonafuentes.dto;


import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;

public class NotificacionDTO {

    private Long idNotificacion;
    private Long usuarioDestinoId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Long grupoId; // antes idEntidadInvitacion
    private Long usuarioInvitadorId;
    private Long solicitudGrupoId;
    private Long subgrupoId; // nuevo
    private Long departamentoId; // nuevo
    private String estado;
    private Boolean accionRequerida;
    private LocalDateTime fechaCreacion;

    public NotificacionDTO() {}

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Long getUsuarioDestinoId() {
        return usuarioDestinoId;
    }

    public void setUsuarioDestinoId(Long usuarioDestinoId) {
        this.usuarioDestinoId = usuarioDestinoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getUsuarioInvitadorId() {
        return usuarioInvitadorId;
    }

    public void setUsuarioInvitadorId(Long usuarioInvitadorId) {
        this.usuarioInvitadorId = usuarioInvitadorId;
    }

    public Long getSolicitudGrupoId() {
        return solicitudGrupoId;
    }

    public void setSolicitudGrupoId(Long solicitudGrupoId) {
        this.solicitudGrupoId = solicitudGrupoId;
    }

    public Long getSubgrupoId() {
        return subgrupoId;
    }

    public void setSubgrupoId(Long subgrupoId) {
        this.subgrupoId = subgrupoId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAccionRequerida() {
        return accionRequerida;
    }

    public void setAccionRequerida(Boolean accionRequerida) {
        this.accionRequerida = accionRequerida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public static NotificacionDTO fromEntity(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();
        if (notificacion != null) {
            dto.setIdNotificacion(notificacion.getIdNotificacion());
            dto.setUsuarioDestinoId(notificacion.getUsuarioDestino() != null ? notificacion.getUsuarioDestino().getIdUsuario() : null);
            dto.setTitulo(notificacion.getTitulo());
            dto.setMensaje(notificacion.getMensaje());
            dto.setTipo(notificacion.getTipo() != null ? notificacion.getTipo().name() : null);
            dto.setGrupoId(notificacion.getGrupo() != null ? notificacion.getGrupo().getIdGrupo() : null);
            dto.setUsuarioInvitadorId(notificacion.getUsuarioInvitador() != null ? notificacion.getUsuarioInvitador().getIdUsuario() : null);
            dto.setSolicitudGrupoId(notificacion.getSolicitudGrupo() != null ? notificacion.getSolicitudGrupo().getIdSolicitud() : null);
            dto.setSubgrupoId(notificacion.getSubgrupo() != null ? notificacion.getSubgrupo().getIdSubgrupo() : null);
            dto.setDepartamentoId(notificacion.getDepartamento() != null ? notificacion.getDepartamento().getIdDepartamento() : null);
            dto.setEstado(notificacion.getEstado() != null ? notificacion.getEstado().name() : null);
            dto.setAccionRequerida(notificacion.getAccionRequerida());
            dto.setFechaCreacion(notificacion.getFechaCreacion());
        }
        return dto;
    }
}
