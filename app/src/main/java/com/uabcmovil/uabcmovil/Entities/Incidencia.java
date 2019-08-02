package com.uabcmovil.uabcmovil.Entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class Incidencia {
    private String edificio = "";
    private String profesor = "";
    private String laboratorio = "";
    private String id_equipo = "";
    private String incidencia = "";
    private String respuesta = "";
    private String estado = "";
    private String fecha = "";
    private String key = UUID.randomUUID().toString().replaceAll("-","");

    public Incidencia(){ };

    public Incidencia(String edif, String lab, String id, String incid, String res,String prof,String es){
        edificio = edif;
        laboratorio = lab;
        id_equipo = id;
        incidencia = incid;
        respuesta = res;
        profesor = prof;
        estado = es;
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

    public String getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(String id_equipo) {
        this.id_equipo = id_equipo;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(String incidencia) {
        this.incidencia = incidencia;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha){
        this.fecha = fecha;
    }

    public void setFecha() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        fecha = format.format(Calendar.getInstance().getTime());
    }
}
