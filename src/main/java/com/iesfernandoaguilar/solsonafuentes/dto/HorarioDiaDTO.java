package com.iesfernandoaguilar.solsonafuentes.dto;

import com.iesfernandoaguilar.solsonafuentes.enums.DiaSemana;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoDescanso;
import com.iesfernandoaguilar.solsonafuentes.model.DescansoDia;
import com.iesfernandoaguilar.solsonafuentes.model.HorarioDia;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioDiaDTO {
    private Long idDia;
    private String diaSemana;
    private Boolean esLaborable;
    private Time horaEntrada;
    private Time horaSalida;
    private String horaEntradaLocal; // Para recibir desde el cliente
    private String horaSalidaLocal; // Para recibir desde el cliente
    private Long configuracionId;
    private List<Long> descansosIds;
    private List<DescansoDTO> descansos; // Para recibir descansos completos desde el cliente
    
    // Constructores
    public HorarioDiaDTO() {}
    
    public HorarioDiaDTO(String diaSemana, Long configuracionId) {
        this.diaSemana = diaSemana;
        this.configuracionId = configuracionId;
    }
    
    // Getters y setters...
    public Long getIdDia() {
        return idDia;
    }

    public void setIdDia(Long idDia) {
        this.idDia = idDia;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Boolean getEsLaborable() {
        return esLaborable;
    }

    public void setEsLaborable(Boolean esLaborable) {
        this.esLaborable = esLaborable;
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

    public Long getConfiguracionId() {
        return configuracionId;
    }

    public void setConfiguracionId(Long configuracionId) {
        this.configuracionId = configuracionId;
    }

    public List<Long> getDescansosIds() {
        return descansosIds;
    }

    public void setDescansosIds(List<Long> descansosIds) {
        this.descansosIds = descansosIds;
    }

    public String getHoraEntradaLocal() {
        return horaEntradaLocal;
    }

    public void setHoraEntradaLocal(String horaEntradaLocal) {
        this.horaEntradaLocal = horaEntradaLocal;
    }

    public String getHoraSalidaLocal() {
        return horaSalidaLocal;
    }

    public void setHoraSalidaLocal(String horaSalidaLocal) {
        this.horaSalidaLocal = horaSalidaLocal;
    }

    public List<DescansoDTO> getDescansos() {
        return descansos;
    }

    public void setDescansos(List<DescansoDTO> descansos) {
        this.descansos = descansos;
    }

    /**
     * Convierte el DTO a entidad HorarioDia
     */
    public HorarioDia toEntity() {
        HorarioDia horario = new HorarioDia();

        // Convertir día de semana
        try {
            horario.setDiaSemana(DiaSemana.valueOf(this.diaSemana));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Día de semana no válido: " + this.diaSemana);
        }

        horario.setEsLaborable(this.esLaborable);

        // Convertir horas de String a Time (priorizar horaEntradaLocal/horaSalidaLocal)
        if (this.horaEntradaLocal != null && !this.horaEntradaLocal.isEmpty()) {
            LocalTime horaEntrada = LocalTime.parse(this.horaEntradaLocal);
            horario.setHoraEntrada(Time.valueOf(horaEntrada));
        } else if (this.horaEntrada != null) {
            horario.setHoraEntrada(this.horaEntrada);
        }

        if (this.horaSalidaLocal != null && !this.horaSalidaLocal.isEmpty()) {
            LocalTime horaSalida = LocalTime.parse(this.horaSalidaLocal);
            horario.setHoraSalida(Time.valueOf(horaSalida));
        } else if (this.horaSalida != null) {
            horario.setHoraSalida(this.horaSalida);
        }

        // Convertir descansos
        List<DescansoDia> descansosEntidad = new ArrayList<>();
        if (this.descansos != null) {
            for (DescansoDTO descansoDTO : this.descansos) {
                DescansoDia descanso = descansoDTO.toEntity();
                descanso.setDia(horario);
                descansosEntidad.add(descanso);
            }
        }
        horario.setDescansos(descansosEntidad);

        return horario;
    }

    /**
     * Convierte una entidad HorarioDia a DTO para enviar al cliente
     */
    public static HorarioDiaDTO fromEntity(HorarioDia horario) {
        HorarioDiaDTO dto = new HorarioDiaDTO();
        dto.setIdDia(horario.getIdDia());
        dto.setDiaSemana(horario.getDiaSemana() != null ? horario.getDiaSemana().name() : null);
        dto.setEsLaborable(horario.getEsLaborable());

        // Convertir Time a String
        if (horario.getHoraEntrada() != null) {
            dto.setHoraEntradaLocal(horario.getHoraEntrada().toLocalTime().toString());
        }
        if (horario.getHoraSalida() != null) {
            dto.setHoraSalidaLocal(horario.getHoraSalida().toLocalTime().toString());
        }

        // Convertir descansos
        if (horario.getDescansos() != null && !horario.getDescansos().isEmpty()) {
            dto.setDescansos(horario.getDescansos().stream()
                    .map(DescansoDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList()));
        } else {
            dto.setDescansos(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Clase interna para los descansos
     */
    public static class DescansoDTO {
        private String tipoDescanso;
        private String horaInicioLocal;
        private Integer duracionMinutos;

        public DescansoDTO() {
        }

        public String getTipoDescanso() {
            return tipoDescanso;
        }

        public void setTipoDescanso(String tipoDescanso) {
            this.tipoDescanso = tipoDescanso;
        }

        public String getHoraInicioLocal() {
            return horaInicioLocal;
        }

        public void setHoraInicioLocal(String horaInicioLocal) {
            this.horaInicioLocal = horaInicioLocal;
        }

        public Integer getDuracionMinutos() {
            return duracionMinutos;
        }

        public void setDuracionMinutos(Integer duracionMinutos) {
            this.duracionMinutos = duracionMinutos;
        }

        /**
         * Convierte el DTO a entidad DescansoDia
         */
        public DescansoDia toEntity() {
            DescansoDia descanso = new DescansoDia();

            // Convertir tipo de descanso (puede ser String simple o enum)
            if (this.tipoDescanso != null && !this.tipoDescanso.isEmpty()) {
                try {
                    descanso.setTipoDescanso(TipoDescanso.valueOf(this.tipoDescanso.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Si no es un enum válido, usar DESCANSO por defecto
                    descanso.setTipoDescanso(TipoDescanso.DESCANSO);
                }
            } else {
                descanso.setTipoDescanso(TipoDescanso.DESCANSO);
            }

            descanso.setDuracionMinutos(this.duracionMinutos);

            // Convertir hora de inicio
            if (this.horaInicioLocal != null && !this.horaInicioLocal.isEmpty()) {
                LocalTime horaInicio = LocalTime.parse(this.horaInicioLocal);
                Time horaInicioTime = Time.valueOf(horaInicio);
                descanso.setHoraInicio(horaInicioTime);
                System.out.println("🔄 [Servidor] DescansoDTO.toEntity() - Tipo: " + this.tipoDescanso +
                    ", horaInicioLocal String: " + this.horaInicioLocal + " → Time: " + horaInicioTime);
            } else {
                System.out.println("🔄 [Servidor] DescansoDTO.toEntity() - Tipo: " + this.tipoDescanso +
                    ", horaInicioLocal: null o vacío");
            }

            return descanso;
        }

        /**
         * Convierte una entidad DescansoDia a DTO
         */
        public static DescansoDTO fromEntity(DescansoDia descanso) {
            DescansoDTO dto = new DescansoDTO();
            dto.setTipoDescanso(descanso.getTipoDescanso() != null ? descanso.getTipoDescanso().name() : null);
            dto.setDuracionMinutos(descanso.getDuracionMinutos());

            // Convertir hora de inicio
            if (descanso.getHoraInicio() != null) {
                dto.setHoraInicioLocal(descanso.getHoraInicio().toLocalTime().toString());
            }

            return dto;
        }
    }
}