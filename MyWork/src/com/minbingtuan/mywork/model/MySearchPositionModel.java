package com.minbingtuan.mywork.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class MySearchPositionModel {

	private static MySearchPosition parseJson(JSONObject obj) {
		MySearchPosition pos = new MySearchPosition();
		pos.mType = obj.optInt("type");
		pos.mDateTime = obj.optString("time");
		pos.mPosition = obj.optString("position");
		return pos;
	}

	public static ArrayList<MySearchPosition> parseResponse(JSONObject response) {
		ArrayList<MySearchPosition> positionList = new ArrayList<MySearchPosition>();
		int status = response.optInt("status");
		if (status == 0) {
			JSONArray array = response.optJSONArray("positionList");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.optJSONObject(i);
					MySearchPosition pos = parseJson(json);
					positionList.add(pos);
				}
			}
		}
		return positionList;
	}
}
