package cmccsi.mhealth.app.sports.ecg.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.bean.ECGListinfo;
import cmccsi.mhealth.app.sports.bean.ECGSummary;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.ecg.fragment.ECGChartFragment;
import cmccsi.mhealth.app.sports.ecg.utils.Range;
import cmccsi.mhealth.app.sports.ecg.utils.RangeUtil;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

public class HistoryECGDetailActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "HistoryECGDetailActivity";
	private FragmentStatePagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private CustomProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ecg_detail1);
		initViews();
		Range semiAnnual = RangeUtil.getRange(RangeUtil.RANGE_TYPE_SEMIANNUAL);
		new DownloadEcgTask(semiAnnual.getStartDate(),semiAnnual.getEndDate()).execute();
	}

	private void initViews() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// 滑动时选择对应单选框
						RadioButton radio = (RadioButton) mRadioGroup
								.getChildAt(position);
						radio.setChecked(true);
					}
				});
		OnRadioItemClickListener radioItemClickListener = new OnRadioItemClickListener();
		// 每一个单选框都设置ClickListener
		for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
			mRadioGroup.getChildAt(i)
					.setOnClickListener(radioItemClickListener);
		}
		//返回按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.button_input_bg_back);
		backButton.setBackground(getResources().getDrawable(R.drawable.my_button_back));
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		//标题
		TextView textTitle = (TextView) findViewById(R.id.textView_title);
		textTitle.setText("趋势分析");
		
	}

	/**
	 * 显示dialog
	 * 
	 * @param msg
	 * @param context
	 */
	protected void showProgressDialog(String msg) {
		if ((!isFinishing()) && (this.mProgressDialog == null)) {
			//this.mProgressDialog = new ProgressDialog(this);
			this.mProgressDialog = CustomProgressDialog.createDialog(this); 
		}
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}

	/**
	 * 取消 dialog
	 */
	protected void dismissProgressDialog() {
		if ((!isFinishing()) && (this.mProgressDialog != null)) {
			this.mProgressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {	
		int id = v.getId();
		switch (id) {
		case R.id.button_input_bg_back: //返回按钮
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.silde_out_right);
			this.finish();
			break;
		}
	}
	/**
	 * 
	 * @author Lianxw
	 * 
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ECGChartFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return String.valueOf(position);
		}
	}

	/**
	 * 处理单选按钮点击事件
	 * 
	 * @author Lianxw
	 * 
	 */
	private class OnRadioItemClickListener implements View.OnClickListener {
		private int currentId = R.id.radio0;

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (currentId == id) {
				return;
			} else {
				currentId = id;
			}
			int pos = 0;
			switch (id) {
			case R.id.radio0:
				pos = 0;
				break;
			case R.id.radio1:
				pos = 1;
				break;
			case R.id.radio2:
				pos = 2;
				break;
			case R.id.radio3:
				pos = 3;
				break;

			}
			mViewPager.setCurrentItem(pos);
		}
	}

	private class DownloadEcgTask extends AsyncTask<String, Integer, Integer> {

		private String startTime;
		private String endTime;

		public DownloadEcgTask(String startTime, String endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getResources().getString(R.string.text_wait));
		}

		/**
		 * 成功更新数据 返回0，否则返回1
		 */
		@Override
		protected Integer doInBackground(String... arg0) {
			if (!NetworkTool.isOnline(getBaseContext())) {
				return 1;
			}
			// 取数据库最晚时间
			String checkTime;
			DataECG tempdata = MHealthProviderMetaData.GetMHealthProvider(
					getBaseContext()).getlastEcgData();
			if (tempdata == null || tempdata.data.date == null) {
				checkTime = "2015-01-01 00:00:00";
			} else {
				checkTime = tempdata.data.date;
			}

			ECGListinfo ecglistInfo = new ECGListinfo();
			// 获取数据库最晚时间到当前时间的数据
			SharedPreferences info = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, 0);
			String userUid = info.getString(SharedPreferredKey.USERUID, null);
			int result = DataSyn.getInstance().getECGListData(ecglistInfo,
					startTime, endTime, userUid);

			SimpleDateFormat sfwithsecond = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			List<DataECG> ecgDataList = new ArrayList<DataECG>();
			System.out.println("-----ecglistInfo.datavalue-------"+ecglistInfo.datavalue.size());
			for (ECGSummary ecgSummary : ecglistInfo.datavalue) {
				try {
					DataECG tempEcg = new DataECG();
					long tempsecond = Long.parseLong(ecgSummary.date);
					Date date = new Date(tempsecond);
					ecgSummary.date = sfwithsecond.format(date);
					tempEcg.createtime = ecgSummary.date;
					tempEcg.data = ecgSummary;
					ecgDataList.add(tempEcg);
				} catch (NumberFormatException e) {
					continue;
				}
			}
			// 成功
			if (result == 0) {
				Date _checktime = new Date();
				try {
					_checktime = sfwithsecond.parse(checkTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// 保存新增数据
				System.out.println("--------保存新增数据---------"+ecgDataList.size());
				MHealthProviderMetaData.GetMHealthProvider(getBaseContext())
						.InsertECGData(ecgDataList, _checktime.getTime(), true);
				return 0;
			} else {
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			for(Fragment f:fragments) {
				if(f instanceof ECGChartFragment){
					((ECGChartFragment)f).refresh();
				}
			}
			dismissProgressDialog();
		}
	}


}
