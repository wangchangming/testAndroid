package com.minbingtuan.mywork;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyWorkDataDB extends SQLiteOpenHelper {
	public final static String TAG = "BonovoBroadDataDB";

	public final static String DATABASE_NAME = "WorkDataDB.db";
	public final static int DATABASE_VERSION = 4;

	public final static String GROUP_TABLE = "group_table";
	public final static String GROUP_ID = "group_id";
	public final static String GROUP_NAME = "group_name";

	public final static String PERSON_TABLE = "person_table";
	public final static String PERSON_ID = "person_id";
	public final static String PERSON_NAME = "person_name";
	public final static String PERSON_TEL = "person_tel";
	public final static String PERSON_GROUP = "person_group";

	public final static String CARD_TABLE = "card_table"; 
	public final static String CARD_ID = "card_id";
	public final static String CARD_TIME_ID = "card_time_id";
	public final static String CARD_TIME_YEAR = "card_time_year";
	public final static String CARD_TIME_MONTH = "card_time_month";
	public final static String CARD_TIME_DAY = "card_time_day";
	public final static String CARD_TIME_HOUR = "card_time_hour";
	public final static String CARD_TIME_MINUTE = "card_time_minute";
	public final static String CARD_TIME_TIME = "card_time_time";// save
																	// year-month-day-mm-hh
	public final static String CARD_TIME_TYPE = "card_time_type";
	
	enum CardTimeType{
		CARD_TIME_ON_DURTY,
		CARD_TIME_OFF_DURTY,
		CARD_TIME_GO_OUT,
		CARD_TIME_GO_BACK,
		CARD_TIME_DAY_OFF,
		CARD_TIME_DAY_BACK,
	}

	/**
	 * 构造器
	 * @param context
	 */
	public MyWorkDataDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = " CREATE TABLE " + GROUP_TABLE + " ( " + GROUP_ID
				+ " INTEGER primary key autoincrement, " + GROUP_NAME
				+ " text);";
		db.execSQL(sql);

		sql = "CREATE TABLE " + PERSON_TABLE + " (" + PERSON_ID
				+ " INTEGER primary key autoincrement, " + PERSON_NAME
				+ " text," + PERSON_TEL + " text, " + PERSON_GROUP + " text);";
		db.execSQL(sql);

		sql = " CREATE TABLE " + CARD_TABLE + "(" + CARD_ID
				+ " INTEGER primary key autoincrement, " + CARD_TIME_ID + " int,"
				+ CARD_TIME_YEAR + " int," + CARD_TIME_MONTH + " int,"
				+ CARD_TIME_DAY + " int," + CARD_TIME_HOUR + " int,"
				+ CARD_TIME_MINUTE + " int," + CARD_TIME_TIME + " text,"
				+ CARD_TIME_TYPE + " int);";

		db.execSQL(sql);

		Log.d(TAG, "onCreate();");
	}

	/**
	 * 更新表
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS " + GROUP_TABLE;
		db.execSQL(sql);
		
		sql = "DROP TABLE IF EXISTS " + PERSON_TABLE;
		db.execSQL(sql);
		
		sql = "DROP TABLE IF EXISTS " + CARD_TABLE;
		db.execSQL(sql);

		onCreate(db);
	}

	/**
	 * 查询传入参数表中的数据，返回结果集
	 * @param name
	 * @return
	 */
	public Cursor selectTable(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(name, null, null, null, null, null,
				"person_id desc");
		return cursor;
	}

	/**
	 * 新增一个部门名称，返回插入烦人记录数
	 * @param name
	 * @return
	 */
	public long insertGroup(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(GROUP_NAME, name);
		long row = db.insert(GROUP_TABLE, null, cv);
		db.close();
		return row;
	}

	/**
	 * 新增一个人的记录信息，返回插入烦人记录数
	 * @param name
	 * @param tel
	 * @param group
	 * @return
	 */
	public long insertPerson(String name, String tel, String group) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(PERSON_NAME, name);
		cv.put(PERSON_TEL, tel);
		cv.put(PERSON_GROUP, group);
		long row = db.insert(PERSON_TABLE, null, cv);
		db.close();
		return row;
	}

	/**
	 * 新增一条时间记录，返回插入的记录数
	 * @param id
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param time
	 * @param type
	 * @return
	 */
	public long insertAttendance(int id, int year, int month, int day,
			int hour, int minute, String time, int type) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(CARD_TIME_ID, id);
		cv.put(CARD_TIME_YEAR, year);
		cv.put(CARD_TIME_MONTH, month);
		cv.put(CARD_TIME_DAY, day);
		cv.put(CARD_TIME_HOUR, hour);
		cv.put(CARD_TIME_MINUTE, minute);
		cv.put(CARD_TIME_TIME, time);
		cv.put(CARD_TIME_TYPE, type);
		long row = db.insert(CARD_TABLE, null, cv);
		db.close();
		return row;
	}
}
