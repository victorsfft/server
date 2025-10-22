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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Departamento")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartamento;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    private LocalDateTime fechaCreacion;
    
    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_subgrupo", nullable = false)
    private Subgrupo subgrupo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
    
    //Relaciones Many-to-Many
    @ManyToMany(mappedBy = "departamentosAsignados", fetch = FetchType.LAZY)
    private List<Tarea> tareasAsignadas = new ArrayList<>();
    
    @ManyToMany(mappedBy = "departamentosInvitados", fetch = FetchType.LAZY)
    private List<Evento> eventosAsignados = new ArrayList<>();

    //Constructores
    public Departamento() {}

    public Departamento(String nombre, Usuario creadoPor) {
        this.nombre = nombre;
        this.creadoPor = creadoPor;
        this.fechaCreacion = LocalDateTime.now();
    }
    //Getters y setters
    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
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

    public Subgrupo getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(Subgrupo subgrupo) {
        this.subgrupo = subgrupo;
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

    public List<Tarea> getTareasAsignadas() {
        return tareasAsignadas;
    }

    public void setTareasAsignadas(List<Tarea> tareasAsignadas) {
        this.tareasAsignadas = tareasAsignadas;
    }

    public void addTareaAsignada(Tarea tarea){
        if (!tareasAsignadas.contains(tarea)) {
            tareasAsignadas.add(tarea);
        }
    }

    public List<Evento> getEventosAsignados() {
        return eventosAsignados;
    }

    public void setEventosAsignados(List<Evento> eventosAsignados) {
        this.eventosAsignados = eventosAsignados;
    }

    public void addEventoAsignado(Evento evento){
        if (!eventosAsignados.contains(evento)) {
            eventosAsignados.add(evento);
        }
    }
}