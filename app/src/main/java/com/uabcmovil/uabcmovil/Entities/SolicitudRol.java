package com.uabcmovil.uabcmovil.Entities;

import java.util.UUID;

public class SolicitudRol {
    private String nombre = "";
    private String correo = "";
    private String keyUsuario = "";
    private String rol_solicitdado = "";
    private String keySolicitud = UUID.randomUUID().toString().replaceAll("-","");

    public SolicitudRol(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getKeyUsuario() {
        return keyUsuario;
    }

    public void setKeyUsuario(String key) {
        this.keyUsuario = key;
    }

    public String getRol_solicitdado() {
        return rol_solicitdado;
    }

    public void setRol_solicitdado(String rol_solicitdado) {
        this.rol_solicitdado = rol_solicitdado;
    }

    public String getKeySolicitud() {
        return keySolicitud;
    }

    public void setKeySolicitud(String keySolicitud) {
        this.keySolicitud = keySolicitud;
    }
}
