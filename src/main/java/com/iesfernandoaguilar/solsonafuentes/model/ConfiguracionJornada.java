package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoConfiguracion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Configuracion_jornada")
public class ConfiguracionJornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConfig;
    
    @Column(name = "nombre_config", unique = true, nullable = false)
    private String nombreConfig;
    
    @Enumerated(EnumType.STRING)
    private EstadoConfiguracion estado = EstadoConfiguracion.ACTIVA;
    
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
    
    private LocalDateTime fechaCreacion;

    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    //Relaciones One-to-Many
    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioDia> horarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "configuracion", fetch = FetchType.LAZY)
    private List<JornadaLaboral> jornadasLaborales = new ArrayList<>();
    
    //Constructores
    public ConfiguracionJornada() {
    }

    //Getters y setters
    public Long getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Long idConfig) {
        this.idConfig = idConfig;
    }

    public String getNombreConfig() {
        return nombreConfig;
    }

    public void setNombreConfig(String nombreConfig) {
        this.nombreConfig = nombreConfig;
    }

    public EstadoConfiguracion getEstado() {
        return estado;
    }

    public void setEstado(EstadoConfiguracion estado) {
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

    public List<HorarioDia> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDia> horarios) {
        this.horarios = horarios;
    }

    public List<JornadaLaboral> getJornadasLaborales() {
        return jornadasLaborales;
    }

    public void setJornadasLaborales(List<JornadaLaboral> jornadasLaborales) {
        this.jornadasLaborales = jornadasLaborales;
    }

    
}
