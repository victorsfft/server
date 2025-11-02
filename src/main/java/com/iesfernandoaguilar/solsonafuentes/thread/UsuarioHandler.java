package com.iesfernandoaguilar.solsonafuentes.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iesfernandoaguilar.solsonafuentes.Servidor;
import com.iesfernandoaguilar.solsonafuentes.dto.ComentarioDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.ConfiguracionJornadaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.DepartamentoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.DescansoJornadaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.EstadisticaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.EventoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.GrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.HistorialDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.IncidenciaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.JornadaLaboralDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.NotificacionDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.SubgrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.TareaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;
import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoAccionHistorial;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoDescanso;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Comentario;
import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.DescansoJornada;
import com.iesfernandoaguilar.solsonafuentes.model.Estadistica;
import com.iesfernandoaguilar.solsonafuentes.model.Evento;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Historial;
import com.iesfernandoaguilar.solsonafuentes.model.Incidencia;
import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Tarea;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.service.ComentarioService;
import com.iesfernandoaguilar.solsonafuentes.service.ConfiguracionJornadaService;
import com.iesfernandoaguilar.solsonafuentes.service.DepartamentoService;
import com.iesfernandoaguilar.solsonafuentes.service.DescansoJornadaService;
import com.iesfernandoaguilar.solsonafuentes.service.EstadisticaService;
import com.iesfernandoaguilar.solsonafuentes.service.EventoService;
import com.iesfernandoaguilar.solsonafuentes.service.GrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.HistorialService;
import com.iesfernandoaguilar.solsonafuentes.service.IncidenciaService;
import com.iesfernandoaguilar.solsonafuentes.service.InformeService;
import com.iesfernandoaguilar.solsonafuentes.service.JornadaLaboralService;
import com.iesfernandoaguilar.solsonafuentes.service.NotificacionService;
import com.iesfernandoaguilar.solsonafuentes.service.SolicitudGrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.SubgrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.TareaService;
import com.iesfernandoaguilar.solsonafuentes.service.UsuarioService;
import com.iesfernandoaguilar.solsonafuentes.util.MakeAbstractRequest;
import com.iesfernandoaguilar.solsonafuentes.util.Mensaje;
import com.iesfernandoaguilar.solsonafuentes.util.Serializador;

public class UsuarioHandler implements Runnable{

    private Socket socket;
    private Servidor server;
    private ApplicationContext context;
    private DataInputStream reader;
    private DataOutputStream writer;
    private boolean cierraSesion;
    private Long usuarioId;

    public UsuarioHandler(Socket socket,ApplicationContext context,Servidor server, Long usuarioId) {
        this.socket = socket;
        this.server = server;
        this.context = context;
        this.usuarioId = usuarioId;
        reader = null;
        writer = null;
        cierraSesion = false;
    }

    public void cerrarConexion() {
        try {
            cierraSesion = true;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        UsuarioService usuarioService = context.getBean(UsuarioService.class);
        GrupoService grupoService = context.getBean(GrupoService.class);
        SolicitudGrupoService solicitudGrupoService = context.getBean(SolicitudGrupoService.class);
        NotificacionService notificacionService = context.getBean(NotificacionService.class);
        SubgrupoService subgrupoService = context.getBean(SubgrupoService.class);
        DepartamentoService departamentoService = context.getBean(DepartamentoService.class);
        TareaService tareaService = context.getBean(TareaService.class);
        ComentarioService comentarioService = context.getBean(ComentarioService.class);
        IncidenciaService incidenciaService = context.getBean(IncidenciaService.class);
        EventoService eventoService = context.getBean(EventoService.class);
        HistorialService historialService = context.getBean(HistorialService.class);
        JornadaLaboralService jornadaLaboralService = context.getBean(JornadaLaboralService.class);
        DescansoJornadaService descansoJornadaService = context.getBean(DescansoJornadaService.class);
        EstadisticaService estadisticaService = context.getBean(EstadisticaService.class);
        InformeService informeService = context.getBean(InformeService.class);
        ConfiguracionJornadaService configuracionJornadaService = context.getBean(ConfiguracionJornadaService.class);

        String nombreEmpresa = "";
        String vatEmpresa = "";
        UsuarioDTO usuarioDTO = null;
        GrupoDTO grupoDto = null;
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        Long idGrupo;
        Long idSubgrupo;
        Long idDepartamento;
        Optional <Usuario> usuarioOpt;
        Departamento departamento;


        System.out.println("usuario handler");

        try{
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            while (!cierraSesion && !socket.isClosed()) {
                String linea = reader.readUTF();
                System.out.println(linea);
                
                
                Mensaje mensajeUser = Serializador.decodificarMensaje(linea);
                System.out.println(mensajeUser);
                Mensaje mensajeServer = new Mensaje();

                System.out.println(mensajeUser.getTipo());
                
                switch(mensajeUser.getTipo()) {
                    case "LOGOUT":
                        server.cerrarSesion(this.usuarioId);
                        cierraSesion = true;
                        break;
                    case "COMPROBAR_EMPRESA":
                        System.out.println("comprobar empresa");
                        mensajeServer.setTipo("EMPRESA_EXISTE");

                        try {

                            nombreEmpresa = mensajeUser.getArgs().get(0);
                            vatEmpresa = mensajeUser.getArgs().get(1);

                            Optional<Grupo> grupoNombre = grupoService.findByNombre(nombreEmpresa);
                            Optional<Grupo> grupoVat = grupoService.findByVat(vatEmpresa);

                            if(grupoNombre.isPresent() || grupoVat.isPresent()) {
                                mensajeServer.addArg("existe");
                            } else if(!comprobarExistenciaCif(vatEmpresa)){
                                mensajeServer.addArg("invalido");
                            }else{
                                mensajeServer.addArg("no_existe");
                                
                                mapper = new ObjectMapper();
                                usuarioDTO = mapper.readValue(mensajeUser.getArgs().get(2), UsuarioDTO.class);
                            }
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                       

                        
                       
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_NOTIFICACIONES":
                        mensajeServer.setTipo("DAR_NOTIFICACIONES");

                        Long idUsuario = Long.valueOf(mensajeUser.getArgs().get(0));
                        List<Notificacion> notificacionesNoLeidas = notificacionService.obtenerNotificaciones(idUsuario,EstadoNotificacion.PENDIENTE);
                        List<NotificacionDTO> dtos = notificacionesNoLeidas.stream()
                                                    .map(NotificacionDTO::fromEntity)
                                                    .collect(Collectors.toList());
                       
                        try {

                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(dtos);
                            
                            
                            
                        } catch (Exception e) {
                            System.err.println("Error de json");
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                   

                    case "CREAR_EMPRESA":
                        mensajeServer.setTipo("EMPRESA_CREADA");

                        grupoDto = new GrupoDTO(nombreEmpresa,
                                                vatEmpresa,
                                                usuarioDTO.getIdUsuario());

                        Grupo grupo = new Grupo();
                        grupo.parse(grupoDto);

                        // Crear subgrupo por defecto
                        Subgrupo subgrupo = new Subgrupo("Grupo 1", grupo.getCreadoPor());
                        grupo.addSubgrupo(subgrupo);
                        
                        // NUEVO: Crear departamento por defecto dentro del subgrupo
                        departamento = new Departamento("Departamento General",grupo.getCreadoPor());
                        departamento.setFechaCreacion(LocalDateTime.now());

                        subgrupo.addDepartamento(departamento);

                        // Guardar el grupo (esto guardará en cascada el subgrupo y el departamento si está configurado)
                        Optional<Grupo> grupoCreado = Optional.ofNullable(grupoService.save(grupo));

                        if(grupoCreado.isPresent()) {
                            mensajeServer.addArg("creado");
                            System.out.println("✅ Empresa creada con subgrupo y departamento por defecto");
                        } else {
                            mensajeServer.addArg("no_creado");
                            System.err.println("❌ Error al crear la empresa");
                        }

                        // Actualizar el usuario con el grupo y rol de administrador
                        usuarioOpt = usuarioService.findByIdUsuario(usuarioDTO.getIdUsuario());
                        if(usuarioOpt.isPresent()){
                            Usuario usuario = usuarioOpt.get();
                            usuario.setGrupo(grupoCreado.get());
                            usuario.setRol(Rol.ADMINISTRADOR);
                            
                            // OPCIONAL: Asignar al usuario al departamento por defecto
                            usuario.setDepartamento(departamento);
                            
                            usuarioService.save(usuario);
                            System.out.println("✅ Usuario asignado como ADMINISTRADOR y añadido al departamento por defecto");
                        }
                        
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_SUBGRUPOS":
                        mensajeServer.setTipo("DAR_SUBGRUPOS");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        List<Subgrupo> subgrupos = subgrupoService.obtenerSubgrupos(idGrupo);
                        List<SubgrupoDTO> subgruposDtos = subgrupos.stream()
                                                    .map(SubgrupoDTO::fromEntity)
                                                    .collect(Collectors.toList());
                       
                        try {
                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(subgruposDtos);
                            
                        } catch (Exception e) {
                            System.err.println("Error de json");
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                    case "OBTENER_EMPLEADOS":
                        mensajeServer.setTipo("DAR_EMPLEADOS");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        List<Usuario> empleados = usuarioService.obtenerEmpleados(idGrupo);
                        List<UsuarioDTO> empleadosDtos = empleados.stream()
                                                    .map(UsuarioDTO::fromEntity)
                                                    .collect(Collectors.toList());
                       
                        try {
                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(empleadosDtos);
                            
                        } catch (Exception e) {
                            System.err.println("Error de json");
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                    case "OBTENER_DEPARTAMENTOS":
                        mensajeServer.setTipo("DAR_DEPARTAMENTOS");

                        idSubgrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        List<Departamento> departamentos = departamentoService.obtenerDepartamentos(idSubgrupo);
                        List<DepartamentoDTO> departamentosDTO = departamentos.stream()
                                                    .map(DepartamentoDTO::fromEntity)
                                                    .collect(Collectors.toList());
                       
                        try {
                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(departamentosDTO);
                            
                        } catch (Exception e) {
                            System.err.println("Error de json");
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                    case "CREAR_INVITACION":   
                        mensajeServer.setTipo("INVITACION_CREADA");

                        String email = mensajeUser.getArgs().get(0);
                        idDepartamento = Long.valueOf(mensajeUser.getArgs().get(1));
                        String rol = mensajeUser.getArgs().get(2);
                        String idInvitador = mensajeUser.getArgs().get(3);


                        usuarioOpt = usuarioService.login(email);
                        Optional <Departamento> departamentoOpt = departamentoService.findByIdDepartamento(idDepartamento);
                        

                        if(!usuarioOpt.isPresent()){
                            mensajeServer.addArg("usuario_no_existe");
                        } else if(!departamentoOpt.isPresent()){
                            mensajeServer.addArg("departamento_no_existe");
                        }else{

                            Usuario invitador = usuarioService.findByIdUsuario(Long.valueOf(idInvitador)).get();
                            Usuario usuario = usuarioOpt.get();
                            departamento = departamentoOpt.get();
                            subgrupo = departamentoOpt.get().getSubgrupo();
                            grupo = subgrupo.getGrupo();

                            Notificacion notificacion = new Notificacion(usuario,"Invitación a " + grupo.getNombre(), 
                                                                        TipoNotificacion.INVITACION_GRUPO, grupo, invitador, 
                                                                        subgrupo, departamento, EstadoNotificacion.PENDIENTE);

                            // Crear o actualizar la invitación
                            notificacionService.crearOActualizarInvitacion(notificacion);

                            mensajeServer.addArg("exito");
                        }

                        enviar(mensajeServer);
                        break;
                    // En UsuarioHandler.java (SERVIDOR) - AÑADIR después del caso "CREAR_INVITACION" (alrededor línea 450)

                    case "CREAR_SUBGRUPO":
                        mensajeServer.setTipo("SUBGRUPO_CREADO");
                        
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nombreSubgrupo = mensajeUser.getArgs().get(1);
                            String descripcionSubgrupo = mensajeUser.getArgs().get(2);
                            Long idCreador = Long.valueOf(mensajeUser.getArgs().get(3));
                            
                            // Buscar el grupo
                            Optional<Grupo> grupoOpt = grupoService.findByIdGrupo(idGrupo);
                            // Buscar el usuario creador
                            Optional<Usuario> creadorOpt = usuarioService.findByIdUsuario(idCreador);
                            
                            if (grupoOpt.isPresent() && creadorOpt.isPresent()) {
                                grupo = grupoOpt.get();
                                Usuario creador = creadorOpt.get();
                                
                                // Crear el nuevo subgrupo
                                Subgrupo nuevoSubgrupo = new Subgrupo(nombreSubgrupo, creador);
                                nuevoSubgrupo.setFechaCreacion(LocalDateTime.now());
                                nuevoSubgrupo.setGrupo(grupo);
                                
                                // Guardar el subgrupo
                                Subgrupo subgrupoGuardado = subgrupoService.save(nuevoSubgrupo);
                                
                                if (subgrupoGuardado != null && subgrupoGuardado.getIdSubgrupo() != null) {
                                    mensajeServer.addArg("exito");
                                    System.out.println("✅ Subgrupo creado: " + nombreSubgrupo);
                                } else {
                                    mensajeServer.addArg("error");
                                    System.err.println("❌ Error al guardar el subgrupo");
                                }
                            } else {
                                if (!grupoOpt.isPresent()) {
                                    mensajeServer.addArg("grupo_no_existe");
                                    System.err.println("❌ Error: Grupo no existe");
                                } else {
                                    mensajeServer.addArg("usuario_no_existe");
                                    System.err.println("❌ Error: Usuario no existe");
                                }
                            }
                            
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear subgrupo: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        enviar(mensajeServer);
                        break;

                    case "CREAR_DEPARTAMENTO":
                        mensajeServer.setTipo("DEPARTAMENTO_CREADO");
                        
                        try {
                            idSubgrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nombreDepartamento = mensajeUser.getArgs().get(1);
                            String descripcionDepartamento = mensajeUser.getArgs().get(2);
                            Long idCreador = Long.valueOf(mensajeUser.getArgs().get(3));
                            
                            // Buscar el subgrupo
                            Optional<Subgrupo> subgrupoOpt = subgrupoService.findByIdSubgrupo(idSubgrupo);
                            // Buscar el usuario creador
                            Optional<Usuario> creadorOpt = usuarioService.findByIdUsuario(idCreador);
                            
                            if (subgrupoOpt.isPresent() && creadorOpt.isPresent()) {
                                subgrupo = subgrupoOpt.get();
                                Usuario creador = creadorOpt.get();
                                
                                // Crear el nuevo departamento
                                Departamento nuevoDepartamento = new Departamento(nombreDepartamento, creador);
                                nuevoDepartamento.setFechaCreacion(LocalDateTime.now());
                                nuevoDepartamento.setSubgrupo(subgrupo);
                                
                                // Guardar el departamento
                                Departamento departamentoGuardado = departamentoService.save(nuevoDepartamento);
                                
                                if (departamentoGuardado != null && departamentoGuardado.getIdDepartamento() != null) {
                                    mensajeServer.addArg("exito");
                                    System.out.println("✅ Departamento creado: " + nombreDepartamento);
                                } else {
                                    mensajeServer.addArg("error");
                                    System.err.println("❌ Error al guardar el departamento");
                                }
                            } else {
                                if (!subgrupoOpt.isPresent()) {
                                    mensajeServer.addArg("subgrupo_no_existe");
                                    System.err.println("❌ Error: Subgrupo no existe");
                                } else {
                                    mensajeServer.addArg("usuario_no_existe");
                                    System.err.println("❌ Error: Usuario no existe");
                                }
                            }
                            
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear departamento: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        enviar(mensajeServer);
                        break;
                    case "OBTENER_TODOS_DEPARTAMENTOS":
                        mensajeServer.setTipo("DAR_DEPARTAMENTOS");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        List<Departamento> todosDepartamentos = departamentoService.obtenerTodosDepartamentos(idGrupo);
                        List<DepartamentoDTO> todosDepartamentosDTO = todosDepartamentos.stream()
                                                    .map(DepartamentoDTO::fromEntity)
                                                    .collect(Collectors.toList());
                    
                        try {
                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(todosDepartamentosDTO);
                            
                        } catch (Exception e) {
                            System.err.println("Error de json");
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                    case "OBTENER_DEPARTAMENTOS_GRUPO":
                        mensajeServer.setTipo("DAR_DEPARTAMENTOS_GESTION");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));

                        // Obtener todos los departamentos del grupo (de todos sus subgrupos)
                        List<Departamento> departamentosGrupo = departamentoService.obtenerTodosDepartamentos(idGrupo);
                        
                        List<DepartamentoDTO> departamentosGrupoDTO = departamentosGrupo.stream()
                                                    .map(DepartamentoDTO::fromEntity)
                                                    .collect(Collectors.toList());
                    
                        try {
                            mapper = new ObjectMapper();
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            json = mapper.writeValueAsString(departamentosGrupoDTO);
                            System.out.println("✅ Departamentos del grupo obtenidos: " + departamentosGrupo.size());
                            
                        } catch (Exception e) {
                            System.err.println("Error de json al serializar departamentos del grupo");
                            e.printStackTrace();
                        }

                        mensajeServer.addArg(json);
                        enviar(mensajeServer);
                        break;
                    case "BUSCAR_DEPARTAMENTOS":
                        mensajeServer.setTipo("BUSCAR_DEPARTAMENTOS_RESULTADO");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        String filtroDept = mensajeUser.getArgs().size() > 1 ? mensajeUser.getArgs().get(1) : "";

                        List<Departamento> departamentosFiltrados = (filtroDept == null || filtroDept.isEmpty())
                                ? departamentoService.obtenerTodosDepartamentos(idGrupo)
                                : departamentoService.buscarDepartamentosPorNombre(idGrupo, filtroDept);

                        List<DepartamentoDTO> departamentosFiltradosDtos = departamentosFiltrados.stream()
                                .map(DepartamentoDTO::fromEntity)
                                .collect(Collectors.toList());

                        try {
                            mapper = new ObjectMapper();
                            mapper.registerModule(new JavaTimeModule());
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            String jsonDepts = mapper.writeValueAsString(departamentosFiltradosDtos);
                            mensajeServer.addArg(jsonDepts);
                            System.out.println("✅ Búsqueda de departamentos completada: " + departamentosFiltrados.size() + " resultados");
                        } catch (Exception e) {
                            System.err.println("❌ Error al serializar departamentos filtrados");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;
                    case "BUSCAR_EMPLEADOS":
                        mensajeServer.setTipo("BUSCAR_EMPLEADOS_RESULTADO");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        String filtroEmp = mensajeUser.getArgs().size() > 1 ? mensajeUser.getArgs().get(1) : "";

                        List<Usuario> empleadosFiltrados = (filtroEmp == null || filtroEmp.isEmpty())
                                ? usuarioService.obtenerEmpleados(idGrupo)
                                : usuarioService.buscarEmpleados(idGrupo, filtroEmp);

                        List<UsuarioDTO> empleadosFiltradosDtos = empleadosFiltrados.stream()
                                .map(UsuarioDTO::fromEntity)
                                .collect(Collectors.toList());

                        try {
                            mapper = new ObjectMapper();
                            mapper.registerModule(new JavaTimeModule());
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            String jsonEmps = mapper.writeValueAsString(empleadosFiltradosDtos);
                            mensajeServer.addArg(jsonEmps);
                            System.out.println("✅ Búsqueda de empleados completada: " + empleadosFiltrados.size() + " resultados");
                        } catch (Exception e) {
                            System.err.println("❌ Error al serializar empleados filtrados");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;
                    case "BUSCAR_SUBGRUPOS":
                        mensajeServer.setTipo("BUSCAR_SUBGRUPOS_RESULTADO");

                        idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                        String filtroSub = mensajeUser.getArgs().size() > 1 ? mensajeUser.getArgs().get(1) : "";

                        List<Subgrupo> subgruposFiltrados = (filtroSub == null || filtroSub.isEmpty())
                                ? subgrupoService.obtenerSubgrupos(idGrupo)
                                : subgrupoService.buscarSubgruposPorNombre(idGrupo, filtroSub);

                        List<SubgrupoDTO> subgruposFiltradosDtos = subgruposFiltrados.stream()
                                .map(SubgrupoDTO::fromEntity)
                                .collect(Collectors.toList());

                        try {
                            mapper = new ObjectMapper();
                            mapper.registerModule(new JavaTimeModule());
                            mapper.setSerializationInclusion(Include.NON_NULL);
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            String jsonSubs = mapper.writeValueAsString(subgruposFiltradosDtos);
                            mensajeServer.addArg(jsonSubs);
                            System.out.println("✅ Búsqueda de subgrupos completada: " + subgruposFiltrados.size() + " resultados");
                        } catch (Exception e) {
                            System.err.println("❌ Error al serializar subgrupos filtrados");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;
                    case "ACTUALIZAR_EMPLEADO":
                        mensajeServer.setTipo("EMPLEADO_ACTUALIZADO");

                        Long idUsuarioAct = Long.valueOf(mensajeUser.getArgs().get(0));
                        String nuevoRol = mensajeUser.getArgs().get(1);
                        Long nuevoDeptId = mensajeUser.getArgs().get(2).isEmpty() ? null
                                : Long.valueOf(mensajeUser.getArgs().get(2));
                        Long nuevaConfigJornadaId = (mensajeUser.getArgs().size() > 3 && !mensajeUser.getArgs().get(3).isEmpty())
                                ? Long.valueOf(mensajeUser.getArgs().get(3)) : null;

                        Usuario empActualizado = usuarioService.actualizarEmpleado(idUsuarioAct, nuevoRol, nuevoDeptId, nuevaConfigJornadaId);

                        if (empActualizado != null) {
                            mensajeServer.addArg("actualizado");
                            System.out.println("✅ Empleado actualizado (Rol: " + nuevoRol + ", ConfigJornada: " + nuevaConfigJornadaId + ")");
                        } else {
                            mensajeServer.addArg("error");
                        }

                        enviar(mensajeServer);
                        break;
                    case "ACTUALIZAR_DEPARTAMENTO":
                        mensajeServer.setTipo("DEPARTAMENTO_ACTUALIZADO");

                        try {
                            idDepartamento = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nuevoNombre = mensajeUser.getArgs().get(1);

                            // Buscar el departamento
                            departamentoOpt = departamentoService
                                    .findByIdDepartamento(idDepartamento);

                            if (departamentoOpt.isPresent()) {
                                departamento = departamentoOpt.get();
                                departamento.setNombre(nuevoNombre);

                                Departamento departamentoActualizado = departamentoService.save(departamento);

                                if (departamentoActualizado != null) {
                                    mensajeServer.addArg("actualizado");
                                    System.out.println("✅ Departamento actualizado: " + nuevoNombre);
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("no_encontrado");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al actualizar departamento: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_SUBGRUPO":
                        mensajeServer.setTipo("SUBGRUPO_ACTUALIZADO");

                        try {
                            idSubgrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nuevoNombre = mensajeUser.getArgs().get(1);

                            // Buscar el subgrupo
                            Optional<Subgrupo> subgrupoOpt = subgrupoService.findByIdSubgrupo(idSubgrupo);

                            if (subgrupoOpt.isPresent()) {
                                subgrupo = subgrupoOpt.get();
                                subgrupo.setNombre(nuevoNombre);

                                Subgrupo subgrupoActualizado = subgrupoService.save(subgrupo);

                                if (subgrupoActualizado != null) {
                                    mensajeServer.addArg("actualizado");
                                    System.out.println("✅ Subgrupo actualizado: " + nuevoNombre);
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("no_encontrado");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al actualizar subgrupo: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_DEPARTAMENTO":
                        mensajeServer.setTipo("DEPARTAMENTO_ELIMINADO");

                        try {
                         idDepartamento = Long.valueOf(mensajeUser.getArgs().get(0));

                            // Verificar si hay usuarios en este departamento
                            List<Usuario> usuariosEnDepartamento = usuarioService.findByIdDepartamento(idDepartamento);

                            if (!usuariosEnDepartamento.isEmpty()) {
                                mensajeServer.addArg("tiene_usuarios");
                                System.err.println("❌ No se puede eliminar departamento con usuarios asignados");
                            } else {
                                departamentoService.eliminarDepartamento(idDepartamento);
                                mensajeServer.addArg("eliminado");
                                System.out.println("✅ Departamento eliminado: " + idDepartamento);
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar departamento: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_SUBGRUPO":
                        mensajeServer.setTipo("SUBGRUPO_ELIMINADO");

                        try {
                            idSubgrupo = Long.valueOf(mensajeUser.getArgs().get(0));

                            // Verificar si hay departamentos en este subgrupo
                            List<Departamento> departamentosEnSubgrupo = departamentoService
                                    .obtenerDepartamentos(idSubgrupo);

                            if (!departamentosEnSubgrupo.isEmpty()) {
                                mensajeServer.addArg("tiene_departamentos");
                                System.err.println("❌ No se puede eliminar subgrupo con departamentos");
                            } else {
                                subgrupoService.eliminarSubgrupo(idSubgrupo);
                                mensajeServer.addArg("eliminado");
                                System.out.println("✅ Subgrupo eliminado: " + idSubgrupo);
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar subgrupo: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_EMPLEADO":
                        mensajeServer.setTipo("EMPLEADO_ELIMINADO");

                        try {
                            idUsuario = Long.valueOf(mensajeUser.getArgs().get(0));

                            // Buscar usuario
                            usuarioOpt = usuarioService.findByIdUsuario(idUsuario);

                            if (usuarioOpt.isPresent()) {
                                Usuario usuario = usuarioOpt.get();

                                // Solo eliminar la relación con el grupo, no el usuario completo
                                usuario.setGrupo(null);
                                usuario.setDepartamento(null);
                                usuario.setRol(null);

                                usuarioService.save(usuario);
                                mensajeServer.addArg("eliminado");
                                System.out.println("✅ Empleado eliminado del grupo: " + idUsuario);
                            } else {
                                mensajeServer.addArg("no_encontrado");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar empleado: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "OBTENER_TAREAS_GRUPO":
                        mensajeServer.setTipo("DAR_TAREAS_GRUPO");

                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Tarea> tareasGrupo = tareaService.obtenerTareasPorGrupo(idGrupo);

                            // Convertir entidades a DTOs
                            List<TareaDTO> tareasDTOs = tareasGrupo.stream()
                                .map(TareaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(tareasDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Tareas del grupo obtenidas: " + tareasDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener tareas del grupo");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "CREAR_TAREA_ASIGNACIONES":
                        mensajeServer.setTipo("TAREA_CREADA");

                        try {
                            String titulo = mensajeUser.getArgs().get(0);
                            String descripcion = mensajeUser.getArgs().get(1);
                            String prioridadStr = mensajeUser.getArgs().get(2);
                            String fechaFinStr = mensajeUser.getArgs().get(3);
                            Long idCreador = Long.valueOf(mensajeUser.getArgs().get(4));

                            Tarea nuevaTarea = new Tarea();
                            nuevaTarea.setTitulo(titulo);
                            nuevaTarea.setDescripcion(descripcion);
                            nuevaTarea.setPrioridad(Prioridad.valueOf(prioridadStr.toUpperCase()));

                            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                                nuevaTarea.setFechaFin(java.time.LocalDate.parse(fechaFinStr));
                            }

                            usuarioOpt = usuarioService.findByIdUsuario(idCreador);
                            if (usuarioOpt.isPresent()) {
                                nuevaTarea.setCreadoPor(usuarioOpt.get());
                            }

                            Tarea tareaCreada = tareaService.crearTarea(nuevaTarea);

                            if (tareaCreada != null) {
                                mensajeServer.addArg("creada");
                                mensajeServer.addArg(String.valueOf(tareaCreada.getIdTarea()));
                                System.out.println("✅ Tarea creada: " + titulo);
                            } else {
                                mensajeServer.addArg("error");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear tarea: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "CREAR_TAREA_ASIGNACIONES_COMENTARIO":
                        mensajeServer.setTipo("TAREA_CREADA");

                        try {
                            String titulo = mensajeUser.getArgs().get(0);
                            String descripcion = mensajeUser.getArgs().get(1);
                            String prioridadStr = mensajeUser.getArgs().get(2);
                            String fechaFinStr = mensajeUser.getArgs().get(3);
                            Long idCreador = Long.valueOf(mensajeUser.getArgs().get(4));
                            String usuariosStr = mensajeUser.getArgs().get(5);
                            String departamentosStr = mensajeUser.getArgs().get(6);
                            String comentarioInicial = mensajeUser.getArgs().get(7);

                            // Crear la tarea
                            Tarea nuevaTarea = new Tarea();
                            nuevaTarea.setTitulo(titulo);
                            nuevaTarea.setDescripcion(descripcion);
                            nuevaTarea.setPrioridad(Prioridad.valueOf(prioridadStr.toUpperCase()));

                            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                                nuevaTarea.setFechaFin(java.time.LocalDate.parse(fechaFinStr));
                            }

                            usuarioOpt = usuarioService.findByIdUsuario(idCreador);
                            if (usuarioOpt.isPresent()) {
                                nuevaTarea.setCreadoPor(usuarioOpt.get());
                            }

                            // Guardar la tarea
                            Tarea tareaCreada = tareaService.crearTarea(nuevaTarea);

                            if (tareaCreada != null) {
                                System.out.println("✅ Tarea creada: " + titulo + " (ID: " + tareaCreada.getIdTarea() + ")");

                                // Asignar usuarios (crea registros en tarea_usuario)
                                if (usuariosStr != null && !usuariosStr.isEmpty()) {
                                    String[] idsUsuarios = usuariosStr.split(",");
                                    for (String idUsuarioStr : idsUsuarios) {
                                        if (!idUsuarioStr.trim().isEmpty()) {
                                            try {
                                                Long idUsuarioAsignado = Long.valueOf(idUsuarioStr.trim());
                                                Optional<Usuario> usuarioAsignado = usuarioService.findByIdUsuario(idUsuarioAsignado);
                                                if (usuarioAsignado.isPresent()) {
                                                    Tarea resultado = tareaService.asignarUsuario(tareaCreada.getIdTarea(), usuarioAsignado.get());
                                                    if (resultado != null) {
                                                        tareaCreada = resultado;
                                                        System.out.println("   ✅ Usuario asignado: " + usuarioAsignado.get().getNombre());
                                                    } else {
                                                        System.err.println("   ❌ No se pudo asignar usuario: " + usuarioAsignado.get().getNombre());
                                                    }
                                                }
                                            } catch (NumberFormatException e) {
                                                System.err.println("   ⚠️ ID de usuario inválido: " + idUsuarioStr);
                                            }
                                        }
                                    }
                                }

                                // Asignar departamentos
                                if (departamentosStr != null && !departamentosStr.isEmpty()) {
                                    String[] idsDepartamentos = departamentosStr.split(",");
                                    for (String idDeptStr : idsDepartamentos) {
                                        if (!idDeptStr.trim().isEmpty()) {
                                            try {
                                                Long idDeptAsignado = Long.valueOf(idDeptStr.trim());
                                                Optional<Departamento> deptAsignado = departamentoService.findByIdDepartamento(idDeptAsignado);
                                                if (deptAsignado.isPresent()) {
                                                    Tarea resultado = tareaService.asignarDepartamento(tareaCreada.getIdTarea(), deptAsignado.get());
                                                    if (resultado != null) {
                                                        tareaCreada = resultado;
                                                        System.out.println("   ✅ Departamento asignado: " + deptAsignado.get().getNombre());
                                                    } else {
                                                        System.err.println("   ❌ No se pudo asignar departamento: " + deptAsignado.get().getNombre());
                                                    }
                                                }
                                            } catch (NumberFormatException e) {
                                                System.err.println("   ⚠️ ID de departamento inválido: " + idDeptStr);
                                            }
                                        }
                                    }
                                }

                                // Crear comentario inicial si no está vacío
                                if (comentarioInicial != null && !comentarioInicial.isEmpty()) {
                                    Comentario nuevoComentario = new Comentario();
                                    nuevoComentario.setTexto(comentarioInicial);
                                    usuarioOpt = usuarioService.findByIdUsuario(idCreador);
                                    if (usuarioOpt.isPresent()) {
                                        nuevoComentario.setUsuario(usuarioOpt.get());
                                    }
                                    nuevoComentario.setTarea(tareaCreada);
                                    Comentario comentarioCreado = comentarioService.crearComentario(nuevoComentario);
                                    if (comentarioCreado != null) {
                                        System.out.println("   ✅ Comentario inicial creado en la tarea");
                                    }
                                }

                                mensajeServer.addArg("creada");
                                mensajeServer.addArg(String.valueOf(tareaCreada.getIdTarea()));
                            } else {
                                mensajeServer.addArg("error");
                                System.err.println("❌ No se pudo crear la tarea");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear tarea con asignaciones y comentario: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "CAMBIAR_ESTADO_TAREA":
                        mensajeServer.setTipo("ESTADO_TAREA_CAMBIADO");

                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nuevoEstadoStr = mensajeUser.getArgs().get(1);

                            EstadoTarea nuevoEstado = EstadoTarea.valueOf(nuevoEstadoStr.toUpperCase());
                            Tarea tareaActualizada = tareaService.cambiarEstado(idTarea, nuevoEstado);

                            if (tareaActualizada != null) {
                                mensajeServer.addArg("actualizado");
                                System.out.println("✅ Estado de tarea actualizado: " + nuevoEstado);
                            } else {
                                mensajeServer.addArg("error");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al cambiar estado de tarea: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ASIGNAR_USUARIO_TAREA":
                        mensajeServer.setTipo("USUARIO_ASIGNADO_TAREA");

                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            idUsuario = Long.valueOf(mensajeUser.getArgs().get(1));

                            usuarioOpt = usuarioService.findByIdUsuario(idUsuario);
                            if (usuarioOpt.isPresent()) {
                                Tarea tareaActualizada = tareaService.asignarUsuario(idTarea, usuarioOpt.get());

                                if (tareaActualizada != null) {
                                    mensajeServer.addArg("asignado");
                                    System.out.println("✅ Usuario asignado a tarea");
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("usuario_no_encontrado");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al asignar usuario a tarea: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "OBTENER_COMENTARIOS_TAREA":
                        mensajeServer.setTipo("DAR_COMENTARIOS_TAREA");

                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Comentario> comentarios = comentarioService.obtenerComentariosPorTarea(idTarea);

                            // Convertir entidades a DTOs para evitar problemas de lazy loading
                            List<ComentarioDTO> comentariosDTOs = comentarios.stream()
                                .map(ComentarioDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(comentariosDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Comentarios de tarea obtenidos: " + comentariosDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener comentarios de tarea");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "CREAR_COMENTARIO_TAREA":
                        mensajeServer.setTipo("COMENTARIO_CREADO");

                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            String textoComentario = mensajeUser.getArgs().get(1);
                            idUsuario = Long.valueOf(mensajeUser.getArgs().get(2));

                            usuarioOpt = usuarioService.findByIdUsuario(idUsuario);
                            Optional<Tarea> tareaOpt = tareaService.findByIdTarea(idTarea);

                            if (usuarioOpt.isPresent() && tareaOpt.isPresent()) {
                                Comentario nuevoComentario = new Comentario();
                                nuevoComentario.setTexto(textoComentario);
                                nuevoComentario.setUsuario(usuarioOpt.get());
                                nuevoComentario.setTarea(tareaOpt.get());

                                Comentario comentarioCreado = comentarioService.crearComentario(nuevoComentario);

                                if (comentarioCreado != null) {
                                    mensajeServer.addArg("creado");
                                    System.out.println("✅ Comentario creado en tarea");
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("no_encontrado");
                            }

                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear comentario: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_TAREA":
                        mensajeServer.setTipo("TAREA_ELIMINADA");

                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            tareaService.eliminarTarea(idTarea);
                            mensajeServer.addArg("eliminada");
                            System.out.println("✅ Tarea eliminada: " + idTarea);
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar tarea: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    // ==================== CASOS PARA INCIDENCIAS ====================

                    case "OBTENER_INCIDENCIAS_GRUPO":
                        mensajeServer.setTipo("DAR_INCIDENCIAS_GRUPO");

                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Incidencia> incidenciasGrupo = incidenciaService.obtenerIncidenciasPorGrupo(idGrupo);

                            // Convertir entidades a DTOs
                            List<IncidenciaDTO> incidenciasDTOs = incidenciasGrupo.stream()
                                .map(IncidenciaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(incidenciasDTOs);
                            mensajeServer.addArg(json);
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener incidencias del grupo");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "CREAR_INCIDENCIA":
                        mensajeServer.setTipo("INCIDENCIA_CREADA");

                        try {
                            // Leer argumentos individuales (titulo, descripcion, prioridad, estado, idUsuario)
                            String titulo = mensajeUser.getArgs().get(0);
                            String descripcion = mensajeUser.getArgs().get(1);
                            String prioridad = mensajeUser.getArgs().get(2);
                            String estado = mensajeUser.getArgs().get(3);
                            Long idUsuarioIncidencia = Long.valueOf(mensajeUser.getArgs().get(4));

                            Incidencia nuevaIncidencia = new Incidencia();
                            nuevaIncidencia.setTitulo(titulo);
                            nuevaIncidencia.setDescripcion(descripcion);

                            if (prioridad != null && !prioridad.isEmpty()) {
                                nuevaIncidencia.setPrioridad(Prioridad.valueOf(prioridad));
                            }

                            if (estado != null && !estado.isEmpty()) {
                                nuevaIncidencia.setEstado(EstadoTarea.valueOf(estado));
                            }

                            Optional<Usuario> usuarioIncidencia = usuarioService.findByIdUsuario(idUsuarioIncidencia);
                            usuarioIncidencia.ifPresent(nuevaIncidencia::setUsuario);

                            incidenciaService.crearIncidencia(nuevaIncidencia);
                            mensajeServer.addArg("creada");
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear incidencia: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_INCIDENCIA":
                        mensajeServer.setTipo("INCIDENCIA_ELIMINADA");

                        try {
                            Long idIncidencia = Long.valueOf(mensajeUser.getArgs().get(0));
                            incidenciaService.eliminarIncidencia(idIncidencia);
                            mensajeServer.addArg("eliminada");
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar incidencia: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "ACEPTAR_INVITACION":
                        mensajeServer.setTipo("exito");
                        try {
                            Long idNotificacion = Long.valueOf(mensajeUser.getArgs().get(0));
                            idUsuario = Long.valueOf(mensajeUser.getArgs().get(1));
                            
                            var notificacion = notificacionService.findByIdNotificacion(idNotificacion);
                            if (notificacion.isPresent()) {
                                Notificacion notif = notificacion.get();
                                
                                // Asignar usuario al grupo
                                usuarioService.asignarUsuarioAGrupo(idUsuario, 
                                    notif.getGrupo().getIdGrupo(), 
                                    notif.getGrupo().getSubgrupos().stream().findFirst().orElse(null),
                                    notif.getDepartamento(),
                                    Rol.EMPLEADO);
                                
                                // Actualizar estado notificación
                                notif.setEstado(EstadoNotificacion.ACEPTADA);
                                notificacionService.save(notif);
                                
                                mensajeServer.addArg("exito");
                                System.out.println("✅ Invitación aceptada: Usuario " + idUsuario);
                            } else {
                                mensajeServer.addArg("error");
                                System.err.println("❌ Notificación no encontrada");
                            }
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error aceptar invitación: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "RECHAZAR_INVITACION":
                        mensajeServer.setTipo("exito");
                        try {
                            Long idNotificacion = Long.valueOf(mensajeUser.getArgs().get(0));
                            
                            var notificacion = notificacionService.findByIdNotificacion(idNotificacion);
                            if (notificacion.isPresent()) {
                                Notificacion notif = notificacion.get();
                                
                                // Actualizar estado notificación
                                notif.setEstado(EstadoNotificacion.RECHAZADA);
                                notificacionService.save(notif);
                                
                                mensajeServer.addArg("exito");
                                System.out.println("✅ Invitación rechazada: Notificación " + idNotificacion);
                            } else {
                                mensajeServer.addArg("error");
                                System.err.println("❌ Notificación no encontrada");
                            }
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error rechazar invitación: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    // ==================== CASOS DE EVENTOS ====================

                    case "CREAR_EVENTO_ASIGNACIONES":
                        mensajeServer.setTipo("EVENTO_CREADO");

                        try {
                            String tituloEvento = mensajeUser.getArgs().get(0);
                            String descripcionEvento = mensajeUser.getArgs().get(1);
                            String fechaInicioStr = mensajeUser.getArgs().get(2);
                            String fechaFinStr = mensajeUser.getArgs().get(3);
                            Boolean seRepite = Boolean.valueOf(mensajeUser.getArgs().get(4));
                            Integer diasRepeticion = Integer.valueOf(mensajeUser.getArgs().get(5));
                            Long idCreador = Long.valueOf(mensajeUser.getArgs().get(6));
                            String usuariosJson = mensajeUser.getArgs().get(7);
                            String departamentosJson = mensajeUser.getArgs().get(8);

                            // Parsear listas de IDs
                            List<Long> usuariosIds = mapper.readValue(usuariosJson,
                                mapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                            List<Long> departamentosIds = mapper.readValue(departamentosJson,
                                mapper.getTypeFactory().constructCollectionType(List.class, Long.class));

                            // Convertir fechas ISO a LocalDateTime
                            java.time.LocalDateTime fechaInicio = java.time.LocalDateTime.parse(fechaInicioStr);
                            java.time.LocalDateTime fechaFin = java.time.LocalDateTime.parse(fechaFinStr);

                            // Obtener usuario creador
                            usuarioOpt = usuarioService.findByIdUsuario(idCreador);

                            if (usuarioOpt.isPresent()) {
                                // Crear evento
                                Evento nuevoEvento = new Evento();
                                nuevoEvento.setTitulo(tituloEvento);
                                nuevoEvento.setDescripcion(descripcionEvento);
                                nuevoEvento.setFechaInicio(fechaInicio.toLocalDate());
                                nuevoEvento.setFechaFin(fechaFin.toLocalDate());
                                nuevoEvento.setHoraInicio(fechaInicio.toLocalTime());
                                nuevoEvento.setHoraFin(fechaFin.toLocalTime());
                                nuevoEvento.setSeRepite(seRepite);
                                nuevoEvento.setDiasRepeticion(diasRepeticion);
                                nuevoEvento.setCreadoPor(usuarioOpt.get());

                                Evento eventoCreado = eventoService.crearEvento(nuevoEvento);

                                if (eventoCreado != null) {
                                    // Asignar usuarios
                                    for (Long idUsuarioAsignado : usuariosIds) {
                                        Optional<Usuario> usuarioAsignadoOpt = usuarioService.findByIdUsuario(idUsuarioAsignado);
                                        if (usuarioAsignadoOpt.isPresent()) {
                                            eventoService.asignarUsuario(eventoCreado.getIdEvento(), usuarioAsignadoOpt.get());
                                        }
                                    }

                                    // Asignar departamentos
                                    for (Long idDept : departamentosIds) {
                                        Optional<Departamento> deptOpt = departamentoService.findByIdDepartamento(idDept);
                                        if (deptOpt.isPresent()) {
                                            eventoService.asignarDepartamento(eventoCreado.getIdEvento(), deptOpt.get());
                                        }
                                    }

                                    mensajeServer.addArg("exito");
                                    System.out.println("✅ Evento creado: " + eventoCreado.getTitulo() +
                                        " con " + usuariosIds.size() + " usuarios y " + departamentosIds.size() + " departamentos");
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("usuario_no_existe");
                            }
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al crear evento: " + e.getMessage());
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "OBTENER_EVENTOS_GRUPO":
                        mensajeServer.setTipo("DAR_EVENTOS_GRUPO");

                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Evento> eventosGrupo = eventoService.obtenerEventosPorGrupo(idGrupo);

                            // Convertir entidades a DTOs
                            List<EventoDTO> eventosDTOs = eventosGrupo.stream()
                                .map(EventoDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(eventosDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Eventos del grupo obtenidos: " + eventosDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener eventos del grupo");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    case "OBTENER_EVENTOS_USUARIO":
                        mensajeServer.setTipo("DAR_EVENTOS_USUARIO");

                        try {
                            Long idUsuarioEventos = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Evento> eventosUsuario = eventoService.obtenerEventosDeUsuario(idUsuarioEventos);

                            // Convertir entidades a DTOs
                            List<EventoDTO> eventosDTOs = eventosUsuario.stream()
                                .map(EventoDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(eventosDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Eventos del usuario obtenidos: " + eventosDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener eventos del usuario");
                            e.printStackTrace();
                        }

                        enviar(mensajeServer);
                        break;

                    // ==================== HISTORIAL HANDLERS ====================
                    case "OBTENER_HISTORIAL_GRUPO":
                        mensajeServer.setTipo("DAR_HISTORIAL_GRUPO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Historial> historial = historialService.obtenerHistorialGrupo(idGrupo);
                            List<HistorialDTO> historialDTOs = historial.stream()
                                .map(HistorialDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(historialDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Historial del grupo obtenido");
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener historial del grupo");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "BUSCAR_HISTORIAL_CON_FILTROS":
                        mensajeServer.setTipo("DAR_HISTORIAL_FILTRADO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String tipoAccionStr = mensajeUser.getArgs().get(1);
                            String idUsuarioStr = mensajeUser.getArgs().get(2);
                            String fechaDesdeStr = mensajeUser.getArgs().get(3);
                            String fechaHastaStr = mensajeUser.getArgs().get(4);

                            TipoAccionHistorial tipoAccion = tipoAccionStr != null && !tipoAccionStr.equals("null") ?
                                TipoAccionHistorial.valueOf(tipoAccionStr) : null;
                            Long idUsuarioFiltro = idUsuarioStr != null && !idUsuarioStr.equals("null") ?
                                Long.valueOf(idUsuarioStr) : null;
                            LocalDateTime fechaDesde = fechaDesdeStr != null && !fechaDesdeStr.equals("null") ?
                                LocalDateTime.parse(fechaDesdeStr) : null;
                            LocalDateTime fechaHasta = fechaHastaStr != null && !fechaHastaStr.equals("null") ?
                                LocalDateTime.parse(fechaHastaStr) : null;

                            List<Historial> historial = historialService.buscarHistorialConFiltros(
                                idGrupo, tipoAccion, idUsuarioFiltro, fechaDesde, fechaHasta);
                            List<HistorialDTO> historialDTOs = historial.stream()
                                .map(HistorialDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(historialDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Historial filtrado obtenido");
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener historial filtrado");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    // ==================== JORNADA LABORAL HANDLERS ====================
                    case "REGISTRAR_ENTRADA_JORNADA":
                        mensajeServer.setTipo("ENTRADA_REGISTRADA");
                        try {
                            Long idUsuarioJornada = Long.valueOf(mensajeUser.getArgs().get(0));
                            String horaEntrada = mensajeUser.getArgs().get(1);
                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioJornada);
                            if (usuarioOpt.isPresent()) {
                                JornadaLaboral jornada = jornadaLaboralService.registrarEntrada(
                                    usuarioOpt.get(), null, LocalTime.parse(horaEntrada));

                                mapper.registerModule(new JavaTimeModule());
                                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                mapper.setSerializationInclusion(Include.NON_NULL);

                                json = mapper.writeValueAsString(JornadaLaboralDTO.fromEntity(jornada));
                                mensajeServer.addArg("exito");
                                mensajeServer.addArg(json);
                                System.out.println("✅ Entrada registrada");
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al registrar entrada");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "REGISTRAR_SALIDA_JORNADA":
                        mensajeServer.setTipo("SALIDA_REGISTRADA");
                        try {
                            Long idUsuarioSalida = Long.valueOf(mensajeUser.getArgs().get(0));
                            String horaSalida = mensajeUser.getArgs().get(1);

                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioSalida);
                            if (usuarioOpt.isPresent()) {
                                JornadaLaboral jornada = jornadaLaboralService.registrarSalida(
                                    usuarioOpt.get(), LocalTime.parse(horaSalida));

                                mapper.registerModule(new JavaTimeModule());
                                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                mapper.setSerializationInclusion(Include.NON_NULL);

                                json = mapper.writeValueAsString(JornadaLaboralDTO.fromEntity(jornada));
                                mensajeServer.addArg("exito");
                                mensajeServer.addArg(json);
                                System.out.println("✅ Salida registrada");
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al registrar salida");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "REGISTRAR_DESCANSO":
                        mensajeServer.setTipo("DESCANSO_REGISTRADO");
                        try {
                            Long idUsuarioDescanso = Long.valueOf(mensajeUser.getArgs().get(0));
                            String tipoDescansoStr = mensajeUser.getArgs().get(1);
                            Integer duracionMinutos = Integer.valueOf(mensajeUser.getArgs().get(2));
                            String horaInicioStr = mensajeUser.getArgs().get(3);

                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioDescanso);
                            if (usuarioOpt.isPresent()) {
                                Optional<JornadaLaboral> jornadaOpt = jornadaLaboralService.obtenerJornadaActual(idUsuarioDescanso);
                                if (jornadaOpt.isPresent()) {
                                    TipoDescanso tipoDescanso = TipoDescanso.valueOf(tipoDescansoStr);
                                    DescansoJornada descanso = descansoJornadaService.registrarDescanso(
                                        jornadaOpt.get(), tipoDescanso, duracionMinutos, LocalTime.parse(horaInicioStr));

                                    // Iniciar descanso en la jornada
                                    jornadaLaboralService.iniciarDescanso(usuarioOpt.get());

                                    mapper.registerModule(new JavaTimeModule());
                                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                    mapper.setSerializationInclusion(Include.NON_NULL);

                                    json = mapper.writeValueAsString(DescansoJornadaDTO.fromEntity(descanso));
                                    mensajeServer.addArg("exito");
                                    mensajeServer.addArg(json);
                                    System.out.println("✅ Descanso registrado");
                                } else {
                                    mensajeServer.addArg("sin_jornada");
                                }
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al registrar descanso");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "REANUDAR_TRABAJO":
                        mensajeServer.setTipo("TRABAJO_REANUDADO");
                        try {
                            Long idUsuarioReanudar = Long.valueOf(mensajeUser.getArgs().get(0));
                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioReanudar);
                            if (usuarioOpt.isPresent()) {
                                jornadaLaboralService.finalizarDescanso(usuarioOpt.get());
                                mensajeServer.addArg("exito");
                                System.out.println("✅ Trabajo reanudado");
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al reanudar trabajo");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_JORNADA_ACTUAL":
                        mensajeServer.setTipo("DAR_JORNADA_ACTUAL");
                        try {
                            Long idUsuarioJornada = Long.valueOf(mensajeUser.getArgs().get(0));
                            Optional<JornadaLaboral> jornadaOpt = jornadaLaboralService.obtenerJornadaActual(idUsuarioJornada);

                            if (jornadaOpt.isPresent()) {
                                mapper.registerModule(new JavaTimeModule());
                                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                mapper.setSerializationInclusion(Include.NON_NULL);

                                json = mapper.writeValueAsString(JornadaLaboralDTO.fromEntity(jornadaOpt.get()));
                                mensajeServer.addArg(json);
                                System.out.println("✅ Jornada actual obtenida");
                            } else {
                                mensajeServer.addArg("null");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener jornada actual");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_JORNADAS_USUARIO":
                        mensajeServer.setTipo("DAR_JORNADAS_USUARIO");
                        try {
                            Long idUsuarioJornadas = Long.valueOf(mensajeUser.getArgs().get(0));
                            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(mensajeUser.getArgs().get(1));
                            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(mensajeUser.getArgs().get(2));

                            List<JornadaLaboral> jornadas = jornadaLaboralService.obtenerJornadasUsuario(
                                idUsuarioJornadas, fechaDesde, fechaHasta);
                            List<JornadaLaboralDTO> jornadasDTOs = jornadas.stream()
                                .map(JornadaLaboralDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(jornadasDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Jornadas de usuario obtenidas: " + jornadasDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener jornadas de usuario");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_JORNADAS_GRUPO":
                        mensajeServer.setTipo("DAR_JORNADAS_GRUPO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(mensajeUser.getArgs().get(1));
                            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(mensajeUser.getArgs().get(2));

                            List<JornadaLaboral> jornadas = jornadaLaboralService.obtenerJornadasGrupo(
                                idGrupo, fechaDesde, fechaHasta);
                            List<JornadaLaboralDTO> jornadasDTOs = jornadas.stream()
                                .map(JornadaLaboralDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(jornadasDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Jornadas de grupo obtenidas: " + jornadasDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener jornadas de grupo");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_EMPLEADOS_SIN_FICHAR":
                        mensajeServer.setTipo("DAR_EMPLEADOS_SIN_FICHAR");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String fechaStr = mensajeUser.getArgs().get(1);
                            java.time.LocalDate fecha = fechaStr != null && !fechaStr.equals("null") ?
                                java.time.LocalDate.parse(fechaStr) : java.time.LocalDate.now();

                            List<Long> idsSinFichar = jornadaLaboralService.obtenerEmpleadosSinFichar(idGrupo, fecha);

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(idsSinFichar);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Empleados sin fichar obtenidos: " + idsSinFichar.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener empleados sin fichar");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_DESCANSOS_JORNADA":
                        mensajeServer.setTipo("DAR_DESCANSOS_JORNADA");
                        try {
                            Long idJornada = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<DescansoJornada> descansos = descansoJornadaService.obtenerDescansosPorJornada(idJornada);
                            List<DescansoJornadaDTO> descansosDTOs = descansos.stream()
                                .map(DescansoJornadaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(descansosDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Descansos de jornada obtenidos: " + descansosDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener descansos de jornada");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    // ==================== ESTADÍSTICAS HANDLERS ====================
                    case "OBTENER_ESTADISTICAS_USUARIO":
                        mensajeServer.setTipo("DAR_ESTADISTICAS_USUARIO");
                        try {
                            Long idUsuarioEstadisticas = Long.valueOf(mensajeUser.getArgs().get(0));
                            List<Estadistica> estadisticas = estadisticaService.obtenerEstadisticasUsuario(idUsuarioEstadisticas);
                            List<EstadisticaDTO> estadisticasDTOs = estadisticas.stream()
                                .map(EstadisticaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(estadisticasDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Estadísticas de usuario obtenidas");
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener estadísticas de usuario");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_ESTADISTICAS_GRUPO":
                        mensajeServer.setTipo("DAR_ESTADISTICAS_GRUPO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String fechaDesdeStr = mensajeUser.getArgs().size() > 1 ? mensajeUser.getArgs().get(1) : "";
                            String fechaHastaStr = mensajeUser.getArgs().size() > 2 ? mensajeUser.getArgs().get(2) : "";

                            List<Estadistica> estadisticas;

                            // Si se especifican fechas, usar rango; si no, usar últimas estadísticas
                            if (fechaDesdeStr != null && !fechaDesdeStr.isEmpty() &&
                                fechaHastaStr != null && !fechaHastaStr.isEmpty()) {
                                LocalDate fechaDesde = LocalDate.parse(fechaDesdeStr);
                                LocalDate fechaHasta = LocalDate.parse(fechaHastaStr);
                                estadisticas = estadisticaService.obtenerEstadisticasGrupoPorRango(idGrupo, fechaDesde, fechaHasta);
                                System.out.println("📊 Obteniendo estadísticas de grupo con rango: " + fechaDesde + " a " + fechaHasta);
                            } else {
                                estadisticas = estadisticaService.obtenerUltimasEstadisticasUsuariosDelGrupo(idGrupo);
                                System.out.println("📊 Obteniendo últimas estadísticas de usuarios del grupo");
                            }

                            List<EstadisticaDTO> estadisticasDTOs = estadisticas.stream()
                                .map(EstadisticaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(estadisticasDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Estadísticas de grupo obtenidas: " + estadisticas.size() + " registros");
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener estadísticas de grupo");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_ESTADISTICAS":
                        // Alias para ACTUALIZAR_ESTADISTICAS_USUARIO, acepta idUsuario e idGrupo pero solo usa idUsuario
                        mensajeServer.setTipo("ESTADISTICAS_ACTUALIZADAS");
                        try {
                            Long idUsuarioActualizar = Long.valueOf(mensajeUser.getArgs().get(0));
                            // idGrupo se recibe pero no se usa actualmente
                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioActualizar);
                            if (usuarioOpt.isPresent()) {
                                Estadistica estadistica = estadisticaService.actualizarEstadisticasUsuario(usuarioOpt.get());

                                mapper.registerModule(new JavaTimeModule());
                                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                mapper.setSerializationInclusion(Include.NON_NULL);

                                json = mapper.writeValueAsString(EstadisticaDTO.fromEntity(estadistica));
                                mensajeServer.addArg("exito");
                                mensajeServer.addArg(json);
                                System.out.println("✅ Estadísticas actualizadas (desde ACTUALIZAR_ESTADISTICAS)");
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al actualizar estadísticas");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_ESTADISTICAS_USUARIO":
                        mensajeServer.setTipo("ESTADISTICAS_ACTUALIZADAS");
                        try {
                            Long idUsuarioActualizar = Long.valueOf(mensajeUser.getArgs().get(0));
                            usuarioOpt = usuarioService.findByIdUsuario(idUsuarioActualizar);
                            if (usuarioOpt.isPresent()) {
                                Estadistica estadistica = estadisticaService.actualizarEstadisticasUsuario(usuarioOpt.get());

                                mapper.registerModule(new JavaTimeModule());
                                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                                mapper.setSerializationInclusion(Include.NON_NULL);

                                json = mapper.writeValueAsString(EstadisticaDTO.fromEntity(estadistica));
                                mensajeServer.addArg("exito");
                                mensajeServer.addArg(json);
                                System.out.println("✅ Estadísticas de usuario actualizadas");
                            } else {
                                mensajeServer.addArg("error");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error al actualizar estadísticas de usuario");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    // ==================== INFORMES HANDLERS ====================
                    case "GENERAR_INFORME_JORNADA":
                        mensajeServer.setTipo("INFORME_GENERADO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String idUsuarioStr = mensajeUser.getArgs().get(1);
                            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(mensajeUser.getArgs().get(2));
                            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(mensajeUser.getArgs().get(3));
                            String rutaDestino = mensajeUser.getArgs().get(4);

                            Long idUsuarioInforme = idUsuarioStr != null && !idUsuarioStr.equals("null") ?
                                Long.valueOf(idUsuarioStr) : null;

                            String archivoGenerado = informeService.generarInformeJornadaLaboral(
                                idGrupo, idUsuarioInforme, fechaDesde, fechaHasta, rutaDestino);

                            mensajeServer.addArg("exito");
                            mensajeServer.addArg(archivoGenerado);
                            System.out.println("✅ Informe de jornada generado: " + archivoGenerado);
                        } catch (Exception e) {
                            System.err.println("❌ Error al generar informe de jornada");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "GENERAR_INFORME_ESTADISTICAS":
                        mensajeServer.setTipo("INFORME_GENERADO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String idUsuarioStr = mensajeUser.getArgs().get(1);
                            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(mensajeUser.getArgs().get(2));
                            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(mensajeUser.getArgs().get(3));
                            String rutaDestino = mensajeUser.getArgs().get(4);

                            Long idUsuarioInforme = idUsuarioStr != null && !idUsuarioStr.equals("null") ?
                                Long.valueOf(idUsuarioStr) : null;

                            String archivoGenerado = informeService.generarInformeEstadisticas(
                                idGrupo, idUsuarioInforme, fechaDesde, fechaHasta, rutaDestino);

                            mensajeServer.addArg("exito");
                            mensajeServer.addArg(archivoGenerado);
                            System.out.println("✅ Informe de estadísticas generado: " + archivoGenerado);
                        } catch (Exception e) {
                            System.err.println("❌ Error al generar informe de estadísticas");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    case "GENERAR_INFORME_RESUMEN":
                        mensajeServer.setTipo("INFORME_GENERADO");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String idUsuarioStr = mensajeUser.getArgs().get(1);
                            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(mensajeUser.getArgs().get(2));
                            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(mensajeUser.getArgs().get(3));
                            String rutaDestino = mensajeUser.getArgs().get(4);
                            String observaciones = mensajeUser.getArgs().get(5);

                            Long idUsuarioInforme = idUsuarioStr != null && !idUsuarioStr.equals("null") ?
                                Long.valueOf(idUsuarioStr) : null;

                            String archivoGenerado = informeService.generarInformeResumen(
                                idGrupo, idUsuarioInforme, fechaDesde, fechaHasta, rutaDestino, observaciones);

                            mensajeServer.addArg("exito");
                            mensajeServer.addArg(archivoGenerado);
                            System.out.println("✅ Informe de resumen generado: " + archivoGenerado);
                        } catch (Exception e) {
                            System.err.println("❌ Error al generar informe de resumen");
                            e.printStackTrace();
                            mensajeServer.addArg("error");
                        }
                        enviar(mensajeServer);
                        break;

                    // ==================== CONFIGURACIÓN JORNADA HANDLERS ====================

                    case "CREAR_CONFIGURACION_JORNADA":
                        mensajeServer.setTipo("CONFIGURACION_JORNADA_CREADA");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nombreConfig = mensajeUser.getArgs().get(1);
                            String fechaInicioStr = mensajeUser.getArgs().get(2);
                            String fechaFinStr = mensajeUser.getArgs().get(3);
                            String horariosJson = mensajeUser.getArgs().get(4);

                            // Parsear fechas
                            java.time.LocalDate fechaInicio = java.time.LocalDate.parse(fechaInicioStr);
                            java.time.LocalDate fechaFin = java.time.LocalDate.parse(fechaFinStr);

                            // Deserializar horarios usando Gson
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            com.google.gson.reflect.TypeToken<List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO>> typeToken =
                                new com.google.gson.reflect.TypeToken<List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO>>() {};
                            List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO> horariosDTOs =
                                gson.fromJson(horariosJson, typeToken.getType());

                            // Convertir DTOs a entidades
                            List<com.iesfernandoaguilar.solsonafuentes.model.HorarioDia> horarios = new ArrayList<>();
                            if (horariosDTOs != null) {
                                for (com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO dto : horariosDTOs) {
                                    horarios.add(dto.toEntity());
                                }
                            }

                            // Crear configuración
                            ConfiguracionJornada configuracion = configuracionJornadaService.crearConfiguracion(
                                nombreConfig, fechaInicio, fechaFin, idGrupo, null, horarios
                            );

                            mensajeServer.addArg(configuracion.getIdConfig().toString());
                            mensajeServer.addArg("creada");
                            System.out.println("✅ Configuración creada: " + nombreConfig);
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            mensajeServer.addArg(e.getMessage());
                            System.err.println("❌ Error al crear configuración: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_CONFIGURACION_JORNADA":
                        mensajeServer.setTipo("CONFIGURACION_JORNADA_ACTUALIZADA");
                        try {
                            Long idConfig = Long.valueOf(mensajeUser.getArgs().get(0));
                            String nombreConfig = mensajeUser.getArgs().get(1);
                            String fechaInicioStr = mensajeUser.getArgs().get(2);
                            String fechaFinStr = mensajeUser.getArgs().get(3);
                            String horariosJson = mensajeUser.getArgs().get(4);

                            // Parsear fechas
                            java.time.LocalDate fechaInicio = java.time.LocalDate.parse(fechaInicioStr);
                            java.time.LocalDate fechaFin = java.time.LocalDate.parse(fechaFinStr);

                            // Deserializar horarios usando Gson
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            com.google.gson.reflect.TypeToken<List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO>> typeToken =
                                new com.google.gson.reflect.TypeToken<List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO>>() {};
                            List<com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO> horariosDTOs =
                                gson.fromJson(horariosJson, typeToken.getType());

                            // Convertir DTOs a entidades
                            List<com.iesfernandoaguilar.solsonafuentes.model.HorarioDia> horarios = new ArrayList<>();
                            if (horariosDTOs != null) {
                                for (com.iesfernandoaguilar.solsonafuentes.dto.HorarioDiaDTO dto : horariosDTOs) {
                                    horarios.add(dto.toEntity());
                                }
                            }

                            // Actualizar configuración
                            ConfiguracionJornada configuracion = configuracionJornadaService.actualizarConfiguracion(
                                idConfig, nombreConfig, fechaInicio, fechaFin, horarios
                            );

                            mensajeServer.addArg(configuracion.getIdConfig().toString());
                            mensajeServer.addArg("actualizada");
                            System.out.println("✅ Configuración actualizada: " + nombreConfig);
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            mensajeServer.addArg(e.getMessage());
                            System.err.println("❌ Error al actualizar configuración: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_CONFIGURACIONES_JORNADA":
                        mensajeServer.setTipo("DAR_CONFIGURACIONES_JORNADA");
                        try {
                            idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
                            String soloActivas = mensajeUser.getArgs().size() > 1 ? mensajeUser.getArgs().get(1) : "false";

                            List<ConfiguracionJornada> configuraciones = Boolean.parseBoolean(soloActivas)
                                ? configuracionJornadaService.obtenerConfiguracionesActivas(idGrupo)
                                : configuracionJornadaService.obtenerConfiguracionesPorGrupo(idGrupo);

                            List<ConfiguracionJornadaDTO> configsDTOs = configuraciones.stream()
                                .map(ConfiguracionJornadaDTO::fromEntity)
                                .collect(Collectors.toList());

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(configsDTOs);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Configuraciones obtenidas: " + configsDTOs.size());
                        } catch (Exception e) {
                            System.err.println("❌ Error al obtener configuraciones");
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_CONFIGURACION_JORNADA":
                        mensajeServer.setTipo("CONFIGURACION_JORNADA_ELIMINADA");
                        try {
                            Long idConfig = Long.valueOf(mensajeUser.getArgs().get(0));
                            configuracionJornadaService.eliminarConfiguracion(idConfig);
                            mensajeServer.addArg("eliminada");
                            System.out.println("✅ Configuración eliminada: " + idConfig);
                        } catch (IllegalStateException e) {
                            mensajeServer.addArg("tiene_usuarios");
                            System.err.println("❌ " + e.getMessage());
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar configuración: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_TAREA":
                        mensajeServer.setTipo("TAREA_ACTUALIZADA");
                        try {
                            Long idTarea = Long.valueOf(mensajeUser.getArgs().get(0));
                            String titulo = mensajeUser.getArgs().get(1);
                            String descripcion = mensajeUser.getArgs().get(2);
                            String prioridadStr = mensajeUser.getArgs().get(3);
                            String fechaFinStr = mensajeUser.getArgs().get(4);

                            // Parámetros opcionales para asignaciones (compatibilidad con versiones antiguas)
                            String usuariosStr = "";
                            String departamentosStr = "";

                            if (mensajeUser.getArgs().size() > 5) {
                                usuariosStr = mensajeUser.getArgs().get(5);
                            }
                            if (mensajeUser.getArgs().size() > 6) {
                                departamentosStr = mensajeUser.getArgs().get(6);
                            }

                            Optional<Tarea> tareaOpt = tareaService.findByIdTarea(idTarea);
                            if (tareaOpt.isPresent()) {
                                Tarea tarea = tareaOpt.get();
                                tarea.setTitulo(titulo);
                                tarea.setDescripcion(descripcion);
                                tarea.setPrioridad(Prioridad.valueOf(prioridadStr.toUpperCase()));

                                if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                                    tarea.setFechaFin(java.time.LocalDate.parse(fechaFinStr));
                                } else {
                                    tarea.setFechaFin(null);
                                }

                                // Limpiar asignaciones actuales
                                tarea.getUsuariosAsignados().clear();
                                tarea.getDepartamentosAsignados().clear();

                                // Asignar nuevos usuarios
                                if (usuariosStr != null && !usuariosStr.isEmpty()) {
                                    String[] idsUsuarios = usuariosStr.split(",");
                                    for (String idUsuarioStr : idsUsuarios) {
                                        if (!idUsuarioStr.trim().isEmpty()) {
                                            Long idUsuarioAsignado = Long.valueOf(idUsuarioStr.trim());
                                            usuarioService.findByIdUsuario(idUsuarioAsignado).ifPresent(
                                                usuario -> tarea.getUsuariosAsignados().add(usuario)
                                            );
                                        }
                                    }
                                }

                                // Asignar nuevos departamentos
                                if (departamentosStr != null && !departamentosStr.isEmpty()) {
                                    String[] idsDepartamentos = departamentosStr.split(",");
                                    for (String idDeptStr : idsDepartamentos) {
                                        if (!idDeptStr.trim().isEmpty()) {
                                            Long idDeptAsignado = Long.valueOf(idDeptStr.trim());
                                            departamentoService.findByIdDepartamento(idDeptAsignado).ifPresent(
                                                dept -> tarea.getDepartamentosAsignados().add(dept)
                                            );
                                        }
                                    }
                                }

                                Tarea tareaActualizada = tareaService.actualizarTarea(tarea);
                                if (tareaActualizada != null) {
                                    mensajeServer.addArg("actualizada");
                                    System.out.println("✅ Tarea actualizada: " + titulo);
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("no_encontrada");
                            }
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al actualizar tarea: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ELIMINAR_EVENTO":
                        mensajeServer.setTipo("EVENTO_ELIMINADO");
                        try {
                            Long idEvento = Long.valueOf(mensajeUser.getArgs().get(0));
                            eventoService.eliminarEvento(idEvento);
                            mensajeServer.addArg("eliminado");
                            System.out.println("✅ Evento eliminado: " + idEvento);
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al eliminar evento: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;

                    case "ACTUALIZAR_EVENTO":
                        mensajeServer.setTipo("EVENTO_ACTUALIZADO");
                        try {
                            Long idEvento = Long.valueOf(mensajeUser.getArgs().get(0));
                            String tituloEvento = mensajeUser.getArgs().get(1);
                            String descripcionEvento = mensajeUser.getArgs().get(2);
                            String fechaInicioStr = mensajeUser.getArgs().get(3);
                            String fechaFinStr = mensajeUser.getArgs().get(4);

                            // Parámetros opcionales (para compatibilidad con versión simple)
                            Boolean seRepite = false;
                            Integer diasRepeticion = 0;
                            List<Long> usuariosIds = new ArrayList<>();
                            List<Long> departamentosIds = new ArrayList<>();

                            // Si hay más argumentos, procesarlos
                            if (mensajeUser.getArgs().size() > 5) {
                                seRepite = Boolean.valueOf(mensajeUser.getArgs().get(5));
                                diasRepeticion = Integer.valueOf(mensajeUser.getArgs().get(6));
                                String usuariosJson = mensajeUser.getArgs().get(7);
                                String departamentosJson = mensajeUser.getArgs().get(8);

                                // Parsear listas de IDs
                                usuariosIds = mapper.readValue(usuariosJson,
                                    mapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                                departamentosIds = mapper.readValue(departamentosJson,
                                    mapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                            }

                            Optional<Evento> eventoOpt = eventoService.findByIdEvento(idEvento);
                            if (eventoOpt.isPresent()) {
                                Evento evento = eventoOpt.get();
                                evento.setTitulo(tituloEvento);
                                evento.setDescripcion(descripcionEvento);

                                // Parsear fechas - pueden venir en formato ISO DateTime o solo fecha
                                evento.setFechaInicio(parseFecha(fechaInicioStr));
                                evento.setFechaFin(parseFecha(fechaFinStr));

                                // Parsear horas si vienen en formato ISO DateTime
                                if (fechaInicioStr.contains("T")) {
                                    java.time.LocalDateTime fechaInicioDateTime = java.time.LocalDateTime.parse(fechaInicioStr);
                                    java.time.LocalDateTime fechaFinDateTime = java.time.LocalDateTime.parse(fechaFinStr);
                                    evento.setHoraInicio(fechaInicioDateTime.toLocalTime());
                                    evento.setHoraFin(fechaFinDateTime.toLocalTime());
                                } else {
                                    evento.setHoraInicio(null);
                                    evento.setHoraFin(null);
                                }

                                evento.setSeRepite(seRepite);
                                evento.setDiasRepeticion(diasRepeticion);

                                // Solo actualizar asignaciones si se proporcionaron
                                if (mensajeUser.getArgs().size() > 5) {
                                    // Limpiar asignaciones actuales
                                    evento.getUsuariosAsistentes().clear();
                                    evento.getDepartamentosInvitados().clear();

                                    // Asignar nuevos usuarios
                                    for (Long idUsuarioAsignado : usuariosIds) {
                                        usuarioService.findByIdUsuario(idUsuarioAsignado).ifPresent(
                                            usuario -> evento.getUsuariosAsistentes().add(usuario)
                                        );
                                    }

                                    // Asignar nuevos departamentos
                                    for (Long idDept : departamentosIds) {
                                        departamentoService.findByIdDepartamento(idDept).ifPresent(
                                            dept -> evento.getDepartamentosInvitados().add(dept)
                                        );
                                    }
                                }

                                Evento eventoActualizado = eventoService.actualizarEvento(evento);
                                if (eventoActualizado != null) {
                                    mensajeServer.addArg("actualizado");
                                    System.out.println("✅ Evento actualizado: " + tituloEvento);
                                } else {
                                    mensajeServer.addArg("error");
                                }
                            } else {
                                mensajeServer.addArg("no_encontrado");
                            }
                        } catch (Exception e) {
                            mensajeServer.addArg("error");
                            System.err.println("❌ Error al actualizar evento: " + e.getMessage());
                            e.printStackTrace();
                        }
                        enviar(mensajeServer);
                        break;



                }
            }
        } catch (java.net.SocketException e) {
            server.cerrarSesion(usuarioId);
        } catch (EOFException eOFException) {
            System.err.println("Se ha cerrado el flujo");
            server.cerrarSesion(usuarioId);
        } catch(IOException iOException){
            System.err.println("ioexception");
            server.cerrarSesion(usuarioId);
        }
        System.out.println("usuario handler cerrado");
    }
                        
    private void enviar(Mensaje msg) {

        try {
            writer.writeUTF(Serializador.codificarMensaje(msg));
            writer.flush(); 
        } catch (IOException iOException) {
            System.out.println("Error en el IO");
        }
    }

    private boolean comprobarExistenciaCif(String cif){
        return MakeAbstractRequest.makeAbstractRequest(cif);
    }

    /**
     * Parsea una cadena de fecha que puede venir en formato ISO DateTime (2025-10-26T00:00:00)
     * o solo fecha (2025-10-26) y retorna un LocalDate.
     */
    private java.time.LocalDate parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) {
            return null;
        }

        // Si contiene 'T', es formato ISO DateTime - extraer solo la fecha
        if (fechaStr.contains("T")) {
            return java.time.LocalDate.parse(fechaStr.substring(0, 10));
        }

        // Caso contrario, parsear directamente como fecha
        return java.time.LocalDate.parse(fechaStr);
    }


}
