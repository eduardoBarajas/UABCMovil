package com.uabcmovil.uabcmovil.Activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Adapters.ImageAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Entities.Incidencia;
import com.uabcmovil.uabcmovil.R;

public class NewIncidenciaActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Incidencias");

    private String selected = "";
    private final String[] EDIFICIOS = new String[]{"E1","E2","E3","E4","E5","E6"};
    private final String[] LABORATORIOS = new String[]{"LAB 1","LAB 2","LAB 3","LAB 4","LAB A","LAB B","LAB C"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapterEdificios = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,EDIFICIOS);
        ArrayAdapter<String> adapterLaboratorios = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,LABORATORIOS);
        setContentView(R.layout.new_incidencia_activity_layout);
        AutoCompleteTextView txtEdificios = findViewById(R.id.edificioNuevaIncidencia);
        AutoCompleteTextView txtLaboratorios = findViewById(R.id.laboratorioNuevaIncidencia);
        txtEdificios.setAdapter(adapterEdificios);
        txtLaboratorios.setAdapter(adapterLaboratorios);
        TextView selecionadoText = findViewById(R.id.SeleccionadoNewIncidencia);
        GridView gridLeft = findViewById(R.id.gridviewIzquierda);
        GridView gridRight = findViewById(R.id.gridviewDerecha);
        ImageAdapter imageAdapterLeft = new ImageAdapter(this);
        ImageAdapter imageAdapterRight = new ImageAdapter(this);
        Button btnConfirmar = findViewById(R.id.btnConfirmarNewIncidencia);
        EditText problemaIncidencia = findViewById(R.id.incidenciaText);
        btnConfirmar.setOnClickListener(e->{
            if(problemaIncidencia.getText().toString().isEmpty()||selected.isEmpty()||problemaIncidencia.getText().toString().isEmpty()
                    || txtEdificios.getText().toString().isEmpty() || txtLaboratorios.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"Asegurate de seleccionar un equipo, y colocar su falla y ubicacion.",Toast.LENGTH_SHORT).show();
            }else{
                Incidencia incidencia = new Incidencia();
                incidencia.setEdificio(txtEdificios.getText().toString());
                incidencia.setLaboratorio(txtLaboratorios.getText().toString());
                incidencia.setEstado("Pendiente");
                incidencia.setId_equipo(selected.split(":")[1]);
                incidencia.setProfesor(Sesion.getInstance().getAlumno().getNombre());
                incidencia.setIncidencia(problemaIncidencia.getText().toString());
                incidencia.setFecha();
                referencia.child(incidencia.getKey()).setValue(incidencia, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(getApplicationContext(),"Se agrego correctamente",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
        gridLeft.setAdapter(imageAdapterLeft);
        gridLeft.setNumColumns(3);
        gridRight.setAdapter(imageAdapterRight);
        gridRight.setNumColumns(3);
        gridLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected.contains("LEFT") || selected.isEmpty()){
                    imageAdapterLeft.setSelected(i);
                    imageAdapterLeft.notifyDataSetChanged();
                    selected = "LEFT:"+ getComputerID("LEFT",i);
                    selecionadoText.setText(getComputerID("LEFT",i));
                }else{
                    if(selected.contains("RIGHT")){
                        imageAdapterRight.restartAdapter();
                        imageAdapterRight.notifyDataSetChanged();
                        imageAdapterLeft.setSelected(i);
                        imageAdapterLeft.notifyDataSetChanged();
                        selected = "LEFT:"+ getComputerID("LEFT",i);
                        selecionadoText.setText(getComputerID("LEFT",i));
                    }
                }
            }
        });
        gridRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected.contains("RIGHT") || selected.isEmpty()){
                    imageAdapterRight.setSelected(i);
                    imageAdapterRight.notifyDataSetChanged();
                    selected = "RIGHT:"+ getComputerID("RIGHT",i);
                    selecionadoText.setText(getComputerID("RIGHT",i));
                }else{
                    if(selected.contains("LEFT")){
                        imageAdapterLeft.restartAdapter();
                        imageAdapterLeft.notifyDataSetChanged();
                        imageAdapterRight.setSelected(i);
                        imageAdapterRight.notifyDataSetChanged();
                        selected = "RIGHT:"+ getComputerID("RIGHT",i);
                        selecionadoText.setText(getComputerID("RIGHT",i));
                    }
                }
            }
        });
    }

    private String getComputerID(String side,int id){
        String returned = "";
        if(side.equals("LEFT")){
            if(id>=0 && id <=2){
                returned = "C"+(10+id);
            }
            if(id>=3 && id <= 5){
                returned = "C"+(17+id);
            }
            if(id>=6 && id <= 8){
                returned = "C"+(24+id);
            }
        }else{
            if(id>=0 && id <=2){
                returned = "C"+(13+id);
            }
            if(id>=3 && id <= 5){
                returned = "C"+(20+id);
            }
            if(id>=6 && id <= 8){
                returned = "C"+(27+id);
            }
        }
        return returned;
    }
}
