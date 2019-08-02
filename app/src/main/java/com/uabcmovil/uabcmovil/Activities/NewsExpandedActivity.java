package com.uabcmovil.uabcmovil.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Noticia;
import com.uabcmovil.uabcmovil.R;

public class NewsExpandedActivity extends AppCompatActivity {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Noticias");
    private Noticia n;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        if(getIntent().hasExtra("Noticia")){
            String[] noticia = getIntent().getStringArrayExtra("Noticia");
            n = new Noticia();
            n.setImagen(noticia[0]);
            n.setTitulo(noticia[1]);
            n.setNoticia(noticia[2]);
            n.setCategoria(noticia[3]);
            n.setFecha(noticia[4]);
            n.setKey(noticia[5]);
        }
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

        setContentView(R.layout.expanded_news_activity_layout);
        ImageView img = findViewById(R.id.imageExpanded);
        TextView titulo = findViewById(R.id.TitleExpanded);
        TextView categoria = findViewById(R.id.CategoriaExpanded);
        TextView fecha = findViewById(R.id.FechaExpanded);
        TextView noticia = findViewById(R.id.NoticiaExpanded);
        Button btnEliminar = findViewById(R.id.btnEliminarNoticia);
        if(!Sesion.getInstance().getAlumno().getRol().equals("Administrador"))
            btnEliminar.setVisibility(View.GONE);
        imageLoader.displayImage(n.getImagen(),img,options);
        titulo.setText(n.getTitulo());
        categoria.setText(n.getCategoria());
        fecha.setText(n.getFecha());
        noticia.setText(n.getNoticia());
        btnEliminar.setOnClickListener(e->{
            referencia.child(n.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    storageReference.child(n.getKey()+"/img.jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Se elimino la noticia.",Toast.LENGTH_SHORT).show();
                    }
                });
                }
            });
            finish();
        });
    }
}
