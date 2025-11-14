package com.iesfernandoaguilar.solsonafuentes.model.filtros;

public class FiltrosHistorial extends FiltrosBase {
    private String tipoAccion;
    private String entidad;

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
}