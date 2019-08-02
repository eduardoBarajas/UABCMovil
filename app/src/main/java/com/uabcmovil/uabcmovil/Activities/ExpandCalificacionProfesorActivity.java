package com.uabcmovil.uabcmovil.Activities;

import android.net.Uri;
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
import com.uabcmovil.uabcmovil.Data.CalificacionesFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.ComentariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioDialog;
import com.uabcmovil.uabcmovil.Dialogs.ComentarioOptionsDialog;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.CalificacionProfesor;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.Entities.ReporteComentario;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.Listeners.RecyclerTouchListener;
import com.uabcmovil.uabcmovil.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ExpandCalificacionProfesorActivity extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Comentarios");
    private DatabaseReference referenciaReportes = database.getReference("ComentariosReportados");
    private DatabaseReference referenciaCalificaciones = database.getReference("CalificacionesProfesores");
    private RecyclerView rv;
    private TextView txtNoComentarios;
    private RatingBar rating;
    private ComentariosFireBaseConnection con;
    private ComentariosRVAdapter adapter;
    private FloatingActionButton btnAgregarComentario;
    private OptionSelectedListener listener;
    private Comentario currentComentario = new Comentario();
    private String profesorKey;
    private CircularImageView imgProfesor;
    private TextView nombreProfesor;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private RatingBar ratingUsuarioProfesor;
    private float currentRating = 0;
    private Button btnCalificar;
    private TextView txtCalificarProfesor;
    private CalificacionesFireBaseConnection califConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = ComentariosFireBaseConnection.getInstance();
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
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                switch (dlgTag){
                    case "EliminarComentario":{
                        currentComentario.setKeyComentario(((String)result));
                        OptionChooserDialog dlg = new OptionChooserDialog(ExpandCalificacionProfesorActivity.this,"DeleteComment","Seguro que deseas eliminar este comentario?",listener);
                        dlg.show();
                        break;
                    }
                    case "EditarComentario":{
                        Comentario comentario = currentComentario;
                        ComentarioDialog dlg = new ComentarioDialog(ExpandCalificacionProfesorActivity.this,"Editar",comentario,listener);
                        dlg.show();
                        break;
                    }
                    case "ReportarComentario":{
                        currentComentario.setKeyComentario(((String)result));
                        OptionChooserDialog dlg = new OptionChooserDialog(ExpandCalificacionProfesorActivity.this,"ReportComment","Seguro que deseas reportar este comentario?",listener);
                        dlg.show();
                        break;
                    }
                    case "ComentarioEditado":{
                        Toast.makeText(getApplicationContext(),"Se edito correctamente el comentario",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "ComentarioAgregado":{
                        Toast.makeText(getApplicationContext(),"Se agrego correctamente el comentario",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "DeleteComment":{
                        if(((String)result).equals("SI")) {
                            referencia.child(currentComentario.getKeyComentario()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(), "Comentario Eliminado", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    }
                    case "ReportComment":{
                        if(((String)result).equals("SI")) {
                            ReporteComentario reporte = new ReporteComentario();
                            reporte.setComentarioReportado(currentComentario.getComentario());
                            reporte.setKeyUsuarioReporto(Sesion.getInstance().getAlumno().getKey());
                            reporte.setUsuarioReportado(currentComentario.getNombreAlumno());
                            reporte.setUsuarioReporto(Sesion.getInstance().getAlumno().getNombre());
                            reporte.setKeyUsuarioReportado(currentComentario.getKeyAlumno());
                            reporte.setComentarioReportadoKey(currentComentario.getKeyComentario());
                            referenciaReportes.child(reporte.getKeyReporte()).setValue(reporte, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(), "Reporte Enviado", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    }
                    case "CalificacionProfesor":{
                        if(((String)result).equals("SI")){
                            CalificacionProfesor calificacion = califConnection.getCalificacion(profesorKey);
                            Map<String,String> nuevaCal = new HashMap<>();
                            nuevaCal.put(Sesion.getInstance().getAlumno().getKey(),String.valueOf(currentRating));
                            if(calificacion!=null){
                                if(calificacion.getKeysAlumnos()!=null){
                                    calificacion.getKeysAlumnos().add(nuevaCal);
                                    float calif = 0;
                                    for(int i=0;i<calificacion.getKeysAlumnos().size();i++)
                                        for(String calUsuario:calificacion.getKeysAlumnos().get(i).values()){
                                            calif += Float.parseFloat(calUsuario);
                                            Log.e("Calificacion2",calUsuario);
                                        }
                                    calificacion.setCalificacion(String.valueOf(calif/calificacion.getKeysAlumnos().size()));
                                }else{
                                    calificacion.setCalificacion(String.valueOf(currentRating));
                                    calificacion.initializeAlumnosArray();
                                    calificacion.getKeysAlumnos().add(nuevaCal);
                                }
                            }else{
                                calificacion = new CalificacionProfesor();
                                calificacion.setKeyProfesor(profesorKey);
                                calificacion.setCalificacion(String.valueOf(currentRating));
                                calificacion.getKeysAlumnos().add(nuevaCal);
                            }
//                            if(calificacion.getKeysAlumnos()==null) {
//                                calificacion.initializeAlumnosArray();
//
//                            }
                            referenciaCalificaciones.child(calificacion.getKey()).setValue(calificacion, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(),"Se califico al profesor",Toast.LENGTH_SHORT).show();
                                    hideRatingBar();
                                }
                            });
                        }
                        break;
                    }
                }
            }
        };
        adapter = new ComentariosRVAdapter(this);
        setContentView(R.layout.expand_calificar_profesor_activity_layout);
        rv = findViewById(R.id.rvComentarios);
        rating = findViewById(R.id.ratingProfesor);
        nombreProfesor = findViewById(R.id.txtNombreProfesorExpand);
        imgProfesor = findViewById(R.id.expandedImage);
        ratingUsuarioProfesor = findViewById(R.id.ratingUserProfesor);
        txtCalificarProfesor = findViewById(R.id.txtCalificarProfesor);
        btnCalificar = findViewById(R.id.btnCalificarDeNuevo);
        txtNoComentarios = findViewById(R.id.NoHayComentarios);
        btnAgregarComentario = findViewById(R.id.btnAgregarNuevoComentario);

        if(getIntent().hasExtra("ProfesorKey")){
            profesorKey = getIntent().getStringExtra("ProfesorKey");
            nombreProfesor.setText(getIntent().getStringExtra("NombreProfesor"));
            imageLoader.displayImage(getIntent().getStringExtra("ImagenProfesor"),imgProfesor,options);
            rating.setRating(Float.parseFloat(getIntent().getStringExtra("RatingProfesor")));
            con.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    adapter.setData(con.getAllComentarios(profesorKey));
                    setRV();
                }
            });
            adapter.setData(con.getAllComentarios(profesorKey));
            setRV();
            btnAgregarComentario.setOnClickListener(e->{
                Comentario comentario = new Comentario();
                comentario.setKeyProfesor(profesorKey);
                ComentarioDialog dlg = new ComentarioDialog(ExpandCalificacionProfesorActivity.this,"Nuevo",comentario,listener);
                dlg.show();
            });
        }
        if(CalificacionesFireBaseConnection.getInstance().isCalificado(Sesion.getInstance().getAlumno().getKey(),profesorKey)){
            hideRatingBar();
        }
        btnCalificar.setOnClickListener(e->{
            CalificacionProfesor calificacionProfesor = CalificacionesFireBaseConnection.getInstance().getCalificacion(profesorKey);
            int index = CalificacionesFireBaseConnection.getInstance().getCalificacionDadaPorUsuarioIndex(profesorKey,Sesion.getInstance().getAlumno().getKey());
            if(index!=-1){
                calificacionProfesor.getKeysAlumnos().remove(index);
            }
            if(calificacionProfesor.getKeysAlumnos()!=null && calificacionProfesor.getKeysAlumnos().size()!=0){
                float calif = 0;
                for(int i=0;i<calificacionProfesor.getKeysAlumnos().size();i++)
                    for(String calUsuario:calificacionProfesor.getKeysAlumnos().get(i).values()){
                        Log.e("Calificacion",calUsuario);
                        calif += Float.parseFloat(calUsuario);
                    }
                calificacionProfesor.setCalificacion(String.valueOf(calif/calificacionProfesor.getKeysAlumnos().size()));
            }else{
                calificacionProfesor.setCalificacion(String.valueOf(0));
            }
            Map<String,Object> dato = new HashMap<>();
            dato.put("calificacion",calificacionProfesor.getCalificacion());
            dato.put("keysAlumnos",calificacionProfesor.getKeysAlumnos());
            referenciaCalificaciones.child(calificacionProfesor.getKey()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    txtCalificarProfesor.setText("Calificar al profesor");
                    ratingUsuarioProfesor.setIsIndicator(false);
                    rating.setRating(Float.parseFloat(calificacionProfesor.getCalificacion()));
                    btnCalificar.setVisibility(View.GONE);
                }
            });
        });

        ratingUsuarioProfesor.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean user) {
                if(user){
                    currentRating = rating;
                    OptionChooserDialog dialog = new OptionChooserDialog(ExpandCalificacionProfesorActivity.this,"CalificacionProfesor","Seguro que quieres dar de calificacion "+ratingBar.getRating()+" Estrellas?",listener);
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
                Comentario comentario = adapter.getComentario(position);
                currentComentario = comentario;
                ComentarioOptionsDialog dlg = new ComentarioOptionsDialog(ExpandCalificacionProfesorActivity.this,comentario,listener);
                dlg.show();
            }
        }));
        califConnection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(CalificacionesFireBaseConnection.getInstance().isCalificado(Sesion.getInstance().getAlumno().getKey(),profesorKey)){
                    hideRatingBar();
                }
                ratingUsuarioProfesor.setRating(Float.parseFloat(califConnection.getCalificacionDadaPorUsuario(profesorKey,Sesion.getInstance().getAlumno().getKey())));
                rating.setRating(Float.parseFloat(califConnection.getCalificacionFromProfesor(profesorKey)));
            }
        });
    }

    private void hideRatingBar() {
        txtCalificarProfesor.setText("Ya haz calificado a este profesor");
        ratingUsuarioProfesor.setIsIndicator(true);
        btnCalificar.setVisibility(View.VISIBLE);
        ratingUsuarioProfesor.setRating(Float.parseFloat(CalificacionesFireBaseConnection.getInstance().getCalificacionDadaPorUsuario(profesorKey,Sesion.getInstance().getAlumno().getKey())));
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
}
