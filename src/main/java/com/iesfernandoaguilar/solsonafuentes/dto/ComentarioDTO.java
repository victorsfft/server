package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.Date;

public class ComentarioDTO {
    private Long idComentario;
    private String texto;
    private Date fechaCreacion;
    private Long tareaId;
    private Long incidenciaId;
    private Long usuarioId;
    
    // Constructores
    public ComentarioDTO() {}
    
    public ComentarioDTO(String texto, Long usuarioId) {
        this.texto = texto;
        this.usuarioId = usuarioId;
    }
    
    // Getters y setters...
    public Long getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Long idComentario) {
        this.idComentario = idComentario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getTareaId() {
        return tareaId;
    }

    public void setTareaId(Long tareaId) {
        this.tareaId = tareaId;
    }

    public Long getIncidenciaId() {
        return incidenciaId;
    }

    public void setIncidenciaId(Long incidenciaId) {
        this.incidenciaId = incidenciaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
}
