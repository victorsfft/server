package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "Subgrupo")
public class Subgrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubgrupo;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    private LocalDateTime fechaCreacion;
    
    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "subgrupo", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "subgrupo", fetch = FetchType.LAZY)
    private List<Departamento> departamentos = new ArrayList<>();

    //Constructores
    public Subgrupo() {
    }

    public Subgrupo(String nombre,Usuario creadoPor) {
        this.nombre = nombre;
        this.creadoPor = creadoPor;
    }

    public Subgrupo(String nombre, Grupo grupo, Usuario creadoPor) {
        this.nombre = nombre;
        this.grupo = grupo;
        this.creadoPor = creadoPor;
    }

    //Getters y setters
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

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
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

    public List<Departamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<Departamento> departamentos) {
        this.departamentos = departamentos;
    }

    public void addDepartamento(Departamento departamento){
    	departamentos.add(departamento);
        departamento.setSubgrupo(this);
    }

    
    
}