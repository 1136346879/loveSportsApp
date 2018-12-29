package cmccsi.mhealth.app.sports.tabhost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.DataPedometor;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.db.PedometerTableMetaData;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;

public class HistoryListFragment extends BaseFragment implements
		OnClickListener, IXListViewListener {
	private static String TAG = "ListSportsHistoryActivity";

	private MySimpleAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mArrayListRecords;
	private XListView mListViewRecord;

	private TextView mTextViewTitle;
	// private ImageButton mImageButtonTitle;

	private boolean flagUpdateStatus;

	private ImageButton mBack;

	private String mDeviceId;

	public HistoryListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.e(TAG, getClass().getSimpleName() + "onCreateView");
		View view = inflater.inflate(R.layout.activity_list_sports_history,
				container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void findViews() {
		mDeviceId = PreferencesUtils.getString(getActivity(),
				SharedPreferredKey.DEVICE_ID, "");
		initViews();
	}

	@Override
	public void clickListner() {

	}

	@Override
	public void loadLogic() {

	}

	@SuppressWarnings("deprecation")
	void initViews() {
		// showmenu
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setOnClickListener(this);
		// title
		mTextViewTitle = (TextView) findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.his_title);
		/*
		 * mImageButtonTitle = (ImageButton) findView(R.id.imageButton_title);
		 * mImageButtonTitle.setVisibility(View.VISIBLE);
		 * mImageButtonTitle.setBackgroundResource(R.drawable.his_list_detail);
		 * mImageButtonTitle.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if
		 * (event.getAction() == MotionEvent.ACTION_DOWN) { // 更改为按下时的背景图片
		 * v.setBackgroundResource(R.drawable.his_list_detail_on); } else if
		 * (event.getAction() == MotionEvent.ACTION_UP) { // 改为抬起时的图片
		 * v.setBackgroundResource(R.drawable.his_list_detail); } return false;
		 * } }); mImageButtonTitle.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // showDialog(DATE_ID);
		 * Intent intent = new Intent(); intent.setClass(mActivity,
		 * MyCalendarActivity.class); startActivity(intent);
		 * mActivity.overridePendingTransition(R.anim.slide_in_right,
		 * R.anim.silde_out_left); } });
		 */

		mArrayListRecords = getPedometerData();
		// 初始化数据
		mAdapter = new MySimpleAdapter(mActivity);
		mListViewRecord = (XListView) findView(R.id.list_sports_history_input);
		mListViewRecord.setXListViewListener(this);
		mListViewRecord.setPullLoadEnable(false);
		mListViewRecord.setAdapter(mAdapter);
		// mListViewRecord.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Intent intent = new Intent();
		// intent.setClass(mActivity, HistoryPedometorDetailActivity.class);
		// if (position < 1)
		// return;
		// int his_id = Integer.parseInt(mArrayListRecords
		// .get(position - 1).get("id").toString());
		// String date = mArrayListRecords.get(position - 1)
		// .get(PedometerTableMetaData.DATE).toString();
		// String[] str = date.split("-");
		// date = str[0] + str[1] + str[2];
		// date = date.substring(0, 8);
		// intent.putExtra("his_id", his_id);
		// intent.putExtra("searchDate", date);
		// startActivity(intent);
		// mActivity.overridePendingTransition(R.anim.slide_in_right,
		// R.anim.silde_out_left);
		// }
		// });
		mListViewRecord.startLoading(Common.getDensity(mActivity) * 60);
	}

	@Override
	public void onRefresh() {
		new AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>>() {
			@Override
			protected ArrayList<HashMap<String, Object>> doInBackground(Void... params) {
				updateData();
				return getPedometerData();
			}

			@Override
			protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
				if (!flagUpdateStatus) {
					ToastUtils.showToast(getActivity(), R.string.MESSAGE_INTERNET_ERROR);
				}
				resetXListView();
				mArrayListRecords = result;
				mAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}

	@Override
	public void onLoadMore() {
	}

	private void resetXListView() {
		mListViewRecord.stopRefresh();
		mListViewRecord.stopLoadMore();
		mListViewRecord
				.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	private void updateData() {

		try {
			// 取当前数据库最后一条
			PedometorDataInfo data = PedoController.GetPedoController(
					getActivity()).getLatestPedometer(mDeviceId);
			String startDate = "";
			String endDate = DateFormatUtils.DateToString(new Date(),
					FormatType.DateShot);
			if(data!=null)
			{
				startDate=DateFormatUtils.ChangeFormat(data.date, FormatType.DateLong,FormatType.DateShot);
			}else{
				startDate = DateFormatUtils.AddDays(endDate, -15,
						FormatType.DateShot);
			}

			PedometorListInfo reqData = new PedometorListInfo();
			int result = DataSyn.getInstance().getPedoInfoByTimeArea(null,
					null, startDate, endDate, reqData);
			if (result == 0) {
				PedoController.GetPedoController(getActivity())
						.insertOrUpdatePedometer(reqData);
				flagUpdateStatus = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 显示列表
	class MySimpleAdapter extends BaseAdapter {
		private final LayoutInflater mInflater;

		public MySimpleAdapter(Context c) {
			mInflater = LayoutInflater.from(c);
		}

		public void setVisible() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return null == mArrayListRecords ? 0 : mArrayListRecords.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		ViewHolder holder;

		// 这个方法在Activity载入时会调用两次?,而且是列表有几个项就循环几次.
		// 而convertView只有在Activity初始化时才会是空.
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mArrayListRecords == null
					|| mArrayListRecords.size() < (position + 1)) {
				return convertView;
			}

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_his, null);
				holder = new ViewHolder();

				holder.item_cal = (TextView) convertView
						.findViewById(R.id.history_item_calorie);
				holder.item_date = (TextView) convertView
						.findViewById(R.id.history_item_date);
				holder.item_stepnum = (TextView) convertView
						.findViewById(R.id.history_item_stepnum);
				holder.mItemDayDivide = (TextView) convertView
						.findViewById(R.id.textView_day_divide);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			convertView.setClickable(true);
			holder.item_date.setTextColor(Color.parseColor("#a2cd5a"));
			holder.item_cal.setTextColor(Color.parseColor("#a2cd5a"));
			holder.item_stepnum.setTextColor(Color.parseColor("#a2cd5a"));

			// 设置自动手动上传颜色
			String transType = mArrayListRecords.get(position)
					.get(PedometerTableMetaData.TRANS_TYPE).toString();
			if (transType.equals("1")) {
				holder.item_date.setTextColor(Color.parseColor("#a2cd5a"));
				holder.item_cal.setTextColor(Color.parseColor("#a2cd5a"));
				holder.item_stepnum.setTextColor(Color.parseColor("#a2cd5a"));
			} else {
				holder.item_date.setTextColor(Color.parseColor("#a2cd5a"));
				holder.item_cal.setTextColor(Color.parseColor("#a2cd5a"));
				holder.item_stepnum.setTextColor(Color.parseColor("#a2cd5a"));
			}

			String item_date_str = mArrayListRecords.get(position)
					.get(PedometerTableMetaData.DATE).toString();
			// Log.i(TAG, "postion:" + position);
			// 按照时间分隔
			SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd"); // 为了显示
			SimpleDateFormat df_HHmm = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df_yyyyMMddHHmmss = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				// Log.d("yd", item_date_str + " item_date_str");
				Date item_date = df_yyyyMMddHHmmss.parse(item_date_str);
				String item_date_yyyyMMdd = df_yyyyMMdd.format(item_date);
				String item_date_mmss = df_HHmm.format(item_date);

				holder.item_date.setText(item_date_mmss);

				Date today_date = new Date();
				long today_time = today_date.getTime();
				String today_date_yyyyMMdd = df_yyyyMMdd.format(today_date);

				long yesterday_time = today_time - 1000L * 60 * 60 * 24L;// 计算昨天
				Date yesterday_date = new Date(yesterday_time);
				String yesterday_date_yyyyMMdd = df_yyyyMMdd
						.format(yesterday_date);
				
				Logger.i(TAG, "---item_date_yyyyMMdd " + item_date_yyyyMMdd);
				Logger.i(TAG, "---today_date_yyyyMMdd " + today_date_yyyyMMdd);
				Logger.i(TAG, "---yesterday_date_yyyyMMdd " + yesterday_date_yyyyMMdd);
				
				if (position == 0) {

					if (item_date_yyyyMMdd.equals(today_date_yyyyMMdd)) {
						holder.mItemDayDivide.setText("今天");
						convertView.setClickable(false);
					} else if (item_date_yyyyMMdd
							.equals(yesterday_date_yyyyMMdd)) {
						holder.mItemDayDivide.setText("昨天");
						convertView.setClickable(false);
					} else {
						holder.mItemDayDivide.setText(item_date_yyyyMMdd);
						convertView.setClickable(false);
					}
				} else {
					// 和上一天比较
					String before_item_date_str = mArrayListRecords
							.get(position - 1).get(PedometerTableMetaData.DATE)
							.toString();
					Date before_item_date = df_yyyyMMddHHmmss
							.parse(before_item_date_str);
					String before_item_date_yyyyMMdd = df_yyyyMMdd
							.format(before_item_date);
					// 比较日期不一样
					if (!before_item_date_yyyyMMdd.equals(item_date_yyyyMMdd)) {
						if (item_date_yyyyMMdd
								.equals(today_date_yyyyMMdd)) {
							holder.mItemDayDivide.setText("昨天");
							convertView.setClickable(false);
						} else {
							holder.mItemDayDivide.setText(item_date_yyyyMMdd);
							convertView.setClickable(false);
						}
					}
				}
				// holder.mItemLayoutList.setClickable(false);
				// //
				// holder.mItemDayDivide.setBackgroundDrawable(getResources().getDrawable(R.drawable.linearlayout_history_bgs));
				// holder.mItemLayoutTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.linearlayout_history_bgs));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String cal_str = mArrayListRecords.get(position)
					.get(PedometerTableMetaData.ENERGY_CONSUMPTION).toString();
			float cal_fl = Float.valueOf(cal_str);

			holder.item_cal.setText((int) cal_fl + "");
			holder.item_stepnum.setText(mArrayListRecords.get(position)
					.get(PedometerTableMetaData.STEP_NUM).toString());

			return convertView;
		}

	}

	static class ViewHolder {
		TextView item_date, item_cal, item_stepnum; // bg_item_type
		TextView mItemDayDivide;
		ImageView itemIcon;
	}

	// 显示列表
	private ArrayList<HashMap<String, Object>> getPedometerData() {
		if (null == mDeviceId) {
			return null;
		}
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		Date endDate=new Date();
		Date startDate=DateFormatUtils.AddDays(endDate, -15);
		ArrayList<PedometorDataInfo> pedolist = PedoController
				.GetPedoController(getActivity()).getAllPeriodPedometer(startDate);
		// Cursor cursor = MHealthProviderMetaData.GetMHealthProvider(mActivity)
		// .GetPedometerData();\
		if (null == pedolist) {
			return null;
		}
		for (PedometorDataInfo pedometorDataInfo : pedolist) {
			HashMap<String, Object> record = new HashMap<String, Object>();
			// String
			// pedoDate=DateFormatUtils.ChangeFormat(pedometorDataInfo.date,
			// FormatType.DateWithUnderline, FormatType.DateWithDiagonal);
			record.put(PedometerTableMetaData.DATE,
					pedometorDataInfo.createtime);
			record.put(PedometerTableMetaData.ENERGY_CONSUMPTION,
					pedometorDataInfo.cal);
			record.put(PedometerTableMetaData.STEP_NUM,
					pedometorDataInfo.stepNum);
			record.put(PedometerTableMetaData.TRANS_TYPE,
					pedometorDataInfo.transType);

			list.add(record);
		}

		return list;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			getActivity().finish();
			break;

		default:
			break;
		}
	}
}
