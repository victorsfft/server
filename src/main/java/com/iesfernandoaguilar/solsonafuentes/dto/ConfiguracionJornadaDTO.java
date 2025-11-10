package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;

import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;

public class ConfiguracionJornadaDTO {
    private Long idConfig;
    private String nombreConfig;
    private Date fechaCreacion;
    private Long grupoId;
    private Long creadoPorId;
    private List<Long> horariosIds;
    private List<Long> jornadasLaboralesIds;
    private List<HorarioDiaDTO> horarios; // Horarios completos (solo se incluyen cuando se necesitan)

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

    public List<HorarioDiaDTO> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDiaDTO> horarios) {
        this.horarios = horarios;
    }

    // Método fromEntity (por defecto no incluye horarios completos)
    public static ConfiguracionJornadaDTO fromEntity(ConfiguracionJornada configuracion) {
        return fromEntity(configuracion, false);
    }

    // Método fromEntity con opción de incluir horarios completos
    public static ConfiguracionJornadaDTO fromEntity(ConfiguracionJornada configuracion, boolean incluirHorariosCompletos) {
        ConfiguracionJornadaDTO dto = new ConfiguracionJornadaDTO();
        dto.setIdConfig(configuracion.getIdConfig());
        dto.setNombreConfig(configuracion.getNombreConfig());
        dto.setFechaCreacion(configuracion.getFechaCreacion() != null
            ? java.sql.Timestamp.valueOf(configuracion.getFechaCreacion()) : null);
        dto.setGrupoId(configuracion.getGrupo() != null ? configuracion.getGrupo().getIdGrupo() : null);
        dto.setCreadoPorId(configuracion.getCreadoPor() != null ? configuracion.getCreadoPor().getIdUsuario() : null);

        // Solo intentar acceder a las colecciones si están inicializadas
        if (configuracion.getHorarios() != null && Hibernate.isInitialized(configuracion.getHorarios())) {
            dto.setHorariosIds(configuracion.getHorarios().stream()
                .map(h -> h.getIdDia())
                .collect(Collectors.toList()));

            // Si se solicita, incluir los horarios completos
            if (incluirHorariosCompletos) {
                dto.setHorarios(configuracion.getHorarios().stream()
                    .map(HorarioDiaDTO::fromEntity)
                    .collect(Collectors.toList()));
            }
        } else {
            dto.setHorariosIds(new ArrayList<>());
        }

        if (configuracion.getJornadasLaborales() != null && Hibernate.isInitialized(configuracion.getJornadasLaborales())) {
            dto.setJornadasLaboralesIds(configuracion.getJornadasLaborales().stream()
                .map(j -> j.getIdJornada())
                .collect(Collectors.toList()));
        } else {
            dto.setJornadasLaboralesIds(new ArrayList<>());
        }

        return dto;
    }

}