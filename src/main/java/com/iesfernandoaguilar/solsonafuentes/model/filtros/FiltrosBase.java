package com.iesfernandoaguilar.solsonafuentes.model.filtros;

import java.time.LocalDate;

public class FiltrosBase {
    private Long idGrupo;
    private Long idUsuario;
    private Long idDepartamento;
    private Long idSubgrupo;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String textoBusqueda;

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public Long getIdSubgrupo() {
        return idSubgrupo;
    }

    public void setIdSubgrupo(Long idSubgrupo) {
        this.idSubgrupo = idSubgrupo;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    public boolean tieneGrupo() {
        return idGrupo != null;
    }

    public boolean tieneUsuario() {
        return idUsuario != null;
    }

    public boolean tieneDepartamento() {
        return idDepartamento != null;
    }

    public boolean tieneSubgrupo() {
        return idSubgrupo != null;
    }

    public boolean tieneFechas() {
        return fechaDesde != null || fechaHasta != null;
    }

    public boolean tieneBusqueda() {
        return textoBusqueda != null && !textoBusqueda.trim().isEmpty();
    }
}
