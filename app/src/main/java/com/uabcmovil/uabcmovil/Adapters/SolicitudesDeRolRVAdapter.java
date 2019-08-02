package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Entities.Solicitud;
import com.uabcmovil.uabcmovil.Entities.SolicitudRol;
import com.uabcmovil.uabcmovil.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SolicitudesDeRolRVAdapter extends RecyclerView.Adapter<SolicitudesDeRolRVAdapter.MyViewHolder> {
    private List<SolicitudRol> solicitudes = new LinkedList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private DatabaseReference referenciaSolicitudes = database.getReference("SolicitudesAsignacionesRoles");
    private Activity activity;


    public SolicitudesDeRolRVAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public SolicitudesDeRolRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.solicitud_rol_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudesDeRolRVAdapter.MyViewHolder holder, int position) {
        SolicitudRol solicitudRol = solicitudes.get(position);
        holder.correo.setText(solicitudRol.getCorreo());
        holder.nombre.setText(solicitudRol.getNombre());
        holder.rol.setText(solicitudRol.getRol_solicitdado());
        holder.aprobar.setOnClickListener(e->{
            Map<String,Object> dato = new HashMap<>();
            dato.put("rol",solicitudRol.getRol_solicitdado());
            referencia.child(solicitudRol.getKeyUsuario()).updateChildren(dato, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    referenciaSolicitudes.child(solicitudRol.getKeySolicitud()).removeValue();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre,correo,rol;
        public Button aprobar;
        public MyViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_usuario_solicitud_rol);
            correo = itemView.findViewById(R.id.correo_usuario_solicitud_rol);
            rol = itemView.findViewById(R.id.rol_usuario_solicitud_rol);
            aprobar = itemView.findViewById(R.id.btnAprobarSolicitudRol);
        }
    }

    public void setData(List<SolicitudRol> solicitudes){this.solicitudes = solicitudes;}

}
