package com.iesfernandoaguilar.solsonafuentes.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iesfernandoaguilar.solsonafuentes.Servidor;
import com.iesfernandoaguilar.solsonafuentes.dto.GrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.NotificacionDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.SubgrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.enums.EstadoNotificacion;
import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Notificacion;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
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

        String nombreEmpresa = "";
        String vatEmpresa = "";
        UsuarioDTO usuarioDTO = null;
        GrupoDTO grupoDto = null;
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
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

                        Subgrupo subgrupo = new Subgrupo("Grupo 1",grupo.getCreadoPor());
                        grupo.addSubgrupo(subgrupo);

                        Optional<Grupo> grupoCreado = Optional.ofNullable(grupoService.save(grupo));

                        if(grupoCreado.isPresent()) {
                            mensajeServer.addArg("creado");
                        } else {
                            mensajeServer.addArg("no_creado");
                        }

                        Optional<Usuario> usuarioOpt = usuarioService.findByIdUsuario(usuarioDTO.getIdUsuario());
                        if(usuarioOpt.isPresent()){
                           usuarioOpt.get().setGrupo(grupoCreado.get()); 
                           usuarioOpt.get().setRol(Rol.ADMINISTRADOR);
                           usuarioService.save(usuarioOpt.get()); 
                        }
                       
                        
                        enviar(mensajeServer);
                        break;

                    case "OBTENER_SUBGRUPOS":
                        mensajeServer.setTipo("DAR_SUBGRUPOS");

                        Long idGrupo = Long.valueOf(mensajeUser.getArgs().get(0));
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
