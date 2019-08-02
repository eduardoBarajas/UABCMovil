package com.uabcmovil.uabcmovil.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Entities.Comentario;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class ComentariosFireBaseConnection extends Observable {
    private List<Comentario> comentarios = new LinkedList<>();
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Comentarios");

    private static ComentariosFireBaseConnection ourInstance = null;

    public static ComentariosFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new ComentariosFireBaseConnection();
        return ourInstance;
    }

    private ComentariosFireBaseConnection() {
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                comentarios.add(dataSnapshot.getValue(Comentario.class));
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comentario comentarioMod = dataSnapshot.getValue(Comentario.class);
                for(int i = 0; i < comentarios.size(); i++){
                    if(comentarioMod.getKeyComentario().equals(comentarios.get(i).getKeyComentario())){
                        comentarios.get(i).setComentario(comentarioMod.getComentario());
                        comentarios.get(i).setFechaComentario(comentarioMod.getFechaComentario());
                        comentarios.get(i).setNombreAlumno(comentarioMod.getNombreAlumno());
                        comentarios.get(i).setKeyAlumno(comentarioMod.getKeyAlumno());
                    }
                }
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Comentario eliminar = null;
                for(Comentario com : comentarios){
                    if(com.getKeyComentario().equals(dataSnapshot.getValue(Comentario.class).getKeyComentario())){
                        eliminar = com;
                    }
                }
                if(eliminar!=null)
                    comentarios.remove(eliminar);
                setChanged();
                notifyObservers("Se Elimino");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<Comentario> getAllComentarios(String key){
        List<Comentario> returned = new LinkedList<>();
        for(Comentario com : comentarios){
            if(com.getKeyProfesor().equals(key)){
                returned.add(com);
            }
        }
        return returned;
    }
}
