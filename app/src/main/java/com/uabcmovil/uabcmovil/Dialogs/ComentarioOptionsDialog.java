package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Activities.FeedBackActivity;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.Entities.Reseña;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class ComentarioOptionsDialog extends Dialog {
    public ComentarioOptionsDialog(@NonNull Activity context, Comentario comentario, OptionSelectedListener listener) {
        super(context);
        setContentView(R.layout.options_comentario_dialog_layout);
        TextView editar,eliminar,reportar;
        editar = findViewById(R.id.editarComentarioDlg);
        eliminar = findViewById(R.id.eliminarComentarioDlg);
        reportar = findViewById(R.id.reportarComentarioDlg);
        if(!comentario.getKeyAlumno().equals(Sesion.getInstance().getAlumno().getKey())){
            editar.setVisibility(View.GONE);
            eliminar.setVisibility(View.GONE);
        }else{
            reportar.setVisibility(View.GONE);
        }

        editar.setOnClickListener(e->{
            listener.optionSelected("EditarComentario","SI");
            dismiss();
        });

        eliminar.setOnClickListener(e->{
            listener.optionSelected("EliminarComentario",comentario.getKeyComentario());
            dismiss();
        });

        reportar.setOnClickListener(e->{
            listener.optionSelected("ReportarComentario",comentario.getKeyComentario());
            dismiss();
        });
    }

    public ComentarioOptionsDialog(Activity context, Reseña reseña, OptionSelectedListener listener) {
        super(context);
        setContentView(R.layout.options_comentario_dialog_layout);
        TextView editar,eliminar,reportar;
        editar = findViewById(R.id.editarComentarioDlg);
        eliminar = findViewById(R.id.eliminarComentarioDlg);
        reportar = findViewById(R.id.reportarComentarioDlg);
        reportar.setVisibility(View.GONE);
        if(!reseña.getKeyUsuario().equals(Sesion.getInstance().getAlumno().getKey())){
            editar.setVisibility(View.GONE);
            eliminar.setVisibility(View.GONE);
            if(Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
                eliminar.setVisibility(View.VISIBLE);
        }
        editar.setOnClickListener(e->{
            listener.optionSelected("EditarReseña","SI");
            dismiss();
        });

        eliminar.setOnClickListener(e->{
            listener.optionSelected("EliminarReseña",reseña.getKey()+":"+reseña.getKeyUsuario());
            dismiss();
        });
    }
}
