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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutOurActivity extends Activity implements OnClickListener {

	private BitMatrix matrix;
	private ImageView img;
	
	//屏幕的宽度
	public static int WIDTH;
	//屏幕的高度
	public static int HEIGHT;
	// 图片宽度
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
		img.setImageBitmap(createImage("http://fir.im/minbingtuan"));
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
		
		// 缩放一个logo图片用于放在中间
		mBitmap = Tools.zoomBitmap(mBitmap, IMAGE_HALFWIDTH);
		
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		//定义二维码图片的宽高
		matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,
				WIDTH/2, WIDTH/2, hints);
		
		//虎丘二维码图片的宽高
		int width = matrix.getWidth();
		int hight = matrix.getHeight();
		
		int[] pixels = new int[width*hight];
		
		for(int y=0;y<hight;y++){
			for(int x=0;x<width;x++){
				if(matrix.get(x, y)){
					pixels[y*width + x] = 0xff000000;
				}else{
						
				}
			}
		}
		Bitmap map = Bitmap.createBitmap(width,hight,Bitmap.Config.ARGB_8888);
		map.setPixels(pixels, 0, width, 0, 0, width, hight);
		return map;

	}
	
	public void getWH(){
		DisplayMetrics dm = new DisplayMetrics();
		//获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		HEIGHT = dm.heightPixels;
		WIDTH = dm.widthPixels;
		
		
	}
	
	// 生成QR图
    private Bitmap createImage(String str) {
    	//把drawable下的图片转换成bitmap,用以显示logo
		InputStream is = getResources().openRawResource(R.drawable.mbt);  
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		
		// 缩放一个logo图片用于放在中间
		mBitmap = Tools.zoomBitmap(mBitmap, IMAGE_HALFWIDTH);
		
        try {

        	//设置二维码的格式
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            
            //定义二维码图片的宽高
            BitMatrix bitMatrix = new QRCodeWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH*2/3, WIDTH*2/3, hints);
            
            
            //获取二维码图片的宽高
            int width = bitMatrix.getWidth();
    		int hight = bitMatrix.getHeight();
            //创建一个像素数组
            int[] pixels = new int[width * hight];
    		
            for (int y = 0; y < hight; y++) {
                for (int x = 0; x < width; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * WIDTH/2 + x] = 0xff000000;
//                    } else {
//                        pixels[y * WIDTH/2 + x] = 0xffffffff;
//                    }
                	
                	if (x > width/2 - IMAGE_HALFWIDTH && x < width/2 + IMAGE_HALFWIDTH
    						&& y > hight/2 - IMAGE_HALFWIDTH
    						&& y < hight/2 + IMAGE_HALFWIDTH ) {
    					pixels[y * width + x] = mBitmap.getPixel(x - width/2
    							+ IMAGE_HALFWIDTH, y - hight/2 + IMAGE_HALFWIDTH);
    				} else {
    					if (bitMatrix.get(x, y)) {
    						pixels[y * width + x] = 0xff000000;
    					} else { // 无信息设置像素点为白色
//    						pixels[y * width + x] = 0xffffffff;
    					}
    				}
                }
            }

            Bitmap map = Bitmap.createBitmap(width, hight,
            		Bitmap.Config.ARGB_8888);

            map.setPixels(pixels, 0, width, 0, 0, width, hight);
            return map;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
