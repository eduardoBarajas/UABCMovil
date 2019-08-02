package com.uabcmovil.uabcmovil.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Entities.ReporteComentario;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class ComentariosReportadosRVAdapter extends RecyclerView.Adapter<ComentariosReportadosRVAdapter.MyViewHolder> {
    private List<ReporteComentario> comentarios = new LinkedList<>();
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comentario_reportado_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ReporteComentario comentario = comentarios.get(position);
        holder.nombreReportador.setText(comentario.getUsuarioReporto());
        holder.nombreReportado.setText(comentario.getUsuarioReportado());
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreReportado, nombreReportador;
        public MyViewHolder(View itemView) {
            super(itemView);
            nombreReportado = itemView.findViewById(R.id.userReportado);
            nombreReportador = itemView.findViewById(R.id.userQueReporto);
        }
    }

    public void setData(List<ReporteComentario> lista){comentarios = lista;}
    public ReporteComentario getComentario(int pos){return comentarios.get(pos);}
}
