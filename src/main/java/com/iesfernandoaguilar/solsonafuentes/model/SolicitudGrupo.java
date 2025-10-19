package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoSolicitud;

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
@Table(name = "Solicitud_grupo")
public class SolicitudGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @Column(nullable = false, length = 100)
    private String nombreGrupo;

    @Column(length = 20)
    private String vat;

    @ManyToOne
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    private Usuario usuarioSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaResolucion;

    public SolicitudGrupo() {
    }

    public SolicitudGrupo(LocalDateTime fechaSolicitud, String nombreGrupo, Usuario usuarioSolicitante, String vat) {
        this.fechaSolicitud = fechaSolicitud;
        this.nombreGrupo = nombreGrupo;
        this.usuarioSolicitante = usuarioSolicitante;
        this.vat = vat;
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    

    
}

