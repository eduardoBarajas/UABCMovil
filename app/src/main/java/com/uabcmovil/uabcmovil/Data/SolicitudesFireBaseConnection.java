package com.uabcmovil.uabcmovil.Data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.Solicitud;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class SolicitudesFireBaseConnection extends Observable {
    private List<Solicitud> solicitudes = new LinkedList<>();
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Solicitudes");
    private static Context context;
    private boolean firstRun = true;

    private static SolicitudesFireBaseConnection ourInstance = null;

    public static SolicitudesFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new SolicitudesFireBaseConnection();
        return ourInstance;
    }

    public static void StartInstance(Context context){
        SolicitudesFireBaseConnection.context = context;
        ourInstance = new SolicitudesFireBaseConnection();
    }

    private SolicitudesFireBaseConnection() {
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
                solicitudes.add(dataSnapshot.getValue(Solicitud.class));
                if(!firstRun && Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                    notificarNuevaSolicitud("Nueva Solicitud Del Lab "+dataSnapshot.getValue(Solicitud.class).getLaboratorio());
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Solicitud solicitudMod = dataSnapshot.getValue(Solicitud.class);
                for(int i = 0; i < solicitudes.size(); i++){
                    if(solicitudMod.getKey().equals(solicitudes.get(i).getKey())){
                        solicitudes.get(i).setComentarios(solicitudMod.getComentarios());
                        solicitudes.get(i).setEdificio(solicitudMod.getEdificio());
                        solicitudes.get(i).setFecha(solicitudMod.getFecha());
                        solicitudes.get(i).setHora(solicitudMod.getHora());
                        solicitudes.get(i).setLaboratorio(solicitudMod.getLaboratorio());
                        solicitudes.get(i).setMateria(solicitudMod.getMateria());
                        solicitudes.get(i).setNombre(solicitudMod.getNombre());
                        solicitudes.get(i).setEstado(solicitudMod.getEstado());
                        solicitudes.get(i).setRechazo(solicitudMod.getRechazo());
                    }
                }
                if(!firstRun && Sesion.getInstance().getAlumno().getRol().equals("Profesor")&&Sesion.getInstance().getAlumno().getNombre().equals(dataSnapshot.getValue(Solicitud.class).getNombre()))
                    if(dataSnapshot.getValue(Solicitud.class).getEstado().equals("Cancelada")){
                        notificarNuevaSolicitud("Tu solicitud ha sido Rechazada");
                    }else{
                        notificarNuevaSolicitud("Tu solicitud ha sido Aprovada");
                    }
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Solicitud eliminar = null;
                for(Solicitud sol : solicitudes){
                    if(sol.getKey().equals(dataSnapshot.getValue(Solicitud.class).getKey())){
                        eliminar = sol;
                    }
                }
                if(eliminar!=null)
                    solicitudes.remove(eliminar);
                setChanged();
                notifyObservers("Se Modifico");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void notificarNuevaSolicitud(String titulo) {
        Intent notificacion = new Intent();
        notificacion.putExtra("tipo","Solicitud Para Laboratorio");
        notificacion.putExtra("titulo",titulo);
        notificacion.putExtra("actividad","Solicitudes");
        notificacion.setAction("android.intent.action.NUEVA_NOTIFICACION");
        SolicitudesFireBaseConnection.context.sendBroadcast(notificacion);
    }

    public List<Solicitud> getAllSolicitudes(String state){
        List<Solicitud> returned = new LinkedList<>();
        for(Solicitud sol : solicitudes){
            if(sol.getEstado().equals(state)){
                returned.add(sol);
            }
        }
        return returned;
    }

    public List<Solicitud> getAllSolicitudes(String state,String name){
        List<Solicitud> returned = new LinkedList<>();
        for(Solicitud sol : solicitudes){
            if(sol.getEstado().equals(state)&&sol.getNombre().equals(name)){
                returned.add(sol);
            }
        }
        return returned;
    }
}
