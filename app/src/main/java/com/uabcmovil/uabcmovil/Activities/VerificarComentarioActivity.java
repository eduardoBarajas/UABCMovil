package com.uabcmovil.uabcmovil.Activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Adapters.ComentariosReportadosRVAdapter;
import com.uabcmovil.uabcmovil.Data.ComentariosReportadosFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioOptionsDialog;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioReportadoDialog;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.Entities.ReporteComentario;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.Listeners.RecyclerTouchListener;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

import static android.view.KeyEvent.KEYCODE_BACK;

public class VerificarComentarioActivity extends AppCompatActivity {
    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private OptionSelectedListener listener;
    private TextView txtNoComentarios;
    private RecyclerView rv;
    private ComentariosReportadosFireBaseConnection con;
    private ComentariosReportadosRVAdapter adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        adapter = new ComentariosReportadosRVAdapter();
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
                                    startActivity(new Intent(VerificarComentarioActivity.this,LoginActivity.class));
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
        setContentView(R.layout.verificar_comentario_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        initDrawer();

        txtNoComentarios = findViewById(R.id.txtNoHayComentariosAdmin);
        rv = findViewById(R.id.rvComentariosAdmin);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnItemTouchListener(new RecyclerTouchListener(this,rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ReporteComentario comentario = adapter.getComentario(position);
                ComentarioReportadoDialog dlg = new ComentarioReportadoDialog(VerificarComentarioActivity.this,comentario);
                dlg.show();
            }
            @Override
            public void onLongClick(View view, int position) { }
        }));
        con = ComentariosReportadosFireBaseConnection.getInstance();
        con.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                adapter.setData(con.getAllComentarios());
                setRV();
            }
        });
        adapter.setData(con.getAllComentarios());
        setRV();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView drawer = (NavigationView)findViewById(R.id.drawer_layout_id);
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
        if(Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
            navItems = drawer.findViewById(R.id.navItemsAdministrador);
        navItems.setVisibility(View.VISIBLE);
        navItems.bringToFront();
        navItems.setNavigationItemSelectedListener(e->{
            switch (e.getItemId()){
                case R.id.inicio:startActivity(new Intent(VerificarComentarioActivity.this,NewsFeedActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.control_usuarios:startActivity(new Intent(VerificarComentarioActivity.this,ControlDeUsuariosActivity.class));finish();break;
                case R.id.revision_de_comentarios:Toast.makeText(getApplicationContext(),"Ya estas en verificacion de comentarios",Toast.LENGTH_SHORT).show();break;
                case R.id.acerca_nosotros:startActivity(new Intent(VerificarComentarioActivity.this,AboutActivity.class));finish();break;
                case R.id.calificanos:startActivity(new Intent(VerificarComentarioActivity.this,FeedBackActivity.class));finish();break;
                default:
                    Toast.makeText(getApplicationContext(),"KESTA PASAMDA",Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void setRV(){
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0){
            rv.setVisibility(View.GONE);
            txtNoComentarios.setVisibility(View.VISIBLE);
        }else{
            rv.setVisibility(View.VISIBLE);
            txtNoComentarios.setVisibility(View.GONE);
            rv.setAdapter(adapter);
        }
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
            OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarAplicacion","Seguro que deseas salir de la aplicacion?",listener);
            dlg.show();
        }
        return true;
    }
}
