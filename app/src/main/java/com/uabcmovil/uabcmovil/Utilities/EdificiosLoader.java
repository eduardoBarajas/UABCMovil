package com.uabcmovil.uabcmovil.Utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class EdificiosLoader { private StringBuilder stringBuilder;
    private List<List<List<LatLng>>> lista_edificios = new LinkedList<>();
    private List<LatLng> centro_edificios = new LinkedList<>();
    private List<String> iconos = new LinkedList<>();
    private Context c;
    private List<String> keys = new LinkedList<>();

    public EdificiosLoader(Context context){
        c = context;
        stringBuilder = new StringBuilder();
    }

    public void loadJson(){
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(c.getAssets().open("edificios.json")));
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            bufferedReader.close();
            Log.e("LEIDO",stringBuilder.toString());
        }catch (Exception ex){
            Log.e("Error de lectrua",ex.getMessage());
        }
    }

    public void parseJson(){
        try {
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONArray edificiosKeys = jsonObject.getJSONArray("EdificiosKeys");
                for (int i = 0; i < edificiosKeys.length(); i++) {
                    keys.add(edificiosKeys.get(i).toString());
                }
                JSONArray centroEdificios = jsonObject.getJSONArray("CentroEdificios");
                for(int i = 0; i <  centroEdificios.length(); i++){
                    String[] coordenadas = centroEdificios.get(i).toString().split(",");
                    centro_edificios.add(new LatLng(Double.parseDouble(coordenadas[0]),Double.parseDouble(coordenadas[1])));
                }
                JSONArray icons = jsonObject.getJSONArray("Iconos");
                for(int i = 0; i <  icons.length(); i++){
                    iconos.add(icons.get(i).toString());
                }
                JSONArray edificios = jsonObject.getJSONArray("Edificios");
                for (int i = 0; i < edificios.length(); i++) {
                    List<List<LatLng>> edificio = new LinkedList<>();
                    List<LatLng> e = new LinkedList<>();
                    for(int k = 0 ; k < edificios.getJSONObject(i).getJSONArray(keys.get(i)).length(); k++){
                        String[] coordenadas = edificios.getJSONObject(i).getJSONArray(keys.get(i)).get(k).toString().split(",");
                        e.add(new LatLng(Double.parseDouble(coordenadas[0]),Double.parseDouble(coordenadas[1])));
                    }
                    edificio.add(e);
                    lista_edificios.add(edificio);
                }
            } catch (JSONException e) {
                Log.e("JSONError ", e.getMessage());
            }
        }

        public List<List<List<LatLng>>> getEdificios(){
            return lista_edificios;
        }
        public List<String> getEdificiosKeys(){return keys;}

        public List<LatLng> getCentro_edificios(){return centro_edificios;}
        public List<String> getIconos(){return iconos;}
}
