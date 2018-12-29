package cmccsi.mhealth.app.sports.net;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkTool {
	@SuppressWarnings("unused")
	private static final String TAG = "NetworkTool";

	public final static int NONE = 0;// 无网络
	public final static int WIFI = 1;// Wi-Fi
	public final static int MOBILE = 2;// 3G,GPRS

	public static boolean isOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * 获取网络状况 0 无网络 1 wifi 2 3G/GPS
	 * 
	 * @param context
	 * @return qjj 2012-12-12
	 */
	public static int getNetworkState(Context context) {
		if (context != null) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// 手机网络判断
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
			// Wifi网络判断
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}
		return NONE;
	}

	public static int getNetworkStateConnected(Context context) {
		if (context != null) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// 手机网络判断
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED) {
				return MOBILE;
			}
			// Wifi网络判断
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (state == State.CONNECTED) {
				return WIFI;
			}
		}
		return NONE;
	}

	/**
	 * 判断GPS是否开启
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isGPSOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}

}
