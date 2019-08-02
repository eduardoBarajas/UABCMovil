package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Activities.CalificarProfesorActivity;
import com.uabcmovil.uabcmovil.Activities.ExpandCalificacionProfesorActivity;
import com.uabcmovil.uabcmovil.Data.CalificacionesFireBaseConnection;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class ProfesoresRVAdapter extends RecyclerView.Adapter<ProfesoresRVAdapter.MyViewHolder> {
    private List<Alumno> profesores = new LinkedList<>();
    private Activity activity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public ProfesoresRVAdapter(Activity activity) {
        this.activity = activity;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.imagen_no_disponible) // resource or drawable
                .showImageForEmptyUri(R.drawable.imagen_no_disponible) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
    }

    @NonNull
    @Override
    public ProfesoresRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profesor_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesoresRVAdapter.MyViewHolder holder, int position) {
        Alumno profesor = profesores.get(position);
        holder.nombre.setText(profesor.getNombre());
        holder.rating.setIsIndicator(true);
        holder.rating.setRating(Float.parseFloat(CalificacionesFireBaseConnection.getInstance().getCalificacionFromProfesor(profesor.getKey())));
        holder.btn.setOnClickListener(e->{
            Intent intent = new Intent(activity, ExpandCalificacionProfesorActivity.class);
            intent.putExtra("ProfesorKey",profesor.getKey());
            intent.putExtra("ImagenProfesor",profesor.getProfilePic());
            intent.putExtra("NombreProfesor",profesor.getNombre());
            intent.putExtra("RatingProfesor",String.valueOf(holder.rating.getRating()));
            activity.startActivity(intent);
        });
        imageLoader.displayImage(profesor.getProfilePic(),holder.img,options);
    }

    @Override
    public int getItemCount() {
        return profesores.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView nombre;
        public RatingBar rating;
        public Button btn;
        public MyViewHolder(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnVerMasProfesor);
            rating = itemView.findViewById(R.id.ratingProfesorRV);
            nombre = itemView.findViewById(R.id.txtNameProfesor);
            img = itemView.findViewById(R.id.imgProfileProfesor);
        }
    }

    public void setData(List<Alumno> profesores){
        this.profesores = profesores;
    }
}
