package cmccsi.mhealth.app.sports.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.baidu.mapapi.map.MapView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;

public class GetWindowBitmap {

	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity,View layout) {

		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
//		Rect frame = new Rect();
//		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		int statusBarHeight = frame.top;
//		System.out.println(statusBarHeight);
//		
//		//获取标题栏高度
//		int titleHeight= activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
//		int windowheight = activity.getWindowManager().getDefaultDisplay().getHeight();
//		int cutheight=windowheight-titleHeight;
		// 获取屏幕长和高
//		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		
		 int[] location = new int[2];  
		 layout.getLocationOnScreen(location);
		 int x = location[0];  
         int y = location[1];  
         int width = layout.getWidth();
         int height = layout.getHeight();

		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
//		Bitmap b = Bitmap.createBitmap(b1, x, y, right, heigh1t);
//		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height-statusBarHeight);
         Bitmap b = null;
         try {
        	 if(height+y>b1.getHeight())
        	 {
        		 b  = Bitmap.createBitmap(b1, x, y, width, b1.getHeight()-y);
        	 }
        	 else
        	 {
        		 b  = Bitmap.createBitmap(b1, x, y, width, height);
        	 }
		} catch (Exception e) {
			b = null;
			e.printStackTrace();
		}
        b1.recycle();
		view.destroyDrawingCache();
		return b;
	}


	// 保存到sdcard
	private static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 程序入口
	public static void shoot(Activity a,View view) {
		GetWindowBitmap.savePic(GetWindowBitmap.takeScreenShot(a,view), "sdcard/xx.png");
	}
}
