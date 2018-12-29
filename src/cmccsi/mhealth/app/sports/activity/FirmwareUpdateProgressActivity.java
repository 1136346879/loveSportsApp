package cmccsi.mhealth.app.sports.activity;

import com.neurosky.ble.TGBleManager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.SaveDeviceToken;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.pedo.TgbleManagerNeuro;
import cmccsi.mhealth.app.sports.view.DownLoadApkProgress;
import cmccsi.mhealth.app.sports.R;

/**
 * 固件上传到手环功能类（固件升级）
 * @type FirmwareUpdateProgressActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午10:06:26
 */
public class FirmwareUpdateProgressActivity extends BaseActivity {
	
	private static final String TAG = "FirmwareUpdateProgressActivity";
	private static final int REQUEST_ENABLE_BT = 8001;
	
	private String mStrURL = "";
	private String mFileNa = "";
	private String mFilePath = "";
	private DownLoadApkProgress mMyProgress = null;
	private TextView mTvMessage;
	private TextView mTvPrcent;

	private FirmwareUpdateProgressActivity mFirmwareUpdateProgressActivity = this;
	private String mDeviceVersion;
	private String mDeviceveId;
	
	private TgbleManagerNeuro tgBleManager = null;

	Handler mHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
			switch (msg.what) {
			case TGBleManager.MSG_FW_TRANSFER_REPORT:

				String toastStr = "";
				boolean successed = false;
				switch (Integer.valueOf(msg.arg1)) {
				case 0:
					ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.firmwareuploadprogressactivity_uploadsuccess));
					updateToken();
					successed = true;
					break;
				case 1:
					toastStr = getResources().getString(R.string.firmwareuploadprogressactivity_uploadfalse);
					successed = false;
					break;
				case 2:
					toastStr = getResources().getString(R.string.firmwareuploadprogressactivity_connectdismiss);
					successed = false;
					break;
				case 3:
					toastStr = getResources().getString(R.string.firmwareuploadprogressactivity_nofindfloder);
					successed = false;
					break;
				case 4:
					toastStr = getResources().getString(R.string.firmwareuploadprogressactivity_lowbuttery);
					successed = false;
					break;
				case 5:
					toastStr =getResources().getString(R.string.firmwareuploadprogressactivity_uploadfalse) ;
					successed = false;
					break;
				case 6:
					toastStr = getResources().getString(R.string.firmwareuploadprogressactivity_uploadfalse);
					successed = false;
					break;
				case 7:
					toastStr =getResources().getString(R.string.firmwareuploadprogressactivity_timeout);
					successed = false;
					break;
				case 8:
					toastStr =getResources().getString(R.string.firmwareuploadprogressactivity_flodererror);
					successed = false;
					break;
				}
				if(!successed){
					ToastUtils.showToast(getApplicationContext(), toastStr);
					finishSelf(false);
				}	
				break;
			case TGBleManager.MSG_FW_TRANSFER_PERCENT:
				Logger.i(TAG, "--- PED MSG_FW_TRANSFER_PERCENT: " + msg.arg1);
				Integer progress_value = Integer.valueOf(msg.arg1);
				if (progress_value >= 100) {
					mMyProgress.setProgress(100);
					mTvPrcent.setText("100%");
				} else {
					mMyProgress.setProgress(progress_value);
					mTvPrcent.setText(progress_value + "%");
				}

				break;
			case TgbleManagerNeuro.MSG_CONNECTED: // 神念手环连接成功
				mHandlerTemp.removeCallbacks(_connect_timeout);
				mTvMessage.setText(getResources().getString(R.string.firmwareuploadprogressactivity_progressing));
				mMyProgress.setMax(100);
				mTvPrcent.setText("0%");
				checkUpdate(mDeviceVersion);
				break;
			case TgbleManagerNeuro.MSG_EXCEPT: //神念手环连接失败
				mHandlerTemp.removeCallbacks(_connect_timeout);
				break;
			}

			super.handleMessage(msg);
		};
	};
	// 连接设备超时
	private Runnable _connect_timeout = new Runnable() {
		@Override
		public void run() {
			mHandlerTemp.removeCallbacks(_connect_timeout);
			Logger.i(TAG, "---连接设备超时");
			ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.firmwareuploadprogressactivity_connecttimeout));
			finishSelf(false);
		}
	};
	private Handler mHandlerTemp = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_new);
		
		//将Activity设置成diglog窗口德形式，点击空白处是不会消失的
		FirmwareUpdateProgressActivity.this.setFinishOnTouchOutside(false);
		
		findView();

		mFilePath = getIntent().getStringExtra("fileurl");
		mStrURL = getIntent().getStringExtra("updateurl");
		Logger.i(TAG, "--- " + mFilePath);
		Logger.i(TAG, "--- " + mStrURL);

		mFileNa = mStrURL.substring(mStrURL.lastIndexOf("/") + 1);
		mDeviceVersion = getIntent().getStringExtra("deviceversion");
		mDeviceveId = getIntent().getStringExtra("deviceveId");
		Logger.i(TAG, "--- mDeviceveId" + mDeviceveId);
		mTvMessage.setText(getResources().getString(R.string.firmwareuploadprogressactivity_connecting));
		connectDevice(mDeviceveId);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return false;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.i(TAG, "--- onDestroy ");
		if(tgBleManager != null){
			tgBleManager.close();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, requestCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			Logger.i(TAG, "---onActivityResult" + resultCode);
			if (resultCode == RESULT_OK) {
				connectDevice(mDeviceveId);
			}else if(resultCode == RESULT_CANCELED){
				finishSelf(false);
			}
			return;
		}
	}

	private void findView() {
		// 
		mMyProgress = (DownLoadApkProgress) findViewById(R.id.pgsBar);
		mTvMessage = (TextView) findViewById(R.id.tv_progress_message);
		mTvPrcent = (TextView) findViewById(R.id.tv_progress_prcent);
	}

	private void checkUpdate(String deviceVersion){
		Logger.i(TAG, "--- deviceVersion " + deviceVersion);
		Logger.i(TAG, "--- tgBleManager.getFwVersion() " + tgBleManager.getFwVersion());
		String[] arr = tgBleManager.getFwVersion().split("\\.");
		String[] vArr = deviceVersion.split("\\.");
		if (arr.length >= 3) {
			int arrH = Integer.valueOf(arr[0]);
			int arrM = Integer.valueOf(arr[1]);
			int arrL = Integer.valueOf(arr[2]);

			int vArrH = Integer.valueOf(vArr[0]);
			int vArrM = Integer.valueOf(vArr[1]);
			int vArrL = Integer.valueOf(vArr[2]);

			if (arrH > vArrH) {
				ToastUtils.showToast(this, getResources().getString(R.string.firmwareuploadprogressactivity_isnew));
				updateToken();
			} else if ((arrH == vArrH) && (arrM > vArrM)) {
				ToastUtils.showToast(this, getResources().getString(R.string.firmwareuploadprogressactivity_isnew));
				updateToken();
			} else if ((arrH == vArrH) && (arrM == vArrM) && (arrL >= vArrL)) {
				ToastUtils.showToast(this, getResources().getString(R.string.firmwareuploadprogressactivity_isnew));
				updateToken();
			} else {
				tgBleManager.updateFirmware(mFilePath, mFileNa);
				
			}
		}
	}
	
	private void updateToken(){
		Logger.i(TAG, "--- updateToken ");
		new Thread(){
			public void run() {
				SaveDeviceToken saveBack = new SaveDeviceToken();
				String userId = PreferencesUtils.getString(getApplicationContext(), SharedPreferredKey.USERUID,"");
				int back = DataSyn.getInstance().saveDeviceToken(userId, mDeviceveId,  tgBleManager.getHwSerialNumber(), tgBleManager.getBondToken(), mDeviceVersion, saveBack);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						finishSelf(true);
					}
				});
			};
		}.start();
	}
	
	private void finishSelf(boolean isupdate){
		if(isupdate){
			Intent intent = new Intent();
			intent.putExtra("result", "update");
			setResult(RESULT_OK, intent);
		}
		if(tgBleManager != null){
			tgBleManager.close();
		}
		mFirmwareUpdateProgressActivity.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
	}
	private void connectDevice(String deviceId){
		// 检查蓝牙是否开启
		if (!deviceId.isEmpty()) {
			if(Build.VERSION.SDK_INT >= 18){
				if(checkBlueEnabled()){
					//连接当前设备
					mTvMessage.setText(getResources().getString(R.string.firmwareuploadprogressactivity_connecting));
					tgBleManager = TgbleManagerNeuro.getSingleInstance(getBaseContext());
					tgBleManager.setHandle(mHandler);
					tgBleManager.setRealActivitiy(false);
					mHandlerTemp.postDelayed(_connect_timeout, 1000*20);
					tgBleManager.content(mDeviceveId);
				}
			}else{
				ToastUtils.showToast(this, getResources().getString(R.string.firmwareuploadprogressactivity_device));
				finishSelf(false);
			}
			
		}else{
			//设备ID为空
			ToastUtils.showToast(this, getResources().getString(R.string.firmwareuploadprogressactivity_connectfalse));
			finishSelf(false);
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
}