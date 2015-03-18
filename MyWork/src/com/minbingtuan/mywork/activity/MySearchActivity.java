package com.minbingtuan.mywork.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.model.MySearchPosition;
import com.minbingtuan.mywork.model.MySearchPositionModel;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MySearchActivity extends Activity implements OnClickListener, View.OnTouchListener {
	private LayoutInflater inflater;
	private RadioButton buttonWork;
	private RadioButton buttonSetting;
	private ImageButton buttonQuery;
	private TextView textViewStartTime;
	private TextView textViewEndTime;
	private DatePicker datePicker;
	private RadioGroup mRadioGroup;

	Intent intent;
	ViewHolder holder;
	ListView mListview;
	BaseAdapter mBaseAdapter;
	IntentFilter intentFilter;

	private MyApplication myApp;
	private int curCheckId = R.id.buttonSearch;
	List<MySearchPosition> mList = new ArrayList<MySearchPosition>();
	Calendar cal = Calendar.getInstance();
	String time1 = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + 1;
	String time2 = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-"
			+ cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		myApp = (MyApplication) getApplication();
		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.layoutTitle);
		buttonWork = (RadioButton) titleLayout.findViewById(R.id.buttonWork);
		buttonSetting = (RadioButton) titleLayout.findViewById(R.id.buttonSetting);
		mRadioGroup = (RadioGroup) titleLayout.findViewById(R.id.main_radio);
		textViewStartTime = (TextView) findViewById(R.id.textViewStartTime);
		textViewEndTime = (TextView) findViewById(R.id.textViewEndTime);
		mListview = (ListView) findViewById(R.id.listView1);
		buttonQuery = (ImageButton) findViewById(R.id.buttonQuery);

		textViewStartTime.setText(time1);
		textViewEndTime.setText(time2);

		mRadioGroup.check(curCheckId);

		buttonWork.setOnClickListener(this);
		buttonSetting.setOnClickListener(this);

		textViewStartTime.setOnTouchListener(this);
		textViewEndTime.setOnTouchListener(this);
		buttonQuery.setOnClickListener(this);

		mBaseAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				ViewHolder holder;
				if (convertView == null) {
					LayoutInflater inflater = LayoutInflater.from(parent.getContext());
					view = inflater.inflate(R.layout.list_item_search, parent, false);
					holder = new ViewHolder();
					holder.mWorkOnOrOff = (TextView) view.findViewById(R.id.textViewWorkOnOrWorkOff);
					holder.mRegisterTime = (TextView) view.findViewById(R.id.textViewRegisterTime);
					view.setTag(holder);
				} else {
					view = convertView;
				}
				holder = (ViewHolder) view.getTag();
				String[] typeName = getResources().getStringArray(R.array.work_type_list);
				holder.mWorkOnOrOff.setText(typeName[mList.get(position).mType]);
				holder.mRegisterTime.setText(mList.get(position).mDateTime);
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mList.size();
			}
		};
		mListview.setAdapter(mBaseAdapter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonWork:
			intent = new Intent(MySearchActivity.this, MyWorkActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonSetting:
			intent = new Intent(MySearchActivity.this, SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonQuery:
			if (!myApp.isConnect()) {
				Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
				return;
			}
			startSearch();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.date_time_dialog, null);
			datePicker = (DatePicker) view.findViewById(R.id.date_picker);
			builder.setView(view);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());

			if (v.getId() == R.id.textViewStartTime) {
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, null);
				int inType = textViewStartTime.getInputType();
				textViewStartTime.setInputType(InputType.TYPE_NULL);
				textViewStartTime.onTouchEvent(event);
				textViewStartTime.setInputType(inType);

				builder.setTitle(getString(R.string.SelectStartTime));
				builder.setPositiveButton(getString(R.string.determine), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1,
								datePicker.getDayOfMonth()));
						textViewStartTime.setText(sb);
						textViewEndTime.requestFocus();
						dialog.cancel();
					}
				});
			} else if (v.getId() == R.id.textViewEndTime) {
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.getMinimum(Calendar.MONTH),
						null);
				int inType = textViewEndTime.getInputType();
				textViewEndTime.setInputType(InputType.TYPE_NULL);
				textViewEndTime.onTouchEvent(event);
				textViewEndTime.setInputType(inType);

				builder.setTitle(getString(R.string.SelectEndTime));
				builder.setPositiveButton(getString(R.string.determine), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1,
								datePicker.getDayOfMonth()));
						textViewEndTime.setText(sb);
						dialog.cancel();
					}
				});
			}
			Dialog dialog = builder.create();
			dialog.show();
		}
		return true;
	}

	public void startSearch() {
		RequestQueue queue = Volley.newRequestQueue(this);
		MyApplication mApp = (MyApplication) getApplication();
		String id = Integer.toString(mApp.getUserId());
		String startTime = textViewStartTime.getText().toString();
		String endTime = textViewEndTime.getText().toString();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("managerId", id);
		params.put("beginTime", startTime);
		params.put("endTime", endTime);
		String url = MyApplication.LocalGETTIMEUrl;
		url += StringUtils.encodeUrl(params);

		Log.d("whui", url.toString());
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {

						mList = MySearchPositionModel.parseResponse(response);
						mBaseAdapter.notifyDataSetChanged();
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

	public final class ViewHolder {
		public TextView mWorkOnOrOff;
		public TextView mRegisterTime;
	}
}
