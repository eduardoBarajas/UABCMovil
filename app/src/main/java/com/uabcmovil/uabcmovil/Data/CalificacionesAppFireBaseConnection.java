package com.uabcmovil.uabcmovil.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.uabcmovil.uabcmovil.Entities.CalificacionApp;
import com.uabcmovil.uabcmovil.Entities.CalificacionProfesor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class CalificacionesAppFireBaseConnection extends Observable {
    private CalificacionApp calificacion = new CalificacionApp();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("CalificacionesApp");
    private GenericTypeIndicator<List<Map<String,String>>> listType = new GenericTypeIndicator<List<Map<String,String>>>() {};


    private static CalificacionesAppFireBaseConnection ourInstance = null;

    public static CalificacionesAppFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new CalificacionesAppFireBaseConnection();
        return ourInstance;
    }

    private CalificacionesAppFireBaseConnection() {
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                calificacion.setKey(dataSnapshot.getKey());
                calificacion.setCalificacion(dataSnapshot.child("calificacion").getValue(String.class));
                calificacion.setKeysUsuarios(dataSnapshot.child("keysUsuarios").getValue(listType));
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                calificacion.setCalificacion(dataSnapshot.child("calificacion").getValue(String.class));
                calificacion.setKey(dataSnapshot.getKey());
                calificacion.setKeysUsuarios(dataSnapshot.child("keysUsuarios").getValue(listType));
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public boolean isCalificado(String alumnoKey){
            if (calificacion.getKeysUsuarios() != null) {
                for (int i = 0; i < calificacion.getKeysUsuarios().size(); i++) {
                    for (String key : calificacion.getKeysUsuarios().get(i).keySet()) {
                        if (key.equals(alumnoKey)) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    public String getCalificacionDadaPorUsuario(String alumnoKey){
            if(calificacion.getKeysUsuarios()!=null)
                for(int i=0;i<calificacion.getKeysUsuarios().size();i++){
                    for(String key : calificacion.getKeysUsuarios().get(i).keySet()){
                        if(key.equals(alumnoKey)){
                            return calificacion.getKeysUsuarios().get(i).get(alumnoKey);
                        }
                    }
            }
        return "0";
    }

    public int getCalificacionDadaPorUsuarioIndex(String alumnoKey){
                if(calificacion.getKeysUsuarios()!=null)
                    for(int i=0;i<calificacion.getKeysUsuarios().size();i++){
                        for(String key : calificacion.getKeysUsuarios().get(i).keySet()){
                            if(key.equals(alumnoKey)){
                                return i;
                            }
                        }
                    }
        return -1;
    }

    public CalificacionApp getCalificacion(){
        return calificacion;
    }

    public void deleteCalificacionPorUsuario(String keyCalificacion, String keyUsuario) {
        int indexEliminar = -1;
        calificacion.setCalificacion("0");
        for(Map<String,String> entradasCalificaciones : calificacion.getKeysUsuarios()){
            for(String key : entradasCalificaciones.keySet()){
                if(key.equals(keyUsuario)){
                    indexEliminar = calificacion.getKeysUsuarios().indexOf(entradasCalificaciones);
                    break;
                }
                calificacion.setCalificacion(String.valueOf(Float.parseFloat(calificacion.getCalificacion())+Float.parseFloat(entradasCalificaciones.get(key))));
            }
        }
        if(indexEliminar!=-1){
          calificacion.getKeysUsuarios().remove(indexEliminar);
          calificacion.setCalificacion(String.valueOf(Float.parseFloat(calificacion.getCalificacion())/calificacion.getKeysUsuarios().size()));
          Map<String,Object> dato = new HashMap<>();
          dato.put("keysUsuarios",calificacion.getKeysUsuarios());
          dato.put("calificacion",calificacion.getCalificacion());
          referencia.child(keyCalificacion).updateChildren(dato);
        }
    }
}
