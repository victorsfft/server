package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.model.Estadistica;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Tarea;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.EstadisticaRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.TareaRepository;

import jakarta.transaction.Transactional;

@Service
public class EstadisticaService {

    @Autowired
    private EstadisticaRepository estadisticaRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private JornadaLaboralService jornadaLaboralService;

    /**
     * Obtiene o crea la estadística actual para un usuario
     */
    @Transactional
    public Estadistica obtenerOCrearEstadisticaUsuario(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        Optional<Estadistica> estadisticaOpt = estadisticaRepository.obtenerEstadisticaActualUsuario(
                usuario.getIdUsuario(), hoy);

        if (estadisticaOpt.isPresent()) {
            return estadisticaOpt.get();
        }

        // Crear nueva estadística
        Estadistica estadistica = new Estadistica();
        estadistica.setUsuario(usuario);
        estadistica.setGrupo(usuario.getGrupo());
        estadistica.setFecha(hoy);
        inicializarEstadistica(estadistica);

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Obtiene o crea la estadística actual para un grupo
     */
    @Transactional
    public Estadistica obtenerOCrearEstadisticaGrupo(Grupo grupo) {
        LocalDate hoy = LocalDate.now();
        Optional<Estadistica> estadisticaOpt = estadisticaRepository.obtenerEstadisticaActualGrupo(
                grupo.getIdGrupo(), hoy);

        if (estadisticaOpt.isPresent()) {
            return estadisticaOpt.get();
        }

        // Crear nueva estadística
        Estadistica estadistica = new Estadistica();
        estadistica.setGrupo(grupo);
        estadistica.setFecha(hoy);
        inicializarEstadistica(estadistica);

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Inicializa los campos de una estadística nueva con valores por defecto
     */
    private void inicializarEstadistica(Estadistica estadistica) {
        estadistica.setTareasCompletadasSemana(0);
        estadistica.setTareasPendientesSemana(0);
        estadistica.setTareasRetrasadasSemana(0);
        estadistica.setHorasTotalesSemana(0.0);
        estadistica.setHorasExtraSemana(0.0);
        estadistica.setCumplimientoJornadaSemana(0.0);
        estadistica.setTareasCompletadasTotales(0);
        estadistica.setTareasPendientesTotales(0);
        estadistica.setTareasRetrasadasTotales(0);
        estadistica.setHorasTotales(0.0);
        estadistica.setHorasExtraTotales(0.0);
        estadistica.setPuntualidadJornadaTotales(0.0);
        estadistica.setCumplimientoJornadaTotales(0.0);
    }

    /**
     * Actualiza las estadísticas de un usuario calculando desde las tareas y jornadas
     */
    @Transactional
    public Estadistica actualizarEstadisticasUsuario(Usuario usuario) {
        Estadistica estadistica = obtenerOCrearEstadisticaUsuario(usuario);

        // Calcular fechas para la semana actual
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
        LocalDate finSemana = inicioSemana.plusDays(6);

        // Obtener tareas del usuario
        List<Tarea> tareasUsuario = tareaRepository.obtenerTareasAsignadasAUsuario(usuario.getIdUsuario());

        // Calcular estadísticas de tareas de la semana
        int tareasCompletadasSemana = 0;
        int tareasPendientesSemana = 0;
        int tareasRetrasadasSemana = 0;

        // Calcular estadísticas totales de tareas
        int tareasCompletadasTotales = 0;
        int tareasPendientesTotales = 0;
        int tareasRetrasadasTotales = 0;

        for (Tarea tarea : tareasUsuario) {
            // Estadísticas de la semana
            if (tarea.getFechaCreacion() != null) {
                LocalDate fechaCreacion = tarea.getFechaCreacion().toLocalDate();
                boolean esEstaSemana = !fechaCreacion.isBefore(inicioSemana) && !fechaCreacion.isAfter(finSemana);

                if (esEstaSemana) {
                    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                        tareasCompletadasSemana++;
                    } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                        tareasPendientesSemana++;
                        if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                            tareasRetrasadasSemana++;
                        }
                    }
                }
            }

            // Estadísticas totales
            if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                tareasCompletadasTotales++;
            } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                tareasPendientesTotales++;
                if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                    tareasRetrasadasTotales++;
                }
            }
        }

        estadistica.setTareasCompletadasSemana(tareasCompletadasSemana);
        estadistica.setTareasPendientesSemana(tareasPendientesSemana);
        estadistica.setTareasRetrasadasSemana(tareasRetrasadasSemana);
        estadistica.setTareasCompletadasTotales(tareasCompletadasTotales);
        estadistica.setTareasPendientesTotales(tareasPendientesTotales);
        estadistica.setTareasRetrasadasTotales(tareasRetrasadasTotales);

        // Calcular estadísticas de jornada laboral
        Double horasSemana = jornadaLaboralService.obtenerHorasTotalesUsuario(
                usuario.getIdUsuario(), inicioSemana, finSemana);
        Double horasExtrasSemana = jornadaLaboralService.obtenerHorasExtrasUsuario(
                usuario.getIdUsuario(), inicioSemana, finSemana);

        estadistica.setHorasTotalesSemana(horasSemana != null ? horasSemana : 0.0);
        estadistica.setHorasExtraSemana(horasExtrasSemana != null ? horasExtrasSemana : 0.0);

        // Calcular cumplimiento de jornada semanal (40 horas es el estándar)
        double horasEsperadasSemana = 40.0;
        if (horasSemana != null && horasSemana > 0) {
            estadistica.setCumplimientoJornadaSemana((horasSemana / horasEsperadasSemana) * 100.0);
        }

        // Calcular estadísticas totales de jornada (último mes)
        LocalDate hace30Dias = hoy.minusDays(30);
        Double horasTotales = jornadaLaboralService.obtenerHorasTotalesUsuario(
                usuario.getIdUsuario(), hace30Dias, hoy);
        Double horasExtrasTotales = jornadaLaboralService.obtenerHorasExtrasUsuario(
                usuario.getIdUsuario(), hace30Dias, hoy);

        estadistica.setHorasTotales(horasTotales != null ? horasTotales : 0.0);
        estadistica.setHorasExtraTotales(horasExtrasTotales != null ? horasExtrasTotales : 0.0);

        // Calcular cumplimiento total (últimos 30 días)
        long diasTrabajables = ChronoUnit.DAYS.between(hace30Dias, hoy);
        double horasEsperadasTotales = diasTrabajables * (40.0 / 5.0); // 40 horas semanales / 5 días
        if (horasTotales != null && horasTotales > 0) {
            estadistica.setCumplimientoJornadaTotales((horasTotales / horasEsperadasTotales) * 100.0);
        }

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Actualiza las estadísticas agregadas del grupo
     */
    @Transactional
    public Estadistica actualizarEstadisticasGrupo(Grupo grupo) {
        Estadistica estadistica = obtenerOCrearEstadisticaGrupo(grupo);

        // Calcular fechas para la semana actual
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
        LocalDate finSemana = inicioSemana.plusDays(6);

        // Obtener todas las tareas del grupo
        List<Tarea> tareasGrupo = tareaRepository.obtenerTareasPorGrupo(grupo.getIdGrupo());

        // Calcular estadísticas de tareas
        int tareasCompletadasSemana = 0;
        int tareasPendientesSemana = 0;
        int tareasRetrasadasSemana = 0;
        int tareasCompletadasTotales = 0;
        int tareasPendientesTotales = 0;
        int tareasRetrasadasTotales = 0;

        for (Tarea tarea : tareasGrupo) {
            if (tarea.getFechaCreacion() != null) {
                LocalDate fechaCreacion = tarea.getFechaCreacion().toLocalDate();
                boolean esEstaSemana = !fechaCreacion.isBefore(inicioSemana) && !fechaCreacion.isAfter(finSemana);

                if (esEstaSemana) {
                    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                        tareasCompletadasSemana++;
                    } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                        tareasPendientesSemana++;
                        if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                            tareasRetrasadasSemana++;
                        }
                    }
                }
            }

            // Totales
            if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                tareasCompletadasTotales++;
            } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                tareasPendientesTotales++;
                if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                    tareasRetrasadasTotales++;
                }
            }
        }

        estadistica.setTareasCompletadasSemana(tareasCompletadasSemana);
        estadistica.setTareasPendientesSemana(tareasPendientesSemana);
        estadistica.setTareasRetrasadasSemana(tareasRetrasadasSemana);
        estadistica.setTareasCompletadasTotales(tareasCompletadasTotales);
        estadistica.setTareasPendientesTotales(tareasPendientesTotales);
        estadistica.setTareasRetrasadasTotales(tareasRetrasadasTotales);

        // Aquí se podrían agregar más cálculos para el grupo (promedio de horas, etc.)

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Obtiene las estadísticas de un usuario
     */
    public List<Estadistica> obtenerEstadisticasUsuario(Long idUsuario) {
        return estadisticaRepository.obtenerEstadisticasPorUsuario(idUsuario);
    }

    /**
     * Obtiene las estadísticas de un grupo
     */
    public List<Estadistica> obtenerEstadisticasGrupo(Long idGrupo) {
        return estadisticaRepository.obtenerEstadisticasPorGrupo(idGrupo);
    }

    /**
     * Obtiene las estadísticas de todos los usuarios del grupo
     */
    public List<Estadistica> obtenerEstadisticasUsuariosDelGrupo(Long idGrupo) {
        return estadisticaRepository.obtenerEstadisticasUsuariosDelGrupo(idGrupo);
    }

    /**
     * Obtiene la última estadística de cada usuario del grupo
     */
    public List<Estadistica> obtenerUltimasEstadisticasUsuariosDelGrupo(Long idGrupo) {
        return estadisticaRepository.obtenerUltimasEstadisticasUsuariosDelGrupo(idGrupo);
    }

    /**
     * Obtiene estadísticas de un usuario en un rango de fechas
     */
    public List<Estadistica> obtenerEstadisticasUsuarioPorRango(Long idUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return estadisticaRepository.obtenerEstadisticasUsuarioPorRango(idUsuario, fechaDesde, fechaHasta);
    }

    /**
     * Obtiene estadísticas de un grupo en un rango de fechas
     */
    public List<Estadistica> obtenerEstadisticasGrupoPorRango(Long idGrupo, LocalDate fechaDesde, LocalDate fechaHasta) {
        return estadisticaRepository.obtenerEstadisticasGrupoPorRango(idGrupo, fechaDesde, fechaHasta);
    }
}
