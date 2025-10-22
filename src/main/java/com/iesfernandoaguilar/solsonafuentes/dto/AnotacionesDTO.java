package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iesfernandoaguilar.solsonafuentes.model.Anotaciones;

public class AnotacionesDTO {
    private Long idAnotacion;
    private String titulo;
    private String texto;
    private LocalDate fecha;
    private LocalDateTime fechaCreacion;
    private Long creadoPorId;
    private Long usuarioId;
    private String usuarioNombre;

    // Constructores
    public AnotacionesDTO() {}

    public AnotacionesDTO(String titulo, String texto, LocalDate fecha, Long usuarioId) {
        this.titulo = titulo;
        this.texto = texto;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }

    // Método para convertir de Anotaciones a AnotacionesDTO
    public static AnotacionesDTO fromEntity(Anotaciones anotacion) {
        if (anotacion == null) {
            return null;
        }

        AnotacionesDTO dto = new AnotacionesDTO();
        dto.setIdAnotacion(anotacion.getIdAnotacion());
        dto.setTitulo(anotacion.getTitulo());
        dto.setTexto(anotacion.getTexto());
        dto.setFecha(anotacion.getFecha());
        dto.setFechaCreacion(anotacion.getFechaCreacion());

        if (anotacion.getCreadoPor() != null) {
            dto.setCreadoPorId(anotacion.getCreadoPor().getIdUsuario());
        }

        if (anotacion.getUsuario() != null) {
            dto.setUsuarioId(anotacion.getUsuario().getIdUsuario());
            dto.setUsuarioNombre(anotacion.getUsuario().getNombre());
        }

        return dto;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
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

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

}