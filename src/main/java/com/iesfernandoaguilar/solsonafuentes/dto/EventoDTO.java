package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.LazyInitializationException;

import com.iesfernandoaguilar.solsonafuentes.model.Evento;

public class EventoDTO {
    private Long idEvento;
    private String titulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean seRepite;
    private Integer diasRepeticion;
    private LocalDateTime fechaCreacion;
    private Long creadoPorId;
    private String creadoPorNombre;
    private List<Long> usuariosAsistentesIds;
    private List<Long> departamentosInvitadosIds;
    
    // Constructores
    public EventoDTO() {}

    public EventoDTO(String titulo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Método para convertir de Evento a EventoDTO
    public static EventoDTO fromEntity(Evento evento) {
        if (evento == null) {
            return null;
        }

        EventoDTO dto = new EventoDTO();
        dto.setIdEvento(evento.getIdEvento());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        dto.setHoraInicio(evento.getHoraInicio());
        dto.setHoraFin(evento.getHoraFin());
        dto.setSeRepite(evento.getSeRepite());
        dto.setDiasRepeticion(evento.getDiasRepeticion());
        dto.setFechaCreacion(evento.getFechaCreacion());

        // Manejar relaciones lazy con try-catch para evitar LazyInitializationException
        try {
            if (evento.getCreadoPor() != null) {
                dto.setCreadoPorId(evento.getCreadoPor().getIdUsuario());
                dto.setCreadoPorNombre(evento.getCreadoPor().getNombre());
            }
        } catch (LazyInitializationException e) {
            System.err.println("WARN: No se pudo cargar creadoPor para evento " + evento.getIdEvento());
            dto.setCreadoPorId(null);
            dto.setCreadoPorNombre("Usuario no disponible");
        }

        try {
            if (evento.getUsuariosAsistentes() != null) {
                dto.setUsuariosAsistentesIds(
                    evento.getUsuariosAsistentes().stream()
                        .map(u -> u.getIdUsuario())
                        .collect(Collectors.toList())
                );
            } else {
                dto.setUsuariosAsistentesIds(new ArrayList<>());
            }
        } catch (LazyInitializationException e) {
            System.err.println("WARN: No se pudo cargar usuariosAsistentes para evento " + evento.getIdEvento());
            dto.setUsuariosAsistentesIds(new ArrayList<>());
        }

        try {
            if (evento.getDepartamentosInvitados() != null) {
                dto.setDepartamentosInvitadosIds(
                    evento.getDepartamentosInvitados().stream()
                        .map(d -> d.getIdDepartamento())
                        .collect(Collectors.toList())
                );
            } else {
                dto.setDepartamentosInvitadosIds(new ArrayList<>());
            }
        } catch (LazyInitializationException e) {
            System.err.println("WARN: No se pudo cargar departamentosInvitados para evento " + evento.getIdEvento());
            dto.setDepartamentosInvitadosIds(new ArrayList<>());
        }

        return dto;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCreadoPorNombre() {
        return creadoPorNombre;
    }

    public void setCreadoPorNombre(String creadoPorNombre) {
        this.creadoPorNombre = creadoPorNombre;
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
