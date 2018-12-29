package cmccsi.mhealth.app.sports.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;

public class Logger {
	public static boolean LogDisable = false;

	public static void i(String TAG, String str) {
		if (LogDisable)
			return;
		Log.i("iShang " + TAG, str);
	}

	public static void e(String TAG, String str) {
		if (LogDisable)
			return;
		// write2SDCard(null, str);
		Log.e(TAG, str);
	}

	public static void d(String TAG, String str) {
		if (LogDisable)
			return;
		Log.d(TAG, str);
	}

	public static void w(String TAG, String str) {
		if (LogDisable)
			return;
		Log.w(TAG, str);
	}

	private static final String mImgUrl = Environment.getExternalStorageDirectory() + "/ishang_image/";// +MD5.getMD5(url));

	/**
	 * 将文件写到SD卡
	 * 
	 * @param path
	 *            路径，默认，可不写
	 * @param info
	 *            要写的信息
	 * @return
	 * @throws IOException
	 */
	public static boolean write2SDCard(String path, String info) {
		File file = new File(mImgUrl);
		FileOutputStream outputStream = null;
		PrintWriter printWriter = null;
		try {
			if (file.isDirectory()) {
				outputStream = new FileOutputStream(file.getAbsolutePath() + "IShang.txt");
				outputStream.write(info.getBytes());
				outputStream.flush();
				if (!file.exists()) {

				} else {
					InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
				}
				// if(printWriter == null)
				// printWriter = new PrintWriter(file);
				// SimpleDateFormat sdf = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				// Locale.SIMPLIFIED_CHINESE);
				// printWriter.print(sdf.format(new Date()));
				// StackTraceElement[] stacks = new Throwable().getStackTrace();
				// printWriter.println(stacks[1].getClass().getSimpleName()+ "-"
				// + stacks[1].getMethodName() + " at " +
				// stacks[1].getLineNumber() + "  :  " + info);
				// printWriter.flush();
			} else {
				file.mkdirs();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}

}
