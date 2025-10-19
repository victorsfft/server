package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;
import java.util.List;

public class DepartamentoDTO {
    private Long idDepartamento;
    private String nombre;
    private Date fechaCreacion;
    private Long subgrupoId;
    private Long creadoPorId;
    private List<Long> usuariosIds;
    private List<Long> tareasAsignadasIds;
    private List<Long> eventosAsignadosIds;
    
    // Constructores
    public DepartamentoDTO() {}
    
    public DepartamentoDTO(String nombre, Long subgrupoId) {
        this.nombre = nombre;
        this.subgrupoId = subgrupoId;
    }
    
    // Getters y setters...
    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getSubgrupoId() {
        return subgrupoId;
    }

    public void setSubgrupoId(Long subgrupoId) {
        this.subgrupoId = subgrupoId;
    }

    public Long getCreadoPorId() {
        return creadoPorId;
    }

    public void setCreadoPorId(Long creadoPorId) {
        this.creadoPorId = creadoPorId;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    public void setUsuariosIds(List<Long> usuariosIds) {
        this.usuariosIds = usuariosIds;
    }

    public List<Long> getTareasAsignadasIds() {
        return tareasAsignadasIds;
    }

    public void setTareasAsignadasIds(List<Long> tareasAsignadasIds) {
        this.tareasAsignadasIds = tareasAsignadasIds;
    }

    public List<Long> getEventosAsignadosIds() {
        return eventosAsignadosIds;
    }

    public void setEventosAsignadosIds(List<Long> eventosAsignadosIds) {
        this.eventosAsignadosIds = eventosAsignadosIds;
    }
    
}