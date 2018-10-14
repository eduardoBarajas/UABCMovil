package com.barajasoft.uabcmobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.barajasoft.uabcmobile.Data.Sesion;
import com.barajasoft.uabcmobile.Entities.Alumno;
import com.barajasoft.uabcmovil.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnInvitado = findViewById(R.id.btnInvitado);
        btnLogin.setOnClickListener(e->{
            Sesion.getInstance().setAlumno(new Alumno("barajas.eduardo"));
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        });
        btnInvitado.setOnClickListener(e->{
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        });
    }
}
