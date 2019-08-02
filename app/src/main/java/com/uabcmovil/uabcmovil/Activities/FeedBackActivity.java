package com.uabcmovil.uabcmovil.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Adapters.ComentariosRVAdapter;
import com.uabcmovil.uabcmovil.Adapters.ReseñasRVAdapter;
import com.uabcmovil.uabcmovil.Data.CalificacionesAppFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.CalificacionesFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.ComentariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.ReseñasFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioDialog;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioOptionsDialog;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.CalificacionApp;
import com.uabcmovil.uabcmovil.Entities.CalificacionProfesor;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.Entities.ReporteComentario;
import com.uabcmovil.uabcmovil.Entities.Reseña;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.Listeners.RecyclerTouchListener;
import com.uabcmovil.uabcmovil.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class FeedBackActivity extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Reseñas");
    private DatabaseReference referenciaCalificaciones = database.getReference("CalificacionesApp");
    private RecyclerView rv;
    private TextView txtNoReseñas;
    private RatingBar rating;
    private ReseñasFireBaseConnection con;
    private ReseñasRVAdapter adapter;
    private FloatingActionButton btnAgregarReseña;
    private OptionSelectedListener listener;
    private Reseña currentReseña = new Reseña();
    private String profesorKey;
    private CircularImageView imgApp;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private RatingBar ratingUsuarioApp;
    private float currentRating = 0;
    private Button btnCalificar;
    private TextView txtCalificarApp;
    private CalificacionesAppFireBaseConnection califConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = ReseñasFireBaseConnection.getInstance();
        califConnection = CalificacionesAppFireBaseConnection.getInstance();
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
                    case "EliminarReseña":{
                        currentReseña.setKey(((String)result).split(":")[0]);
                        currentReseña.setKeyUsuario(((String)result).split(":")[1]);
                        OptionChooserDialog dlg = new OptionChooserDialog(FeedBackActivity.this,"DeleteReseña","Seguro que deseas eliminar esta Reseña?",listener);
                        dlg.show();
                        break;
                    }
                    case "EditarReseña":{
                        if(((String)result).equals("SI")) {
                            ComentarioDialog dlg = new ComentarioDialog(FeedBackActivity.this,"Editar",currentReseña,listener);
                            dlg.show();
                        }
                        break;
                    }
                    case "ReseñaEditada":{
                        if (((String) result).equals("SI")) {
                            Toast.makeText(getApplicationContext(),"La reseña fue editada",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case "ReseñaAgregada":{
                        Toast.makeText(getApplicationContext(),"Se agrego correctamente la reseña",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "DeleteReseña":{
                        if(((String)result).equals("SI")) {
                            referencia.child(currentReseña.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(), "Reseña Eliminado", Toast.LENGTH_SHORT).show();
                                    resetCalificacion(CalificacionesAppFireBaseConnection.getInstance().getCalificacion().getCalificacion());
                                }
                            });
                        }
                        break;
                    }
                    case "CalificacionApp":{
                        if(((String)result).equals("SI")){
                            CalificacionApp calificacion = califConnection.getCalificacion();
                            Map<String,String> nuevaCal = new HashMap<>();
                            nuevaCal.put(Sesion.getInstance().getAlumno().getKey(),String.valueOf(currentRating));
                            if(calificacion!=null){
                                if(calificacion.getKeysUsuarios()!=null){
                                    calificacion.getKeysUsuarios().add(nuevaCal);
                                    float calif = 0;
                                    for(int i=0;i<calificacion.getKeysUsuarios().size();i++)
                                        for(String calUsuario:calificacion.getKeysUsuarios().get(i).values()){
                                            calif += Float.parseFloat(calUsuario);
                                        }
                                    calificacion.setCalificacion(String.valueOf(calif/calificacion.getKeysUsuarios().size()));
                                }else{
                                    calificacion.setCalificacion(String.valueOf(currentRating));
                                    calificacion.initializeAlumnosArray();
                                    calificacion.getKeysUsuarios().add(nuevaCal);
                                }
                            }else{
                                calificacion = new CalificacionApp();
                                calificacion.setCalificacion(String.valueOf(currentRating));
                                calificacion.getKeysUsuarios().add(nuevaCal);
                            }
                            referenciaCalificaciones.child(calificacion.getKey()).setValue(calificacion, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(),"Se califico la app",Toast.LENGTH_SHORT).show();
                                    hideRatingBar();
                                    con.updateCalificacionReseña(Sesion.getInstance().getAlumno().getKey(),String.valueOf(currentRating));
                                }
                            });
                        }
                        break;
                    }
                }
            }
        };
        adapter = new ReseñasRVAdapter(this);
        setContentView(R.layout.feedback_activity_layout);
        rv = findViewById(R.id.rvReseñas);
        txtCalificarApp = findViewById(R.id.txtMsgCalificar);
        rating = findViewById(R.id.ratingApp);
        ratingUsuarioApp = findViewById(R.id.ratingUserApp);
        btnCalificar = findViewById(R.id.btnCalificarDeNuevo);
        txtNoReseñas = findViewById(R.id.NoHayReseñas);
        btnAgregarReseña = findViewById(R.id.btnAgregarNuevaReseña);
        califConnection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                CalificacionApp calificacionApp = califConnection.getCalificacion();
                if(CalificacionesAppFireBaseConnection.getInstance().isCalificado(Sesion.getInstance().getAlumno().getKey())){
                    hideRatingBar();
                }
                ratingUsuarioApp.setRating(Float.parseFloat(califConnection.getCalificacionDadaPorUsuario(Sesion.getInstance().getAlumno().getKey())));
                rating.setRating(Float.parseFloat(calificacionApp.getCalificacion()));
            }
        });
        con.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                adapter.setData(con.getAllReseñas());
                setRV();
            }
        });
        adapter.setData(con.getAllReseñas());
        setRV();

        if(CalificacionesAppFireBaseConnection.getInstance().isCalificado(Sesion.getInstance().getAlumno().getKey())){
            hideRatingBar();
        }
        btnCalificar.setOnClickListener(e->{
            CalificacionApp calificacionApp = CalificacionesAppFireBaseConnection.getInstance().getCalificacion();
            int index = CalificacionesAppFireBaseConnection.getInstance().getCalificacionDadaPorUsuarioIndex(Sesion.getInstance().getAlumno().getKey());
            if(index!=-1){
                calificacionApp.getKeysUsuarios().remove(index);
            }
            if(calificacionApp.getKeysUsuarios()!=null && calificacionApp.getKeysUsuarios().size()!=0){
                float calif = 0;
                for(int i=0;i<calificacionApp.getKeysUsuarios().size();i++)
                    for(String calUsuario:calificacionApp.getKeysUsuarios().get(i).values()){
                        Log.e("Calificacion",calUsuario);
                        calif += Float.parseFloat(calUsuario);
                    }
                calificacionApp.setCalificacion(String.valueOf(calif/calificacionApp.getKeysUsuarios().size()));
            }else{
                calificacionApp.setCalificacion(String.valueOf(0));
            }
            Map<String,Object> dato = new HashMap<>();
            dato.put("calificacion",calificacionApp.getCalificacion());
            dato.put("keysUsuarios",calificacionApp.getKeysUsuarios());
            referenciaCalificaciones.child(calificacionApp.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    resetCalificacion(CalificacionesAppFireBaseConnection.getInstance().getCalificacion().getCalificacion());
                }
            });
        });

        ratingUsuarioApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean user) {
                if(user){
                    currentRating = rating;
                    OptionChooserDialog dialog = new OptionChooserDialog(FeedBackActivity.this,"CalificacionApp","Seguro que quieres dar de calificacion "+ratingBar.getRating()+" Estrellas?",listener);
                    dialog.show();
                }
            }
        });
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnItemTouchListener(new RecyclerTouchListener(this,rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) { }
            @Override
            public void onLongClick(View view, int position) {
                Reseña reseña = adapter.getReseña(position);
                currentReseña = reseña;
                if(reseña.getKeyUsuario().equals(Sesion.getInstance().getAlumno().getKey())||Sesion.getInstance().getAlumno().getRol().equals("Administrador")){
                    ComentarioOptionsDialog dlg = new ComentarioOptionsDialog(FeedBackActivity.this,reseña,listener);
                    dlg.show();
                }
            }
        }));

        btnAgregarReseña.setOnClickListener(e->{
            if(!con.noHaCreadoReseña(Sesion.getInstance().getAlumno().getKey())){
                if(califConnection.isCalificado(Sesion.getInstance().getAlumno().getKey())){
                    Reseña reseña = new Reseña();
                    reseña.setKeyUsuario(Sesion.getInstance().getAlumno().getKey());
                    reseña.setCalificacion(califConnection.getCalificacionDadaPorUsuario(reseña.getKeyUsuario()));
                    reseña.setNombre(Sesion.getInstance().getAlumno().getNombre());
                    reseña.setRol(Sesion.getInstance().getAlumno().getRol());
                    ComentarioDialog dlg = new ComentarioDialog(FeedBackActivity.this,"Nuevo",reseña,listener);
                    dlg.show();
                }else{
                    Toast.makeText(getApplicationContext(),"Primero debes calificar la app antes de dejar una reseña",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Solo puedes tener una reseña a la vez",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetCalificacion(String calificacion) {
        txtCalificarApp.setText("Califica la app");
        ratingUsuarioApp.setIsIndicator(false);
        ratingUsuarioApp.setRating(0);
        rating.setRating(Float.parseFloat(calificacion));
        btnCalificar.setVisibility(View.GONE);
        con.resetCalificacionEnReseña(currentReseña.getKeyUsuario());
        califConnection.deleteCalificacionPorUsuario(califConnection.getCalificacion().getKey(),currentReseña.getKeyUsuario());
    }

    private void hideRatingBar() {
        txtCalificarApp.setText("Ya calificaste la app");
        ratingUsuarioApp.setIsIndicator(true);
        btnCalificar.setVisibility(View.VISIBLE);
        ratingUsuarioApp.setRating(Float.parseFloat(CalificacionesAppFireBaseConnection.getInstance().getCalificacionDadaPorUsuario(Sesion.getInstance().getAlumno().getKey())));
    }

    private void setRV(){
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0){
            rv.setVisibility(View.GONE);
            txtNoReseñas.setVisibility(View.VISIBLE);
        }else{
            rv.setVisibility(View.VISIBLE);
            txtNoReseñas.setVisibility(View.GONE);
            rv.setAdapter(adapter);
        }
    }
}
