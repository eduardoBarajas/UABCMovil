package com.uabcmovil.uabcmovil.Adapters;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uabcmovil.uabcmovil.Activities.NewsExpandedActivity;
import com.uabcmovil.uabcmovil.Entities.Noticia;
import com.uabcmovil.uabcmovil.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.MyViewHolder>{
    private List<Noticia> noticias = new LinkedList<>();
    private Noticia most_recent = null;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Activity activity;

    public NewsRVAdapter(Activity context){
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.imagen_no_disponible) // resource or drawable
                .showImageForEmptyUri(R.drawable.imagen_no_disponible) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        activity = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);
        holder.txtCategoria.setText(noticia.getCategoria());
        holder.txtTitle.setText(noticia.getTitulo());
        imageLoader.displayImage(noticia.getImagen(),holder.news_model_img,options);
        holder.btnVerMas.setOnClickListener(e->{
            Intent intent = new Intent(activity, NewsExpandedActivity.class);
            String[] n = new String[]{noticia.getImagen(),noticia.getTitulo(),noticia.getNoticia(),noticia.getCategoria(),noticia.getFecha(),noticia.getKey()};
            intent.putExtra("Noticia",n);
            Pair<View, String> p1 = Pair.create(holder.news_model_img, "FOTO_NOTICIA");
            Pair<View, String> p2 = Pair.create(holder.txtTitle, "TITULO_NOTICIA");
            //las opciones que se ocupan para la transicion son la actividad, el view que hara transicion (imageview) y el nombre que se puso de share transition
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,img,"iconoArchivo");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,p1,p2);
            activity.startActivity(intent,options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView news_model_img;
        public TextView txtTitle, txtCategoria;
        public Button btnVerMas;
        public MyViewHolder(View itemView) {
            super(itemView);
            news_model_img = itemView.findViewById(R.id.news_model_img);
            txtTitle = itemView.findViewById(R.id.txtTitleNewsModel);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaNewsModel);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);
        }
    }

    public void setData(List<Noticia> lista){
        noticias = lista;
    }
}