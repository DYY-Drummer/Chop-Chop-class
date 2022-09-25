package com.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.topelec.zigbeecontrol.Command;
import com.topelec.zigbeecontrol.SensorControl;

public class projectorActivity extends AppCompatActivity implements
        SensorControl.MotorListener {
    private static final String TAG = "TOUYING";

    private Button return_button;
    private ImageView projector_visibility;
    private Button power_open;
    private Button power_close;
    public static boolean projector_on = false;

    SensorControl mSensorControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projector);

        mSensorControl = new SensorControl();
        mSensorControl.addMotorListener(this);

        return_button=(Button) findViewById(R.id.return_projector);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_landingpage();
            }
        });

        projector_visibility=(ImageView) findViewById(R.id.projector_visibility);
        if(projector_on){
            projector_visibility.setImageDrawable(getDrawable(R.drawable.eye_visible));
        }
        power_open=(Button) findViewById(R.id.power_open);
        power_close=(Button) findViewById(R.id.power_close);
        power_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeProjector();
            }
        });
        power_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProjector();
            }
        });
    }

    public void open_landingpage(){
        Intent intent =new Intent(this,LandingpageActivity.class);
        startActivity(intent);
    }
    public void change_visibility(){

    }
    public void closeProjector(){
        projector_on=false;
        projector_visibility.setImageDrawable(getDrawable(R.drawable.eye_invisible));
        //TODO 关闭投影仪
        mSensorControl.fanStop(false);
        Log.d(TAG, "投影关");
    }
    public void openProjector(){
        projector_on=true;
        projector_visibility.setImageDrawable(getDrawable(R.drawable.eye_visible));
        //TODO 开启投影仪
        mSensorControl.fanForward(false);
        Log.d(TAG, "投影开");
    }

    @Override
    public void motorControlResult(byte motor_status) {
        Message msg = new Message();
        msg.what = 0x02;
        Bundle data = new Bundle();
        data.putByte("motor_status", motor_status);
        msg.setData(data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorControl.actionControl(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSensorControl.removeMotorListener(this);
        mSensorControl.closeSerialDevice();
    }
}
