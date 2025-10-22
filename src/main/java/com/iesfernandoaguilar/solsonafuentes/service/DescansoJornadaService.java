package com.iesfernandoaguilar.solsonafuentes.service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.TipoDescanso;
import com.iesfernandoaguilar.solsonafuentes.model.DescansoJornada;
import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;
import com.iesfernandoaguilar.solsonafuentes.repository.DescansoJornadaRepository;

import jakarta.transaction.Transactional;

@Service
public class DescansoJornadaService {

    @Autowired
    private DescansoJornadaRepository descansoRepository;

    /**
     * Registra un nuevo descanso
     */
    @Transactional
    public DescansoJornada registrarDescanso(
            JornadaLaboral jornada,
            TipoDescanso tipoDescanso,
            Integer duracionMinutos,
            LocalTime horaInicio) {

        DescansoJornada descanso = new DescansoJornada();
        descanso.setJornada(jornada);
        descanso.setTipoDescanso(tipoDescanso);
        descanso.setDuracionMinutos(duracionMinutos);
        descanso.setHoraInicio(Time.valueOf(horaInicio));

        return descansoRepository.save(descanso);
    }

    /**
     * Obtiene los descansos de una jornada
     */
    public List<DescansoJornada> obtenerDescansosPorJornada(Long idJornada) {
        return descansoRepository.obtenerDescansosPorJornada(idJornada);
    }

    /**
     * Obtiene los descansos de un usuario en una fecha específica
     */
    public List<DescansoJornada> obtenerDescansosPorUsuarioYFecha(Long idUsuario, LocalDate fecha) {
        return descansoRepository.obtenerDescansosPorUsuarioYFecha(idUsuario, fecha);
    }

    /**
     * Obtiene el tiempo total de descansos en minutos
     */
    public Integer obtenerTiempoTotalDescansos(Long idJornada) {
        return descansoRepository.obtenerTiempoTotalDescansos(idJornada);
    }

    /**
     * Obtiene los descansos de un usuario en un rango de fechas
     */
    public List<DescansoJornada> obtenerDescansosPorUsuarioYRango(
            Long idUsuario,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        return descansoRepository.obtenerDescansosPorUsuarioYRango(idUsuario, fechaDesde, fechaHasta);
    }
}
