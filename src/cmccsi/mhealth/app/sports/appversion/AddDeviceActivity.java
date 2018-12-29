package cmccsi.mhealth.app.sports.appversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import com.cmcc.bracelet.lsjx.libs.JWDeviceManager;
import com.lidroid.xutils.BitmapUtils;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.DeviceTypeInfo;
import cmccsi.mhealth.app.sports.bean.DeviceTypeListInfo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.BaseDeviceInterface;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

/**
 * 新设备添加类（主要包括添加手机设备、手环设备、计步设备等）
 * @type AddDeviceActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午9:58:52
 */
public class AddDeviceActivity extends BaseActivity implements OnClickListener{

	private final static String TAG = "AddDeviceActivity";
	// Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10 * 1000;
    private static final long BLE_TYPE_PERIOD = 5 * 1000;
    private static final int REQUEST_ENABLE_BT = 10;
	
	private AddDeviceActivity mContext = AddDeviceActivity.this;

	private RelativeLayout mRlDeviceType;
	private LinearLayout mLlDeviceImage;
	private ImageView mIvDeviceImage;
	private EditText mEtDeviceId;
	private TextView mTvDeviceType;
	private TextView mTvDeviceName;
	private TextView mTvDeviceInfo;
	private Button mBtnSearch;

	private Dialog mDialogDeviceType;
	private Dialog mDialogDevice;
	private CustomProgressDialog progressDialog;

	private String mDeviceType = null;
	private String mDeviceId = null;

	private List<String> mBleMacList = null;
	private List<DeviceTypeInfo> mDeviceTypeList = null;
	private ArrayAdapter<String> mDeviceTypeArrayAdapter = null;
	private ArrayAdapter<String> mDeviceArrayAdapter = null;
	private BluetoothAdapter mBleAdapter;
	private String mBleType;
	
	private boolean mScanning = false;
	private boolean mConnecting = false;
	
	private JWDeviceManager mJWDeviceManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_add);

		initViews();
		mDialogDevice = new Dialog(this, R.style.dialog_notfullscreen);
		mDialogDeviceType = new Dialog(this, R.style.dialog_notfullscreen);
		initDialog(mDialogDevice, "可配对设备");
		initDialog(mDialogDeviceType, "选择设备类型");
		
		//初始化丁当手环
		mJWDeviceManager = JWDeviceManager.getInstance(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initViews() {
		BaseBackKey("添加设备", this);

		RelativeLayout rl_add = (RelativeLayout) findViewById(R.id.imageButton_title_add);
		TextView tv_add = (TextView) findViewById(R.id.textview_title_add);
		TextView tv_add_text = (TextView) findViewById(R.id.textview_title_add_text);
		if (Config.ISALONE) {
			rl_add.setVisibility(View.VISIBLE);
			rl_add.setOnClickListener(this);
			tv_add.setVisibility(View.GONE);
			tv_add_text.setVisibility(View.VISIBLE);
			tv_add_text.setText("完成");
		}

		mRlDeviceType = (RelativeLayout) findViewById(R.id.rl_device_add_type);
		mRlDeviceType.setOnClickListener(this);
		mLlDeviceImage = (LinearLayout) findViewById(R.id.ll_device_add_image);
		mIvDeviceImage = (ImageView) findViewById(R.id.iv_device_add_image);
		mEtDeviceId = (EditText) findViewById(R.id.et_device_add_id);
		mTvDeviceName = (TextView) findViewById(R.id.tv_device_add_name);
		mTvDeviceInfo = (TextView) findViewById(R.id.tv_device_add_info);
		mTvDeviceType = (TextView) findViewById(R.id.tv_device_add_type);
		mBtnSearch = (Button) findViewById(R.id.btn_device_add_search);
		mBtnSearch.setOnClickListener(this);
		
		mDeviceTypeArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_type_item);
		mDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_type_item);
		mDeviceTypeList = new ArrayList<DeviceTypeInfo>();
		mBleMacList = new ArrayList<String>();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_device_add_type:
            try {
            	new DownLoadDeviceTypeTask().execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_device_add_search:
            try {
            	showDeviceDialog();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.imageButton_title_add:
            mDeviceId = mEtDeviceId.getText().toString();
            if(StringUtils.isNotBlank(mDeviceType) && StringUtils.isNotBlank(mDeviceId)){
            	try {
            		new AddDeviceTask().execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }else{
            	ToastUtils.showToast(mContext, "设备类型或设备序列号不能为空");
            }
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Logger.i(TAG, "---onActivityResult");

		if(REQUEST_ENABLE_BT == requestCode){
			showDeviceDialog();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 初始化小窗口
	 * 
	 * @param view
	 */
	private void initDialog(Dialog dialog, String title) {
		dialog.setContentView(R.layout.device_type_list);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
		((TextView)dialog.findViewById(R.id.tv_device_type_title)).setText(title);
		// 点击dialog后反应
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

					dialog.dismiss();
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});	
	}

	/**
	 * 设备列表弹窗
	 */
	private void showDeviceTypeDialog(List<DeviceTypeInfo> deviceTypeList) {

		ListView deviceListView = (ListView) mDialogDeviceType.findViewById(R.id.lv_device_type_list);
		deviceListView.setAdapter(mDeviceTypeArrayAdapter);
		deviceListView.setOnItemClickListener(mDeviceTypeClickListener);
		
		mDeviceTypeArrayAdapter.clear();
		if(deviceTypeList.size() > 0){
			for(int i = 0; i < deviceTypeList.size(); i++){
				mDeviceTypeArrayAdapter.add(deviceTypeList.get(i).productName);
			}
		}

		mDialogDeviceType.show();	
	}

	/**
	 * 蓝牙搜索列表
	 */
	private void showDeviceDialog() {
		Logger.i(TAG, "showDeviceDialog");

		ListView deviceListView = (ListView) mDialogDevice.findViewById(R.id.lv_device_type_list);
		deviceListView.setAdapter(mDeviceArrayAdapter);
		deviceListView.setOnItemClickListener(mDeviceClickListener);
		mBleAdapter = BluetoothAdapter.getDefaultAdapter();
		
		mBleMacList.clear();
		mDeviceArrayAdapter.clear();
		if(Build.VERSION.SDK_INT < 18){
			ToastUtils.showToast(mContext, "该设备不支持BLE蓝牙");
			return;
		}
		createLeScanCallback();
		if(mBleAdapter == null || !mBleAdapter.isEnabled()){
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
			// 设置蓝牙可见性，最多300秒  
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);  
			mContext.startActivityForResult(intent, REQUEST_ENABLE_BT);  
			return;
		}

		//扫描ble蓝牙设备
        mScanning = true;
        mHandlerTemp.postDelayed(_ble_scan_timeout, SCAN_PERIOD);
        mBleAdapter.startLeScan(mLeScanCallback);

		mDialogDevice.show();

		//dialog取消后注销蓝牙接收器
		mDialogDevice.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Logger.i(TAG, "---setOnDismissListener");
				
				if(mScanning){
					mBleAdapter.stopLeScan(mLeScanCallback);
				}
				mScanning = false;
				mHandlerTemp.removeCallbacks(_ble_scan_timeout);
			}
		});
	}
	
	private Handler mHandlerTemp = new Handler();
	
	// ble蓝牙扫描超时后操作
	private Runnable _ble_scan_timeout = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mScanning){
				mHandlerTemp.removeCallbacks(_ble_scan_timeout);
        		mScanning = false;
        		mBleAdapter.stopLeScan(mLeScanCallback);
        	}
		}
	};
	//连接手环超时
	private Runnable _bracelet_connect_timeout = new Runnable() {

		@Override
		public void run() {
			if(mConnecting){
				mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);
				Logger.i(TAG, "---_bracelet_connect_timeout");
				ToastUtils.showToast(mContext, "手环数据获取超时，请重试。");
				mEtDeviceId.setText("");
				mConnecting = false;
				close();
				mJWDeviceManager.disConnect();
			}
		}

	};
	//延时获取手环类型（丁当）
	private Runnable _bracelet_get_type = new Runnable() {

		@Override
		public void run() {
			close();
			if (mJWDeviceManager.getDeviceInfo() != null && mJWDeviceManager.getDeviceInfo().equals("1")) {
				mDeviceType = "SMARTPHONE_BT_LS_IW-106";
			} else if (mJWDeviceManager.getDeviceInfo() != null && mJWDeviceManager.getDeviceInfo().equals("2")) {
				mDeviceType = "SMARTPHONE_BT_LS_IW-201";
			}else{
				mEtDeviceId.setText("");
				ToastUtils.showToast(mContext, "获取手环数据失败");
			}
			Logger.i(TAG, "---mDeviceType " + mDeviceType);
			mJWDeviceManager.disConnect();
		}

	};
	// ble蓝牙设备扫描回调方法
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private void createLeScanCallback(){
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
				Logger.i(TAG, "---LeScanCallback:" + device.getName() + " " + device.getAddress());
				
				String device_name = StringUtils.isNotBlank(device.getName()) ? device.getName() : "";
				//Lizhiwei add TODO
//				mBleType = "Air";
				if (device_name.contains(mBleType) && !mBleMacList.contains(device.getAddress())) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mBleMacList.add(device.getAddress());
							mDeviceArrayAdapter.add(device.getName() + " " + device.getAddress());
						}
					});
				}
			}
		};
	}

	//
	private void afterSelectDeviceType(DeviceTypeInfo deviceTypeInfo) {
		if (deviceTypeInfo.isBtDevice.equals("1")) {
			mBtnSearch.setVisibility(View.VISIBLE);
			mEtDeviceId.setEnabled(false);
			mEtDeviceId.setHint("");
			Logger.i(TAG, "---mBleType:" + deviceTypeInfo.btPrefix);
			mBleType = deviceTypeInfo.btPrefix;
		} else {
			mBtnSearch.setVisibility(View.GONE);
			mEtDeviceId.setEnabled(true);
			mEtDeviceId.setHint("请输入设备序列号");
			mEtDeviceId.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		mEtDeviceId.setText("");
		
		if(deviceTypeInfo.productName.equals("智能手机设备") || deviceTypeInfo.productPara.equals("SMARTPHONE_DEVICE")){
			mEtDeviceId.setEnabled(false);
			mEtDeviceId.setText(PreferencesUtils.getString(mContext, SharedPreferredKey.PHONENUM, ""));
		}
		
		mDeviceType = deviceTypeInfo.productPara;
		mTvDeviceType.setText(deviceTypeInfo.productName);
		mLlDeviceImage.setVisibility(View.VISIBLE);
//		ImageUtil.getInstance().loadBitmap(mIvDeviceImage, deviceTypeInfo.productPic);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(mIvDeviceImage, deviceTypeInfo.productPic);
		mTvDeviceName.setText(deviceTypeInfo.productAppTag);
		mTvDeviceInfo.setText(deviceTypeInfo.productDesc);
		
		if(deviceTypeInfo.productName.equals("爱动力计步器") || deviceTypeInfo.productPara.equals("WS-JBQ-001")){
			mLlDeviceImage.setVisibility(View.GONE);
		}
		
		//设备序列号输入框 获取焦点
		mEtDeviceId.setFocusable(true);
		mEtDeviceId.setFocusableInTouchMode(true);
		mEtDeviceId.requestFocus();
	}

	private OnItemClickListener mDeviceTypeClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//			ToastUtils.showToast(mContext, mDeviceTypeList.get(arg2).productName);
			afterSelectDeviceType(mDeviceTypeList.get(arg2));
	
			mDialogDeviceType.dismiss();
		}
	};
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			
			if(mScanning){
				mBleAdapter.stopLeScan(mLeScanCallback);
			}
			mScanning = false;
			mHandlerTemp.removeCallbacks(_ble_scan_timeout);
			
			if(av.getItemAtPosition(arg2).toString().contains("DD")){			
				//初始化丁当手环
				mJWDeviceManager = JWDeviceManager.getInstance(mContext);
				
				mConnecting = true;
				showProgress("获取手环数据");				
				mHandlerTemp.postDelayed(_bracelet_connect_timeout, SCAN_PERIOD);
				mJWDeviceManager.connect(mBleMacList.get(arg2));
				mJWDeviceManager.setCallBack(mBaseCallBack);
			}
			
			mEtDeviceId.setText(mBleMacList.get(arg2));
			mDialogDevice.dismiss();
		}
	};
	/**
	 * 加载设备类型列表
	 */
	private class DownLoadDeviceTypeTask extends AsyncTask<Null, Null, Integer> {

		@Override
		protected Integer doInBackground(Null... params) {
			DeviceTypeListInfo reqData = new DeviceTypeListInfo();
			int result = DataSyn.getInstance().getDeviceTypeList(reqData);

			if (result == 0 && reqData != null) {
				if (reqData.datavalue.size() > 0) {
					mDeviceTypeList = reqData.datavalue;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 0) {
				try {
					showDeviceTypeDialog(mDeviceTypeList);
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}else{
				ToastUtils.showToast(mContext, R.string.MESSAGE_INTERNET_ERROR);
			}
		}
	}
	/**
	 * 添加新设备
	 */
	private class AddDeviceTask extends AsyncTask<Null, Null, BackInfo> {
		
		public AddDeviceTask() {
			showProgress("添加设备...");
		}

		@Override
		protected BackInfo doInBackground(Null... params) {
			BackInfo reqData = new BackInfo();
			int result = DataSyn.getInstance().addDevice(mDeviceId, mDeviceType, reqData);

			if (StringUtils.isNotBlank(reqData.status)) {
				Logger.i(TAG, "---reqData " + reqData.message);
				return reqData;
			}
			return null;
		}

		@Override
		protected void onPostExecute(BackInfo result) {
			super.onPostExecute(result);
			close();
			if(result != null){
				if (result.status.equals("SUCCESS")) {
					ToastUtils.showToast(mContext, "添加成功");
					Intent intent = new Intent();
					intent.putExtra("result", "add");
					mContext.setResult(RESULT_OK, intent);
					mContext.finish();
				}else{
					ToastUtils.showToast(mContext, result.message);
				}
			}else{
				ToastUtils.showToast(mContext, R.string.MESSAGE_INTERNET_ERROR);
			}
		}
	}
	/**
	 * 加载提示
	 */
	private void showProgress(String msg) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = CustomProgressDialog.createDialog(AddDeviceActivity.this);
		progressDialog.setMessage(msg);
		progressDialog.show();
	}
	/**
	 * 关闭加载提示
	 */
	private void close() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 丁当手环回调函数
	 */
	private BaseDeviceInterface.BaseCallBack mBaseCallBack = new BaseDeviceInterface.BaseCallBack(){

		@Override
		public void connected(int code, String msg) {

			mConnecting = false;
			mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);

			if (code == DeviceConstants.CONNECTED_SUCCESS) {
				Logger.i(TAG, "---CONNECTED_SUCCESS");
				mHandlerTemp.postDelayed(_bracelet_get_type, BLE_TYPE_PERIOD);
			} else {
				Logger.i(TAG, "---CONNECTED_FAIL");
				ToastUtils.showToast(mContext, "手环数据获取失败，请重试。");
				mJWDeviceManager.disConnect();
			}
		}

		@Override
		public void disConnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pedoDataPercent(int percent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pedoDataReceived(PedometorListInfo data1, List<PedoDetailInfo> data2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void realTimeDataReceived(PedometorDataInfo data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void realTimeEKGDataReceived(int key, Object data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ekgStop(int result, int finalHR) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ekgDataReceived(int key, Object data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exception(int code, String msg) {
			// TODO Auto-generated method stub
			
		}
		
	};
}
