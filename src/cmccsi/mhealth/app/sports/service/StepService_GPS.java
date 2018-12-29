package cmccsi.mhealth.app.sports.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

//gps做的
public class StepService_GPS extends Service implements BDLocationListener {
	private static final int MAP_GPS_WALK = 10 * 1000;
	private static final int MAP_GPS_RUNNING = 5 * 1000;
	private static final int MAP_GPS_CYCLE = 2 * 1000;
	
	private static final String TAG = "StepService";
	public static final int NOTIFY_ID = 1;
	boolean threadDisable;
	private final Binder mbinder = new myBind();
	private LocationClient mLocClient;
	private Timer mTimer;
	private TimerTask mTask = null;
	private GpsInfoDetail mGpsInfoDetail;
	public int mCount;
	public int nDeltaCount;
	private Boolean bNoise;
	private String mStartTime;
	private float BMR;

	public boolean mStart_Stop = true;
	private boolean isPointStop = false;
	public static boolean isRunning=false;

	@Override
	public void onCreate() {
		Logger.i(TAG, "StepService onCreate ");

		setBMR();
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		Logger.d(TAG, "stepservice onLowMemory");
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "stepservice onStartCommand");
		// intent.putExtra("restart", mCount);
		isRunning=true;
		mCount = 0;
		nDeltaCount = 0;
		bNoise = false;
		return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
	}

	/**
	 * calBMR(根据人的生理体征计算BMR系数，用于卡路里计算)
	 */
	private void setBMR() {
		String mGender = PreferencesUtils.getString(this, SharedPreferredKey.GENDER, "35");
		String mAge = PreferencesUtils.getString(this, SharedPreferredKey.BIRTHDAY, "1980");
		int born = Integer.valueOf(String.valueOf(mAge).substring(0, 4));
		int now = Integer.valueOf(Common.getDate2Time(new Date(), "yyyy"));
		int age = now - born;
		String mHeight = PreferencesUtils.getString(this, SharedPreferredKey.HEIGHT, "165");
		mHeight = mHeight.split("\\.")[0];
		String mWeight = PreferencesUtils.getString(this, SharedPreferredKey.WEIGHT, "55");
		mWeight = mWeight.split("\\.")[0];
		BMR = Common.calBMR(Common.String2Int(mGender), age, Common.String2Int(mHeight), Common.String2Int(mWeight));
	}

	public interface OnMyLocationChangeListener {
		void change(GpsInfoDetail GPSInfo);

		void timer(String timer);

		void changeAltitude(double altitude);

		void gpsIntensity(int intensity); // 0-强，1-弱
	}

	public void registerCallback(OnMyLocationChangeListener cb) {
		ICallback = cb;
		location();
	}

	public void unRegisterCallback(OnMyLocationChangeListener cb) {
		ICallback = cb;
		ICallback = null;
	}

	OnMyLocationChangeListener ICallback;
	private LocationManager manager;

	private void location() {

		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(this);
		setLocationOption();
		mLocClient.start();

		// 获取海拔信息
		getaltitude();
	}

	private void getaltitude() {
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 创建一个criteria对象
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// 设置需要获取海拔方向数据
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(false);// 不要求方位信息
		criteria.setAltitudeRequired(true);// 不要求海拔信息
		// 设置允许产生资费
		criteria.setCostAllowed(true);
		// 要求低耗电
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, true);
		Log.i("a----------", "we choose " + provider);
		// 重要函数，监听数据测试
		manager.requestLocationUpdates(provider, 1000, 10, new MyGPSLinster());
	}

	private double mAlitude;

	private class MyGPSLinster implements LocationListener {
		// StringBuilder builder = new StringBuilder();

		// 用户位置改变的时候 的回调方法
		@Override
		public void onLocationChanged(Location location) {
			Logger.d("cjz", "gps callback location==null "+(null==location));
			if (location != null && ICallback != null) {

				mAlitude = location.getAltitude();
				Editor editor = getSharedPreferences("config", 0).edit();
				editor.putString("lastlocation", mAlitude + "");
				editor.commit();
				ICallback.changeAltitude(mAlitude);
				Log.e("----------", "alitude = " + mAlitude /*
															 * +" latitude = "+
															 * latitude
															 * +" longitude"
															 * +longitude
															 */);
			}
		}

		// 状态改变
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		// gps ,打开
		@Override
		public void onProviderEnabled(String provider) {
		}

		// 关闭
		@Override
		public void onProviderDisabled(String provider) {
		}
	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();

		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setProdName("com.baidu.location.service_v2.9");
		// 设置定位精度Hight_Accuracy（高精度）、Battery_Saving（低功耗）、Device_Sensors（仅设备(Gps)模式）
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setAddrType("all");
		
		//定位频率
		switch(Constants.RUNTYPE){
		case 1: //步行
			option.setScanSpan(MAP_GPS_WALK);
			break;
		case 2: //跑步
			option.setScanSpan(MAP_GPS_RUNNING);
			break;
		case 3: //骑行
			option.setScanSpan(MAP_GPS_CYCLE);
			break;
		default:
			option.setScanSpan(MAP_GPS_WALK);
			break;
		}

		mLocClient.setLocOption(option);
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("StepService onBind ");
		return mbinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "stepservice onDestroy");
		Logger.d(TAG, "stepservice onDestroy");
		isRunning=false;
		stopTimer();
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if(Config.ISALONE){
			nm.cancel(4);;
		}else{
			nm.cancel(3);
		}
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(this);
			mLocClient.stop();
		}
		super.onDestroy();
	}

	public class myBind extends Binder {
		public StepService_GPS getService() {
			return StepService_GPS.this;
		}
	}

	private boolean bFirstChange = true;
	BDLocation mLocation = null;
	BDLocation mLocationOK = null;
	BDLocation mLocationTmp = null;
	double speedTmp = 0;
	int nDeltaCountTmp = 0;
	LatLng mOldGeoPoint;
	double mDistances = 0;

	@Override
	public void onReceiveLocation(BDLocation location) {
		Logger.d("cjz", "baidu callback location==null "+(null==location));
		if (location == null)
			return;
		// ============filter======================

		// 首次开始计时,记录轨迹开始时间（标记轨迹）
		if (bFirstChange) {
			startTimer();
			mStartTime = Common.getDate2Time(new Date(), Common.COMMON_DATE_YYYY_MM_DD_MID_CREATETIME);
			PreferencesUtils.putString(getApplicationContext(), SharedPreferredKey.START_TIME1, mStartTime);
			bFirstChange = false;
		}
		// 两次坐标点相同
		if (mLocation != null && location.getLatitude() == mLocation.getLatitude()
				&& location.getLongitude() == mLocation.getLongitude()) {

			Logger.i("cjz", "location 位置没变");
			return;
		}

		try {
			// 过滤有效点
			if (!Common.checkPoint(location)) {
				if (ICallback != null) {
					ICallback.gpsIntensity(1);
				}
				return;
			}
		
		if (ICallback != null) {
			ICallback.gpsIntensity(0);
		}
		// ============filter======================

		Logger.i(TAG + "==========", "filter success");

		mGpsInfoDetail = new GpsInfoDetail();
		mGpsInfoDetail.setLatitude(location.getLatitude());
		mGpsInfoDetail.setLongtitude(location.getLongitude());

		mGpsInfoDetail.setAltitude(mAlitude);
		mGpsInfoDetail.setStarttime(mStartTime);
		mGpsInfoDetail.setDetailtime(Common.getDate2Time(new Date(), Common.COMMON_DATE_YYYY_MM_DD_MID_CREATETIME));

		LatLng newpoint = new LatLng((mGpsInfoDetail.getLatitude()), (mGpsInfoDetail.getLongtitude()));

		double distance = 0;
		double speed = 0;
		float cal = 0;
		if (mLocation != null) {
			mOldGeoPoint = new LatLng((mLocation.getLatitude()), (mLocation.getLongitude()));
			distance = DistanceUtil.getDistance(mOldGeoPoint, newpoint);
			Logger.i(TAG, "distance" + distance);

			speed = (distance / 1000 / nDeltaCount) * 3600;// km/h

			// if(5.0f>speed)//步行 7km/hr //跑步20km/hr //骑行70km/hr speed ok
			// {
			// nDeltaCount = 0;
			//
			// bNoise = false;
			//
			// cal = Common.calRunCalorie(BMR, (float) speed,
			// (float)nDeltaCount, Constants.RUNTYPE);
			// mGpsInfoDetail.setSpeed((float) speed);// 时速
			// mGpsInfoDetail.setCal(cal);
			// }else{
			// 第一个噪点
			if (bNoise != true) {
				bNoise = true;
				// 保存第一个噪声点和速度
				mLocationTmp = location;
				speedTmp = speed;
				nDeltaCountTmp = nDeltaCount;
				return;
			} else {// 相邻的第二个噪声点
				if ((1.5 * speedTmp) > speed) {
					// 上一个点写入
					mGpsInfoDetail.setLatitude(mLocationTmp.getLatitude());
					mGpsInfoDetail.setLongtitude(mLocationTmp.getLongitude());

					cal = Common.calRunCalorie(BMR, (float) speed, (float) nDeltaCount, Constants.RUNTYPE);
					mGpsInfoDetail.setSpeed((float) speedTmp);// 时速
					mGpsInfoDetail.setCal(cal);

					// 保存第一个噪声点和速度
					mLocationTmp = location;
					speedTmp = speed;
					nDeltaCount -= nDeltaCountTmp;
					nDeltaCountTmp = 0;
				} else {
					// 加速后突显噪点，丢弃
					bNoise = false;
					return;
				}
			}

			mGpsInfoDetail.setDistance(distance);// 更新距离

		} else {
			mGpsInfoDetail.setDistance(0);
		}
		if (isPointStop) {
			twoCountPoint--;
			if (twoCountPoint == 0) {
				isPointStop = false;
				twoCountPoint = 2;
			}
			mGpsInfoDetail.setIsStopPoint(1);
		} else {
			mGpsInfoDetail.setIsStopPoint(0);
		}
		MHealthProviderMetaData.GetMHealthProvider(this).insertDetail(mGpsInfoDetail);
		if (ICallback != null) {
			ICallback.change(mGpsInfoDetail);
		}
		mLocation = location;
		} catch (Exception e) {
			// TODO: handle exception
			Logger.i(TAG, "location error");
			e.printStackTrace();
		}
	}

	private int twoCountPoint = 2;

	public void requeatNotify() {
		if (mLocClient != null) {
			mLocClient.requestNotifyLocation();
		}
	}

	public void stopTimer() {
		mStart_Stop = true;
		isPointStop = true;
		if (null != mTimer) {
			mTask.cancel();
			mTask = null;
			mTimer.cancel(); // Cancel timer
			mTimer.purge();
			mTimer = null;
			// mHandler.removeMessages(mMessage.what);
		}
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(this);
			mLocClient.stop();
			Logger.i(TAG, "----map stop");
		}
	}

	public void startTimer() {
		mStart_Stop = false;
		if (null == mTimer) {
			if (null == mTask) {
				mTask = new TimerTask() {
					@Override
					public void run() {
						mCount++;
						nDeltaCount++;
						String time = Common.sec2Time(mCount);
						Logger.i(TAG, time);
						if (ICallback != null) {
							ICallback.timer(time);
						}
					}
				};
			}

			mTimer = new Timer(true);
			mTimer.schedule(mTask, 1000, 1000); // set timer duration
		}
		if (!mLocClient.isStarted()) {
			mLocClient.registerLocationListener(this);
			mLocClient.start();
		}
	}

}
