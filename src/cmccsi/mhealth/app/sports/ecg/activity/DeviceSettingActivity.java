package cmccsi.mhealth.app.sports.ecg.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.appversion.AddDeviceActivity;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.BaseDeviceInterface;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.ecg.adapter.DeviceListAdapter;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.pedo.TgbleManagerNeuro;
import cmccsi.mhealth.app.sports.pedo.UploadManager;
import cmccsi.mhealth.app.sports.phonesteplib.StepController;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.app.sports.R;

import com.cmcc.bracelet.lsjx.libs.JWDeviceManager;

public class DeviceSettingActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "DeviceSettingActivity";
	private final static int REQUEST_ENABLE_BT = 1;
	private final static int REQUEST_ENABLE_DEVICE_ADD = 2;
	
	private final static int UPLOAD_PERIOD = 30 * 1000;
	private final static int CONNECT_PERIOD = 20 * 1000;
	
	public final static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	private static final int NET_PROBLEM = 5; // 网络问题

	private CustomProgressDialog mDialog;
	private List<String> mData = new ArrayList<String>();
	private DeviceListAdapter mAdapter;
	private DeviceListInfo deviceListInfo;
	private DeviceInfo oldDevice;
	private DeviceInfo selectedDevice;
	private BackInfo uploadResult;
	private StepServiceUploadReceiver receiver;
	private ListView listView;
	private TextView title;
	private LinearLayout mLlBracelet;
	private LinearLayout mLlNotice;

	private CustomProgressDialog progressDialog;
	private CommonAskDialog mAskDialog = null;
	private Dialog dialog;
	private JWDeviceManager mJWDeviceManager = null;

	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private List<String> mBleMacList;

	private TgbleManagerNeuro tgble = null;
	private int isTgbleToOther = 0;// 是否手环切换到其他 1是手环到其他 2是其他到手环
	
	private boolean isUploaded = false;
	private boolean isConnected = false;
	private Activity mContext = DeviceSettingActivity.this;

	private Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TgbleManagerNeuro.MSG_CONNECTED: // 神念手环连接成功

				Logger.d(TAG, "---手环链接成功！isTgbleToOther " + isTgbleToOther);
				isConnected = true;
				mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);
				if (isTgbleToOther == 1) {
					tgble.getPedometerData();
				} else if (isTgbleToOther == 2) {
					// 切换成功后上传设备信息
					new UplaodBindedDeviceTask().execute();
				}
				break;
			case TgbleManagerNeuro.MSG_EXCEPT: //神念手环连接失败
				Logger.d(TAG, "---手环链接失败！");
				close();
				tgble.close();
				if (isTgbleToOther != 1) {
					isConnected = true;
					mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);
//					ToastUtils.showToast(getBaseContext(), "您的手环没有切换成功。");
					showMyToast(getApplicationContext(), "您的手环没有切换成功。");
					// 如果从手机计步器切换，失败后重新启动
					if (oldDevice != null && Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara) == DeviceConstants.DEVICE_MOBILE_STEP) {
						startPhoneStep();
					}
				}else {
					isUploaded = true;
					mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
				    new UploadBraceletDataTask().execute();
				}
				break;
			case TgbleManagerNeuro.MSG_SUCCESS: // 上传手环数据成功
				Logger.d(TAG, "---上传手环数据成功");
				isUploaded = true;
				mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
				close();
				tgble.close();
				if (Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara) == DeviceConstants.DEVICE_BRACLETE_JW) {
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接丁当手环");
					mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
					mJWDeviceManager.setCallBack(mBaseCallBack);
				}else{
					// 切换成功后上传设备信息
					new UplaodBindedDeviceTask().execute();
				}
				
				break;
			case TgbleManagerNeuro.MSG_FAILED: // 上传手环数据失败	
				Logger.d(TAG, "---上传手环数据失败");
				close();
				tgble.close();
				ToastUtils.showToast(getBaseContext(), "您的手环没有切换成功。");
				break;
			case TgbleManagerNeuro.MSG_RESET:
				Logger.d(TAG, "---手环链接失败！");
				close();
				tgble.close();
				if (isTgbleToOther != 1) {
					isConnected = true;
//					ToastUtils.showToast(getBaseContext(), msg.getData().getString("MSG_RESET"));
					showMyToast(getApplicationContext(), msg.getData().getString("MSG_RESET"));
				}else {
					isUploaded = true;
					mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
				    new UploadBraceletDataTask().execute();
				}
				break;
			case TgbleManagerNeuro.MSG_TRANSPORT:
				break;
			case TgbleManagerNeuro.MSG_STATUS:

				break;
			case TgbleManagerNeuro.MSG_POWER:

				break;
			case TgbleManagerNeuro.MSG_CURRENTCOUNT:

				break;
				
			case DeviceConstants.CONNECTED_SUCCESS: //丁当手环
				Logger.d(TAG, "---丁当连接成功");
				isConnected = true;
				mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);
				// 切换成功后上传设备信息
				new UplaodBindedDeviceTask().execute();
				break;
			case DeviceConstants.CONNECTED_FAIL:
				Logger.d(TAG, "---丁当连接失败");
				if(Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara) == DeviceConstants.DEVICE_BRACLETE_JW) {
//					ToastUtils.showToast(getBaseContext(), "您的手环没有切换成功，请查看手环是否开机");
					showMyToast(getApplicationContext(), "您的手环没有切换成功，请查看手环是否开机。");
				}else{
					showMyToast(getApplicationContext(), "您的手环没有切换成功。");
				}
				close();
				// 如果从手机计步器切换，失败后重新启动
				if (oldDevice != null && Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara) == DeviceConstants.DEVICE_MOBILE_STEP) {
					startPhoneStep();
				}
				break;
			case DeviceConstants.UPLOAD_SUCCESS:

				break;
			case DeviceConstants.UPLOAD_FAIL:

				break;
			default:
				break;
			}
		};
	};
	//上传手环数据超时
	private Runnable _bracelet_upload_timeout = new Runnable() {
		@Override
		public void run() {
			if(!isUploaded){
				mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
				Logger.i(TAG, "上传手环数据  超时，开始上传本地手环数据");
				close();
				tgble.close();
			    new UploadBraceletDataTask().execute();
			}else{
				Logger.i(TAG, "上传手环数据  未超时");
			}
		}
	};
	//连接手环超时
	private Runnable _bracelet_connect_timeout = new Runnable() {
		@Override
		public void run() {
			
			if(!isConnected){
				Logger.i(TAG, "连接手环 超时");
				mHandlerTemp.removeCallbacks(_bracelet_connect_timeout);
				if(Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara) == DeviceConstants.DEVICE_BRACLETE_JW) {
					Message msg = new Message();
					msg.what = DeviceConstants.CONNECTED_FAIL;
					mbleAlertHandler.sendMessage(msg);
				} else if(Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara) == DeviceConstants.DEVICE_BRACLETE_JW201) {
					Message msg = new Message();
					msg.what = DeviceConstants.CONNECTED_FAIL;
					mbleAlertHandler.sendMessage(msg);
				}else{						
					Message msg = new Message();
					msg.what = TgbleManagerNeuro.MSG_EXCEPT;
					mbleAlertHandler.sendMessage(msg);
				}
				
			}else{
				Logger.i(TAG, "连接手环  未超时");
			}
		}
	};
	private Handler mHandlerTemp = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_setting);

		Logger.i(TAG, "---onCreate");
		
		initViews();
		// 初始化设备列表数据
		initList();

		// 提示Dialog
		mDialog = CustomProgressDialog.createDialog(this);
		mDialog.setCanceledOnTouchOutside(false);

		new LoadDeviceListTask("加载设备列表...").execute();

		// 手机计步广播接收
		receiver = new StepServiceUploadReceiver();
		IntentFilter itentf = new IntentFilter();
		itentf.addAction(Config.UPLOADSTATUS_ACTION);
		itentf.addAction(Config.PHONESTEP_STARTUPLOAD_ACTION);
		registerReceiver(receiver, itentf);

		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		mBleMacList = new ArrayList<String>();
		
		if (Build.VERSION.SDK_INT >= 18
				&& PreferencesUtils.getBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, false)) {
			// 初始化手环设备管理工具
			tgble = TgbleManagerNeuro.getSingleInstance(getBaseContext());
			tgble.setHandle(mbleAlertHandler);
			tgble.setRealActivitiy(false);
		} else if (Build.VERSION.SDK_INT < 18
				&& PreferencesUtils.getBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, false)) {
			ToastUtils.showToast(DeviceSettingActivity.this, "您的Android系统版本过低，暂不支持蓝牙手环");
			return;
		}
		
		//当前设备如果是手环，检查蓝牙是否开启
		if(PreferencesUtils.getInt(DeviceSettingActivity.this, SharedPreferredKey.DEVICE_TYPE, 0) == 2){
			checkBlueEnabled();
		}
		
		//初始化丁当手环
		mJWDeviceManager = JWDeviceManager.getInstance(this);
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
		listView = (ListView) findViewById(R.id.list_device);

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
		mLlBracelet = (LinearLayout) findViewById(R.id.ll_bracelet);

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(tgble != null){
			tgble.close();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "---onDestroy");
		unregisterReceiver(receiver);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_input_bg_back: // 返回按钮
			if(tgble != null){
				tgble.close();
			}			
			DeviceSettingActivity.this.finish();
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
		mAdapter = new DeviceListAdapter(this, mData);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAdapter.notifyDataSetChanged();
				selectedDevice = deviceListInfo.datavalue.get(position);
				int oldDeviceType = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
				int selectDeviceType = Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara);
				String msg_device_type = Common.getDeviceDisplayName(selectDeviceType);

				if (oldDevice != null && oldDevice.deviceSerial.equals(selectedDevice.deviceSerial)) {
					return;
				} else {
					if (oldDeviceType == DeviceConstants.DEVICE_PEDOMETER) {
						showAskDialog("请手动上传您计步器的运动数据，切换后，运动界面将显示" + msg_device_type
								+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？",
								selectDeviceType);
					} else if (selectDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
							|| selectDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
							|| selectDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
						if (Build.VERSION.SDK_INT < 18) {
							Toast.makeText(DeviceSettingActivity.this, "您的Android系统版本过低，暂不支持配对该蓝牙手环。",
									Toast.LENGTH_SHORT).show();
							return;
						}
						showAskDialog("切换后，运动界面将显示" + msg_device_type
								+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？",
								selectDeviceType);
					} else {
						showAskDialog("切换后，运动界面将显示" + msg_device_type
								+ "设备的计步数据，且该设备的数据将作为您当天的有效数据参与各排名，您确定切换吗？",
								selectDeviceType);
					}
				}
			}
		});
		
		if(Config.ISALONE){
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	            	if (oldDevice.deviceSerial.equals(deviceListInfo.datavalue.get(position).deviceSerial)) {
	            		showDeleteInfoDialog("提示", "您的设备处于激活状态，为了防止数据异常，请先切换或者解绑设备后再进行删除操作。");
	        		} else {
	                    showDeleteAlertDialog(position);
	        		}
	                return true;
	            }
	        });
		}
		
	}
	private void showAskDialog(String msg, final int newDevice) {

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
					switch (newDevice) {
					case DeviceConstants.DEVICE_MOBILE_STEP:
						switchToPhone();
						break;
					case DeviceConstants.DEVICE_BRACLETE_BEATBAND:
						switchToBraclet();
						break;
					case DeviceConstants.DEVICE_PEDOMETER:
						switchToPedometer();
						break;
					case DeviceConstants.DEVICE_BRACLETE_JW:
						switchToJWBraclet();
						break;
					case DeviceConstants.DEVICE_BRACLETE_JW201:
						switchToJW201Braclet();
						break;
					default:
						// 切换成功后上传设备信息
						new UplaodBindedDeviceTask().execute();
						break;
					}
				}
			}
		});
		mAskDialog.show(getSupportFragmentManager(), "CommonAskDialog");
	}
	private void showDeleteInfoDialog(String dialogHeadText, String dialogContent) {
		dialog = new Dialog(mContext, R.style.dialog_withStatusBar);
		dialog.setContentView(R.layout.dialog_delete);
		((TextView) dialog.findViewById(R.id.Introduction_delete)).setText(dialogHeadText);
		((TextView) dialog.findViewById(R.id.Introduction_delete_detail)).setText(dialogContent);
		dialog.findViewById(R.id.btn_dialog_delete_yes).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
    /**
     * 长按删除设备提示框
     */
	private void showDeleteAlertDialog(final int position) {
		String dialogHeadText = "提示";
		String dialogContent = "您确定要删除该设备吗？";

		dialog = new Dialog(mContext, R.style.dialog_withStatusBar);
		dialog.setContentView(R.layout.dialog_delete);
		((TextView) dialog.findViewById(R.id.Introduction_delete)).setText(dialogHeadText);
		((TextView) dialog.findViewById(R.id.Introduction_delete_detail)).setText(dialogContent);
		dialog.findViewById(R.id.btn_dialog_delete_yes).setVisibility(View.GONE);
		dialog.findViewById(R.id.ll_dialog_delete_buttons).setVisibility(View.VISIBLE);
		
		dialog.findViewById(R.id.btn_dialog_delete_sure).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteDevice(position);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.findViewById(R.id.btn_dialog_delete_cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
    /**
     * 删除设备方法
     */
    private void deleteDevice(int position){	
		try {
			new DeleteDeviceTask().execute(deviceListInfo.datavalue.get(position).deviceSerial,
					deviceListInfo.datavalue.get(position).productPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	/**
	 * 切换到手机计步
	 * TODO
	 * @return void
	 * @author shaoting.chen
	 * @time 上午9:57:55
	 */
	private void switchToPhone() {
		Logger.i(TAG, "---switchToPhone");
		int old_devcie_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
		
		if (oldDevice != null) {
			if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				isTgbleToOther = 1;
			}
		}

		if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {// 神念手环切换到手机，上传数据
			showProgress("正在上传手环数据");
			new UploadBraceletDataTask().execute();
		} else if(old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW) {
			showProgress("正在上传手环数据");
			new UploadJWBraceletDataTask().execute();
		} else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW201) { //从手机计步
			showProgress("正在上传手环数据");
			new UploadJWBraceletDataTask().execute();
		} else { //其他设备切换到
			// 切换成功后上传设备信息
			new UplaodBindedDeviceTask().execute();
		}
	}
	/**
	 * 切换到计步器
	 * TODO
	 * @return void
	 * @author shaoting.chen
	 * @time 上午9:57:55
	 */
	private void switchToPedometer() {
		Logger.i(TAG, "---switchToPedometer");
		int old_devcie_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
		
		if (oldDevice != null) {
			if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				isTgbleToOther = 1;
			}
		}

		// 如果从手机切换
		if (oldDevice != null && old_devcie_type == DeviceConstants.DEVICE_MOBILE_STEP
				&& Common.isStepServiceRunning(DeviceSettingActivity.this)) {
			showProgress("正在结束手机计步");
			// 上传信息？where回调？
			Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
			sendBroadcast(it);
			return;

		} else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {// 从手环切换
			showProgress("正在上传手环数据");
			new UploadBraceletDataTask().execute();
		} else if(old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW) {
			showProgress("正在上传手环数据");
			new UploadJWBraceletDataTask().execute();
		} else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW201) { //从手机计步
			showProgress("正在上传手环数据");
			new UploadJWBraceletDataTask().execute();
		} else {
			// 切换成功后上传设备信息
			new UplaodBindedDeviceTask().execute();
		}
	}

	/**
	 * 切换到 神念（BeatBand）手环计步 
	 * 
	 * @return void
	 * @author shaoting.chen
	 * @time 下午2:08:28
	 */
	private void switchToBraclet() {
		Logger.i(TAG, "---switchToBraclet");
		int old_devcie_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);

		if (checkBlueEnabled()) {
			if (old_devcie_type == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning) { //从手机计步
				showProgress("正在结束手机计步");
				Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
				sendBroadcast(it);
				return;
			} else if(old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW) {
				showProgress("正在上传手环数据");
				new UploadJWBraceletDataTask().execute();
			} else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW201) { //从手机计步
				showProgress("正在上传手环数据");
				new UploadJWBraceletDataTask().execute();
			} else {
				isConnected = false;
				mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
				showProgress("正在连接手环");
				isTgbleToOther = 2;
				tgble.setOption(0);
				tgble.content();
			}
		}
	}
	/**
	 * 切换到 丁当（JW）手环计步 
	 * 
	 * @return void
	 * @author shaoting.chen
	 * @time 下午2:08:28
	 */
	private void switchToJWBraclet() {
		Logger.i(TAG, "---switchToJWBraclet");
		int old_devcie_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
		mJWDeviceManager = JWDeviceManager.getInstance(this);
		
		if (checkBlueEnabled()) {
			if (oldDevice != null) {
				if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
					isTgbleToOther = 1;
				}
			}
			if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {// 神念手环切换到手机，上传数据
				showProgress("正在上传手环数据");
				new UploadBraceletDataTask().execute();
			}else if (old_devcie_type == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning) { //从手机计步
				showProgress("正在结束手机计步");
				Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
				sendBroadcast(it);
				return;
			}else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW201) { //从手机计步
				showProgress("正在上传手环数据");
				new UploadJWBraceletDataTask().execute();
			} else {
//				isConnected = false;
//				mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
				isConnected = false;
				mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
				showProgress("正在连接丁当手环");
				Logger.i(TAG, "----MAC" + selectedDevice.deviceSerial.substring(2));
				mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
				mJWDeviceManager.setCallBack(mBaseCallBack);
			}
		}
	}
	/**
	 * 切换到 丁当201（JW）手环计步 
	 * 
	 * @return void
	 * @author shaoting.chen
	 * @time 下午2:08:28
	 */
	private void switchToJW201Braclet() {
		Logger.i(TAG, "---switchToJW201Braclet");
		mJWDeviceManager = JWDeviceManager.getInstance(this);
		int old_devcie_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
		if (checkBlueEnabled()) {
			if (oldDevice != null) {
				if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
					isTgbleToOther = 1;
				}
			}
			if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {// 神念手环切换到手机，上传数据
				showProgress("正在上传手环数据");
				new UploadBraceletDataTask().execute();
			}else if (old_devcie_type == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning) { //从手机计步
				showProgress("正在结束手机计步");
				Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
				sendBroadcast(it);
				return;
			}else if (old_devcie_type == DeviceConstants.DEVICE_BRACLETE_JW) { //从手机计步
				showProgress("正在上传手环数据");
				new UploadJWBraceletDataTask().execute();
			} else {
//				isConnected = false;
//				mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
				isConnected = false;
				mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
				showProgress("正在连接丁当201手环");
				Logger.i(TAG, "----MAC" + selectedDevice.deviceSerial.substring(2));
				mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
				mJWDeviceManager.setCallBack(mBaseCallBack);
			}
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
	 * 开启手机计步
	 */
	private void startPhoneStep() {
		StepController mStepController = new StepController();
		mStepController.setContext(getBaseContext());
		mStepController.startStepService(Config.SC_ACTION);
	}

	/**
	 * 设置选中项目
	 */
	private void setSelectedItem() {
		for (int i = 0; i < deviceListInfo.datavalue.size(); i++) {
			if (deviceListInfo.datavalue.get(i).isUsed.equals("1")) {

				mAdapter.setFirstSelectPosition(i);
				oldDevice = deviceListInfo.datavalue.get(i);
				return;
			}
		}
        if(oldDevice == null){
        	oldDevice = new DeviceInfo();
			oldDevice.deviceSerial = "noDevice";
			oldDevice.productPara = "noDevice";
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				//当前设备如果是手环，检查蓝牙是否开启
				if(PreferencesUtils.getInt(DeviceSettingActivity.this, SharedPreferredKey.DEVICE_TYPE, 0) != 2){
					switchToBraclet();
				}
			}
			return;
		}else if(requestCode == REQUEST_ENABLE_DEVICE_ADD){
			if (resultCode == RESULT_OK && data.getExtras().getString("result").equals("add")) {
				new LoadDeviceListTask("加载设备列表...").execute();
			}			
		}
		super.onActivityResult(requestCode, requestCode, data);
	}

	/**
	 * 上传设备信息
	 * @author Xiao
	 */
	private class UplaodBindedDeviceTask extends AsyncTask<Null, Null, Integer> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress("正在上传新绑定设备的信息");
		}

		@Override
		protected Integer doInBackground(Null... params) {
			if (!NetworkTool.isOnline(DeviceSettingActivity.this)) {
				return -1;
			}
			uploadResult = new BackInfo();
			Logger.i("cjz", "UplaodBindedDeviceTask");
			return DataSyn.getInstance().uploadBindDeviceInfo(oldDevice, selectedDevice, uploadResult);

		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Logger.i("cjz", "加载了一次！");
			if (result == 0) {
				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				Editor editor = info.edit();
				editor.putString(SharedPreferredKey.DEVICE_ID, selectedDevice.deviceSerial);
				editor.putInt(SharedPreferredKey.DEVICE_TYPE, Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara));
				editor.putString(SharedPreferredKey.DEVICE_NAME, selectedDevice.productName);
				editor.putString(SharedPreferredKey.DEVICE_MODEL, selectedDevice.productPara);

				editor.commit();
				if (Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara) == DeviceConstants.DEVICE_MOBILE_STEP)// 切换到手机开启手机计步
				{
					startPhoneStep();
				}
				new LoadDeviceListTask("重新加载设备列表...", true).execute();

				Logger.i(TAG, "---重新加载成功");
			} else {
				if (result == 500) {
					ToastUtils.showToast(DeviceSettingActivity.this, uploadResult.reason);
				}else {
					Logger.i("cjz", "网络异常，请检查网络设置！");
					ToastUtils.showToast(DeviceSettingActivity.this, R.string.MESSAGE_INTERNET_ERROR);
				}
				if (oldDevice != null && Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara) == DeviceConstants.DEVICE_MOBILE_STEP)// 切换到手机开启手机计步
				{
					startPhoneStep();
				}
				Logger.i(TAG, "---重新加载失败");
			}
			close();
		}
	}

	/**
	 * 从网络加载设备列表
	 */
	private class LoadDeviceListTask extends AsyncTask<Null, Null, Integer> {
		private boolean mReload = false;

		public LoadDeviceListTask(String msg, boolean reLoad) {
			showProgress(msg);
			this.mReload = reLoad;
		}
		public LoadDeviceListTask(String msg) {
			showProgress(msg);
		}

		@Override
		protected Integer doInBackground(Null... params) {
			if (!NetworkTool.isOnline(DeviceSettingActivity.this)) {
//				Toast.makeText(getApplicationContext(), "网络异常，请检查网络设置!", 1).show();
				handle.sendEmptyMessage(NET_PROBLEM);
				return -1;
			}

			deviceListInfo = DeviceListInfo.getInstance();
			int res = DataSyn.getInstance().getDeviceListData(deviceListInfo);
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 0) {
				if (deviceListInfo.datavalue.size() > 0) {
					listView.setVisibility(View.VISIBLE);
					mLlNotice.setVisibility(View.INVISIBLE);
			
					mData.clear();
					boolean hasECG=false;
					for (DeviceInfo deviceInfo : deviceListInfo.datavalue) {
						if (Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara) == DeviceConstants.DEVICE_BRACLETE_BEATBAND)
						{
							hasECG=true;
						}
						String type = deviceInfo.deviceSerial.substring(0, 2);
						if (type.equals("00"))// 手机
						{
							mData.add(deviceInfo.productName + " " + android.os.Build.MODEL);
						} else {
							String tempProductName="";
							tempProductName=Common.getDeviceDisplayName(Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara));
//							tempProductName=deviceInfo.productName;
							mData.add(tempProductName + " " + deviceInfo.deviceSerial);
						}
					}
					PreferencesUtils.putBoolean(DeviceSettingActivity.this, SharedPreferredKey.HAVE_BRACELET_DEVICE, hasECG);
					mAdapter.setDeviceListInfo(deviceListInfo, mData);
					setSelectedItem();
					mAdapter.notifyDataSetChanged();
					
					if(mReload){
//						ToastUtils.showToast(DeviceSettingActivity.this, "切换成功！");
					}

				} else {
					listView.setVisibility(View.INVISIBLE);
					mLlNotice.setVisibility(View.VISIBLE);
					mLlBracelet.setVisibility(View.GONE);
				}
			}else {
				if(mReload){
					ToastUtils.showToast(DeviceSettingActivity.this, "切换失败！");
					// 如果从手机计步器切换，失败后重新启动
					if (oldDevice != null && Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara) == DeviceConstants.DEVICE_MOBILE_STEP) {
						startPhoneStep();
					}
				}
			}
			close();
		}
	}

	/**
	 * 提交本地手环数据
	 */
	private class UploadBraceletDataTask extends AsyncTask<Null, Null, Integer> {
		
		public UploadBraceletDataTask() {
			showProgress("上传本地手环数据...");
		}

		@Override
		protected Integer doInBackground(Null... params) {
			PedometorDataInfo data = null;
			data = PedoController.GetPedoController(mContext).getPedometerByDay(oldDevice.deviceSerial,
					new Date(System.currentTimeMillis()));

			if(data != null){
				return (UploadManager.uploadPedo(data)) ? 0 : 1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			close();
			if (result == 0) {
				if(Build.VERSION.SDK_INT < 18)
				{
					new UplaodBindedDeviceTask().execute();
					return;
				}
				
				int old_device_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
				
				if (old_device_type == DeviceConstants.DEVICE_BRACLETE_JW) {
					mJWDeviceManager.disConnect();
				}else if (old_device_type == DeviceConstants.DEVICE_BRACLETE_JW201) {
					mJWDeviceManager.disConnect();
				}else if (old_device_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND){
					tgble.close();
				}
				
				int select_device_type = Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara);
				
				if (select_device_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) { //手环
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接手环");
					isTgbleToOther = 2;
					tgble.setOption(0);
					tgble.content();
				}else if (select_device_type == DeviceConstants.DEVICE_BRACLETE_JW) {
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接丁当手环");
					mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
					mJWDeviceManager.setCallBack(mBaseCallBack);
				} else if (select_device_type == DeviceConstants.DEVICE_BRACLETE_JW201) {
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接丁当201手环");
					mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
					mJWDeviceManager.setCallBack(mBaseCallBack);
				}else{
					// 切换成功后上传设备信息
					new UplaodBindedDeviceTask().execute();
				}

			} else {
				ToastUtils.showToast(mContext, "切换失败！");
			}
		}
	}
	
	/**
	 * 提交本地手环数据
	 */
	private class UploadJWBraceletDataTask extends AsyncTask<Null, Null, Integer> {
		
		public UploadJWBraceletDataTask() {
			showProgress("上传本地手环数据...");
		}

		@Override
		protected Integer doInBackground(Null... params) {
			PedometorDataInfo data = null;
			data = PedoController.GetPedoController(mContext).getPedometerByDay(oldDevice.deviceSerial,
					new Date(System.currentTimeMillis()));

			if(data != null){
				return (UploadManager.uploadBlePedo(data)) ? 0 : 1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			close();
			if (result == 0) {
				
				int old_device_type = Common.getDeviceType(oldDevice.deviceSerial, oldDevice.productPara);
				int select_device_type = Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara);
				
				if (old_device_type == DeviceConstants.DEVICE_BRACLETE_JW) {
					mJWDeviceManager.disConnect();
				}else if (old_device_type == DeviceConstants.DEVICE_BRACLETE_JW201) {
					mJWDeviceManager.disConnect();
				}else if (old_device_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND){
					tgble.close();
				}
				if (select_device_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) { //手环
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接手环");
					isTgbleToOther = 2;
					tgble.setOption(0);
					tgble.content();
				}else if (select_device_type == DeviceConstants.DEVICE_BRACLETE_JW) {
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接丁当手环");
					mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
					mJWDeviceManager.setCallBack(mBaseCallBack);
				} else if (select_device_type == DeviceConstants.DEVICE_BRACLETE_JW201) {
					isConnected = false;
					mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
					showProgress("正在连接丁当201手环");
					mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
					mJWDeviceManager.setCallBack(mBaseCallBack);
				}else{
					// 切换成功后上传设备信息
					new UplaodBindedDeviceTask().execute();
				}

			} else {
				ToastUtils.showToast(mContext, "切换失败！");
			}
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
			close();
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

	/**
	 * 手机计步 广播接收器
	 */
	private class StepServiceUploadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Config.UPLOADSTATUS_ACTION)
					|| intent.getAction().equals(Config.PHONESTEP_STARTUPLOAD_ACTION)) {

				String result = "";
				result = intent.getStringExtra(StepService.UPLOAD_STATUS);
				if (result != null && !result.equals("")) {
//					ToastUtils.showToast(DeviceSettingActivity.this, result);
				}
				if (!result.equals(getResources().getString(R.string.phonestep_uploading))) {
					Logger.i("cjz", result + " 结束完了，开始更改sendMsgToChangeDevice");
					if (null == selectedDevice.deviceSerial || null == selectedDevice.productPara) {
						return;
					}

					int select_device_type = Common.getDeviceType(selectedDevice.deviceSerial, selectedDevice.productPara);
					if (select_device_type == DeviceConstants.DEVICE_BRACLETE_BEATBAND) { //手环
						isConnected = false;
						mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
						showProgress("正在连接手环");
						isTgbleToOther = 2;
						tgble.setOption(0);
						tgble.content();
					}else if(select_device_type == DeviceConstants.DEVICE_BRACLETE_JW){
						isConnected = false;
						mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
						showProgress("正在连接丁当手环");
						mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
						mJWDeviceManager.setCallBack(mBaseCallBack);
					} else if (select_device_type == DeviceConstants.DEVICE_BRACLETE_JW201) {
						isConnected = false;
						mHandlerTemp.postDelayed(_bracelet_connect_timeout, CONNECT_PERIOD);
						showProgress("正在连接丁当201手环");
						mJWDeviceManager.connect(selectedDevice.deviceSerial.substring(2));
						mJWDeviceManager.setCallBack(mBaseCallBack);
					}else{
						// 切换成功后上传设备信息
						new UplaodBindedDeviceTask().execute();
					}	
					
				}

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
		progressDialog = CustomProgressDialog.createDialog(DeviceSettingActivity.this);
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
			// TODO Auto-generated method stub
			close();
			Message message = new Message();
			if(code == DeviceConstants.CONNECTED_SUCCESS){
				Logger.i(TAG, "---CONNECTED_SUCCESS");
				message.what = DeviceConstants.CONNECTED_SUCCESS;
			}else if(code == DeviceConstants.CONNECTED_FAIL){
				Logger.i(TAG, "---CONNECTED_FAIL");
				message.what = DeviceConstants.CONNECTED_FAIL;
			}
			mbleAlertHandler.sendMessage(message);
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
	
	//hjn add
	
	Handler handle = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
            
            case NET_PROBLEM:
                ToastUtils.showToast(mContext, R.string.MESSAGE_INTERNET_ERROR);
                break;
            }
        };
    };
    
    private void showMyToast(Context context, String msg){
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 40);
		LinearLayout toastView = (LinearLayout) toast.getView();
		//设置此布局为横向的 
		toastView.setOrientation(LinearLayout.HORIZONTAL); 

		ImageView imageCodeProject = new ImageView(getApplicationContext());
		imageCodeProject.setImageResource(R.drawable.toast_star);
		imageCodeProject.setPaddingRelative(0, 0, 20, 0);
		toastView.addView(imageCodeProject, 0);
		toast.show();

    }

}
