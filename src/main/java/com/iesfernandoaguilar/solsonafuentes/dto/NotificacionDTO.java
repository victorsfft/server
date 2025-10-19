package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;

public class NotificacionDTO{
    private Long idNotificacion;
    private Long usuarioDestinoId;
    private String titulo;
    private String mensaje;
    private TipoNotificacion tipo;
    private Long idEntidadInvitacion;
    private Long usuarioInvitadorId;
    private Long solicitudGrupoId;
    private EstadoNotificacion estado;
    private Boolean accionRequerida;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public NotificacionDTO() {}


    // Getters y Setters
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

    public Long getIdEntidadInvitacion() {
        return idEntidadInvitacion;
    }

    public void setIdEntidadInvitacion(Long idEntidadInvitacion) {
        this.idEntidadInvitacion = idEntidadInvitacion;
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

    public TipoNotificacion getTipo() {
        return tipo;
    }


    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }


    public EstadoNotificacion getEstado() {
        return estado;
    }


    public void setEstado(EstadoNotificacion estado) {
        this.estado = estado;
    }

    


    public static NotificacionDTO fromEntity(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(notificacion.getIdNotificacion());
        dto.setUsuarioDestinoId(notificacion.getUsuarioDestino().getIdUsuario());
        dto.setTitulo(notificacion.getTitulo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setTipo(notificacion.getTipo());
        dto.setIdEntidadInvitacion(notificacion.getIdEntidadInvitacion());
        dto.setUsuarioInvitadorId(notificacion.getUsuarioInvitador() != null ? notificacion.getUsuarioInvitador().getIdUsuario() : null);
        dto.setSolicitudGrupoId(notificacion.getSolicitudGrupo() != null ? notificacion.getSolicitudGrupo().getIdSolicitud() : null);
        dto.setEstado(notificacion.getEstado());
        dto.setAccionRequerida(notificacion.getAccionRequerida());
        dto.setFechaCreacion(notificacion.getFechaCreacion());

        return dto;
    }
}