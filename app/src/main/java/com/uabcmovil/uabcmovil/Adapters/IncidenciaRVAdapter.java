package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.util.NumberUtils;
import com.uabcmovil.uabcmovil.Activities.ExpandIncidenciaActivity;
import com.uabcmovil.uabcmovil.Activities.ExpandSolicitudActivity;
import com.uabcmovil.uabcmovil.Entities.Incidencia;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IncidenciaRVAdapter extends RecyclerView.Adapter<IncidenciaRVAdapter.MyViewHolder> {
    private List<Incidencia> incidencias = new LinkedList<>();
//    private String[] colors = new String[]{"#3F51B5","#448AFF","#8BC34A","#03A9F4",
//            "#7C4DFF","#009688","#FF9800","#FF5722","#607D8B","#795548","#E040FB","#F44336"};
    private Activity activity;
    //private Random rand = new Random();

    public IncidenciaRVAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.incidencias_model_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Incidencia incidencia = incidencias.get(position);
        holder.nombreProfesorIncidencia.setText(incidencia.getProfesor());
        holder.ubicacionIncidencia.setText(incidencia.getEdificio()+"  "+incidencia.getLaboratorio());
        holder.btnVerMasIncidencia.setOnClickListener(e->{
            Intent intent = new Intent(activity, ExpandIncidenciaActivity.class);
            intent.putExtra("Incidencia",incidenciaToStringArray(incidencia));
            activity.startActivity(intent);
        });
        //holder.view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors[rand.nextInt(11)])));
    }

    @Override
    public int getItemCount() {
        return incidencias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreProfesorIncidencia, ubicacionIncidencia;
        public Button btnVerMasIncidencia;
        //public View view;
        public MyViewHolder(View itemView) {
            super(itemView);
            //view = itemView;
            nombreProfesorIncidencia = itemView.findViewById(R.id.nombreProfesorIncidencia);
            ubicacionIncidencia = itemView.findViewById(R.id.ubicacionIncidencia);
            btnVerMasIncidencia = itemView.findViewById(R.id.btnVerMasIncidencia);
        }
    }
    public void setData(List<Incidencia> lista){
        incidencias = lista;
    }
    private String[] incidenciaToStringArray(Incidencia inc){
        return new String[]{inc.getEstado(),inc.getProfesor(),inc.getFecha(),
        inc.getLaboratorio(),inc.getEdificio(),inc.getId_equipo(),inc.getIncidencia(),
        inc.getRespuesta(),inc.getKey()};
    }
}
