package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDate;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvento;
    
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    
    @Column(name = "se_repite")
    private Boolean seRepite = false;
    
    @Column(name = "dias_repeticion")
    private Integer diasRepeticion = 0;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones Many-to-Many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Evento_usuario",
        joinColumns = @JoinColumn(name = "id_evento"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> usuariosAsistentes = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Evento_departamento",
        joinColumns = @JoinColumn(name = "id_evento"),
        inverseJoinColumns = @JoinColumn(name = "id_departamento")
    )
    private List<Departamento> departamentosInvitados = new ArrayList<>();
    
    //Constructores
    public Evento() {
    }

    //Getters y setters
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

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public List<Usuario> getUsuariosAsistentes() {
        return usuariosAsistentes;
    }

    public void setUsuariosAsistentes(List<Usuario> usuariosAsistentes) {
        this.usuariosAsistentes = usuariosAsistentes;
    }

    public void addUsuarioAsistente(Usuario usuario){
        if(!usuariosAsistentes.contains(usuario)){
            usuariosAsistentes.add(usuario);
        }
    }

    public List<Departamento> getDepartamentosInvitados() {
        return departamentosInvitados;
    }

    public void setDepartamentosInvitados(List<Departamento> departamentosInvitados) {
        this.departamentosInvitados = departamentosInvitados;
    }

    public void addDepartamentoInvitado(Departamento departamento){
        if(!departamentosInvitados.contains(departamento)){
            departamentosInvitados.add(departamento);
        }
    }

    

    
}