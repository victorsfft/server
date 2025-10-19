package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;

public class AnotacionesDTO {
    private Long idAnotacion;
    private String titulo;
    private String texto;
    private Date fecha;
    private Date fechaCreacion;
    private Long creadoPorId;
    private Long usuarioId;
    
    // Constructores
    public AnotacionesDTO() {}
    
    public AnotacionesDTO(String titulo, String texto, Date fecha, Long usuarioId) {
        this.titulo = titulo;
        this.texto = texto;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }
    
    // Getters y setters...
    public Long getIdAnotacion() {
        return idAnotacion;
    }

    public void setIdAnotacion(Long idAnotacion) {
        this.idAnotacion = idAnotacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
}