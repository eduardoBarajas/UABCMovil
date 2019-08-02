package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Adapters.NewsRVAdapter;
import com.uabcmovil.uabcmovil.Data.NoticiasFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Noticia;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.view.KeyEvent.KEYCODE_BACK;
import static java.lang.Thread.sleep;

public class NewsFeedActivity extends AppCompatActivity {
    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private List<Noticia> noticias = new LinkedList<>();
    private RecyclerView rv;
    private NewsRVAdapter adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private static final int NEW_FEED = 789;
    private TextView txtNoNoticia;
    private ImageView imgNoNoticia;
    private OptionSelectedListener listener;
    private NoticiasFireBaseConnection con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = NoticiasFireBaseConnection.getInstance(getApplicationContext());
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
                                    startActivity(new Intent(NewsFeedActivity.this,LoginActivity.class));
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
        adapter = new NewsRVAdapter(this);
        setContentView(R.layout.newsfeed_activity_layout);
        txtNoNoticia = findViewById(R.id.txtNoNoticias);
        imgNoNoticia = findViewById(R.id.imgNoNoticias);

        rv = findViewById(R.id.allNewsRV);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter.setData(noticias);
        rv.setAdapter(adapter);

        if(adapter.getItemCount()==0){
            rv.setVisibility(View.GONE);
            txtNoNoticia.setVisibility(View.VISIBLE);
            imgNoNoticia.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = findViewById(R.id.newButton);
        if(!Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(e->{
            startActivityForResult(new Intent(NewsFeedActivity.this,NewFeedActivity.class),NEW_FEED);
        });
        layout = findViewById(R.id.coordinatorLayoutNews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNews);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        initDrawer();
        con.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(((String)o).equals("dataChanged")){
                    noticias = con.getAllNoticias();
                    reloadAdapter();
                }
            }
        });
    }

    private void reloadAdapter() {
        if(noticias.size()>0){
            rv.setVisibility(View.VISIBLE);
            txtNoNoticia.setVisibility(View.GONE);
            imgNoNoticia.setVisibility(View.GONE);
        }else{
            rv.setVisibility(View.GONE);
            txtNoNoticia.setVisibility(View.VISIBLE);
            imgNoNoticia.setVisibility(View.VISIBLE);
        }
        adapter.setData(noticias);
        adapter.notifyDataSetChanged();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layoutNews);
        NavigationView drawer = (NavigationView)findViewById(R.id.drawer_layout_id_News);
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
                case R.id.califica:startActivity(new Intent(NewsFeedActivity.this,CalificarProfesorActivity.class));finish();break;
                case R.id.inicio:Toast.makeText(getApplicationContext(),"Ya estas en el inicio.",Toast.LENGTH_SHORT).show();break;
                case R.id.perfil:startActivity(new Intent(NewsFeedActivity.this,ProfileActivity.class));finish();break;
                case R.id.mapa:startActivity(new Intent(NewsFeedActivity.this,MapActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.solicitudes:startActivity(new Intent(NewsFeedActivity.this,SolicitudesActivity.class));finish();break;
                case R.id.incidencias:startActivity(new Intent(NewsFeedActivity.this,IncidenciasActivity.class));finish();break;
                case R.id.acerca_nosotros:startActivity(new Intent(NewsFeedActivity.this,AboutActivity.class));finish();break;
                case R.id.control_usuarios:startActivity(new Intent(NewsFeedActivity.this,ControlDeUsuariosActivity.class));finish();break;
                case R.id.revision_de_comentarios:startActivity(new Intent(NewsFeedActivity.this,VerificarComentarioActivity.class));finish();break;
                case R.id.calificanos:startActivity(new Intent(NewsFeedActivity.this,FeedBackActivity.class));finish();break;
                default:
                    Toast.makeText(getApplicationContext(),"KESTA PASAMDA",Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_FEED && resultCode == RESULT_OK){
            recreate();
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
