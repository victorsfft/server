package com.iesfernandoaguilar.solsonafuentes.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.enums.DiaSemana;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
@Table(name = "Horario_dia")
public class HorarioDia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDia;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;
    
    @Column(name = "es_laborable")
    private Boolean esLaborable = true;
    
    @Temporal(TemporalType.TIME)
    private Time horaEntrada;
    
    @Temporal(TemporalType.TIME)
    private Time horaSalida;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "dia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DescansoDia> descansos = new ArrayList<>();
  
    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_config", nullable = false)
    private ConfiguracionJornada configuracion;
    
    //Constructores
    public HorarioDia() {
    }

    //Getters y setters
    public Long getIdDia() {
        return idDia;
    }

    public void setIdDia(Long idDia) {
        this.idDia = idDia;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Boolean getEsLaborable() {
        return esLaborable;
    }

    public void setEsLaborable(Boolean esLaborable) {
        this.esLaborable = esLaborable;
    }

    public Time getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Time getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
    }

    public List<DescansoDia> getDescansos() {
        return descansos;
    }

    public void setDescansos(List<DescansoDia> descansos) {
        this.descansos = descansos;
    }

    public ConfiguracionJornada getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(ConfiguracionJornada configuracion) {
        this.configuracion = configuracion;
    }

    

}
