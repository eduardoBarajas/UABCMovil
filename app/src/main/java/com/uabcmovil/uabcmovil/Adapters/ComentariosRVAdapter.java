package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.Entities.Comentario;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class ComentariosRVAdapter extends RecyclerView.Adapter<ComentariosRVAdapter.MyViewHolder>{
    private List<Comentario> comentarios = new LinkedList<>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public ComentariosRVAdapter(Activity activity){
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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comentarios_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.comentario.setText(comentario.getComentario());
        holder.nombre.setText(comentario.getNombreAlumno());
        holder.fecha.setText(comentario.getFechaComentario());
        if(UsuariosFireBaseConnection.getInstance().getUserByKey(comentario.getKeyAlumno())!=null){
            imageLoader.displayImage(UsuariosFireBaseConnection.getInstance().getUserByKey(comentario.getKeyAlumno()).getProfilePic(),holder.profilePic,options);
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre,fecha,comentario;
        public CircularImageView profilePic;
        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imgProfileComentario);
            nombre = itemView.findViewById(R.id.txtNombreComentario);
            fecha = itemView.findViewById(R.id.txtFechaComentario);
            comentario = itemView.findViewById(R.id.txtComentario);
        }
    }

    public void setData(List<Comentario> comentarios ){
        this.comentarios = comentarios;
    }

    public Comentario getComentario(int pos){
        return comentarios.get(pos);
    }
}
