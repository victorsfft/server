package com.iesfernandoaguilar.solsonafuentes;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.iesfernandoaguilar.solsonafuentes.thread.InicioSesionHandler;
import com.iesfernandoaguilar.solsonafuentes.thread.UsuarioHandler;

@SpringBootApplication
public class Servidor {

    private final String PROPERTIES = "src/main/resources/conf.properties";
   
    private ServerSocket server;
    private int port;
    private ExecutorService executor;
    private ExecutorService handlersExecutor;
    private boolean stop;
    private ConcurrentHashMap<Long, UsuarioHandler> usuariosHandlers;


    public Servidor() {
        Properties props = new Properties();
        stop = false;
        try {
            props.load(new FileInputStream(PROPERTIES));

            port = Integer.parseInt(props.getProperty("PORT"));
            server = new ServerSocket(port);
            executor = Executors.newCachedThreadPool();
            handlersExecutor = Executors.newCachedThreadPool();
            usuariosHandlers = new ConcurrentHashMap<>();


        } catch (IOException e) {
            System.err.println("Error initializing the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //se inicia segundo e inicia el hilo de escuchar servidor y el scanner
    @Bean
    CommandLineRunner startServer(Servidor servidor,ApplicationContext context) {
        System.out.println("Arrancando server");
        return args -> {
            new Thread(() -> servidor.listen(context)).start();
            new Thread(servidor::listenScanner).start();
        };
    }

    private void listenScanner(){ 
        Scanner sc = new Scanner(System.in);
        while(true){
            if(sc.nextLine().equalsIgnoreCase("apagar")){
                sc.close();
                stop();
                break;
            }
        }
    }

    private void listen(ApplicationContext context) {
        while (!stop) { 
            try {
                Socket socket = server.accept();
                executor.execute(new InicioSesionHandler(socket,this,context));
            }catch(SocketException e){
                System.out.println("Error en el socket");
            } catch (IOException e) {
                System.out.println("Error en el IO");
            } catch(Exception e){
                System.out.println("Error generico");
            } 
        }
    }

    private void stop(){
        System.out.println("Apagando servidor");
        stop = true;
        try{

            if (server != null && !server.isClosed()) {
                server.close();
            }

            executor.shutdown();
            executor.close();
        } catch(IOException ioE){
            System.out.println("Error en el IO");
        }
    }

    public void iniciarSesion(Long usuarioId, ApplicationContext context, Socket socket){
        if (usuariosHandlers.containsKey(usuarioId)) {
            System.out.println("Usuario ya conectado. Cerrando sesión anterior.");
            usuariosHandlers.get(usuarioId).cerrarConexion();
            usuariosHandlers.remove(usuarioId);
        }
        UsuarioHandler uHandler = new UsuarioHandler(socket, context, this, usuarioId);
        usuariosHandlers.put(usuarioId, uHandler);
        handlersExecutor.execute(uHandler);
    }

    public void cerrarSesion(Long usuarioId) {
        UsuarioHandler handler = usuariosHandlers.remove(usuarioId);
        if (handler != null) {
            handler.cerrarConexion();
            System.out.println("Usuario " + usuarioId + " desconectado.");
        }
    }

    public boolean isSesionActiva(Long usuarioId) {
        return usuariosHandlers.containsKey(usuarioId);
    }


    public static void main(String[] args) {
        SpringApplication.run(Servidor.class, args);
    }


}

