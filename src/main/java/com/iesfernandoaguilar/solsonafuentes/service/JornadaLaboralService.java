package com.iesfernandoaguilar.solsonafuentes.service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoJornada;
import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;
import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.JornadaLaboralRepository;

import jakarta.transaction.Transactional;

@Service
public class JornadaLaboralService {

    @Autowired
    private JornadaLaboralRepository jornadaRepository;

    /**
     * Registra la entrada de un usuario
     */
    @Transactional
    public JornadaLaboral registrarEntrada(Usuario usuario, ConfiguracionJornada configuracion, LocalTime horaEntrada) {
        LocalDate hoy = LocalDate.now();
        Optional<JornadaLaboral> jornadaExistente = jornadaRepository.obtenerJornadaActual(usuario.getIdUsuario(), hoy);

        JornadaLaboral jornada;
        if (jornadaExistente.isPresent()) {
            jornada = jornadaExistente.get();
            if (jornada.getHoraEntrada() != null) {
                throw new IllegalStateException("Ya existe una entrada registrada para hoy");
            }
        } else {
            jornada = new JornadaLaboral();
            jornada.setUsuario(usuario);
            jornada.setConfiguracion(configuracion);
            jornada.setFecha(hoy);
            jornada.setHorasTrabajadas(0.0);
            jornada.setHorasExtras(0.0);
        }

        jornada.setHoraEntrada(Time.valueOf(horaEntrada));
        jornada.setEstado(EstadoJornada.TRABAJANDO);

        return jornadaRepository.save(jornada);
    }

    /**
     * Registra la salida de un usuario y calcula las horas trabajadas
     */
    @Transactional
    public JornadaLaboral registrarSalida(Usuario usuario, LocalTime horaSalida) {
        LocalDate hoy = LocalDate.now();
        Optional<JornadaLaboral> jornadaOpt = jornadaRepository.obtenerJornadaActual(usuario.getIdUsuario(), hoy);

        if (!jornadaOpt.isPresent()) {
            throw new IllegalStateException("No existe una entrada registrada para hoy");
        }

        JornadaLaboral jornada = jornadaOpt.get();
        if (jornada.getHoraSalida() != null) {
            throw new IllegalStateException("Ya existe una salida registrada para hoy");
        }

        jornada.setHoraSalida(Time.valueOf(horaSalida));
        jornada.setEstado(EstadoJornada.COMPLETADA);

        // Calcular horas trabajadas
        calcularHorasTrabajadas(jornada);

        return jornadaRepository.save(jornada);
    }

    /**
     * Cambia el estado a EN_PAUSA cuando el usuario inicia un descanso
     */
    @Transactional
    public JornadaLaboral iniciarDescanso(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        Optional<JornadaLaboral> jornadaOpt = jornadaRepository.obtenerJornadaActual(usuario.getIdUsuario(), hoy);

        if (!jornadaOpt.isPresent()) {
            throw new IllegalStateException("No existe una entrada registrada para hoy");
        }

        JornadaLaboral jornada = jornadaOpt.get();
        if (jornada.getEstado() != EstadoJornada.TRABAJANDO) {
            throw new IllegalStateException("El estado actual no permite iniciar un descanso");
        }

        jornada.setEstado(EstadoJornada.EN_PAUSA);
        return jornadaRepository.save(jornada);
    }

    /**
     * Cambia el estado a TRABAJANDO cuando el usuario finaliza un descanso
     */
    @Transactional
    public JornadaLaboral finalizarDescanso(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        Optional<JornadaLaboral> jornadaOpt = jornadaRepository.obtenerJornadaActual(usuario.getIdUsuario(), hoy);

        if (!jornadaOpt.isPresent()) {
            throw new IllegalStateException("No existe una jornada activa");
        }

        JornadaLaboral jornada = jornadaOpt.get();
        if (jornada.getEstado() != EstadoJornada.EN_PAUSA) {
            throw new IllegalStateException("El usuario no está en pausa");
        }

        jornada.setEstado(EstadoJornada.TRABAJANDO);
        return jornadaRepository.save(jornada);
    }

    /**
     * Calcula las horas trabajadas basándose en entrada, salida y descansos
     * También calcula horas extras si superan las 8 horas diarias
     */
    private void calcularHorasTrabajadas(JornadaLaboral jornada) {
        if (jornada.getHoraEntrada() == null || jornada.getHoraSalida() == null) {
            return;
        }

        LocalTime entrada = jornada.getHoraEntrada().toLocalTime();
        LocalTime salida = jornada.getHoraSalida().toLocalTime();

        // Calcular minutos totales trabajados
        long minutosTotal = ChronoUnit.MINUTES.between(entrada, salida);

        // Si la salida es al día siguiente (pasó medianoche)
        if (salida.isBefore(entrada)) {
            minutosTotal = ChronoUnit.MINUTES.between(entrada, LocalTime.MAX) +
                           ChronoUnit.MINUTES.between(LocalTime.MIN, salida);
        }

        // Convertir a horas (decimal)
        double horasTotales = minutosTotal / 60.0;

        // Jornada estándar: 8 horas
        double horasEstándar = 8.0;

        if (horasTotales <= horasEstándar) {
            jornada.setHorasTrabajadas(horasTotales);
            jornada.setHorasExtras(0.0);
        } else {
            jornada.setHorasTrabajadas(horasEstándar);
            jornada.setHorasExtras(horasTotales - horasEstándar);
        }
    }

    /**
     * Obtiene la jornada actual de un usuario
     */
    public Optional<JornadaLaboral> obtenerJornadaActual(Long idUsuario) {
        return jornadaRepository.obtenerJornadaActual(idUsuario, LocalDate.now());
    }

    /**
     * Obtiene las jornadas de un usuario en un rango de fechas
     */
    public List<JornadaLaboral> obtenerJornadasUsuario(Long idUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return jornadaRepository.obtenerJornadasUsuario(idUsuario, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene las jornadas de un grupo en un rango de fechas
     */
    public List<JornadaLaboral> obtenerJornadasGrupo(Long idGrupo, LocalDate fechaDesde, LocalDate fechaHasta) {
        return jornadaRepository.obtenerJornadasGrupo(idGrupo, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene los IDs de empleados que no han fichado hoy
     */
    public List<com.iesfernandoaguilar.solsonafuentes.model.Usuario> obtenerEmpleadosSinFichar(Long idGrupo, LocalDate fecha) {
        return jornadaRepository.obtenerEmpleadosSinFichar(idGrupo, fecha);
    }

    /**
     * Obtiene el total de horas trabajadas por un usuario en un período
     */
    public Double obtenerHorasTotalesUsuario(Long idUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return jornadaRepository.obtenerHorasTotalesUsuario(idUsuario, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene el total de horas extras por un usuario en un período
     */
    public Double obtenerHorasExtrasUsuario(Long idUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return jornadaRepository.obtenerHorasExtrasUsuario(idUsuario, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene todas las jornadas del día para un grupo
     */
    public List<JornadaLaboral> obtenerJornadasDelDia(Long idGrupo, LocalDate fecha) {
        return jornadaRepository.obtenerJornadasDelDia(idGrupo, fecha);
    }

    /**
     * Actualiza una jornada laboral
     */
    @Transactional
    public JornadaLaboral actualizarJornada(JornadaLaboral jornada) {
        return jornadaRepository.save(jornada);
    }
}
