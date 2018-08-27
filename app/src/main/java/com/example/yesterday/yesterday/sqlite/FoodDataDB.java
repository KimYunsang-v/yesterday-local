package com.example.yesterday.yesterday.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yesterday.yesterday.RecyclerView.FoodItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodDataDB extends SQLiteOpenHelper {

    ArrayList items;

    public FoodDataDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FoodData (food TEXT,date TEXT, BLDtime TEXT,PRIMARY KEY(food,date));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // fooddata에 음식 추가
    public void insert(String create_at, String food, String date, String BLDtime) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO FoodData VALUES('" + food + "', " + date + ", '" + BLDtime + "');");
        db.close();
    }


    // 음식의 이름, count select
    public HashMap<String,Integer> selectGroupBy() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        String food="";
        int currentCount=0;

        HashMap<String,Integer> foodCountMap = new HashMap<String,Integer>();

        Cursor cursor = db.rawQuery("select food,count(*) from fooddata group by food;",null);

        // 나중에 기간에 맞는 음식만 추가
        while(cursor.moveToNext()){
            food = cursor.getString(1);
            currentCount = cursor.getInt(2);

            foodCountMap.put(food,currentCount);
        }

        return foodCountMap;
    }

    // 음식의 이름, count select
    public ArrayList<FoodItem> selectAll() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        String food = "";
        String date = "";
        String BLDtime = "";
        items = new ArrayList<FoodItem>();

       // HashMap<String,Integer> foodCountMap = new HashMap<String,Integer>();

        Cursor cursor = db.rawQuery("select * from fooddata",null);

        // 나중에 기간에 맞는 음식만 추가
        while(cursor.moveToNext()){
            food = cursor.getString(1);
            date = cursor.getString(2);
            BLDtime = cursor.getString(3);

            items.add(new FoodItem(food,date,BLDtime));
        }
        return items;
    }
}
