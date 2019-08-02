package com.uabcmovil.uabcmovil.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Adapters.ProfesoresRVAdapter;
import com.uabcmovil.uabcmovil.Data.CalificacionesFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

import static android.view.KeyEvent.KEYCODE_BACK;

public class CalificarProfesorActivity extends AppCompatActivity {
    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private OptionSelectedListener listener;
    private ProfesoresRVAdapter adapter;
    private UsuariosFireBaseConnection con;
    private RecyclerView rv;
    private ImageView imgNoUsuarios;
    private TextView txtNoUsuarios;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private CalificacionesFireBaseConnection califConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        califConnection = CalificacionesFireBaseConnection.getInstance();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.imagen_no_disponible) // resource or drawable
                .showImageForEmptyUri(R.drawable.imagen_no_disponible) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        adapter = new ProfesoresRVAdapter(CalificarProfesorActivity.this);
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
                                    startActivity(new Intent(CalificarProfesorActivity.this,LoginActivity.class));
                                    finish();
                                }
                            };
                            closeSessionHandler.postDelayed(timer,1000);
                        }
                        break;
                    }
                }
            }
        };
        setContentView(R.layout.calificar_profesor_activity_layout);
        imgNoUsuarios = findViewById(R.id.imgNoProfesores);
        txtNoUsuarios = findViewById(R.id.txtMsgNoProfesores);
        rv = findViewById(R.id.rvProfesores);

        rv.setFocusableInTouchMode(true);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        initDrawer();

        con = UsuariosFireBaseConnection.getInstance();
        con.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(CalificarProfesorActivity.this!=null){
                    adapter.setData(con.getProfesores());
                    adapter.notifyDataSetChanged();
                    setRV();
                }
            }
        });
        califConnection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(CalificarProfesorActivity.this!=null){
                    adapter.setData(con.getProfesores());
                    adapter.notifyDataSetChanged();
                    setRV();
                }
            }
        });
        adapter.setData(con.getProfesores());
        adapter.notifyDataSetChanged();
        setRV();
    }
    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_calificacion);
        NavigationView drawer = (NavigationView)drawerLayout.findViewById(R.id.drawer_layout_calificacion_profesor);
        Alumno alumno = Sesion.getInstance().getAlumno();
        TextView txtUser, txtCarrera, txtRol;
        CircularImageView imgProfile = drawer.findViewById(R.id.imgPerfil);
        txtUser = drawer.findViewById(R.id.txtUsuarioDrawer);
        txtCarrera = drawer.findViewById(R.id.txtCarreraDrawer);
        txtRol = drawer.findViewById(R.id.rolUsuarioDrawer);
        if(alumno.getCarrera().equals("No definido")){
            txtCarrera.setVisibility(View.GONE);
        }else{
            txtCarrera.setVisibility(View.VISIBLE);
            txtCarrera.setText(alumno.getCarrera());
        }
        if(alumno.getRol()!="No definido")
            txtRol.setText(alumno.getRol());
        txtUser.setText(alumno.getNombre());
        imageLoader.displayImage(alumno.getProfilePic(),imgProfile,options);
        NavigationView navItems = null;
        if(Sesion.getInstance().getAlumno().getRol().equals("Estudiante"))
            navItems = drawer.findViewById(R.id.navItems);
        if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")||Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
            navItems = drawer.findViewById(R.id.navItemsProfesor);
        if(Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
            navItems = drawer.findViewById(R.id.navItemsAdministrador);
        navItems.setVisibility(View.VISIBLE);
        navItems.bringToFront();
        navItems.setNavigationItemSelectedListener(e->{
            switch (e.getItemId()){
                case R.id.califica:Toast.makeText(getApplicationContext(),"Ya estas en calificar al profesor",Toast.LENGTH_SHORT).show();break;
                case R.id.inicio:startActivity(new Intent(CalificarProfesorActivity.this,NewsFeedActivity.class));finish();break;
                case R.id.perfil:startActivity(new Intent(CalificarProfesorActivity.this,ProfileActivity.class));finish();break;
                case R.id.mapa:startActivity(new Intent(CalificarProfesorActivity.this, MapActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.solicitudes:startActivity(new Intent(CalificarProfesorActivity.this,SolicitudesActivity.class));finish();break;
                case R.id.incidencias:startActivity(new Intent(CalificarProfesorActivity.this,IncidenciasActivity.class));finish();break;
                case R.id.acerca_nosotros:startActivity(new Intent(CalificarProfesorActivity.this,AboutActivity.class));finish();break;
                case R.id.calificanos:startActivity(new Intent(CalificarProfesorActivity.this,FeedBackActivity.class));finish();break;
                default:Toast.makeText(getApplicationContext(),"KESTA PASAMDA",Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        //si se recibe una tecla se limpia la pantalla
        super.onKeyDown(keyCode,event);
        if(keyCode == KEYCODE_BACK){
            OptionChooserDialog dlg = new OptionChooserDialog(CalificarProfesorActivity.this,"CerrarAplicacion","Seguro que deseas salir de la aplicacion?",listener);
            dlg.show();
        }
        return true;
    }

    private void setRV(){
        if(rv!=null){
            rv.setAdapter(adapter);
        }
        if(adapter.getItemCount()==0){
            imgNoUsuarios.setVisibility(View.VISIBLE);
            txtNoUsuarios.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            imgNoUsuarios.requestFocus();
        }else{
            imgNoUsuarios.setVisibility(View.GONE);
            txtNoUsuarios.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.requestFocus();
        }
    }
}
