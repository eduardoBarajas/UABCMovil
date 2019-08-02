package com.uabcmovil.uabcmovil.Entities;

import android.net.Uri;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Alumno {
    String user = "None";
    String nombre = "No definido";
    String matricula = "No definido";
    String carrera = "No definido";
    String key = "None";
    List<Materia> clases = new LinkedList<>();
    String rol = "No definido";
    String profilePic = "";

    public Alumno(){

    }
    public Alumno(String correo, String nombre,Uri img){
        this.user = correo;
        this.nombre = nombre;
        profilePic = img.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<Materia> getClases() {
        return clases;
    }

    public void setClases(List<Materia> clases) {
        this.clases = clases;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}

