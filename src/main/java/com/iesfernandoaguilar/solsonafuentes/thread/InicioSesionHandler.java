package com.iesfernandoaguilar.solsonafuentes.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iesfernandoaguilar.solsonafuentes.Servidor;
import com.iesfernandoaguilar.solsonafuentes.dto.GrupoDTO;
import com.iesfernandoaguilar.solsonafuentes.dto.UsuarioDTO;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.service.GrupoService;
import com.iesfernandoaguilar.solsonafuentes.service.UsuarioService;
import com.iesfernandoaguilar.solsonafuentes.util.Mensaje;
import com.iesfernandoaguilar.solsonafuentes.util.Serializador;

public class InicioSesionHandler implements Runnable{

    private Socket socket;
    private Servidor server;
    private ApplicationContext context;
    private DataInputStream reader;
    private DataOutputStream writer;
    private boolean iniciarSesion;
    private UsuarioService usuarioService;
    private GrupoService grupoService;

    public InicioSesionHandler(Socket socket,Servidor server,ApplicationContext context) {
        this.socket = socket;
        this.server = server;
        this.context = context;
        reader = null;
        writer = null;
        iniciarSesion = false;
        usuarioService = context.getBean(UsuarioService.class);
        grupoService = context.getBean(GrupoService.class);
    }

    @Override
    public void run() {
        Optional<Usuario> usuario = null;
        Optional<Usuario> usuarioOpt = null;
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("inicia sesion handler");

        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            while (!iniciarSesion && !socket.isClosed()) {
                String linea = reader.readUTF();
                System.out.println(linea);
                
                Mensaje mensajeUser = Serializador.decodificarMensaje(linea);
                Mensaje mensajeServer = new Mensaje();
                
                switch(mensajeUser.getTipo()) {
                    case "VALIDAR_SESION":
                        Long idUsuario = Long.valueOf(mensajeUser.getArgs().get(0));
                        usuarioOpt = usuarioService.findByIdUsuario(idUsuario);

                        if (usuarioOpt.isPresent()) {
                            Usuario usuarioValidado = usuarioOpt.get();
                            
                            mensajeServer.setTipo("SESION_ACTIVA");

                            ObjectMapper localMapper = new ObjectMapper();
                            localMapper.setSerializationInclusion(Include.NON_NULL);
                            localMapper.registerModule(new JavaTimeModule());
                            localMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                            UsuarioDTO usuarioDTO = new UsuarioDTO();
                            usuarioDTO.parse(usuarioValidado);
                            usuarioDTO.setContraseniaHasheada("");
                            usuarioDTO.setSalt("");
                            
                            try {
                                mensajeServer.addArg(localMapper.writeValueAsString(usuarioDTO));

                                if(usuarioValidado.getGrupo() != null) {
                                    String jsonGrupo = obtenerGrupo(usuarioValidado.getGrupo().getIdGrupo());
                                    mensajeServer.addArg(jsonGrupo);
                                } else {
                                    mensajeServer.addArg(null);
                                }
                            } catch (JsonProcessingException e) {
                                System.err.println("Error al procesar JSON para validación de sesión.");
                                mensajeServer.setTipo("SESION_INVALIDA");
                                mensajeServer.getArgs().clear();
                                enviar(mensajeServer);
                                break;
                            }

                            if (!server.isSesionActiva(idUsuario)) {
                                server.iniciarSesion(idUsuario, context, socket);
                            }
                            
                            enviar(mensajeServer);

                            if (!server.isSesionActiva(idUsuario)) {
                                return; 
                            }

                        } else {
                            mensajeServer.setTipo("SESION_INVALIDA");
                            enviar(mensajeServer);
                        }
                        break;
                    case "COMPROBAR_NOMBRE_Y_CORREO":
                        mensajeServer.setTipo("USUARIO_EXISTE");

                        Optional<Usuario> usuarioNombre = usuarioService.findByNombre(mensajeUser.getArgs().get(0));
                        Optional<Usuario> usuarioCorreo = usuarioService.findByEmail(mensajeUser.getArgs().get(1));

                        if(usuarioNombre.isPresent() || usuarioCorreo.isPresent()) {
                            mensajeServer.addArg("existe");
                        } else {
                            mensajeServer.addArg("no_existe");
                        }
                        enviar(mensajeServer);
                        break;
                    case "OBTENER_SALT":
                        mensajeServer.setTipo("ENVIAR_SALT");
                        
                        usuario = usuarioService.login(mensajeUser.getArgs().get(0));

                        if(usuario.isPresent()) {
                            mensajeServer.addArg("salt");
                            mensajeServer.addArg(usuario.get().getSalt());
                        } else {
                            mensajeServer.addArg("no_salt");
                        }
                        
                        enviar(mensajeServer);
                        writer.flush();
                        break;

                    case "CREAR_CUENTA":
                        try {
                            mapper = new ObjectMapper();
                            UsuarioDTO usuarioMapped = mapper.readValue(mensajeUser.getArgs().get(0), UsuarioDTO.class);

                            Usuario usuarioCreado = new Usuario(
                                usuarioMapped.getNombre(),
                                usuarioMapped.getEmail(),
                                usuarioMapped.getContraseniaHasheada(),
                                usuarioMapped.getSalt()
                            );

                            usuarioService.save(usuarioCreado);                        
                        } catch(JsonProcessingException jPException) {
                            System.out.println("Error en json");
                        }
                        break;

                    case "INICIAR_SESION":
                        mensajeServer.setTipo("INICIO_SESION_RESPUESTA");

                        usuarioOpt = usuarioService.login(mensajeUser.getArgs().get(0));
                        String json = "";

                        if(usuarioOpt.isPresent()) {
                            Usuario usuarioLogeado = usuarioOpt.get();

                            if(usuarioLogeado.getContraseniaHasheada().equals(mensajeUser.getArgs().get(1))) {
                                mensajeServer.addArg("exito");

                                mapper = new ObjectMapper();
                                UsuarioDTO usuarioDTO = new UsuarioDTO();
                                usuarioDTO.parse(usuarioLogeado);
                                usuarioDTO.setContraseniaHasheada("");
                                usuarioDTO.setSalt("");
                                mensajeServer.addArg(mapper.writeValueAsString(usuarioDTO));

                                if(usuarioLogeado.getGrupo() != null) {
                                    json = obtenerGrupo(usuarioLogeado.getGrupo().getIdGrupo());
                                    mensajeServer.addArg(json);
                                }

                                usuario = usuarioOpt;
                                iniciarSesion = true;

                            } else {
                                mensajeServer.addArg("contrasenia_incorrecta");
                            }
                        } else {
                            mensajeServer.addArg("no_existe");
                        }

                        enviar(mensajeServer);
                        writer.flush();
                        break;  
                }
            }
        } catch (EOFException eOFException) {
            System.err.println("Se ha cerrado el flujo");
        } catch(IOException iOException){
            System.err.println("ioexception");
        }
        System.out.println("acaba inicia sesion handler");

        if(iniciarSesion){
            server.iniciarSesion(usuario.get().getIdUsuario(), context,socket);
        }
    }
                        
    private void enviar(Mensaje msg) {

        try {
            writer.writeUTF(Serializador.codificarMensaje(msg));
            writer.flush(); 
        } catch (IOException iOException) {
            System.out.println("Error en el IO");
        }
    }

    private String obtenerGrupo(Long grupoId) {
        String jsonGrupo = "";
       

        try{

            Optional<Grupo> grupoOpt = grupoService.findByIdGrupo(grupoId);

            if(grupoOpt.isPresent()) {
                Grupo grupo = grupoOpt.get();
                GrupoDTO grupoDto = new GrupoDTO();
                grupoDto.parse(grupo);

                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Include.NON_NULL);
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                jsonGrupo = mapper.writeValueAsString(grupoDto);
            }

        } catch(JsonProcessingException jPException){
            System.err.println("Error en el json");
        }

        return jsonGrupo;

       
    }
}

 