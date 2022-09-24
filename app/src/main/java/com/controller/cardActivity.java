package com.controller;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import com.database.DatabaseHelper;
import com.topelec.rfidcontrol.ModulesControl;
import com.topelec.zigbeecontrol.Command;

import java.util.HashMap;
import java.util.Map;

public class cardActivity extends AppCompatActivity {
    private static final String TAG = ".cardActivity";

    private Button return_button;
    private TextView student_id;
    private TextView student_name;
    private TextView student_attendance;

    ModulesControl mModulesControl;
    Intent intent;

    /**数据库相关**/
    Context mContext;
    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private final static String TABLE_NAME = "HFCard";
    private final static String ID = "_id";
    private final static String CARD_ID = "card_id";
    private final static String STUDENT_ID = "student_id";
    private final static String STUDENT_NAME = "student_name";
    private final static String COUNT = "count";
    private static final double stepValue = 2.00;
    private final static String daka_action = "com.controller.cardActivity.daka_action";
    /**
     * 用于更新rechargeUI
     */
    Handler uiHandler = new Handler() {
        //2.重写消息处理函数
        public void handleMessage(Message msg) {
            Bundle data;
            intent = new Intent(daka_action);
            switch (msg.what) {
                //判断发送的消息
                case Command.HF_TYPE:  //设置卡片类型TypeA返回结果  ,错误类型:1
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        intent.putExtra("what",1);
                        intent.putExtra("Result",getResources().getString(R.string.buscard_type_a_fail));
                        sendBroadcast(intent);
                    }
                    break;
                case  Command.HF_FREQ:  //射频控制（打开或者关闭）返回结果   ,错误类型:1
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        intent.putExtra("what",1);
                        if (data.getBoolean("Result")) {
                            intent.putExtra("Result",getResources().getString(R.string.buscard_frequency_open_fail));
                        }else {
                            intent.putExtra("Result",getResources().getString(R.string.buscard_frequency_close_fail));
                        }
                        sendBroadcast(intent);
                    }

                    break;
                case Command.HF_ACTIVE:       //激活卡片，寻卡，返回结果
                    data = msg.getData();
                    if (data.getBoolean("result")) {
//                        hfView.setText(R.string.active_card_succeed);
                    } else {
                        intent.putExtra("what",2);
                        sendBroadcast(intent);

                    }

                    break;
                case Command.HF_ID:      //防冲突（获取卡号）返回结果

                    data = msg.getData();
                    intent.putExtra("what",3);

                    if (data.getBoolean("result")) {
                        intent.putExtra("Result",data.getString("cardNo"));
                        sendBroadcast(intent);
                    } else {

                    }
//                    Log.v(TAG,"Result = "+ data.getString("cardNo"));

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 用于同步UI,接受CardActivityGroup的broadcast
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int what = intent.getExtras().getInt("what");
            switch (what) {
                case 1://初始化错误
                    //TODO:
                    showMsgPage(intent.getExtras().getString("Result"),"","");
                    break;
                case 2://未检测到卡
                    hideMsgPage();
                    break;
                case 3: //成功获取卡号
                    String currentId = intent.getExtras().getString("Result");
                    Log.d(TAG, currentId);
                    updateCardUI(currentId);
                    break;
                default:
                    break;
            }
        }
    };

    private void hideMsgPage(){
        student_id=(TextView) findViewById(R.id.student_id);
        student_name=(TextView) findViewById(R.id.student_name);
        student_attendance=(TextView) findViewById(R.id.student_attendance);
        // 没有检测到卡，清空显示
        student_id.setText("学号：");
        student_name.setText("姓名：");
        student_attendance.setText("考勤次数：");
    }

    private void showMsgPage(String cardId,String stuName,String countNum){
        student_id=(TextView) findViewById(R.id.student_id);
        student_name=(TextView) findViewById(R.id.student_name);
        student_attendance=(TextView) findViewById(R.id.student_attendance);
        //TODO:刷卡后刷新打卡信息
        student_id.setText("学号："+cardId);
        student_name.setText("姓名："+stuName);
        student_attendance.setText("考勤次数："+countNum);//考勤次数，可以写死
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        /**RFID传感器绑定**/
        mModulesControl = new ModulesControl(uiHandler);
        mModulesControl.actionControl(true);

        /**数据库相关变量初始化**/
        mContext = this;
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        mDatabase = mDatabaseHelper.getReadableDatabase();

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
        student_id.setText("学号：");
        student_name.setText("姓名：");
        student_attendance.setText("考勤次数：");//考勤次数，可以写死

    }
    public void open_landingpage(){
        Intent intent =new Intent(this,LandingpageActivity.class);
        startActivity(intent);
    }

    /**
     * 查询一条记录
     * @param key
     * @param selectionArgs
     * @return 返回学号，姓名，打卡次数
     */
    private Map<String, String> searchHFCard(String key,String selectionArgs) {
        Cursor cursor = mDatabase.query(TABLE_NAME, null, key + "=?", new String[] {selectionArgs}, null, null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //double[] sumList = new double[cursor.getCount()];
        if (cursor.getCount() == 1) {
            /*
                private static final String CREATE_HF_TABLE = "CREATE TABLE HFCard("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "card_id TEXT,"
            + "student_id TEXT,"
            + "student_name TEXT,"
            + "count INTEGER DEFAULT 0);";
             */
            String stuid = cursor.getString(2);
            String stuname = cursor.getString(3);
            int count = cursor.getInt(4);
            cursor.close();
            Map<String, String> map = new HashMap<>();
            map.put("stuid", stuid);
            map.put("stuname", stuname);
            map.put("count", Integer.toString(count));
            return map;
        }else {
            cursor.close();
            return null;
        }
    }

    /**
     * 更新一条记录
     * @param key
     * @param data
     * @return 返回打卡次数字符串，错误返回null
     */
    private String updateHFCard(String key, String data,String Column, String value) {
        ContentValues values = new ContentValues();
        values.put(Column, value);
        int result =  mDatabase.update(TABLE_NAME, values, key + "=?",new String[]{data});
        if (result != 0) {
            return value;
        }
        return null;
    }

    /**
     *
     * @param CardId 卡号
     */
    private void updateCardUI(String CardId) {
        Map searchResult = searchHFCard(CARD_ID,CardId);
        if (searchResult == null) { //如果数据库中没有记录
            showMsgPage(getResources().getString(R.string.buscard_please_author_first),"","");

        } else if (searchResult.equals("-1")) {  //返回值为-1，数据库中搜索不止一个记录，错误
            showMsgPage(getResources().getString(R.string.buscard_search_more_than_one),"","");

        } else {  //返回打卡次数，更新UI
            int newCount = Integer.valueOf((String) searchResult.get("count")) + 1;
            if (Integer.toString(newCount).equals(updateHFCard(CARD_ID, CardId, COUNT, Integer.toString(newCount)))) {
                showMsgPage((String) searchResult.get("stuid"), (String) searchResult.get("stuname"),Integer.toString(newCount));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**用于接收group发送过来的广播**/
        /***用于接收group发送过来的广播***/
        IntentFilter filter = new IntentFilter(daka_action);
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * 在活动销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModulesControl.actionControl(false);
        mModulesControl.closeSerialDevice();
    }
}
