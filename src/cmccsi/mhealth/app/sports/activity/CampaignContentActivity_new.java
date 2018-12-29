package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.ActivityDetailData_new;
import cmccsi.mhealth.app.sports.bean.MedalInfo_new;
import cmccsi.mhealth.app.sports.bean.RankListInfo_new;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.tabhost.CampaignRankFragment;
import cmccsi.mhealth.app.sports.view.CustomDialog;
import cmccsi.mhealth.app.sports.view.CustomDialogOK;
import cmccsi.mhealth.app.sports.view.CustomListDialog;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.SyncHorizontalScrollView;
import cmccsi.mhealth.app.sports.R;

public class CampaignContentActivity_new extends BaseActivity {

	protected static final String TAG = "CampaignContentActivity_new";

	private ImageButton mImageButtonBack;

	private String mActivityTitle="";
	private TextView mTextViewTitleRun;
	public String userNm = "";
//	public String userId = "";

	private boolean isPersonal = true;
	private CustomProgressDialog mProgressDialog;
	private ViewPager mViewPager;
	private ViewPager mViewPager_group;
	private TabFragmentPagerAdapter mFragmentAdapter;
	private TabFragmentPagerAdapter mFragmentAdapter_group;
	public List<Fragment> mFtList;
	public List<Fragment> mFtList_group;
	/**
	 * 传递过来的活动ID
	 */
	private String mActivityId="";
	private RadioGroup mRadioGroup;
	private ActivityDetailData_new mActivity;
	private List<String> mTabTitle = new ArrayList<String>();
	private List<String> mGroupTabTitle = new ArrayList<String>();

	private TextView mTextViewName;
	private TextView mTextViewGroup;
	private TextView mTextViewAvgStep;
	private TextView mTextViewPersent;
	private TextView mtv_list_description;
	private TextView mtv_descript;
	private TextView mtv_Medal;

	private TextView mTextViewlist1RankId;
	
	private ImageView mImageViewAvater;

	private RadioButton mRadioButtonPersonal;

	private LinearLayout mll_activity_persent;
	private LinearLayout mLlActivitydetailsTop;

	private int indicatorWidth;
	private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private RadioGroup rg_nav_content;
	private ImageView iv_nav_indicator;
	private ImageView iv_nav_left;
	private ImageView iv_nav_right;
	private int currentIndicatorLeft = 0;
	
	protected int mFlag = 0;

	protected int mClubId = 0;

	private String mDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_new);
		try {
			Intent it=getIntent();
			mActivityId=it.getStringExtra("ACTIVITYID");
			mActivityTitle=it.getStringExtra("ACTIVITYTITLE");
			userNm=PreferencesUtils.getString(getBaseContext(), SharedPreferredKey.USERUID, "");
			initView();
			loadlogic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			mProgressDialog.cancel();
			switch (msg.what) {
			case 0:
				mDescription=mActivity.description;
				mActivityTitle=mActivity.name;
				if(mActivity.userRank!=null){
				displayActivityDetail(mActivity,mActivity.userRank.size(),isPersonal);
				//头像
				String avatar = PreferencesUtils.getString(
						getBaseContext(), SharedPreferredKey.AVATAR, null);
				if (avatar != null) {
					ImageUtil.getInstance().loadBitmap(mImageViewAvater,
							avatar);
				}
				//数据显示
				initViewPager();
				}
				break;

			case 1:
				mProgressDialog.cancel();
				ToastUtils.showToast(getBaseContext(), getResources().getString(R.string.campaigncontentactivity_getmsgfalse));				
				break;

			case 2:


				break;
			case 3:

				Toast.makeText(getApplicationContext(), getResources().getString(R.string.campaigncontentactivity_getorderfalse), Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};


	@Override
	protected void onResume() {
		super.onResume();
		mRadioButtonPersonal.setChecked(true);
		// loadlogic();
	}

	private void loadlogic() {
		mFtList=new ArrayList<Fragment>();
		mFtList_group=new ArrayList<Fragment>();
		getActivityDetail();
	}

	private void getActivityDetail() {
		//加了“努力加载中”的dialog
		mProgressDialog.setMessage(getResources().getString(R.string.text_wait));
		mProgressDialog.show();
		new Thread() {
			public void run() {
				mActivity = new ActivityDetailData_new();
				if (DataSyn.getInstance().getAvtivityDetailInfo(userNm,mActivityId,mActivity) == 0) {
					handler.sendEmptyMessage(0);
				}else{
					mActivity=null;
					handler.sendEmptyMessage(1);
				}
			};
		}.start();
	}


	@SuppressWarnings("deprecation")
	private void initView() {
		mProgressDialog = CustomProgressDialog.createDialog(this);

		mTextViewTitleRun = ((TextView) findView(R.id.textView_title));
		mTextViewTitleRun.setText(mActivityTitle);

		mImageButtonBack = (ImageButton) findView(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.my_button_back));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CampaignContentActivity_new.this.finish();
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.silde_out_right);
			}
		});

		mImageViewAvater = findView(R.id.iamgeview_activitydetails_avater);
		mTextViewName = findView(R.id.textview_activitydetails_name);
		mTextViewGroup = findView(R.id.textview_activitydetails_group);
		mTextViewAvgStep = findView(R.id.textview_activitydetails_avgstep);
		mTextViewPersent = findView(R.id.tv_activity_persent);
		mtv_list_description=findView(R.id.tv_list_description);
		mRadioButtonPersonal = findView(R.id.radiobutton_personal);
		mtv_list_description=findView(R.id.tv_list_description);
		mLlActivitydetailsTop = findView(R.id.ll_activitydetails_top);
		
		mtv_descript=findView(R.id.tv_descript);
		mtv_descript.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mActivity != null) {
					if (mDescription == null || mDescription == "") {
						BaseToast(getResources().getString(R.string.campaigncontentactivity_noactivitydescription));
						return;
					}
//					showDialog(mActivityTitle, mDescription);
					showDialog(getResources().getString(R.string.campaigncontentactivity_activitydescription), mDescription);
				}
			}
		});
		mtv_Medal=findView(R.id.tv_Medal);
		mtv_Medal.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mActivity != null) {
					showMedalDialog(getResources().getString(R.string.campaigncontentactivity_awardsetting), getMedalList(mActivity));
				}
			}
		});

		mTextViewlist1RankId = findView(R.id.textview_activitydetails_list1_rankid);
		mll_activity_persent=findView(R.id.ll_activity_persent);
		mRadioGroup = findView(R.id.radiogroup_activitydetails);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mActivity == null) {
					return;
				}
				switch (checkedId) {
				case R.id.radiobutton_personal:
					isPersonal = true;	
					if(mActivity.userRank!=null){
						displayActivityDetail(mActivity,mActivity.userRank.size(),isPersonal);
					}
					mViewPager_group.setVisibility(View.GONE);
					mViewPager.setVisibility(View.VISIBLE);
					break;
				case R.id.radiobutton_group:
					isPersonal = false;
					if(mActivity.groupRank!=null){
						displayActivityDetail(mActivity,mActivity.groupRank.size(),isPersonal);
						}
					mViewPager.setVisibility(View.GONE);
					mViewPager_group.setVisibility(View.VISIBLE);
					break;
				}
				initViewPager();
			}
		});
		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);
		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		mViewPager=findView(R.id.mViewPager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position)).performClick();
					if(mActivity!=null&&mActivity.userRank!=null&&mActivity.userRank.size()>position){
						displayPersonOfActivity(mActivity.userRank.get(mActivity.userRank.size()-position-1), mActivity.aimstep);
					}
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		mViewPager_group=findView(R.id.mViewPager_group);
		mViewPager_group.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position)).performClick();
					if(mActivity!=null&&mActivity.groupRank!=null&&mActivity.groupRank.size()>position){
						displayPersonOfActivity(mActivity.groupRank.get(mActivity.groupRank.size()-position-1), mActivity.aimstep);
					}
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

					// 记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
					if(isPersonal){
						if (mTabTitle.size() > 3) {
							mHsv.smoothScrollTo(
									(checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0)
											- ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
						}
					}else{
						if (mGroupTabTitle.size() > 3) {
							mHsv.smoothScrollTo(
									(checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0)
											- ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
						}
					}
					
				}
				
				if(isPersonal){
					if(mActivity.userRank!=null&&mActivity.userRank.size()>checkedId){
						displayPersonOfActivity(mActivity.userRank.get(mActivity.userRank.size()-checkedId-1), mActivity.aimstep);
					}
					mViewPager.setCurrentItem(checkedId);
				}else{
					if(mActivity.groupRank!=null&&mActivity.groupRank.size()>checkedId){
						displayPersonOfActivity(mActivity.groupRank.get(mActivity.groupRank.size()-checkedId-1), mActivity.aimstep);
					}
					mViewPager_group.setCurrentItem(checkedId);
					
				}
			}
		});
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	public void showDialog(String title, String content) {
		final CustomDialogOK dialog = new CustomDialogOK(
				CampaignContentActivity_new.this, R.style.customdialog,
				R.layout.customdialog, title, content);
		dialog.show();
	}
	
	/**
	 * 显示奖项信息
	 * TODO
	 * @param title 标题
	 * @param contents 内容
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:00:25
	 */
	private void showMedalDialog(String title, List<String> contents) {
		final CustomListDialog dialog = new CustomListDialog(
				CampaignContentActivity_new.this, R.style.customdialog,title, contents);
		dialog.show();
	}
	
	/**
	 * 获取奖项描述
	 * TODO
	 * @param data
	 * @return
	 * @return List<String>
	 * @author jiazhi.cao
	 * @time 上午9:01:04
	 */
	private List<String> getMedalList(ActivityDetailData_new data) {
		List<String> result = new ArrayList<String>();
		try{
			for (MedalInfo_new tempdata : data.medalinfo) {
				if (tempdata.medaltype.equals("user") && isPersonal) {
					if (tempdata.medalgap == 0) {
						result.add(tempdata.medalname + "　　已入围");
					} else {
						result.add(tempdata.medalname + "　　距离入围步数" + tempdata.medalgap);
					}
				} else if (tempdata.medaltype.equals("group") && !isPersonal) {
					if (tempdata.medalgap == 0) {
						result.add(tempdata.medalname + "　　已入围");
					} else {
						result.add(tempdata.medalname + "　　距离入围步数" + tempdata.medalgap);
					}
				}
			}
			if(result.size()==0){
				result.add(getResources().getString(R.string.campaigncontentactivity_noaward));
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			result.add(getResources().getString(R.string.campaigncontentactivity_noaward));
		}
		
		return result;
	}
	
	/**
	 * 展示活动信息
	 * TODO
	 * @param data 活动信息
	 * @param level 组织level
	 * @param isperson
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:57:47
	 */
	private void displayActivityDetail(ActivityDetailData_new data,int level,boolean isperson){
		if(data==null){
			return;
		}
		
		if(isperson){
			if(mActivity.userRank!=null&&mActivity.userRank.size()>=level){
				displayPersonOfActivity(data.userRank.get(data.userRank.size()-level),data.aimstep);
			}
//			mTabTitle.clear();// 标题
			if(mTabTitle.size()==0){
				//总是倒序的
				for(int i=mActivity.levelList.size()-1;i>-1;i--){
					mTabTitle.add(mActivity.levelList.get(i).orgName);
				}
			}
			
			initNavigationHSV(mTabTitle);
		}else{
			if(mActivity.groupRank!=null&&mActivity.groupRank.size()>=level){
				displayPersonOfActivity(data.groupRank.get(data.groupRank.size()-level),data.aimstep);
			}
//			mGroupTabTitle.clear();// 标题
			if(mGroupTabTitle.size()==0){
				//总是倒序的
				for(int i=mActivity.levelList.size()-1;i>0;i--){
					mGroupTabTitle.add(mActivity.levelList.get(i).orgName);
				}
			}			
			initNavigationHSV(mGroupTabTitle);
		}
		
		
	}
	
	/**
	 * 展示个人基本信息
	 * TODO
	 * @param data
	 * @param position
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:48:32
	 */
	private void displayPersonOfActivity(RankListInfo_new data,int aimstep) {
		if (data == null) {
			return;
		}

		mLlActivitydetailsTop.setVisibility(View.VISIBLE);
		mTextViewName.setText(data.membername);
		mTextViewGroup.setText(data.areaName);
		//获取焦点
		mTextViewGroup.setFocusableInTouchMode(true);
		mTextViewGroup.requestFocus();
		mTextViewAvgStep.setText(String.valueOf(data.memberstep));
		mTextViewlist1RankId.setText(String.valueOf(data.memberrank));
		// 达标率
		if (aimstep == 0) {
			// 没有达标率
			mll_activity_persent.setVisibility(View.GONE);
			mtv_list_description.setText(getResources().getString(R.string.campaigncontentactivity_averagenumberofsteps));
		} else {
			mll_activity_persent.setVisibility(View.VISIBLE);
			mTextViewPersent.setText(String.valueOf(data.wcl)+"%");
			mtv_list_description.setText("达标率排行榜\r\n(相同达标率按OA顺序显示)");
		}

	}
	
	/**
	 * 初始化标题
	 * TODO
	 * @param tabTitle
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:02:29
	 */
	private void initNavigationHSV(List<String> tabTitle) {
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
		LayoutInflater mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

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
	
	private void initViewPager(){
		if(mActivity.userRank!=null){
			mFtList.clear();
			for (int i = mActivity.userRank.size()-1; i > -1; i--) {
				int templevel = 0;
				try {
					templevel = Integer.parseInt(mActivity.userRank.get(i).level);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				CampaignRankFragment crf = new CampaignRankFragment(mActivity.userRank.get(i), "user", templevel,
						mActivityId);
				mFtList.add(crf);
			}
		}
		if(mActivity.groupRank!=null){
		mFtList_group.clear();
		for (int i = mActivity.groupRank.size()-1; i > -1; i--) {
			int templevel = 0;
			try {
				templevel = Integer.parseInt(mActivity.groupRank.get(i).level);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			CampaignRankFragment crf = new CampaignRankFragment(mActivity.groupRank.get(i), "group", templevel,
					mActivityId);
			mFtList_group.add(crf);
		}
		}
//		// 团队目前没有级别
//		CampaignRankFragment crf = new CampaignRankFragment(mActivity.groupRank.get(0), "group", 1, mActivityId);
//		mFtList_group.add(crf);
		if(mFragmentAdapter==null){
			mFragmentAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFtList);
		}
		mViewPager.setAdapter(mFragmentAdapter);

		if(mFragmentAdapter_group==null){
			mFragmentAdapter_group = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFtList_group);			
		}
		mViewPager_group.setAdapter(mFragmentAdapter_group);
	}
	
	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> mlist;
		public TabFragmentPagerAdapter(FragmentManager fm,List<Fragment> list) {
			super(fm);
			this.mlist=list;
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = mlist.get(arg0);

			return ft;
		}

		@Override
		public int getCount() {
			return mlist.size();
		}

	}
}
