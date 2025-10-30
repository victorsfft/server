package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;

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

    // Método fromEntity
    public static ConfiguracionJornadaDTO fromEntity(ConfiguracionJornada configuracion) {
        ConfiguracionJornadaDTO dto = new ConfiguracionJornadaDTO();
        dto.setIdConfig(configuracion.getIdConfig());
        dto.setNombreConfig(configuracion.getNombreConfig());
        dto.setEstado(configuracion.getEstado() != null ? configuracion.getEstado().name() : null);
        dto.setFechaInicio(configuracion.getFechaInicio() != null
            ? java.sql.Date.valueOf(configuracion.getFechaInicio()) : null);
        dto.setFechaFin(configuracion.getFechaFin() != null
            ? java.sql.Date.valueOf(configuracion.getFechaFin()) : null);
        dto.setFechaCreacion(configuracion.getFechaCreacion() != null
            ? java.sql.Timestamp.valueOf(configuracion.getFechaCreacion()) : null);
        dto.setGrupoId(configuracion.getGrupo() != null ? configuracion.getGrupo().getIdGrupo() : null);
        dto.setCreadoPorId(configuracion.getCreadoPor() != null ? configuracion.getCreadoPor().getIdUsuario() : null);

        if (configuracion.getHorarios() != null) {
            dto.setHorariosIds(configuracion.getHorarios().stream()
                .map(h -> h.getIdDia())
                .collect(Collectors.toList()));
        }

        if (configuracion.getJornadasLaborales() != null) {
            dto.setJornadasLaboralesIds(configuracion.getJornadasLaborales().stream()
                .map(j -> j.getIdJornada())
                .collect(Collectors.toList()));
        }

        return dto;
    }

}