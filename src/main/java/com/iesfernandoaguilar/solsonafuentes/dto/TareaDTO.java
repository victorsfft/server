package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;
import java.util.List;

public class TareaDTO {
    private Long idTarea;
    private String prioridad;
    private String estado;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaCreacion;
    private String titulo;
    private String descripcion;
    private Long creadoPorId;
    private List<Long> usuariosAsignadosIds;
    private List<Long> departamentosAsignadosIds;
    private List<Long> tareasDependientesIds;
    
    // Constructores
    public TareaDTO() {}
    
    public TareaDTO(String titulo, String descripcion, String prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    // Getters y setters
    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCreadoPorId() {
        return creadoPorId;
    }

    public void setCreadoPorId(Long creadoPorId) {
        this.creadoPorId = creadoPorId;
    }

    public List<Long> getUsuariosAsignadosIds() {
        return usuariosAsignadosIds;
    }

    public void setUsuariosAsignadosIds(List<Long> usuariosAsignadosIds) {
        this.usuariosAsignadosIds = usuariosAsignadosIds;
    }

    public List<Long> getDepartamentosAsignadosIds() {
        return departamentosAsignadosIds;
    }

    public void setDepartamentosAsignadosIds(List<Long> departamentosAsignadosIds) {
        this.departamentosAsignadosIds = departamentosAsignadosIds;
    }

    public List<Long> getTareasDependientesIds() {
        return tareasDependientesIds;
    }

    public void setTareasDependientesIds(List<Long> tareasDependientesIds) {
        this.tareasDependientesIds = tareasDependientesIds;
    }
    
    
    
}
