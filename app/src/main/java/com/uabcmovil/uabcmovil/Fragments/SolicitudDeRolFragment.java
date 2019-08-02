package com.uabcmovil.uabcmovil.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Adapters.SolicitudRVAdapter;
import com.uabcmovil.uabcmovil.Adapters.SolicitudesDeRolRVAdapter;
import com.uabcmovil.uabcmovil.Data.SolicitudesRolFireBaseConnection;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

public class SolicitudDeRolFragment extends Fragment {
    private ImageView imgNoSolicitudes;
    private TextView txtNoSolicitudes;
    private RecyclerView rvSolicitudes;
    private SolicitudesDeRolRVAdapter adapter;
    private Fragment this_fragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this_fragment = this;
        adapter = new SolicitudesDeRolRVAdapter(this.getActivity());
        SolicitudesRolFireBaseConnection connection = SolicitudesRolFireBaseConnection.getInstance();
        connection.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(((String)o).equals("Se Modifico") || ((String)o).equals("Se elimino") ){
                    if(((String)o).equals("Se elimino"))
                        Toast.makeText(getContext(),"Se aprobo la solicitud",Toast.LENGTH_SHORT).show();
                    adapter.setData(connection.getSolicitudes());
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
        adapter.setData(connection.getSolicitudes());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.solicitud_de_rol_fragment_layout,container,false);
        imgNoSolicitudes = view.findViewById(R.id.imgSolicitudDeRol);
        txtNoSolicitudes = view.findViewById(R.id.textViewSolicitudDeRol);
        rvSolicitudes = view.findViewById(R.id.rvSolicitudDeRol);
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
