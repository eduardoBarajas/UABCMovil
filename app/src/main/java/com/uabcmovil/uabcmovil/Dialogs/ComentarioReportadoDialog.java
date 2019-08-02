package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Entities.ReporteComentario;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class ComentarioReportadoDialog extends Dialog {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("ComentariosReportados");
    private DatabaseReference referenciaComentarios = database.getReference("Comentarios");

    public ComentarioReportadoDialog(@NonNull Activity context, ReporteComentario comentario) {
        super(context);
        setContentView(R.layout.verificar_comentario_dialog_layout);
        OptionSelectedListener listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(dlgTag.equals("Ignorar") && ((String)result).equals("SI")){
                    referencia.child(comentario.getKeyReporte()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(context,"Se ha ignorado el reporte",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
                }
                if(dlgTag.equals("Eliminar")&&((String)result).equals("SI")){
                    referenciaComentarios.child(comentario.getComentarioReportadoKey()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            referencia.child(comentario.getKeyReporte()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(context,"Se ha eliminado el comentario",Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        }
                    });
                }
            }
        };
        TextView nombreReportado, nombreReportador, txtComment;
        Button btnIgnorar, btnEliminar;
        nombreReportado = findViewById(R.id.nombreUsuarioReportado);
        nombreReportador = findViewById(R.id.nombreReportador);
        txtComment = findViewById(R.id.comentarioReportado);
        btnIgnorar = findViewById(R.id.ignorarButton);
        btnEliminar = findViewById(R.id.eliminarButton);
        nombreReportado.setText(comentario.getUsuarioReportado());
        nombreReportador.setText(comentario.getUsuarioReporto());
        txtComment.setText(comentario.getComentarioReportado());

        btnIgnorar.setOnClickListener(e->{
            OptionChooserDialog dlg = new OptionChooserDialog(context,"Ignorar","Seguro que deseas ignorar este reporte?",listener);
            dlg.show();
        });
        btnEliminar.setOnClickListener(e->{
            OptionChooserDialog dlg = new OptionChooserDialog(context,"Eliminar","Seguro que deseas eliminar este comentario?",listener);
            dlg.show();
        });
    }
}
