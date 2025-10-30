package com.iesfernandoaguilar.solsonafuentes.service;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoConfiguracion;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoJornada;
import com.iesfernandoaguilar.solsonafuentes.model.*;
import com.iesfernandoaguilar.solsonafuentes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar configuraciones de jornada laboral
 * Incluye lógica para generar jornadas laborales automáticamente
 */
@Service
public class ConfiguracionJornadaService {

    @Autowired
    private ConfiguracionJornadaRepository configuracionRepository;

    @Autowired
    private HorarioDiaRepository horarioDiaRepository;

    @Autowired
    private DescansoDiaRepository descansoDiaRepository;

    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    /**
     * Crea una nueva configuración de jornada con sus horarios y descansos
     */
    @Transactional
    public ConfiguracionJornada crearConfiguracion(String nombreConfig, LocalDate fechaInicio,
                                                    LocalDate fechaFin, Long idGrupo, Long idUsuarioCreador,
                                                    List<HorarioDia> horarios) {
        // Validar que el grupo existe
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Validar que no exista otra configuración con el mismo nombre en el grupo
        if (configuracionRepository.existsByNombreAndGrupoId(nombreConfig, idGrupo)) {
            throw new RuntimeException("Ya existe una configuración con ese nombre en el grupo");
        }

        // Validar fechas
        if (fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Crear configuración
        ConfiguracionJornada configuracion = new ConfiguracionJornada();
        configuracion.setNombreConfig(nombreConfig);
        configuracion.setEstado(EstadoConfiguracion.ACTIVA);
        configuracion.setFechaInicio(fechaInicio);
        configuracion.setFechaFin(fechaFin);
        configuracion.setFechaCreacion(java.time.LocalDateTime.now());
        configuracion.setGrupo(grupo);

        // Buscar usuario creador
        if (idUsuarioCreador != null) {
            usuarioRepository.findById(idUsuarioCreador).ifPresent(configuracion::setCreadoPor);
        }

        // Guardar configuración
        configuracion = configuracionRepository.save(configuracion);

        // Guardar horarios asociados
        if (horarios != null && !horarios.isEmpty()) {
            for (HorarioDia horario : horarios) {
                horario.setConfiguracion(configuracion);
                HorarioDia horarioGuardado = horarioDiaRepository.save(horario);

                // Guardar descansos del horario
                if (horario.getDescansos() != null) {
                    for (DescansoDia descanso : horario.getDescansos()) {
                        descanso.setDia(horarioGuardado);
                        descansoDiaRepository.save(descanso);
                    }
                }
            }
        }

        return configuracion;
    }

    /**
     * Actualiza una configuración existente
     */
    @Transactional
    public ConfiguracionJornada actualizarConfiguracion(Long idConfig, String nuevoNombre,
                                                         LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin,
                                                         List<HorarioDia> nuevosHorarios) {
        ConfiguracionJornada configuracion = configuracionRepository.findByIdConfig(idConfig)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        // Validar fechas
        if (nuevaFechaFin != null && nuevaFechaInicio.isAfter(nuevaFechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Actualizar datos básicos
        if (nuevoNombre != null && !nuevoNombre.equals(configuracion.getNombreConfig())) {
            // Verificar que no exista otro con ese nombre
            if (configuracionRepository.existsByNombreAndGrupoId(nuevoNombre, configuracion.getGrupo().getIdGrupo())) {
                throw new RuntimeException("Ya existe una configuración con ese nombre");
            }
            configuracion.setNombreConfig(nuevoNombre);
        }

        configuracion.setFechaInicio(nuevaFechaInicio);
        configuracion.setFechaFin(nuevaFechaFin);

        // Actualizar horarios
        if (nuevosHorarios != null) {
            // Eliminar horarios existentes y sus descansos
            List<HorarioDia> horariosExistentes = horarioDiaRepository.findByConfiguracionIdOrderByDia(idConfig);
            for (HorarioDia horarioExistente : horariosExistentes) {
                // Eliminar descansos del horario
                List<DescansoDia> descansos = descansoDiaRepository.findByDiaIdOrderByHoraInicio(horarioExistente.getIdDia());
                descansoDiaRepository.deleteAll(descansos);
                // Eliminar horario
                horarioDiaRepository.delete(horarioExistente);
            }

            // Guardar nuevos horarios
            for (HorarioDia nuevoHorario : nuevosHorarios) {
                nuevoHorario.setConfiguracion(configuracion);
                nuevoHorario.setIdDia(null); // Asegurar que es nuevo
                HorarioDia horarioGuardado = horarioDiaRepository.save(nuevoHorario);

                // Guardar descansos
                if (nuevoHorario.getDescansos() != null) {
                    for (DescansoDia descanso : nuevoHorario.getDescansos()) {
                        descanso.setDia(horarioGuardado);
                        descanso.setIdDescanso(null); // Asegurar que es nuevo
                        descansoDiaRepository.save(descanso);
                    }
                }
            }
        }

        return configuracionRepository.save(configuracion);
    }

    /**
     * Elimina una configuración y sus horarios asociados
     */
    @Transactional
    public boolean eliminarConfiguracion(Long idConfig) {
        Optional<ConfiguracionJornada> configOpt = configuracionRepository.findByIdConfig(idConfig);
        if (configOpt.isEmpty()) {
            return false;
        }

        ConfiguracionJornada configuracion = configOpt.get();

        // Verificar si hay usuarios usando esta configuración
        long usuariosUsandoConfig = usuarioRepository.countByConfiguracionJornadaId(idConfig);
        if (usuariosUsandoConfig > 0) {
            throw new RuntimeException("No se puede eliminar la configuración porque hay " + usuariosUsandoConfig + " usuario(s) asignado(s) a ella");
        }

        // Eliminar horarios y descansos
        List<HorarioDia> horarios = horarioDiaRepository.findByConfiguracionIdOrderByDia(idConfig);
        for (HorarioDia horario : horarios) {
            // Eliminar descansos
            List<DescansoDia> descansos = descansoDiaRepository.findByDiaIdOrderByHoraInicio(horario.getIdDia());
            descansoDiaRepository.deleteAll(descansos);
            // Eliminar horario
            horarioDiaRepository.delete(horario);
        }

        // Eliminar configuración
        configuracionRepository.delete(configuracion);
        return true;
    }

    /**
     * Obtiene todas las configuraciones de un grupo
     */
    public List<ConfiguracionJornada> obtenerConfiguracionesPorGrupo(Long idGrupo) {
        return configuracionRepository.findByGrupoId(idGrupo);
    }

    /**
     * Obtiene solo las configuraciones activas de un grupo
     */
    public List<ConfiguracionJornada> obtenerConfiguracionesActivas(Long idGrupo) {
        return configuracionRepository.findActivasByGrupoId(idGrupo);
    }

    /**
     * Obtiene una configuración por su ID
     */
    public Optional<ConfiguracionJornada> obtenerConfiguracionPorId(Long idConfig) {
        return configuracionRepository.findByIdConfig(idConfig);
    }

    /**
     * Cambia el estado de una configuración (ACTIVA/INACTIVA)
     */
    @Transactional
    public ConfiguracionJornada cambiarEstado(Long idConfig, EstadoConfiguracion nuevoEstado) {
        ConfiguracionJornada configuracion = configuracionRepository.findByIdConfig(idConfig)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        configuracion.setEstado(nuevoEstado);
        return configuracionRepository.save(configuracion);
    }

    /**
     * Genera jornadas laborales para un usuario basándose en una configuración
     * Esta es la lógica clave cuando se asigna una plantilla a un empleado
     */
    @Transactional
    public List<JornadaLaboral> generarJornadasParaUsuario(Long idUsuario, Long idConfiguracion, LocalDate fechaDesde, LocalDate fechaHasta) {
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar configuración
        ConfiguracionJornada configuracion = configuracionRepository.findByIdConfig(idConfiguracion)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        // Validar que la configuración esté activa
        if (configuracion.getEstado() != EstadoConfiguracion.ACTIVA) {
            throw new RuntimeException("La configuración debe estar activa para generar jornadas");
        }

        // Obtener horarios de la configuración
        List<HorarioDia> horarios = horarioDiaRepository.findByConfiguracionIdOrderByDia(idConfiguracion);
        if (horarios.isEmpty()) {
            throw new RuntimeException("La configuración no tiene horarios definidos");
        }

        List<JornadaLaboral> jornadasGeneradas = new ArrayList<>();

        // Generar jornadas para el rango de fechas
        LocalDate fechaActual = fechaDesde;
        while (!fechaActual.isAfter(fechaHasta)) {
            // Obtener día de la semana
            DayOfWeek diaSemana = fechaActual.getDayOfWeek();
            String diaSemanaStr = convertirDiaSemana(diaSemana);

            // Buscar horario correspondiente
            Optional<HorarioDia> horarioOpt = horarios.stream()
                    .filter(h -> h.getDiaSemana().name().equals(diaSemanaStr))
                    .findFirst();

            if (horarioOpt.isPresent()) {
                HorarioDia horario = horarioOpt.get();

                // Solo crear jornada si el día es laborable
                if (Boolean.TRUE.equals(horario.getEsLaborable())) {
                    // Verificar si ya existe una jornada para este usuario y fecha
                    Optional<JornadaLaboral> jornadaExistente = jornadaLaboralRepository
                            .obtenerJornadaActual(idUsuario, fechaActual);

                    if (jornadaExistente.isEmpty()) {
                        // Crear nueva jornada
                        JornadaLaboral jornada = new JornadaLaboral();
                        jornada.setUsuario(usuario);
                        jornada.setConfiguracion(configuracion);
                        jornada.setFecha(fechaActual);
                        jornada.setEstado(EstadoJornada.PENDIENTE);

                        // Las horas de entrada/salida se registrarán cuando el usuario fiche
                        // No las establecemos aquí porque son de registro, no plantilla

                        jornada = jornadaLaboralRepository.save(jornada);
                        jornadasGeneradas.add(jornada);
                    }
                }
            }

            fechaActual = fechaActual.plusDays(1);
        }

        return jornadasGeneradas;
    }

    /**
     * Genera jornadas automáticamente para un mes completo
     */
    @Transactional
    public List<JornadaLaboral> generarJornadasMesCompleto(Long idUsuario, Long idConfiguracion, int mes, int anio) {
        LocalDate primerDiaMes = LocalDate.of(anio, mes, 1);
        LocalDate ultimoDiaMes = primerDiaMes.plusMonths(1).minusDays(1);

        return generarJornadasParaUsuario(idUsuario, idConfiguracion, primerDiaMes, ultimoDiaMes);
    }

    /**
     * Genera jornadas para los próximos N días
     */
    @Transactional
    public List<JornadaLaboral> generarJornadasProximosDias(Long idUsuario, Long idConfiguracion, int numeroDias) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaFin = hoy.plusDays(numeroDias - 1);

        return generarJornadasParaUsuario(idUsuario, idConfiguracion, hoy, fechaFin);
    }

    /**
     * Convierte DayOfWeek a String compatible con DiaSemana enum
     */
    private String convertirDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

    /**
     * Obtiene los horarios de una configuración
     */
    public List<HorarioDia> obtenerHorariosConfiguracion(Long idConfig) {
        return horarioDiaRepository.findByConfiguracionIdOrderByDia(idConfig);
    }

    /**
     * Obtiene los descansos de un horario específico
     */
    public List<DescansoDia> obtenerDescansosHorario(Long idHorario) {
        return descansoDiaRepository.findByDiaIdOrderByHoraInicio(idHorario);
    }
}
