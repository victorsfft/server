package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;

public class SubgrupoDTO {
    private Long idSubgrupo;
    private String nombre;
    private LocalDateTime fechaCreacion;
    private Long grupoId;
    private Long creadoPorId;
    private List<Long> usuariosIds;
    private List<Long> departamentosIds;
    
    // Constructores
    public SubgrupoDTO() {}
    
    public SubgrupoDTO(String nombre, Long grupoId) {
        this.nombre = nombre;
        this.grupoId = grupoId;
    }

    // Getters y setters...
    public Long getIdSubgrupo() {
        return idSubgrupo;
    }

    public void setIdSubgrupo(Long idSubgrupo) {
        this.idSubgrupo = idSubgrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getCreadoPorId() {
        return creadoPorId;
    }

    public void setCreadoPorId(Long creadoPorId) {
        this.creadoPorId = creadoPorId;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    public void setUsuariosIds(List<Long> usuariosIds) {
        this.usuariosIds = usuariosIds;
    }

    public List<Long> getDepartamentosIds() {
        return departamentosIds;
    }

    public void setDepartamentosIds(List<Long> departamentosIds) {
        this.departamentosIds = departamentosIds;
    }
    
    public static SubgrupoDTO fromEntity(Subgrupo subgrupo) {
        SubgrupoDTO dto = new SubgrupoDTO();
        dto.setNombre(subgrupo.getNombre());
        dto.setFechaCreacion(subgrupo.getFechaCreacion());
        dto.setGrupoId(subgrupo.getGrupo().getIdGrupo());
        dto.setCreadoPorId(subgrupo.getCreadoPor().getIdUsuario());
    
        return dto;
    }
}