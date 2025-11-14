package com.iesfernandoaguilar.solsonafuentes.model.filtros;

public class FiltrosTarea extends FiltrosBase {
    private String prioridad;
    private String estado;

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean tienePrioridad() {
        return prioridad != null && !prioridad.trim().isEmpty();
    }

    public boolean tieneEstado() {
        return estado != null && !estado.trim().isEmpty();
    }
}
