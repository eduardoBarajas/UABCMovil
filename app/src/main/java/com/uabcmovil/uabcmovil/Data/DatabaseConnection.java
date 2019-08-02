package com.uabcmovil.uabcmovil.Data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uabcmovil.uabcmovil.Entities.Alumno;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DatabaseConnection extends AsyncTask {

    //0 es GET y 1 es POST
    private int byGetOrPost = 1;
    private Context context;
    private String action;
    private Object objeto;
    private boolean isList;
    private boolean readOperation;
    private String data = "";
    private StringBuilder sb;
    public DatabaseConnection(Context context, String action, Object obj){
        this.context = context;
        this.action = action;
        this.objeto = obj;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            switch (action) {
                case "addUser": {
                        //en el objeto puede ir el usuario que quiero mandar a la bd

                        //en la cadena de data se pondra toda la informacion que se quiera mandar
                        //primero se manda la accion codificada en utf-8 seguido de "=" y despues el valor que tendra
                        data = URLEncoder.encode("addUser", "UTF-8") + "=" + URLEncoder.encode("addUser", "UTF-8");
                        //despues del primer campo se agrega un "&"
                        isList = false;
                        readOperation = false;
                        break;
                    }
                    case "getAllUsers": {
                        data = URLEncoder.encode(action, "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                        isList = true;
                        readOperation = true;
                        break;
                    }
                    case "CheckUserAvaible":{
                        String correo = (String) objeto;
                        data = URLEncoder.encode(action, "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                        data += "&" + URLEncoder.encode("Correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8");
                        isList = false;
                        readOperation = true;
                        break;
                    }
                    case "getUser":{
                        String correo = (String) objeto;
                        data = URLEncoder.encode(action, "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                        data += "&" + URLEncoder.encode("Correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8");
                        isList = false;
                        readOperation = true;
                        break;
                    }
                }
            }catch(Exception ex){
                Log.e("Error","error  en el data");
            }
            if(byGetOrPost == 1){
                //el link donde esta el archivo php que maneja la base de datos
                String link="http://laboratoriohidraulica.000webhostapp.com/assets/codigoBlog/bdCon.php";
                try {
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                    writer.write(data);
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    sb = new StringBuilder();
                    String line = null;

                    //leer la respuesta del servidor
                    while((line = reader.readLine()) != null){
                        Log.e("DATOS_LEIDOS",line);
                        sb.append(line);
                    }
                    //obtener la info del archivo json
                    if(isList&&readOperation){
                        //algo
                        parseListData();
                    }else{
                        if(readOperation){
                            parseData();
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error",e.getMessage());
                }
            }
            return null;
        }

    private Object parseListData() {
        switch (action) {
            case "getAllUsers": {
                ArrayList<Alumno> listaAlumnos = new ArrayList<>();
                String json = sb.toString();
                //aqui hacer algo para despues de que se hace el post
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray alumnos = jsonObject.getJSONArray("Alumnos");
                        for (int i = 0; i < alumnos.length(); i++) {
                            Alumno alum = new Alumno();
                            alum.setMatricula(alumnos.getJSONObject(i).getString("Matricula"));
                            listaAlumnos.add(alum);
                        }
                        return listaAlumnos;
                    } catch (JSONException e) {
                        Log.e("JSONError", e.getMessage());
                    }
                }
                break;
            }
        }
        return null;
    }

    private Object parseData() {
        String json = sb.toString();
        //aqui hacer algo para despues de que se hace el post
        if(json != null){
            try {
                JSONObject jsonObject = new JSONObject(json);
                switch (action){
                    case "getUser":{
                        Alumno alumno = new Alumno();
                        alumno.setCarrera(jsonObject.getString("Carrera"));

                        return alumno;
                    }
                }
            } catch (Exception e) {
                Log.e("JSONError",e.getMessage());
            }
        }
        return null;
    }
}
