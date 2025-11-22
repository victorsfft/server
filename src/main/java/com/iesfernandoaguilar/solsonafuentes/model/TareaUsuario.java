package com.iesfernandoaguilar.solsonafuentes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Tarea_usuario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_tarea", "id_usuario"}))
public class TareaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea", nullable = false)
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "trabajando", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean trabajando = false;

    // Constructores
    public TareaUsuario() {}

    public TareaUsuario(Tarea tarea, Usuario usuario) {
        this.tarea = tarea;
        this.usuario = usuario;
        this.trabajando = false;
    }

    public TareaUsuario(Tarea tarea, Usuario usuario, boolean trabajando) {
        this.tarea = tarea;
        this.usuario = usuario;
        this.trabajando = trabajando;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isTrabajando() {
        return trabajando;
    }

    public void setTrabajando(boolean trabajando) {
        this.trabajando = trabajando;
    }
}
