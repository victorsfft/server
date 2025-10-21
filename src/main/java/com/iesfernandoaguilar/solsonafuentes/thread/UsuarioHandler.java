package com.iesfernandoaguilar.solsonafuentes.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
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
import com.iesfernandoaguilar.solsonafuentes.dto.DepartamentoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.GrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.NotificacionDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.SubgrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.TareaDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;
import com.iesfernandoaguilar.solsonafuentes.model.Comentario;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Tarea;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.service.ComentarioService;
import com.iesfernandoaguilar.solsonafuentes.service.DepartamentoService;
import com.iesfernandoaguilar.solsonafuentes.service.GrupoService;
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

    public UsuarioHandler(Socket socket,ApplicationContext context,Servidor server) {
        this.socket = socket;
        this.server = server;
        this.context = context;
        reader = null;
        writer = null;
        cierraSesion = false;
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

                            notificacionService.save(notificacion);

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

                        Usuario empActualizado = usuarioService.actualizarEmpleado(idUsuarioAct, nuevoRol, nuevoDeptId);

                        if (empActualizado != null) {
                            mensajeServer.addArg("actualizado");
                            System.out.println("✅ Empleado actualizado");
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

                    case "CREAR_TAREA":
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

                            mapper.registerModule(new JavaTimeModule());
                            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            mapper.setSerializationInclusion(Include.NON_NULL);

                            json = mapper.writeValueAsString(comentarios);
                            mensajeServer.addArg(json);
                            System.out.println("✅ Comentarios de tarea obtenidos: " + comentarios.size());
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

                }
            }
        } catch (EOFException eOFException) {
            System.err.println("Se ha cerrado el flujo");
        } catch(IOException iOException){
            System.err.println("ioexception");
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

 
    
}
