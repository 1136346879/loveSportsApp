package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.AreaInfo;
import cmccsi.mhealth.app.sports.bean.AreaListInfo;
import cmccsi.mhealth.app.sports.bean.SaveAreaInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.app.sports.R;

/**
 * 区域组织信息设置
 * 
 * @type SettingArea TODO
 * @author shaoting.chen
 * @time 2015年3月2日下午3:13:29
 */

public class SettingArea extends BaseActivity implements OnClickListener, OnItemClickListener {

	private final static int AREA_TYPE_PROVINCE = 0; // 区域数据-省
	private final static int AREA_TYPE_CITY = 1; // 区域数据-市
	private final static int AREA_TYPE_COUNTY = 2; // 区域数据-乡/区

	private final static int SAVE_STATUS_SUCCESS = 3; // 保存成功
	private final static int SAVE_STATUS_FAIL = 4; // 保存失败

	private TextView mTvProvince;
	private TextView mTvCity;
	// private TextView mTextViewCountry;
	// private ImageView mImageViewLine1;
	private ImageView mIvLine2;
	private ListView mLvAreaList;
	private LinearLayout mLl_notice;

	private AreaListInfo mAreaList;
	private Context mContext = SettingArea.this;
	private MyAdapter mAdapter;

	private int mAreaType = AREA_TYPE_PROVINCE;
	private String mOrgId = "";
	private Map<Integer, AreaInfo> mSelectedAreasMap;
	private String userId;
	private String countyId;
	private String oldOrgId;

	private SharedPreferences sp;
	private CommonAskDialog mAskDialog = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case AREA_TYPE_PROVINCE:
				mAdapter = new MyAdapter();
				mAdapter.clearAreaInfoList();
				mAdapter.setAreaInfoList(mAreaList.datavalue);
				mLvAreaList.setAdapter(mAdapter);
				mLl_notice.setVisibility(View.VISIBLE);
				break;
			case AREA_TYPE_CITY:
				mTvProvince.setVisibility(View.VISIBLE);
				mTvProvince.setText(mSelectedAreasMap.get(mAreaType - 1).getOrgName());
				if (mAdapter != null) {
					mAdapter.clearAreaInfoList();
					mAdapter.setAreaInfoList(mAreaList.datavalue);
				}
				mLl_notice.setVisibility(View.GONE);
				break;
			case AREA_TYPE_COUNTY:
				mTvCity.setVisibility(View.VISIBLE);
				mIvLine2.setVisibility(View.VISIBLE);
				mTvCity.setText(mSelectedAreasMap.get(mAreaType - 1).getOrgName());
				if (mAdapter != null) {
					mAdapter.clearAreaInfoList();
					mAdapter.setAreaInfoList(mAreaList.datavalue);
				}
				mLl_notice.setVisibility(View.GONE);
				break;
			case SAVE_STATUS_FAIL:
				ToastUtils.showToast(getApplicationContext(), "保存失败！");
				mAreaType--;
				break;
			case SAVE_STATUS_SUCCESS:
				ToastUtils.showToast(getApplicationContext(), "保存成功！");
				Editor editor = sp.edit();
				editor.putInt(SharedPreferredKey.COUNTY_ID, mSelectedAreasMap.get(2).getOrgId());
				editor.putInt(SharedPreferredKey.CITY_ID, mSelectedAreasMap.get(1).getOrgId());
				editor.putInt(SharedPreferredKey.PROVINCE_ID, mSelectedAreasMap.get(0).getOrgId());
				editor.putString(SharedPreferredKey.COUNTY_NAME, mSelectedAreasMap.get(2).getOrgName());
				editor.putString(SharedPreferredKey.CITY_NAME, mSelectedAreasMap.get(1).getOrgName());
				editor.putString(SharedPreferredKey.PROVINCE_NAME, mSelectedAreasMap.get(0).getOrgName());

				editor.commit();
				dismiss();
				Intent _intent = new Intent();
				_intent.putExtra("result", "SettingArea");
				setResult(RESULT_OK, _intent);
				finish();
				break;
			}
			mAreaType++;
			dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area);

		sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		mAreaList = new AreaListInfo();
		mSelectedAreasMap = new HashMap<Integer, AreaInfo>();
		getAreasByParentId();

		BaseBackKey("设置所属区域", this);
		initViews();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter != null) {
			mAdapter.clearAreaInfoList();
		}
	}

	/**
	 * 加载界面控件
	 */
	private void initViews() {
		mLl_notice = (LinearLayout) findViewById(R.id.ll_notice);
		mTvProvince = (TextView) findViewById(R.id.tv_province);
		mTvProvince.setOnClickListener(this);
		mTvCity = (TextView) findViewById(R.id.tv_city);
		mTvCity.setOnClickListener(this);
		// mTextViewCountry = (TextView) findViewById(R.id.tv_country);
		// mTextViewCountry.setOnClickListener(this);
		// mImageViewLine1 = (ImageView) findViewById(R.id.line_area1);
		mIvLine2 = (ImageView) findViewById(R.id.line_area2);
		mLvAreaList = (ListView) findViewById(R.id.lv_area_list);
		mLvAreaList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 保存选中区域记录
		mSelectedAreasMap.put(mAreaType - 1, mAreaList.datavalue.get(arg2));

		// 判断加载哪一级区域信息
		if ((mAreaType) == AREA_TYPE_CITY) {
			mOrgId = String.valueOf(mAreaList.datavalue.get(arg2).orgId);
			getAreasByParentId();
			// mImageViewLine1.setVisibility(View.VISIBLE);
		} else if ((mAreaType) == AREA_TYPE_COUNTY) {
			mOrgId = String.valueOf(mAreaList.datavalue.get(arg2).orgId);
			getAreasByParentId();

		} else {
			countyId = String.valueOf(mAreaList.datavalue.get(arg2).orgId);
			userId = PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
			oldOrgId = String.valueOf(PreferencesUtils.getInt(this, SharedPreferredKey.COUNTY_ID, 1));
			Logger.i("settingArea", "----" + countyId + " " + oldOrgId);
			if(!countyId.equals(oldOrgId)){
				showAskDialog("修改区域组织后，第二天才可看到您在该区域的排名，是否继续修改？");
			}else{
				ToastUtils.showToast(this, "已存在所选组织信息！");
			}
			
		}

	}
	
	/**
	 * 提示框
	 * 
	 * @param msg 显示信息
	 */
	private void showAskDialog(String msg) {
		if(mAskDialog != null){
			mAskDialog.isHidden();
			mAskDialog.dismiss();
		}
		String[] buttons = { "确定", "", "取消" };
		mAskDialog = CommonAskDialog.create(msg, buttons, false, true);
		mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
			@Override
			public void onClick(int which) {
				if (which == CommonAskDialog.BUTTON_OK) {
					saveAreaInfo();
				}
			}
		});
		mAskDialog.show(getSupportFragmentManager(), "CommonAskDialog");
	}

	/**
	 * 保存区域信息
	 * 
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 上午10:03:51
	 */
	private int saveAreaInfo() {
		showProgressDialog(getResources().getString(R.string.text_wait), this);
		try {
			new Thread() {
				public void run() {
					SaveAreaInfo saveBack = new SaveAreaInfo();
					int back = DataSyn.getInstance().saveAreaInfo(userId, countyId, oldOrgId, saveBack);
					if (back == 0) {
						Message msg6 = new Message();
						msg6.what = 3;
						mHandler.sendMessage(msg6);
					} else {
						Message msg5 = new Message();
						msg5.what = 4;
						mHandler.sendMessage(msg5);
					}
				};
			}.start();
			return 0;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 获取区域列表
	 */
	private int getAreasByParentId() {
		showProgressDialog(getResources().getString(R.string.text_wait), this);
		try {
			new Thread() {
				public void run() {
					if (mAreaList != null) {
						mAreaList.datavalue.clear();
					}
					DataSyn.getInstance().getAreaInfo(mOrgId, mAreaList);
					switch (mAreaType) {
					case AREA_TYPE_CITY:
						Message msg1 = new Message();
						msg1.what = 1;
						mHandler.sendMessage(msg1);
						break;
					case AREA_TYPE_COUNTY:
						Message msg2 = new Message();
						msg2.what = 2;
						mHandler.sendMessage(msg2);
						break;
					case AREA_TYPE_PROVINCE:
						Message msg0 = new Message();
						msg0.what = 0;
						mHandler.sendMessage(msg0);
						break;
					}
				};
			}.start();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_province:
			if (mTvCity.getVisibility() != View.GONE) {
				mAreaType = AREA_TYPE_CITY;
				if (mSelectedAreasMap.containsKey(mAreaType - 1)) {
					mOrgId = String.valueOf(mSelectedAreasMap.get(mAreaType - 1).getOrgId());
					getAreasByParentId();
					// mTextViewProvince.setVisibility(View.GONE);
					mTvCity.setVisibility(View.GONE);
					mIvLine2.setVisibility(View.GONE);
				}
			} else {
				mAreaType = AREA_TYPE_PROVINCE;
				mOrgId = "";
				getAreasByParentId();
				mTvProvince.setVisibility(View.GONE);
			}

			break;
		// case R.id.tv_country:
		// mAreaType = AREA_TYPE_PROVINCE;
		// mOrgId = "";
		// getAreasByParentId();
		// // mTextViewProvince.setVisibility(View.GONE);
		// mTextViewProvince.setVisibility(View.GONE);
		// mImageViewLine1.setVisibility(View.GONE);
		// mTextViewCity.setVisibility(View.GONE);
		// mImageViewLine2.setVisibility(View.GONE);
		// break;
		}
	}

	/**
	 * 
	 * @type MyAdapter TODO
	 * @author shaoting.chen
	 * @time 2015年3月2日下午3:26:11
	 */
	class MyAdapter extends BaseAdapter {

		private ArrayList<AreaInfo> areaInfoList;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return areaInfoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return areaInfoList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_area_item, null);
				holder = new ViewHolder();
				holder.mTextViewArea = (TextView) convertView.findViewById(R.id.tv_area);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mTextViewArea.setText(areaInfoList.get(position).getOrgName());

			return convertView;
		}

		public void setAreaInfoList(ArrayList<AreaInfo> list) {
			if (list != null) {
				areaInfoList = (ArrayList<AreaInfo>) list.clone();
			}

			notifyDataSetChanged();
		}

		public void clearAreaInfoList() {
			if (areaInfoList != null) {
				areaInfoList.clear();
			}
			notifyDataSetChanged();
		}

		class ViewHolder {
			TextView mTextViewArea;
		}
	}
}
