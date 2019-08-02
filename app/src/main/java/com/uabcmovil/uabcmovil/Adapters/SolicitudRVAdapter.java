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
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Activities.ExpandSolicitudActivity;
import com.uabcmovil.uabcmovil.Entities.Solicitud;
import com.uabcmovil.uabcmovil.Fragments.SolicitudesAceptadasFragment;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class SolicitudRVAdapter extends RecyclerView.Adapter<SolicitudRVAdapter.MyViewHolder> {
    private List<Solicitud> solicitudes = new LinkedList<>();
    private Activity activity;

    public SolicitudRVAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public SolicitudRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.solicitudes_model_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudRVAdapter.MyViewHolder holder, int position) {
        Solicitud solicitud = solicitudes.get(position);
        holder.txtProfesor.setText(solicitud.getNombre());
        holder.txtfechaSolicitud.setText(solicitud.getFecha()+"  "+solicitud.getHora());
        holder.txtUbicacion.setText(solicitud.getEdificio()+"  "+solicitud.getLaboratorio());
        holder.btnVerMasSolicitud.setOnClickListener(e->{
            Intent intent = new Intent(activity, ExpandSolicitudActivity.class);
            intent.putExtra("Solicitud",solicitudToStringArray(solicitud));
            activity.startActivity(intent);
        });
    }

    private String[] solicitudToStringArray(Solicitud solicitud) {
        return new String[]{solicitud.getEstado(),solicitud.getComentarios(),solicitud.getEdificio(),
        solicitud.getFecha(),solicitud.getHora(),solicitud.getKey(),solicitud.getLaboratorio(),
        solicitud.getMateria(),solicitud.getNombre(),solicitud.getRechazo()};
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProfesor,txtUbicacion,txtfechaSolicitud;
        public Button btnVerMasSolicitud;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtProfesor = itemView.findViewById(R.id.nombreProfesorSolicitud);
            txtUbicacion = itemView.findViewById(R.id.ubicacionSolicitud);
            txtfechaSolicitud = itemView.findViewById(R.id.fechaSolicitud);
            btnVerMasSolicitud = itemView.findViewById(R.id.btnVerMasSolicitud);
        }
    }

    public void setData(List<Solicitud> lista){
        solicitudes = lista;
    }
}
