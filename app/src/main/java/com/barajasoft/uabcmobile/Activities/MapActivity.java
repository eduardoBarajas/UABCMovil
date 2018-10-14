package com.barajasoft.uabcmobile.Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.barajasoft.uabcmobile.Dialogs.LocationExpandedDlg;
import com.barajasoft.uabcmovil.Helpers.EdificiosLoader;
import com.barajasoft.uabcmovil.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private static final LatLngBounds UABC_SAUZAL_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(31.866440,  -116.668511))
            .include(new LatLng(31.866626,  -116.665711))
            .include(new LatLng(31.863114,  -116.665351))
            .include(new LatLng(31.862909,  -116.668607))
            .build();

    //En la lista de polygons se almacenan todos los poligonos que se van a agregar al mapa
    private List<com.mapbox.mapboxsdk.annotations.Polygon> polygons = new LinkedList<com.mapbox.mapboxsdk.annotations.Polygon>();
    //en polygons key guardo una referencia de acuerdo a la posicion donde esta el poligono del edificio
    private Map<String,Integer> polygonsKeys = new HashMap<>();
    private Map<Integer,String> reversePolygonsKeys = new HashMap<>();
    private List<Marker> markers = new LinkedList<>();
    private ArrayList<String> edificios = new ArrayList<>();
    private ArrayList<LatLng> centros = new ArrayList<>();


    private EdificiosLoader loader;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private AppCompatSpinner buscador;
    private boolean mapLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new EdificiosLoader(getApplicationContext());
        loader.loadJson();
        loader.parseJson();
        int index = 0;
        for(String key : loader.getEdificiosKeys()){
            edificios.add(key);
            polygonsKeys.put(key,index);
            reversePolygonsKeys.put(index,key);
            index++;
        }

        Mapbox.getInstance(this, "pk.eyJ1IjoiZWR1MjMiLCJhIjoiY2psa2lpNTRzMG43azNrbzU2emYxaThtNiJ9.j20WpkbxDZbaszbFUToebg");
        setContentView(R.layout.map_activity_layout);

        //creamos el view que manejara la busqueda de edificios
        buscador = findViewById(R.id.searchSpinner);
        buscador.setOnItemSelectedListener(this);
        ArrayAdapter<String> buscadorData = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,edificios);
        buscadorData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buscador.setAdapter(buscadorData);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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
        mapboxMap.setMinZoomPreference(14);
        for(List<List<LatLng>> points : loader.getEdificios()){
            polygons.add(mapboxMap.addPolygon(new PolygonOptions()
                    .addAll(points.get(0))
                    .fillColor(Color.parseColor("#4CAF50"))
                    .strokeColor(Color.parseColor("#212121"))));
        }
        for(LatLng centro : loader.getCentro_edificios()){
            centros.add(centro);
            markers.add(mapboxMap.addMarker(new MarkerOptions().position(centro).title(reversePolygonsKeys.get(markers.size()))));
        }

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LocationExpandedDlg dlg = new LocationExpandedDlg(MapActivity.this,marker.getTitle());
                dlg.show();
                return true;
            }
        });
        mapLoaded = true;
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
}
