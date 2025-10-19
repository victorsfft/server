package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class EventoDTO {
    private Long idEvento;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean seRepite;
    private Integer diasRepeticion;
    private Date fechaCreacion;
    private Long creadoPorId;
    private List<Long> usuariosAsistentesIds;
    private List<Long> departamentosInvitadosIds;
    
    // Constructores
    public EventoDTO() {}
    
    public EventoDTO(String titulo, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y setters...
    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
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

    public Boolean getSeRepite() {
        return seRepite;
    }

    public void setSeRepite(Boolean seRepite) {
        this.seRepite = seRepite;
    }

    public Integer getDiasRepeticion() {
        return diasRepeticion;
    }

    public void setDiasRepeticion(Integer diasRepeticion) {
        this.diasRepeticion = diasRepeticion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getCreadoPorId() {
        return creadoPorId;
    }

    public void setCreadoPorId(Long creadoPorId) {
        this.creadoPorId = creadoPorId;
    }

    public List<Long> getUsuariosAsistentesIds() {
        return usuariosAsistentesIds;
    }

    public void setUsuariosAsistentesIds(List<Long> usuariosAsistentesIds) {
        this.usuariosAsistentesIds = usuariosAsistentesIds;
    }

    public List<Long> getDepartamentosInvitadosIds() {
        return departamentosInvitadosIds;
    }

    public void setDepartamentosInvitadosIds(List<Long> departamentosInvitadosIds) {
        this.departamentosInvitadosIds = departamentosInvitadosIds;
    }
    
}