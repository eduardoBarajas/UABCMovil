package com.barajasoft.uabcmobile.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.barajasoft.uabcmobile.Activities.ImageActivity;
import com.barajasoft.uabcmobile.Adapters.HorariosRVAdapter;
import com.barajasoft.uabcmobile.Data.Sesion;
import com.barajasoft.uabcmobile.Entities.Alumno;
import com.barajasoft.uabcmobile.Entities.Materia;
import com.barajasoft.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class LocationExpandedDlg extends Dialog {
    private Activity context;
    private ImageView imagen;
    private TextView txtHorario;
    private RecyclerView rvHorario;
    private List<Materia> materiaList = new LinkedList<>();
    private HorariosRVAdapter adapter;
    public LocationExpandedDlg(@NonNull Activity context, String edificio) {
        super(context);
        adapter = new HorariosRVAdapter();
        this.context = context;
        setContentView(R.layout.location_expand_dlg);
        rvHorario = findViewById(R.id.rvHorario);
        rvHorario.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        rvHorario.setLayoutManager(manager);
        rvHorario.setItemAnimator(new DefaultItemAnimator());

        txtHorario = findViewById(R.id.txtHorario);
        imagen = findViewById(R.id.imgEstatica);
        TextView txtEdificio = findViewById(R.id.txtEdificio);
        txtEdificio.setText(edificio);
        Button btnPanoramica = findViewById(R.id.btnPanoramica);
        btnPanoramica.setOnClickListener(e->{
            //las opciones que se ocupan para la transicion son la actividad, el view que hara transicion (imageview) y el nombre que se puso de share transition
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,imagen,"IMAGEN");
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("Edificio",edificio);
            context.startActivity(intent);
        });
        for(Materia m : Sesion.getInstance().getAlumno().getClases()){
            if(m.getSalon().contains(edificio)){
                materiaList.add(m);
            }
        }
        if(materiaList.size()==0){
            txtHorario.setVisibility(View.GONE);
            rvHorario.setVisibility(View.GONE);
        }else{
            adapter.setData(materiaList);
            rvHorario.setAdapter(adapter);
        }
        //obtener tamaño pantalla
        DisplayMetrics medidas = context.getResources().getDisplayMetrics();
        //se ajusta el dialogo a las medidas de la pantalla si tienes clases en ese edificio
        if(materiaList.size()==0)
            getWindow().setLayout(medidas.widthPixels,medidas.heightPixels-((medidas.heightPixels/100)*30));
        else
            getWindow().setLayout(medidas.widthPixels,medidas.heightPixels-((medidas.heightPixels/100)*10));
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
