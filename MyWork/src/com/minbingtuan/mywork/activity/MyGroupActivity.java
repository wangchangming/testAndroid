package com.minbingtuan.mywork.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.model.MyGroupModel;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

public class MyGroupActivity extends Activity implements OnClickListener {
	ListView mListview;
	BaseAdapter mBaseApdater;
	List<MyGroupModel> mList = new ArrayList<MyGroupModel>();
	int mSelectIndex = 0;
	Button btn_confirm;

	MyApplication myApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		myApp = (MyApplication) getApplication();
		mListview = (ListView) findViewById(R.id.ListView1);
		btn_confirm = (Button) findViewById(R.id.button_confirm);
		btn_confirm.setOnClickListener(this);

		mSelectIndex = this.getIntent().getExtras().getInt("groupIndex");
		mBaseApdater = new BaseAdapter() {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mList.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = null;
				ViewHolder holder;
				if (convertView == null) {
					LayoutInflater inflater = LayoutInflater.from(parent.getContext());
					view = inflater.inflate(R.layout.list_item_group, parent, false);
					holder = new ViewHolder();
					holder.textName = (CheckedTextView) view.findViewById(R.id.checked_group_name);
					view.setTag(holder);
				} else {
					view = convertView;
				}
				holder = (ViewHolder) view.getTag();
				String name = mList.get(position).mGroupName;
				holder.textName.setText(name);
				if (mSelectIndex == position) {
					holder.textName.setChecked(true);
					holder.textName.setTextColor(R.color.blue);
				} else {
					holder.textName.setChecked(false);
					holder.textName.setTextColor(R.color.white);
				}
				return view;
			}
		};
		mListview.setAdapter(mBaseApdater);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mSelectIndex = (int) (arg2 + arg3);
				mBaseApdater.notifyDataSetChanged();
			}
		});
		init();

	}

	public void init() {
		RequestQueue queue = Volley.newRequestQueue(this);
		String url = MyApplication.LocalGroupUrl;

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						int status = response.optInt("status");
						if (status == 0) {
							JSONArray array = response.optJSONArray("groupList");
							if (array != null) {
								for (int i = 0; i < array.length(); i++) {
									JSONObject json = array.optJSONObject(i);
									MyGroupModel model = MyGroupModel.newInstance(json);
									mList.add(model);
									mBaseApdater.notifyDataSetChanged();
								}
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								VolleyErrorHelper.handleServerError(error, getApplication()), Toast.LENGTH_SHORT)
								.show();
					}

				});
		queue.add(jsObjRequest);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_confirm:
			if (mList.size() == 0) {
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("name", mList.get(mSelectIndex).mGroupName);
			intent.putExtra("id", mList.get(mSelectIndex).mId);
			intent.putExtra("groupIndex", mSelectIndex);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}

	static class ViewHolder {

		protected CheckedTextView textName;

	}
}
