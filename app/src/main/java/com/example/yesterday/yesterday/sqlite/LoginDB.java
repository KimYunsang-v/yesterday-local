package com.example.yesterday.yesterday.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDB extends SQLiteOpenHelper{

    public LoginDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Login (id TEXT PRIMARY KEY, pw TEXT, name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String create_at, String id, String pw, String name) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO Login VALUES('" + id + "', " + pw + ", '" + name + "');");
        db.close();
    }

    public String getResult(String parent_id, String parent_pw) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Boolean loginCheck = false;

        String id = "";
        String pw = "";
        String name = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Login", null);
        while (cursor.moveToNext()) {
            id = cursor.getString(1);
            pw = cursor.getString(2);
            name = cursor.getString(3);

            if (parent_id.equals(id) && parent_pw.equals(pw)) {
                loginCheck = true;
                break;
            }
        }

        if(loginCheck)
            result = name;
        else
            result = "fail";

        return result;
    }
}

