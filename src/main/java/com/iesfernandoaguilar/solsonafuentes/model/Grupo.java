package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.dto.GrupoDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Grupo")
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    private LocalDateTime fechaCreacion;

    private String cif;
    
    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "grupo",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Subgrupo> subgrupos = new ArrayList<>();
    
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private List<ConfiguracionJornada> configuracionesJornada = new ArrayList<>();
    
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private List<Estadistica> estadisticas = new ArrayList<>();
    
    //Constructores
    public Grupo() {}

    //Getters y setters
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

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Subgrupo> getSubgrupos() {
        return subgrupos;
    }

    public void setSubgrupos(List<Subgrupo> subgrupos) {
        this.subgrupos = subgrupos;
    }

    public List<ConfiguracionJornada> getConfiguracionesJornada() {
        return configuracionesJornada;
    }

    public void setConfiguracionesJornada(List<ConfiguracionJornada> configuracionesJornada) {
        this.configuracionesJornada = configuracionesJornada;
    }

    public List<Estadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(List<Estadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }

    public void addSubgrupo(Subgrupo subgrupo){
        subgrupos.add(subgrupo);
        subgrupo.setGrupo(this);
    }

    public void parse(GrupoDTO grupoDTO) {
        if (grupoDTO != null) {
            setNombre(grupoDTO.getNombre());
            setCif(grupoDTO.getCif());
            if (grupoDTO.getFechaCreacion() != null) {
                setFechaCreacion(grupoDTO.getFechaCreacion());
            } else {
                setFechaCreacion(LocalDateTime.now());
            }
            if (grupoDTO.getCreadoPorId() != null) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(grupoDTO.getCreadoPorId());
                setCreadoPor(usuario);
            }
        }
    }

    
}