package com.uabcmovil.uabcmovil.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Entities.Materia;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class HorariosRVAdapter extends RecyclerView.Adapter<HorariosRVAdapter.MyViewHolder>{
    private List<Materia> listaMaterias = new LinkedList<>();

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.materia_horario_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Materia m = listaMaterias.get(position);
        holder.clase.setText(m.getNombreClase());
        holder.hora.setText(m.getHora().split(" ")[1]);
        //cambiar esto despues
        holder.dia.setText(m.getHora().split(" ")[0]);
        if(m.getSalon().contains("/")){
            holder.salon.setText(m.getSalon().split("/")[1]);
        }else{
            holder.salon.setText(m.getSalon());
        }

    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dia, clase, hora, salon;
        public MyViewHolder(View itemView) {
            super(itemView);
            dia = itemView.findViewById(R.id.txtDia);
            clase = itemView.findViewById(R.id.txtMateria);
            hora = itemView.findViewById(R.id.txtHora);
            salon = itemView.findViewById(R.id.txtSalon);
        }
    }

    public void setData(List<Materia> lista){
        listaMaterias = lista;
    }
}
