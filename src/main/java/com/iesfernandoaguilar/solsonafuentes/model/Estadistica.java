package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Estadistica")
public class Estadistica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstadistica;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;
    
    //Estadisticas semanales
    private Integer tareasCompletadasSemana = 0;
    private Integer tareasPendientesSemana = 0;
    private Integer tareasRetrasadasSemana = 0;
    private Double horasTotalesSemana = 0.0;
    private Double horasExtraSemana = 0.0;
    private Double cumplimientoJornadaSemana = 0.0;
    
    //Estadisticas totales
    private Integer tareasCompletadasTotales = 0;
    private Integer tareasPendientesTotales = 0;
    private Integer tareasRetrasadasTotales = 0;
    private Double horasTotales = 0.0;
    private Double horasExtraTotales = 0.0;
    private Double puntualidadJornadaTotales = 0.0;
    private Double cumplimientoJornadaTotales = 0.0;
    
    
    //Constructores
    public Estadistica() {
    }

    //Getters y setters
    public Long getIdEstadistica() {
        return idEstadistica;
    }


    public void setIdEstadistica(Long idEstadistica) {
        this.idEstadistica = idEstadistica;
    }


    public LocalDate getFecha() {
        return fecha;
    }


    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Grupo getGrupo() {
        return grupo;
    }


    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }


    public Integer getTareasCompletadasSemana() {
        return tareasCompletadasSemana;
    }


    public void setTareasCompletadasSemana(Integer tareasCompletadasSemana) {
        this.tareasCompletadasSemana = tareasCompletadasSemana;
    }


    public Integer getTareasPendientesSemana() {
        return tareasPendientesSemana;
    }


    public void setTareasPendientesSemana(Integer tareasPendientesSemana) {
        this.tareasPendientesSemana = tareasPendientesSemana;
    }


    public Integer getTareasRetrasadasSemana() {
        return tareasRetrasadasSemana;
    }


    public void setTareasRetrasadasSemana(Integer tareasRetrasadasSemana) {
        this.tareasRetrasadasSemana = tareasRetrasadasSemana;
    }


    public Double getHorasTotalesSemana() {
        return horasTotalesSemana;
    }


    public void setHorasTotalesSemana(Double horasTotalesSemana) {
        this.horasTotalesSemana = horasTotalesSemana;
    }


    public Double getHorasExtraSemana() {
        return horasExtraSemana;
    }


    public void setHorasExtraSemana(Double horasExtraSemana) {
        this.horasExtraSemana = horasExtraSemana;
    }


    public Double getCumplimientoJornadaSemana() {
        return cumplimientoJornadaSemana;
    }


    public void setCumplimientoJornadaSemana(Double cumplimientoJornadaSemana) {
        this.cumplimientoJornadaSemana = cumplimientoJornadaSemana;
    }


    public Integer getTareasCompletadasTotales() {
        return tareasCompletadasTotales;
    }


    public void setTareasCompletadasTotales(Integer tareasCompletadasTotales) {
        this.tareasCompletadasTotales = tareasCompletadasTotales;
    }


    public Integer getTareasPendientesTotales() {
        return tareasPendientesTotales;
    }


    public void setTareasPendientesTotales(Integer tareasPendientesTotales) {
        this.tareasPendientesTotales = tareasPendientesTotales;
    }


    public Integer getTareasRetrasadasTotales() {
        return tareasRetrasadasTotales;
    }


    public void setTareasRetrasadasTotales(Integer tareasRetrasadasTotales) {
        this.tareasRetrasadasTotales = tareasRetrasadasTotales;
    }


    public Double getHorasTotales() {
        return horasTotales;
    }


    public void setHorasTotales(Double horasTotales) {
        this.horasTotales = horasTotales;
    }


    public Double getHorasExtraTotales() {
        return horasExtraTotales;
    }


    public void setHorasExtraTotales(Double horasExtraTotales) {
        this.horasExtraTotales = horasExtraTotales;
    }


    public Double getPuntualidadJornadaTotales() {
        return puntualidadJornadaTotales;
    }


    public void setPuntualidadJornadaTotales(Double puntualidadJornadaTotales) {
        this.puntualidadJornadaTotales = puntualidadJornadaTotales;
    }


    public Double getCumplimientoJornadaTotales() {
        return cumplimientoJornadaTotales;
    }


    public void setCumplimientoJornadaTotales(Double cumplimientoJornadaTotales) {
        this.cumplimientoJornadaTotales = cumplimientoJornadaTotales;
    }

    
}