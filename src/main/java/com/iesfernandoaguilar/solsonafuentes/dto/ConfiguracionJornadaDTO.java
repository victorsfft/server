package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;
import java.util.List;

public class ConfiguracionJornadaDTO {
    private Long idConfig;
    private String nombreConfig;
    private String estado;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaCreacion;
    private Long grupoId;
    private Long creadoPorId;
    private List<Long> horariosIds;
    private List<Long> jornadasLaboralesIds;
    
    // Constructores
    public ConfiguracionJornadaDTO() {}
    
    public ConfiguracionJornadaDTO(String nombreConfig, Long grupoId) {
        this.nombreConfig = nombreConfig;
        this.grupoId = grupoId;
    }
    
    // Getters y setters...
    public Long getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Long idConfig) {
        this.idConfig = idConfig;
    }

    public String getNombreConfig() {
        return nombreConfig;
    }

    public void setNombreConfig(String nombreConfig) {
        this.nombreConfig = nombreConfig;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getCreadoPorId() {
        return creadoPorId;
    }

    public void setCreadoPorId(Long creadoPorId) {
        this.creadoPorId = creadoPorId;
    }

    public List<Long> getHorariosIds() {
        return horariosIds;
    }

    public void setHorariosIds(List<Long> horariosIds) {
        this.horariosIds = horariosIds;
    }

    public List<Long> getJornadasLaboralesIds() {
        return jornadasLaboralesIds;
    }

    public void setJornadasLaboralesIds(List<Long> jornadasLaboralesIds) {
        this.jornadasLaboralesIds = jornadasLaboralesIds;
    }
    
}