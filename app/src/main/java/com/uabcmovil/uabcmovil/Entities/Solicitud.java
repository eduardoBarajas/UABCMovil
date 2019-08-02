package com.uabcmovil.uabcmovil.Entities;

import java.util.UUID;

public class Solicitud {
    private String nombre = "";
    private String materia = "";
    private String hora = "";
    private String fecha = "";
    private String edificio = "";
    private String laboratorio = "";
    private String comentarios = "";
    private String key = UUID.randomUUID().toString().replaceAll("-","");
    private String estado = "";
    private String rechazo = "";

    public Solicitud(){}

    public Solicitud(String nom,String mat, String time,String date,String edif,
                     String lab, String comm, String est){
        nombre = nom;
        materia = mat;
        hora = time;
        fecha = date;
        edificio = edif;
        laboratorio = lab;
        comentarios = comm;
        estado = est;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRechazo() {
        return rechazo;
    }

    public void setRechazo(String rechazo) {
        this.rechazo = rechazo;
    }
}
