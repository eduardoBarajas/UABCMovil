package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EditUserDialog extends Dialog {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private OptionSelectedListener listener;
    public EditUserDialog(@NonNull Activity context, Alumno user) {
        super(context);
        setContentView(R.layout.edit_user_dialog_layout);
        EditText txtNombre, txtMatricula;
        AutoCompleteTextView txtCarrera, txtRol;
        TextView txtCorreo;
        Button btnConfirmar, btnCancelar;
        txtCorreo = findViewById(R.id.correo_usuario_edit_usuario);
        txtNombre = findViewById(R.id.nombre_usuario_edit_usuario);
        txtRol = findViewById(R.id.rol_usuario_edit_usuario);
        txtMatricula = findViewById(R.id.matricula_edit_usuario);
        txtCarrera = findViewById(R.id.carrera_edit_usuario);
        btnCancelar = findViewById(R.id.btnCancelarEdit);
        btnConfirmar = findViewById(R.id.btnConfirmarEdit);
        txtCorreo.setText(user.getUser());
        txtNombre.setText(user.getNombre());
        txtRol.setText(user.getRol());
        txtMatricula.setText(user.getMatricula());
        txtCarrera.setText(user.getCarrera());

        ArrayList<String> carreras = new ArrayList<>();
        ArrayList<String> roles = new ArrayList<>();
        Collections.addAll(carreras,"INGENIERIA CIVIL","INGENIERIA EN ELECTRONICA","INGENIERIA  EN COMPUTACION",
                "INGENIERIA INDUSTRIAL","BIOINGENIERIA","INGENIERIA EN NANOTECNOLOGIA");
        Collections.addAll(roles,"No definido","Estudiante","Profesor","Administrador","AdminLab");

        ArrayAdapter<String> adapterCarreras = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, carreras);
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, roles);
        txtCarrera.setAdapter(adapterCarreras);
        txtRol.setAdapter(adapterRoles);
        btnCancelar.setOnClickListener(e->{
            dismiss();
        });
        btnConfirmar.setOnClickListener(e->{
            OptionChooserDialog dlg = new OptionChooserDialog(context,"EditarUsuario","Seguro que quieres editar a este usuario?",listener);
            dlg.show();
        });
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(dlgTag.equals("EditarUsuario")&&((String)result).equals("SI")){
                    Map<String,Object> datos = new HashMap<>();
                    datos.put("rol",txtRol.getText().toString());
                    datos.put("nombre",txtNombre.getText().toString());
                    datos.put("carrera",txtCarrera.getText().toString());
                    datos.put("matricula",txtMatricula.getText().toString());
                    referencia.child(user.getKey()).updateChildren(datos, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(context,"Se actualizo al usuario exitosamente.",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
                }
            }
        };
    }
}
