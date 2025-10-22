package com.iesfernandoaguilar.solsonafuentes.enums;

public enum TipoAccionHistorial {
    // Acciones de tareas
    TAREA_CREADA,
    TAREA_ACTUALIZADA,
    TAREA_COMPLETADA,
    TAREA_CANCELADA,
    TAREA_ELIMINADA,
    TAREA_ASIGNADA,
    TAREA_PRIORIDAD_CAMBIADA,
    TAREA_ESTADO_CAMBIADO,

    // Acciones de jornada laboral
    ENTRADA_REGISTRADA,
    SALIDA_REGISTRADA,
    DESCANSO_INICIADO,
    DESCANSO_FINALIZADO,

    // Acciones de departamentos
    DEPARTAMENTO_CREADO,
    DEPARTAMENTO_ACTUALIZADO,
    DEPARTAMENTO_ELIMINADO,

    // Acciones de usuarios
    USUARIO_CREADO,
    USUARIO_ACTUALIZADO,
    USUARIO_ELIMINADO,
    USUARIO_ASIGNADO,
    ROL_CAMBIADO,

    // Acciones de subgrupos
    SUBGRUPO_CREADO,
    SUBGRUPO_ACTUALIZADO,
    SUBGRUPO_ELIMINADO,

    // Acciones de eventos
    EVENTO_CREADO,
    EVENTO_ACTUALIZADO,
    EVENTO_ELIMINADO,

    // Acciones de incidencias
    INCIDENCIA_CREADA,
    INCIDENCIA_RESUELTA,
    INCIDENCIA_ELIMINADA,

    // Acciones administrativas
    CONFIGURACION_ACTUALIZADA,
    PERMISO_OTORGADO,
    PERMISO_REVOCADO,
    INFORME_GENERADO,

    // Acciones generales
    ACCION_CUSTOM
}
