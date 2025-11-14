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

    @Autowired
    private com.iesfernandoaguilar.solsonafuentes.repository.GrupoRepository grupoRepository;

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

    @Transactional
    public List<Estadistica> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosEstadisticas filtros, Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Estadisticas");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        LocalDate fechaDesde = filtros.getFechaDesde();
        LocalDate fechaHasta = filtros.getFechaHasta();

        if (filtros.getPeriodo() != null && !filtros.getPeriodo().isEmpty()) {
            LocalDate[] fechas = getFechasFromPeriodo(filtros.getPeriodo());
            fechaDesde = fechas[0];
            fechaHasta = fechas[1];
        }

        return calcularEstadisticasConFiltros(
            filtros.getIdGrupo(),
            filtros.getIdUsuario(),
            filtros.getIdDepartamento(),
            filtros.getIdSubgrupo(),
            fechaDesde,
            fechaHasta
        );
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private LocalDate[] getFechasFromPeriodo(String periodo) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaDesde = null;
        LocalDate fechaHasta = hoy;

        switch (periodo) {
            case "ultimos_30_dias":
                fechaDesde = hoy.minusDays(30);
                break;
            case "ultimos_7_dias":
                fechaDesde = hoy.minusDays(7);
                break;
            case "hoy":
                fechaDesde = hoy;
                break;
            default:
                fechaDesde = hoy.minusDays(30); // Default to last 30 days
                break;
        }
        return new LocalDate[]{fechaDesde, fechaHasta};
    }

    /**
     * Calcula estadísticas con filtros personalizados
     * @param idGrupo ID del grupo
     * @param idUsuario ID del usuario (null para todos)
     * @param idDepartamento ID del departamento (null para todos)
     * @param idSubgrupo ID del subgrupo (null para todos)
     * @param fechaDesde Fecha desde (null para usar fecha predeterminada)
     * @param fechaHasta Fecha hasta (null para usar hoy)
     * @return Lista de estadísticas calculadas por usuario
     */
    @Transactional
    public List<Estadistica> calcularEstadisticasConFiltros(
            Long idGrupo,
            Long idUsuario,
            Long idDepartamento,
            Long idSubgrupo,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        // Establecer fechas por defecto si no se proporcionan
        LocalDate hoy = LocalDate.now();
        if (fechaHasta == null) {
            fechaHasta = hoy;
        }
        if (fechaDesde == null) {
            fechaDesde = fechaHasta.minusDays(30); // Últimos 30 días por defecto
        }

        // Obtener todas las tareas del grupo
        List<Tarea> todasLasTareas = tareaRepository.obtenerTareasPorGrupo(idGrupo);
        System.out.println("📊 Total tareas en el grupo: " + todasLasTareas.size());

        // Filtrar tareas por usuario/departamento/subgrupo y fechas
        List<Tarea> tareasFiltradas = filtrarTareas(todasLasTareas, idUsuario, idDepartamento, idSubgrupo, fechaDesde, fechaHasta);
        System.out.println("📊 Tareas filtradas: " + tareasFiltradas.size());
        System.out.println("📊 Filtros aplicados - Usuario: " + idUsuario + ", Depto: " + idDepartamento +
                           ", Subgrupo: " + idSubgrupo + ", Desde: " + fechaDesde + ", Hasta: " + fechaHasta);

        java.util.List<Estadistica> estadisticas = new java.util.ArrayList<>();

        // Opción 1: Si NO hay filtro de usuario (TODOS) -> Contar cada tarea UNA vez
        if (idUsuario == null) {
            System.out.println("📊 Modo: TODOS - Contando tareas únicas (sin duplicados)");

            Estadistica est = new Estadistica();
            est.setFecha(hoy);
            est.setUsuario(null); // No hay usuario específico

            // Calcular estadísticas de tareas únicas
            int tareasCompletadas = 0;
            int tareasPendientes = 0;
            int tareasRetrasadas = 0;

            // Usar un Set para evitar contar la misma tarea varias veces
            java.util.Set<Long> tareasContadas = new java.util.HashSet<>();
            for (Tarea tarea : tareasFiltradas) {
                // Solo contar cada tarea una vez
                if (tareasContadas.add(tarea.getIdTarea())) {
                    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                        tareasCompletadas++;
                    } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                        tareasPendientes++;
                        if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                            tareasRetrasadas++;
                        }
                    }
                }
            }

            est.setTareasCompletadasTotales(tareasCompletadas);
            est.setTareasPendientesTotales(tareasPendientes);
            est.setTareasRetrasadasTotales(tareasRetrasadas);

            // Para "Todos", calcular horas sumando todos los usuarios del grupo
            // Obtener todos los usuarios del grupo
            Grupo grupo = grupoRepository.findById(idGrupo).orElse(null);
            Double horasTotales = 0.0;
            Double horasExtras = 0.0;

            if (grupo != null && grupo.getUsuarios() != null) {
                for (Usuario u : grupo.getUsuarios()) {
                    Double h = jornadaLaboralService.obtenerHorasTotalesUsuario(u.getIdUsuario(), fechaDesde, fechaHasta);
                    Double he = jornadaLaboralService.obtenerHorasExtrasUsuario(u.getIdUsuario(), fechaDesde, fechaHasta);
                    horasTotales += (h != null ? h : 0.0);
                    horasExtras += (he != null ? he : 0.0);
                }
            }

            est.setHorasTotales(horasTotales);
            est.setHorasExtraTotales(horasExtras);
            est.setHorasTotalesSemana(horasTotales);
            est.setHorasExtraSemana(horasExtras);

            // Calcular tasa de cumplimiento de tareas
            int totalTareas = tareasCompletadas + tareasPendientes;
            double cumplimiento = totalTareas > 0 ? (tareasCompletadas * 100.0 / totalTareas) : 0.0;
            est.setCumplimientoJornadaTotales(cumplimiento);

            System.out.println("📊 TODOS: Completadas=" + tareasCompletadas + ", Pendientes=" + tareasPendientes +
                               ", Retrasadas=" + tareasRetrasadas + " (tareas únicas)");

            estadisticas.add(est);

        } else {
            // Opción 2: Si HAY filtro de usuario -> Contar todas sus tareas asignadas (incluidas compartidas)
            System.out.println("📊 Modo: Usuario específico - Contando todas las tareas asignadas");

            // Agrupar tareas por usuario
            java.util.Map<Long, List<Tarea>> tareasPorUsuario = new java.util.HashMap<>();
            for (Tarea tarea : tareasFiltradas) {
                if (tarea.getUsuariosAsignados() != null) {
                    for (Usuario usuario : tarea.getUsuariosAsignados()) {
                        Long userId = usuario.getIdUsuario();

                        // Solo agregar tareas para el usuario filtrado
                        if (userId.equals(idUsuario)) {
                            tareasPorUsuario.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(tarea);
                        }
                    }
                }
            }
            System.out.println("📊 Usuarios con tareas: " + tareasPorUsuario.size());

            // Calcular estadísticas para el usuario
            for (java.util.Map.Entry<Long, List<Tarea>> entry : tareasPorUsuario.entrySet()) {
                Long userId = entry.getKey();
                List<Tarea> tareasUsuario = entry.getValue();

                Estadistica est = new Estadistica();
                // Obtener el usuario desde el mapa
                Usuario usuario = tareasUsuario.get(0).getUsuariosAsignados().stream()
                    .filter(u -> u.getIdUsuario().equals(userId))
                    .findFirst()
                    .orElse(null);
                est.setUsuario(usuario);
                est.setFecha(hoy);

                // Calcular estadísticas de tareas
                int tareasCompletadas = 0;
                int tareasPendientes = 0;
                int tareasRetrasadas = 0;

                for (Tarea tarea : tareasUsuario) {
                    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                        tareasCompletadas++;
                    } else if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
                        tareasPendientes++;
                        if (tarea.getFechaFin() != null && tarea.getFechaFin().isBefore(hoy)) {
                            tareasRetrasadas++;
                        }
                    }
                }

                est.setTareasCompletadasTotales(tareasCompletadas);
                est.setTareasPendientesTotales(tareasPendientes);
                est.setTareasRetrasadasTotales(tareasRetrasadas);

                // Calcular estadísticas de jornada laboral
                Double horasTotales = jornadaLaboralService.obtenerHorasTotalesUsuario(userId, fechaDesde, fechaHasta);
                Double horasExtras = jornadaLaboralService.obtenerHorasExtrasUsuario(userId, fechaDesde, fechaHasta);

                est.setHorasTotales(horasTotales != null ? horasTotales : 0.0);
                est.setHorasExtraTotales(horasExtras != null ? horasExtras : 0.0);

                // También setear valores "Semana" para las gráficas
                est.setHorasTotalesSemana(horasTotales != null ? horasTotales : 0.0);
                est.setHorasExtraSemana(horasExtras != null ? horasExtras : 0.0);

                // Calcular tasa de cumplimiento de tareas
                int totalTareas = tareasCompletadas + tareasPendientes;
                double cumplimiento = totalTareas > 0 ? (tareasCompletadas * 100.0 / totalTareas) : 0.0;
                est.setCumplimientoJornadaTotales(cumplimiento);

                System.out.println("📊 Usuario " + userId + " (" + (usuario != null ? usuario.getNombre() : "null") + "): " +
                                   "Completadas=" + tareasCompletadas + ", Pendientes=" + tareasPendientes + ", Retrasadas=" + tareasRetrasadas);

                estadisticas.add(est);
            }
        }

        System.out.println("📊 Total estadísticas devueltas: " + estadisticas.size());
        return estadisticas;
    }

    /**
     * Filtra tareas según los criterios especificados
     */
    private List<Tarea> filtrarTareas(List<Tarea> tareas, Long idUsuario, Long idDepartamento, Long idSubgrupo, LocalDate fechaDesde, LocalDate fechaHasta) {
        return tareas.stream()
                .filter(tarea -> tarea.getUsuariosAsignados() != null && !tarea.getUsuariosAsignados().isEmpty())
                .filter(tarea -> {
                    if (idUsuario == null) return true;
                    return tarea.getUsuariosAsignados().stream()
                            .anyMatch(u -> u.getIdUsuario().equals(idUsuario));
                })
                .filter(tarea -> {
                    if (idDepartamento == null) return true;
                    return tarea.getUsuariosAsignados().stream()
                            .anyMatch(u -> u.getDepartamento() != null &&
                                    u.getDepartamento().getIdDepartamento().equals(idDepartamento));
                })
                .filter(tarea -> {
                    if (idSubgrupo == null) return true;
                    return tarea.getUsuariosAsignados().stream()
                            .anyMatch(u -> u.getSubgrupo() != null &&
                                    u.getSubgrupo().getIdSubgrupo().equals(idSubgrupo));
                })
                .filter(tarea -> {
                    // NO filtrar por fechas en las tareas
                    // Las tareas se muestran siempre (pendientes y completadas)
                    // Solo las horas trabajadas se filtran por período
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
