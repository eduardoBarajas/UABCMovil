package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.uabcmovil.uabcmovil.Data.CalificacionesFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.IncidenciasFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.SolicitudesFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.SolicitudesRolFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Entities.CalificacionProfesor;
import com.uabcmovil.uabcmovil.R;

import static java.lang.Thread.sleep;
import com.uabcmovil.uabcmovil.Activities.*;

public class LauncherActivity extends AppCompatActivity {
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sesion.getInstance();
        SolicitudesFireBaseConnection.StartInstance(getApplicationContext());
        IncidenciasFireBaseConnection.StartInstance(getApplicationContext());
        SolicitudesRolFireBaseConnection.StartInstance(getApplicationContext());
        CalificacionesFireBaseConnection.getInstance();
        UsuariosFireBaseConnection.getInstance();
        setContentView(R.layout.launcher_activity_layout);
        logo = findViewById(R.id.imgViewLogoLauncher);
        //se crea un thread para que corra el timer.
        Handler handler = new Handler();
        Runnable timer = new Runnable() {
            @Override
            public void run() {
                //cuando el tiempo llegue a 0 se redireccionara a la siguiente actividad.
                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LauncherActivity.this, logo, "ImagenLogo");
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                finish();
            }
        };
        handler.postDelayed(timer,2000);
    }
}
