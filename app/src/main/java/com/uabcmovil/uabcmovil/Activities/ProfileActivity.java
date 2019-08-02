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
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Adapters.HorariosRVAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.UABCServerConnection;
import com.uabcmovil.uabcmovil.Data.UABCServerConnectionHolder;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Dialogs.UabcLoginDlg;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Materia;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import org.jsoup.Connection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ProfileActivity extends AppCompatActivity{
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private List<Materia> materias = new LinkedList<>();
    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private TextView txtUser,txtCarrera, txtMatricula;
    private OptionSelectedListener listener;
    private SimpleDateFormat format;
    private HorariosRVAdapter adapter;
    private RecyclerView rv;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private CardView clasesDelDia;
    private ImageView imgNoClases;
    private TextView txtClasesDelDia;
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
                                    startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                                    finish();
                                }
                            };
                            closeSessionHandler.postDelayed(timer,1000);
                        }
                        break;
                    }
                    case "UabcLoginDlg":{
                        if(!((String)result).isEmpty())
                            updateHorario(Sesion.getInstance().getAlumno().getUser().split("@")[0],((String)result));
                        break;
                    }
                }
            }
        };
        format = new SimpleDateFormat("EEEE");
        String currentDay = format.format(Calendar.getInstance().getTime());
        setContentView(R.layout.profile_activity_layout);
        txtClasesDelDia = findViewById(R.id.txtClasesDelDia);
        clasesDelDia = findViewById(R.id.cardClasesDelDia);
        imgNoClases = findViewById(R.id.imgNoClases);
        txtCarrera = findViewById(R.id.txtCarrera);
        txtMatricula = findViewById(R.id.txtMatricula);
        txtUser = findViewById(R.id.txtNombreUsuario);
        layout = findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new HorariosRVAdapter();
        if(Sesion.getInstance().getAlumno().getCarrera()!=null){
            setAlumno(Sesion.getInstance().getAlumno());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        initDrawer();

        CardView actualizarCard = findViewById(R.id.cardActualizar);
        CardView miHorarioCard = findViewById(R.id.cardHorario);
        rv = findViewById(R.id.RVClases);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        setMiHorarioHoy(Sesion.getInstance().getAlumno().getClases());

        miHorarioCard.setOnClickListener(e->{
            startActivity(new Intent(ProfileActivity.this,HorarioActivity.class));
        });
        actualizarCard.setOnClickListener(e->{
            UabcLoginDlg dlg = new UabcLoginDlg(ProfileActivity.this,Sesion.getInstance().getAlumno().getUser(),listener);
            dlg.show();
        });
    }

    private void updateHorario(String user, String pass) {
        try {
            Alumno current = Sesion.getInstance().getAlumno();
            UABCServerConnectionHolder con = UABCServerConnectionHolder.getInstance((Connection.Response)new UABCServerConnection().execute("crearConexion",user,pass).get());
            Connection.Response login = con.getConexion();
            Alumno alum = (Alumno) new UABCServerConnection().execute("getUserInfo", login).get();
            current.setClases(checkIfClasesAreNotFused(alum.getClases()));
            current.setCarrera(alum.getCarrera());
            current.setMatricula(alum.getMatricula());
            Map<String,Object> dato = new HashMap<>();
            dato.put("clases",current.getClases());
            referencia.child(current.getKey()).updateChildren(dato);
            dato = new HashMap<>();
            dato.put("matricula",current.getMatricula());
            referencia.child(current.getKey()).updateChildren(dato);
            dato = new HashMap<>();
            dato.put("carrera",current.getCarrera());
            referencia.child(current.getKey()).updateChildren(dato);
            Sesion.getInstance().addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    if(((String)o).equals("userLoaded")){
                        setAlumno(alum);
                        //se actualiza el drawer
                        NavigationView drawer = findViewById(R.id.drawer_layout_id);
                        TextView txtUser, txtCarrera;
                        txtUser = drawer.findViewById(R.id.txtUsuarioDrawer);
                        txtCarrera = drawer.findViewById(R.id.txtCarreraDrawer);
                        txtCarrera.setText(alum.getCarrera());
                        txtUser.setText(alum.getNombre());
                        con.restartConnection();
                        Sesion.getInstance().deleteObserver(this);
                    }
                }
            });
            Sesion.getInstance().setAlumno(current);
            Toast.makeText(getApplicationContext(),"Horario Actualizado",Toast.LENGTH_SHORT).show();
        }catch (Exception e1) {
            Toast.makeText(getApplicationContext(),"Ocurrio un error intentalo mas tarde",Toast.LENGTH_SHORT).show();
            Log.e("HUBO UN ERROR",":v");
        }
    }

    private void setAlumno(Alumno alumno) {
        txtUser.setText(alumno.getNombre());
        txtMatricula.setText("Matricula: "+alumno.getMatricula());
        txtCarrera.setText("Carrera: "+alumno.getCarrera());
        if(rv!=null)
            setMiHorarioHoy(checkIfClasesAreNotFused(alumno.getClases()));
    }

    private void setMiHorarioHoy(List<Materia> clases){
        for(Materia m : clases){
            if(m.getHora().split(" ")[0].equals(traducirFecha(format.format(Calendar.getInstance().getTime())))){
                materias.add(m);
            }
        }
        adapter.setData(materias);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
        if(adapter.getItemCount()==0){
            txtClasesDelDia.setText("No tienes clases hoy");
            imgNoClases.setVisibility(View.VISIBLE);
            clasesDelDia.setVisibility(View.GONE);
        }else{
            txtClasesDelDia.setText("Mis clases hoy");
            imgNoClases.setVisibility(View.GONE);
            clasesDelDia.setVisibility(View.VISIBLE);
        }
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
        if(Sesion.getInstance().getAlumno().getRol().equals("Estudiante"))
            navItems = drawer.findViewById(R.id.navItems);
        if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")||Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
            navItems = drawer.findViewById(R.id.navItemsProfesor);
        navItems.setVisibility(View.VISIBLE);
        navItems.bringToFront();
        navItems.setNavigationItemSelectedListener(e->{
            switch (e.getItemId()){
                case R.id.califica:startActivity(new Intent(ProfileActivity.this,CalificarProfesorActivity.class));finish();break;
                case R.id.inicio:startActivity(new Intent(ProfileActivity.this,NewsFeedActivity.class));finish();break;
                case R.id.perfil:Toast.makeText(getApplicationContext(),"Ya estas en perfil.",Toast.LENGTH_SHORT).show();break;
                case R.id.mapa:startActivity(new Intent(ProfileActivity.this,MapActivity.class));finish();break;
                case R.id.calificanos:startActivity(new Intent(ProfileActivity.this,FeedBackActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.acerca_nosotros:startActivity(new Intent(ProfileActivity.this,AboutActivity.class));finish();break;
                default:
                    Toast.makeText(getApplicationContext(),"KESTA PASAMDA",Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private List<Materia> checkIfClasesAreNotFused(List<Materia> clases) {
        String[] c = new String[]{};
        List<Materia> newLista = new LinkedList<>();
        for(Materia m : clases){
            if(m.getHora().split(" ").length>2){
                c = m.getHora().split(" ");
                for(int i=0; i < c.length; i+=2){
                    Materia materia = new Materia();
                    materia.setTipoClase(m.getTipoClase());
                    materia.setSubGrupo(m.getSubGrupo());
                    materia.setProfesor(m.getProfesor());
                    materia.setEtapa(m.getEtapa());
                    materia.setGrupo(m.getGrupo());
                    materia.setNombreClase(m.getNombreClase());
                    materia.setSalon(getSalonName(materia));
                    materia.setHora(c[i]+" "+c[i+1]);
                    newLista.add(materia);
                }
            }else{
                m.setSalon(getSalonName(m));
                newLista.add(m);
            }
        }
        return newLista;
    }

    private String traducirFecha(String format) {
        switch (format){
            case "Monday": return "Lunes";
            case "Tuesday": return "Martes";
            case "Wednesday": return "Míercoles";
            case "Thursday": return "Jueves";
            case "Friday": return "Viernes";
            case "Saturday": return "Sábado";
        }
        return "";
    }

    private String getSalonName(Materia m){
        switch (m.getNombreClase()){
            case "12116 - AUTOMATIZACION Y CONTROL":{
                if(m.getTipoClase().equals("Clase")){
                    return "E1/308";
                }else{
                    return "E36/Lab Mecatronica";
                }
            }
            case "12112 - TOPICOS DE MANEJO FINANCIERO": {
                if(m.getTipoClase().equals("Taller")){
                    return "E1/201";
                }
                break;
            }
            case "12118 - DISEÑO DE REDES DE COMPUTADORAS": return "E45/Lab Redes";
            case "12140 - MICROPROCESADORES AVANZADOS":{
                if(m.getTipoClase().equals("Taller")){
                    return "E36/Lab Electronica";
                }else{
                    return "E1/104";
                }
            }
            case "12143 - APLICACIONES DISTRIBUIDAS":{
                if(m.getTipoClase().equals("Laboratorio")){
                    return "E34/Lab 3";
                }
                break;
            }
            case "12148 - ADMINISTRACION DE PROYECTOS DE SOFTWARE":{
                return "E34/Lab 4";
            }
            case "12100 - ELECTRONICA APLICADA (EP)":{
                return "E1/108";
            }
            case "12102 - ORGANIZACION DE COMPUTADORAS Y LENGUAJE ENSAMBLADOR (EP)":{
                return "E1/205";
            }
            case "12105 - PROGRAMACION ORIENTADA A OBJETOS AVANZADA (EP)":{
                return "E40/Sala C";
            }
        }
        return m.getSalon();
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