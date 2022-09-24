package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Amber on 2015/4/8.数据库单例类
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = ".Database";
    private static DatabaseHelper mInstance = null;

    /**高频卡数据库名称**/
    public static final String DATABASE_NAME = "cards.db";

    /**数据库版本号**/
    private static final int DATABASE_VERSION = 1;

    /**创建高频表的SQL语句**/
    private static final String CREATE_HF_TABLE = "CREATE TABLE HFCard("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "card_id TEXT,"
            + "student_id TEXT,"
            + "student_name TEXT,"
            + "count INTEGER DEFAULT 0);";


    /**构造函数**/
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**单例模式 初始化函数**/
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HF_TABLE);
        Log.d(TAG, "onCreate run");
        insertData(db, "343E9669", "18376255", "丁一洋");
        insertData(db, "5A5C38A5", "18375123", "朱穆清");
        insertData(db, "8AA33EA5", "18374046", "邓浩");
    }
    /*
    * 手动插入学生信息
    * */
    private void insertData(SQLiteDatabase db, String card_id, String student_id, String student_name) {
        ContentValues cv = new ContentValues();
        cv.put("card_id", card_id);
        cv.put("student_id", student_id);
        cv.put("student_name", student_name);
        db.insert("HFCard", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**可以拿到当前数据库的版本信息 与之前数据库的版本信息   用来更新数据库**/
    }

    /**
     * 删除数据库
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
