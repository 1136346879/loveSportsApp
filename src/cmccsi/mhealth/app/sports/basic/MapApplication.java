package cmccsi.mhealth.app.sports.basic;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.errorhandler.CrashHandler;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MapApplication extends Application {

	private static MapApplication mInstance = null;
	public boolean m_bKeyRight = true;
	private final ArrayList<Activity> activities = new ArrayList<Activity>();

	// public static final String strKey = "ByBGoDGhjFZrkfKRbHTxEwel";//发布
	// 调试key plNeTD49s6NYPmdR9agGuabD //打包key 3GBVUGXrB6aKug13bEoLYbtS
//	public static final String strKey = "Ge1wD2ve2YrDMQ9Le9WBmIqL";//一拖三版key
//	public static final String strKey = "GCxKr6P5UWcyLtrVq2pV8N8F";//独立版key

	// public static final String strKey = "OXREGXM59xj5jaeSExwHviIG";// bebug
	private static final String LTAG = MapApplication.class.getSimpleName();
	private SDKReceiver mReceiver;
	
	public static IWXAPI mWeiXinAPI;

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Logger.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(MapApplication.this,
						"请输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initEngineManager(this);
		resgisterWXAPI();
	}

	

	public void initEngineManager(Context context) {
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);

		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(getApplicationContext());
		// 发送以前没发送的报告(可选)
		// crashHandler.sendPreviousReportsToServer();
	}

	public static MapApplication getInstance() {
		return mInstance;
	}

	public void addActivity(Activity a) {
		activities.add(a);
	}

	public void finishActivity() {
		for (Activity activity : activities) {
			activity.finish();
		}

	}

	public void finishReceive() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		finishReceive();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		finishReceive();
	}
	
	private void resgisterWXAPI(){
		if(Config.ISALONE){
			mWeiXinAPI = WXAPIFactory.createWXAPI(this, Constants.APP_ID_Alone, true);
			mWeiXinAPI.registerApp(Constants.APP_ID_Alone);
		}else{
			mWeiXinAPI = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
			mWeiXinAPI.registerApp(Constants.APP_ID);
		}
	}
}