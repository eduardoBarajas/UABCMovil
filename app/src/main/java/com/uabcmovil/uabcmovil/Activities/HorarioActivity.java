package com.uabcmovil.uabcmovil.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Materia;
import com.uabcmovil.uabcmovil.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HorarioActivity extends AppCompatActivity {
    private List<Materia> clases = new ArrayList<>();
    private ArrayList<String> colors = new ArrayList<>();
    private Map<String,ArrayList<String>> dias = new HashMap<>();
    private Map<String,Integer> coloresOcupados = new HashMap<>();
    private Random rand = new Random();
    private LinearLayout newrow;
    private LinearLayout layout;
    private LinearLayout.LayoutParams layoutParams;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.0f;
    private HorizontalScrollView parent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Collections.addAll(colors,"#3F51B5","#448AFF","#8BC34A","#03A9F4",
                "#7C4DFF","#009688","#FF9800","#FF5722","#607D8B","#795548","#E040FB","#F44336");
        dias.put("Lunes",new ArrayList<>());
        dias.put("Martes",new ArrayList<>());
        dias.put("Míercoles",new ArrayList<>());
        dias.put("Jueves",new ArrayList<>());
        dias.put("Viernes",new ArrayList<>());
        dias.put("Sábado",new ArrayList<>());
        setContentView(R.layout.horario_activity_layout);
        parent = findViewById(R.id.parent);
        layout = findViewById(R.id.horarioLayout);


        gestureDetector = new GestureDetector(this,new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = 1 - detector.getScaleFactor();
                float prevScale = scaleFactor;
                scaleFactor += scale;

                if (scaleFactor < 0.1f) // Minimum scale condition:
                    scaleFactor = 0.1f;

                if (scaleFactor > 5f) // Maximum scale condition:
                    scaleFactor = 3f;
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / scaleFactor, 1f / prevScale, 1f / scaleFactor, detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                //parent.startAnimation(scaleAnimation);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });


        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        newrow = new LinearLayout(this);
        layoutParams.setMargins(0,0,0,0);
        layoutParams.weight = 1;
        newrow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        newrow.setOrientation(LinearLayout.HORIZONTAL);

        TextView txt = createTextView("HOLO");
        txt.setVisibility(View.INVISIBLE);
        newrow.addView(txt,layoutParams);
        newrow.addView(createTextView("Lunes"),layoutParams);
        newrow.addView(createTextView("Martes"),layoutParams);
        newrow.addView(createTextView("Míercoles"),layoutParams);
        newrow.addView(createTextView("Jueves"),layoutParams);
        newrow.addView(createTextView("Viernes"),layoutParams);
        layout.addView(newrow,layoutParams);
        clases = Sesion.getInstance().getAlumno().getClases();
        for(Materia clase: clases){
            String horario[] = clase.getHora().split("\\s");
            for(int i=0;i<horario.length;i++){
                if(i%2==0){
                    try{
                        dias.get(horario[i]).add(horario[i+1]);
                    }catch(Exception ex){
                        Log.e("Error","con el horario");
                        Toast.makeText(getApplicationContext(),"Ocurrio un error con el horario, asegurate de actualizarlo antes",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                }
            }
        }
        ArrayList<String> copyColors = new ArrayList<>();
        for(int x=0; x<colors.size();x++)
            copyColors.add(null);
        Collections.copy(copyColors,colors);
        for(Materia clase : clases){
            Log.e("Clase",clase.getNombreClase());
            Log.e("Horario",clase.getHora());
            Log.e("Profesor",clase.getProfesor());
            if(!coloresOcupados.containsKey(clase.getNombreClase())){
                int elegido = rand.nextInt(copyColors.size());
                coloresOcupados.put(clase.getNombreClase(),elegido);
                copyColors.remove(elegido);
            }
        }
        int[] contHours = getLowestHour();
        for(int hora = contHours[0]; hora<contHours[1]+1;hora++){
            int currentHora = hora;

            newrow = new LinearLayout(this);
            layoutParams.setMargins(0,4,4,0);
            layoutParams.weight = 1;
            newrow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            newrow.setOrientation(LinearLayout.HORIZONTAL);
            newrow.addView(createTextView(String.valueOf(hora)+":00"),layoutParams);

            String[] diasArray = {"Lunes","Martes","Míercoles","Jueves","Viernes"};
            for(String dia : diasArray){
                for(int i = 2; i<dias.get(dia).size();i++){
                    for(int j = 0; j < dias.get(dia).size()-1;j++){
                        if(Integer.parseInt(dias.get(dia).get(j).split("\\:")[0]) > Integer.parseInt(dias.get(dia).get(j+1).split("\\:")[0])){
                            String aux = dias.get(dia).get(j);
                            dias.get(dia).set(j,dias.get(dia).get(j+1));
                            dias.get(dia).set(j+1,aux);
                        }
                    }
                }
            }
            int contClases = 0;
            for(String dia : diasArray){
                int tempCont = contClases;
                for(int x = 0 ;x < dias.get(dia).size(); x++){
                    String[] cadenas = dias.get(dia).get(x).split("\\:");
                    if(Integer.parseInt(cadenas[0]) == currentHora){
                        Materia current = getClase(dia +" " + dias.get(dia).get(x));
                        addToNewRow(current,coloresOcupados.get(current.getNombreClase()),true);
                        contClases++;
                        break;
                    }else{
                        if(Integer.parseInt(cadenas[1].split("\\-")[1]) == currentHora+1){
                            Materia current = getClase(dia +" " + dias.get(dia).get(x));
                            Log.e("KHe",dias.get(dia).get(x));
                            Log.e("KHex2",dia +" " + dias.get(dia).get(x));

                            addToNewRow(current,coloresOcupados.get(current.getNombreClase()),true);
                            contClases++;
                            break;
                        }
                    }
                }
                if(contClases == tempCont){
                    addToNewRow(null,0,false);
                    contClases++;
                }
            }
            layout.addView(newrow,layoutParams);
            contClases = 0;
        }
    }

    private void addToNewRow(Materia clase,int color, boolean visible){
        CardView card = (CardView) getLayoutInflater().inflate(R.layout.clase_horario_model,null);
        card.setCardBackgroundColor(Color.parseColor(colors.get(color)));
        card.setPreventCornerOverlap(true);
        card.setUseCompatPadding(true);
        TextView txtClase = card.findViewById(R.id.txtClase);
        TextView txtProfesor = card.findViewById(R.id.txtProfesor);
        TextView txtGrupo = card.findViewById(R.id.txtGrupo);
        TextView txtTipoClase = card.findViewById(R.id.txtTipoClase);

        if(clase!=null){
            txtClase.setText(clase.getNombreClase());
            txtProfesor.setText(clase.getProfesor());
            txtGrupo.setText("Grupo:"+clase.getGrupo()+"  Subgrupo:"+clase.getSubGrupo());
            txtTipoClase.setText("Salon:"+clase.getSalon() + "  TipoClase:"+clase.getTipoClase());
        }
        if(!visible)
            card.setVisibility(View.INVISIBLE);
        newrow.addView(card,layoutParams);
    }

    private Materia getClase(String horario){
        for(Materia clase : clases){
            if(clase.getHora().contains(horario)){
                return clase;
            }
        }
        return null;
    }

    private int[] getLowestHour() {
        int[] cont = {0,0};
        for(String dia : dias.keySet()){
            for(int x=0;x<dias.get(dia).size();x++){
                String[] cadenas = dias.get(dia).get(x).split("\\:");
                if(cont[0]>Integer.parseInt(cadenas[0]))
                    cont[0] = Integer.parseInt(cadenas[0]);
                if(cont[0]==0)
                    cont[0] = Integer.parseInt(cadenas[0]);
                if(cont[1]<Integer.parseInt(cadenas[0]))
                    cont[1] = Integer.parseInt(cadenas[0]);
                if(cont[1]==0)
                    cont[1] = Integer.parseInt(cadenas[0]);
            }
        }
        return cont;
    }

    private TextView createTextView(String texto){
        TextView txt = new TextView(this);
        txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
        txt.setWidth(400);
        txt.setText(texto);
        txt.setGravity(Gravity.CENTER);
        return txt;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
