package com.iesfernandoaguilar.solsonafuentes.util;

public class Serializador {

    public static String codificarMensaje(Mensaje mensaje){
        return mensaje.getTipo() + ";" + String.join(";", mensaje.getArgs());
    }

    public static Mensaje decodificarMensaje(String linea){
        Mensaje mensaje = new Mensaje();

        String[] palabrasLinea = linea.split(";");

        mensaje.setTipo(palabrasLinea[0]);

        if(palabrasLinea.length > 1){
            for (int i = 1; i < palabrasLinea.length; i++) {
                mensaje.addArg(palabrasLinea[i]);
            }
        }

        return mensaje;
    }
}
