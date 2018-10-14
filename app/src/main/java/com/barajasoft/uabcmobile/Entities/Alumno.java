package com.barajasoft.uabcmobile.Entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Alumno {
    String user;
    List<Materia> clases = new LinkedList<>();

    public Alumno(){

    }

    public Alumno(String user){
        this.user = user;
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
}
