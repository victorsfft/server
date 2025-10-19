package com.iesfernandoaguilar.solsonafuentes.model;



import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoNotificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destino", nullable = false)
    private Usuario usuarioDestino;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    // Campos para invitaciones
    @Column(name = "id_entidad_invitacion")
    private Long idEntidadInvitacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_invitador")
    private Usuario usuarioInvitador;

    // Campos para solicitudes de grupo
    @ManyToOne
    @JoinColumn(name = "id_solicitud_grupo",referencedColumnName = "id_solicitud")
    private SolicitudGrupo solicitudGrupo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false)
    private Boolean accionRequerida = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(Usuario usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
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

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public Long getIdEntidadInvitacion() {
        return idEntidadInvitacion;
    }

    public void setIdEntidadInvitacion(Long idEntidadInvitacion) {
        this.idEntidadInvitacion = idEntidadInvitacion;
    }

    public Usuario getUsuarioInvitador() {
        return usuarioInvitador;
    }

    public void setUsuarioInvitador(Usuario usuarioInvitador) {
        this.usuarioInvitador = usuarioInvitador;
    }

    public SolicitudGrupo getSolicitudGrupo() {
        return solicitudGrupo;
    }

    public void setSolicitudGrupo(SolicitudGrupo solicitudGrupo) {
        this.solicitudGrupo = solicitudGrupo;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoNotificacion estado) {
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

     
    
}



