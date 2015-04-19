package baidumapsdk.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

/**
 * 热力图功能demo
 */
public class HeatMapDemo extends Activity {
	@SuppressWarnings("unused")
	private static final String LTAG = BaseMapDemo.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private HeatMap heatmap;
	private Button mAdd;
	private Button mRemove;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heatmap);
		mMapView = (MapView) findViewById(R.id.mapview);
		mBaiduMap = mMapView.getMap();
		mAdd = (Button) findViewById(R.id.add);
		mRemove = (Button) findViewById(R.id.remove);
		mAdd.setEnabled(false);
		mRemove.setEnabled(false);
		mAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addHeatMap();
			}
		});
		mRemove.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				heatmap.removeHeatMap();
				mAdd.setEnabled(true);
				mRemove.setEnabled(false);
			}
		});
		addHeatMap();
	}

	private void addHeatMap() {
		final Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mBaiduMap.addHeatMap(heatmap);
				mAdd.setEnabled(false);
				mRemove.setEnabled(true);
			}
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<LatLng> data = getRandomLocations();
				heatmap = new HeatMap.Builder().data(data).build();
				h.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}

	private List<LatLng> getRandomLocations() {
		// get random 5000
		List<LatLng> randomList = new ArrayList<LatLng>();
		Random r = new Random();
		for (int i = 0; i < 500; i++) {
			// 116.220000,39.780000 116.570000,40.150000
			int rlat = r.nextInt(370000);
			int rlng = r.nextInt(370000);
			int lat = 39780000 + rlat;
			int lng = 116220000 + rlng;
			LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
			randomList.add(ll);
		}
		return randomList;
	}
}
