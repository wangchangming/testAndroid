package com.minbingtuan.mywork.model;

import org.json.JSONObject;

public class MyGroupModel {

	public String mGroupName;
	public String mId;

	public static MyGroupModel newInstance(JSONObject obj) {
		MyGroupModel model = new MyGroupModel();
		model.parseJson(obj);
		return model;
	}

	private void parseJson(JSONObject obj) {
		mGroupName = obj.optString("name");
		mId = obj.optString("id");
	}
}
