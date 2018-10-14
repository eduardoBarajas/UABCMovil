package com.barajasoft.uabcmobile.Data;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.barajasoft.uabcmobile.Entities.Alumno;
import com.barajasoft.uabcmobile.Entities.Materia;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sesion {
    private Alumno alumno = null;
    private static Sesion instance = null;
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private ArrayList<Alumno> alumnos = new ArrayList<>();


    static public Sesion getInstance(){
        if(instance == null){
            instance = new Sesion();
        }
        return instance;
    }

    private Sesion(){
        referencia.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                alumnos.add(dataSnapshot.getValue(Alumno.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAlumno(Alumno alumno){
        boolean found = false;
        for(Alumno a : alumnos){
           if(alumno.getUser().equals(a.getUser())){
              found = true;
              alumno = a;
           }
        }
        if(!found){
            setHorarioAlumno(alumno);
            referencia.push().setValue(alumno);
        }
        this.alumno = alumno;
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
}
