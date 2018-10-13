package com.barajasoft.uabcmobile.Activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.barajasoft.uabcmovil.R;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout layout = null;
    //private List<DrawerOption> rowItems;
    private ListView listaDrawer;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        
        initDrawer();
        
        
        Button btn = findViewById(R.id.button);
        Button btn2 = findViewById(R.id.button2);
        btn.setOnClickListener(e->{
            startActivity(new Intent(MainActivity.this,MapActivity.class));
        });
        btn2.setOnClickListener(e->{
            startActivity(new Intent(MainActivity.this,ImageActivity.class));
        });

    }
    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView drawer = (NavigationView)findViewById(R.id.drawer_layout_id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
