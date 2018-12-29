package cmccsi.mhealth.app.sports.phonesteplib;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * 保护线程服务
 */
public class ProtectionService extends Service {
	@Override
	public void onCreate() {
		if (!PhoneStepService.isServiceRunning(getApplicationContext())) {
			Intent service = new Intent(ProtectionService.this, PhoneStepService.class);
			startService(service);
		}
	}

	@Override
	public void onDestroy() {
		if (!PhoneStepService.isServiceRunning(getApplicationContext())) {
			Intent service = new Intent(ProtectionService.this, PhoneStepService.class);
			startService(service);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_REDELIVER_INTENT;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param className
	 *            ：判断的服务名字：包名+类名
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(ProtectionService.class.getName()) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
