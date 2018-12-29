package cmccsi.mhealth.app.sports.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.PedoRankBriefInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankListInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.controller.PedoRankController;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.tabhost.RankinglistFragment;
import cmccsi.mhealth.app.sports.view.SyncHorizontalScrollView;
import cmccsi.mhealth.app.sports.R;

/**
 * 排行榜Activity
 * 
 * @type RankingActivity
 * @author zhangfengjuan
 * @time 2015-3-12下午9:43:18
 */
public class RankingActivity extends BaseActivity {

	public static final String ARGUMENTS_RANK = "arg";
	public static final String ISAREARANK = "is_area_rank";
	public static final String RANK_AREA = "0";// 区域
	public static final String RANK_GROUP = "1";// 企业

	private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private RadioGroup rg_nav_content;
	private ImageView iv_nav_indicator;
	private ImageView iv_nav_left;
	private ImageView iv_nav_right;
	private ViewPager mViewPager;
	private int indicatorWidth;
	public List<String> tabTitle = new ArrayList<String>();// 标题
	private LayoutInflater mInflater;
	private TabFragmentPagerAdapter mAdapter;
	private int currentIndicatorLeft = 0;
	private RadioGroup mRgTime;
	private RadioGroup mRgPersonalOrTeam;
	public List<Fragment> mFtList;
	private String oneOrSeven;// 昨日 、七日
	private String teamOrPerSon;// 个人、团队
	private ArrayList<PedoRankBriefInfo> datavalue;
	private int isupdate = 0;
	private PedoRankController mPedoRankController;
	private String GroupOrArea = "0";//
	private ImageView mIvBack;
	private TextView mTvTitle;
	private boolean isNotCall = true;// 是否没有初始化视图，即有没有调用initView
	private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking);

		Intent intent = getIntent();
		if (intent != null) {
			GroupOrArea = intent.getStringExtra(ISAREARANK);
		}

		oneOrSeven = "1";
		teamOrPerSon = "user";

		datavalue = new ArrayList<PedoRankBriefInfo>();
		mPedoRankController = PedoRankController.GetPedoRankBriefController(this);

		mFtList = new ArrayList<Fragment>();

		findViewById();// 初始化控件
        if(GroupOrArea.equals(RANK_AREA)){
        	mTvTitle.setText("区域排行榜");
        }else if(GroupOrArea.equals(RANK_GROUP)){
        	mTvTitle.setText("日常排名");
        }

		if (mPedoRankController != null && !tabTitle.isEmpty()) {//
			if (mFtList.isEmpty()) {
				for (int i = 0; i < tabTitle.size(); i++) {
					Fragment ft = new RankinglistFragment(null, GroupOrArea);
					mFtList.add(ft);
				}
			}
		}
		setListener();
		getRankInfo();// 获取数据并更新视图
	}

	/**
	 * TODO 先判断本地数据库是否最新数据，如果最新直接获取；否则需要重新请求，更新到数据库，覆盖原数据；
	 * 
	 * @param dayCount
	 * @param type
	 * @return void
	 * @author zhangfengjuan
	 * @time 下午3:46:24
	 */
	private void getRankInfo() {
		if (mPedoRankController != null) {

			isupdate = mPedoRankController.CheckIsUpdateDate(oneOrSeven, teamOrPerSon, GroupOrArea);
			if (isupdate == 0) {// 获取数据数据
				ArrayList<PedoRankBriefInfo> tempDatavalue = mPedoRankController.GetAllPedoRankBriefInfo(oneOrSeven,
						teamOrPerSon, GroupOrArea);
				if (!tempDatavalue.isEmpty()) {
					datavalue = tempDatavalue;
					tabTitle.clear();// 将之前的tab名称清空。
//					mFtList.clear();
					for (int i = 0; i < datavalue.size(); i++) {
						tabTitle.add(datavalue.get(i).getAreaName());
					}
					if (mFtList.isEmpty()) {
						for (int i = 0; i < tabTitle.size(); i++) {
							Fragment ft = new RankinglistFragment(datavalue, GroupOrArea);
							mFtList.add(ft);
						}
					}

					if (isNotCall) {// 只要调用一次initView方法
						initView();
						isNotCall = false;
					} else {
						int index = 0;
						RankinglistFragment fragment = (RankinglistFragment) mFtList.get(index);
						fragment.setParams(oneOrSeven, teamOrPerSon, GroupOrArea, index, i);
						i = 0;
						fragment.transportData(datavalue);
						fragment.showdata(datavalue.get(index));
						mAdapter.notifyDataSetChanged();
						mViewPager.setCurrentItem(index);
						initNavigationHSV();

					}
				} else { //
					new GetPedoRankListInfoTask().execute(oneOrSeven, teamOrPerSon);
				}

			} else if (isupdate == 1) {// 获取网络数据
				new GetPedoRankListInfoTask().execute(oneOrSeven, teamOrPerSon);
			}
		} else {
			new GetPedoRankListInfoTask().execute(oneOrSeven, teamOrPerSon);
		}
	}

	/*
	 * 加载控件
	 */
	private void findViewById() {
		
		mTvTitle =  (TextView) findViewById(R.id.textView_title_rank);
		
		mIvBack = (ImageView) findViewById(R.id.button_input_bg_back);

		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		mRgTime = (RadioGroup) findViewById(R.id.rg_time);
		mRgPersonalOrTeam = (RadioGroup) findViewById(R.id.rg_personal_team);

		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);

		mRgTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_yestoday) {
					oneOrSeven = "1";
					i = 1;
				} else if (checkedId == R.id.rb_sevendays) {
					oneOrSeven = "7";
					i = 1;
				}
				getRankInfo();

			}
		});
		mRgPersonalOrTeam.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.rb_personal) {
					teamOrPerSon = "user";
					// currentRBtnClk=1;
				} else if (checkedId == R.id.rb_team) {
					teamOrPerSon = "group";
					// currentRBtnClk=2;
				}
				getRankInfo();

			}
		});

		mIvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RankingActivity.this.finish();
			}
		});

	}

	private void initView() {

		// 另一种方式获取
		initNavigationHSV();
		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
	}

	private void initNavigationHSV() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//
		if (tabTitle.size() > 3) {
			indicatorWidth = dm.widthPixels / 3;
		} else {
			if (tabTitle.size() == 0) {
				indicatorWidth = dm.widthPixels / 1;
			} else {
				indicatorWidth = dm.widthPixels / tabTitle.size();
			}
			iv_nav_right.setVisibility(View.INVISIBLE);
			iv_nav_left.setVisibility(View.INVISIBLE);
		}
		LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);
		mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
		// 获取布局填充器
		mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

		rg_nav_content.removeAllViews();
		// 添加选项卡
		for (int i = 0; i < tabTitle.size(); i++) {
			RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle.get(i));
			rb.setLayoutParams(new LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT));
			rg_nav_content.addView(rb);
		}
		// 这句代码是用于将第一个视图选中，使之字体变成蓝色
		((RadioButton) rg_nav_content.getChildAt(0)).performClick();
	}

	private void setListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position)).performClick();
					int index = mViewPager.getCurrentItem();
					RankinglistFragment fragment = (RankinglistFragment) mFtList.get(index);
					fragment.setParams(oneOrSeven, teamOrPerSon, GroupOrArea, index, i);
					i = 0;
					fragment.transportData(datavalue);
					fragment.showdata(datavalue.get(index));
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (rg_nav_content.getChildAt(checkedId) != null) {

					TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft,
							((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(100);
					animation.setFillAfter(true);

					// 执行位移动画
					iv_nav_indicator.startAnimation(animation);

					mViewPager.setCurrentItem(checkedId); // ViewPager 跟随一起 切换

					// 记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
					if (tabTitle.size() > 3) {
						mHsv.smoothScrollTo(
								(checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0)
										- ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
					}
				}
				if (mAdapter != null) {
					int index = mViewPager.getCurrentItem();
					RankinglistFragment fragment = (RankinglistFragment) mFtList.get(index);
					fragment.setParams(oneOrSeven, teamOrPerSon, GroupOrArea, index, i);
					i = 0;
					fragment.transportData(datavalue);
					fragment.showdata(datavalue.get(index));
					mAdapter.notifyDataSetChanged();
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {

			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = mFtList.get(arg0);
			Bundle argsv = new Bundle();
			argsv.putSerializable(ARGUMENTS_RANK, (Serializable) datavalue.get(arg0));
			ft.setArguments(argsv);
			return ft;
		}

		@Override
		public int getCount() {
			return tabTitle.size();
		}

	}

	/**
	 * 获取排行榜信息
	 * 
	 * @type GetPedoRankListInfoTask
	 * @author zhangfengjuan
	 * @time 2015-3-16下午3:03:04
	 */
	private class GetPedoRankListInfoTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showProgressDialog(getResources().getString(R.string.text_wait), RankingActivity.this);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {

			String dayCount = params[0];// 1 or 7
			String type = params[1];// team or user
			String userId = PreferencesUtils.getString(RankingActivity.this, SharedPreferredKey.USERUID, "");
			int OrgId;// 测试
			if (GroupOrArea.equals("0")) {
				OrgId = PreferencesUtils.getInt(RankingActivity.this, SharedPreferredKey.COUNTY_ID, 0);
			} else {
				OrgId = PreferencesUtils.getInt(RankingActivity.this, SharedPreferredKey.CLUB_ID, 0);
			}

			PedoRankListInfo mPedoRankListInfo = new PedoRankListInfo();
			int res = DataSyn.getInstance().getPedoRankListInfo(userId, OrgId + "", dayCount, type,
					Integer.parseInt(GroupOrArea), mPedoRankListInfo, 1+"");
			if (res == 0) {
				ArrayList<PedoRankBriefInfo> tempDatavalue = mPedoRankListInfo.datavalue;
				if (!tempDatavalue.isEmpty()) {
					datavalue = tempDatavalue;
				} 
			} 
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0) {
				mPedoRankController.InsertPedoRankBriefList(datavalue, oneOrSeven, teamOrPerSon, GroupOrArea);
				getRankInfo();
			} else if (result == -2) {
				datavalue = ShowTestData();
				if(GroupOrArea.equals(RANK_AREA)){
					ToastUtils.showToast(getApplicationContext(), "添加区域组织信息后第二天方可查看排名数据");
				}else{
					ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);;
				}
			}else{
				datavalue = ShowTestData();
				ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
			}

			dismiss();
			super.onPostExecute(result);
		}

	}

	/**
	 * datavalue为空的话，展示测试数据
	 * 
	 * @return
	 * @return ArrayList<PedoRankBriefInfo>
	 * @author zhangfengjuan
	 * @time 上午9:41:34
	 */
	private ArrayList<PedoRankBriefInfo> ShowTestData() {
		PedoRankDetailInfo detail = new PedoRankDetailInfo();
		detail.setRank(1);
		detail.setGroup("");
		detail.setName("无数据");
		detail.setStep(0);
		ArrayList<PedoRankDetailInfo> l = new ArrayList<PedoRankDetailInfo>();
		l.add(detail);
		PedoRankBriefInfo tempO = new PedoRankBriefInfo();
		// tempO.set_id(1);
		tempO.setAreaName("");
		// tempO.setDate("1");
		// tempO.setDayCount("1");
		tempO.setLevel(1);
		tempO.setMembername("错误数据");
		tempO.setMemberrank(-1);
		tempO.setMemberstep(-1);
		// tempO.setRankGroup(1);
		tempO.setRankList(l);
		// tempO.setType("");
		ArrayList<PedoRankBriefInfo> mPrbiList = new ArrayList<PedoRankBriefInfo>();
		mPrbiList.add(tempO);
		return mPrbiList;

	}

}
