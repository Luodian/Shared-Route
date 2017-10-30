package com.example.administrator.sharedroute.localdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jne
 * Date: 2015/1/6.
 */
public class OrderDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "localdatabase.db";
    public static final String TABLE_item = "client";

    public OrderDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " + TABLE_item + " (id int primary key, type text, describe text , publishTime text, getTime text, getLocation text,sendTime text , sendLocation text , pickupCode text  , price Real , PorA integer)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {//当数据库需要更新时调用，根据前后版本号来判断，若一样则不跟新，每个数据库在建立的时候就会有一个版本号，即为构造函数中的版本号
        String sql = "DROP TABLE IF EXISTS " + TABLE_item;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}
