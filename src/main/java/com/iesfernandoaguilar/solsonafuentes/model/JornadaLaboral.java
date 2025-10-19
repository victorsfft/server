package com.iesfernandoaguilar.solsonafuentes.model;

import java.sql.Time;
import java.time.LocalDate;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoJornada;

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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "Jornada_laboral")
public class JornadaLaboral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJornada;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Temporal(TemporalType.TIME)
    @Column(name = "hora_entrada")
    private Time horaEntrada;
    
    @Temporal(TemporalType.TIME)
    @Column(name = "hora_salida")
    private Time horaSalida;
    
    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;
    
    @Column(name = "horas_extras")
    private Double horasExtras;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoJornada estado = EstadoJornada.PENDIENTE;

    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_config", nullable = false)
    private ConfiguracionJornada configuracion;

    //Constructores
    public JornadaLaboral() {
    }
    
    //Getters y setters
    public Long getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Long idJornada) {
        this.idJornada = idJornada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Time getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Time getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Double getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(Double horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public Double getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(Double horasExtras) {
        this.horasExtras = horasExtras;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public void setEstado(EstadoJornada estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ConfiguracionJornada getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(ConfiguracionJornada configuracion) {
        this.configuracion = configuracion;
    }

    
    
}
