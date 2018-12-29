package cmccsi.mhealth.app.sports.common.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	private static Toast toast = null;

	// private static Object synObj = new Object();

	public static void showToast(Context context, String msg) {
		showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
	}
	
	public static void showToast_L(Context context, String msg) {
		showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
	}

	public static void showToast(Context context, int msg) {
		showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
	}
	public static void showToast_L(Context context, int msg) {
		showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
	}

	private static synchronized void showToast(final Context act, final String msg, final int len) {

		if (toast != null) {
			toast.setText(msg);
			toast.show();
		} else {
			toast = Toast.makeText(act, msg, len);
			toast.show();
		}
	}

	private static synchronized void showToast(final Context act, final int msg, final int len) {

		if (toast != null) {
			toast.setText(msg);
			toast.show();
		} else {
			toast = Toast.makeText(act, msg, len);
			toast.show();
		}
	}
}
