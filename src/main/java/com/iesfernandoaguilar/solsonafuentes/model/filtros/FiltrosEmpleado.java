package com.iesfernandoaguilar.solsonafuentes.model.filtros;

public class FiltrosEmpleado extends FiltrosBase {
    private String rol;
    private Boolean activo;

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public boolean tieneRol() {
        return rol != null && !rol.trim().isEmpty();
    }

    public boolean tieneActivo() {
        return activo != null;
    }
}
