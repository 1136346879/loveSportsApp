package cmccsi.mhealth.app.sports.tabhost;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.CampaignViewFragment_new;
import cmccsi.mhealth.app.sports.activity.RankingActivity;
import cmccsi.mhealth.app.sports.adapter.MyFragmentAdapter;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.ActivityInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

/*
 * 原系统为CampaignFragment
 * 修改布局结构改为viewpager显示
 */
public class CampaignFragment_new extends BaseFragment implements OnClickListener {

	public static String TAG = "RaceListActivity";
	private ViewPager mPager;// 页卡内容
	private ArrayList<Fragment> listViews; // Tab页面列表
	private TextView tx_race_now, tx_race_soon, tx_race_finish;// 页卡头标

	private ActivityInfo mActivityInfo;
	private String mCurrentTime;
	private String mPhonenum;
	private String mPassword;
	public boolean isFromDatabase = false;
	private int mActivitynownum;
	private int mActivityOldNum;
	private int mActivityFutureNum;
	public int ClubId = 1;
	public String userNm = "";
	private ImageButton mBack;
	private CustomProgressDialog mProgressDialog;

	MyFragmentAdapter myFragmentAd;

	SharedPreferences info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_racelist1, container, false);
		ClubId = PreferencesUtils.getInt(getActivity(), SharedPreferredKey.CLUB_ID, 0);
		userNm = PreferencesUtils.getString(getActivity(), SharedPreferredKey.NAME, null);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	private void initView() {
		mProgressDialog = CustomProgressDialog.createDialog(getActivity());
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setOnClickListener(this);

		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.racelist_title);
		tx_race_now = findView(R.id.tx_race_now);
		tx_race_now.setBackgroundResource(R.drawable.weight_top_round);
		tx_race_soon = findView(R.id.tx_race_soon);
		tx_race_finish = findView(R.id.tx_race_finish);
		tx_race_now.setOnClickListener(new txListener(1));
		tx_race_soon.setOnClickListener(new txListener(0));
		tx_race_finish.setOnClickListener(new txListener(2));
		mPager = findView(R.id.vPager_race);
		listViews = new ArrayList<Fragment>();
		myFragmentAd = new MyFragmentAdapter(getFragmentManager(), listViews);
		mPager.setAdapter(myFragmentAd);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
				// 改变了敬请期待和进行中的顺序，布局文件也有改动
				case 0:
					tx_race_now.setBackgroundColor(getResources().getColor(R.color.white));
					tx_race_soon.setBackgroundResource(R.drawable.weight_top_round);
					tx_race_finish.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				case 1:
					tx_race_now.setBackgroundResource(R.drawable.weight_top_round);
					tx_race_soon.setBackgroundColor(getResources().getColor(R.color.white));
					tx_race_finish.setBackgroundColor(getResources().getColor(R.color.white));
					break;
				case 2:
					tx_race_now.setBackgroundColor(getResources().getColor(R.color.white));
					tx_race_soon.setBackgroundColor(getResources().getColor(R.color.white));
					tx_race_finish.setBackgroundResource(R.drawable.weight_top_round);
					break;
				default:
					tx_race_now.setBackgroundColor(getResources().getColor(R.color.white));
					tx_race_soon.setBackgroundResource(R.drawable.weight_top_round);
					tx_race_finish.setBackgroundColor(getResources().getColor(R.color.white));
					break;
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

	/*
	 * 载入数据
	 */
	private void loadData() {
		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mPhonenum = sp.getString(SharedPreferredKey.PHONENUM, "");
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, "");

		mActivitynownum = sp.getInt("activity_now_num", 0);
		mActivityOldNum = sp.getInt("activity_old_num", 0);
		mActivityFutureNum = sp.getInt("activity_future_num", 0);

		Date currentDate = new Date();
		if (mCurrentTime == null) {
			mCurrentTime = Common.getDateAsYYYYMMDD(currentDate.getTime());
		}

		mActivityInfo = new ActivityInfo();
		info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mProgressDialog.setMessage(getResources().getString(R.string.text_wait));
		mProgressDialog.show();
		autoUpdate();

	}

	/*
	 * 自动更新数据
	 */
	private void autoUpdate() { // TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 请求数据成
//				int OrgId=PreferencesUtils.getInt(mActivity, SharedPreferredKey.CLUB_ID, 0);
				if (DataSyn.getInstance().getActivityInfo_new(3,ClubId, 1 , mActivityInfo) == 0) {
					if (mActivityInfo == null) {
						Logger.i(TAG, "活动列表解析错误");
						Message message = new Message();
						message.what = 2;
						mHandler.sendMessage(message);
					} else {
						mActivityFutureNum = mActivityInfo.activityfuturenum;
						mActivityOldNum = mActivityInfo.activityoldnum;
						mActivitynownum = mActivityInfo.activitynownum;
						// 存更新时间
						Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
								.edit();
						sharedata.putInt("activity_now_num", mActivitynownum);
						sharedata.putInt("activity_old_num", mActivityOldNum);
						sharedata.putInt("activity_future_num", mActivityFutureNum);
						sharedata.commit();
					}
					// 数据填充到集合
				} else {
					Logger.i(TAG, "活动列表获取错误");
					Message message = new Message();
					message.what = 2;
					mHandler.sendMessage(message);
				}

				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		});
		thread.start();

	}

	/*
	 * 绑定到列表
	 */
	private void addActivityInfo(ActivityInfo activityInfo, boolean fromdatabase) {

		CampaignViewFragment_new campaignSoon = new CampaignViewFragment_new(activityInfo.activityfuture, 2);
		CampaignViewFragment_new campaignNow = new CampaignViewFragment_new(activityInfo.activitynow, 1);
		CampaignViewFragment_new campaignFinish = new CampaignViewFragment_new(activityInfo.activityfinish, 0);
		listViews.add(0, campaignSoon);
		listViews.add(1, campaignNow);
		listViews.add(2, campaignFinish);
		for (int i = 0; i < listViews.size(); i++) {
			((CampaignViewFragment_new) listViews.get(i)).setmPhonenum(mPhonenum);
			((CampaignViewFragment_new) listViews.get(i)).setmPassword(mPassword);
			((CampaignViewFragment_new) listViews.get(i)).setClubId(ClubId);
		}
		myFragmentAd.notifyDataSetChanged();
		// 进入页面默认选择第二个pager
		mPager.setCurrentItem(1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button_input_bg_back) {
			getActivity().finish();
		}
	}

	@Override
	public void findViews() {
		try {
			// TODO Auto-generated method stub
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}

	/*
	 * tab点击
	 */
	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mActivitynownum = info.getInt("activity_now_num", 0);
				mActivityOldNum = info.getInt("activity_old_num", 0);
				mActivityFutureNum = info.getInt("activity_future_num", 0);
				addActivityInfo(mActivityInfo, isFromDatabase);
				break;
			case 2:
				// 加了网络异常的Toast
				ToastUtils.showToast(getActivity(), R.string.MESSAGE_INTERNET_ERROR);
				break;
			}
			mProgressDialog.dismiss();
			super.handleMessage(msg);
		}
	};

}
