package com.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.topelec.zigbeecontrol.Command;
import com.topelec.zigbeecontrol.SensorControl;

import java.util.Random;

public class temperature_monitorActivity extends AppCompatActivity implements
        SensorControl.MotorListener, SensorControl.TempHumListener {
    private Button return_button;
    private TextView temperature;
    private int Temp;
    private int Hum;
    private int fanStatus;//0——停止；1——风扇；2——浇水
    private boolean isAuto;
    SensorControl mSensorControl;


    Handler myHandler = new Handler() {
        //2.重写消息处理函数
        public void handleMessage(Message msg) {
            Bundle data;
            data = msg.getData();
            switch (msg.what) {
                //判断发送的消息
//                case 0x02:
//                    switch (data.getByte("motor_status")) {
//                        case 0x01:
//                            fanView.setImageDrawable(getResources().getDrawable(R.drawable.smartagriculture_fan_on));
//                            waterView.setImageDrawable(null);
//                            fanStatus = 1;
//                            break;
//                        case 0x02:
//                            fanView.setImageDrawable(null);
//                            waterView.setImageDrawable(getResources().getDrawable(R.drawable.smartagriculture_water_on));
//                            fanStatus = 2;
//                            break;
//                        case 0x00:
//                            fanView.setImageDrawable(null);
//                            waterView.setImageDrawable(null);
//                            fanStatus = 0;
//                            break;
//                        default:
//                            break;
//                    }
//                    break;
                case 0x03:
                    switch (data.getByte("senser_id")) {
                        case 0x01:
                            Temp = data.getInt("senser_data");
                            temperature.setText(String.valueOf(Temp) + ".0°C");

                            //如下温度自动化管理代码
                            if (airconditionActivity.tempAutoSwitch) {
                                if (Temp >= 28) {
                                    //TODO 温度大于设定值，降低温度，执行打开风扇动作
                                    mSensorControl.fanForward(true);
                                } else {
                                    //TODO 实时温度小于设定值，停止降低温度，如果此时风扇是运行状态，则执行停止风扇动作。
                                    mSensorControl.fanStop(true);
                                }
                            }
                            break;
//                        case 0x02:
//                            Hum = data.getInt("senser_data");
//                            humView.setText(String.valueOf(Hum));
//
//                            if (isAuto && toControl.equals("2")) {
//                                if (Hum < settingHumidity) {
//                                    //TODO 湿度小于设定值，土壤湿度小，执行浇水（风扇倒转）动作
//                                    if (fanStatus != 2)
//                                        mSensorControl.fanBackward(true);
//                                } else {
//                                    //TODO 实时湿度大于设定值，土壤湿度足够，停止灌溉。
//                                    if (fanStatus != 0)
//                                        mSensorControl.fanStop(true);
//                                }
//                            }
//                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    //定义发送任务定时器
    Handler timerHandler = new Handler();
    Runnable sendRunnable = new Runnable() {
        int i = 1;

        @Override
        public void run() {
            //TODO:查询温度湿度
            switch (i) {
                case 1:
                    mSensorControl.checkTemperature(true);
                    i++;
                    break;
                case 2:
                    mSensorControl.checkHumidity(true);
                    i = 1;
                    break;
                default:
                    break;
            }
            timerHandler.postDelayed(this, Command.CHECK_SENSOR_DELAY);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_monitor);
        return_button=(Button) findViewById(R.id.return_temperature);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_landingpage();
            }
        });

        temperature=(TextView) findViewById(R.id.value_temperature);

        mSensorControl = new SensorControl();
        mSensorControl.addMotorListener(this);
        mSensorControl.addTempHumListener(this);

        //TODO:绑定温度传感器数值
//        temperature.setText("27.5 ℃");
    }



    public void open_landingpage(){
        Intent intent =new Intent(this,LandingpageActivity.class);
        startActivity(intent);
    }



    @Override
    public void motorControlResult(byte motor_status) {

        Message msg = new Message();
        msg.what = 0x02;
        Bundle data = new Bundle();
        data.putByte("motor_status", motor_status);
        msg.setData(data);
        myHandler.sendMessage(msg);
    }

    @Override
    public void tempHumReceive(byte senser_id, int senser_data) {

        Message msg = new Message();
        msg.what = 0x03;
        Bundle data = new Bundle();
        data.putByte("senser_id", senser_id);
        data.putInt("senser_data", senser_data);
//        Log.d(TAG,"sensor_id = " + senser_id);
//        Log.d(TAG,"sensor_data = "+senser_data);
        msg.setData(data);
        myHandler.sendMessage(msg);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSensorControl.actionControl(true);

        //TODO:每350ms发送一次数据
        timerHandler.postDelayed(sendRunnable, Command.CHECK_SENSOR_DELAY);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSensorControl.removeMotorListener(this);
        mSensorControl.removeTempHumListener(this);
        mSensorControl.closeSerialDevice();
    }
}
