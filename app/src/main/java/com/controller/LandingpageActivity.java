package com.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.controller.R;

public class LandingpageActivity extends AppCompatActivity {
    private Button button_temperature;
    private Button button_card;
    private Button button_aircondition;
    private Button button_projector;
    private Button button_course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);
        button_temperature=(Button) findViewById(R.id.button_temperature);
        button_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_temperatureMonitor();
            }
        });

        button_card=(Button) findViewById(R.id.button_card);
        button_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_card();
            }
        });

        button_aircondition=(Button) findViewById(R.id.button_aircondition);
        button_aircondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_aircondition();
            }
        });

        button_projector=(Button) findViewById(R.id.button_projector);
        button_projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_projector();
            }
        });

        button_course=(Button) findViewById(R.id.button_course);
        button_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectorActivity.projector_on=true;
                //TODO:开启空调和投影仪
            }
        });

    }
    public void open_temperatureMonitor(){
        Intent intent = new Intent(this,temperature_monitorActivity.class);
        startActivity(intent);
    }

    public void open_card(){
            Intent intent= new Intent(this,cardActivity.class);
            startActivity(intent);
    }
    public void open_aircondition(){
        Intent intent= new Intent(this,airconditionActivity.class);
        startActivity(intent);
    }
    public void open_projector(){
        Intent intent=new Intent(this,projectorActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coverflow_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View mView = getWindow().getDecorView();
        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
