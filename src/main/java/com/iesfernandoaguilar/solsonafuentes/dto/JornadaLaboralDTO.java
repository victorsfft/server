package com.iesfernandoaguilar.solsonafuentes.dto;

import java.sql.Time;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoJornada;
import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JornadaLaboralDTO {
    private Long idJornada;
    private LocalDate fecha;
    private Time horaEntrada;
    private Time horaSalida;
    private Double horasTrabajadas;
    private Double horasExtras;
    private EstadoJornada estado;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idConfiguracion;

    public JornadaLaboralDTO() {
    }

    public static JornadaLaboralDTO fromEntity(JornadaLaboral jornada) {
        JornadaLaboralDTO dto = new JornadaLaboralDTO();
        dto.setIdJornada(jornada.getIdJornada());
        dto.setFecha(jornada.getFecha());
        dto.setHoraEntrada(jornada.getHoraEntrada());
        dto.setHoraSalida(jornada.getHoraSalida());
        dto.setHorasTrabajadas(jornada.getHorasTrabajadas());
        dto.setHorasExtras(jornada.getHorasExtras());
        dto.setEstado(jornada.getEstado());

        if (jornada.getUsuario() != null) {
            dto.setIdUsuario(jornada.getUsuario().getIdUsuario());
            dto.setNombreUsuario(jornada.getUsuario().getNombre());
        }

        if (jornada.getConfiguracion() != null) {
            dto.setIdConfiguracion(jornada.getConfiguracion().getIdConfig());
        }

        return dto;
    }

    public Long getIdJornada() { return idJornada; }
    public void setIdJornada(Long idJornada) { this.idJornada = idJornada; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Time getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(Time horaEntrada) { this.horaEntrada = horaEntrada; }
    public Time getHoraSalida() { return horaSalida; }
    public void setHoraSalida(Time horaSalida) { this.horaSalida = horaSalida; }
    public Double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public Double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(Double horasExtras) { this.horasExtras = horasExtras; }
    public EstadoJornada getEstado() { return estado; }
    public void setEstado(EstadoJornada estado) { this.estado = estado; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public Long getIdConfiguracion() { return idConfiguracion; }
    public void setIdConfiguracion(Long idConfiguracion) { this.idConfiguracion = idConfiguracion; }
}
