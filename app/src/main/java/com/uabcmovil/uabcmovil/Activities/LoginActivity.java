package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

import static android.view.KeyEvent.KEYCODE_BACK;

public class LoginActivity extends AppCompatActivity {
    private OptionSelectedListener listener;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private final int SIGN_IN = 751;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        //auth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_oath))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
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
                }
            }
        };
        setContentView(R.layout.login_activity_layout);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnOtroLogin = findViewById(R.id.btnLoginOtraCuenta);
        Button btnInvitado = findViewById(R.id.btnInvitado);
        if(auth.getCurrentUser()==null){
            btnOtroLogin.setVisibility(View.GONE);
            googleSignInClient.signOut();
        }else{
            btnOtroLogin.setVisibility(View.VISIBLE);
            btnLogin.setText("Continuar como " + auth.getCurrentUser().getEmail().split("@")[0]);
        }
        btnLogin.setOnClickListener(e->{
            if(auth.getCurrentUser()!=null){
                Toast.makeText(getApplicationContext(),"Sesion iniciada con : \n"+auth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                Sesion.getInstance().addObserver(new Observer() {
                    @Override
                    public void update(Observable observable, Object o) {
                        if(((String)o).equals("userLoaded")){
                            Intent intent = null;
                            if(Sesion.getInstance().getAlumno().getRol().equals("Profesor") || Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                                intent = new Intent(LoginActivity.this,SolicitudesActivity.class);
                            if(Sesion.getInstance().getAlumno().getRol().equals("Estudiante")||Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
                                intent = new Intent(LoginActivity.this,NewsFeedActivity.class);
                            if(Sesion.getInstance().getAlumno().getRol().equals("No definido"))
                                intent = new Intent(LoginActivity.this,NoRolAsignadoActivity.class);
                            Sesion.getInstance().deleteObserver(this);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                Sesion.getInstance().setAlumno(new Alumno(auth.getCurrentUser().getEmail(),auth.getCurrentUser().getDisplayName(),auth.getCurrentUser().getPhotoUrl()));
            }else{
                signIn();
            }
        });
        btnOtroLogin.setOnClickListener(e->{
            auth.signOut();
            googleSignInClient.signOut();
            signIn();
        });
        btnInvitado.setOnClickListener(e->{
            Intent intent = new Intent(LoginActivity.this,MapActivity.class);
            Sesion.getInstance().getAlumno().setUser("Invitado");
            intent.putExtra("Invitado",true);
            startActivity(intent);
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checar si el usuario ya habia iniciado sesion
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null){//si diferente de nulo entonces si existia una sesion
            //llevar a la otra activity
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
    public void supportFinishAfterTransition(){
        super.supportFinishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //si no hay excepcion significa que se inicio correctamente la sesion
            //mando a la otra activity
            if(!account.getEmail().contains("uabc")){
                Toast.makeText(getApplicationContext(),"Solo se permiten cuentas de la uabc",Toast.LENGTH_LONG).show();
                googleSignInClient.signOut();
            }else{
                firebaseAuthWithGoogle(account);
            }
        }catch(ApiException e){
            Log.e("Error de login","SignInResult: failed code = "+e.getStatusCode());
            Toast.makeText(getApplicationContext(),"NO SE PUDO INICIAR SESION\n"+e.getStatusCode(),Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e("Firebase","firebaseAuthWithGoogle: "+account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.e("Sesion","Se inicio");
                            FirebaseUser user = auth.getCurrentUser();
                            //iniciar nueva actividad
                            Sesion.getInstance().addObserver(new Observer() {
                                @Override
                                public void update(Observable observable, Object o) {
                                    if(((String)o).equals("userLoaded")){
                                        Intent intent = null;
                                        if(Sesion.getInstance().getAlumno().getRol().equals("Profesor") || Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                                            intent = new Intent(LoginActivity.this,SolicitudesActivity.class);
                                        if(Sesion.getInstance().getAlumno().getRol().equals("Estudiante") || Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
                                            intent = new Intent(LoginActivity.this,NewsFeedActivity.class);
                                        if(Sesion.getInstance().getAlumno().getRol().equals("No definido"))
                                            intent = new Intent(LoginActivity.this,NoRolAsignadoActivity.class);
                                        Sesion.getInstance().deleteObserver(this);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            Sesion.getInstance().setAlumno(new Alumno(user.getEmail(),user.getDisplayName(),user.getPhotoUrl()));
                        }else{
                            Log.e("Error de login","failed");
                            Toast.makeText(getApplicationContext(),"NO SE PUDO INICIAR SESION\n",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

