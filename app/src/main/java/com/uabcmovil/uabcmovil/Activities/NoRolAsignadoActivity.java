package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.SolicitudesRolFireBaseConnection;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Dialogs.RolSolicitadoDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.SolicitudRol;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.KeyEvent.KEYCODE_BACK;

public class NoRolAsignadoActivity extends AppCompatActivity implements Observer {
    private OptionSelectedListener listener;
    private AtomicBoolean userRoleChanged = new AtomicBoolean(false);
    private boolean appActive = false;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("SolicitudesAsignacionesRoles");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SolicitudesRolFireBaseConnection.getInstance();
        setContentView(R.layout.no_rol_activity_layout);
        Button btnSolicitud = findViewById(R.id.btnSolicitudAsignacionRol);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        TextView labelSolicitudEnviada = findViewById(R.id.labelEnviada);
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                switch (dlgTag){
                    case "CerrarAplicacion":{
                        if(((String)result).equals("SI")){
                            finish();
                        }
                        break;
                    }
                    case "CerrarSesion":{
                        if(((String)result).equals("SI")){
                            Sesion.getInstance().clearSesion();
                            Handler closeSessionHandler = new Handler();
                            Runnable timer = new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Se cerro la sesion.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(NoRolAsignadoActivity.this,LoginActivity.class));
                                    finish();
                                }
                            };
                            closeSessionHandler.postDelayed(timer,1000);
                        }
                        break;
                    }
                    case "RolSeleccionado":{
                        SolicitudRol solicitudRol = new SolicitudRol();
                        Alumno alumno = Sesion.getInstance().getAlumno();
                        solicitudRol.setCorreo(alumno.getUser());
                        solicitudRol.setKeyUsuario(alumno.getKey());
                        solicitudRol.setNombre(alumno.getNombre());
                        solicitudRol.setRol_solicitdado((String)result);
                        referencia.child(solicitudRol.getKeySolicitud()).setValue(solicitudRol, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(),"Se enviara la solicitud para asignar el rol de: "+((String)result),Toast.LENGTH_SHORT).show();
                                labelSolicitudEnviada.setVisibility(View.VISIBLE);
                                btnSolicitud.setVisibility(View.GONE);
                            }
                        });
                        break;
                    }
                }
            }
        };
        btnCerrarSesion.setOnClickListener(e->{
            OptionChooserDialog dlg = new OptionChooserDialog(NoRolAsignadoActivity.this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
            dlg.show();
        });
        btnSolicitud.setOnClickListener(e->{
            RolSolicitadoDialog dlg = new RolSolicitadoDialog(NoRolAsignadoActivity.this,listener);
            dlg.show();
        });
        Sesion.getInstance().addObserver(this);
        if(SolicitudesRolFireBaseConnection.getInstance().isAlreadySolicited(Sesion.getInstance().getAlumno().getUser())){
            btnSolicitud.setVisibility(View.GONE);
            labelSolicitudEnviada.setText("La solicitud ya ha sido enviada,\nPor favor espera a que te asignen el rol.");
            labelSolicitudEnviada.setVisibility(View.VISIBLE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        //si se recibe una tecla se limpia la pantalla
        super.onKeyDown(keyCode,event);
        if(keyCode == KEYCODE_BACK){
            OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarAplicacion","Seguro que deseas salir de la aplicacion?",listener);
            dlg.show();
        }
        return true;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(!Sesion.getInstance().getAlumno().getRol().equals("No definido")){
            userRoleChanged.set(true);
            if(appActive)
                updateActivity();
        }
    }

    private void updateActivity() {
        if(userRoleChanged.get()==true){
            switch (Sesion.getInstance().getAlumno().getRol()){
                case "Estudiante":{
                    Sesion.getInstance().deleteObserver(this);
                    startActivity(new Intent(NoRolAsignadoActivity.this,NewsFeedActivity.class));
                    finish();
                    break;
                }
                case "Profesor":{
                    Sesion.getInstance().deleteObserver(this);
                    startActivity(new Intent(NoRolAsignadoActivity.this,SolicitudesActivity.class));
                    finish();
                    break;
                }
                case "AdminLab":{
                    Sesion.getInstance().deleteObserver(this);
                    startActivity(new Intent(NoRolAsignadoActivity.this,IncidenciasActivity.class));
                    finish();
                    break;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        appActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        appActive = true;
        updateActivity();
    }
}
