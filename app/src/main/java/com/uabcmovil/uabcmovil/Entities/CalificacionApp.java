package com.uabcmovil.uabcmovil.Entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CalificacionApp {
    private String key = UUID.randomUUID().toString().replaceAll("-","");
    private String calificacion = "0";
    private List<Map<String,String>> keysUsuarios = new LinkedList<>();

    public CalificacionApp(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public List<Map<String, String>> getKeysUsuarios() {
        return keysUsuarios;
    }

    public void setKeysUsuarios(List<Map<String, String>> keysUsuarios) {
        this.keysUsuarios = keysUsuarios;
    }

    public void initializeAlumnosArray(){
        this.keysUsuarios = new LinkedList<>();
    }
}
