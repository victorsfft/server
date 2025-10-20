package com.iesfernandoaguilar.solsonafuentes.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
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
import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.enums.TipoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.service.DepartamentoService;
import com.iesfernandoaguilar.solsonafuentes.service.GrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.NotificacionService;
import com.iesfernandoaguilar.solsonafuentes.service.SolicitudGrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.SubgrupoService;
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
