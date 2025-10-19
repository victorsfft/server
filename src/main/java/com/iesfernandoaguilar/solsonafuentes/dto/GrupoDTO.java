package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;

public class GrupoDTO {
    private Long idGrupo;
    private String nombre;
    private LocalDateTime fechaCreacion;
    private String cif;
    private Long creadoPorId;
    private List<Long> usuariosIds;
    private List<Long> subgruposIds;
    private List<Long> configuracionesJornadaIds;
    
    // Constructores
    public GrupoDTO() {}
    
    public GrupoDTO(String nombre, String cif) {
        this.nombre = nombre;
        this.cif = cif;
    }

    public GrupoDTO(String nombre, String cif, Long creadoPorId) {
        this.nombre = nombre;
        this.cif = cif;
        this.creadoPorId = creadoPorId;
        this.usuariosIds = new ArrayList<>();
        this.subgruposIds = new ArrayList<>();
        this.configuracionesJornadaIds = new ArrayList<>();
    }
    
    // Getters y setters...
    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
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

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
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

    public List<Long> getSubgruposIds() {
        return subgruposIds;
    }

    public void setSubgruposIds(List<Long> subgruposIds) {
        this.subgruposIds = subgruposIds;
    }

    public List<Long> getConfiguracionesJornadaIds() {
        return configuracionesJornadaIds;
    }

    public void setConfiguracionesJornadaIds(List<Long> configuracionesJornadaIds) {
        this.configuracionesJornadaIds = configuracionesJornadaIds;
    }

    public void parse(Grupo grupo) {
        if (grupo != null) {
            setNombre(grupo.getNombre());
            setCif(grupo.getCif());
            if (grupo.getFechaCreacion() != null) {
                setFechaCreacion(grupo.getFechaCreacion());
            } else {
                setFechaCreacion(LocalDateTime.now());
            }
            if (grupo.getCreadoPor() != null) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(grupo.getCreadoPor().getIdUsuario());
                setCreadoPorId(usuario.getIdUsuario());
            }
        }
    }

    
}