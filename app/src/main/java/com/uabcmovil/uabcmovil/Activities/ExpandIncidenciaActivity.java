package com.uabcmovil.uabcmovil.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.uabcmovil.uabcmovil.Adapters.ImageAdapter;
import com.uabcmovil.uabcmovil.Data.Sesion;
import com.uabcmovil.uabcmovil.Dialogs.OptionChooserDialog;
import com.uabcmovil.uabcmovil.Entities.Incidencia;
import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class ExpandIncidenciaActivity extends AppCompatActivity {
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Incidencias");

    private TextView txtProfesor,txtTitulo,txtEdificio, txtLab, txtSeleccionado, txtIncidencia,txtRespuesta2;
    private EditText txtRespuesta;
    private Button btnConfirmar,btnEliminar;
    private GridView left, right;
    private OptionSelectedListener listener;
    private Incidencia incidenciaActual;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageAdapter imageAdapterLeft = new ImageAdapter(this);
        ImageAdapter imageAdapterRight = new ImageAdapter(this);
        setContentView(R.layout.expanded_incidencia_activity_layout);
        btnEliminar = findViewById(R.id.btnEliminarExpandedIncidencia);
        txtTitulo = findViewById(R.id.titleExpandedIncidencia);
        txtEdificio = findViewById(R.id.txtEdificioExpandedIncidencia);
        txtLab = findViewById(R.id.txtLaboratorioExpandedIncidencia);
        left = findViewById(R.id.gridviewIzquierdaExpandedIncidencia);
        right = findViewById(R.id.gridviewDerechaExpandedIncidencia);
        txtSeleccionado = findViewById(R.id.SeleccionadoExpandedIncidencia);
        txtIncidencia = findViewById(R.id.txtIncidenciaExpandedIncidencia);
        txtRespuesta = findViewById(R.id.txtRespuestaExpanedIncidencia);
        txtRespuesta2 = findViewById(R.id.txtRespuestaExpanded);
        btnConfirmar = findViewById(R.id.btnConfirmarExpandedIncidencia);
        txtProfesor = findViewById(R.id.txtProfesorExpandedIncidencia);
        LinearLayout respuestaLayout = findViewById(R.id.layoutRespuesta);
        listener = new OptionSelectedListener() {
            @Override
            public void optionSelected(String dlgTag, Object result) {
                if(((String)result).equals("SI")){
                    referencia.child(incidenciaActual.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Se elimino la el registro exitosamente.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        };
        if(getIntent().hasExtra("Incidencia")){
            incidenciaActual = new Incidencia();
            incidenciaActual = getIncidenciaFromStringArray(getIntent().getStringArrayExtra("Incidencia"));
            if(incidenciaActual.getEstado().equals("Pendiente")){
                txtTitulo.setText("Incidencia Pendiente");
                txtRespuesta.setVisibility(View.VISIBLE);
                if(Sesion.getInstance().getAlumno().getRol().equals("Profesor")) {
                    btnConfirmar.setVisibility(View.GONE);
                    respuestaLayout.setVisibility(View.GONE);
                }
            }else{
                txtTitulo.setText("Incidencia Atendida");
                btnConfirmar.setVisibility(View.GONE);
                txtRespuesta2.setVisibility(View.VISIBLE);
                txtRespuesta2.setText(incidenciaActual.getRespuesta());
            }
            txtProfesor.setText(incidenciaActual.getProfesor());
            txtEdificio.setText(incidenciaActual.getEdificio());
            txtLab.setText(incidenciaActual.getLaboratorio());
            String[] pcSelecition = selectPcInGridView(incidenciaActual.getId_equipo());
            if(pcSelecition[0].equals("LEFT")){
                imageAdapterLeft.setSelected(Integer.parseInt(pcSelecition[1]));
                left.setAdapter(imageAdapterLeft);
                right.setAdapter(imageAdapterRight);
            }else{
                imageAdapterRight.setSelected(Integer.parseInt(pcSelecition[1]));
                right.setAdapter(imageAdapterRight);
                left.setAdapter(imageAdapterLeft);
            }
            txtSeleccionado.setText(incidenciaActual.getId_equipo());
            txtIncidencia.setText(incidenciaActual.getIncidencia());
            btnEliminar.setOnClickListener(e->{
                OptionChooserDialog dlg = new OptionChooserDialog(ExpandIncidenciaActivity.this,"EliminarIncidencia","Estas seguro que deseas eliminar este registro?",listener);
                dlg.show();
            });
            btnConfirmar.setOnClickListener(e->{
                if(txtRespuesta.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Asegurate de agregar una respuesta antes.",Toast.LENGTH_SHORT).show();
                }else{
                    incidenciaActual.setRespuesta(txtRespuesta.getText().toString());
                    incidenciaActual.setEstado("Atendida");
                    referencia.child(incidenciaActual.getKey()).setValue(incidenciaActual, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(getApplicationContext(),"Se marco como atendida exitosamente",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        }
    }

    private String[] selectPcInGridView(String id_equipo) {
        int selected = -1;
        String side = "";
        String[] id = id_equipo.split("C");
        char[] caracteres = id[1].toCharArray();
        int[] ids = new int[]{Character.getNumericValue(caracteres[0]),Character.getNumericValue(caracteres[1])};
        if(ids[1]>=4){
            side = "RIGHT";
            //esta en el grid de la derecha
            switch (ids[0]){
                case 1:{
                    selected = ids[1]-3;
                    break;
                }
                case 2:{
                    selected = ids[1];
                    break;
                }
                case 3:{
                    selected = ids[1]+3;
                    break;
                }
            }
        }else {
            side = "LEFT";
            //esta en grid de la izquierda
            switch (ids[0]) {
                case 1: {
                    selected = ids[1];
                    break;
                }
                case 2: {
                    selected = ids[1] + 3;
                    break;
                }
                case 3: {
                    selected = ids[1] + 6;
                    break;
                }
            }
        }
        return new String[]{side,String.valueOf(selected)};
    }

    private Incidencia getIncidenciaFromStringArray(String[] i) {
        Incidencia incidencia = new Incidencia();
        incidencia.setEstado(i[0]);
        incidencia.setProfesor(i[1]);
        incidencia.setFecha(i[2]);
        incidencia.setLaboratorio(i[3]);
        incidencia.setEdificio(i[4]);
        incidencia.setId_equipo(i[5]);
        incidencia.setIncidencia(i[6]);
        incidencia.setRespuesta(i[7]);
        incidencia.setKey(i[8]);
        return incidencia;
    }
}
