package com.barajasoft.uabcmobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.barajasoft.uabcmovil.R;

import static java.lang.Thread.sleep;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 2000;
                while(time > 0){
                    time -= 100;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(new Intent(IntroActivity.this,LoginActivity.class));
                finish();
            }
        });
        timer.start();
    }
}
