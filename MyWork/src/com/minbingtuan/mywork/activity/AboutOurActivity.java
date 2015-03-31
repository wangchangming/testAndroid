package com.minbingtuan.mywork.activity;

import java.io.InputStream;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.Tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class AboutOurActivity extends Activity implements OnClickListener {

	private BitMatrix matrix;
	private ImageView img;
	
	//屏幕的宽度
	public static int WIDTH;
	//屏幕的高度
	public static int HEIGHT;
	// 图片宽度的一般
	private static  int IMAGE_HALFWIDTH ;
	// 需要插图图片的大小 这里设定为150*150
	int[] mPixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_our);

		getWH();
		IMAGE_HALFWIDTH = WIDTH/18;
		findViewById(R.id.re).setOnClickListener(this);
		img = (ImageView) findViewById(R.id.img);
		try {
			img.setImageBitmap(CreateCode("http://fir.im/minbingtuan"));
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re:
			finish();
			break;

		default:
			break;
		}
	}

	private Bitmap CreateCode(String str) throws WriterException {
		//把drawable下的图片转换成bitmap
		InputStream is = getResources().openRawResource(R.drawable.mbt);  
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		
		// 缩放一个40*40的图片
		mBitmap = Tools.zoomBitmap(mBitmap, IMAGE_HALFWIDTH);
		
		QRCodeWriter writer = new QRCodeWriter();
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,
				WIDTH/2, WIDTH/2, hints);
		
		int width = matrix.getWidth();
		int hight = matrix.getHeight();
		
		int halfW = width/2;
		int halfH = hight/2;
		
		int[] pixels = new int[width*hight];
		
		for(int y=0;y<hight;y++){
			for(int x=0;x<width;x++){
				if(x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
						&& y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH){
					pixels[y * width + x] = mBitmap.getPixel(x - halfW
							+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				}else{
					if(matrix.get(x, y)){
						pixels[y*width + x] = 0xff000000;
					}else{
						
					}
				}
			}
		}
		Bitmap map = Bitmap.createBitmap(width,hight,Bitmap.Config.ARGB_8888);
		map.setPixels(pixels, 0, width, 0, 0, width, hight);
		return map;

	}
	
	public void getWH(){
		WindowManager wm = this.getWindowManager();
		WIDTH = wm.getDefaultDisplay().getWidth();
		HEIGHT = wm.getDefaultDisplay().getHeight();
		
	}

}
