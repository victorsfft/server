package com.iesfernandoaguilar.solsonafuentes.util;

import java.util.ArrayList;
import java.util.List;

public class Mensaje {

    private String tipo;
    private List<String> args;

    public Mensaje() {
        this.tipo = "";
        this.args = new ArrayList<>();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getArgs() {
        return args;
    }

    public void addArg(String parametro) {
        args.add(parametro);
    }

    public void clearArgs(){
        args.clear();
    }
}
