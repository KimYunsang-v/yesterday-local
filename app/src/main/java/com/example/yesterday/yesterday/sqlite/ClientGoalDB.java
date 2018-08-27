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
        db.execSQL("CREATE TABLE ClientGoal (food TEXT, totalCount INT, startDate TEXT, endDate TEXT, favorite INT, type TEXT, PRIMARY KEY(food,type));");
        Log.i("create ClientGoal","success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String addGoal(SQLiteDatabase db, String food, int totalCount, String startDate, String endDate, int favorite, String type) {
        // 읽고 쓰기가 가능하게 DB 열기
        //SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO ClientGoal VALUES('" + food + "', '" + totalCount + "', '" + startDate + "', '" + endDate +"', '" + favorite + "','" +type + "');");
        db.close();

        Log.i("addGoal","success");

        return "success";
    }

    public String deleteGoal(SQLiteDatabase db,String food, String type) {
        db.execSQL("DELETE FROM ClientGoal WHERE food='" + food + "' and type= '" + type + "';");

        Log.i("deleteGoal","success");

        return "success";
    }

    public ArrayList<RecyclerItem> selectGoal(SQLiteDatabase db) {
        String food = "";
        int count=0;
        String startDate="";
        String endDate = "";
        int favorite = 0;
        String type = "";
        // 읽고 쓰기가 가능하게 DB 열기
        //SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        Cursor cursor = db.rawQuery("SELECT * FROM ClientGoal ORDER BY endDate ASC;",null);

        if (cursor != null && cursor.getCount() != 0)
            cursor.moveToFirst();

        while(cursor.moveToNext()){
            food = cursor.getString(cursor.getColumnIndex("food"));
            count = cursor.getInt(cursor.getColumnIndex("totalCount"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            type = cursor.getString(cursor.getColumnIndex("type"));
            Log.i("selectGoal",food);

            items.add(new RecyclerItem(food, count, startDate, endDate, favorite, type));
        }

        cursor.close();
        db.close();

        Log.i("addGoal","success");

        return items;
    }

    public void checkType(String food, int favorite,String type) {

        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        if(favorite == 1) {
            // DB에 입력한 값으로 행 추가
            db.rawQuery("update clientgoal set favorite='0' ,type= 'success' where food= '"+ food +"' and type='"+type+"';", null);
        }
        else{
            db.rawQuery("update clientgoal set type= 'success' where food= '"+ food +"' and type='"+type+"';", null);
        }
        db.close();

        Log.i("checkType","success");
    }
}