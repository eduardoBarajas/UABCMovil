package com.uabcmovil.uabcmovil.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.DatePickerDialog;
import com.uabcmovil.uabcmovil.Dialogs.TimePickerDialog;
import com.uabcmovil.uabcmovil.Entities.Solicitud;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.HashMap;
import java.util.Map;

public class NewSolicitudActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Solicitudes");

    private EditText comentarios;
    private TextView txtHoraSeleccionada, txtFechaSeleccionada;
    private ImageView imgHora, imgFecha;
    private AutoCompleteTextView materias, edificios, laboratorios;
    private Button btnConfirmar, btnCancelar;
    private Solicitud solicitudActual;
    private final String[] CLASES = new String[]{"INGENIERIA DE REQUERIMIENTOS","ELECTRONICA APLICADA","INTELIGENCIA ARTIFICIAL","METODOS NUMERICOS"
    ,"TOPICOS DE MANEJO FINANCIERO","AUTOMATIZACION Y CONTROL","DISEÑO DE REDES DE COMPUTADORAS","MICROPROCESADORES AVANZADOS",
    "APLICACIONES DISTRIBUIDAS","ADMINISTRACION DE PROYECTOS DE SOFTWARE"};
    private final String[] EDIFICIOS = new String[]{"E1","E2","E3","E4","E5","E6"};
    private final String[] LABORATORIOS = new String[]{"LAB 1","LAB 2","LAB 3","LAB 4","LAB A","LAB B","LAB C"};
    private OptionSelectedListener listener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        solicitudActual = new Solicitud();
        setContentView(R.layout.new_solicitud_activity_layout);
        txtFechaSeleccionada = findViewById(R.id.fechaSeleccionada);
        txtHoraSeleccionada = findViewById(R.id.horaSeleccionada);
        comentarios = findViewById(R.id.comentariosNuevaSolicitud);
        imgHora = findViewById(R.id.imgHoraChooser);
        imgFecha = findViewById(R.id.imgDateChooser);
        materias = findViewById(R.id.MateriaNuevaSolicitud);
        edificios = findViewById(R.id.edificioNuevaSolicitud);
        laboratorios = findViewById(R.id.laboratorioNuevaSolicitud);
        btnConfirmar = findViewById(R.id.confirmarBtnNuevaSolicitud);
        btnCancelar = findViewById(R.id.cancelarNuevaSolicitud);
        ArrayAdapter<String> adapterClases = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,CLASES);
        ArrayAdapter<String> adapterEdificios = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,EDIFICIOS);
        ArrayAdapter<String> adapterLaboratorios = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,LABORATORIOS);
        materias.setAdapter(adapterClases);
        edificios.setAdapter(adapterEdificios);
        laboratorios.setAdapter(adapterLaboratorios);
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                switch (dlgTag){
                    case "DatePickerDialog":{
                        txtFechaSeleccionada.setText((String)result);
                        solicitudActual.setFecha((String)result);
                        txtFechaSeleccionada.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "TimePickerDialog":{
                        txtHoraSeleccionada.setText((String)result);
                        solicitudActual.setHora((String)result);
                        txtHoraSeleccionada.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        };
        imgHora.setOnClickListener(e->{
            TimePickerDialog dlg = new TimePickerDialog(NewSolicitudActivity.this,listener);
            dlg.show();
        });
        imgFecha.setOnClickListener(e->{
            DatePickerDialog dlg = new DatePickerDialog(NewSolicitudActivity.this,listener);
            dlg.show();
        });
        btnCancelar.setOnClickListener(e->{
            finish();
        });
        btnConfirmar.setOnClickListener(e->{
            if(!solicitudActual.getFecha().isEmpty()&!solicitudActual.getHora().isEmpty()&&!materias.getText().toString().isEmpty()&&
                    !edificios.getText().toString().isEmpty()&&!laboratorios.getText().toString().isEmpty()){
                solicitudActual.setNombre(Sesion.getInstance().getAlumno().getNombre());
                solicitudActual.setMateria(materias.getText().toString());
                solicitudActual.setLaboratorio(laboratorios.getText().toString());
                solicitudActual.setEdificio(edificios.getText().toString());
                solicitudActual.setComentarios(comentarios.getText().toString());
                solicitudActual.setEstado("Pendiente");
                referencia.child(solicitudActual.getKey()).setValue(solicitudActual, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(getApplicationContext(),"Se agrego correctamente",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(),"Asegurate de llenar todos los campos (Comentario opcional)",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
