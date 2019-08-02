package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
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

import com.uabcmovil.uabcmovil.Activities.ImageActivity;
import com.uabcmovil.uabcmovil.Adapters.HorariosRVAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Materia;
import com.uabcmovil.uabcmovil.R;

import java.util.LinkedList;
import java.util.List;

public class LocationExpandedDlg extends Dialog {
    private Activity context;
    private ImageView imagen;
    private TextView txtHorario;
    private RecyclerView rvHorario;
    private List<Materia> materiaList = new LinkedList<>();
    private HorariosRVAdapter adapter;
    public LocationExpandedDlg(@NonNull Activity context, String edificio, int pos) {
        super(context);
        TypedArray imgPrev = context.getResources().obtainTypedArray(R.array.imagenes_preview);
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
        imagen.setImageDrawable(imgPrev.getDrawable(pos));
        TextView txtEdificio = findViewById(R.id.txtEdificio);
        txtEdificio.setText(edificio);
        Button btnPanoramica = findViewById(R.id.btnPanoramica);
        if(imgPrev.getDrawable(pos)==context.getDrawable(R.drawable.no_disponible)){
            btnPanoramica.setVisibility(View.GONE);
        }
        btnPanoramica.setOnClickListener(e->{
            //las opciones que se ocupan para la transicion son la actividad, el view que hara transicion (imageview) y el nombre que se puso de share transition
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,imagen,"IMAGEN");
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("Edificio",edificio);
            intent.putExtra("Posicion",String.valueOf(pos));
            context.startActivity(intent);
        });
        if (!Sesion.getInstance().getAlumno().getUser().equals("Invitado")) {
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
        }else{
            txtHorario.setVisibility(View.GONE);
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
