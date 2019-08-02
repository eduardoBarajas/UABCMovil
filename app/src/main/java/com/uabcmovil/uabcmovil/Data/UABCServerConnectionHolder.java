package com.uabcmovil.uabcmovil.Data;
import org.jsoup.Connection;

public class UABCServerConnectionHolder {

    private static UABCServerConnectionHolder instance = null;
    Connection.Response conexion;
    private UABCServerConnectionHolder(Connection.Response con){
        conexion = con;
    }

    public static UABCServerConnectionHolder getInstance() {
        return instance;
    }

    public static UABCServerConnectionHolder getInstance(Connection.Response con) {
        if (instance == null)
            instance = new UABCServerConnectionHolder(con);
        return instance;
    }

    public Connection.Response getConexion() {
        return conexion;
    }

    public void restartConnection(){instance = null;}
}
