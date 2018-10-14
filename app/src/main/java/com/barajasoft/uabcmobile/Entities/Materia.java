package com.barajasoft.uabcmobile.Entities;

public class Materia {
    String nombreClase;
    String hora;
    String profesor;
    String grupo;
    String subGrupo;
    String salon;
    String etapa;
    String tipoClase;

    public Materia(){}

    public Materia(String clase, String h, String prof, String gru, String sGru, String sal, String eta, String tClase){
        nombreClase = clase;
        hora = h;
        profesor = prof;
        grupo = gru;
        subGrupo = sGru;
        salon = sal;
        etapa = eta;
        tipoClase = tClase;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(String subGrupo) {
        this.subGrupo = subGrupo;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }
}
