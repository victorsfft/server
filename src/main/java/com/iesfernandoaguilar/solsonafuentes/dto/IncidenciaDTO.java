package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class IncidenciaDTO {
    private Long idIncidencia;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Date fechaCreacion;
    private Long usuarioId;
    private List<Long> comentariosIds;
    
    // Constructores
    public IncidenciaDTO() {}
    
    public IncidenciaDTO(String titulo, String descripcion, Long usuarioId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

    // Getters y setters...
    public Long getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(Long idIncidencia) {
        this.idIncidencia = idIncidencia;
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

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<Long> getComentariosIds() {
        return comentariosIds;
    }

    public void setComentariosIds(List<Long> comentariosIds) {
        this.comentariosIds = comentariosIds;
    }
    
}