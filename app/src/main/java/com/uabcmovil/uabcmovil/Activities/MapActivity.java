package com.uabcmovil.uabcmovil.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.LocationExpandedDlg;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Materia;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;
import com.uabcmovil.uabcmovil.Utilities.EdificiosLoader;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private CoordinatorLayout layout = null;
    private DrawerLayout drawerLayout;
    private LocationEngine locationEngine;
    private SimpleDateFormat currentDayFormat;
    private SimpleDateFormat currentHour;
    private OptionSelectedListener listener;
    private String mensajeDialogo = "";

    private static final LatLngBounds UABC_SAUZAL_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(31.914869, -116.876110))
            .include(new LatLng(31.310685, -116.920939))
            .include(new LatLng(31.662051, -116.337603))
            .include(new LatLng(32.131752, -115.688879))
            .build();

    //En la lista de polygons se almacenan todos los poligonos que se van a agregar al mapa
    private List<com.mapbox.mapboxsdk.annotations.Polygon> polygons = new LinkedList<com.mapbox.mapboxsdk.annotations.Polygon>();
    //en polygons key guardo una referencia de acuerdo a la posicion donde esta el poligono del edificio
    private Map<String, Integer> polygonsKeys = new HashMap<>();
    private Map<Integer, String> reversePolygonsKeys = new HashMap<>();
    private List<Marker> markers = new LinkedList<>();
    private ArrayList<String> edificios = new ArrayList<>();
    private ArrayList<LatLng> centros = new ArrayList<>();

    private EdificiosLoader loader;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private AppCompatSpinner buscador;
    private boolean mapLoaded = false;
    private boolean guest = false;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                                    startActivity(new Intent(MapActivity.this,LoginActivity.class));
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

        currentDayFormat = new SimpleDateFormat("EEEE");
        currentHour = new SimpleDateFormat("kk:mm");
        currentHour.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));

        loader = new EdificiosLoader(getApplicationContext());
        loader.loadJson();
        loader.parseJson();
        int index = 0;
        for (String key : loader.getEdificiosKeys()) {
            edificios.add(key);
            polygonsKeys.put(key, index);
            reversePolygonsKeys.put(index, key);
            index++;
        }

        Mapbox.getInstance(this, "pk.eyJ1IjoiZWR1MjMiLCJhIjoiY2psa2lpNTRzMG43azNrbzU2emYxaThtNiJ9.j20WpkbxDZbaszbFUToebg");
        setContentView(R.layout.map_activity_layout);

        FloatingActionButton btnMiPosicion = findViewById(R.id.miPosicion);
        FloatingActionButton btnMiClase = findViewById(R.id.miClase);
        if(getIntent().hasExtra("Invitado")){
            guest = true;
            mensajeDialogo = "Seguro que deseas salir del mapa?";
            btnMiClase.setVisibility(View.GONE);
            hideDrawer();
        }else{
            mensajeDialogo = "Seguro que deseas salir de la aplicacion?";
        }
        btnMiClase.setOnClickListener(e->{
            if(mapLoaded) {
                List<Materia> clases = checkClassesOfToday();
                if (clases != null) {
                    String actualTime = currentHour.format(Calendar.getInstance().getTime());
                    boolean found = false;
                    for (Materia c : clases) {
                        String[] horario = c.getHora().split(" ");
                        horario = horario[1].split("-");
                        int horaInicioClase = -60 + Integer.parseInt(horario[0].split(":")[0]) * 60 + Integer.parseInt(horario[0].split(":")[1]);
                        int finalHoraClase =  Integer.parseInt(horario[1].split(":")[0]) * 60 + Integer.parseInt(horario[1].split(":")[1]);
                        int horaActual = Integer.parseInt(actualTime.split(":")[0]) * 60 + Integer.parseInt(actualTime.split(":")[1]);
                        if (horaActual >= horaInicioClase && horaActual <= finalHoraClase) {
                            found = true;
                            polygons.get(polygonsKeys.get(c.getSalon().split("/")[0]))
                                    .setFillColor(Color.parseColor("#F44336"));
                            //llevar al centro del edificio
                            //se hace zoom al DIA por default
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(new LatLng(centros.get(polygonsKeys.get(c.getSalon().split("/"))).getLatitude(), centros.get(polygonsKeys.get(c.getSalon().split("/"))).getLongitude())) // Sets the new camera position
                                    .zoom(18) // Sets the zoom
                                    .bearing(180) // Rotate the camera
                                    .tilt(30) // Set the camera tilt
                                    .build(); // Creates a CameraPosition from the builder

                            mapboxMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(position), 4000);


                            Handler colorHandler = new Handler();
                            Runnable colorHandlerCode = new Runnable() {
                                @Override
                                public void run() {
                                    polygons.get(polygonsKeys.get(c.getSalon().split("/")[0]))
                                            .setFillColor(Color.parseColor("#4CAF50"));
                                }
                            };
                            colorHandler.postDelayed(colorHandlerCode, 1000 * 60 * 2);
                            break;
                        }
                    }
                    if (!found) {
                        Toast.makeText(getApplicationContext(), "Tienes hora libre, ponte a hacer tarea :) ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Tienes Clases El Dia De Hoy", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Aun no se cargan los edificios.", Toast.LENGTH_SHORT).show();
            }
        });
        btnMiPosicion.setOnClickListener(e -> {
            if (mapLoaded && locationEngine != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location lastLocation = locationEngine.getLastLocation();
                if(lastLocation != null){
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())) // Sets the new camera position
                            .zoom(18) // Sets the zoom
                            .bearing(180) // Rotate the camera
                            .tilt(30) // Set the camera tilt
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 1000);
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo obtener tu posicion actual.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "No se pudo obtener tu posicion actual.", Toast.LENGTH_SHORT).show();
            }
        });
        if(!guest){
            layout = findViewById(R.id.coordinatorLayout);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

            initDrawer();
        }
        //creamos el view que manejara la busqueda de edificios
        buscador = findViewById(R.id.searchSpinner);
        buscador.setOnItemSelectedListener(this);
        ArrayAdapter<String> buscadorData = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, edificios);
        buscadorData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buscador.setAdapter(buscadorData);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void hideDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView drawer = (NavigationView)findViewById(R.id.drawer_layout_id);
        drawer.setVisibility(View.GONE);
    }

    private List<Materia> checkClassesOfToday() {
        if(Sesion.getInstance().getAlumno().getClases().size()>0){
            List<Materia> materias = new LinkedList<>();
            String fecha = currentDayFormat.format(Calendar.getInstance().getTime());
            for(Materia clase : Sesion.getInstance().getAlumno().getClases()){
                if(clase.getHora().split(" ")[0].equals(traducirFecha(fecha))){
                    materias.add(clase);
                }
            }
            return materias;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setLatLngBoundsForCameraTarget(UABC_SAUZAL_BOUNDS);
        mapboxMap.setMaxZoomPreference(14);
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //El componente de location es el que permite usar la localizacion del usuario en el mapa.
        locationComponent.activateLocationComponent(this);
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setRenderMode(RenderMode.COMPASS);
        locationComponent.setCameraMode(CameraMode.NONE);
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);

        //El location engine es el que nos permite conocer las posiciones del usuario.
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        locationEngine.activate();
        //se agrega el locationEngine en el componente de location.
        locationComponent.activateLocationComponent(this,locationEngine);


        for(List<List<LatLng>> points : loader.getEdificios()){
            polygons.add(mapboxMap.addPolygon(new PolygonOptions()
                    .addAll(points.get(0))
                    .fillColor(Color.parseColor("#4CAF50"))
                    .strokeColor(Color.parseColor("#212121"))));
        }
        for(LatLng centro : loader.getCentro_edificios()){
            centros.add(centro);
            markers.add(mapboxMap.addMarker(new MarkerOptions().position(centro).icon(getIcon(loader.getIconos().get(markers.size()))).title(reversePolygonsKeys.get(markers.size()))));
        }

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LocationExpandedDlg dlg = new LocationExpandedDlg(MapActivity.this,marker.getTitle(),polygonsKeys.get(marker.getTitle()));
                dlg.show();
                return true;
            }
        });
        //se hace zoom al DIA por default
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(31.865665,-116.666274)) // Sets the new camera position
                .zoom(22) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 4000);

        mapLoaded = true;
    }

    private Icon getIcon(String s) {
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        int drawable = -1;
        switch (s){
            case "BALLENA":drawable = R.drawable.ballena;break;
            case "BASKET":drawable = R.drawable.basket;break;
            case "BIBLIOTECA":drawable = R.drawable.biblioteca;break;
            case "ESTACIONAMIENTO":drawable = R.drawable.estacionamiento;break;
            case "FUT":drawable = R.drawable.fut;break;
            case "INFO-DIRECCION":drawable = R.drawable.info;break;
            case "LAB":drawable = R.drawable.lab;break;
            case "LABCOMPUTACION":drawable = R.drawable.computacion;break;
            case "LABORATORIO":drawable = R.drawable.quimica;break;
            case "LOCACION":drawable = R.drawable.locacion;break;
            case "MALECON":drawable = R.drawable.malecon;break;
            case "SALONES":drawable = R.drawable.salon;break;
            case "TORTUGAS":drawable = R.drawable.tortuga;break;
            case "VOLLEY":drawable = R.drawable.voley;break;
        }
        return iconFactory.fromResource(drawable);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        if(mapLoaded){
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(centros.get(polygonsKeys.get(item)).getLatitude(), centros.get(polygonsKeys.get(item)).getLongitude())) // Sets the new camera position
                    .zoom(18) // Sets the zoom
                    .bearing(180) // Rotate the camera
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 4000);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                case R.id.califica:startActivity(new Intent(MapActivity.this,CalificarProfesorActivity.class));finish();break;
                case R.id.inicio:startActivity(new Intent(MapActivity.this,NewsFeedActivity.class));finish();break;
                case R.id.perfil:startActivity(new Intent(MapActivity.this,ProfileActivity.class));finish();break;
                case R.id.mapa:Toast.makeText(getApplicationContext(),"Ya estas en el mapa.",Toast.LENGTH_SHORT).show();break;
                case R.id.calificanos:startActivity(new Intent(MapActivity.this,FeedBackActivity.class));finish();break;
                case R.id.cerrar_sesion: {
                    OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarSesion","Estas seguro de que quieres cerrar la sesion actual?",listener);
                    dlg.show();
                    break;
                }
                case R.id.acerca_nosotros:startActivity(new Intent(MapActivity.this,AboutActivity.class));finish();break;
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
                if(!guest)
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //si se recibe una tecla se limpia la pantalla
        super.onKeyDown(keyCode,event);
        if(keyCode == KEYCODE_BACK){
            OptionChooserDialog dlg = new OptionChooserDialog(this,"CerrarAplicacion",mensajeDialogo,listener);
            dlg.show();
        }
        return true;
    }
}
