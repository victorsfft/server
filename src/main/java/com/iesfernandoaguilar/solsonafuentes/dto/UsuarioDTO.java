package com.iesfernandoaguilar.solsonafuentes.dto;

import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;

public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String email;
    private String contraseniaHasheada;
    private String salt;
    private Rol rol;
    private Long grupoId;
    private Long subgrupoId;
    private Long departamentoId;
    private List<Long> tareasAsignadasIds;
    private List<Long> eventosAsignadosIds;
    
    // Constructores
    public UsuarioDTO() {}
    
    public UsuarioDTO(String nombre, String email, String contraseniaHasheada) {
        this.nombre = nombre;
        this.email = email;
        this.contraseniaHasheada = contraseniaHasheada;
    }
    
    // Getters y setters...
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseniaHasheada() {
        return contraseniaHasheada;
    }

    public void setContraseniaHasheada(String contraseniaHasheada) {
        this.contraseniaHasheada = contraseniaHasheada;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getSubgrupoId() {
        return subgrupoId;
    }

    public void setSubgrupoId(Long subgrupoId) {
        this.subgrupoId = subgrupoId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public List<Long> getTareasAsignadasIds() {
        return tareasAsignadasIds;
    }

    public void setTareasAsignadasIds(List<Long> tareasAsignadasIds) {
        this.tareasAsignadasIds = tareasAsignadasIds;
    }

    public List<Long> getEventosAsignadosIds() {
        return eventosAsignadosIds;
    }

    public void setEventosAsignadosIds(List<Long> eventosAsignadosIds) {
        this.eventosAsignadosIds = eventosAsignadosIds;
    }

    public void parse(Usuario usuario) {
        if (usuario != null) {
            idUsuario = usuario.getIdUsuario();
            nombre = usuario.getNombre();
            email = usuario.getEmail();
            contraseniaHasheada = usuario.getContraseniaHasheada();
            salt = usuario.getSalt();
            rol = usuario.getRol();
            grupoId = usuario.getGrupo()!= null ? usuario.getGrupo().getIdGrupo() : null;
            subgrupoId = usuario.getSubgrupo()!= null ? usuario.getSubgrupo().getIdSubgrupo() : null;
            departamentoId = usuario.getDepartamento()!= null ? usuario.getDepartamento().getIdDepartamento() : null;
        }
    }
    
    public static UsuarioDTO fromEntity(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        if (usuario != null) {
            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());
            dto.setContraseniaHasheada(usuario.getContraseniaHasheada());
            dto.setSalt(usuario.getSalt());
            dto.setRol(usuario.getRol());
            dto.setGrupoId(usuario.getGrupo() != null ? usuario.getGrupo().getIdGrupo() : null);
            dto.setSubgrupoId(usuario.getSubgrupo() != null ? usuario.getSubgrupo().getIdSubgrupo() : null);
            dto.setDepartamentoId(usuario.getDepartamento() != null ? usuario.getDepartamento().getIdDepartamento() : null);
        }
        return dto;
    }
}