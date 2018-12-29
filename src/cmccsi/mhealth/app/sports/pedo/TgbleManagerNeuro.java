package cmccsi.mhealth.app.sports.pedo;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.ObjectUtils.Null;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cmcc.mhealth.ble.BasePedometorSummary;
import cmcc.mhealth.ble.BasePedometorSummary.PedometorSession;
import cmcc.mhealth.ble.BasePedometorSummary.PedometorSubData;
import cmccsi.mhealth.app.sports.bean.DataDetailPedo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.bean.SaveDeviceToken;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;

import com.neurosky.ble.TGBleManager;
import com.neurosky.ble.TGBleManagerCallback;

public class TgbleManagerNeuro {
	
	private String TAG="TgbleManagerNeuro";
	private boolean islog=true;
	private Context mContext;
	private Handler mAlertHandler=null;
	private BluetoothAdapter mBluetoothAdapter;
	private TGBleManager tgBleManager;
	
	private PedometorDataInfo bluetoothPedo;// 当前页面显示的数据
	
	private int option = 0;
	
	private int connectStatus=0;//连接状态0初始状态 1正在连接 2正在操作
	
	/**
	 * 设置连接后模式 0不做任何事，1上传运动数据，2心电，3实时运动，
	 * TODO
	 * @param option
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:21:26
	 */
	public void setOption(int option) {
		Logger.d(TAG, "setOption"+option);
		this.option = option;
	}


	private boolean isRealTime=true;//手环实时显示开关
	private boolean isRealActivitiy=true;

	private BluetoothDevice bt;
	private String mDeviceAdress = "";// 设备id
	private Bundle mBundle = new Bundle();
	private TimerUpdateECGPedo mTimerUpdateECGPedo = null;
	
	public static final int MSG_SUCCESS = 1001;
	public static final int MSG_TRANSPORT = 1002;	
	public static final int MSG_STATUS = 1003;
	public static final int MSG_LOST = 1004;
	public static final int MSG_POWER = 1007;
	public static final int MSG_BUSY = 1009;
	public static final int MSG_CURRENTCOUNT = 1011;
	public static final int MSG_FAILED = 1012;
	public static final int MSG_EXCEPT = 1013;
	public static final int MSG_CONNECTED = 1014;
	public static final int MSG_RESET = 1015;
	public static final int MSG_EKGSTOP = 1016;
	public static final int MSG_DATARECEIVED = 1017;
	
	private static TgbleManagerNeuro SINGLEINSTANCE = null;
	public static TgbleManagerNeuro getSingleInstance(Context context){
		if(SINGLEINSTANCE == null){
			SINGLEINSTANCE = new TgbleManagerNeuro(context);
		}
		return SINGLEINSTANCE;
	}
	
	private TgbleManagerNeuro(Context mContext)
	{

		this.mContext = mContext;
		init();
	}

	/**
	 * 设置是否在实时显示页面（是否开启实时计步）
	 * TODO
	 * @param isRealActivitiy
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午1:35:00
	 */
	public void setRealActivitiy(boolean isRealActivitiy) {
		this.isRealActivitiy = isRealActivitiy;
	}
	
	private  void init(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		tgBleManager = TGBleManager.getInstance();
		tgBleManager.initTGBleManager(mContext, mBluetoothAdapter, bleCallback);
		tgBleManager.setupTGBleManager(true);
//		tgBleManager.enableLogging();
	}
	
	/**
	 * 手环连接
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:19
	 */
	public void content(String deviceAddress) {
		this.mDeviceAdress = deviceAddress.substring(2);
		content();
	}
	/**
	 * 手环连接
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:19
	 */
	public void content() {
		initNeroBLE();
		
		if(islog) Logger.d(TAG, TAG+"content" +"option:"+option);
		
		Logger.d(TAG, "---mDeviceAdress " + mDeviceAdress);
		Logger.d(TAG, "---mDeviceAdress.equals() " + mDeviceAdress.equals(""));
		mDeviceAdress = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ADDRESS, null);
		Logger.d(TAG, "---mDeviceAdress " + mDeviceAdress);
//		mDeviceAdress = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ADDRESS, null);
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Toast.makeText(mContext, "手机蓝牙不可用", Toast.LENGTH_LONG).show();
		} else {
			if(connectStatus==0)// 未连接  进入连接中状态
			{
				connectStatus=1;
			}
			else if(connectStatus == 2){ // 已连接， 但是不return， 要后面发送CONNECTED消息
				
			}else 
			{
				return;
			}
			
			Logger.d(TAG, "tgBleManager isRealTime" + isRealTime	+ " |" + tgBleManager.getConnectEvent().toString());
			
			if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
				Logger.d(TAG, "tgBleManager.startRealTimeSport();");
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_CONNECTED);
					mBundle.putString("MSG_CONNECTED", "连接成功");
					msg.setData(mBundle);
					msg.sendToTarget();
				}
//				isRealTime = true;
				connectStatus = 2;
//			} else if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Connected")) {
//				if (tgBleManager.getBondToken() == null) {
//					tgBleManager.tryBond();
//				} else {
//					tgBleManager.adoptBond(tgBleManager.getBondToken(), tgBleManager.getHwSerialNumber());
//				}
			} else {
				if (mDeviceAdress != null&&BluetoothAdapter.checkBluetoothAddress(mDeviceAdress)) {
					bt = mBluetoothAdapter.getRemoteDevice(mDeviceAdress);
					
					if (bt != null) {
						try {
							Logger.i(TAG, "candidateConnect "+"option"+option);
							//tgBleManager.close();
							tgBleManager.candidateConnect(bt);
						} catch (Exception e) {
							e.printStackTrace();
							connectStatus= 0;
							if(mAlertHandler!=null)
							{
								Message msg = Message.obtain(mAlertHandler, MSG_EXCEPT);
								mBundle.putString("MSG_EXCEPT", "手环连接错误");
								msg.setData(mBundle);
								msg.sendToTarget();
							}
						}
					} else {
						Logger.i("TgbleManager", "Peripheril does not exist!");
					}
				} else {
					connectStatus = 0; // 地址不合法 状态归0
//					initNeroBLE();
				}
			}
		}
	}
	
	public void getPedometerData()
	{

		if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
			try {
//				if(connectStatus==0)
//				{
//					connectStatus=2;
//				}
//				else
//				{
//					return;
//				}
				if(isRealActivitiy){
					tgBleManager.stopRealTimeSport();
					option=0;
				}
				Log.e("lkh", "从手环获取数据！");
				tgBleManager.startSyncData();
			} catch (Exception e) {
				e.printStackTrace();
				connectStatus=0;
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_EXCEPT);
					mBundle.putString("MSG_EXCEPT", "手环连接错误");
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}
		}
		else
		{
			if(islog) Logger.d(TAG, "getPedometerData  content");
			option=1;
			content();
		}
	}
	
	/**
	 * 开启手环实时数据
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:32:32
	 */
	public void startRealTimeSport()
	{
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			if(islog) Logger.d(TAG, "startRealTimeSport  startRealTimeSport| "+tgBleManager.getConnectEvent().toString());
			tgBleManager.startRealTimeSport();
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_CONNECTED);
				mBundle.putString("MSG_CONNECTED", "连接成功");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
		else
		{
			if(islog) Logger.d(TAG, "startRealTimeSport  content|"+tgBleManager.getConnectEvent().toString());
			option=3;
			content();
		}
	}
	
	/**
	 * 停止手环实时数据显示
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:32:48
	 */
	public void stopRealTimeSport()
	{
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			tgBleManager.stopRealTimeSport();
		}
	}
	
	/**
	 * 心电测量
	 */
	public void startRealTimeEKG(){
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			if(islog) Logger.d(TAG, "startRealTimeEKG  startRealTimeEKG| "+tgBleManager.getConnectEvent().toString());
			tgBleManager.startRealTimeEKG();
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_STATUS);
				String position=PreferencesUtils.getString(mContext, SharedPreferredKey.POSITION, "左手");
				if(position.equals("右手")){
					mBundle.putString("MSG_STATUS", "正在测量，请"+position+"佩戴手环，左手食指放在金属片上");
				}else{
					mBundle.putString("MSG_STATUS", "正在测量，请"+position+"佩戴手环，右手食指放在金属片上");
				}				
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
		else
		{
			if(islog) Logger.d(TAG, "startRealTimeSport  content|"+tgBleManager.getConnectEvent().toString());
			option=2;
			content();
		}
	}
	
	/**
	 * 停止手环心电测量
	 */
	public void stopRealTimeEKG()
	{
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			tgBleManager.stopRealTimeEKG();
		}
	}
	
	/**
	 * 关闭连接
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午1:32:41
	 */
	public void close()
	{
		if(tgBleManager!=null)
		{
			tgBleManager.close();
		}
	}
	
	public void dismissHandle()
	{
		this.mAlertHandler=null;
	}
	
	public void setHandle(Handler mAlertHandler)
	{
		this.mAlertHandler=mAlertHandler;
	}

	/**
	 * 手环参数初始化
	 * TODO
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:45
	 */
	public void initNeroBLE() {
		//tgBleManager.setupTGBleManager(true);
		// set user profile & goal & alarm
		try {
			if (PreferencesUtils.getString(mContext, SharedPreferredKey.GENDER, "1").equals("1")) {
				tgBleManager.setFemale(false);
			} else {
				tgBleManager.setFemale(true);
			}
			tgBleManager.setHeight(Integer.valueOf(PreferencesUtils.getString(mContext, SharedPreferredKey.HEIGHT, "175")));
			String weight = PreferencesUtils.getString(mContext, SharedPreferredKey.WEIGHT, "70");
			int temp = weight == "" ? 0 : (int) (Math.round(Float.valueOf(weight)));
			tgBleManager.setWeight(temp);
			if (PreferencesUtils.getString(mContext, SharedPreferredKey.POSITION, "左手").equals("左手")) {
				tgBleManager.setBandLocation(true);
			} else {
				tgBleManager.setBandLocation(false);
			}
			tgBleManager.setWalkingStepLength(0);//
			tgBleManager.setRunningStepLength(0);//开启自动判断步长
			String birthday = PreferencesUtils.getString(mContext, SharedPreferredKey.BIRTHDAY, "1990-01-01");
			String[] birth = birthday.split("-");
			int birthYear = Integer.parseInt(birth[0]);
			int birthMonth = Integer.parseInt(birth[1]);
			int birthDay = Integer.parseInt(birth[2]);
			// int birthMonth=Integer.parseInt(birthday.substring(6,8));
			// int birthDay=Integer.parseInt(birthday.substring(10,12));
			tgBleManager.setBirthYear(birthYear);
			tgBleManager.setBirthMonth(birthMonth);
			tgBleManager.setBirthDay(birthDay);
			tgBleManager.setGoalSteps(Integer.valueOf(PreferencesUtils.getString(mContext, SharedPreferredKey.TARGET_STEP,
					"10000")));
			boolean enableSport = PreferencesUtils.getBoolean(mContext, SharedPreferredKey.ENABLE_SPORT, false);
			boolean enableSleep = PreferencesUtils.getBoolean(mContext, SharedPreferredKey.ENABLE_SLEEP, false);

			int sportHour = PreferencesUtils.getInt(mContext, SharedPreferredKey.SPORT_HOUR, 17);
			int sportMinute = PreferencesUtils.getInt(mContext, SharedPreferredKey.SPORT_MINUTE, 30);
			int sleepHour = PreferencesUtils.getInt(mContext, SharedPreferredKey.SLEEP_HOUR, 17);
			int sleepMin = PreferencesUtils.getInt(mContext, SharedPreferredKey.SLEEP_MINUTE, 30);

			int sleepRepeat = PreferencesUtils.getInt(mContext, SharedPreferredKey.DAYS, 1);

			if (!enableSport) {
				tgBleManager.setGoalDurationHour(Integer.valueOf(25));
			} else {
				tgBleManager.setGoalDurationHour(sportHour);// 25 close
				tgBleManager.setGoalDurationMinute(sportMinute);
				tgBleManager.setGoalDurationSecond(0);
			}
			tgBleManager.setDisplayImperialUnits(false);
			tgBleManager.setDisplayTime24Hour(true);

			if (!enableSleep) {
				tgBleManager.setAlarmHour(25);
			} else {
				tgBleManager.setAlarmRepeat(sleepRepeat);
				tgBleManager.setAlarmHour(sleepHour);
				tgBleManager.setAlarmMinute(sleepMin);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	
	/**
	 * TODO
	 * 蓝牙手环回调
	 */
	public TGBleManagerCallback bleCallback=new TGBleManagerCallback(){
		
		//电池电量（确定手环已连接）
		@Override
		public void batteryLevel(int percent) {
			connectStatus=2;
			if (percent < 30) {
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_POWER);
					mBundle.putInt("MSG_POWER", percent);
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}
			Log.i(TAG, "Battery Level: " + percent+ ",option= " + option);
			switch (option) {
			case 1:
				
				Logger.d("TgbleManager", "batteryLevel startSyncData-------");
//				if(mAlertHandler!=null)
//				{
//					Message msg = Message.obtain(mAlertHandler, MSG_STATUS);
//					mBundle.putString("MSG_STATUS", "正在同步");
//					msg.setData(mBundle);
//					msg.sendToTarget();
//				}
				tgBleManager.startSyncData();
				option = 0;
				break;
			case 2:
				Log.i("TgbleManager", "batteryLevel startRealTimeEKG-------");
				tgBleManager.startRealTimeEKG();
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_STATUS);
					mBundle.putString("MSG_STATUS", "正在测量");
					msg.setData(mBundle);
					msg.sendToTarget();
				}
				option = 0;
				break;
			case 3:
				Logger.d("TgbleManager", "batteryLevel startRealTimeSport -------");
				tgBleManager.startRealTimeSport();//tgBleManager.askCurrentCount();
				option = 0;
				break;
			case 4:
				tgBleManager.eraseData();
				option = 0;
				break;
//			case 5:
//				tgBleManager.fwDown();
//
//				option = 0;
//				break;
			default:

				option = 0;
				break;
			}	
			
		}

		@Override
		public void bleDidAbortConnect() {
			connectStatus = 0;
		}

		//手环已连接的回调
		@Override
		public void bleDidBond(int result) {
			Log.i(TAG, "Bond result: " + result+"option: "+option);
			
			String msgresult="";
			switch (result) {
			case 0:
				if(mAlertHandler!=null)
				{
					Message msg1 = Message.obtain(mAlertHandler, MSG_CONNECTED);
					mBundle.putString("MSG_CONNECTED", "连接成功");
					msg1.setData(mBundle);
					msg1.sendToTarget();
				}
				String deviceToken = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_TOKEN, null);
				String deviceVersion = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_VERSION, null);
				Logger.d("cjz", "固件版本 version "+tgBleManager.getFwVersion());
				if(deviceToken == null || deviceVersion == null || !deviceVersion.equals(tgBleManager.getFwVersion()))
					
				{
					Logger.d(TAG, "---save token");
					Editor editor = mContext.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
	    					Context.MODE_PRIVATE).edit();
	        		editor.putString(SharedPreferredKey.DEVICE_TOKEN, tgBleManager.getBondToken());
	    			editor.putString(SharedPreferredKey.DEVICE_NUMBER, tgBleManager.getHwSerialNumber());
	    	        editor.commit();
	    	        saveDeviceTokenInfo(tgBleManager.getBondToken(),tgBleManager.getHwSerialNumber(),tgBleManager.getFwVersion());
				}
//				connectStatus = 2;
				break;
			case 5:
				msgresult="手环没有连接";
				break;
			case 7:
				msgresult="手环连接超时";
				break;
			case 9://exception会提示
				msgresult="";
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_RESET);
					mBundle.putString("MSG_RESET", "手环已与其他设备绑定，请reset手环");
					msg.setData(mBundle);
					msg.sendToTarget();
				}
				break;
			case 12:
				msgresult="请检查您的设备是否支持此功能";
				break;
			default:
				msgresult="手环连接失败，请重新配对";
				break;
			}
			if(msgresult!="")
			{
				connectStatus = 0;
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_EXCEPT);
					mBundle.putString("MSG_EXCEPT", msgresult);
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}
			
		}

		//手环连接
		@Override
		public void bleDidConnect() {
			Logger.d(TAG, "bleDidConnect:"+ "option"+option);
			String deviceToken = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_TOKEN, null);
			String deviceSN = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_NUMBER, null);
			if(deviceToken!=null&&deviceToken!="")
			{
				Logger.d(TAG, deviceToken+"---deviceToken---");
				tgBleManager.adoptBond(deviceToken, deviceSN);
			}
			else
			{
				if(tgBleManager.getBondToken() == null ){
					tgBleManager.tryBond();
				}else{
					Logger.d(TAG,"Token:"+tgBleManager.getBondToken()+" HwSerial:"+tgBleManager.getHwSerialNumber());
					tgBleManager.adoptBond(tgBleManager.getBondToken(), tgBleManager.getHwSerialNumber());
				}
			}
		}

		//断开连接
		@Override
		public void bleDidDisconnect() {
			if(islog) Logger.d(TAG, "bleDidDisconnect");
			tgBleManager.stopRealTimeSport();
			connectStatus=0;
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_LOST);
//				mBundle.putString("MSG_LOST", "手环连接断开");
//				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void bleDidSendAlarmGoals(int result) {
			
		}

		@Override
		public void bleDidSendUserConfig(int result) {
			
		}

		//连接丢失
		@Override
		public void bleLostConnect() {
			
			if(islog) Logger.d(TAG, "bleLostConnect");
			connectStatus=0;
			tgBleManager.disconnect();
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_LOST);
				mBundle.putString("MSG_LOST", "手环连接丢失");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void candidateFound(BluetoothDevice device, int rssi,String mfgID) {
			
		}

		//实时运动数据
		@Override
		public void currentCount(Date time, int steps, int calories,int actCalories,
				int distance, int energy, int mode, int hr) {
			Log.i(TAG, "currentCount steps: " + steps);
			Log.i(TAG, "currentCount calories: " + calories);
			Log.i(TAG, "currentCount distance: " + distance);
			Log.i(TAG, "currentCount energy: " + energy);
			Log.i(TAG, "currentCount mode: " + mode);
			Log.i(TAG, "currentCount heart rate: " + hr);

			//***********************************************
			if(bluetoothPedo==null||!bluetoothPedo.date.equals(DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline)))
			{
				bluetoothPedo=new PedometorDataInfo();
				bluetoothPedo.createtime=DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
				bluetoothPedo.date=DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline);
				bluetoothPedo.deviceId="01"+mDeviceAdress;
			}
			
			bluetoothPedo.stepNum=steps+"";
			bluetoothPedo.cal=actCalories+"";
			bluetoothPedo.distance=distance+"";
			bluetoothPedo.strength2=mode+"";
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_CURRENTCOUNT);
				
				mBundle.putString("stepSum", steps+"");
				mBundle.putString("calSum", actCalories+"");
				mBundle.putString("distanceSum", distance+"");
				mBundle.putString("stepTime", mode+"");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void dataReceived(int key, Object data) {
			//Logger.d(TAG, "dataReceived key:"+key +"dataReceived data"+data);
			if(mAlertHandler != null) {
				Message msg=Message.obtain(mAlertHandler,MSG_DATARECEIVED);
				Bundle tempbundle=new Bundle();
				tempbundle.putInt("EKGSTOP_key", key);
				msg.obj = data;
				msg.setData(tempbundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void diagEventRecord(Date time, int eventCode) {
			
		}

		@Override
		public void ekgSample(int sample) {
			
		}

		@Override
		public void ekgStarting(Date time, int sampleRate, boolean realTime,String comment) {
			
		}

		@Override
		public void ekgStop(int result, int finalHR) {
			if(mAlertHandler != null) {
				Message msg=Message.obtain(mAlertHandler,MSG_EKGSTOP);
				mBundle.putInt("EKGSTOP_RESULT", result);
				mBundle.putInt("EKGSTOP_FINALHR", finalHR);
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void eraseComplete(int arg0) {
			tgBleManager.startRealTimeSport();
			Message msg = Message.obtain(mAlertHandler, MSG_SUCCESS);
			mBundle.putString("MSG_FAILED", "数据上传失败");
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void exceptionMessage(int exceptEvent) {
			Logger.d(TAG, "exceptEvent: "+exceptEvent);
			connectStatus=0;
//			if(mAlertHandler!=null)
//			{
//				Message msg=Message.obtain(mAlertHandler,MSG_EXCEPT);
//				mBundle.putString("MSG_EXCEPT", "手环连接错误");
//				msg.setData(mBundle);
//				msg.sendToTarget();
//			}
			 Log.i(TAG, "exceptionMessage result: " + exceptEvent);
			 Log.i(TAG, "kankan: " + exceptEvent);
				String msgresult="";
				switch (exceptEvent) {
				case 4:
					msgresult="年龄不在正确范围之内";
					break;
				case 5:
					msgresult="5";//手环在忙别的事情
					break;
				case 6:
					msgresult="目标步数不在正确范围之内";
					break;
				case 7:
					msgresult="获取实时数据失败";
					break;
				case 105:
					msgresult="蓝牙出现错误，请检查手机蓝牙";
					break;
				case 107:
//					msgresult="手环连接超时";
					break;
				case 304://新连接正常提示
					msgresult="";
					
					break;
				case 301://
					msgresult="";
					
					break; 
				case 303:
					msgresult="";
					
					break;
				case 99:
					msgresult="";
					
					break; 
				default:
					msgresult="手环连接失败";
					break;
				
			}
			if(msgresult!=""&&!msgresult.equals("")&&!"5".equals(msgresult))
			{
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_EXCEPT);
					mBundle.putString("MSG_EXCEPT", msgresult);
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}else if ("5".equals(msgresult)) {
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_BUSY);
					mBundle.putString("MSG_EXCEPT", msgresult);
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}
		}

		@Override
		public void fatBurnRecord(Date time, int heartRate, int fatBurn,int trainingZone) {
			
		}

		@Override
		public void fwUpdateInProgress(boolean transfer) {
			
		}

		@Override
		public void fwUpdatePercent(int percent) {
			Message msg = new Message();
			msg.what = TGBleManager.MSG_FW_TRANSFER_PERCENT;
			msg.arg1 = percent;
			msg.obj = null;
			try {
				mAlertHandler.sendMessage(msg);
			} catch (Exception e) {
				Logger.e(TAG, e.toString());
			}
		}

		@Override
		public void fwUpdateReport(int result, int size, int checksum, int transfer) {
            Logger.i(TAG, "--- result " + result);
			Message msg = new Message();
			msg.what = TGBleManager.MSG_FW_TRANSFER_REPORT;
			msg.arg1 = result;
			msg.obj = result;
			try {
				mAlertHandler.sendMessage(msg);
			} catch (Exception e) {
				Logger.e(TAG, e.toString());
			}
		}

		@Override
		public void pedometryRecord(Date time, int steps, int calories, int distance, 
				int activeCalories, int stepBPM, int energy, int mode, int sleepPhase,int sedentaryTime,
				int walkingTime,int runningTime) {
			
		}

		@Override
		public void potentialBond(String code, String sn, String devName) {
			tgBleManager.takeBond();
		}

		@Override
		public void sleepRecord(Date time, int phase, int code) {
			
		}

		@Override
		public void sleepResults(int arg0, Date arg1, Date arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
				int arg8, int arg9, int arg10, int arg11) {
			
		}

		@Override
		public void sleepSmoothData(int arg0, Date[] arg1, int[] arg2) {
			
		}

		@Override
		public void transferInProgress(boolean transfer) {
			
		}

		@Override
		public void transferPercent(int percent) {
			Log.i(TAG, "Sync percentage: " + percent);
			if (percent > 100) {
				percent = 100;
			}
			if(mAlertHandler!=null)
			{
				Message msg = Message.obtain(mAlertHandler, MSG_TRANSPORT);
				mBundle.putString("MSG_TRANSPORT", "传输进度:" + percent + "%");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		//获取运动数据
		@Override
		public void transferReport(int result, int ped_avail, int ped_recv, int ekg_avail, 
				int ekg_recv, int sleep_avail, int sleep_recv,int diag_avail,int diag_recv) {
			Log.i(TAG, "sync result: " + result);
			Log.i(TAG, "PED available: " + ped_avail);
			Log.i(TAG, "PED receive: " + ped_recv);
			Log.i(TAG, "EKG available: " + ekg_avail);
			Log.i(TAG, "EKG receive: " + ekg_recv);
			Log.i(TAG, "sleep available: " + sleep_avail);
			Log.i(TAG, "sleep receive: " + sleep_recv);
			Log.i(TAG, "diag available: " + diag_avail);
			Log.i(TAG, "diag receive: " + diag_recv);
			if (result==0){
				PedometorSession session = (PedometorSession) tgBleManager
						.getPedometorSession();
				Logger.d("cjz", "tgBleManager.getPedometorSession()");
				List<PedometorSubData> subDataList = session.pedSession;
				Logger.d("cjz", "subDataList = session.pedSession");
				PedometorListInfo pedoData = new PedometorListInfo();
				
				PedoDetailInfo detailDataInfo = new PedoDetailInfo();
				
				for (PedometorSubData pedSub : subDataList) {
					if (pedSub.flagtypeScale.equals("D")) {
						Log.i(TAG, "flagtypeScale:" + pedSub.flagtypeScale);
						List<BasePedometorSummary> basePedList = pedSub.data;
						for (BasePedometorSummary basePed : basePedList) {
							PedometorDataInfo dataPedometor = new PedometorDataInfo();
							//实时运-动数据大
							if(bluetoothPedo!=null&&Integer.parseInt(basePed.stepNum)<=Integer.parseInt(bluetoothPedo.stepNum))
							{
								dataPedometor.stepNum =bluetoothPedo.stepNum;
								dataPedometor.date =bluetoothPedo.date;
								dataPedometor.cal = bluetoothPedo.cal;
								dataPedometor.distance = bluetoothPedo.distance;
								dataPedometor.strength2 = bluetoothPedo.strength2;
							}
							else
							{
								dataPedometor.stepNum = basePed.stepNum;	
								dataPedometor.date = basePed.date;									
								dataPedometor.cal = basePed.cal;	
								dataPedometor.distance = basePed.distance;	
								dataPedometor.strength2 = String.valueOf(Integer.valueOf(basePed.strength1)*60) ;
							}
							dataPedometor.power = basePed.power;	
//							dataPedometor.strength2 = basePed.strength1;
//							运动时长是差量
							
							dataPedometor.strength3 = basePed.strength2;
							dataPedometor.strength1 = basePed.strength3;
							dataPedometor.strength4 = basePed.strength4;
							dataPedometor.deviceId="01"+mDeviceAdress;
							dataPedometor.createtime=DateFormatUtils.ChangeFormat(dataPedometor.date, FormatType.DateWithUnderline, FormatType.DateLong);
							pedoData.datavalue.add(dataPedometor);
							Logger.d(TAG, "获取数据"+dataPedometor.stepNum);
							PedoController.GetPedoController(mContext).insertOrUpdatePedometer(pedoData);
						}
					}
					if (pedSub.flagtypeScale.equals("H")) {
						//处理详细包
						List<BasePedometorSummary> basePedList = pedSub.data;

						for (BasePedometorSummary basePed : basePedList) {

							try {
								detailDataInfo.date = Common.FormatyyyyMMdd(basePed.date);
							} catch (ParseException e) {

								e.printStackTrace();
							}

							DataDetailPedo detailData = new DataDetailPedo();
							try {
								detailData.start_time = Common.FormatyyyyMMddHH(basePed.date).substring(8, 10);
								if (detailData.start_time.length() == 2
										&& detailData.start_time.substring(0, 1).equals("0")) {
									detailData.start_time = detailData.start_time.substring(1, 2);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							} catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
							}
							detailData.snp5 = basePed.stepNum + ",0,0,0,0,0,0,0,0,0,0,0";
							detailData.knp5 = basePed.cal + ",0,0,0,0,0,0,0,0,0,0,0";
							detailData.level2p5 = Integer.valueOf(basePed.strength1) * 60 + ",0,0,0,0,0,0,0,0,0,0,0";
							detailData.level3p5 = Integer.valueOf(basePed.strength2) * 60 + ",0,0,0,0,0,0,0,0,0,0,0";
							detailDataInfo.datavalue.add(detailData);

						}
						MHealthProviderMetaData.GetMHealthProvider(mContext).updatePedoDetailData(
								detailDataInfo);

					}
				}

				tgBleManager.eraseData(); // wy
				if(isRealActivitiy)
				{
//					if(!isRealTime)
//					{
//						isRealTime=true;
//						option=3;
//						tgBleManager.startRealTimeSport();
//					}
				}
				else
				{
					Logger.d("cjz", "transferReport close");
					tgBleManager.close();
				}
				new UploadPedoAsk(pedoData.datavalue,detailDataInfo).execute();
			}
			else
			{
				//取实时数据
				startRealTimeSport();
				if(mAlertHandler!=null)
				{
					Message msg = Message.obtain(mAlertHandler, MSG_FAILED);
					mBundle.putString("MSG_FAILED", "获取手环数据失败");
					msg.setData(mBundle);
					msg.sendToTarget();
				}
			}
//			connectStatus=0;
		}
		
	};
	
	class UploadPedoAsk extends AsyncTask<Null, Null, Integer> {

		private List<PedometorDataInfo> pedolist;
		private PedoDetailInfo pedoDetail;

		public UploadPedoAsk(List<PedometorDataInfo> pedolist, PedoDetailInfo pedoDetail) {
			this.pedolist = pedolist;
			this.pedoDetail = pedoDetail;
		}

		@Override
		protected Integer doInBackground(Null... params) {
			int isSucess = 0;// 0成功 -1失败 1没有数据
			try {
				if (pedolist==null||pedolist.size() == 0) {
					return 1;
				}
				for (PedometorDataInfo pedo : pedolist) {
					if(!UploadManager.uploadPedo(pedo))
					{
						isSucess=-1;
					}
				}
				if(!UploadManager.uploadPedoDetail(pedoDetail, pedolist.get(0).deviceId))
				{
					isSucess=-1;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// BaseToast("上传"+e.getMessage());
				return -1;
			}
			return isSucess;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);	
			if(mAlertHandler!=null&&result<0)
			{
				Message msg;
				msg = Message.obtain(mAlertHandler, MSG_FAILED);
				mBundle.putString("MSG_FAILED", "数据上传失败");
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

	}
	/**
	 * 启动自动上传手环数据timer
	 */
	public void startUploadTimer(){
		if(mTimerUpdateECGPedo == null){
			mTimerUpdateECGPedo = new TimerUpdateECGPedo(3600); 
		}
	}
	/**
	 * 取消自动上传手环数据timer
	 */
	public void cancleUploadTime(){
		if(mTimerUpdateECGPedo != null){
			mTimerUpdateECGPedo.cancle(); 
			mTimerUpdateECGPedo = null;
		}
	}
	/**
	 * 手环数据定时发送
	 * @type TimerUpdateECGPedo
	 * TODO
	 * @author shaoting.chen
	 * @time 2015年4月3日下午2:37:27
	 */
	public class TimerUpdateECGPedo {
	    Timer timer;
	    TimerUpdateAskTask timerTask;
	    public TimerUpdateECGPedo(int seconds) {     
	        timer = new Timer();     
	        timerTask = new TimerUpdateAskTask();
	        timer.schedule(timerTask, 1800*1000, seconds*1000); 
	    }
	    public void cancle(){
	    	timer.cancel();
	    	timerTask.cancel();
	    }
	    class TimerUpdateAskTask extends TimerTask {     
	        public void run() {     
	            Logger.i("-----", "Timer TimerUpdateAskTask!");
	            if(PreferencesUtils.getInt(mContext, SharedPreferredKey.DEVICE_TYPE, 0) == DeviceConstants.DEVICE_BRACLETE_BEATBAND)//手环上传数据
	    		{
	            	try {
	            		if(SINGLEINSTANCE!=null)
	            		{
	            			SINGLEINSTANCE.getPedometerData();
	            		}
					} catch (Exception e) {
						// TODO: handle exception
						
					}
	    		} 
	        }     
	    }         
	}
	
	/**
	 * 保存蓝牙设备令牌
	 * 
	 * @return
	 * @return int
	 */
	private int saveDeviceTokenInfo(String token, String number,String version) {
		
		final String deviceToken=token;
		final String deviceNumber=number;
		final String deviceVersion=version;
		try {
			new Thread() {
				public void run() {
					String userId=PreferencesUtils.getString(mContext, SharedPreferredKey.USERUID,"");
					String deviceId="01"+PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ADDRESS,"");
					SaveDeviceToken saveBack = new SaveDeviceToken();
					int back = DataSyn.getInstance().saveDeviceToken(userId, deviceId,  deviceNumber, deviceToken, deviceVersion, saveBack);
//					if (back == 0) {
//						Message msg5 = new Message();
//						msg5.what = 3;
//						mAlertHandler.sendMessage(msg5);
//					} else {
//						Message msg6 = new Message();
//						msg6.what = 4;
//						mAlertHandler.sendMessage(msg6);
//					}
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
	 * 更新手环系统
	 * @param fileUrl
	 * @param fileName
	 */
	public void updateFirmware(String fileUrl, String fileName){
		if(StringUtils.isNotBlank(fileUrl) && StringUtils.isNotBlank(fileName)){
			tgBleManager.fwDown(fileUrl, fileName);
		}
	}
	public String getFwVersion(){
		return tgBleManager.getFwVersion();
	}
	
	public String getBondToken(){
		return tgBleManager.getBondToken();
	}
	public String getHwSerialNumber(){
		return tgBleManager.getHwSerialNumber();
	}
}
