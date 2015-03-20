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
                +"userid INTEGER not null,"
                +"username VARCHAR(50) NOT NULL,"
                +"realname VARCHAR(20) NOT NULL,"
                +"password VARCHAR(50) NOT NULL,"
                +"mobile   VARCHAR(20) NOT NULL,"
                +"email    VARCHAR(20),"
                +"groupId  INTEGER,"
                +"birthday VARCHAR(20)," 
                +"groupName VARCHAR(50)"
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
