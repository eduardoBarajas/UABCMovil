package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.uabcmovil.uabcmovil.Adapters.ViewPagerAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Fragments.ControlUsuarioFragment;
import com.uabcmovil.uabcmovil.Fragments.SolicitudDeRolFragment;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ControlDeUsuariosActivity extends AppCompatActivity {
    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private OptionSelectedListener listener;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    TabLayout tabLayout;
    private int tabsIcons[]={R.drawable.account_circle,R.drawable.account_off};
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
                                    startActivity(new Intent(ControlDeUsuariosActivity.this,LoginActivity.class));
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
        setContentView(R.layout.control_de_usuarios_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabsControlUsuarios);
        viewPager = findViewById(R.id.viewpagerControlUsuarios);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        initViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();

        initDrawer();
    }

    private void initTabs() {
        tabLayout.getTabAt(0).setIcon(tabsIcons[0]).setText("Asignar Rol");
        tabLayout.getTabAt(1).setIcon(tabsIcons[1]).setText("Control de usuario");
    }

    private void initViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SolicitudDeRolFragment());
        adapter.addFragment(new ControlUsuarioFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
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
                case R.id.inicio:startActivity(new Intent(ControlDeUsuariosActivity.this,NewsFeedActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.control_usuarios:Toast.makeText(getApplicationContext(),"Ya estas en asignacion de roles",Toast.LENGTH_SHORT).show();break;
                case R.id.revision_de_comentarios:startActivity(new Intent(ControlDeUsuariosActivity.this,VerificarComentarioActivity.class));finish();break;
                case R.id.acerca_nosotros:startActivity(new Intent(ControlDeUsuariosActivity.this,AboutActivity.class));finish();break;
                case R.id.calificanos:startActivity(new Intent(ControlDeUsuariosActivity.this,FeedBackActivity.class));finish();break;
                default:
                    Toast.makeText(getApplicationContext(),"KESTA PASAMDA",Toast.LENGTH_SHORT).show();
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
            OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarAplicacion","Seguro que deseas salir de la aplicacion?",listener);
            dlg.show();
        }
        return true;
    }
}
