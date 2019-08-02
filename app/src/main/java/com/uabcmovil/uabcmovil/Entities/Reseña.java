package com.uabcmovil.uabcmovil.Entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class Reseña {
    private String rol = "";
    private String nombre = "";
    private String reseña = "";
    private String key = UUID.randomUUID().toString().replaceAll("-","");
    private String keyUsuario = "";
    private String calificacion = "";
    private String fecha = "";
    public  Reseña(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        fecha = simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getReseña() {
        return reseña;
    }

    public void setReseña(String reseña) {
        this.reseña = reseña;
    }

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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getKeyUsuario() {
        return keyUsuario;
    }

    public void setKeyUsuario(String keyUsuario) {
        this.keyUsuario = keyUsuario;
    }
}
