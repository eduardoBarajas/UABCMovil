package com.uabcmovil.uabcmovil.Entities;

import java.util.UUID;

public class Comentario {
    private String keyProfesor = "";
    private String keyAlumno = "";
    private String nombreAlumno = "";
    private String comentario = "";
    private String fechaComentario = "";
    private String keyComentario = UUID.randomUUID().toString().replaceAll("-","");

    public Comentario(){}

    public String getKeyProfesor() {
        return keyProfesor;
    }

    public void setKeyProfesor(String keyProfesor) {
        this.keyProfesor = keyProfesor;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public String getKeyAlumno() {
        return keyAlumno;
    }

    public void setKeyAlumno(String keyAlumno) {
        this.keyAlumno = keyAlumno;
    }

    public String getKeyComentario() {
        return keyComentario;
    }

    public void setKeyComentario(String keyComentario) {
        this.keyComentario = keyComentario;
    }
}
