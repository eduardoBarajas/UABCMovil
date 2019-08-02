package com.uabcmovil.uabcmovil.Entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CalificacionProfesor {
    private String key = UUID.randomUUID().toString().replaceAll("-","");
    private String keyProfesor = "";
    private String calificacion = "0";
    private List<Map<String,String>> keysAlumnos = new LinkedList<>();

    public CalificacionProfesor(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyProfesor() {
        return keyProfesor;
    }

    public void setKeyProfesor(String keyProfesor) {
        this.keyProfesor = keyProfesor;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public List<Map<String,String>> getKeysAlumnos() {
        return keysAlumnos;
    }

    public void setKeysAlumnos(List<Map<String,String>> keysAlumnos) {
        this.keysAlumnos = keysAlumnos;
    }

    public void initializeAlumnosArray(){
        this.keysAlumnos = new LinkedList<>();
    }
}
