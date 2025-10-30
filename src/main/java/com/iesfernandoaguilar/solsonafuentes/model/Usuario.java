package com.iesfernandoaguilar.solsonafuentes.model;

import java.util.ArrayList;
import java.util.List;

import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "cont_hasheada")
    private String contraseniaHasheada;

    @Column
    private String salt;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.USUARIO;

    //Relaciones Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo", nullable = true)
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_subgrupo", nullable = true)
    private Subgrupo subgrupo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = true)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_configuracion_jornada", nullable = true)
    private ConfiguracionJornada configuracionJornada;

    //Relaciones One-to-Many
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Grupo> gruposCreados = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Subgrupo> subgruposCreados = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Departamento> departamentosCreados = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Tarea> tareasCreadas = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Evento> eventosCreados = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<ConfiguracionJornada> configuracionesCreadas = new ArrayList<>();
    
    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Anotaciones> anotacionesCreadas = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Incidencia> incidencias = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<JornadaLaboral> jornadasLaborales = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Anotaciones> anotaciones = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Comentario> comentarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Estadistica> estadisticas = new ArrayList<>();
    
    //Relaciones Many-to-Many
    @ManyToMany(mappedBy = "usuariosAsignados", fetch = FetchType.LAZY)
    private List<Tarea> tareasAsignadas = new ArrayList<>();
    
    @ManyToMany(mappedBy = "usuariosAsistentes", fetch = FetchType.LAZY)
    private List<Evento> eventosAsignados = new ArrayList<>();

    //Constructores
    public Usuario() {}

    public Usuario(String nombre,String email, String contraseniaHasheada, String salt){
        this.nombre = nombre;
        this.email = email;
        this.contraseniaHasheada = contraseniaHasheada;
        this.salt = salt;
    }
    
    //Getters y setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseniaHasheada() {
        return contraseniaHasheada;
    }

    public void setContraseniaHasheada(String contraseniaHasheada) {
        this.contraseniaHasheada = contraseniaHasheada;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Subgrupo getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(Subgrupo subgrupo) {
        this.subgrupo = subgrupo;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public ConfiguracionJornada getConfiguracionJornada() {
        return configuracionJornada;
    }

    public void setConfiguracionJornada(ConfiguracionJornada configuracionJornada) {
        this.configuracionJornada = configuracionJornada;
    }

    public List<Grupo> getGruposCreados() {
        return gruposCreados;
    }

    public void setGruposCreados(List<Grupo> gruposCreados) {
        this.gruposCreados = gruposCreados;
    }

    public List<Subgrupo> getSubgruposCreados() {
        return subgruposCreados;
    }

    public void setSubgruposCreados(List<Subgrupo> subgruposCreados) {
        this.subgruposCreados = subgruposCreados;
    }

    public List<Departamento> getDepartamentosCreados() {
        return departamentosCreados;
    }

    public void setDepartamentosCreados(List<Departamento> departamentosCreados) {
        this.departamentosCreados = departamentosCreados;
    }

    public List<Tarea> getTareasCreadas() {
        return tareasCreadas;
    }

    public void setTareasCreadas(List<Tarea> tareasCreadas) {
        this.tareasCreadas = tareasCreadas;
    }

    public List<Evento> getEventosCreados() {
        return eventosCreados;
    }

    public void setEventosCreados(List<Evento> eventosCreados) {
        this.eventosCreados = eventosCreados;
    }

    public List<ConfiguracionJornada> getConfiguracionesCreadas() {
        return configuracionesCreadas;
    }

    public void setConfiguracionesCreadas(List<ConfiguracionJornada> configuracionesCreadas) {
        this.configuracionesCreadas = configuracionesCreadas;
    }

    public List<Anotaciones> getAnotacionesCreadas() {
        return anotacionesCreadas;
    }

    public void setAnotacionesCreadas(List<Anotaciones> anotacionesCreadas) {
        this.anotacionesCreadas = anotacionesCreadas;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public List<JornadaLaboral> getJornadasLaborales() {
        return jornadasLaborales;
    }

    public void setJornadasLaborales(List<JornadaLaboral> jornadasLaborales) {
        this.jornadasLaborales = jornadasLaborales;
    }

    public List<Anotaciones> getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(List<Anotaciones> anotaciones) {
        this.anotaciones = anotaciones;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<Estadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(List<Estadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }

    public List<Tarea> getTareasAsignadas() {
        return tareasAsignadas;
    }

    public void setTareasAsignadas(List<Tarea> tareasAsignadas) {
        this.tareasAsignadas = tareasAsignadas;
    }

    public void addTareaAsignada(Tarea tarea){
        if(!tareasAsignadas.contains(tarea)){
            tareasAsignadas.add(tarea);
        }
    }

    public List<Evento> getEventosAsignados() {
        return eventosAsignados;
    }

    public void setEventosAsignados(List<Evento> eventosAsignados) {
        this.eventosAsignados = eventosAsignados;
    }

    public void addEventoAsignado(Evento evento){
        if(!eventosAsignados.contains(evento)){
            eventosAsignados.add(evento);
        }
    }

    public void parse(UsuarioDTO usuario2) {
        if(usuario2 != null){
            setIdUsuario(usuario2.getIdUsuario());
            setNombre(usuario2.getNombre());
            setEmail(usuario2.getEmail());
            setContraseniaHasheada(usuario2.getContraseniaHasheada());
            setSalt(usuario2.getSalt());
            setRol(usuario2.getRol());
        }
    }
}