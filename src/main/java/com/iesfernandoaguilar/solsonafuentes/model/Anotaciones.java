package com.iesfernandoaguilar.solsonafuentes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Anotaciones")
public class Anotaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnotacion;
    
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String texto;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    private LocalDateTime fechaCreacion;

    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    //Constructores
    public Anotaciones() {
    }

    //Getters y setters
    public Long getIdAnotacion() {
        return idAnotacion;
    }

    public void setIdAnotacion(Long idAnotacion) {
        this.idAnotacion = idAnotacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    

}