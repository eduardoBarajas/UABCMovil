package com.uabcmovil.uabcmovil.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.Reseña;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class ReseñasFireBaseConnection extends Observable {
    private List<Reseña> reseñaList = new LinkedList<>();
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Reseñas");

    private static ReseñasFireBaseConnection ourInstance = null;

    public static ReseñasFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new ReseñasFireBaseConnection();
        return ourInstance;
    }

    private ReseñasFireBaseConnection() {
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                reseñaList.add(dataSnapshot.getValue(Reseña.class));
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Reseña reseñaMod = dataSnapshot.getValue(Reseña.class);
                for(int i = 0; i < reseñaList.size(); i++){
                    if(reseñaMod.getKey().equals(reseñaList.get(i).getKey())){
                        reseñaList.get(i).setCalificacion(reseñaMod.getCalificacion());
                        reseñaList.get(i).setNombre(reseñaMod.getNombre());
                        reseñaList.get(i).setReseña(reseñaMod.getReseña());
                        reseñaList.get(i).setRol(reseñaMod.getRol());
                    }
                }
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Reseña eliminar = null;
                for(Reseña reseña : reseñaList){
                    if(reseña.getKey().equals(dataSnapshot.getValue(Reseña.class).getKey())){
                        eliminar = reseña;
                    }
                }
                if(eliminar!=null)
                    reseñaList.remove(eliminar);
                setChanged();
                notifyObservers("Se Elimino");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<Reseña> getAllReseñas(){
        return reseñaList;
    }

    public boolean noHaCreadoReseña(String userKey){
        for(Reseña reseña : reseñaList){
            if(reseña.getKeyUsuario().equals(userKey))
                return true;
        }
        return false;
    }

    public void resetCalificacionEnReseña(String userKey){
        Reseña reseña = null;
        for(Reseña r : reseñaList){
            if(r.getKeyUsuario().equals(userKey))
                reseña = r;
        }
        if(reseña!=null){
            referencia.child(reseña.getKey()).child("calificacion").setValue("0");
        }
    }

    public void updateCalificacionReseña(String userKey,String calificacion){
        Reseña reseña = null;
        for(Reseña r : reseñaList){
            if(r.getKeyUsuario().equals(userKey))
                reseña = r;
        }
        if(reseña!=null){
            referencia.child(reseña.getKey()).child("calificacion").setValue(calificacion);
        }
    }
}
