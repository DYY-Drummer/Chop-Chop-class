package com.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class cardActivity extends AppCompatActivity {
    private Button return_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        return_button=(Button) findViewById(R.id.return_card);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_landingpage();
            }
        });

    }
    public void open_landingpage(){
        Intent intent =new Intent(this,LandingpageActivity.class);
        startActivity(intent);
    }
}
