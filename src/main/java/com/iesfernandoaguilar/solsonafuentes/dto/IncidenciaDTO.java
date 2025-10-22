package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.iesfernandoaguilar.solsonafuentes.model.Incidencia;

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

    // Método para convertir Entity a DTO
    public static IncidenciaDTO fromEntity(Incidencia incidencia) {
        if (incidencia == null) return null;

        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setIdIncidencia(incidencia.getIdIncidencia());
        dto.setTitulo(incidencia.getTitulo());
        dto.setDescripcion(incidencia.getDescripcion());
        dto.setPrioridad(incidencia.getPrioridad() != null ? incidencia.getPrioridad().name() : null);
        dto.setEstado(incidencia.getEstado() != null ? incidencia.getEstado().name() : null);

        // Convertir LocalDate/LocalDateTime a Date
        if (incidencia.getFechaInicio() != null) {
            dto.setFechaInicio(incidencia.getFechaInicio().atStartOfDay());
        }
        if (incidencia.getFechaFin() != null) {
            dto.setFechaFin(incidencia.getFechaFin().atStartOfDay());
        }
        if (incidencia.getFechaCreacion() != null) {
            dto.setFechaCreacion(Date.from(incidencia.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant()));
        }

        // Convertir usuario a ID
        if (incidencia.getUsuario() != null) {
            dto.setUsuarioId(incidencia.getUsuario().getIdUsuario());
        }

        // Convertir comentarios a IDs
        try {
            if (incidencia.getComentarios() != null && !incidencia.getComentarios().isEmpty()) {
                dto.setComentariosIds(
                    incidencia.getComentarios().stream()
                        .map(comentario -> comentario.getIdComentario())
                        .collect(Collectors.toList())
                );
            }
        } catch (Exception e) {
            // Si hay LazyInitializationException, dejamos la lista como null
        }

        return dto;
    }
}