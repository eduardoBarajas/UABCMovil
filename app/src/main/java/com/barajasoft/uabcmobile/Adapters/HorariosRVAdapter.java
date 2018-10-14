package com.barajasoft.uabcmobile.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barajasoft.uabcmobile.Entities.Materia;
import com.barajasoft.uabcmovil.R;

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
        holder.hora.setText(m.getHora());
        //cambiar esto despues
        holder.dia.setText("Lunes");
    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dia, clase, hora;
        public MyViewHolder(View itemView) {
            super(itemView);
            dia = itemView.findViewById(R.id.txtDia);
            clase = itemView.findViewById(R.id.txtMateria);
            hora = itemView.findViewById(R.id.txtHora);
        }
    }

    public void setData(List<Materia> lista){
        listaMaterias = lista;
    }
}
