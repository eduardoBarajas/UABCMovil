package com.barajasoft.uabcmobile.Activities;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.barajasoft.uabcmovil.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    private VrPanoramaView panoramaView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer_layout);
        panoramaView = findViewById(R.id.vrPanoramaView);
        loadImg();
    }

    private void loadImg() {
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        InputStream inputStream = null;

        AssetManager assetsManager = getAssets();
        try{
            inputStream = assetsManager.open("sunset_at_pier.jpg");
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
