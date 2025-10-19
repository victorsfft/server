package com.iesfernandoaguilar.solsonafuentes.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class JornadaLaboralDTO {
    private Long idJornada;
    private Date fecha;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private Double horasTrabajadas;
    private Double horasExtras;
    private String estado;
    private Long usuarioId;
    private Long configuracionId;
    
    // Constructores
    public JornadaLaboralDTO() {}
    
    public JornadaLaboralDTO(Date fecha, Long usuarioId, Long configuracionId) {
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.configuracionId = configuracionId;
    }

    // Getters y setters...
    public Long getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Long idJornada) {
        this.idJornada = idJornada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getConfiguracionId() {
        return configuracionId;
    }

    public void setConfiguracionId(Long configuracionId) {
        this.configuracionId = configuracionId;
    }
    
}