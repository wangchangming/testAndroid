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

	public final static String CARD_TABLE = "card_table"; // �򿪱����
	public final static String CARD_ID = "card_id";// ��ID
	public final static String CARD_TIME_ID = "card_time_id";// ���˵�id����Ӧperson_table�е�person_id
	public final static String CARD_TIME_YEAR = "card_time_year";// �򿪵�ʱ��
	public final static String CARD_TIME_MONTH = "card_time_month";
	public final static String CARD_TIME_DAY = "card_time_day";
	public final static String CARD_TIME_HOUR = "card_time_hour";
	public final static String CARD_TIME_MINUTE = "card_time_minute";
	public final static String CARD_TIME_TIME = "card_time_time";// save
																	// year-month-day-mm-hh
	public final static String CARD_TIME_TYPE = "card_time_type";// �����ͣ��ϰ࣬�°�
	
	enum CardTimeType{
		CARD_TIME_ON_DURTY,//�ϰ�
		CARD_TIME_OFF_DURTY,//�°�
		CARD_TIME_GO_OUT,//���
		CARD_TIME_GO_BACK,//�ع�
		CARD_TIME_DAY_OFF,//���
		CARD_TIME_DAY_BACK,//���
	}

	public MyWorkDataDB(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

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

	public Cursor selectTable(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(name, null, null, null, null, null,
				"person_id desc");
		return cursor;
	}

	public long insertGroup(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(GROUP_NAME, name);
		long row = db.insert(GROUP_TABLE, null, cv);
		db.close();
		return row;
	}

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
