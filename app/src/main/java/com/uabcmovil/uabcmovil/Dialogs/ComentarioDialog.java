package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Activities.FeedBackActivity;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.Entities.Reseña;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ComentarioDialog extends Dialog {
    private SimpleDateFormat format;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia;

    public ComentarioDialog(@NonNull Activity context, String action, Comentario comentario, OptionSelectedListener listener) {
        super(context);
        referencia = database.getReference("Comentarios");
        format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        setContentView(R.layout.comentario_dialog_layout);
        TextView titulo;
        Button btnConfirmar;
        EditText editComentario;
        titulo = findViewById(R.id.titleComentarioDialog);
        btnConfirmar = findViewById(R.id.btnConfirmarComentarioDialog);
        editComentario = findViewById(R.id.editComentarioDialog);
        if(!action.equals("Nuevo")){
            editComentario.setText(comentario.getComentario());
            btnConfirmar.setText("Confirmar");
            titulo.setText("Editar Comentario");
        }else{
            btnConfirmar.setText("Confirmar");
            titulo.setText("Nuevo Comentario");
        }
        btnConfirmar.setOnClickListener(e->{
            if(editComentario.getText().toString().isEmpty()){
                Toast.makeText(context,"Asegurate de llenar el comentario",Toast.LENGTH_SHORT).show();
            }else{
                if(action.equals("Editar")){
                    Map<String,Object> dato = new HashMap<>();
                    dato.put("fechaComentario",format.format(Calendar.getInstance().getTime()));
                    dato.put("comentario",editComentario.getText().toString());
                    referencia.child(comentario.getKeyComentario()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            listener.optionSelected("ComentarioEditado","SI");
                            dismiss();
                        }
                    });
                }else{
                    comentario.setNombreAlumno(Sesion.getInstance().getAlumno().getNombre());
                    comentario.setKeyAlumno(Sesion.getInstance().getAlumno().getKey());
                    comentario.setFechaComentario(format.format(Calendar.getInstance().getTime()));
                    comentario.setComentario(editComentario.getText().toString());
                    referencia.child(comentario.getKeyComentario()).setValue(comentario, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            listener.optionSelected("ComentarioAgregado","SI");
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    public ComentarioDialog(FeedBackActivity context, String action, Reseña reseña, OptionSelectedListener listener) {
        super(context);
        referencia = database.getReference("Reseñas");
        format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        setContentView(R.layout.comentario_dialog_layout);
        TextView titulo;
        Button btnConfirmar;
        EditText editComentario;
        titulo = findViewById(R.id.titleComentarioDialog);
        btnConfirmar = findViewById(R.id.btnConfirmarComentarioDialog);
        editComentario = findViewById(R.id.editComentarioDialog);
        if(!action.equals("Nuevo")){
            editComentario.setText(reseña.getReseña());
            btnConfirmar.setText("Confirmar");
            titulo.setText("Editar Reseña");
        }else{
            btnConfirmar.setText("Confirmar");
            titulo.setText("Nueva Reseña");
        }
        btnConfirmar.setOnClickListener(e->{
            if(editComentario.getText().toString().isEmpty()){
                Toast.makeText(context,"Asegurate de llenar la reseña",Toast.LENGTH_SHORT).show();
            }else{
                if(action.equals("Editar")){
                    Map<String,Object> dato = new HashMap<>();
                    dato.put("fecha",format.format(Calendar.getInstance().getTime()));
                    dato.put("reseña",editComentario.getText().toString());
                    referencia.child(reseña.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            listener.optionSelected("ReseñaEditada","SI");
                            dismiss();
                        }
                    });
                }else{
                    reseña.setReseña(editComentario.getText().toString());
                    referencia.child(reseña.getKey()).setValue(reseña, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            listener.optionSelected("ReseñaAgregada","SI");
                            dismiss();
                        }
                    });
                }
            }
        });
    }
}
