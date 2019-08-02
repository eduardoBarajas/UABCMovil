package com.uabcmovil.uabcmovil.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Solicitud;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.HashMap;
import java.util.Map;

public class ExpandSolicitudActivity extends AppCompatActivity {
    private TextView title, nombreProfesor, materia, hora, fecha, edificio, lab;
    private Button btnAprobar, btnRechazar;
    private EditText motivosRechazo;
    private Solicitud solicitudActual;
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Solicitudes");
    private OptionSelectedListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_solicitud_activity_layout);
        TextView comentariosTextView = findViewById(R.id.comentariosTextViewExpandedSolicitud);
        TextView motivosRechazoTextView = findViewById(R.id.rechazoTextViewExpandedSolicitud);
        TextView rechazoLabel = findViewById(R.id.rechazoLabel);
        title = findViewById(R.id.titleExpandedSolicitud);
        nombreProfesor = findViewById(R.id.nombreProfesorExpandedSolicitud);
        materia = findViewById(R.id.materiaExpandedSolicitud);
        hora = findViewById(R.id.horaExpandedSolicitud);
        fecha = findViewById(R.id.fechaExpandedSolicitud);
        edificio = findViewById(R.id.edificioExpandedSolicitud);
        lab = findViewById(R.id.labExpandedSolicitud);
        btnAprobar = findViewById(R.id.aprobarBtnExpandedSolicitud);
        btnRechazar = findViewById(R.id.rechazarBtnExpandedSolicitud);
        motivosRechazo = findViewById(R.id.rechazoExpandedSolicitud);
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(dlgTag.equals("EliminarSolicitud"))
                    if(((String)result).equals("SI")) {
                        referencia.child(solicitudActual.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Se elimino la solicitud.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                if(dlgTag.equals("AprobarSolicitud")){
                    if(((String)result).equals("SI")){
                        Map<String,Object> dato = new HashMap<>();
                        dato.put("estado","Aceptada");
                        referencia.child(solicitudActual.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(),"Se aprobo la solicitud",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
                if(dlgTag.equals("RechazarSolicitud")){
                    if(((String)result).equals("SI")){
                        Map<String,Object> dato = new HashMap<>();
                        dato.put("estado","Cancelada");
                        dato.put("rechazo",motivosRechazo.getText().toString());
                        referencia.child(solicitudActual.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(),"Se rechazo la solicitud",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }
        };
        if(getIntent().hasExtra("Solicitud")){
            solicitudActual = getSolicitudFromArray(getIntent().getStringArrayExtra("Solicitud"));
            switch (solicitudActual.getEstado()){
                case "Aceptada":{
                    btnAprobar.setVisibility(View.GONE);
                    title.setText("Solicitud Aprobada");
                    rechazoLabel.setVisibility(View.GONE);
                    if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")){
                        btnRechazar.setText("Eliminar");

                    }
                    break;
                }
                case "Pendiente":{
                    title.setText("Solicitud Pendiente");
                    motivosRechazo.setVisibility(View.VISIBLE);
                    if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")){
                        btnRechazar.setText("Eliminar");
                        btnAprobar.setVisibility(View.GONE);
                        rechazoLabel.setVisibility(View.GONE);
                        motivosRechazo.setVisibility(View.GONE);
                    }
                    break;
                }
                case "Cancelada":{
                    btnAprobar.setVisibility(View.GONE);
                    motivosRechazoTextView.setVisibility(View.VISIBLE);
                    motivosRechazoTextView.setText(solicitudActual.getRechazo());
                    title.setText("Solicitud Cancelada");
                    break;
                }
            }
            comentariosTextView.setVisibility(View.VISIBLE);
            comentariosTextView.setText(solicitudActual.getComentarios());
            nombreProfesor.setText(solicitudActual.getNombre());
            materia.setText(solicitudActual.getMateria());
            hora.setText(solicitudActual.getHora());
            fecha.setText(solicitudActual.getFecha());
            edificio.setText(solicitudActual.getEdificio());
            lab.setText(solicitudActual.getLaboratorio());
            motivosRechazo.setText(solicitudActual.getRechazo());
            btnAprobar.setOnClickListener(e->{
                if(motivosRechazo.getText().toString().isEmpty()){
                    OptionChooserDialog dlg = new OptionChooserDialog(ExpandSolicitudActivity.this,"AprobarSolicitud","Segugo que deseas aprobar esta solicitud?",listener);
                    dlg.show();
                }else{
                    Toast.makeText(getApplicationContext(),"Asegurate que el campo de motivos de rechazo este limpio\n" +
                            "para poder aprobar la solicitud.",Toast.LENGTH_SHORT).show();
                }
            });
            if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")){
                btnRechazar.setText("Eliminar");
                btnRechazar.setOnClickListener(e->{
                        OptionChooserDialog dlg = new OptionChooserDialog(ExpandSolicitudActivity.this,"EliminarSolicitud","De verdad deseas eliminar tu solicitud?",listener);
                        dlg.show();
                });
            }
            if(solicitudActual.getEstado().equals("Pendiente")&&!Sesion.getInstance().getAlumno().getRol().equals("Profesor")) {
                btnRechazar.setOnClickListener(e->{
                    //preguntar primero si de verdad desea eliminar
                    if(!motivosRechazo.getText().toString().isEmpty()){
                        OptionChooserDialog dlg = new OptionChooserDialog(ExpandSolicitudActivity.this,"RechazarSolicitud","De verdad deseas rechazar este registro?",listener);
                        dlg.show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Asegurate de llenar el campo de motivos de rechazo \n" +
                                "para poder rechazar la solicitud.",Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                if(!Sesion.getInstance().getAlumno().getRol().equals("Profesor")){
                    btnRechazar.setVisibility(View.GONE);
                }
            }
        }
    }

    private Solicitud getSolicitudFromArray(String[] s) {
        Solicitud  solicitud = new Solicitud();
        solicitud.setEstado(s[0]);
        solicitud.setComentarios(s[1]);
        solicitud.setEdificio(s[2]);
        solicitud.setFecha(s[3]);
        solicitud.setHora(s[4]);
        solicitud.setKey(s[5]);
        solicitud.setLaboratorio(s[6]);
        solicitud.setMateria(s[7]);
        solicitud.setNombre(s[8]);
        solicitud.setRechazo(s[9]);
        return solicitud;
    }
}
