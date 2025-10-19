package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;

public class EstadisticaDTO {
    private Long idEstadistica;
    private Date fecha;
    private Long usuarioId;
    private Long grupoId;
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
    
    // Constructores
    public EstadisticaDTO() {}
    
    public EstadisticaDTO(Date fecha, Long usuarioId) {
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }
    
    // Getters y setters...
    public Long getIdEstadistica() {
        return idEstadistica;
    }

    public void setIdEstadistica(Long idEstadistica) {
        this.idEstadistica = idEstadistica;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Integer getTareasCompletadasSemana() {
        return tareasCompletadasSemana;
    }

    public void setTareasCompletadasSemana(Integer tareasCompletadasSemana) {
        this.tareasCompletadasSemana = tareasCompletadasSemana;
    }

    public Integer getTareasPendientesSemana() {
        return tareasPendientesSemana;
    }

    public void setTareasPendientesSemana(Integer tareasPendientesSemana) {
        this.tareasPendientesSemana = tareasPendientesSemana;
    }

    public Integer getTareasRetrasadasSemana() {
        return tareasRetrasadasSemana;
    }

    public void setTareasRetrasadasSemana(Integer tareasRetrasadasSemana) {
        this.tareasRetrasadasSemana = tareasRetrasadasSemana;
    }

    public Double getHorasTotalesSemana() {
        return horasTotalesSemana;
    }

    public void setHorasTotalesSemana(Double horasTotalesSemana) {
        this.horasTotalesSemana = horasTotalesSemana;
    }

    public Double getHorasExtraSemana() {
        return horasExtraSemana;
    }

    public void setHorasExtraSemana(Double horasExtraSemana) {
        this.horasExtraSemana = horasExtraSemana;
    }

    public Double getCumplimientoJornadaSemana() {
        return cumplimientoJornadaSemana;
    }

    public void setCumplimientoJornadaSemana(Double cumplimientoJornadaSemana) {
        this.cumplimientoJornadaSemana = cumplimientoJornadaSemana;
    }

    public Integer getTareasCompletadasTotales() {
        return tareasCompletadasTotales;
    }

    public void setTareasCompletadasTotales(Integer tareasCompletadasTotales) {
        this.tareasCompletadasTotales = tareasCompletadasTotales;
    }

    public Integer getTareasPendientesTotales() {
        return tareasPendientesTotales;
    }

    public void setTareasPendientesTotales(Integer tareasPendientesTotales) {
        this.tareasPendientesTotales = tareasPendientesTotales;
    }

    public Integer getTareasRetrasadasTotales() {
        return tareasRetrasadasTotales;
    }

    public void setTareasRetrasadasTotales(Integer tareasRetrasadasTotales) {
        this.tareasRetrasadasTotales = tareasRetrasadasTotales;
    }

    public Double getHorasTotales() {
        return horasTotales;
    }

    public void setHorasTotales(Double horasTotales) {
        this.horasTotales = horasTotales;
    }

    public Double getHorasExtraTotales() {
        return horasExtraTotales;
    }

    public void setHorasExtraTotales(Double horasExtraTotales) {
        this.horasExtraTotales = horasExtraTotales;
    }

    public Double getPuntualidadJornadaTotales() {
        return puntualidadJornadaTotales;
    }

    public void setPuntualidadJornadaTotales(Double puntualidadJornadaTotales) {
        this.puntualidadJornadaTotales = puntualidadJornadaTotales;
    }

    public Double getCumplimientoJornadaTotales() {
        return cumplimientoJornadaTotales;
    }

    public void setCumplimientoJornadaTotales(Double cumplimientoJornadaTotales) {
        this.cumplimientoJornadaTotales = cumplimientoJornadaTotales;
    }
    
}