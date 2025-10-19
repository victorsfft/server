package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tarea")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarea;
    
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad = Prioridad.MEDIA;
    
    @Enumerated(EnumType.STRING)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;
    
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
    
    private LocalDateTime fechaCreacion;

    private String titulo;
    private String descripcion;

    //Relaciones Many-to-One    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "tarea", fetch = FetchType.LAZY)
    private List<Comentario> comentarios = new ArrayList<>();
    
    //Relaciones Many-to-Many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Tarea_usuario",
        joinColumns = @JoinColumn(name = "id_tarea"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> usuariosAsignados = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Tarea_departamentos",
        joinColumns = @JoinColumn(name = "id_tarea"),
        inverseJoinColumns = @JoinColumn(name = "id_departamento")
    )
    private List<Departamento> departamentosAsignados = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Tarea_dependiente",
        joinColumns = @JoinColumn(name = "id_tarea"),
        inverseJoinColumns = @JoinColumn(name = "id_tarea_dependiente")
    )
    private List<Tarea> tareasDependientes = new ArrayList<>();

    //Constructores
    public Tarea() {
    }

    //Getters y setters
    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
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

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<Usuario> getUsuariosAsignados() {
        return usuariosAsignados;
    }

    public void setUsuariosAsignados(List<Usuario> usuariosAsignados) {
        this.usuariosAsignados = usuariosAsignados;
    }

    public void addUsuarioAsignado(Usuario usuario){
        if(!usuariosAsignados.contains(usuario)){
            usuariosAsignados.add(usuario);
        }
    }

    public List<Departamento> getDepartamentosAsignados() {
        return departamentosAsignados;
    }

    public void setDepartamentosAsignados(List<Departamento> departamentosAsignados) {
        this.departamentosAsignados = departamentosAsignados;
    }

    public void addDepartamentoAsignado(Departamento departamento){
        if(!departamentosAsignados.contains(departamento)){
            departamentosAsignados.add(departamento);
        }
    }

    public List<Tarea> getTareasDependientes() {
        return tareasDependientes;
    }

    public void setTareasDependientes(List<Tarea> tareasDependientes) {
        this.tareasDependientes = tareasDependientes;
    }

    public void addTareaDependiente(Tarea tarea){
        if(!tareasDependientes.contains(tarea)){
            tareasDependientes.add(tarea);
        }
    }

    
}