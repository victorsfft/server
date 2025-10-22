package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iesfernandoaguilar.solsonafuentes.model.Estadistica;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadisticaDTO {
    private Long idEstadistica;
    private LocalDate fecha;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idGrupo;
    private Integer tareasCompletadasSemana;
    private Integer tareasPendientesSemana;
    private Integer tareasRetrasadasSemana;
    private Double horasTotalesSemana;
    private Double horasExtraSemana;
    private Double cumplimientoJornadaSemana;
    private Integer tareasCompletadasTotales;
    private Integer tareasPendientesTotales;
    private Integer tareasRetrasadasTotales;
    private Double horasTotales;
    private Double horasExtraTotales;
    private Double puntualidadJornadaTotales;
    private Double cumplimientoJornadaTotales;

    public EstadisticaDTO() {
    }

    public static EstadisticaDTO fromEntity(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setIdEstadistica(estadistica.getIdEstadistica());
        dto.setFecha(estadistica.getFecha());
        dto.setTareasCompletadasSemana(estadistica.getTareasCompletadasSemana());
        dto.setTareasPendientesSemana(estadistica.getTareasPendientesSemana());
        dto.setTareasRetrasadasSemana(estadistica.getTareasRetrasadasSemana());
        dto.setHorasTotalesSemana(estadistica.getHorasTotalesSemana());
        dto.setHorasExtraSemana(estadistica.getHorasExtraSemana());
        dto.setCumplimientoJornadaSemana(estadistica.getCumplimientoJornadaSemana());
        dto.setTareasCompletadasTotales(estadistica.getTareasCompletadasTotales());
        dto.setTareasPendientesTotales(estadistica.getTareasPendientesTotales());
        dto.setTareasRetrasadasTotales(estadistica.getTareasRetrasadasTotales());
        dto.setHorasTotales(estadistica.getHorasTotales());
        dto.setHorasExtraTotales(estadistica.getHorasExtraTotales());
        dto.setPuntualidadJornadaTotales(estadistica.getPuntualidadJornadaTotales());
        dto.setCumplimientoJornadaTotales(estadistica.getCumplimientoJornadaTotales());

        if (estadistica.getUsuario() != null) {
            dto.setIdUsuario(estadistica.getUsuario().getIdUsuario());
            dto.setNombreUsuario(estadistica.getUsuario().getNombre());
        }

        if (estadistica.getGrupo() != null) {
            dto.setIdGrupo(estadistica.getGrupo().getIdGrupo());
        }

        return dto;
    }

    public Long getIdEstadistica() { return idEstadistica; }
    public void setIdEstadistica(Long idEstadistica) { this.idEstadistica = idEstadistica; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public Long getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Long idGrupo) { this.idGrupo = idGrupo; }
    public Integer getTareasCompletadasSemana() { return tareasCompletadasSemana; }
    public void setTareasCompletadasSemana(Integer tareasCompletadasSemana) { this.tareasCompletadasSemana = tareasCompletadasSemana; }
    public Integer getTareasPendientesSemana() { return tareasPendientesSemana; }
    public void setTareasPendientesSemana(Integer tareasPendientesSemana) { this.tareasPendientesSemana = tareasPendientesSemana; }
    public Integer getTareasRetrasadasSemana() { return tareasRetrasadasSemana; }
    public void setTareasRetrasadasSemana(Integer tareasRetrasadasSemana) { this.tareasRetrasadasSemana = tareasRetrasadasSemana; }
    public Double getHorasTotalesSemana() { return horasTotalesSemana; }
    public void setHorasTotalesSemana(Double horasTotalesSemana) { this.horasTotalesSemana = horasTotalesSemana; }
    public Double getHorasExtraSemana() { return horasExtraSemana; }
    public void setHorasExtraSemana(Double horasExtraSemana) { this.horasExtraSemana = horasExtraSemana; }
    public Double getCumplimientoJornadaSemana() { return cumplimientoJornadaSemana; }
    public void setCumplimientoJornadaSemana(Double cumplimientoJornadaSemana) { this.cumplimientoJornadaSemana = cumplimientoJornadaSemana; }
    public Integer getTareasCompletadasTotales() { return tareasCompletadasTotales; }
    public void setTareasCompletadasTotales(Integer tareasCompletadasTotales) { this.tareasCompletadasTotales = tareasCompletadasTotales; }
    public Integer getTareasPendientesTotales() { return tareasPendientesTotales; }
    public void setTareasPendientesTotales(Integer tareasPendientesTotales) { this.tareasPendientesTotales = tareasPendientesTotales; }
    public Integer getTareasRetrasadasTotales() { return tareasRetrasadasTotales; }
    public void setTareasRetrasadasTotales(Integer tareasRetrasadasTotales) { this.tareasRetrasadasTotales = tareasRetrasadasTotales; }
    public Double getHorasTotales() { return horasTotales; }
    public void setHorasTotales(Double horasTotales) { this.horasTotales = horasTotales; }
    public Double getHorasExtraTotales() { return horasExtraTotales; }
    public void setHorasExtraTotales(Double horasExtraTotales) { this.horasExtraTotales = horasExtraTotales; }
    public Double getPuntualidadJornadaTotales() { return puntualidadJornadaTotales; }
    public void setPuntualidadJornadaTotales(Double puntualidadJornadaTotales) { this.puntualidadJornadaTotales = puntualidadJornadaTotales; }
    public Double getCumplimientoJornadaTotales() { return cumplimientoJornadaTotales; }
    public void setCumplimientoJornadaTotales(Double cumplimientoJornadaTotales) { this.cumplimientoJornadaTotales = cumplimientoJornadaTotales; }
}
