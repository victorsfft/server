package com.iesfernandoaguilar.solsonafuentes.model.filtros;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltrosHistorial extends FiltrosBase {
    private String tipoAccion;
    private String entidad;
    private String periodo;

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public boolean tieneTipoAccion() {
        return tipoAccion != null && !tipoAccion.trim().isEmpty();
    }

    public boolean tieneEntidad() {
        return entidad != null && !entidad.trim().isEmpty();
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public boolean tienePeriodo() {
        return periodo != null && !periodo.trim().isEmpty();
    }
}