package com.iesfernandoaguilar.solsonafuentes.dto;

import java.sql.Time;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoDescanso;
import com.iesfernandoaguilar.solsonafuentes.model.DescansoJornada;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DescansoJornadaDTO {
    private Long idDescanso;
    private TipoDescanso tipoDescanso;
    private Integer duracionMinutos;
    private Time horaInicio;
    private Long idJornada;

    public DescansoJornadaDTO() {
    }

    public static DescansoJornadaDTO fromEntity(DescansoJornada descanso) {
        DescansoJornadaDTO dto = new DescansoJornadaDTO();
        dto.setIdDescanso(descanso.getIdDescanso());
        dto.setTipoDescanso(descanso.getTipoDescanso());
        dto.setDuracionMinutos(descanso.getDuracionMinutos());
        dto.setHoraInicio(descanso.getHoraInicio());

        if (descanso.getJornada() != null) {
            dto.setIdJornada(descanso.getJornada().getIdJornada());
        }

        return dto;
    }

    public Long getIdDescanso() { return idDescanso; }
    public void setIdDescanso(Long idDescanso) { this.idDescanso = idDescanso; }
    public TipoDescanso getTipoDescanso() { return tipoDescanso; }
    public void setTipoDescanso(TipoDescanso tipoDescanso) { this.tipoDescanso = tipoDescanso; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }
    public Long getIdJornada() { return idJornada; }
    public void setIdJornada(Long idJornada) { this.idJornada = idJornada; }
}
