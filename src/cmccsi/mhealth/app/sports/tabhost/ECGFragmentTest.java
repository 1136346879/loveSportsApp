/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cmccsi.mhealth.app.sports.tabhost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.bean.SimplePostInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.device.DeviceManagerService;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.EcgJsonCreat;
import cmccsi.mhealth.app.sports.view.CenterRollingBallECG;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

import com.cmcc.ecg.net.AFDetectionAndroid;
import com.cmcc.ecg.net.Constant;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.neurosky.algorithm.TGLibEKG;

@SuppressLint("NewApi")
public class ECGFragmentTest extends BaseFragment {

	private final static String TAG = "ECG";
	private BluetoothAdapter mBluetoothAdapter;
	private TextView tv_HR, tv_HRV, tv_TraningZone, tv_RR_length, tv_RR_length2, tv_TraningZone2;
	private RelativeLayout rlStartEcg;
	private Button btnAdd;
	boolean readyToSync = false;
	int option = 0;
	private List<NameValuePair> mUploadlist;
	private TextView mTextViewTitle = null;
	protected FragmentActivity mActivity;
	private ImageButton mImageButtonBack = null;
	private TextView buttonText;
	private ImageView img_heart;
	private Animation big_animation = null;
	private int MAX_HR = 105;
	private int MIN_HR = 55;
	private PrintWriter mCurrentFile;
	private StringBuffer buff = new StringBuffer();
	private static GraphView mGraph1;
	private GraphViewSeries gvSeries1;
	private ImageView ecg_level;
	private int graph2LastXValue = 0;
	private int threadFlag = 0;
	private int ECGlength = 2048;
	public static final int MSG_HR = 1;
	public static final int MSG_HRV = 2;
	public static final int MSG_RR = 3;
	public static final int MSG_TRAININGZONE = 4;
	public static final int MSG_MOOD = 5;
	public static final int MSG_ECG = 6;
	public static final int MSG_STATUS = 7;
	public static final int MSG_PERCENT = 8;
	public static final int MSG_POWER = 9;
	public static final int MSG_TIMEOUT = 10;
	public static final int MSG_RR_INTERVAL = 11;
	private int PowerPercent = 0;
	private int REQUEST_ENABLE_BT = 2;
	private String deviceAddress = null;
	Bundle mBundle = new Bundle();
	int[] ECGdata = new int[512];
	int index = 0;
	List<DataECG> ecgDataList = new ArrayList<DataECG>();
	Calendar calendar = Calendar.getInstance();
	Calendar today = (Calendar) calendar.clone();
	DataECG ecgData = new DataECG(today.getTimeInMillis());
	private AFDetectionAndroid AFcode = new AFDetectionAndroid();
	int Rnum = 0;
	boolean isAF = false;
	private boolean ecgWaveFlag = true;
	private Handler mHandlerTemp = new Handler();
	private Runnable mTimer2;
	private RelativeLayout mHRProgress;
	PowerManager powerManager = null;
	WakeLock wakeLock = null;
	private RelativeLayout layout;
	private RelativeLayout mHRProgressResult;// 心脏节律
	private RelativeLayout mProgress_heartlevel;// 心脏负荷率
	private RelativeLayout mProgress_rr;// r间隔
	private String simpleresult = "正常窦性心律";
	private String[] dialogHead = { "心脏健康信息", "心脏负荷率", "R波间隔", "心率范围", "心率波" };
	private String[] dialogText = {
			"1) 心脏健康信息是反映心脏健康的重要指标之一，一般情况下，一段时间内心脏健康信息平均值越高，提示心脏越健康。\n"
					+ "2)  静息状态下，成人心脏健康信息的常见范围为：10~100老年人或久坐人士的心脏健康信息值可以低于10，年轻人及运动员的心脏健康信息值可以高于100。心脏健康信息比较高的人，其心脏健康信息值的浮动区间也较大。\n"
					+ "3)  心脏健康信息受很多因素的影响，如压力，健康状况，睡眠不足及疲劳等都会造成心脏健康信息值的浮动。所以用心脏健康信息检测心脏健康时，使用近期的心脏健康信息的平均值比较有意义。\n"
					+ "4)  不可单纯依赖心脏健康信息的指标代替心脏健康检查，任何心脏风险提示结论需向医生咨询。",
			"1) 心脏负荷是当前心率占最大心率的百分比。专家建议，运动时Heart Level保持在60%~75%左右有助于燃烧脂肪，提高耐力。\n"
					+ "2) 心脏负荷在50~60%属于轻度运动；\n心脏负荷在75~85%:属于剧烈运动；\n心脏负荷>85%时. 应尽量避免或只持续短时间。",
			"R-R间期\n两次心跳的时间间隔（单位为毫秒）。",
			"1) 心跳速率（单位为次/分钟）。 \n" + "2) 最大心率时根据年龄和性别计算出的最大心跳速率。当实际心率比最大心率更高时，可能会对健康有不利影响。 一般人在静息时正常心率范围是:50-100。\n",
			"图形反应了整个心率的节律。每次心跳时、心肌细胞去极化的时候会在皮肤表面引起微小的电学改变，形成心率波。这个微小的信号可以被BeatBand手环检测，并放大呈现。" };

	private String noBracleteToast = "您当前使用的设备不是BeatBand手环，请到设备列表切换后进行测量。";

	private boolean hasBleException = false;
	private boolean isConnect = false;
	@SuppressLint("HandlerLeak")
	Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DeviceConstants.EXCEPTION_CONNECT:
				hasBleException = true;
				Bundle edata = msg.getData();
				if (mDeviceManagerService != null) {
					mDeviceManagerService.disConnect();
				}
				mbleAlertHandler.removeCallbacks(myStopProgressRunnable);
				closeProgress();
				ToastUtils.showToast(getActivity(), edata.getString("EXCEPTION_CONNECT"));
				break;
			case DeviceConstants.DEVICE_RESET:
				hasBleException = true;
				Bundle edataReset = msg.getData();
				if (mDeviceManagerService != null) {
					mDeviceManagerService.disConnect();
				}
				mbleAlertHandler.removeCallbacks(myStopProgressRunnable);
				closeProgress();
				ToastUtils.showToast(getActivity(), edataReset.getString("DEVICE_RESET"));
				break;
			case DeviceConstants.CONNECTED_SUCCESS:
				hasBleException = true;
				isConnect = true;
				mbleAlertHandler.removeCallbacks(myStopProgressRunnable);
				closeProgress();
				ToastUtils.showToast(getActivity(), "手环连接成功");
				if (buttonClick) {
					startTesting();
				}
				handler.removeCallbacks(_l_b_fail_scan);
				break;
			case DeviceConstants.ECG_DEVICE_STATUS:
				Bundle data1 = msg.getData();
				String status = data1.getString("ECG_DEVICE_STATUS");
				if (status.startsWith("正在测量")) {
					showWave();
					Message msgTemp = Message.obtain(mHandler, MSG_STATUS);
					mBundle.putString("MSG_STATUS", status);
					msgTemp.setData(mBundle);
					msgTemp.sendToTarget();
				} else {
					Toast.makeText(getActivity(), data1.getString("ECG_DEVICE_STATUS"), Toast.LENGTH_SHORT).show();
				}
				break;
//			case TgbleManagerNeuro.MSG_POWER:
//				Bundle data6 = msg.getData();
//				int PowerPercent = data6.getInt("MSG_POWER");
//				Toast.makeText(getActivity(), "手环电量：" + PowerPercent + "，电量较低", Toast.LENGTH_SHORT).show();
//				break;
			case DeviceConstants.CONNECTED_FAIL:
				ToastUtils.showToast(getActivity(), "连接失败");
				break;
			case DeviceConstants.ECG_DEVICE_STOP:
				ekgStop(msg.getData().getInt("EKGSTOP_RESULT"), msg.getData().getInt("EKGSTOP_FINALHR"));
				break;
			case DeviceConstants.ECG_DEVICE_DATARECEIVED:
				dataReceived(msg.getData().getInt("EKGSTOP_key"), msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	private static final int ECG_REQUEST_ENABLE_BT = 8002;
	private Activity mContext = getActivity();
	private DeviceManagerService mDeviceManagerService = null;
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

			if (Build.VERSION.SDK_INT >= 18) {
				if (Common.checkBlueEnabled(mContext, ECG_REQUEST_ENABLE_BT)) {
					// 连接当前设备
					mDeviceManagerService.connect(PreferencesUtils
							.getString(mContext, SharedPreferredKey.DEVICE_ID, ""));
				}
			} else {
				ToastUtils.showToast(mContext, "您的Android系统版本过低，暂不支持蓝牙手环");
			}
		}
	};

	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_ecg_new, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		mActivity = getActivity();
		powerManager = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		// graphInit();
		initialView();

		//开启设备管理服务
		Intent _intent = new Intent(mContext, DeviceManagerService.class);
		mContext.startService(_intent);

		return view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == ECG_REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				if (PreferencesUtils.getInt(mActivity, SharedPreferredKey.DEVICE_TYPE, 0) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
					//连接当前设备		
					mDeviceManagerService.connect(PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ID, ""));
				}
			}
			return;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HR:
				Bundle data1 = msg.getData();
				tv_HR.setText(data1.getString("MSG_HR"));
				// ECGResult.setText(data1.getString("HR_RESULT"));
				ecgData.data.hr = data1.getString("MSG_HR");
				if (data1.getInt("HR_STATUS") == 0) {
					mHRProgress.setBackgroundResource(R.drawable.display_2);
				} else {
					mHRProgress.setBackgroundResource(R.drawable.countdown_ring_22);
				}
				break;
			case MSG_HRV:
				Bundle data2 = msg.getData();

				tv_HRV.setText(data2.getString("MSG_HRV"));
				ecgData.data.hrv = data2.getString("MSG_HRV");
				break;
			case MSG_RR_INTERVAL:
				img_heart.startAnimation(big_animation);
				Bundle data11 = msg.getData();

				tv_RR_length.setText(data11.getString("MSG_RR_INTERVAL"));
				tv_RR_length2.setVisibility(View.VISIBLE);
				ecgData.data.rr_interval = data11.getString("MSG_RR_INTERVAL");
				break;
			case MSG_RR:
				break;

			case MSG_TRAININGZONE:
				Bundle data4 = msg.getData();
				tv_TraningZone.setText(data4.getString("MSG_TRAININGZONE"));
				ecgData.data.trainingzone = data4.getString("MSG_TRAININGZONE");
				tv_TraningZone2.setVisibility(View.VISIBLE);
				break;
			case MSG_MOOD:
				Bundle data5 = msg.getData();
				int dataMood = data5.getInt("MSG_MOOD");
				// tv_Mood.setText("情绪指数:" + dataMood);
				// int Width = mMoodLayout.getWidth();
				// float pos = (float) (Width - 160) * dataMood / 100;
				// nowPos = MoveWithAnim(nowPos, pos);
				if (dataMood < 20) {
					ecg_level.setBackgroundResource(R.drawable.ecg_level2);
				} else if (dataMood < 40) {
					ecg_level.setBackgroundResource(R.drawable.ecg_level3);
				} else if (dataMood < 60) {
					ecg_level.setBackgroundResource(R.drawable.ecg_level4);
				} else if (dataMood < 80) {
					ecg_level.setBackgroundResource(R.drawable.ecg_level5);
				} else {
					ecg_level.setBackgroundResource(R.drawable.ecg_level6);
				}
				ecgData.data.mood = data5.getInt("MSG_MOOD") + "";
				break;
			case MSG_ECG:
				ecgWaveFlag = false;
				Bundle data6 = msg.getData();
				dataProcess(data6.getIntArray("MSG_ECG"));
				saveProcess(data6.getIntArray("MSG_ECG"));
				// tv_Mood.setText("MOOD:"+data1);
				break;
			case MSG_STATUS:
				Bundle data7 = msg.getData();
				String status = data7.getString("MSG_STATUS");
				if ("测量完毕".equals(status)) {
					rlStartEcg.setEnabled(true);
					buttonText.setText("开始测量");
				} else if (status.startsWith("正在测量")) {
					buttonText.setText("正在测量");
					rlStartEcg.setEnabled(false);
					BaseToast(status);
				} else {
					BaseToast(status);
				}
				// tv.setText(status);
				// BaseToast(status);
				// tv_Mood.setText("MOOD:"+data1);
				break;
			case MSG_POWER:
				Bundle data9 = msg.getData();
				PowerPercent = data9.getInt("MSG_POWER");
				BaseToast("手环电量：" + PowerPercent + "，电量较低");
				break;
			case MSG_PERCENT:
				Bundle data8 = msg.getData();
				int dataPercent = data8.getInt("MSG_PERCENT");
				if (dataPercent < 64) {
					addProgressView(77, dataPercent);
				} else {
					mTextViewStepPercent.setText("测量进度 100%");
				}
				break;
			case MSG_TIMEOUT:
				Bundle data10 = msg.getData();
				BaseToast(data10.getString("MSG_TIMEOUT"));
				break;
			}
		}
	};

	// private RelativeLayout mHRgoodresult;
	private RelativeLayout mHRbadresult;
	Handler handler = new Handler();
	private Dialog dialog;

	private void initDialog(String dialogHeadText, String dialogContent) {
		dialog = new Dialog(mActivity, R.style.dialog_fullscreen);
		dialog.setContentView(R.layout.dialog_hrv);
		((TextView) dialog.findViewById(R.id.Introduction_hrv)).setText(dialogHeadText);
		((TextView) dialog.findViewById(R.id.Introduction_hrv_detail)).setText(dialogContent);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
		dialog.findViewById(R.id.rl_container).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
	}

	private void showDeviceDialog() {
		dialog.findViewById(R.id.Introduction_hrv).setVisibility(View.VISIBLE);
		dialog.show();
	}

	public void initialView() {
		// title
		buttonText = (TextView) findView(R.id.buttonText);
		img_heart = findView(R.id.img_heart);
		big_animation = AnimationUtils.loadAnimation(mActivity, R.anim.big_heart);

		tv_RR_length = (TextView) findView(R.id.textview_rr);
		tv_RR_length2 = (TextView) findView(R.id.textview_rr_2);
		// tv = (TextView) findView(R.id.textView_sdk_version);
		mTextViewTitle = (TextView) findView(R.id.textView_title);
		mTextViewTitle.setText("心境");
		tv_HRV = (TextView) findView(R.id.textview_hrv);
		// ECGResult = (TextView) findView(R.id.text_mbwcl);
		tv_HR = (TextView) findView(R.id.textview_HR_BPM2);

		ecg_level = (ImageView) findView(R.id.ecg_level);

		tv_TraningZone = (TextView) findView(R.id.textview_heartlevel);
		tv_TraningZone2 = (TextView) findView(R.id.textview_heartlevel_2);
		mHRbadresult = (RelativeLayout) findView(R.id.Progress_hrv);
		mHRbadresult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "click introduction");
				initDialog(dialogHead[0], dialogText[0]);
				showDeviceDialog();
			}

		});
		mProgress_heartlevel = (RelativeLayout) findView(R.id.Progress_heartlevel);
		mProgress_heartlevel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "click introduction");
				initDialog(dialogHead[1], dialogText[1]);
				showDeviceDialog();
			}

		});

		mProgress_rr = (RelativeLayout) findView(R.id.Progress_rr);
		mProgress_rr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "click introduction");
				initDialog(dialogHead[2], dialogText[2]);
				showDeviceDialog();
			}

		});

		mHRProgressResult = (RelativeLayout) findView(R.id.Progress_hr_result);
		mHRProgressResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "click introduction");
				initDialog(dialogHead[3], dialogText[3]);
				showDeviceDialog();
			}

		});

		layout = (RelativeLayout) findView(R.id.ecg_view_line);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "click introduction");
				initDialog(dialogHead[4], dialogText[4]);
				showDeviceDialog();
			}

		});
		mRelativeLayoutProgress = findView(R.id.Progress_center_rote2);
		// gvSeries1.appendData(new GraphViewData(graph2LastXValue, 0), true,
		// ECGlength);

		mHRProgress = (RelativeLayout) findView(R.id.rel_center_progress);

		mImageButtonBack = (ImageButton) findView(R.id.button_input_bg_back);
		mImageButtonBack.setVisibility(View.VISIBLE);
		// mImageButtonBack.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.slidemenu_button));
		// mImageButtonBack.setOnClickListener(this);
		mImageButtonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// MainCenterActivity m = (MainCenterActivity) mActivity;
				// m.showMenu();
				// overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				getActivity().finish();
			}
		});

		rlStartEcg = (RelativeLayout) findView(R.id.relativelaouut_start_ecg);
		rlStartEcg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PreferencesUtils.getInt(mActivity, SharedPreferredKey.DEVICE_TYPE, 0) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
					if (!isConnect) {
						showProgress("正在连接中...");
						buttonClick = true;
						return;
					}
					startTesting();
				} else {
					ToastUtils.showToast_L(mActivity, noBracleteToast);
				}

			}

		});
		btnAdd = (Button) findView(R.id.button_add);

		btnAdd.setVisibility(View.VISIBLE);
		btnAdd.setBackgroundResource(R.drawable.history_icon);
		btnAdd.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.history_icon_2);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.history_icon);
					// v.setBackgroundResource(R.drawable.history_icon);

				}
				return false;
			}
		});
		btnAdd.setOnClickListener(new OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				Date date = new Date();
				SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				Intent intent = new Intent();
				// lianxw
				// intent.setClass(mActivity, HistoryECGDetailActivity.class);
				intent.setClass(mActivity, cmccsi.mhealth.app.sports.ecg.activity.HistoryECGDetailActivity.class);
				intent.putExtra("searchDate", df_yyyyMMdd.format(date));
				startActivity(intent);
			}
		});
		addProgressView(77, 0);
		AFcode.setRnum(64);
		AFcode.Initialise();

	}

	private void startTesting() {
		graphInit();
		// rlStartEcg.setBackgroundResource(R.drawable.button_start_ecg);
		ecg_level.setBackgroundResource(R.drawable.ecg_level2);
		Rnum = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date(System.currentTimeMillis());
		ecgDataList.clear();

		ecgData.createtime = sf.format(date);
		addProgressView(77, 0);
		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			// 无数据时显示水平线
			ecgWaveFlag = true;
			showWaveWithoutECG();
			mDeviceManagerService.startRealTimeEKG();
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		mDeviceManagerService.stopRealTimeEKG();
		mDeviceManagerService.disConnect();
		// tgBleManager.disconnect();//wy
		if (_l_b_fail_scan != null) {
			Log.i(TAG, "Disable the timer for the fail scan.");
			handler.removeCallbacks(_l_b_fail_scan);
		}
		Log.i(TAG, "连接已关闭");
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		
		// 绑定Service
		Intent _intent = new Intent(mContext, DeviceManagerService.class);
		mContext.bindService(_intent, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
		// tgBleManager.close();
		super.onPause();
		wakeLock.release();
	}

	@Override
	public void onStop() {
		// tgBleManager.close();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// tgBleManager.close();
		mDeviceManagerService = null;
		mContext.unbindService(conn);
		super.onDestroy();
	}

	String FinalHR = "";

	private void dataReceived(int key, Object data) {
		// Logger.d(TAG, "dataReceived key:"+key +"dataReceived data"+data);
		switch (key) {
		case TGLibEKG.MSG_CARDIOZONE_HEARTRATE:
			Log.i(TAG, "HR: " + data);
			// tv_HR = (TextView)findView(R.id.textview_HR_BPM);
			if ((Integer) data > 999)
				data = 999;

			FinalHR = data.toString();
			Message msg = Message.obtain(mHandler, MSG_HR);
			if ((Integer) data > MAX_HR) {
				simpleresult = "窦性心律过速";
				mBundle.putString("HR_RESULT", "心动过速");
				mBundle.putInt("HR_STATUS", 0);
			} else if ((Integer) data <= MAX_HR && (Integer) data >= MIN_HR) {
				simpleresult = "正常窦性心律";
				mBundle.putString("HR_RESULT", "心率正常");
				mBundle.putInt("HR_STATUS", 1);
			} else {
				simpleresult = "窦性心律过缓";
				mBundle.putString("HR_RESULT", "心动过缓");
				mBundle.putInt("HR_STATUS", 0);
			}
			mBundle.putString("MSG_HR", data.toString());
			msg.setData(mBundle);
			msg.sendToTarget();
			break;
		case TGLibEKG.MSG_CARDIOZONE_HEARTRATE_TS:
			Log.i(TAG, "HR_TS: " + data);
			break;
		case TGLibEKG.MSG_CARDIOZONE_HRV:
			Log.i(TAG, "HRV: " + data);
			if ((Integer) data > 999)
				data = 999;
			Message msg1 = Message.obtain(mHandler, MSG_HRV);
			mBundle.putString("MSG_HRV", data.toString());
			msg1.setData(mBundle);
			msg1.sendToTarget();

			break;
		case TGLibEKG.MSG_CARDIOZONE_HRV_TS:
			Log.i(TAG, "HRV_TS: " + data);

			break;
		case TGLibEKG.MSG_EKG_RRINT:
			Log.i(TAG, "rrInt: " + data);
			int rrData = (Integer) data;
			if (rrData > 999)
				rrData = 999;
			Message msg10 = Message.obtain(mHandler, MSG_RR_INTERVAL);
			mBundle.putString("MSG_RR_INTERVAL", rrData + "");
			msg10.setData(mBundle);
			msg10.sendToTarget();
			// tv_RR_length.setText(data+"");
			if (Rnum < 64) {
				if ((Integer) data != 0) {
					try {

						Rnum++;
						Log.i(TAG, "Rnum: " + Rnum);
						isAF = AFcode.get_result(60000 / (Integer) data);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg2 = Message.obtain(mHandler, MSG_PERCENT);
					mBundle.putInt("MSG_PERCENT", Rnum);
					msg2.setData(mBundle);
					msg2.sendToTarget();
				}

			} else {

				// Log.v(TAG, "HRTemp: "+HRtemp);
				if (isAF) {
					Message msg2 = Message.obtain(mHandler, MSG_RR);
					mBundle.putString("MSG_RR", "节律\n异常");
					msg2.setData(mBundle);
					msg2.sendToTarget();
				} else {
					Message msg2 = Message.obtain(mHandler, MSG_RR);
					mBundle.putString("MSG_RR", "节律\n正常");
					msg2.setData(mBundle);
					msg2.sendToTarget();
				}
				mDeviceManagerService.stopRealTimeEKG();
				Message msg2 = Message.obtain(mHandler, MSG_PERCENT);
				mBundle.putInt("MSG_PERCENT", Rnum);
				msg2.setData(mBundle);
				msg2.sendToTarget();
			}

			// tv_RR.setText("RR_INTERVAL:"+data.toString());
			break;
		case TGLibEKG.MSG_EKG_RRINT_TS:
			Log.i(TAG, "rrInt_TS: " + data);
			break;
		case TGLibEKG.MSG_MOOD_METER:
			// Log.i(TAG, "Mood: " + data);
			Message msg3 = Message.obtain(mHandler, MSG_MOOD);
			mBundle.putInt("MSG_MOOD", (Integer) data);
			msg3.setData(mBundle);
			msg3.sendToTarget();
			break;
		case TGLibEKG.MSG_MOOD_METER_TS:
			Log.i(TAG, "Mood_TS: " + data);
			break;
		case TGLibEKG.MSG_SMOOTHED_WAVE:
			// Log.i(TAG, "Smoothed RAW" + data);
			ECGdata[index] = (Integer) data;
			index++;
			if (index >= 512) {
				index = 0;
				Message msg4 = Message.obtain(mHandler, MSG_ECG);
				// mBundle.putInt("MSG_ECG", (Integer)data);
				mBundle.putIntArray("MSG_ECG", ECGdata);
				msg4.setData(mBundle);
				msg4.sendToTarget();
			}
			// dataProcess((Integer) data);
			break;
		case TGLibEKG.MSG_HEART_LEVEL:
			Log.i(TAG, "trainingZone: " + data);
			Message msg5 = Message.obtain(mHandler, MSG_TRAININGZONE);
			mBundle.putString("MSG_TRAININGZONE", data.toString());
			msg5.setData(mBundle);
			msg5.sendToTarget();
			// tv = (TextView)findView(R.id.textview_TRAINING_ZONE);
			// tv_TraningZone.setText("TRAINING_ZONE:"+data.toString());
			break;
		case TGLibEKG.MSG_TRAINING_ZONE_TS:
			Log.i(TAG, "trainingZone_TS" + data);
			break;
		}
	}

	private void ekgStop(int result, int finalHR) {
		ecgWaveFlag = true;
		showWaveWithoutECG();
		if (recordname != null) {
			String sdpath = Environment.getExternalStorageDirectory().getPath();
			String appName = getResources().getString(R.string.app_name);
			File mfile = new File(sdpath + "//" + appName + "//" + recordname + ".txt");// ace
																						// output
			Reader reader = null;
			BufferedReader br = null;
			String datatemp;
			StringBuilder rawData = new StringBuilder();
			try {
				reader = new FileReader(mfile);
				br = new BufferedReader(reader);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				while ((datatemp = br.readLine()) != null) {
					rawData.append("" + datatemp + ",");
				}
			} catch (IOException e) {
				// TODO
				// Auto-generated
				e.printStackTrace();
			}

			String hr = FinalHR;
			// 没有数据返回
			if (hr.equals("null") || hr == null || hr.equals("0")) {
				return;
			}
			ecgDataList.add(ecgData);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).InsertECGData(ecgDataList, today.getTimeInMillis(),
					false);
			uploadInit(rawData.toString(), hr, isAF, ecgData.createtime, simpleresult, ecgData.data.hrv,
					ecgData.data.mood);
		}
		ecgDataList.clear();
		// ecgData=null;
		Message msg = Message.obtain(mHandler, MSG_STATUS);
		mBundle.putString("MSG_STATUS", "测量完毕");
		msg.setData(mBundle);
		msg.sendToTarget();
		Log.i(TAG, "EKG result: " + result);
		Log.i(TAG, "EKG final HR " + finalHR);

	}

	String recordname = null;

	protected void showWave() {
		try {
			// Create a directory if there is no SD card
			String appName = getResources().getString(R.string.app_name);
			// String appName = "ishang_wan";
			String dirPath = Environment.getExternalStorageDirectory() + "/" + appName;
			File dir = new File(dirPath);
			// String state = Environment.getExternalStorageState();
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "新建文件夹");

				}
			}
			StringBuilder fileName = new StringBuilder();
			fileName.append(DateFormat.format("yyyy-MM-dd HH-mm-ss", System.currentTimeMillis()));
			recordname = fileName.toString();
			fileName.append(".txt");
			File outputFile = new File(dirPath, fileName.toString());
			if (!outputFile.exists()) {
				if (outputFile.createNewFile()) {
					Log.v(TAG, "新建文件");
				} else {
					Log.v(TAG, "新建文件失败");
				}
			}
			FileOutputStream fis = new FileOutputStream(outputFile);
			mCurrentFile = new PrintWriter(fis);

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private CenterRollingBallECG mCenterRollingBall;
	private RelativeLayout mRelativeLayoutProgress;
	private TextView mTextViewStepPercent;

	private void addProgressView(int max, int value) {
		mCenterRollingBall = new CenterRollingBallECG(mActivity);
		mCenterRollingBall.setPics(ConstantsBitmaps.mBitmapBgCenterRoundEcgNew, ConstantsBitmaps.mBitmapPointRound);
		mCenterRollingBall.setRing(true);
		// mCenterRollingBall.setAlpha(10);
		// mCenterRollingBall.setRing(false);
		mCenterRollingBall.setLocationX(0.95f);
		mCenterRollingBall.setLocationY(0.95f);
		mCenterRollingBall.setRadius(0.85f);// 0.82f
		mCenterRollingBall.setAngle(-90);
		// mCenterRollingBall.setFirstLayer(Color.RED);
		mCenterRollingBall.setMaxScore(63);
		mCenterRollingBall.setScore(value);
		mCenterRollingBall.invalidate();
		mRelativeLayoutProgress.removeAllViews();
		mRelativeLayoutProgress.addView(mCenterRollingBall);
		if (mTextViewStepPercent == null)
			mTextViewStepPercent = findView(R.id.textview_percent);
		mTextViewStepPercent.setText("测量进度 " + value * 100 / 64 + "%");
	}

	private void saveProcess(int[] ecgRawData) {
		Log.v(TAG, "接收的数组长度为：" + ecgRawData.length);
		// Log.v(TAG,ecgRawData.toString());
		Log.v("BLETest", "线程 " + threadFlag + " 处理开始");
		int ecgdata = 0;
		for (int i = 0; i < ecgRawData.length; i++) {
			ecgdata = ecgRawData[i];
			buff.delete(0, buff.length());
			buff.append(String.valueOf(ecgdata));
			mCurrentFile.println(buff.toString());
			mCurrentFile.flush();
		}
		Log.v("BLETest", "线程 " + threadFlag + " 处理结束");

	}

	private void dataProcess(int[] ecgRawData) {
		// Log.v(TAG,"接收的数组长度为："+ecgRawData.length);
		// Log.v(TAG,ecgRawData.toString());
		Log.v("BLETest", "线程 " + threadFlag + " 处理开始");
		int ecgdata1 = 0;
		for (int i = 0; i < ecgRawData.length; i++) {
			ecgdata1 = (i % 2 == 0) ? ecgRawData[i] : (-ecgRawData[i]);
			gvSeries1.appendData(new GraphViewData(graph2LastXValue, ecgdata1), true, ECGlength);
			graph2LastXValue++;

		}
		Log.v("BLETest", "线程 " + threadFlag + " 处理结束");
	}

	/**
	 * 初始化心电图显示view
	 */
	private void graphInit() {
		mGraph1 = new LineGraphView(mActivity // context
				, "" // heading
		);
		mGraph1.setViewPort(0, ECGlength);
		mGraph1.setScalable(true);
		((LineGraphView) mGraph1).setDrawBackground(false);
		gvSeries1 = new GraphViewSeries(new GraphViewData[] {});
		mGraph1.addSeries(gvSeries1);
		// RelativeLayout layout = (RelativeLayout)
		// findView(R.id.ecg_view_line);
		layout.removeAllViews();
		layout.addView(mGraph1);
		// 初始化显示
		tv_HR.setText("0");
		tv_TraningZone.setText("--");
		tv_RR_length.setText("--");
		tv_HRV.setText("--");
		mGraph1.setFocusable(false);
		mGraph1.setOnClickListener(null);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initDialog(dialogHead[4], dialogText[4]);
				showDeviceDialog();
			}
		});
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}

	/**
	 * uploadInit(上传数据格式化) TODO(这里描述这个方法适用条件 – 可选) TODO(这里描述这个方法的执行流程 – 可选)
	 * TODO(这里描述这个方法的使用方法 – 可选) TODO(这里描述这个方法的注意事项 – 可选)
	 * 
	 * @param
	 * @return void
	 * @Exception 异常对象
	 * @创建人：WB
	 * @创建时间：2014-3-27 下午2:05:58
	 * @修改人：WB
	 * @修改时间：2014-3-27 下午2:05:58
	 */
	private int dataFromPHP;
	private String myGwUrl = Constant.getGwUrl();

	/**
	 * 上传心电数据
	 * 
	 * @param rawData
	 *            原始数据
	 * @param hr
	 *            心律
	 * @param isAF
	 *            是否房颤
	 * @param recordtime
	 *            检测时间
	 * @param result
	 *            检测结果（心律正常）
	 * @param hrv
	 *            心律变异率
	 * @param mood
	 *            压力
	 */
	public void uploadInit(String rawData, String hr, boolean isAF, String recordtime, String result, String hrv,
			String mood) {
		EcgJsonCreat mJsonCreat = new EcgJsonCreat();
		Log.e("Lianxw", "upload upload............");
		String afResult = "";
		String measureTime = "";
		String collectDate = "";
		String deviceId = "";
		mUploadlist = new ArrayList<NameValuePair>();

		if (isAF)
			afResult = "房颤";
		else
			afResult = "未发现房颤";
		measureTime = recordtime;
		collectDate = recordtime.substring(0, 10);

		mJsonCreat.appJson(rawData, "" + hr, afResult, "", result, measureTime, hrv, mood);

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);

		deviceAddress = info.getString(SharedPreferredKey.DEVICE_ADDRESS, null); // 设备类型
		// String mPhoneNum = data.getString(SharedPreferredKey.PHONENUM, null);
		// // 拿到电话号码
		deviceId = "01" + deviceAddress;// wy for cmri

		Logger.i("111", deviceId);
		mJsonCreat.httpJson(collectDate, deviceId);
		mUploadlist = mJsonCreat.jsonsend();
		Log.v(TAG, "发送的数据" + hr + afResult + result + measureTime);
		Log.v(TAG, "发送的数据：" + mUploadlist.get(0).getValue().substring(mUploadlist.get(0).getValue().length() - 200));
		// Log.v(TAG, "发送的数据：" + mUploadlist.toString());

		new Thread() {
			public void run() {
				myGwUrl = Config.ECG_UPLOAD_URL;
				// "http://111.11.29.83:8099/DADS_HTTP/service/uploadEcgDatas";
				SimplePostInfo ecginfo = new SimplePostInfo();
				dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, ecginfo, mUploadlist);
				// myNetTool.postDataFromNet(myGwUrl, ecginfo,mUploadlist);
				Log.v(TAG, "服务器返回结果： " + dataFromPHP);
				if (dataFromPHP == 0) {
					Message msg = Message.obtain(mHandler, MSG_STATUS);
					mBundle.putString("MSG_STATUS", "数据上传成功");
					msg.setData(mBundle);
					msg.sendToTarget();
				} else {
					Message msg = Message.obtain(mHandler, MSG_STATUS);
					mBundle.putString("MSG_STATUS", "数据上传失败");
					msg.setData(mBundle);
					msg.sendToTarget();
				}

			};
		}.start();
	}

	private Runnable _l_b_fail_scan = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "Time Out, Can't find devices.");
			if (_l_b_fail_scan != null) {
				Log.i(TAG, "Disable the timer for the fail scan.");
				handler.removeCallbacks(_l_b_fail_scan);
			}
			if (hasBleException) {
				Message msg = Message.obtain(mHandler, MSG_STATUS);
				mBundle.putString("MSG_STATUS", "未找到手环设备");
				msg.setData(mBundle);
				msg.sendToTarget();
				mDeviceManagerService.disConnect();
				// tgBleManager.disconnect();//wy
			} else {
				Message msg = Message.obtain(mHandler, MSG_STATUS);
				mBundle.putString("MSG_STATUS", "未找到手环设备");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
	};

	private void showWaveWithoutECG() {
		mTimer2 = new Runnable() {
			@Override
			public void run() {
				if (ecgWaveFlag) {
					if (null == gvSeries1) {
						return;
					}
					gvSeries1.appendData(new GraphViewData(graph2LastXValue, 100), true, ECGlength);
					graph2LastXValue += 4;
					mHandlerTemp.postDelayed(this, 1);
				}
			}
		};
		mHandlerTemp.postDelayed(mTimer2, 1);
	}

	// -------------------------------------BUG
	// #3276----------------------------------------------
	private CustomProgressDialog progressDialog;
	private boolean buttonClick = false;

	/**
	 * 加载提示
	 */
	private void showProgress(String msg) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		if (null == mActivity) {
			return;
		}
		progressDialog = CustomProgressDialog.createDialog(mActivity, false);
		progressDialog.setMessage(msg);
		progressDialog.show();
		mbleAlertHandler.postDelayed(myStopProgressRunnable, 30 * 1000);
	}

	/**
	 * 关闭加载提示
	 */
	private void closeProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	Runnable myStopProgressRunnable = new Runnable() {
		@Override
		public void run() {
			closeProgress();
			Toast.makeText(mActivity, "手环连接失败", Toast.LENGTH_LONG).show();
		}
	};
}
