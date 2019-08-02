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
import com.uabcmovil.uabcmovil.Entities.Solicitud;
import com.uabcmovil.uabcmovil.Entities.SolicitudRol;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class SolicitudesRolFireBaseConnection extends Observable {
    private List<SolicitudRol> solicitudes = new LinkedList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("SolicitudesAsignacionesRoles");
    private static Context context;
    private boolean firstRun = true;

    private static SolicitudesRolFireBaseConnection ourInstance = null;

    public static SolicitudesRolFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new SolicitudesRolFireBaseConnection();
        return ourInstance;
    }

    public static void StartInstance(Context context){
        SolicitudesRolFireBaseConnection.context = context;
        ourInstance = new SolicitudesRolFireBaseConnection();
    }

    private SolicitudesRolFireBaseConnection() {
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
                solicitudes.add(dataSnapshot.getValue(SolicitudRol.class));
                if(!firstRun && Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
                    notificarNuevaSolicitud("Llego una nueva solicitud para asignacion de rol",Sesion.getInstance().getAlumno().getRol());
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                SolicitudRol eliminar = null;
                for(SolicitudRol sol : solicitudes){
                    if(sol.getKeySolicitud().equals(dataSnapshot.getValue(SolicitudRol.class).getKeySolicitud())){
                        eliminar = sol;
                    }
                }
                if(eliminar!=null)
                    solicitudes.remove(eliminar);
                if(!firstRun && eliminar.getKeyUsuario().equals(Sesion.getInstance().getAlumno().getKey())){
                    notificarNuevaSolicitud("Se te ha asignado el rol que solicitaste",Sesion.getInstance().getAlumno().getRol());
                }
                setChanged();
                notifyObservers("Se Modifico");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<SolicitudRol> getSolicitudes(){return solicitudes;}

    public boolean isAlreadySolicited(String correo){
        for(SolicitudRol sol : solicitudes){
            if(sol.getCorreo().equals(correo)){
                return true;
            }
        }
        return false;
    }

    private void notificarNuevaSolicitud(String titulo, String rol) {
        Intent notificacion = new Intent();
        notificacion.putExtra("tipo","Solicitud De Rol");
        notificacion.putExtra("titulo",titulo);
        String actividad = "";
        switch (rol){
            case "Adminstrador":{
                actividad = "ControlUsuarios";
                break;
            }
            case "Estudiante":{
                actividad = "NewsFeed";
                break;
            }
            case "Profesor":{
                actividad = "Solicitudes";
                break;
            }
            case "AdminLab":{
                actividad = "Solicitudes";
                break;
            }
        }
        notificacion.putExtra("actividad",actividad);
        notificacion.setAction("android.intent.action.NUEVA_NOTIFICACION");
        SolicitudesRolFireBaseConnection.context.sendBroadcast(notificacion);
    }

}
