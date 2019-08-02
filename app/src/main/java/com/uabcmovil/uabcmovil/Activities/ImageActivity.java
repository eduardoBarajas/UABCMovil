package com.uabcmovil.uabcmovil.Activities;

import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.uabcmovil.uabcmovil.R;

import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    private VrPanoramaView panoramaView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imgSelected = "malecon.jpg";
        TypedArray imgPano = getResources().obtainTypedArray(R.array.imagenes_pano);

        setContentView(R.layout.image_viewer_activity_layout);
        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(e->{
            finish();
        });
        if(getIntent().hasExtra("Posicion")){
            imgSelected = imgPano.getString(Integer.parseInt(getIntent().getStringExtra("Posicion")));
        }
        panoramaView = findViewById(R.id.vrPanoramaView);
        loadImg(imgSelected);
    }

    private void loadImg(String selected) {
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        InputStream inputStream = null;

        AssetManager assetsManager = getAssets();
        try{
            inputStream = assetsManager.open(selected);
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            panoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream),options);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoramaView.resumeRendering();
    }

    @Override
    protected void onPause() {
        super.onPause();
        panoramaView.pauseRendering();
    }
    @Override
    protected void onDestroy() {
        panoramaView.shutdown();
        super.onDestroy();
    }
}
