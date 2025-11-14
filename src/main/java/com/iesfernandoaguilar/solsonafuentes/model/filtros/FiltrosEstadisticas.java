package com.iesfernandoaguilar.solsonafuentes.model.filtros;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltrosEstadisticas extends FiltrosBase {
    // Esta clase puede extender FiltrosBase y añadir campos específicos para estadísticas si son necesarios.
    // Por ejemplo, podría tener un tipo de estadística a generar.
    private String tipoEstadistica;
    private String periodo;

    public String getTipoEstadistica() {
        return tipoEstadistica;
    }

    public void setTipoEstadistica(String tipoEstadistica) {
        this.tipoEstadistica = tipoEstadistica;
    }

    public boolean tieneTipoEstadistica() {
        return tipoEstadistica != null && !tipoEstadistica.trim().isEmpty();
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
}
