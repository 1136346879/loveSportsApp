package cmccsi.mhealth.app.sports.activity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import cmccsi.mhealth.app.sports.appversion.PreLoadLoginActivity;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.basic.SendMsgBroadCast;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.pedo.PedometerActivity;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.service.StepService_GPS;
import cmccsi.mhealth.app.sports.tabhost.TabBaseFragment;
import cmccsi.mhealth.app.sports.R;

public class PreLoadActivity extends BaseActivity {
	private static final String INTENT_ACCOUNT_INFO = "com.cmcc.jky.android.ACCOUNT";
	private static final String TAG = "PreLoadActivity";
	private static final int WHAT_LOAD_FINISHED = 1000;
	private String userUid;
	private int msgId;
	private LinearLayout loadingLinearLayout;
	private LinearLayout guideLinearLayout;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_LOAD_FINISHED:
				setAlert();
				restoreDeviceInfo();
				PreferencesUtils.putBoolean(PreLoadActivity.this, "newLogin", true);
				Bundle bundl = new Bundle();
				bundl.putBoolean("isLogin", true);
				if (msgId == 1) {
					Intent intent = new Intent();
					intent.putExtra("intent", 9);
					intent.setClass(PreLoadActivity.this, TabBaseFragment.class);
					startActivity(intent);
					finish();
				} else {
					// intentActivity(PreLoadActivity.this,
					// MainFragmentActivity.class, bundl, true);
					if (Config.ISALONE) {
						intentActivity(PreLoadActivity.this, MainFragmentActivity2.class, bundl, true);
					}else{
						intentActivity(PreLoadActivity.this, MainFragmentActivity2.class, bundl, true);
					}
					finish();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		// 是否是独立版的apk true表示是，false表示是非独立版的
		if (Config.ISALONE) {
			beginLogin();
		} else {
			Intent intent = getIntent();
			userUid = intent.getStringExtra(INTENT_ACCOUNT_INFO);
			msgId = intent.getIntExtra("startUpType", 0);
			if (TextUtils.isEmpty(userUid)) {
				showDialog();
				return;
			}
			new Thread(new InitAccountRunnable()).start();
		}
	}

	private void beginLogin() {
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		// 判断版本是否相同，不同开启欢迎导航页
		String versionName = getVersion();
		System.out.println("versionName: 1 : "+info.getString( SharedPreferredKey.APPVERNAME, ""));
		System.out.println("versionName: 2 : "+versionName);
		if (!PreferencesUtils.getString(this, SharedPreferredKey.APPVERNAME, "").equals(versionName)) {
			Editor editorShare = info.edit();
			editorShare.clear();
			editorShare.commit();
			clearServise();
			Common.clearDatabases(this);
			setPreLoading();
		} else if (!info.getBoolean("checkdAuto", false))// 如果版本相同，判断是否自动登陆
		{
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
//					Intent intent = new Intent(PreLoadActivity.this, PreLoadLoginActivity.class);
//					startActivity(intent);
					Intent	intent = new Intent();
					intent.setClass(PreLoadActivity.this, PedometerActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					finish();
				}
			}, 1500);
		}else if (info.getBoolean("checkdAuto", false)){
			new Thread(new InitAccountRunnable()).start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 弹出dialog
	 * 
	 * @since 1.0.0
	 * @author Xiao
	 */
	private void showDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示！").setMessage("请从医疗云平台启动应用")
				.setPositiveButton("好的", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exit();
					}
				}).create();
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				exit();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private void exit() {
		finish();
	}


	/**
	 * 初始化user
	 * 
	 * @version 1.0.0
	 * @author Xiao
	 */
	private class InitAccountRunnable implements Runnable {

		@Override
		public void run() {
			Context context = PreLoadActivity.this;
			String versionName = getVersion();
			if (Config.ISALONE) {
				userUid = PreferencesUtils.getString(context, SharedPreferredKey.NICK_NAME, "");

			} else {

				if (!PreferencesUtils.getString(context, SharedPreferredKey.USERUID, "").equals(userUid)) {
					SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					Editor editorShare = info.edit();
					editorShare.clear();
					editorShare.commit();
					clearServise();
					Common.clearDatabases(PreLoadActivity.this);
				}
				//版本升级删除数据库数据不删除运动数据
				if (!PreferencesUtils.getString(context, SharedPreferredKey.APPVERNAME, "").equals(versionName)) {
					PreferencesUtils.putString(context, SharedPreferredKey.APPVERNAME, versionName);
					clearServise();
					Common.clearDatabasesWithoutPedo(PreLoadActivity.this);
				}
				
			}

			DataSyn dataSyn = DataSyn.getInstance();
			Log.d("cmccsi.mhealth.portal.sports", "userUid = " + userUid);
			dataSyn.setUserUid(userUid);
			dataSyn.loadServerInfo(context);
			dataSyn.loadUserInfoNotInThread(context);
			// dataSyn.loadClubId(context);
			DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
			dataSyn.getDeviceListData(deviceListInfo);

			
			mHandler.sendEmptyMessageDelayed(WHAT_LOAD_FINISHED, 2 * 1000);
		}
	}

	/**
	 * 保存设备信息到 sharedpreference中
	 */
	private void restoreDeviceInfo() {
		String Address = "";
		DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
		DeviceInfo deviceInfo_now = null;
		boolean have_bracelet_device = false;
		for (DeviceInfo deviceInfo : deviceListInfo.datavalue) {
			// 判断是否有手环设备
			if (Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				have_bracelet_device = true;
				Address = deviceInfo.deviceSerial.substring(2);
				if (deviceInfo != null) {
					Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					editor.putString(SharedPreferredKey.DEVICE_TOKEN, deviceInfo.deviceToken);
					editor.putString(SharedPreferredKey.DEVICE_NUMBER, deviceInfo.deviceNumber);
					editor.putString(SharedPreferredKey.DEVICE_VERSION, deviceInfo.deviceVersion);
					editor.commit();
				}
			}
			// 获取当前绑定设备
			if (Integer.valueOf(deviceInfo.isUsed) == 1) {
				deviceInfo_now = deviceInfo;
			}
		}
		PreferencesUtils.putBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, have_bracelet_device);
		PreferencesUtils.putString(this, SharedPreferredKey.DEVICE_ADDRESS, Address);
		if (deviceInfo_now != null) {
			Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(SharedPreferredKey.DEVICE_ID, deviceInfo_now.deviceSerial);
			System.out.println("---------DEVICE_ID------------"+deviceInfo_now.deviceSerial);
			editor.putString(SharedPreferredKey.DEVICE_NAME, deviceInfo_now.productName);
			editor.putString(SharedPreferredKey.DEVICE_MODEL, deviceInfo_now.productPara);
			editor.putInt(SharedPreferredKey.DEVICE_TYPE,
					Common.getDeviceType(deviceInfo_now.deviceSerial, deviceInfo_now.productPara));
			editor.commit();
		} else {
			// Editor editor =
			// this.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
			// Context.MODE_PRIVATE).edit();
			// editor.putString(SharedPreferredKey.DEVICE_ID, null);
			// editor.putString(SharedPreferredKey.DEVICE_NAME, null);
			// editor.putString(SharedPreferredKey.DEVICE_MODEL, null);
			// editor.putInt(SharedPreferredKey.DEVICE_TYPE,
			// Common.getDeviceType(null));
			// editor.commit();
		}
	}

	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		if (Config.ISALONE) {
			beginLogin();
		}else {
			setIntent(intent);// must store the new intent unless getIntent() will
			// return the old one
			Intent intent1 = getIntent();
			userUid = intent1.getStringExtra(INTENT_ACCOUNT_INFO);
			msgId = intent1.getIntExtra("startUpType", 0);
			System.out.println("-----启动页面-1111---msgId------" + msgId);
			if (TextUtils.isEmpty(userUid)) {
				showDialog();
				return;
			}
			new Thread(new InitAccountRunnable()).start();
		}
	}

	private void clearServise() {
		if (StepService_GPS.isRunning) {

			stopService(new Intent().setClass(this, StepService_GPS.class));
		}
		if (Common.isStepServiceRunning(this)) {
			Intent it = new Intent(Config.PHONESTEP_STOP_NOSAVE_ACTION);
			sendBroadcast(it);
		}
	}

	private void setAlert() {
		System.out.println("-----etwet--------");
		String strDate = getStringDateShort() + " 22:00:00";
		Date time = strToDateLong(strDate);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);// 获取系统提示服务
		Intent intent = new Intent(this, SendMsgBroadCast.class);// 一般用广播连用
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
		if (time.getTime() > System.currentTimeMillis()) {
			am.set(AlarmManager.RTC, time.getTime(), operation);// 开始设置闹铃
		}
	}

	/**
	 * 获取现在短时间字符串
	 * 
	 * @return 返回短时间字符串格式：年-月-日
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间字符串转换为时间类型 yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 返回时间类型长字符串时间 yyyy-MM-dd HH:mm:ss
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*---------------------------------------------------------------------*/
	private boolean bNewInsatall;
	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	// 指示点图片
	private ImageView mImageView;
	private ImageView[] mImageViews;
	// 该应用的主布局LinearLayout
	private ViewGroup mainViewGroup;
	// 主布局底部指示当前页面的小圆点视图，LinearLayout
	private ViewGroup indicatorViewGroup;
	// 定义LayoutInflater
	private LayoutInflater mInflater;
	private View mLayoutBasic5;
	private int[] imageRes = new int[] { R.drawable.loading_guid1, R.drawable.loading_guid2, R.drawable.loading_guid3 };

	private void setPreLoading() {
		// TODO Auto-generated method stub
		mInflater = getLayoutInflater();
		mPageViews = new ArrayList<View>();
		mLayoutBasic5 = mInflater.inflate(R.layout.view_basic5, null);
		ImageView ll5 = (ImageView) mLayoutBasic5.findViewById(R.id.imageview_loading_title5);
		ll5.setImageResource(imageRes[imageRes.length - 1]);
		ImageView imageButtonEnter = (ImageView) mLayoutBasic5.findViewById(R.id.imageButton_enter);

		imageButtonEnter.setOnClickListener(new OnClickListener() {

			@Override
			public synchronized void onClick(View arg0) {
				// Intent intent = new Intent();
				// intent.setClass(PreLoadActivity.this,
				// PreLoadLoadingActivity.class);
				// startActivity(intent);
				// PreLoadGuideActivity.this.finish();
				// overridePendingTransition(R.anim.slide_in_right,
				// R.anim.silde_out_left);
				loadingLinearLayout.setVisibility(View.VISIBLE);
				guideLinearLayout.setVisibility(View.GONE);

				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				Editor editorShare = info.edit();
				editorShare.putString(SharedPreferredKey.APPVERNAME, getVersion());
				editorShare.commit();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
//						Intent intent = new Intent(PreLoadActivity.this, PreLoadLoginActivity.class);
//						startActivity(intent);
					Intent	intent = new Intent();
						intent.setClass(PreLoadActivity.this, PedometerActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
						finish();
					}
				}, 1500);
			}
		});
		ImageView[] guideImages = new ImageView[3];
		for (int i = 0; i < guideImages.length - 1; i++) {
			guideImages[i] = new ImageView(this);
			guideImages[i].setImageResource(imageRes[i]);
			guideImages[i].setScaleType(ScaleType.FIT_XY);
			mPageViews.add(guideImages[i]);
		}
		mPageViews.add(mLayoutBasic5);
		mImageViews = new ImageView[mPageViews.size()];
		mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.activity_loading, null);
		loadingLinearLayout = (LinearLayout) mainViewGroup.findViewById(R.id.linearLayout_loading_title);
		guideLinearLayout = (LinearLayout) mainViewGroup.findViewById(R.id.linearguideLayout_loading);
		loadingLinearLayout.setVisibility(View.GONE);
		guideLinearLayout.setVisibility(View.VISIBLE);
		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.linearguideLayout_myviewpager);
		indicatorViewGroup = (ViewGroup) mainViewGroup.findViewById(R.id.linearguideLayout_mybottomviewgroup);
		for (int i = 0; i < mImageViews.length; i++) {
			mImageView = new ImageView(PreLoadActivity.this);
			// mImageView.setLayoutParams(new LayoutParams(20, 20));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
			lp.setMargins(20, 0, 20, 0);
			mImageView.setLayoutParams(lp);
			// mImageView.setPadding(40, 0, 40, 0);
			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageView.setBackgroundResource(R.drawable.page_indicator);
			}
			mImageViews[i] = mImageView;
			// 把指示作用的远点图片加入底部的视图中
			indicatorViewGroup.addView(mImageViews[i]);
		}
		// 注意这两种用法的区别，前者无法正常显示！！
		// setContentView(R.layout.main);
		setContentView(mainViewGroup);
		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}
}
