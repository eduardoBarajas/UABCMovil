package com.uabcmovil.uabcmovil.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uabcmovil.uabcmovil.Dialogs.EditUserDialog;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class ControlDeUsuariosRVAdapter extends RecyclerView.Adapter<ControlDeUsuariosRVAdapter.MyViewHolder> {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Alumnos");
    private List<Alumno> usuarios = new LinkedList<>();
    private Activity activity;
    private OptionSelectedListener listener;
    private String currentUsuario;

    public ControlDeUsuariosRVAdapter(Activity activity){
        this.activity = activity;
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(dlgTag.equals("Eliminar Usuario")){
                    if(((String)result).equals("SI")){
                        referencia.child(currentUsuario).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(activity,"Se elimino al usuario",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public ControlDeUsuariosRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.control_usuarios_model,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ControlDeUsuariosRVAdapter.MyViewHolder holder, int position) {
        Alumno usuario = usuarios.get(position);
        holder.txtMatricula.setText(usuario.getMatricula());
        holder.txtCorreo.setText(usuario.getUser());
        holder.txtCarrera.setText(usuario.getCarrera());
        holder.txtNombre.setText(usuario.getNombre());
        holder.txtRol.setText(usuario.getRol());
        holder.btnEliminar.setOnClickListener(e->{
            currentUsuario = usuario.getKey();
            OptionChooserDialog dlg = new OptionChooserDialog(activity,"Eliminar Usuario","Estas seguro de eliminar a este usuario?",listener);
            dlg.show();
        });
        holder.btnEditar.setOnClickListener(e->{
            EditUserDialog dlg = new EditUserDialog(activity,usuario);
            dlg.show();
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtNombre,txtCarrera, txtMatricula, txtRol,txtCorreo;
        public Button btnEditar, btnEliminar;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.nombre_usuario_control_usuario);
            txtCarrera = itemView.findViewById(R.id.carrera_control_usuario);
            txtMatricula = itemView.findViewById(R.id.matricula_control_usuario);
            txtCorreo = itemView.findViewById(R.id.correo_usuario_control_usuario);
            btnEditar = itemView.findViewById(R.id.btnEditarControlUsuarios);
            btnEliminar = itemView.findViewById(R.id.btnEliminarControlUsuarios);
            txtRol = itemView.findViewById(R.id.rol_usuario_control_usuario);
        }
    }

    public void setData(List<Alumno> alumnos){
        this.usuarios = alumnos;
    }
}
