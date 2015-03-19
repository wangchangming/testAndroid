package com.minbingtuan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelpler extends SQLiteOpenHelper {

    //数据库名称
    private static final String DATABASENAME = "UserInfo.db";
    //数据库版本
    private static final int DATABASEVERSION = 1;
    //数据表名称
    private static final String TABLENAME = "uesr";
    
    public DBHelpler(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立用户表
        String sql = "create table "+TABLENAME+
                "(" 
                +"id INTEGER PRIMARY KEY ,"
                +"userid int not null,"
                +"username varchar(50) not null,"
                +"realname varchar(20) not null,"
                +"password varchar(50) not null,"
                +"mobile   varchar(20) not null,"
                +"email    varchar(20),"
                +"groupId  int,"
                +"birthday varchar(20)," 
                +"groupName varchar(50)"
        		+")";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLENAME;
        db.execSQL(sql);
        this.onCreate(db);
    }

}
