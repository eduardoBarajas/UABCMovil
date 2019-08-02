package com.uabcmovil.uabcmovil.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uabcmovil.uabcmovil.Entities.Noticia;
import com.uabcmovil.uabcmovil.R;
import com.uabcmovil.uabcmovil.Utilities.ImageAngleCorrector;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class NewFeedActivity extends AppCompatActivity{
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referencia = database.getReference("Noticias");
    static final private int PICK_IMAGE = 123;
    private Uri imgDir;
    private ImageView imgImagen;
    private boolean imgSelected = false;
    private int RESULT = 0;
    private String categoriaSelected = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> categoriasData = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Platica","Reunion","Recreativo","Informacion"});
        categoriasData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageReference = FirebaseStorage.getInstance().getReference();
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd yyyy DDD HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        setContentView(R.layout.new_feed_activity_layout);
        Spinner categorias = findViewById(R.id.sppinerCategorias);
        categorias.setAdapter(categoriasData);
        categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoriaSelected = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        EditText txtTitulo = findViewById(R.id.txtTituloNewFeed);
        EditText txtNoticia = findViewById(R.id.txtNoticiaNewFeed);
        imgImagen = findViewById(R.id.imgImagenNewFeed);
        Button btnAceptarNewFeed = findViewById(R.id.btnAceptarNewFeed);

        btnAceptarNewFeed.setOnClickListener(e->{
            if(txtTitulo.getText().toString().isEmpty() || txtNoticia.getText().toString().isEmpty() || categoriaSelected.isEmpty()){
                Toast.makeText(getApplicationContext(),"Debes llenar todos los campos!.",Toast.LENGTH_SHORT).show();
            }else{
                Noticia newNoticia = new Noticia();
                newNoticia.setFecha(translateDate(format.format(Calendar.getInstance().getTime())));
                newNoticia.setCategoria(categoriaSelected);
                newNoticia.setNoticia(txtNoticia.getText().toString());
                newNoticia.setTitulo(txtTitulo.getText().toString());
                referencia.push().setValue(newNoticia, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Map<String, Object> dato = new HashMap<>();
                        dato.put("key", databaseReference.getKey());
                        referencia.child(databaseReference.getKey()).updateChildren(dato).addOnCompleteListener(e->{
                            savePicture(databaseReference.getKey());
                        });
                    }
                });
            }
        });
        imgImagen.setOnClickListener(e->{
            Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
            intent.setType( "image/*" );
            startActivityForResult( intent, PICK_IMAGE);
        });
    }

    private String translateDate(String format) {
        String fecha = "";
        String[] date = format.split(" ");
        switch (date[0]){
            case "Monday": fecha += "Lun";break;
            case "Tuesday": fecha += "Mar";break;
            case "Wednesday": fecha += "Mie";break;
            case "Thursday": fecha += "Jue";break;
            case "Friday": fecha += "Vie";break;
            case "Saturday": fecha += "Sab";break;
        }
        fecha += " "+date[2]+" de ";
        switch(date[1]){
            case "Jan": fecha += "Ene";break;
            case "Feb": fecha += "Feb";break;
            case "Mar": fecha += "Mar";break;
            case "Apr": fecha += "Abr";break;
            case "May": fecha += "May";break;
            case "Jun": fecha += "Jun";break;
            case "Jul": fecha += "Jul";break;
            case "Aug": fecha += "Ago";break;
            case "Sep": fecha += "Sep";break;
            case "Oct": fecha += "Oct";break;
            case "Nov": fecha += "Nov";break;
            case "Dec": fecha += "Dec";break;
            default:fecha+=date[1];
        }
        fecha += " del "+date[3]+" a las "+date[5].split(":")[0]+":"+date[5].split(":")[1];
        return fecha;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imgDir = data.getData();
            imgImagen.setImageBitmap(ImageAngleCorrector.getFixedBitmap(getContentResolver(),imgDir));
            imgSelected = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePicture(String key) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir;
        File archivo;
        Uri link = Uri.EMPTY;
        if(imgSelected){
            myDir = new File(root + "/UABCMovil/Resources/Temp");
            myDir.mkdirs();
            archivo = new File(myDir,"img.jpg");
            try {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(ImageAngleCorrector.getFixedBitmap(getContentResolver(),imgDir), 1600, 900, false);
                FileOutputStream out = new FileOutputStream(archivo);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                link = Uri.fromFile(archivo);
            } catch (Exception e) {
                Log.e("ErrorImagen","No se pudo convertir la imagen");
            }
        }else{
            link = Uri.parse("android.resource://"+getPackageName()+"/drawable/imagen_no_disponible");
        }
        StorageReference arch = storageReference.child(key+"/"+"img.jpg");
        arch.putFile(link)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e("State","Subido");
                        storageReference.child(key+"/img.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Map<String, Object> dato = new HashMap<>();
                                dato.put("imagen",task.getResult().toString());
                                referencia.child(key).updateChildren(dato).addOnCompleteListener(e->{
                                    Toast.makeText(getApplicationContext(),"Se agrego correctamente la noticia",Toast.LENGTH_SHORT).show();
                                    RESULT = RESULT_OK;
                                    finish();
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("State","No Subido");
                    }
                });
    }

    @Override
    public void finish() {
        setResult(RESULT);
        super.finish();
    }
}