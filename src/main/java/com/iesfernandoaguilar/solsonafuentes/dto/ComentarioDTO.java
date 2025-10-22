package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.model.Comentario;

public class ComentarioDTO {
    private Long idComentario;
    private String texto;
    private LocalDateTime fechaCreacion;
    private Long tareaId;
    private Long incidenciaId;
    private Long usuarioId;
    private String usuarioNombre;

    // Constructores
    public ComentarioDTO() {}

    public ComentarioDTO(String texto, Long usuarioId) {
        this.texto = texto;
        this.usuarioId = usuarioId;
    }

    // Método para convertir de Comentario a ComentarioDTO
    public static ComentarioDTO fromEntity(Comentario comentario) {
        if (comentario == null) {
            return null;
        }

        ComentarioDTO dto = new ComentarioDTO();
        dto.setIdComentario(comentario.getIdComentario());
        dto.setTexto(comentario.getTexto());
        dto.setFechaCreacion(comentario.getFechaCreacion());

        if (comentario.getTarea() != null) {
            dto.setTareaId(comentario.getTarea().getIdTarea());
        }

        if (comentario.getIncidencia() != null) {
            dto.setIncidenciaId(comentario.getIncidencia().getIdIncidencia());
        }

        if (comentario.getUsuario() != null) {
            dto.setUsuarioId(comentario.getUsuario().getIdUsuario());
            dto.setUsuarioNombre(comentario.getUsuario().getNombre());
        }

        return dto;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
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

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

}
