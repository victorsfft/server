package com.iesfernandoaguilar.solsonafuentes.dto;

import java.sql.Time;
import java.util.List;

public class HorarioDiaDTO {
    private Long idDia;
    private String diaSemana;
    private Boolean esLaborable;
    private Time horaEntrada;
    private Time horaSalida;
    private Long configuracionId;
    private List<Long> descansosIds;
    
    // Constructores
    public HorarioDiaDTO() {}
    
    public HorarioDiaDTO(String diaSemana, Long configuracionId) {
        this.diaSemana = diaSemana;
        this.configuracionId = configuracionId;
    }
    
    // Getters y setters...
    public Long getIdDia() {
        return idDia;
    }

    public void setIdDia(Long idDia) {
        this.idDia = idDia;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
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

    public Long getConfiguracionId() {
        return configuracionId;
    }

    public void setConfiguracionId(Long configuracionId) {
        this.configuracionId = configuracionId;
    }

    public List<Long> getDescansosIds() {
        return descansosIds;
    }

    public void setDescansosIds(List<Long> descansosIds) {
        this.descansosIds = descansosIds;
    }
    
    
}