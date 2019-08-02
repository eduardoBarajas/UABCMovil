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
import com.uabcmovil.uabcmovil.Entities.ReporteComentario;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class ComentariosReportadosFireBaseConnection extends Observable{
    private List<ReporteComentario> comentarios = new LinkedList<>();
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("ComentariosReportados");

    private static ComentariosReportadosFireBaseConnection ourInstance = null;

    public static ComentariosReportadosFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new ComentariosReportadosFireBaseConnection();
        return ourInstance;
    }

    private ComentariosReportadosFireBaseConnection() {
        referencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                comentarios.add(dataSnapshot.getValue(ReporteComentario.class));
                setChanged();
                notifyObservers("Se Modifico");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ReporteComentario eliminar = null;
                for(ReporteComentario com : comentarios){
                    if(com.getKeyReporte().equals(dataSnapshot.getValue(ReporteComentario.class).getKeyReporte())){
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

    public List<ReporteComentario> getAllComentarios(){
        return comentarios;
    }
}
