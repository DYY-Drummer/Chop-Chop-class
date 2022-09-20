package com.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class cardActivity extends AppCompatActivity {
    private Button return_button;
    private TextView student_id;
    private TextView student_name;
    private TextView student_attendance;
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

        student_id=(TextView) findViewById(R.id.student_id);
        student_name=(TextView) findViewById(R.id.student_name);
        student_attendance=(TextView) findViewById(R.id.student_attendance);

        //TODO:刷卡后刷新打卡信息
        student_id.setText("学号：18376255");
        student_name.setText("姓名：丁一洋");
        student_attendance.setText("考勤次数：9");//考勤次数，可以写死

    }
    public void open_landingpage(){
        Intent intent =new Intent(this,LandingpageActivity.class);
        startActivity(intent);
    }
}
