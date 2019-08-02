package com.uabcmovil.uabcmovil.Data;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uabcmovil.uabcmovil.Entities.Alumno;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class UsuariosFireBaseConnection extends Observable{
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private List<Alumno> alumnos = new ArrayList<>();
    private static UsuariosFireBaseConnection instance = null;

    static public UsuariosFireBaseConnection getInstance(){
        if(instance == null)
            instance = new UsuariosFireBaseConnection();
        return instance;
    }

    private UsuariosFireBaseConnection(){
        referencia.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!alumnos.contains(dataSnapshot.getValue(Alumno.class))) {
                    alumnos.add(dataSnapshot.getValue(Alumno.class));
                    setChanged();
                    notifyObservers("Se Modifico");
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i=0;i<alumnos.size();i++){
                    if(alumnos.get(i).getUser().equals(dataSnapshot.getValue(Alumno.class).getUser())){
                        alumnos.get(i).setKey(dataSnapshot.getValue(Alumno.class).getKey());
                        alumnos.get(i).setMatricula(dataSnapshot.getValue(Alumno.class).getMatricula());
                        alumnos.get(i).setCarrera(dataSnapshot.getValue(Alumno.class).getCarrera());
                        alumnos.get(i).setNombre(dataSnapshot.getValue(Alumno.class).getNombre());
                        alumnos.get(i).setClases(dataSnapshot.getValue(Alumno.class).getClases());
                        alumnos.get(i).setRol(dataSnapshot.getValue(Alumno.class).getRol());
                    }
                }
                setChanged();
                notifyObservers("Se Modifico");
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Alumno eliminar = null;
                for(Alumno alumno : alumnos){
                    if(alumno.getKey().equals(dataSnapshot.getValue(Alumno.class).getKey())){
                        eliminar = alumno;
                    }
                }
                if(eliminar!=null)
                    alumnos.remove(eliminar);
                setChanged();
                notifyObservers("Se Modifico");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<Alumno> getUsers(){
        return alumnos;
    }

    public List<Alumno> getUsers(String name, String matri, String rol){
        List<Alumno> returned = new LinkedList<>();
        for(Alumno user : alumnos){
            if(!name.isEmpty() && !matri.isEmpty() && !rol.isEmpty())
                if(user.getNombre().equals(name)&&user.getMatricula().equals(matri)&&user.getRol().equals(rol)){
                    Log.e("Entro x1","N1 M1 R1");
                    returned.add(user);
                }
            if(!name.isEmpty() && !matri.isEmpty() && rol.isEmpty())
                if(user.getNombre().equals(name)&&user.getMatricula().equals(matri)) {
                    Log.e("Entro x2","N1 M1 R0");
                    returned.add(user);
                }
            if(!name.isEmpty() && matri.isEmpty() && !rol.isEmpty())
                if(user.getNombre().equals(name)&&user.getRol().equals(rol)) {
                    Log.e("Entro x3","N1 M0 R1");
                    returned.add(user);
                }
            if(!name.isEmpty() && matri.isEmpty() && rol.isEmpty())
                if(user.getNombre().equals(name)) {
                    Log.e("Entro x4","N1 M0 R0");
                    returned.add(user);
                }
            if(name.isEmpty() && !matri.isEmpty() && !rol.isEmpty())
                if(user.getMatricula().equals(matri)&&user.getRol().equals(rol)) {
                    Log.e("Entro x5","N0 M1 R1");
                    returned.add(user);
                }
            if(name.isEmpty() && !matri.isEmpty() && rol.isEmpty())
                if(user.getMatricula().equals(matri)){
                    Log.e("Entro x6","N0 M1 R0");
                    returned.add(user);
                }
            if(name.isEmpty() && matri.isEmpty() && !rol.isEmpty())
                if(user.getRol().equals(rol)) {
                    Log.e("Entro x7","N0 M0 R1");
                    returned.add(user);
                }
            if(name.isEmpty() && matri.isEmpty() && rol.isEmpty()) {
                Log.e("Entro x8","N0 M0 R0");
                continue;
            }
        }
        return returned;
    }

    public List<String> getAllNames(){
        List<String> nombres = new LinkedList<>();
        for(Alumno alumno : alumnos){
            Log.e("NombreEnCon",alumno.getNombre());
            nombres.add(alumno.getNombre());
        }
        return nombres;
    }

    public List<String> getAllMatris(){
        List<String> matriculas = new LinkedList<>();
        for(Alumno alumno : alumnos){
            matriculas.add(alumno.getMatricula());
        }
        return matriculas;
    }

    public List<Alumno> getProfesores(){
        List<Alumno> profesores = new LinkedList<>();
        for(Alumno alumno : alumnos){
            if(alumno.getRol().equals("Profesor"))
                profesores.add(alumno);
        }
        return profesores;
    }

    public Alumno getUserByKey(String key){
        for(Alumno alumno : alumnos){
            if(alumno.getKey().equals(key))
                return alumno;
        }
        return null;
    }
}
