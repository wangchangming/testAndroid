package com.example.attitude.util;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.attitude.module.AttitudeDesign;
import com.example.attitude.module.DesignDetail;

/**
 * 数据库,操作类
 * 
 * @author Savvy
 * 
 */
public class MytabOperate {
	private SQLiteDatabase db = null;

	public MytabOperate(SQLiteDatabase db) {
		this.db = db;
	}

	// 关闭数据库
	public void closeDB() {
		this.db.close();
	}

	public void formatDataBase() {
		String sql = "DROP TABLE IF EXISTS " + StaticProperty.TABLEATTITUDE;
		// String sql2 = "DROP TABLE IF EXISTS " + StaticProperty.TABLEDETAIL;
		String sql3 = "CREATE TABLE "
				+ StaticProperty.TABLEATTITUDE // 主列表
				+ "("
				+ "id			INTEGER				PRIMARY KEY ," // 自增
				+ "dsg_name 	VARCHAR(50) 		NOT NULL ,"
				+ "dsg_title 	VARCHAR(50)  		NOT NULL ,"
				+ "dsg_tel 			INTEGER  ," + "dsg_email 		VARCHAR(50)    ,"
				+ "dsg_sex 		INTEGER    ," + "dsg_level 		INTEGER    ,"
				+ "dsg_photo 		VARCHAR(1000)    ,"
				+ "group_photo 		VARCHAR(1000)    ,"
				+ "group_id 		INTEGER   		NOT NULL ,"
				+ "group_name 		VARCHAR(30)    ,"
				+ "group_theme 		VARCHAR(1000)    ,"
				+ "group_photo_num 	INTEGER 		" + ")";
		// String sql4 = "CREATE TABLE "
		// + StaticProperty.TABLEDETAIL // 详情表
		// + "("
		// + "id			INTEGER				PRIMARY KEY ," // 自增
		// + "photo_url 	VARCHAR(500) 		NOT NULL ,"
		// + "photo_infor 	VARCHAR(1000) 		 ,"
		// + "aid 			INTEGER 		NOT NULL " + ")";
		db.execSQL(sql);
		// db.execSQL(sql2);
		db.execSQL(sql3);
		// db.execSQL(sql4);
	}

	public void insertAttitudeDesign(AttitudeDesign attitude) {
//		System.out.println(attitude + "attitude**********");
		String sql = "INSERT INTO "
				+ StaticProperty.TABLEATTITUDE
				+ "(dsg_name,dsg_title,dsg_tel,dsg_email,dsg_sex,dsg_level,"
				+ "dsg_photo,group_photo,group_id,group_name,group_theme,group_photo_num) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		Object args[] = new Object[] { attitude.getDsg_name(),
				attitude.getDsg_title(), attitude.getDsg_tel(),
				attitude.getDsg_email(), attitude.getDsg_sex(),
				attitude.getDsg_level(), attitude.getDsg_photo(),
				attitude.getGroup_photo(), attitude.getGroup_id(),
				attitude.getGroup_name(), attitude.getGroup_theme(),
				attitude.getGroup_photo_num() };
		this.db.execSQL(sql, args);
	}

	public AttitudeDesign findAttitudeDesignById(String group_id) {
		String sql = "SELECT dsg_name,dsg_title,dsg_tel,dsg_email,dsg_sex,dsg_level,"
				+ "dsg_photo,group_photo,group_id,group_name,group_theme,group_photo_num FROM "
				+ StaticProperty.TABLEATTITUDE + " WHERE group_id=?";
		String args[] = new String[] { group_id };
		Cursor result = this.db.rawQuery(sql, args);
		AttitudeDesign attitude = new AttitudeDesign();
		if (result.getCount() > 0) {
			result.moveToFirst();
			attitude.setDsg_name(result.getString(0));
			attitude.setDsg_title(result.getString(1));
			attitude.setDsg_tel(result.getInt(2));
			attitude.setDsg_email(result.getString(3));
			attitude.setDsg_sex(result.getInt(4));
			attitude.setDsg_level(result.getInt(5));
			attitude.setDsg_photo(result.getString(6));
			attitude.setGroup_photo(result.getString(7));
			attitude.setGroup_id(result.getInt(8));
			attitude.setGroup_name(result.getString(9));
			attitude.setGroup_theme(result.getString(10));
			attitude.setGroup_photo_num(result.getInt(11));
		}
		return attitude;
	}

	public List<AttitudeDesign> findAllAttitudeDesign() {
		List<AttitudeDesign> all = new ArrayList<AttitudeDesign>();
		String sql = "SELECT dsg_name,dsg_title,dsg_tel,dsg_email,dsg_sex,dsg_level,"
				+ "dsg_photo,group_photo,group_id,group_name,group_theme,group_photo_num FROM "
				+ StaticProperty.TABLEATTITUDE;
		String args[] = new String[] {};
		Cursor result = this.db.rawQuery(sql, args);
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			AttitudeDesign attitude = new AttitudeDesign();
			attitude.setDsg_name(result.getString(0));
			attitude.setDsg_title(result.getString(1));
			attitude.setDsg_tel(result.getInt(2));
			attitude.setDsg_email(result.getString(3));
			attitude.setDsg_sex(result.getInt(4));
			attitude.setDsg_level(result.getInt(5));
			attitude.setDsg_photo(result.getString(6));
			attitude.setGroup_photo(result.getString(7));
			attitude.setGroup_id(result.getInt(8));
			attitude.setGroup_name(result.getString(9));
			attitude.setGroup_theme(result.getString(10));
			attitude.setGroup_photo_num(result.getInt(11));
			attitude.setShowFlag(true);
			all.add(attitude);
		}
		return all;
	}

	public void insertDesignDetail(DesignDetail designDetail, int aid) {
//		 System.out.println("插入数据库**********");
		String sql = "INSERT INTO " + StaticProperty.TABLEDETAIL
				+ "(photo_url,photo_infor,aid) " + "VALUES (?,?,?)";
		Object args[] = new Object[] { designDetail.getPhoto_url(),
				designDetail.getPhoto_infor(), aid };
		this.db.execSQL(sql, args);
//		System.out.print("插入成功！！！！！！！！！！！");
	}

	public List<DesignDetail> findDesignDetailById(String aid) {
		String sql = "SELECT photo_url,photo_infor,aid FROM "
				+ StaticProperty.TABLEDETAIL + " WHERE aid=?";
		String args[] = new String[] { aid };
		Cursor result = this.db.rawQuery(sql, args);
		List<DesignDetail> detailList = new ArrayList<DesignDetail>();
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			DesignDetail designDetail = new DesignDetail();
			designDetail.setPhoto_url(result.getString(0));
			designDetail.setPhoto_infor(result.getString(1));
			designDetail.setAid(result.getInt(2));
			detailList.add(designDetail);
		}
		return detailList;
	}

	public boolean findDesignDetailByUrl(String photo_url) {
		String sql = "SELECT photo_url,photo_infor,aid FROM "
				+ StaticProperty.TABLEDETAIL + " WHERE photo_url=?";
//		System.out.println("查询数据库**********"+photo_url);
		String args[] = new String[] { photo_url };
		Cursor result = this.db.rawQuery(sql, args);
		DesignDetail designDetail = null;
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			designDetail = new DesignDetail();
			designDetail.setPhoto_url(result.getString(0));
			designDetail.setPhoto_infor(result.getString(1));
			designDetail.setAid(result.getInt(2));
		}
//		System.out.println(designDetail+"!!!!!!!!!!!!!!!!!!!photo_url");
		boolean flag = true;
		if (designDetail != null) {
			flag = false;
		}
		return flag;
	}
}
