package com.minbingtuan.database;

import android.database.sqlite.SQLiteDatabase;

public class DBOperate {

    //数据表名称
    private static final String TABLENAME = "uesr";
    private SQLiteDatabase db = null; 
    
    public DBOperate(SQLiteDatabase db){
        this.db = db;
    }
    
    private void insert() {
        
    }
}
