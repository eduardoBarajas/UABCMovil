package com.uabcmovil.uabcmovil.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Adapters.IncidenciaRVAdapter;
import com.uabcmovil.uabcmovil.Data.IncidenciasFireBaseConnection;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

public class IncidenciasAtendidasFragment extends Fragment {
    public IncidenciasAtendidasFragment(){}

    private Fragment this_fragment;
    private IncidenciaRVAdapter adapter;
    private ImageView imageView;
    private RecyclerView rv;
    private TextView txt;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this_fragment = this;
        adapter = new IncidenciaRVAdapter(this.getActivity());
        IncidenciasFireBaseConnection connection = IncidenciasFireBaseConnection.getInstance();
        connection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(((String)o).equals("Se Modifico")){
                    if(Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                        adapter.setData(connection.getAllIncidencias("Atendida"));
                    if(Sesion.getInstance().getAlumno().getRol().equals("Profesor"))
                        adapter.setData(connection.getAllIncidencias("Atendida",Sesion.getInstance().getAlumno().getNombre()));
                    adapter.notifyDataSetChanged();
                    if(rv!=null)
                        setRV();
                    if(getFragmentManager()!=null){
                        if(!getFragmentManager().isStateSaved())
                            getFragmentManager().beginTransaction().detach(this_fragment).attach(this_fragment).commit();
                    }
                }
            }
        });
        if(Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
            adapter.setData(connection.getAllIncidencias("Atendida"));
        if(Sesion.getInstance().getAlumno().getRol().equals("Profesor"))
            adapter.setData(connection.getAllIncidencias("Atendida",Sesion.getInstance().getAlumno().getNombre()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incidencias_atendidas_fragment_layout,container,false);
        imageView = view.findViewById(R.id.imageViewIncidenciasAtendidas);
        rv = view.findViewById(R.id.rvIncidenciasAtendidas);
        txt = view.findViewById(R.id.textViewIncidenciasAtendidas);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        setRV();
        return view;
    }

    private void setRV(){
        if(adapter.getItemCount()==0){
            rv.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }else{
            rv.setVisibility(View.VISIBLE);
            txt.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            rv.setAdapter(adapter);
        }
    }
}