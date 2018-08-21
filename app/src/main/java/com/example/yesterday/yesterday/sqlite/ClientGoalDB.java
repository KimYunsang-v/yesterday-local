package com.example.yesterday.yesterday.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;

import java.util.ArrayList;

public class ClientGoalDB extends SQLiteOpenHelper {
    ArrayList items = new ArrayList<RecyclerItem>();

    public ClientGoalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ClientGoal (food TEXT PRIMARY KEY, totalCount INT, startDate TEXT, endDate TEXT, favorite INT, type TEXT PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addGoal(String create_at, String food, String totalCount, String startDate, String endDate, int favorite, String type) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO ClientGoal VALUES('" + food + "', " + totalCount + ", '" + startDate + ", '" + endDate +", '" + favorite + "'+type +');");
        db.close();

        Log.i("addGoal","success");
    }

    public void deleteGoal(String food, String type) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM ClientGoal WHERE food='" + food + "' and type=" + type + "';");

        Log.i("deleteGoal","success");
    }

    public ArrayList<RecyclerItem> selectGoal() {
        String food = "";
        int count=0;
        String startDate="";
        String endDate = "";
        int favorite = 0;
        String type = "";
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        // DB에 입력한 값으로 행 추가
        Cursor cursor = db.rawQuery("SELECT * FROM ClientGoal ORDER BY endDate ASC;",null);

        while(cursor.moveToNext()){
            food = cursor.getString(1);
            count = cursor.getInt(2);
            startDate = cursor.getString(3);
            endDate = cursor.getString(4);
            favorite = cursor.getInt(5);
            type = cursor.getString(6);

            items.add(new RecyclerItem(food, count, startDate, endDate, favorite, type));
        }

        cursor.close();
        db.close();

        Log.i("addGoal","success");

        return items;
    }
}