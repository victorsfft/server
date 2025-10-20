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
@Table(name = "notificacion")
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

    // Relación con Grupo (antes id_entidad_invitacion)
    @ManyToOne
    @JoinColumn(name = "id_entidad_invitacion") // columna real
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_invitador")
    private Usuario usuarioInvitador;

    @ManyToOne
    @JoinColumn(name = "id_solicitud_grupo", referencedColumnName = "id_solicitud")
    private SolicitudGrupo solicitudGrupo;

    @ManyToOne
    @JoinColumn(name = "id_subgrupo")
    private Subgrupo subgrupo;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false)
    private Boolean accionRequerida = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // --- Getters y Setters ---

    public Notificacion(Usuario usuarioDestino, String titulo, TipoNotificacion tipo, Grupo grupo,
                        Usuario usuarioInvitador, Subgrupo subgrupo, Departamento departamento,
                        EstadoNotificacion estado) {
        this.usuarioDestino = usuarioDestino;
        this.titulo = titulo;
        this.tipo = tipo;
        this.grupo = grupo;
        this.usuarioInvitador = usuarioInvitador;
        this.subgrupo = subgrupo;
        this.departamento = departamento;
        this.estado = estado;
    }

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

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
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

    public Subgrupo getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(Subgrupo subgrupo) {
        this.subgrupo = subgrupo;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
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
