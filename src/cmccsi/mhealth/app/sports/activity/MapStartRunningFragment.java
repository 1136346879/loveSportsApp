package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.basic.SampleFragment;
import cmccsi.mhealth.app.sports.bean.GPSListInfo;
import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.ShowProgressDialog;
import cmccsi.mhealth.app.sports.common.UploadUtil;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.service.StepService_GPS;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.app.sports.R;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

public class MapStartRunningFragment extends SampleFragment implements OnClickListener {

	private static final String TAG = "Map";
	protected static final int TIME_CHANGE = 0;

	private static final int NOTIFICATION = 0;

	private MapView mMapView;
	private TextView mTextViewTimer;
	LatLng[] mLinePoints = null;
	PolylineOptions lineGeometry = new PolylineOptions();
	private StepService_GPS mService;
	private MyLocationData mLocData;
	private List<GpsInfoDetail> mListGpsDetails = new ArrayList<GpsInfoDetail>();
	private List<LatLng> mGeoPoints = new ArrayList<LatLng>();
	private TextView mTextViewAltitede;
	// private TextView mTextViewCal;
	private BitmapDescriptor mOverlayEndItem;
	private BitmapDescriptor mOverlayStartItem;
	private String mTextviewTime;
	View view;
	private String tag;
	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;
	private Marker mMarkerStart;
	private Marker mMarkerEnd;
	private InfoWindow mInfoWindow;
	private MapApplication mApplication;

	private NotificationManager nm = null;
	private CommonAskDialog mAskDialog;

	private double mCenterLatitude;
	private double mCenterLongitude;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.acticity_running, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	public void setType(String type) {
		// this.mType = type;
	}

	public MapStartRunningFragment(String tag) {
		this.tag = tag;
	}

	@Override
	public void clickListner() {
		super.clickListner();
		mMapView.setOnClickListener(this);
		mStopRunnnig.setOnClickListener(this);
		mFinishRunnnig.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mButtonOpenMap.setOnClickListener(this);
		mButtonMoveToLocation.setOnClickListener(this);
		mBaiduMap.setOnMapClickListener(null);

	}

	@Override
	public void findViews() {
		super.findViews();
		mMapView = (MapView) findView(R.id.map_start);
		mTextViewTitle.setText("正在运动");
		mFinishRunnnig = (Button) findView(R.id.button_finish_running);
		mStopRunnnig = (Button) findView(R.id.button_stop_running);
		mTextViewkilometre = findView(R.id.textview_kilometre);
		mBack = findView(R.id.button_input_bg_back);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setVisibility(View.GONE);
		mLlayoutMap = findView(R.id.LLayout_map);

		mTextViewTimer = findView(R.id.startmap_timer);
		if (mTextviewTime != null && StepService_GPS.isRunning) {
			mTextViewTimer.setText(mTextviewTime);
			mStopRunnnig.setEnabled(true);
			if (mStart_Stop) {
				mStopRunnnig.setText("继续");
				if (mListGpsDetails.size() > 1) {
					mListGpsDetails.get(mListGpsDetails.size()-1).setIsStopPoint(1);
				}
			} else {
				mStopRunnnig.setText("暂停");
			}
		}else{
			mTextViewTimer.setText("00:00:00");
		}
		mButtonMoveToLocation = findView(R.id.button_get_location);
		mButtonOpenMap = findView(R.id.button_open_map);		
		mIvMapGps = findView(R.id.iv_map_gps);
		
		if (mStart_cal1 != null && StepService_GPS.isRunning) {
			mTextViewkilometre.setText(mStart_cal1);
		}else{
			mTextViewkilometre.setText("-.--");
		}
		mTextViewSpeed = findView(R.id.textview_speed);
		mTextViewAltitede = findView(R.id.textview_altitude);
		// mTextViewCal = findView(R.id.textview_cal);

		/**
		 * 获取地图控制器
		 */
		mBaiduMap = mMapView.getMap();

		mUiSettings = mBaiduMap.getUiSettings();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapView.setClickable(false);

		/**
		 * 显示内置缩放控件
		 */
		mMapView.showZoomControls(false);
		/**
		 * 是否启用旋转手势
		 */
		mUiSettings.setRotateGesturesEnabled(true);
		mUiSettings.setZoomGesturesEnabled(true);
		/**
		 * 是否启用平移手势
		 */
		mUiSettings.setScrollGesturesEnabled(true);
		/**
		 * 是否显示比例尺控件
		 */
		mMapView.showScaleControl(false);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		LatLng p = new LatLng(mCenterLatitude, mCenterLongitude);
		// 定义地图状态
		MapStatus _mMapStatus = new MapStatus.Builder().target(p).zoom(18).build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(_mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		// 设定地图中心点
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p);
		mBaiduMap.animateMapStatus(u);

		// 添加标注点覆盖物
		mOverlayStartItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_start);
		OverlayOptions ooA = new MarkerOptions().position(p).icon(mOverlayStartItem);
		mMarkerStart = (Marker) (mBaiduMap.addOverlay(ooA));

		// 当不需要定位图层时关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);

//		setOnMarkClickListener();

		// change点标记
		if (StepService_GPS.isRunning) {
			// 从数据库取出数据
			String starttime = PreferencesUtils.getString(mApplication, SharedPreferredKey.START_TIME1, null);
			if (starttime != null) {
				mListGpsDetails = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoDetails(starttime);
				if (mListGpsDetails != null && mListGpsDetails.size() != 0) {
					mFristPoint = null;
					mFristPoint = new LatLng((mListGpsDetails.get(0).getLatitude()), (mListGpsDetails.get(0).getLongtitude()));
					LatLng ll = new LatLng(mFristPoint.latitude, mFristPoint.longitude);
					mMarkerStart.remove();
					mMarkerStart = null;
					OverlayOptions ooAstart = new MarkerOptions().position(ll).icon(mOverlayStartItem);
					mMarkerStart = (Marker) (mBaiduMap.addOverlay(ooAstart));
					for (int i = 1; i < mListGpsDetails.size(); i++) {
						refresh(mListGpsDetails.get(i), mListGpsDetails.get(i - 1));
					}
					setMyLocation(mListGpsDetails.get(mListGpsDetails.size() - 1));
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("running", "onCreate");
		nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mApplication = (MapApplication) getActivity().getApplication();
		// getprefence();
		startservice();
		getActivity().bindService(new Intent(getActivity(), StepService_GPS.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);

		Intent intent = getActivity().getIntent();
		mCenterLatitude = intent.getDoubleExtra("center_latitude", 116.403875);
		mCenterLongitude = intent.getDoubleExtra("center_longitude", 39.915168);

		showNotification();
	}

	private void getprefence() {
		mTextviewTime = PreferencesUtils.getString(getActivity(), SharedPreferredKey.TIMER, null);
		mStart_Stop = PreferencesUtils.getBoolean(getActivity(), SharedPreferredKey.START_STOP, false);
		mStart_cal1 = PreferencesUtils.getString(getActivity(), SharedPreferredKey.START_CAL1, null);
		// mTextviewTime = "00:00:00";
		// mStart_Stop = false;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public void loadLogic() {
		// LinearLayout.LayoutParams lp = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT);
		// lp.setMargins(20, 10, 20, 20);
		// mLlayoutMap.setLayoutParams(lp);
	};

	StepService_GPS.OnMyLocationChangeListener changeListener = new StepService_GPS.OnMyLocationChangeListener() {

		@Override
		public void timer(String timer) {
			Log.d("running", "changeListener  timer  ");
			Message message = new Message();
			message.what = TIME_CHANGE;
			message.obj = timer;
			handler.sendMessage(message);
		}

		@Override
		public void change(GpsInfoDetail GPSInfo) {
			Log.d("running", "changeListener  change");
			UIchange(GPSInfo);
		}

		@Override
		public void changeAltitude(double altitude) {
			Log.d("running", "changeListener  changeAltitude");
			if (altitude != 0) {
				if (altitude > 10) {
					mTextViewAltitede.setText(String.format("%.1f", altitude));
				} else {
					mTextViewAltitede.setText(String.format("%.2f", altitude));
				}
			}
		}

		@Override
		public void gpsIntensity(int intensity) {
			// TODO Auto-generated method stub
			if(intensity == 0){
				mIvMapGps.setBackgroundResource(R.drawable.map_gps_q);
			}else{
				mIvMapGps.setBackgroundResource(R.drawable.map_gps_r);
			}
			
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("running", "mConnection");
			mService = ((StepService_GPS.myBind) service).getService();
			mService.registerCallback(changeListener);
			// mService.getType(mType);
			mService.requeatNotify();
			// mMapController.setZoom(18);
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d("running", "onServiceDisconnected");
			mService = null;
		}
	};
	private Button mFinishRunnnig;
	private Button mStopRunnnig;

	protected void startservice() {
		if (StepService_GPS.isRunning) {
			System.out.println("--------startservice---true----");
			getprefence();
		} else {
			System.out.println("--------startservice---false----");
			Log.d("running", "轨迹服务启动");
			Intent intent = new Intent();
			intent.setClass(getActivity(), StepService_GPS.class);
			getActivity().startService(intent);

		}
	}

	/**
	 * 
	 * 判断服务是否运行
	 * 
	 * @param context
	 * 
	 * @param className
	 *            ：判断的服务名字：包名+类名
	 * 
	 * @return true 在运行 false 不在运行
	 */
	public boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	private int lineColor;
	/**
	 * 绘制折线，该折线状态随地图状态变化
	 * @param mNowPoints 现在的点
	 * @return 折线对象
	 */
	public void drawLine(GpsInfoDetail mNowPoints,GpsInfoDetail mLastPoints) {
		LatLng[] linePoints = new LatLng[2];// 数组不能存null！！
		LatLng geoPoint1 = null ;
		LatLng geoPoint2 = null;
		geoPoint1 = new LatLng((mNowPoints.getLatitude()), (mNowPoints.getLongtitude()));
		mGeoPoints.add(geoPoint1);
		geoPoint2 = new LatLng(mLastPoints.getLatitude(), mLastPoints.getLongtitude());
		mGeoPoints.add(geoPoint2);
		if (mNowPoints.getIsStopPoint() == 1) {
			lineColor = Color.argb(255, 195, 195, 195);
			System.out.println("------11111111111111111---------");
		} else {
			lineColor = Color.argb(255, 143, 195, 32);
			System.out.println("------22222222222222222---------");
		}
		linePoints[0] = geoPoint2;
		linePoints[1] = geoPoint1;
		if (mMarkerEnd != null) {
			mMarkerEnd.remove();
		}
		mOverlayEndItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_end);
		OverlayOptions ooA = new MarkerOptions().position(linePoints[linePoints.length - 1]).icon(mOverlayEndItem);
		mMarkerEnd = (Marker) (mBaiduMap.addOverlay(ooA));
		// setOnMarkClickListener();
		OverlayOptions ooPolyline = lineGeometry.width(10).color(lineColor).points(Arrays.asList(linePoints));
		mBaiduMap.addOverlay(ooPolyline);
	}

	public void changeText(List<GpsInfoDetail> points) {
		double mDistance = 0;
		double avgCal = 0;
		for (int i = 0; i < points.size(); i++) {
			mDistance += points.get(i).getDistance();
			avgCal += points.get(i).getCal();
		}
		if (mDistance / 1000f > 10) {
			mTextViewkilometre.setText(String.format("%.1f", mDistance / 1000f));
		} else {
			mTextViewkilometre.setText(String.format("%.2f", mDistance / 1000f));
		}

		if (mService != null && mService.mCount != 0) {
			if (((mDistance / 1000 / mService.mCount) * 3600) > 10) {
				mTextViewSpeed.setText(String.format("%.1f", (mDistance / 1000 / mService.mCount) * 3600));
			} else {
				mTextViewSpeed.setText(String.format("%.2f", (mDistance / 1000 / mService.mCount) * 3600));
			}
		}
		// if (avgCal > 10) {
		// mTextViewCal.setText(String.format("%.1f", avgCal));
		// } else {
		// mTextViewCal.setText(String.format("%.2f", avgCal));
		// }
	}

	public void UIchange(GpsInfoDetail arrGPSInfo) {
		if (arrGPSInfo == null)
			return;
		mListGpsDetails.add(arrGPSInfo);
		setMyLocation(arrGPSInfo);
		if (mListGpsDetails.size()==1) {
			mMarkerStart.remove();
			mMarkerStart = null;
			LatLng p = new LatLng(mListGpsDetails.get(0).getLatitude(), mListGpsDetails.get(0).getLongtitude());
			// 添加标注点覆盖物
			mOverlayStartItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_start);
			OverlayOptions ooA = new MarkerOptions().position(p).icon(mOverlayStartItem);
			mMarkerStart = (Marker) (mBaiduMap.addOverlay(ooA));
		}
		else if (mListGpsDetails.size() > 1) {
			refresh(arrGPSInfo, mListGpsDetails.get(mListGpsDetails.size() - 2));
		}
	}

	private void setMyLocation(GpsInfoDetail arrGPSInfo) {
		mNowPoint = new LatLng((arrGPSInfo.getLatitude()), (arrGPSInfo.getLongtitude()));
		mLocData = new MyLocationData.Builder().direction(2.0f).latitude(mNowPoint.latitude).longitude(mNowPoint.longitude)
				.build();
		mBaiduMap.setMyLocationData(mLocData);
		LatLng ll = new LatLng(arrGPSInfo.getLatitude(), arrGPSInfo.getLongtitude());
		final MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mBaiduMap.animateMapStatus(u);
			}
		});

	}

	private void refresh(GpsInfoDetail arrGPSInfo,GpsInfoDetail arrGPSInfo1) {
		// 添加折线
		drawLine(arrGPSInfo,arrGPSInfo1);
		if (mListGpsDetails.size() == 1) {
			mBaiduMap.clear();
			LatLng ll = new LatLng(mNowPoint.latitude, mNowPoint.longitude);
			mMarkerStart.remove();
			mMarkerStart = null;
			OverlayOptions ooA = new MarkerOptions().position(ll).icon(mOverlayStartItem);
			mMarkerStart = (Marker) (mBaiduMap.addOverlay(ooA));
			// setOnMarkClickListener();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			mMapView.invalidate();
		}
		changeText(mListGpsDetails);
		double tmpAltitude = arrGPSInfo.getAltitude();
		if (tmpAltitude != 0) {
			if (tmpAltitude > 10) {
				mTextViewAltitede.setText(String.format("%.1f", tmpAltitude));
			} else {
				mTextViewAltitede.setText(String.format("%.2f", tmpAltitude));
			}
		}
		// Common.fitPoints(mGeoPoints, mBaiduMap);
		LatLng ll = new LatLng((int) (arrGPSInfo.getLatitude()), (int) (arrGPSInfo.getLongtitude()));
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);

	}

	private void setOnMarkClickListener() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(mActivity);
				button.setBackgroundResource(R.drawable.popup);
				if (marker == mMarkerEnd) {
					button.setText("到此处您走了" + getWalkDistance() + "end米");
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(button, ll, -47);
					mBaiduMap.showInfoWindow(mInfoWindow);
				} else if (marker == mMarkerStart) {
					button.setText("到此处您走了0米");
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(button, ll, -47);
					mBaiduMap.showInfoWindow(mInfoWindow);
				} else {
				}
				return true;
			}
		});

	}

	private int getWalkDistance() {
		// for (int i = 0; i < index; i++) {
		// GeoPoint firstpos = mPoints.get(i);
		// if(mPoints.size() != i+1)
		// endpos = mPoints.get(i+1);
		// juli += DistanceUtil.getDistance(mAllPoints.get(0),
		// mAllPoints.get(index));
		// System.out.println(juli + "");
		// }
		return 0;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy -- >");
//		mService.unRegisterCallback(changeListener);
//		if (Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)) {
//			getActivity().unbindService(mConnection);
////			PreferencesUtils.putString(mActivity, SharedPreferredKey.TIMER, mTextViewTimer.getText().toString());
//		} else {
//			PreferencesUtils.removeSp(mActivity, SharedPreferredKey.TIMER);
//		}
		mApplication.finishReceive();
		if (mOverlayEndItem != null) {
			mOverlayEndItem.recycle();
		}
		if (mOverlayStartItem != null)
			mOverlayStartItem.recycle();

		mMapView.onDestroy();
		mMapView = null;

		super.onDestroy();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		// mbCanDrawLine = false;
		Log.e(TAG, "onPause() -- >");
		// if (mBMapMan != null) {
		// mBMapMan.stop();
		// }
		super.onPause();
	}

	@Override
	public void onResume() {
		if (mTextviewTime != null || !mTextViewTimer.getText().equals("00:00:00")) {
			mStopRunnnig.setEnabled(true);
		} else {
			mStopRunnnig.setEnabled(false);
		}
		mMapView.onResume();

		super.onResume();
	}

	private TextView mTextViewkilometre;
	private Button mButtonOpenMap;
	private ImageView mIvMapGps;
	private boolean mOpenMap;
	private LinearLayout mLlayoutMap;
	private Button mButtonMoveToLocation;
	private boolean mStart_Stop;
	private String mStart_cal1;// 暂存的距离成就
	private TextView mTextViewSpeed;
	private LatLng mNowPoint;
	private LatLng mFristPoint;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.getView().setFocusable(true);
		this.getView().setFocusableInTouchMode(true);
		this.getView().requestFocus();
		this.getView().setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// finishRunning();
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
					ToastUtils.showToast(getActivity(), getResources().getString(R.string.mapstartrunningfragment_exitmsg));
					return true;
				} else
					return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		ScaleAnimation sa = null;
		int mWidth = mLlayoutMap.getWidth();
		int mHeight = mLlayoutMap.getHeight();
		int windowWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int windowheight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
		switch (v.getId()) {
		// case R.id.btn_jd:
		// if (mPoints.size() != 0)
		// mMapView.getController().animateTo(mPoints.get(mPoints.size() - 1));
		// break;
		// case R.id.btn_jl:
		// for (int i = 0; i < mPoints.size() - 1; i++) {
		// GeoPoint firstpos = mPoints.get(i);
		// GeoPoint endpos = mPoints.get(i + 1);
		// juli += DistanceUtil.getDistance(firstpos, endpos);
		// }
		// Toast.makeText(getApplicationContext(), juli + "",
		// Toast.LENGTH_LONG).show();
		// break;
		case R.id.button_finish_running:
			mFinishRunnnig.setEnabled(false);
			saveData();
			mFinishRunnnig.setEnabled(true);
			break;
		case R.id.button_stop_running:
			// if (Common.isServiceRunning(mActivity,
			// Constants.SERVICE_RUNNING_NAME)) {
			if (mStart_Stop) {
				PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.START_STOP, false);
				mStart_Stop = false;
				mStopRunnnig.setText("暂停");
				mService.startTimer();
				// stopService();
			} else {
				PreferencesUtils.putString(mActivity, SharedPreferredKey.START_CAL1, mTextViewkilometre.getText().toString());
				PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.START_STOP, true);
				mStart_Stop = true;
				mStopRunnnig.setText("继续");
				mService.stopTimer();
				// restartService();
			}
			// // TODO ?
			// PreferencesUtils.putLong(mActivity, SharedPreferredKey.TIMER, new
			// Date().getTime());
			break;
		case R.id.button_input_bg_back:
			Log.d("running", tag);
			// finishRunning();
			final String starttime = PreferencesUtils.getString(mActivity, SharedPreferredKey.START_TIME1, null);
			if (starttime != null) {
				MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteDetailData(starttime);
				handler.sendEmptyMessage(R.string.MESSAGE_UN_SAVE_DATA);
			}
			// if (tag.equals("map")) {
			// FragmentManager sfm = getActivity().getSupportFragmentManager();
			// sfm.popBackStack();
			//
			// } else {
			// TabBaseFragment fca = (TabBaseFragment) getActivity();
			// MapFragment fragment = new MapFragment();
			// fca.switchContent(fragment);
			// }
			getActivity().finish();
			break;
		case R.id.map_start:
			mMapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			break;
		case R.id.button_open_map:
			openOrCloseMap(mWidth, mHeight, windowWidth, windowheight);
			break;
		case R.id.button_get_location:
			// if (mGeoPoints != null && mGeoPoints.size() > 1){
			// Common.fitPoints(mGeoPoints, mBaiduMap);
			// } else if (mGeoPoints.size() > 0) {
			if (mGeoPoints.size() > 0) {
				LatLng ll = new LatLng(mGeoPoints.get(0).latitude, mGeoPoints.get(0).longitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}else{
				LatLng p = new LatLng(mCenterLatitude, mCenterLongitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p);
				mBaiduMap.animateMapStatus(u);
			}

			break;
		default:
			break;
		}
	}

	public void finishRunning() {
//		if (nm != null) {
//			if(Config.ISALONE){
//				nm.cancel(4);
//			}else{
//				nm.cancel(3);
//			}
//		}
		stopService();
		// 返回计划运动界面 TODO
		// gengqi
		// TabBaseFragment fca = (TabBaseFragment) getActivity();
		// fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment)
		// new MapListGPSFragment());
	}

	private void saveData() {
		final String starttime = PreferencesUtils.getString(mActivity, SharedPreferredKey.START_TIME1, null);

		if (MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoDetails(starttime).size() < 2) {
			String[] buttons = { "确定", "", "取消" };
			mAskDialog = CommonAskDialog.create(getResources().getString(R.string.mapstartrunningfragment_exitmsg1), buttons, false, true);
			mAskDialog.setAlertIconVisible(-1);
			mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
				@Override
				public void onClick(int which) {
					if (which == CommonAskDialog.BUTTON_OK) {
						if (starttime != null) {
							MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteDetailData(starttime);
							handler.sendEmptyMessage(R.string.MESSAGE_UN_SAVE_DATA);
						}
						finishRunning();
						// if (tag.equals("map")) {
						// FragmentManager sfm =
						// getActivity().getSupportFragmentManager();
						// sfm.popBackStack();
						// } else {
						// TabBaseFragment fca = (TabBaseFragment)
						// getActivity();
						// MapFragment fragment = new MapFragment();
						// fca.switchContent(fragment);
						// }
						removeSp();
						mTextviewTime = "00:00:00";
						getActivity().finish();
					}
				}
			});
			mAskDialog.show(mActivity.getSupportFragmentManager(), "CommonAskDialog");
		} else {
			String[] buttons = { "保存", "不保存", "取消" };
			mAskDialog = CommonAskDialog.create(getResources().getString(R.string.mapstartrunningfragment_exitmsg2), buttons, true, true);
			mAskDialog.setAlertIconVisible(-1);
			mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
				@Override
				public void onClick(int which) {
					if (which == CommonAskDialog.BUTTON_OK) {
						finishRunning();
						if (starttime != null && mListGpsDetails.size() > 0) {
							final GPSListInfo gpsListInfo = new GPSListInfo();
							gpsListInfo.setStarttime(starttime);

							gpsListInfo.setDuration(mTextViewTimer.getText().toString());
							float cal = 0;
							float speed = 0;
							float distance = 0;
							for (GpsInfoDetail gpsInfoDetail : mListGpsDetails) {
								cal += gpsInfoDetail.getCal();
								distance += gpsInfoDetail.getDistance();
							}

							gpsListInfo.setDistance(distance);
							gpsListInfo.setCal(cal);
							if (mService != null && mService.mCount != 0) {
								gpsListInfo.setSpeed((distance / 1000 / mService.mCount) * 3600);
							}
							gpsListInfo.setSporttype(Constants.RUNTYPE);
							gpsListInfo.setIsUpload(1);
							// 上传数据
							final List<GPSListInfo> infos = new ArrayList<GPSListInfo>();
							infos.add(gpsListInfo);
							ShowProgressDialog.showProgressDialog("正在上传", mActivity);
							new Thread() {
								public void run() {
									Message msg = new Message();
									msg.obj = gpsListInfo;
									msg.what = UploadUtil.upload(mActivity, infos);
									handler.sendMessage(msg);
								};
							}.start();
						}
					} else if (which == CommonAskDialog.BUTTON_NEUTRAL) {
						finishRunning();
						if (starttime != null) {
							MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteDetailData(starttime);
							handler.sendEmptyMessage(R.string.MESSAGE_UN_SAVE_DATA);
						}
					} else if (which == CommonAskDialog.BUTTON_CANCEL) {

					}
				}
			});
			mAskDialog.show(mActivity.getSupportFragmentManager(), "CommonAskDialog");
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ShowProgressDialog.dismiss();
			switch (msg.what) {
			case R.string.MESSAGE_UPLOAD_GPS_SUCCESS:
				BaseToast(String.valueOf(getString(R.string.MESSAGE_UPLOAD_GPS_SUCCESS)));
				updateDataBase(msg, 0);
				break;
			case R.string.MESSAGE_UN_SAVE_DATA:
//				BaseToast("上传失败，已保存至本地历史轨迹");
				removeSp();
				// finishRunning();
				// 通知栏老进不来 单独启了一个activity
				// if (tag.equals("map")) {
				// FragmentManager sfm = getActivity()
				// .getSupportFragmentManager();
				// sfm.popBackStack();
				// } else {
				// TabBaseFragment fca = (TabBaseFragment) getActivity();
				// MapFragment fragment = new MapFragment();
				// fca.switchContent(fragment);
				// }
				getActivity().finish();
				break;
			case TIME_CHANGE:
				mTextViewTimer.setText(msg.obj.toString());
				if (!mTextViewTimer.getText().equals("00:00:00")) {
					mStopRunnnig.setEnabled(true);
					mService.startTimer();
				}
				break;
			default:
				BaseToast("上传失败，已保存至本地历史轨迹");
				System.out.println("--------aaaaaaa-------");
				updateDataBase(msg, 1);
				break;
			}
		}

		private void updateDataBase(android.os.Message msg, int flag) {
			GPSListInfo gpsListInfo = new GPSListInfo();
			gpsListInfo = (GPSListInfo) msg.obj;
			gpsListInfo.setIsUpload(flag);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGpsListInfo(gpsListInfo);
			removeSp();
			ShowProgressDialog.dismiss();
			// finishRunning();

			// if (tag.equals("map")) {
			// FragmentManager sfm = getActivity().getSupportFragmentManager();
			// sfm.popBackStack();
			// } else {
			// TabBaseFragment fca = (TabBaseFragment) getActivity();
			// MapFragment fragment = new MapFragment();
			// fca.switchContent(fragment);
			// }
			getActivity().finish();
		};
	};

	private void removeSp() {
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.START_TIME1);
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.TIMER);
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.START_CAL1);
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.START_STOP);
	}

	private void openOrCloseMap(int mWidth, int mHeight, int wWidth, int wHeight) {
		ScaleAnimation sa;
		mLlayoutMap.clearAnimation();
		AnimationSet set = new AnimationSet(true);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLlayoutMap.getLayoutParams(); // 取控件mGrid当前的布局参数
		// if (mButtonOpenMap.isChecked()) {
		//
		// } else {
		//
		// }
		if (mOpenMap) {
			mButtonOpenMap.setBackgroundResource(R.drawable.map_max_min);

			mOpenMap = false;
			// linearParams.weight = 1.0f;
			// linearParams.setMargins(20, 10, 20, 10);
			sa = new ScaleAnimation((float) wWidth / wWidth, 1.0f, (float) wHeight / mHeight, 1.0f, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0.1f);
			// Animation animation = AnimationUtils.loadAnimation(mActivity,
			// R.anim.map_close_anim);
			// mLlayoutMap.setAnimation(animation);
			// mLlayoutMap.setLayoutParams(linearParams);
			locToCenter(0, (wHeight - mHeight) / 4);
		} else {
			mButtonOpenMap.setBackgroundResource(R.drawable.map_min);

			mOpenMap = true;
			linearParams.weight = 0.0f;
			float fx = (float) mWidth / wWidth;
			float fy = (float) mLlayoutMap.getTop() / wHeight;
			// 展开
			linearParams.setMargins(0, 0, 0, 0);
			sa = new ScaleAnimation(fx, 1.0f, fy, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, fy);
			// Animation animation = AnimationUtils.loadAnimation(mActivity,
			// R.anim.map_open_anim);
			// mLlayoutMap.setAnimation(animation);
			mLlayoutMap.setLayoutParams(linearParams);
			locToCenter(0, -(wHeight - mHeight) / 8);
		}
		sa.setDuration(500);
		set.addAnimation(sa);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setInterpolator(new OvershootInterpolator(1f));
		set.setAnimationListener(new myAnimationListener(mLlayoutMap));
		mLlayoutMap.startAnimation(set);
	}
	
    private void locToCenter(int x, int y) {
    	//新的中心点
    	LatLng ll = null;
    	if (mGeoPoints.size() > 0) {
			ll = new LatLng(mGeoPoints.get(mGeoPoints.size() - 1).latitude, mGeoPoints.get(mGeoPoints.size() - 1).longitude);
		}
    	// 开启定位图层  
    	mBaiduMap.setMyLocationEnabled(true);

    	//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(ll).zoom(18).build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.scrollBy(x, y);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.animateMapStatus(mMapStatusUpdate);

		// 当不需要定位图层时关闭定位图层  
		mBaiduMap.setMyLocationEnabled(false);
    }

	class myAnimationListener implements AnimationListener {
		LinearLayout view;

		public myAnimationListener(LinearLayout view) {
			super();
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (!mOpenMap) {

				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLlayoutMap.getLayoutParams(); // 取控件mGrid当前的布局参数
				linearParams.weight = 1.0f;
				linearParams.setMargins(20, 10, 20, 10);
				mLlayoutMap.setLayoutParams(linearParams);
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// toast("animatino finish");
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

	/** stop service */
	private void stopService() {
		mService.unRegisterCallback(changeListener);
		if (StepService_GPS.isRunning) {
			// stopTimer();
			getActivity().unbindService(mConnection);
			mActivity.stopService(new Intent().setClass(mActivity, StepService_GPS.class));
		}
//		BaseToast("已停止运动");
	}

	/** restart service */
	private void restartService() {
		startservice();
	}

	// hjn add
	// 显示Notification
	public void showNotification() {
		Intent appIntent = new Intent(getActivity().getApplicationContext(),
					cmccsi.mhealth.app.sports.activity.FragmentContainerActivity.class);
		appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式
		PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, appIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		RemoteViews contentView = new RemoteViews(getActivity().getPackageName(), R.layout.notification_phone_sendmsg);
		contentView.setTextViewText(R.id.notification_phone_sendmsg_text,getResources().getString(R.string.mapstartrunningfragment_exitmsg3));
		contentView.setTextViewTextSize(R.id.notification_phone_sendmsg_text,TypedValue.COMPLEX_UNIT_SP, 18);
		Notification notification = new NotificationCompat.Builder(getActivity()).setSmallIcon(R.drawable.i_shang)
				.setContent(contentView).setContentIntent(contentIntent).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL).build();
		notification.flags = Notification.FLAG_NO_CLEAR;
		long[] vibrate = { 0, 0, 0, 0 };
		notification.vibrate = vibrate;
		if(Config.ISALONE){
			nm.notify(4, notification);
		}else{
			nm.notify(3, notification);
		}
		
	}

}
