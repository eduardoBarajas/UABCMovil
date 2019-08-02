package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Entities.Reseña;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class ReseñasRVAdapter extends RecyclerView.Adapter<ReseñasRVAdapter.MyViewHolder>{
    private List<Reseña> reseñaList = new LinkedList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public ReseñasRVAdapter(Activity activity){
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.resenias_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reseña reseña = reseñaList.get(position);
        holder.reseña.setText(reseña.getReseña());
        holder.fecha.setText(reseña.getFecha());
        holder.nombre.setText(reseña.getNombre());
        holder.rol.setText(reseña.getRol());
        holder.rating.setRating(Float.parseFloat(reseña.getCalificacion()));
        if(UsuariosFireBaseConnection.getInstance().getUserByKey(reseña.getKeyUsuario())!=null){
            imageLoader.displayImage(UsuariosFireBaseConnection.getInstance().getUserByKey(reseña.getKeyUsuario()).getProfilePic(),holder.profilePic,options);
        }
    }

    @Override
    public int getItemCount() {
        return reseñaList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre,fecha,reseña,rol;
        public CircularImageView profilePic;
        public RatingBar rating;
        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imgProfileReseña);
            nombre = itemView.findViewById(R.id.txtNombreReseña);
            fecha = itemView.findViewById(R.id.txtFechaReseña);
            reseña = itemView.findViewById(R.id.txtReseña);
            rol = itemView.findViewById(R.id.txtRolReseña);
            rating = itemView.findViewById(R.id.ratingBarReseña);
            rating.setIsIndicator(true);
        }
    }

    public void setData(List<Reseña> reseñas ){
        this.reseñaList = reseñas;
    }

    public Reseña getReseña(int pos){
        return reseñaList.get(pos);
    }
}