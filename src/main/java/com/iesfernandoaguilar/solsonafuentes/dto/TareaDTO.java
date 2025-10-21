package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.iesfernandoaguilar.solsonafuentes.model.Tarea;

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

    // Método para convertir Entity a DTO
    public static TareaDTO fromEntity(Tarea tarea) {
        if (tarea == null) return null;

        TareaDTO dto = new TareaDTO();
        dto.setIdTarea(tarea.getIdTarea());
        dto.setTitulo(tarea.getTitulo());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setPrioridad(tarea.getPrioridad() != null ? tarea.getPrioridad().name() : null);
        dto.setEstado(tarea.getEstado() != null ? tarea.getEstado().name() : null);

        // Convertir LocalDate a Date
        if (tarea.getFechaInicio() != null) {
            dto.setFechaInicio(Date.from(tarea.getFechaInicio().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (tarea.getFechaFin() != null) {
            dto.setFechaFin(Date.from(tarea.getFechaFin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (tarea.getFechaCreacion() != null) {
            dto.setFechaCreacion(Date.from(tarea.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant()));
        }

        // Convertir creador a ID
        if (tarea.getCreadoPor() != null) {
            dto.setCreadoPorId(tarea.getCreadoPor().getIdUsuario());
        }

        // Convertir listas de objetos a listas de IDs (con manejo seguro de lazy loading)
        try {
            if (tarea.getUsuariosAsignados() != null && !tarea.getUsuariosAsignados().isEmpty()) {
                dto.setUsuariosAsignadosIds(
                    tarea.getUsuariosAsignados().stream()
                        .map(usuario -> usuario.getIdUsuario())
                        .collect(Collectors.toList())
                );
            }
        } catch (Exception e) {
            // Si hay LazyInitializationException, dejamos la lista como null
        }

        try {
            if (tarea.getDepartamentosAsignados() != null && !tarea.getDepartamentosAsignados().isEmpty()) {
                dto.setDepartamentosAsignadosIds(
                    tarea.getDepartamentosAsignados().stream()
                        .map(dept -> dept.getIdDepartamento())
                        .collect(Collectors.toList())
                );
            }
        } catch (Exception e) {
            // Si hay LazyInitializationException, dejamos la lista como null
        }

        try {
            if (tarea.getTareasDependientes() != null && !tarea.getTareasDependientes().isEmpty()) {
                dto.setTareasDependientesIds(
                    tarea.getTareasDependientes().stream()
                        .map(t -> t.getIdTarea())
                        .collect(Collectors.toList())
                );
            }
        } catch (Exception e) {
            // Si hay LazyInitializationException, dejamos la lista como null
        }

        return dto;
    }
}
