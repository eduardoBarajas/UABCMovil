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
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.CalificacionProfesor;
import com.uabcmovil.uabcmovil.Entities.Comentario;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class CalificacionesFireBaseConnection extends Observable {
    private List<CalificacionProfesor> calificaciones = new LinkedList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("CalificacionesProfesores");
    private GenericTypeIndicator<List<Map<String,String>>> listType = new GenericTypeIndicator<List<Map<String,String>>>() {};


    private static CalificacionesFireBaseConnection ourInstance = null;

    public static CalificacionesFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new CalificacionesFireBaseConnection();
        return ourInstance;
    }

    private CalificacionesFireBaseConnection() {
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CalificacionProfesor calificacion = new CalificacionProfesor();
                calificacion.setKey(dataSnapshot.getKey());
                calificacion.setCalificacion(dataSnapshot.child("calificacion").getValue(String.class));
                calificacion.setKeyProfesor(dataSnapshot.child("keyProfesor").getValue(String.class));
                calificacion.setKeysAlumnos(dataSnapshot.child("keysAlumnos").getValue(listType));
                calificaciones.add(calificacion);
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CalificacionProfesor calificacionMod = new CalificacionProfesor();
                calificacionMod.setCalificacion(dataSnapshot.child("calificacion").getValue(String.class));
                calificacionMod.setKey(dataSnapshot.getKey());
                calificacionMod.setKeyProfesor(dataSnapshot.child("keyProfesor").getValue(String.class));
                calificacionMod.setKeysAlumnos(dataSnapshot.child("keysAlumnos").getValue(listType));
                for(int i = 0; i < calificaciones.size(); i++){
                    if(calificacionMod.getKey().equals(calificaciones.get(i).getKey())){
                        calificaciones.get(i).setCalificacion(calificacionMod.getCalificacion());
                        calificaciones.get(i).setKeysAlumnos(calificacionMod.getKeysAlumnos());
                    }
                }
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                CalificacionProfesor eliminar = null;
                for(CalificacionProfesor com : calificaciones){
                    if(com.getKey().equals(dataSnapshot.getKey())){
                        eliminar = com;
                    }
                }
                if(eliminar!=null)
                    calificaciones.remove(eliminar);
                setChanged();
                notifyObservers("Se Elimino");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public boolean isCalificado(String alumnoKey,String profesorKey){
        for(CalificacionProfesor calificacion : calificaciones){
            if(profesorKey.equals(calificacion.getKeyProfesor()))
                if(calificacion.getKeysAlumnos()!=null){
                    for(int i=0;i<calificacion.getKeysAlumnos().size();i++){
                        for(String key : calificacion.getKeysAlumnos().get(i).keySet()){
                            if(key.equals(alumnoKey)){
                                return true;
                            }
                        }
                    }
                }
        }
        return false;
    }

    public String getCalificacionDadaPorUsuario(String profesorKey,String alumnoKey){
        for(CalificacionProfesor calificacion : calificaciones){
            if(profesorKey.equals(calificacion.getKeyProfesor()))
                if(calificacion.getKeysAlumnos()!=null)
                    for(int i=0;i<calificacion.getKeysAlumnos().size();i++){
                        for(String key : calificacion.getKeysAlumnos().get(i).keySet()){
                            if(key.equals(alumnoKey)){
                                return calificacion.getKeysAlumnos().get(i).get(alumnoKey);
                            }
                        }
                    }
        }
        return "0";
    }

    public int getCalificacionDadaPorUsuarioIndex(String profesorKey,String alumnoKey){
        for(CalificacionProfesor calificacion : calificaciones){
            if(profesorKey.equals(calificacion.getKeyProfesor()))
                if(calificacion.getKeysAlumnos()!=null)
                    for(int i=0;i<calificacion.getKeysAlumnos().size();i++){
                        for(String key : calificacion.getKeysAlumnos().get(i).keySet()){
                            if(key.equals(alumnoKey)){
                                return i;
                            }
                        }
                    }
        }
        return -1;
    }

    public CalificacionProfesor getCalificacion(String keyProfesor){
        for(CalificacionProfesor calificacion : calificaciones){
            if(calificacion.getKeyProfesor().equals(keyProfesor))
                return calificacion;
        }
        return null;
    }

    public String getCalificacionFromProfesor(String profesorKey){
        for(CalificacionProfesor calificacion : calificaciones){
            if(calificacion.getKeyProfesor().equals(profesorKey)){
                return calificacion.getCalificacion();
            }
        }
        return "0";
    }
}
