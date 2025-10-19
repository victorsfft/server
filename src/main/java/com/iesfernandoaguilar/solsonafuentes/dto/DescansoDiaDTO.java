package com.iesfernandoaguilar.solsonafuentes.dto;

import java.sql.Time;

public class DescansoDiaDTO {
    private Long idDescanso;
    private String tipoDescanso;
    private Integer duracionMinutos;
    private Time horaInicio;
    private Long diaId;
    
    // Constructores
    public DescansoDiaDTO() {}
    
    public DescansoDiaDTO(String tipoDescanso, Integer duracionMinutos, Time horaInicio) {
        this.tipoDescanso = tipoDescanso;
        this.duracionMinutos = duracionMinutos;
        this.horaInicio = horaInicio;
    }
    
    // Getters y setters...
    public Long getIdDescanso() {
        return idDescanso;
    }

    public void setIdDescanso(Long idDescanso) {
        this.idDescanso = idDescanso;
    }

    public String getTipoDescanso() {
        return tipoDescanso;
    }

    public void setTipoDescanso(String tipoDescanso) {
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

    public Long getDiaId() {
        return diaId;
    }

    public void setDiaId(Long diaId) {
        this.diaId = diaId;
    }
    

}
