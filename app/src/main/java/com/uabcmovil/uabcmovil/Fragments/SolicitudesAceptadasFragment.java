package com.uabcmovil.uabcmovil.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Adapters.SolicitudRVAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Data.SolicitudesFireBaseConnection;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

public class SolicitudesAceptadasFragment extends Fragment {
    public SolicitudesAceptadasFragment(){}

    private ImageView imgNoSolicitudes;
    private TextView txtNoSolicitudes;
    private RecyclerView rvSolicitudes;
    private SolicitudRVAdapter adapter;
    private Fragment this_fragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this_fragment = this;
        adapter = new SolicitudRVAdapter(this.getActivity());
        SolicitudesFireBaseConnection connection = SolicitudesFireBaseConnection.getInstance();
        connection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(((String)o).equals("Se Modifico")){
                    if(Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
                        adapter.setData(connection.getAllSolicitudes("Aceptada"));
                    if(Sesion.getInstance().getAlumno().getRol().equals("Profesor"))
                        adapter.setData(connection.getAllSolicitudes("Aceptada",Sesion.getInstance().getAlumno().getNombre()));
                    adapter.notifyDataSetChanged();
                    if(rvSolicitudes!=null)
                        setRV();
                    if(getFragmentManager()!=null){
                        if(!getFragmentManager().isStateSaved())
                            getFragmentManager().beginTransaction().detach(this_fragment).attach(this_fragment).commit();
                    }
                }
            }
        });
        if(Sesion.getInstance().getAlumno().getRol().equals("AdminLab"))
            adapter.setData(connection.getAllSolicitudes("Aceptada"));
        if(Sesion.getInstance().getAlumno().getRol().equals("Profesor"))
            adapter.setData(connection.getAllSolicitudes("Aceptada",Sesion.getInstance().getAlumno().getNombre()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.solicitudes_aceptadas_fragment_layout,container,false);
        imgNoSolicitudes = view.findViewById(R.id.imageViewSolicitudesAceptadas);
        txtNoSolicitudes = view.findViewById(R.id.textViewSolicitudesAceptadas);
        rvSolicitudes = view.findViewById(R.id.rvSolicitudesAceptadas);
        rvSolicitudes.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        rvSolicitudes.setLayoutManager(manager);
        rvSolicitudes.setItemAnimator(new DefaultItemAnimator());
        setRV();
        return view;
    }

    private void setRV(){
        if(adapter.getItemCount()==0){
            rvSolicitudes.setVisibility(View.GONE);
            txtNoSolicitudes.setVisibility(View.VISIBLE);
            imgNoSolicitudes.setVisibility(View.VISIBLE);
        }else{
            rvSolicitudes.setVisibility(View.VISIBLE);
            txtNoSolicitudes.setVisibility(View.GONE);
            imgNoSolicitudes.setVisibility(View.GONE);
            rvSolicitudes.setAdapter(adapter);
        }
    }
}
