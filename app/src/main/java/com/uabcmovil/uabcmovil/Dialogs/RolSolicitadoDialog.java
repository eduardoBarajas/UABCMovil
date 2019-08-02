package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class RolSolicitadoDialog extends Dialog {
    private String selected = "";
    private Activity activity;
    private OptionSelectedListener listenerInterno;
    public RolSolicitadoDialog(@NonNull Activity context, OptionSelectedListener listener) {
        super(context);
        activity = context;
        setContentView(R.layout.rol_chooser_dialog_layout);
        listenerInterno = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(dlgTag.equals("SeleccionDeRol")){
                    if(((String)result).equals("SI")) {
                        listener.optionSelected("RolSeleccionado", selected);
                        dismiss();
                    }
                }
            }
        };
        Button btnEstudiante = findViewById(R.id.btnEstudianteROL);
        Button btnProfesor = findViewById(R.id.btnProfesorROL);
        Button btnAdmin = findViewById(R.id.btnAdminROL);
        btnEstudiante.setOnClickListener(e->{
            selected = "Estudiante";
            OptionChooserDialog dlg = new OptionChooserDialog(activity,"SeleccionDeRol","Seguro que quieres seleccionar el rol de : \n"+selected,listenerInterno);
            dlg.show();
        });
        btnProfesor.setOnClickListener(e->{
            selected = "Profesor";
            OptionChooserDialog dlg = new OptionChooserDialog(activity,"SeleccionDeRol","Seguro que quieres seleccionar el rol de : \n"+selected,listenerInterno);
            dlg.show();
        });
        btnAdmin.setOnClickListener(e->{
            selected = "AdminLab";
            OptionChooserDialog dlg = new OptionChooserDialog(activity,"SeleccionDeRol","Seguro que quieres seleccionar el rol de : \n"+selected,listenerInterno);
            dlg.show();
        });
    }
}
