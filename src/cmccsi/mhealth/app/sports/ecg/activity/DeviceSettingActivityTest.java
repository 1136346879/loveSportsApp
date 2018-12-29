package cmccsi.mhealth.app.sports.ecg.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.FirmwareDownloadProgressActivity;
import cmccsi.mhealth.app.sports.activity.FirmwareUpdateProgressActivity;
import cmccsi.mhealth.app.sports.appversion.AddDeviceActivity;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.device.DeviceManagerService;
import cmccsi.mhealth.app.sports.ecg.adapter.DeviceListAdapter;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.app.sports.R;

/**
 * 设备列表
 * @type DeviceSettingActivityTest
 * TODO
 * @author shaoting.chen
 * @time 2015年5月5日上午10:55:15
 */
public class DeviceSettingActivityTest extends BaseActivity implements OnClickListener {

	private static final String TAG = "DeviceSettingActivityTest";
	public final static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	private static final int REQUEST_ENABLE_BT = 8001;
	private final static int REQUEST_ENABLE_DEVICE_ADD = 2;
	private final static int REQUEST_ENABLE_FD_DOWNLOAD = 3;
	private final static int REQUEST_ENABLE_FD_UPDATE = 4;
	
	private static final int NET_PROBLEM = 30; // 网络问题
	
	private List<String> mData = new ArrayList<String>(); //设备名称
	private DeviceListAdapter mDeviceListAdapter;
	private ListView mLvDeviceList;
	private DeviceListInfo mDeviceListInfo; //设备列表
	
	private DeviceInfo mOldDevice = null; //切换之前的设备
	private DeviceInfo mSelectedDevice = null; //当前激活设备
	private DeviceInfo mTempDevice; //正在切换的设备
	
	private TextView title;
	private LinearLayout mLlNotice; //无设备时显示信息

	private CustomProgressDialog mProgressDialog;
	private CommonAskDialog mAskDialog = null;
	private Dialog mDialog;

	private Context mContext = this;
	private DeviceManagerService mDeviceManagerService = null;
	private boolean isSwitch = false; //是否在切换设备
	
	
	public boolean isUpdate = false;
	public int updateCount = 1;
	private String mUpdateUrl;
	private String mDeviceVersion;
	private String mDeviceId;

	private Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DeviceConstants.CONNECTED_SUCCESS:
				closeProgress();
				break;
			case DeviceConstants.CONNECTED_FAIL:
				closeProgress();
				ToastUtils.showToast(mContext, "连接失败");
				break;
			case DeviceConstants.DEVCIE_SWITCH_SUCCESS:
				Logger.i(TAG, "----切换成功");
				isSwitch = false;
				closeProgress();
				ToastUtils.showToast(mContext, "切换成功");
				
				if(Common.getDeviceType(mTempDevice.deviceSerial, mTempDevice.productPara)==DeviceConstants.DEVICE_MOBILE_STEP){
					mDeviceManagerService.connect(mSelectedDevice.deviceSerial);
				}
				mOldDevice = mSelectedDevice;
				mSelectedDevice = mTempDevice;
				setSelectedItem();
				break;
			case DeviceConstants.DEVCIE_SWITCH_FAIL:
				Logger.i(TAG, "----切换失败");
				isSwitch = false;
				closeProgress();
				ToastUtils.showToast(mContext, "切换失败");
				break;
			case NET_PROBLEM: //加载设备列表时网络异常
				ToastUtils.showToast(mContext, R.string.MESSAGE_INTERNET_ERROR);
				break;
			default:
				break;
			}
		};
	};
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.i(TAG, "---onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logger.i(TAG, "---onServiceConnected");
			// 返回一个MsgService对象
			mDeviceManagerService = ((DeviceManagerService.DeviceBind) service).getService(mbleAlertHandler);

			// 当前设备如果是手环，检查蓝牙是否开启
//			if (!PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, "").isEmpty()
//					&& PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, "9999").substring(0, 2).equals("01")) {
//				if(Build.VERSION.SDK_INT >= 18){
//					if(checkBlueEnabled()){
//						//连接当前设备
//						mDeviceManagerService.connect(PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, ""));
//					}
//				}else{
//					ToastUtils.showToast(mContext, "您的Android系统版本过低，暂不支持蓝牙手环");
//				}
//				
//			}else{
//				//连接当前设备
//				mDeviceManagerService.connect(PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, ""));
//			}	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_setting);

		Logger.i(TAG, "---onCreate");
		
		initViews();
		// 初始化设备列表数据
		initList();

		new LoadDeviceListTask(getResources().getString(R.string.text_wait)).execute();
		
		//开启设备管理服务
		Intent _intent = new Intent(this, DeviceManagerService.class);
		startService(_intent);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.i(TAG, "---onResume");
		//绑定Service  
		Intent _intent = new Intent(this, DeviceManagerService.class);
		bindService(_intent, conn, Context.BIND_AUTO_CREATE);
		
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Logger.i(TAG, "---onStop");
	}

	/**
	 * 初始化界面控件 TODO
	 * 
	 * @return void
	 * @author shaoting.chen
	 * @time 下午4:35:33
	 */
	private void initViews() {
		// iv_deviceDrawable = (ImageView) findViewById(R.id.iv_deviceDrawable);
		mLvDeviceList = (ListView) findViewById(R.id.list_device);

		// 返回按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.button_input_bg_back);
		backButton.setBackgroundResource(R.drawable.my_button_back);
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		// 标题
		title = (TextView) findViewById(R.id.textView_title);
		title.setText("设备管理");
		RelativeLayout rl_add = (RelativeLayout) findViewById(R.id.imageButton_title_add);	
		TextView tv_add = (TextView) findViewById(R.id.textview_title_add);	
		TextView tv_add_text = (TextView) findViewById(R.id.textview_title_add_text);
		TextView tv_device_empty = (TextView) findViewById(R.id.tv_device_setting_text_empty);
		tv_device_empty.setText(R.string.text_device_setting_empty);
		if(Config.ISALONE){
			rl_add.setVisibility(View.VISIBLE);
			rl_add.setOnClickListener(this);
			tv_add.setVisibility(View.GONE);
			tv_add_text.setVisibility(View.VISIBLE);
			tv_add_text.setText("添加");
			tv_device_empty.setText(R.string.text_device_setting_empty_alone);
		}
		
		mLlNotice = (LinearLayout) findViewById(R.id.ll_device_notice);

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	@Override
	public void onDestroy() {
		mDeviceManagerService = null;
		unbindService(conn);
		super.onDestroy();
		Logger.i(TAG, "---onDestroy");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_input_bg_back: // 返回按钮
	
			DeviceSettingActivityTest.this.finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			break;
		case R.id.imageButton_title_add:
			Intent intent = new Intent(this, AddDeviceActivity.class);
			startActivityForResult(intent, REQUEST_ENABLE_DEVICE_ADD);
			break;
		}
	}

	// 初始化列表数据
	private void initList() {
		mDeviceListAdapter = new DeviceListAdapter(DeviceSettingActivityTest.this, mData);
		mLvDeviceList.setAdapter(mDeviceListAdapter);
		mLvDeviceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mTempDevice = mDeviceListInfo.datavalue.get(position);
				//判断当前设备是否为所选设备
				if (mTempDevice.deviceSerial.equals(mSelectedDevice.deviceSerial)) {
					return;
				}

				String msg = "";	
				int oldDeviceType=Common.getDeviceType(mSelectedDevice.deviceSerial, mSelectedDevice.productPara);
				int selectDeviceType=Common.getDeviceType(mTempDevice.deviceSerial, mTempDevice.productPara);
				String msg_device_type=mDeviceListAdapter.getItem(position);
				if (oldDeviceType == DeviceConstants.DEVICE_PEDOMETER) {
					msg="请手动上传您计步器的运动数据，切换后，运动界面将显示" + msg_device_type
							+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？";
				} else if (selectDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
						|| selectDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
						|| selectDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
					if (Build.VERSION.SDK_INT < 18) {
						Toast.makeText(DeviceSettingActivityTest.this, "您的Android系统版本过低，暂不支持配对该蓝牙手环。",
								Toast.LENGTH_SHORT).show();
						return;
					}
					msg="切换后，运动界面将显示" + msg_device_type
							+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？";
				} else {
					msg="切换后，运动界面将显示" + msg_device_type
							+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？";
				}
				if(!msg.equals("")){
					showAskDialog(msg);
				}
			}
		});
		if(Config.ISALONE){
			mLvDeviceList.setOnItemLongClickListener(new OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	            	if (mSelectedDevice.deviceSerial.equals(mDeviceListInfo.datavalue.get(position).deviceSerial)) {
	            		showDeleteInfoDialog("提示", "您的设备处于激活状态，为了防止数据异常，请先切换或者解绑设备后再进行删除操作。");
	        		} else {
	                    showDeleteAlertDialog(position);
	        		}
	                return true;
	            }
	        });
		}
	}

	/**
	 * 切换设备弹窗
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
					isSwitch = true; //正在切换设备
					Logger.i(TAG, "---dtype " + mTempDevice.deviceSerial.substring(0, 2));
					// 当前设备如果是手环，检查蓝牙是否开启
					if (mTempDevice.deviceSerial.substring(0, 2).equals("01")) {
						if(Build.VERSION.SDK_INT >= 18){
//							if(checkBlueEnabled()){
								showProgress("正在切换设备...");
								if(Common.getDeviceType(mSelectedDevice.deviceSerial, mSelectedDevice.productPara)==DeviceConstants.DEVICE_MOBILE_STEP){
									mDeviceManagerService.disConnect();					
								}
								mDeviceManagerService.switchDevice(mSelectedDevice, mTempDevice);
//							}
						}else{
							ToastUtils.showToast(mContext, "您的Android系统版本过低，暂不支持蓝牙手环");
						}
						return;
					}
					
					showProgress("正在切换设备...");
					if(Common.getDeviceType(mSelectedDevice.deviceSerial, mSelectedDevice.productPara)==DeviceConstants.DEVICE_MOBILE_STEP){
						mDeviceManagerService.disConnect();					
					}
					mDeviceManagerService.switchDevice(mSelectedDevice, mTempDevice);
				}
			}
		});
		mAskDialog.show(getSupportFragmentManager(), "CommonAskDialog");
	}
	
	private void showUpdateDialog(String msg) {
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
					Intent intent = new Intent();
					intent.putExtra("downloadsite",
							mUpdateUrl);
					intent.setClass(mContext, FirmwareDownloadProgressActivity.class);
					startActivityForResult(intent, REQUEST_ENABLE_FD_DOWNLOAD);
				}
			}
		});
		mAskDialog.show(getSupportFragmentManager(), "CommonAskDialog");
	}
	
	private void showDeleteInfoDialog(String dialogHeadText, String dialogContent) {
		mDialog = new Dialog(mContext, R.style.dialog_withStatusBar);
		mDialog.setContentView(R.layout.dialog_delete);
		((TextView) mDialog.findViewById(R.id.Introduction_delete)).setText(dialogHeadText);
		((TextView) mDialog.findViewById(R.id.Introduction_delete_detail)).setText(dialogContent);
		mDialog.findViewById(R.id.btn_dialog_delete_yes).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
		mDialog.show();
	}
    /**
     * 长按删除设备提示框
     */
	private void showDeleteAlertDialog(final int position) {
		String dialogHeadText = "提示";
		String dialogContent = "您确定要删除该设备吗？";

		mDialog = new Dialog(mContext, R.style.dialog_withStatusBar);
		mDialog.setContentView(R.layout.dialog_delete);
		((TextView) mDialog.findViewById(R.id.Introduction_delete)).setText(dialogHeadText);
		((TextView) mDialog.findViewById(R.id.Introduction_delete_detail)).setText(dialogContent);
		mDialog.findViewById(R.id.btn_dialog_delete_yes).setVisibility(View.GONE);
		mDialog.findViewById(R.id.ll_dialog_delete_buttons).setVisibility(View.VISIBLE);
		
		mDialog.findViewById(R.id.btn_dialog_delete_sure).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteDevice(position);
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
		mDialog.findViewById(R.id.btn_dialog_delete_cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
		mDialog.show();
	}
	
	 /**
     * 删除设备方法
     */
    private void deleteDevice(int position){	
		try {
			new DeleteDeviceTask().execute(mDeviceListInfo.datavalue.get(position).deviceSerial,
					mDeviceListInfo.datavalue.get(position).productPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	 * 设置选中项目
	 */
	private void setSelectedItem() {

		int position_selected = mDeviceListInfo.datavalue.indexOf(mSelectedDevice);
		int position_old = mDeviceListInfo.datavalue.indexOf(mOldDevice);
		mDeviceListInfo.datavalue.get(position_old).isUsed = "0";
		mDeviceListInfo.datavalue.remove(position_selected);
		mSelectedDevice.isUsed = "1";
		mDeviceListInfo.datavalue.add(0, mSelectedDevice);
		
        mData.remove(position_selected);
		if (mSelectedDevice.deviceSerial.substring(0, 2).equals("00"))// 手机
		{
			mData.add(0, mSelectedDevice.productName + " " + android.os.Build.MODEL);
		} else {
			mData.add(0, mSelectedDevice.productName + " " + mSelectedDevice.deviceSerial);
		}
        
		mDeviceListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				//是否切换设备
				if(isSwitch){
					showProgress("正在切换设备...");
					if(Common.getDeviceType(mSelectedDevice.deviceSerial, mSelectedDevice.productPara)==DeviceConstants.DEVICE_MOBILE_STEP){
						mDeviceManagerService.disConnect();					
					}
					mDeviceManagerService.switchDevice(mSelectedDevice, mTempDevice);
				}else{
					//连接当前设备
//					mDeviceManagerService.connect(PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, ""));
				}
			}
			return;
		}else if(requestCode == REQUEST_ENABLE_DEVICE_ADD){
			if (resultCode == RESULT_OK && data.getExtras().getString("result").equals("add")) {
				new LoadDeviceListTask("加载设备列表...").execute();
			}			
		}else if(requestCode == REQUEST_ENABLE_FD_DOWNLOAD){
			if(resultCode == RESULT_OK && data.getExtras().getString("result").equals("download")){
				updateFirmware(mDeviceId);
			}
		}else if(requestCode == REQUEST_ENABLE_FD_UPDATE){
			if(resultCode == RESULT_OK && data.getExtras().getString("result").equals("update")){
				new LoadDeviceListTask("加载设备列表...").execute();
			}
		}
		super.onActivityResult(requestCode, requestCode, data);
	}
	
	public void downloadFirmware(String updateUrl, String deviceVersion, String deviceId){
		mUpdateUrl = updateUrl;
		mDeviceVersion = deviceVersion;
		mDeviceId = deviceId;
		showUpdateDialog("固件升级会清空手环内的数据，请先同步数据" + "\n\n" + "是否立即更新固件？");
	}
	
	private void updateFirmware(String deviceId){
//		showProgress("固件下载完成，连接手环设备");
//		connectDevice(deviceId);
		Intent intent = new Intent();
		intent.putExtra("fileurl", Environment.getExternalStorageDirectory().toString() + "/adlDownload");
		intent.putExtra("updateurl", mUpdateUrl);
		intent.putExtra("deviceversion", mDeviceVersion);
		intent.putExtra("deviceveId", mDeviceId);
		intent.setClass(mContext, FirmwareUpdateProgressActivity.class);
		startActivityForResult(intent, REQUEST_ENABLE_FD_UPDATE);
	}
	private void connectDevice(String deviceId){
		// 当前设备如果是手环，检查蓝牙是否开启
		if (!deviceId.isEmpty()
				&& deviceId.substring(0, 2).equals("01")) {
			if(Build.VERSION.SDK_INT >= 18){
				if(checkBlueEnabled()){
					//连接当前设备
					mDeviceManagerService.connect(deviceId);
				}
			}else{
				ToastUtils.showToast(mContext, "您的Android系统版本过低，暂不支持蓝牙手环");
			}
			
		}else{
			//连接当前设备
			mDeviceManagerService.connect(deviceId);
		}	
	}

	/**
	 * 加载提示弹窗
	 */
	private void showProgress(String msg) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = CustomProgressDialog.createDialog(DeviceSettingActivityTest.this);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	/**
	 * 关闭加载提示
	 */
	private void closeProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	
	/**
	 * 判断蓝牙是否开启
	 * @return boolean 
	 */
	private boolean checkBlueEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 从网络加载设备列表
	 */
	private class LoadDeviceListTask extends AsyncTask<Null, Null, Integer> {

		public LoadDeviceListTask(String msg) {
			showProgress(msg);
			}

		@Override
		protected Integer doInBackground(Null... params) {
			if (!NetworkTool.isOnline(DeviceSettingActivityTest.this)) {
				return -1;
			}

			mDeviceListInfo = DeviceListInfo.getInstance();
			int res = DataSyn.getInstance().getDeviceListData(mDeviceListInfo);
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 0) {
				if (mDeviceListInfo.datavalue.size() > 0) {
					mData.clear();
					boolean hasECG=false;
					for (int i = 0; i < mDeviceListInfo.datavalue.size(); i++) {
						int type = Common.getDeviceType(mDeviceListInfo.datavalue.get(i).deviceSerial, mDeviceListInfo.datavalue.get(i).productPara);
						if (type  == DeviceConstants.DEVICE_MOBILE_STEP)// 手机
						{
							mData.add(mDeviceListInfo.datavalue.get(i).productName + " " + android.os.Build.MODEL);
						} else {
							mData.add(mDeviceListInfo.datavalue.get(i).productName + " " + mDeviceListInfo.datavalue.get(i).deviceSerial);
						}
						if (mDeviceListInfo.datavalue.get(i).isUsed.equals("1")) {
                          
							mDeviceListAdapter.setFirstSelectPosition(i);
							mSelectedDevice = mDeviceListInfo.datavalue.get(i);
							Logger.i(TAG, "---mSelectedDevice " + mSelectedDevice.deviceSerial + " " + mSelectedDevice.productPara);
						}
						if(type==DeviceConstants.DEVICE_BRACLETE_BEATBAND){
							hasECG=true;
						}
					}
					PreferencesUtils.putBoolean(DeviceSettingActivityTest.this, SharedPreferredKey.HAVE_BRACELET_DEVICE, hasECG);
					if(mSelectedDevice == null){
						mSelectedDevice = new DeviceInfo();
						mSelectedDevice.deviceSerial = "noDevice";
						mSelectedDevice.productPara = "noDevice";
			        }

					mDeviceListAdapter.setDeviceListInfo(mDeviceListInfo, mData);
					mDeviceListAdapter.notifyDataSetChanged();

				} else {
					mLlNotice.setVisibility(View.VISIBLE);
				}
			}else if(result == -1){
			    mbleAlertHandler.sendEmptyMessage(NET_PROBLEM);
			}
			closeProgress();
		}
	}
	
	/**
	 * 删除设备
	 */
	private class DeleteDeviceTask extends AsyncTask<String, Null, BackInfo> {
		
		public DeleteDeviceTask() {
			showProgress("删除设备...");
		}

		@Override
		protected BackInfo doInBackground(String... params) {
			BackInfo reqData = new BackInfo();
			int result = DataSyn.getInstance().deleteDevice(params[0], params[1], reqData);

			if (StringUtils.isNotBlank(reqData.status)) {
				return reqData;
			}
			return null;
		}

		@Override
		protected void onPostExecute(BackInfo result) {
			super.onPostExecute(result);
			closeProgress();
			if(result != null){
				if (result.status.equals("SUCCESS")) {
					ToastUtils.showToast(mContext, "删除成功");
					new LoadDeviceListTask("加载设备列表...").execute();
				}else{
					ToastUtils.showToast(mContext, result.message);
				}
			}else{
				ToastUtils.showToast(mContext, R.string.MESSAGE_INTERNET_ERROR);
			}
		}
	}
	
}
