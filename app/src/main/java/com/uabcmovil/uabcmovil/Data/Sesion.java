package com.uabcmovil.uabcmovil.Data;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Materia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Handler;

public class Sesion extends Observable{
    private Alumno alumno = null;
    private static Sesion instance = null;
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private ArrayList<Alumno> alumnos = new ArrayList<>();

    static public Sesion getInstance(){
        if(instance == null)
            instance = new Sesion();
        return instance;
    }

    private Sesion(){
        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setChanged();
                notifyObservers("usersLoaded");
                Log.e("Entro aqui","SE SUPONE QUE YA CARGO TODOS LOS USUARIOS "+alumnos.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        referencia.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!alumnos.contains(dataSnapshot.getValue(Alumno.class)))
                    alumnos.add(dataSnapshot.getValue(Alumno.class));
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i=0;i<alumnos.size();i++){
                    if(alumnos.get(i).getUser().equals(dataSnapshot.getValue(Alumno.class).getUser())){
                        alumnos.get(i).setMatricula(dataSnapshot.getValue(Alumno.class).getMatricula());
                        alumnos.get(i).setCarrera(dataSnapshot.getValue(Alumno.class).getCarrera());
                        alumnos.get(i).setNombre(dataSnapshot.getValue(Alumno.class).getNombre());
                        alumnos.get(i).setClases(dataSnapshot.getValue(Alumno.class).getClases());
                        alumnos.get(i).setRol(dataSnapshot.getValue(Alumno.class).getRol());
                        if(Sesion.getInstance().alumno.getUser().equals(dataSnapshot.getValue(Alumno.class).getUser())){
                            Sesion.instance.alumno = alumnos.get(i);
                            setChanged();
                            notifyObservers("UsuarioActualizado");
                        }
                        Log.e("Actualizado","key "+dataSnapshot.getKey());
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        alumno = new Alumno();
    }

    public void setAlumno(Alumno alum){
        boolean found = false;
        for(Alumno a : alumnos){
            if(alum.getUser().equals(a.getUser())){
                found = true;
                this.alumno = a;
                setChanged();
                notifyObservers("userLoaded");
                break;
            }
        }
        if(!found){
            setHorarioAlumno(alum);
            referencia.push().setValue(alum, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    Map<String,Object> dato = new HashMap<>();
                    dato.put("key",databaseReference.getKey());
                    referencia.child(databaseReference.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Sesion.this.alumno.setKey(databaseReference.getKey());
                            Sesion.this.alumno.setUser(alum.getUser());
                            Sesion.this.alumno.setNombre(alum.getNombre());
                            Sesion.this.alumno.setProfilePic(alum.getProfilePic());
                            setChanged();
                            notifyObservers("userLoaded");
                        }
                    });
                }
            });
        }
    }

    private void setHorarioAlumno(Alumno alumno) {
        List<Materia> materiaList = new LinkedList<>();
        Materia m = new Materia("Automatizacion","14:00-16:00","Murillo","471","1","104-E1","Terminal","Clase");
        materiaList.add(m);
        m = new Materia("Micros Avanzados","16:00-18:00","Hawa","471","1","108-E1","Terminal","Clase");
        materiaList.add(m);
        alumno.setClases(materiaList);
    }

    public Alumno getAlumno(){
        return alumno;
    }

    public void clearSesion(){alumno = new Alumno(); alumnos.clear(); instance = new Sesion();}
}
