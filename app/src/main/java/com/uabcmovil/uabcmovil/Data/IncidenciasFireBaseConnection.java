package com.uabcmovil.uabcmovil.Data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.Incidencia;
import com.uabcmovil.uabcmovil.Entities.Solicitud;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class IncidenciasFireBaseConnection extends Observable {
    private List<Incidencia> incidencias = new LinkedList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Incidencias");
    private static Context context;
    private boolean firstRun = true;

    private static IncidenciasFireBaseConnection ourInstance = null;

    public static IncidenciasFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new IncidenciasFireBaseConnection();
        return ourInstance;
    }

    public static void StartInstance(Context context){
        IncidenciasFireBaseConnection.context = context;
        ourInstance = new IncidenciasFireBaseConnection();
    }

    private IncidenciasFireBaseConnection() {
        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstRun = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                incidencias.add(dataSnapshot.getValue(Incidencia.class));
                if(!firstRun && Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                    notificarNuevaSolicitud("Nueva Incidencia Del Lab "+dataSnapshot.getValue(Solicitud.class).getLaboratorio());
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Incidencia incidenciaMod = dataSnapshot.getValue(Incidencia.class);
                for(int i = 0; i < incidencias.size(); i++){
                    if(incidenciaMod.getKey().equals(incidencias.get(i).getKey())){
                        incidencias.get(i).setIncidencia(incidenciaMod.getIncidencia());
                        incidencias.get(i).setEdificio(incidenciaMod.getEdificio());
                        incidencias.get(i).setFecha(incidenciaMod.getFecha());
                        incidencias.get(i).setProfesor(incidenciaMod.getProfesor());
                        incidencias.get(i).setLaboratorio(incidenciaMod.getLaboratorio());
                        incidencias.get(i).setId_equipo(incidenciaMod.getId_equipo());
                        incidencias.get(i).setRespuesta(incidenciaMod.getRespuesta());
                        incidencias.get(i).setEstado(incidenciaMod.getEstado());
                    }
                }
                if(!firstRun && Sesion.getInstance().getAlumno().getRol().equals("Profesor")&&Sesion.getInstance().getAlumno().getNombre().equals(dataSnapshot.getValue(Solicitud.class).getNombre()))
                    if(dataSnapshot.getValue(Solicitud.class).getEstado().equals("Atendida"))
                        notificarNuevaSolicitud("La incidencia que enviaste ha sido Atendida");
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Incidencia eliminar = null;
                for(Incidencia inc : incidencias){
                    if(inc.getKey().equals(dataSnapshot.getValue(Incidencia.class).getKey())){
                        eliminar = inc;
                    }
                }
                if(eliminar!=null)
                    incidencias.remove(eliminar);
                setChanged();
                notifyObservers("Se Modifico");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<Incidencia> getAllIncidencias(String state){
        List<Incidencia> returned = new LinkedList<>();
        for(Incidencia inc : incidencias){
            if(inc.getEstado().equals(state)){
                returned.add(inc);
            }
        }
        return returned;
    }

    public List<Incidencia> getAllIncidencias(String state,String name){
        List<Incidencia> returned = new LinkedList<>();
        for(Incidencia inc : incidencias){
            if(inc.getEstado().equals(state)&&inc.getProfesor().equals(name)){
                returned.add(inc);
            }
        }
        return returned;
    }

    private void notificarNuevaSolicitud(String titulo) {
        Intent notificacion = new Intent();
        notificacion.putExtra("tipo","Inicidencias de Laboratorios");
        notificacion.putExtra("titulo",titulo);
        notificacion.putExtra("actividad","Incidencias");
        notificacion.setAction("android.intent.action.NUEVA_NOTIFICACION");
        IncidenciasFireBaseConnection.context.sendBroadcast(notificacion);
    }
}
