package cmccsi.mhealth.app.sports.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.FriendsInfo;
import cmccsi.mhealth.app.sports.bean.GoalInfo;
import cmccsi.mhealth.app.sports.bean.GoalNetInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.RequestListInfo;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.pedo.PedometerActivity;
import cmccsi.mhealth.app.sports.pedo.TgbleManagerNeuro;
import cmccsi.mhealth.app.sports.pedo.UploadManager;
import cmccsi.mhealth.app.sports.tabhost.MainGridFragment;
import cmccsi.mhealth.app.sports.tabhost.TabBaseFragment;
import cmccsi.mhealth.app.sports.R;

/**
 * 主页1
 */
public class MainFragmentActivity extends BaseActivity implements OnClickListener {

	public static final int TAB_HEALTH = 0;
	public static final int TAB_CORPORATION = 1;
	public static final int TAB_PLAY = 2;
	private final int WHAT_ENSURE_EXIT = 1000;
	private final long DELAY_EXIT_MILLS = 3 * 1000;

	private ViewPager viewPager;
	private RadioButton main_tab_health, main_tab_corporation, main_tab_play;
	private ImageButton settingimg;
	// private ImageButton usercontent;
	private TextView tv_showGoal;
	private ImageView iv_showGoal;

	private Intent intent;
	private boolean isExiting;
	private final ExitRunnable exitRunnable = new ExitRunnable();
	boolean hasShowAlert = false;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			BackInfo bi = data.getParcelable("backinfo");
			switch (msg.what) {
			case WHAT_ENSURE_EXIT:
				int mDeviceType = PreferencesUtils.getInt(MainFragmentActivity.this, SharedPreferredKey.DEVICE_TYPE, 0);
				
				switch (mDeviceType) {
				case DeviceConstants.DEVICE_BRACLETE_BEATBAND:
					if (Build.VERSION.SDK_INT > 17) {
						TgbleManagerNeuro tgble = TgbleManagerNeuro.getSingleInstance(getApplicationContext());
						tgble.cancleUploadTime();
					}
					break;
				default:
					break;
				}
				new Thread(new Runnable() {					
					@Override
					public void run() {
						String mDeviceId=PreferencesUtils.getString(MainFragmentActivity.this, SharedPreferredKey.DEVICE_ID, "");
						PedometorDataInfo pedo= PedoController.GetPedoController(MainFragmentActivity.this).getLatestPedometer(mDeviceId);
						UploadManager.uploadBlePedo(pedo);						
					}
				});
				finish();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_main_fragment);
		BaseBackKey(getResources().getString(R.string.app_name), this);
		intent = new Intent();
		initView();
		addListener();
		Intent startIntent = getIntent();
		if (startIntent != null) {
			boolean isLogin = startIntent.getBooleanExtra("isLogin", false);
			String mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
			if (mDeviceId != null) {
				if (!isLogin) {
					// 登录到应用首先展示运动页面
					// intent.setClass(this, PedometorActivity.class);
					intent.setClass(this, PedometerActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.silde_out_left);
				}
			} else {
				ToastUtils.showToast(this, getResources().getString(R.string.mainfragmentactivity_note));
			}

		}
	}

	/**
	 * 加载页面元素
	 */
	private void initView() {
		iv_showGoal = findView(R.id.iv_showGoal);
		tv_showGoal = findView(R.id.tv_showGoal);
		ImageButton mImageButtonBack = findView(R.id.button_input_bg_back);
		mImageButtonBack.setOnClickListener(this);
		settingimg = (ImageButton) findViewById(R.id.imageButton_title);
		// usercontent.setVisibility(View.GONE);
		settingimg.setOnClickListener(this);
		settingimg.setVisibility(View.VISIBLE);
		settingimg.setBackgroundResource(R.drawable.tab_main_setting);
		viewPager = (ViewPager) findViewById(R.id.vp_main);
		main_tab_health = (RadioButton) findViewById(R.id.main_tab_health);
		main_tab_corporation = (RadioButton) findViewById(R.id.main_tab_corporation);
		main_tab_play = (RadioButton) findViewById(R.id.main_tab_play);
		main_tab_health.setOnClickListener(this);
		main_tab_corporation.setOnClickListener(this);
		main_tab_play.setOnClickListener(this);

		adapter = new FragmentAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
	}

	private FragmentAdapter adapter;

	/**
	 * 添加监听
	 */
	private void addListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int id) {

				switch (id) {
				case TAB_HEALTH:
					main_tab_health.setChecked(true);
					break;
				case TAB_CORPORATION:
					main_tab_corporation.setChecked(true);
					// if (!hasShowAlert&&!isClubInfoAvailable()) {
					// hasShowAlert=true;
					// Toast.makeText(getApplicationContext(),
					// getString(R.string.msg_show_club_error),
					// Toast.LENGTH_LONG).show();
					// }
					break;
				case TAB_PLAY:
					main_tab_play.setChecked(true);
					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_tab_health:
			viewPager.setCurrentItem(TAB_HEALTH);
			break;
		case R.id.main_tab_corporation:
			viewPager.setCurrentItem(TAB_CORPORATION);
			if (!hasShowAlert && !isClubInfoAvailable()) {
				hasShowAlert = true;
				Toast.makeText(this, getString(R.string.msg_show_club_error), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_tab_play:
			viewPager.setCurrentItem(TAB_PLAY);
			break;
		case R.id.imageButton_title:
			intent.setClass(this, TabBaseFragment.class);
			intent.putExtra("intent", 11);
			startActivity(intent);
			break;
		case R.id.button_input_bg_back:
//			intent.setClass(this, TabBaseFragment.class);
//			intent.putExtra("intent", 12);
//			startActivity(intent);
			new Thread(new Runnable() {					
				@Override
				public void run() {
					String mDeviceId=PreferencesUtils.getString(MainFragmentActivity.this, SharedPreferredKey.DEVICE_ID, "");
					PedometorDataInfo pedo= PedoController.GetPedoController(MainFragmentActivity.this).getLatestPedometer(mDeviceId);
					UploadManager.uploadBlePedo(pedo);						
				}
			}).start();
			finish();
			break;
		case R.id.iv_showGoal:
			intent.setClass(this, PedometerActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// if (isExiting) {
		// handler.removeCallbacks(exitRunnable);
		// handler.sendEmptyMessage(WHAT_ENSURE_EXIT);
		// }else {
		// isExiting = true;
		// handler.postDelayed(exitRunnable, DELAY_EXIT_MILLS);
		// Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
		// }
		int mDeviceType = PreferencesUtils.getInt(MainFragmentActivity.this, SharedPreferredKey.DEVICE_TYPE, 0);
		switch (mDeviceType) {
		case 2:
			if (Build.VERSION.SDK_INT > 17) {
				TgbleManagerNeuro tgble = TgbleManagerNeuro.getSingleInstance(getApplicationContext());
				tgble.cancleUploadTime();
			}
			
			break;

		default:
			break;
		}
		new Thread(new Runnable() {					
			@Override
			public void run() {
				String mDeviceId=PreferencesUtils.getString(MainFragmentActivity.this, SharedPreferredKey.DEVICE_ID, "");
				PedometorDataInfo pedo= PedoController.GetPedoController(MainFragmentActivity.this).getLatestPedometer(mDeviceId);
				UploadManager.uploadBlePedo(pedo);						
			}
		}).start();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 加载好友
		new Thread(new LoadFriendListRunnable()).start();
		
		GoalInfo info = GoalInfo.getInstance(this);
		iv_showGoal.setImageResource(info.type.getImgRes());
		iv_showGoal.setOnClickListener(this);
		tv_showGoal.setText(info.getGoalReportInTime(this));
		new LoadAchievementTask().execute();
		adapter.notifyDataSetChanged();
	}

	/**
	 * club信息是否正常获取
	 * 
	 * @return
	 */
	private boolean isClubInfoAvailable() {
		return PreferencesUtils.getInt(this, SharedPreferredKey.CLUB_ID, Constants.DEFAULT_CLUBID) != Constants.DEFAULT_CLUBID;
	}

	/*
	 * private String getAchievementStr(String achievementName, String value) {
	 * return getString(R.string.showachievement, achievementName, value); }
	 */

	private class LoadAchievementTask extends AsyncTask<String, Integer, GoalInfo> {

		@Override
		protected GoalInfo doInBackground(String... params) {
			GoalNetInfo goalNetInfo = new GoalNetInfo();
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			int res = DataSyn.getInstance().getGoalData("2014-10-01", dateFormatter.format(date), goalNetInfo);
			GoalInfo goalInfo = null;
			if (res == 0) {
				goalInfo = GoalInfo.getInstanse(goalNetInfo);
				restoreGoalInfo2SP(goalNetInfo);
			} else {
				goalInfo = GoalInfo.getInstance(MainFragmentActivity.this);
			}
			mFriendReqData = new RequestListInfo();
			mRaceReqData = new RequestListInfo();
			String pn = PreferencesUtils.getString(MainFragmentActivity.this, SharedPreferredKey.PHONENUM, "");
			String pwd = PreferencesUtils.getString(MainFragmentActivity.this, SharedPreferredKey.PASSWORD, "");
			int suc = DataSyn.getInstance().getFriendRequestList(pn, pwd, mFriendReqData);
			suc *= DataSyn.getInstance().getRaceInvitedRequestList(pn, pwd, mRaceReqData);
			if (suc == 0) {
				if ((mFriendReqData != null && mFriendReqData.dataValue.size() > 0)
						|| (mRaceReqData != null && mRaceReqData.dataValue.size() > 0)) {
					PreferencesUtils.putBoolean(getApplicationContext(), "ISHAVENEWMSG", true);
				} else {
					PreferencesUtils.putBoolean(getApplicationContext(), "ISHAVENEWMSG", false);
				}
			} else {
				PreferencesUtils.putBoolean(getApplicationContext(), "ISHAVENEWMSG", false);
			}
			return goalInfo;
		}

		/**
		 * 把goal数据存储到SharedPreferences中
		 * 
		 * @param goalNetInfo
		 */
		private void restoreGoalInfo2SP(GoalNetInfo goalNetInfo) {
			PreferencesUtils.putString(MainFragmentActivity.this, SharedPreferredKey.GOAL_TYPE, goalNetInfo.goalinfo.goal);
			PreferencesUtils.putString(MainFragmentActivity.this, SharedPreferredKey.LATEST_RATE, goalNetInfo.goalinfo.rate);
			PreferencesUtils.putString(MainFragmentActivity.this, SharedPreferredKey.CURRENT_DISTANCE,
					goalNetInfo.goalinfo.distance);
		}

		@Override
		protected void onPostExecute(GoalInfo result) {
			super.onPostExecute(result);
			iv_showGoal.setImageResource(result.type.getImgRes());
			tv_showGoal.setText(result.getGoalReportInTime(MainFragmentActivity.this));
		}

	}

	private RequestListInfo mFriendReqData;// 好友请求列表
	private RequestListInfo mRaceReqData;// 比赛请求列表

	/**
	 * 预先加载好友数据
	 * 
	 * @author Xiao
	 *
	 */
	private class LoadFriendListRunnable implements Runnable {
		public void run() {
			FriendsInfo friendsReqData = new FriendsInfo();
			try {
				int res = DataSyn.getInstance().getFriendsList(friendsReqData);
				if (res == 0) {
					MHealthProviderMetaData healthProvider = MHealthProviderMetaData
							.GetMHealthProvider(MainFragmentActivity.this);
					healthProvider.deleteMyFriend();
					healthProvider.FriendInsertValue(friendsReqData.friendslist);
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismiss();
			}
		}
	}

	private class ExitRunnable implements Runnable {

		@Override
		public void run() {
			isExiting = false;
		}
	}

	/**
	 * 自定义fragment适配器
	 * 
	 * @author cst
	 *
	 */
	public class FragmentAdapter extends FragmentPagerAdapter {
		public final static int TAB_COUNT = 3;

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int id) {
			switch (id) {
			case MainFragmentActivity.TAB_HEALTH:
				MainGridFragment homeFragment = new MainGridFragment(TAB_HEALTH);
				return homeFragment;
			case MainFragmentActivity.TAB_CORPORATION:
				MainGridFragment categoryFragment = new MainGridFragment(TAB_CORPORATION);
				return categoryFragment;
			case MainFragmentActivity.TAB_PLAY:
				MainGridFragment carFragment = new MainGridFragment(TAB_PLAY);
				return carFragment;
			}
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return TAB_COUNT;
		}
	}

}
