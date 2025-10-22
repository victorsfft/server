package com.iesfernandoaguilar.solsonafuentes.model;

import java.sql.Time;

import com.iesfernandoaguilar.solsonafuentes.enums.TipoDescanso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "Descanso_jornada")
public class DescansoJornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDescanso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descanso")
    private TipoDescanso tipoDescanso = TipoDescanso.DESCANSO;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Temporal(TemporalType.TIME)
    @Column(name = "hora_inicio", nullable = false)
    private Time horaInicio;

    // Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jornada", nullable = false)
    private JornadaLaboral jornada;

    // Constructores
    public DescansoJornada() {
    }

    // Getters y setters
    public Long getIdDescanso() {
        return idDescanso;
    }

    public void setIdDescanso(Long idDescanso) {
        this.idDescanso = idDescanso;
    }

    public TipoDescanso getTipoDescanso() {
        return tipoDescanso;
    }

    public void setTipoDescanso(TipoDescanso tipoDescanso) {
        this.tipoDescanso = tipoDescanso;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public JornadaLaboral getJornada() {
        return jornada;
    }

    public void setJornada(JornadaLaboral jornada) {
        this.jornada = jornada;
    }
}
