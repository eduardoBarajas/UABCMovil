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
import com.uabcmovil.uabcmovil.Entities.Noticia;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class NoticiasFireBaseConnection extends Observable {
    private List<Noticia> noticias = new LinkedList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Noticias");
    private static Context context;
    private static NoticiasFireBaseConnection ourInstance = null;
    private boolean firstRun = true;

    public static NoticiasFireBaseConnection getInstance() {
        if(ourInstance==null)
            ourInstance = new NoticiasFireBaseConnection();
        return ourInstance;
    }

    public static NoticiasFireBaseConnection getInstance(Context context) {
        NoticiasFireBaseConnection.context = context;
        if(ourInstance==null)
            ourInstance = new NoticiasFireBaseConnection();
        return ourInstance;
    }

    private NoticiasFireBaseConnection() {
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
                Noticia noticia = dataSnapshot.getValue(Noticia.class);
                noticias.add(0,noticia);
                setChanged();
                notifyObservers("dataChanged");
                if(!firstRun){
                    notificarNuevaNoticia(noticia.getTitulo());
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int eliminado = -1;
                for(int i = 0; i < noticias.size(); i++){
                    if(noticias.get(i).getKey().equals(dataSnapshot.getValue(Noticia.class).getKey())){
                        eliminado = i;
                        break;
                    }
                }
                if(eliminado != -1){
                    noticias.remove(eliminado);
                    setChanged();
                    notifyObservers("dataChanged");
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void notificarNuevaNoticia(String titulo) {
        Intent notificacion = new Intent();
        notificacion.putExtra("tipo","Nueva Noticia");
        notificacion.putExtra("titulo",titulo);
        notificacion.putExtra("actividad","NewsFeed");
        notificacion.setAction("android.intent.action.NUEVA_NOTIFICACION");
        NoticiasFireBaseConnection.context.sendBroadcast(notificacion);
    }

    public List<Noticia> getAllNoticias(){
        return noticias;
    }
}
