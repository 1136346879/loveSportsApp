package cmccsi.mhealth.app.sports.device;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.net.DataSyn;

import com.neurosky.ble.TGBleManager;
import com.neurosky.ble.TGBleManagerCallback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.widget.Toast;

/**
 * BeatBand 手环设备实现类
 * 
 * @type BeatBandDevice TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:38:47
 */
public class BeatBandDevice extends AbstractBeatBandDevice {

	private String TAG = "BeatBandDevice";

	private BaseDeviceInterface.BaseCallBack mBaseCallBack;

	private Context mContext;

	private BluetoothAdapter mBluetoothAdapter;
	private TGBleManager tgBleManager;
	private PedometorDataInfo bluetoothPedo;// 当前页面显示的数据
	private String mDeviceId;
	private boolean isRealTime = true;// 手环实时显示开关

	private int option = 0;// 用于区分操作手环

	private int connectStatus = 0;// 连接状态0初始状态 1连接成功
	private int mStatus = 0;// 手环状态 0初始状态 1连接中 2 已连接 3正在动作

	private boolean islog = true;

	public BeatBandDevice(Context context) {
		this.mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		tgBleManager = TGBleManager.getInstance();
		tgBleManager.initTGBleManager(mContext, mBluetoothAdapter, bleCallback);
		tgBleManager.setupTGBleManager(true);
	}

	@Override
	public void connect(String address) {
		// TODO Auto-generated method stub
		Logger.i(TAG, "---address " + address);
		mStatus = 3;
		mDeviceId = "01" + address;
		connectDevice(address);
	}

	@Override
	public void disConnect() {
		// TODO Auto-generated method stub
		mStatus = 3;
		tgBleManager.disconnect();
	}

	@Override
	public void syncData(String startTime, String endTime) {
		// TODO Auto-generated method stub
		if (isRealTime) {
			if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
				tgBleManager.stopRealTimeSport();
			}
			option = 0;
			isRealTime = false;
		}
		if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
			try {
				tgBleManager.startSyncData();
			} catch (Exception e) {
				e.printStackTrace();
				mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, "手环连接错误");
			}
		} else {
			if (islog)
				Logger.d(TAG, "getPedometerData  content");
			option = 1;
			connectDevice(mDeviceId.substring(2));
		}
	}

	@Override
	public int getDeviceStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startRealTime() {
		// TODO Auto-generated method stub
		isRealTime=true;
		if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
			if (islog)
				Logger.d(TAG, "startRealTimeSport  startRealTimeSport| " + tgBleManager.getConnectEvent().toString());
			tgBleManager.startRealTimeSport();

		} else {
			if (islog)
				Logger.d(TAG, "startRealTimeSport  content|" + tgBleManager.getConnectEvent().toString());
			option = 3;
			connectDevice(mDeviceId.substring(2));
		}
	}

	@Override
	public void stopRealTime() {
		// TODO Auto-generated method stub
		isRealTime=false;
		if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {
			tgBleManager.stopRealTimeSport();
		}
	}

	@Override
	public void startRealTimeEKG() {
		// TODO Auto-generated method stub
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			if(islog){
				Logger.d(TAG, "startRealTimeEKG  startRealTimeEKG| "+tgBleManager.getConnectEvent().toString());
			}
			
			tgBleManager.startRealTimeEKG();
			
		}
		else
		{
			if(islog) Logger.d(TAG, "startRealTimeSport  content|"+tgBleManager.getConnectEvent().toString());
//			content();
		}
	}

	@Override
	public void stopRealTimeEKG() {
		// TODO Auto-generated method stub
		if(tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded"))
		{
			tgBleManager.stopRealTimeEKG();
		}
	}

	@Override
	public void clearDeviceData() {
		// TODO Auto-generated method stub
		tgBleManager.eraseData();
	}

	@Override
	public void setCallBack(BaseCallBack cb) {
		// TODO Auto-generated method stub
		this.mBaseCallBack = cb;
	}

	/**
	 * 设置手环参数 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:33:21
	 */
	public void setNeroBLE() {
		initNeroBLE();
	}

	/**
	 * 手环连接 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:19
	 */
	public void connectDevice(String deviceAdress) {
		initNeroBLE();
		if (islog)
			Logger.d(TAG, TAG + "content" + "option:" + option);

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Toast.makeText(mContext, "手机蓝牙不可用", Toast.LENGTH_LONG).show();
		} else {
			mStatus = 1;
			Logger.d(TAG, tgBleManager.getConnectEvent().toString());

			if (tgBleManager.getConnectEvent().toString().equals("Connect_Event_Bonded")) {

				mBaseCallBack.connected(DeviceConstants.CONNECTED_SUCCESS, "连接成功");
			} else {
				if (deviceAdress != null && BluetoothAdapter.checkBluetoothAddress(deviceAdress)) {
					BluetoothDevice bt = mBluetoothAdapter.getRemoteDevice(deviceAdress);
					if (bt != null) {
						try {
							Logger.i(TAG, "candidateConnect " + "option" + option);
							tgBleManager.candidateConnect(bt);
						} catch (Exception e) {
							e.printStackTrace();
							connectStatus = 0;
							mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, "手环连接错误");
						}
					} else {
						Logger.i(TAG, "Peripheril does not exist!");
						mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, "蓝牙连接出错");
					}
				} else {
					mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, "手环地址不合法");
				}
			}
		}
	}

	/**
	 * 手环参数初始化 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:45
	 */
	public void initNeroBLE() {
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
			tgBleManager.setRunningStepLength(0);// 开启自动判断步长
			String birthday = PreferencesUtils.getString(mContext, SharedPreferredKey.BIRTHDAY, "1985-11-23");
			Logger.i(TAG, "---birthday " + birthday);
			String[] birth = birthday.split("-");
			int birthYear = Integer.parseInt(birth[0]);
			int birthMonth = Integer.parseInt(birth[1]);
			int birthDay = Integer.parseInt(birth[2]);
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
	 * TODO 蓝牙手环回调
	 */
	public TGBleManagerCallback bleCallback = new TGBleManagerCallback() {

		// 电池电量（确定手环已连接）
		@Override
		public void batteryLevel(int percent) {
			mStatus = 2;
			if (percent < 30) {
				mBaseCallBack.exception(DeviceConstants.DEVICE_POWER, String.valueOf(percent));
				
			}
			Logger.i(TAG, "Battery Level: " + percent + ",option= " + option);
			switch (option) {
			case 1:

				tgBleManager.startSyncData();
				option = 0;
				break;
			case 2:
				Logger.i("TgbleManager", "batteryLevel startRealTimeEKG-------");
				tgBleManager.startRealTimeEKG();

				option = 0;
				break;
			case 3:
				Logger.d("TgbleManager", "batteryLevel startRealTimeSport -------");
				tgBleManager.startRealTimeSport();// tgBleManager.askCurrentCount();
				option = 0;
				break;
			case 4:
				tgBleManager.eraseData();
				option = 0;
				break;
			case 5:
				tgBleManager.fwDown();

				option = 0;
				break;
			default:

				option = 0;
				break;
			}

		}

		@Override
		public void bleDidAbortConnect() {
		}

		// 手环已连接的回调
		@Override
		public void bleDidBond(int result) {
			Logger.i(TAG, "Bond result: " + result + "option: " + option);

			String msgresult = "";
			switch (result) {
			case 0:
				mBaseCallBack.connected(DeviceConstants.CONNECTED_SUCCESS, "连接成功");
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
				break;
			case 5:
				msgresult = "手环没有连接";
				break;
			case 7:
				msgresult = "手环连接超时";
				break;
			case 9:// exception会提示
				msgresult = "手环已与其他设备绑定，请reset手环";

				break;
			case 12:
				msgresult = "请检查您的设备是否支持此功能";
				break;
			default:
				msgresult = "手环连接失败，请重新配对";
				break;
			}
			if (msgresult != "") {
				if(null!=mBaseCallBack){
					mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, msgresult);
				}
			}

		}

		// 手环连接
		@Override
		public void bleDidConnect() {
			Logger.d(TAG, "bleDidConnect:" + "option" + option);
			String deviceToken = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_TOKEN, null);
			String deviceSN = PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_NUMBER, null);
			if (deviceToken != null && deviceToken != "") {
				Logger.d(TAG, deviceToken + "---deviceToken---");
				tgBleManager.adoptBond(deviceToken, deviceSN);
			} else {
				if (tgBleManager.getBondToken() == null) {
					tgBleManager.tryBond();
				} else {
					Logger.d(TAG, "Token:" + tgBleManager.getBondToken() + " HwSerial:" + tgBleManager.getHwSerialNumber());
					tgBleManager.adoptBond(tgBleManager.getBondToken(), tgBleManager.getHwSerialNumber());
				}
			}
		}

		// 断开连接
		@Override
		public void bleDidDisconnect() {
			if (islog)
				Logger.d(TAG, "bleDidDisconnect");
			mBaseCallBack.disConnected();
		}

		@Override
		public void bleDidSendAlarmGoals(int result) {

		}

		@Override
		public void bleDidSendUserConfig(int result) {

		}

		// 连接丢失
		@Override
		public void bleLostConnect() {

			if (islog)
				Logger.d(TAG, "bleLostConnect");
			tgBleManager.disconnect();

		}

		@Override
		public void candidateFound(BluetoothDevice device, int rssi, String mfgID) {

		}

		// 实时运动数据
		@Override
		public void currentCount(Date time, int steps, int calories, int actCalories, int distance, int energy, int mode, int hr) {
			Logger.i(TAG, "currentCount steps: " + steps);
			Logger.i(TAG, "currentCount calories: " + calories);
			Logger.i(TAG, "currentCount distance: " + distance);
			Logger.i(TAG, "currentCount energy: " + energy);
			Logger.i(TAG, "currentCount mode: " + mode);
			Logger.i(TAG, "currentCount heart rate: " + hr);

			// ***********************************************
			if (bluetoothPedo == null
					|| !bluetoothPedo.date.equals(DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline))) {
				bluetoothPedo = new PedometorDataInfo();
				bluetoothPedo.createtime = DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
				bluetoothPedo.date = DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline);
				bluetoothPedo.deviceId = mDeviceId;
			}

			bluetoothPedo.stepNum = steps + "";
			bluetoothPedo.cal = actCalories + "";
			bluetoothPedo.distance = distance + "";
			bluetoothPedo.strength2 = mode + "";
			mBaseCallBack.realTimeDataReceived(bluetoothPedo);

		}

		@Override
		public void dataReceived(int key, Object data) {
			// Logger.d(TAG, "dataReceived key:"+key +"dataReceived data"+data);
			mBaseCallBack.ekgDataReceived(key, data);
		}

		@Override
		public void diagEventRecord(Date time, int eventCode) {

		}

		@Override
		public void ekgSample(int sample) {

		}

		@Override
		public void ekgStarting(Date time, int sampleRate, boolean realTime, String comment) {

		}

		@Override
		public void ekgStop(int result, int finalHR) {
			mBaseCallBack.ekgStop(result, finalHR);
		}

		@Override
		public void eraseComplete(int arg0) {
			tgBleManager.startRealTimeSport();
			mBaseCallBack.exception(DeviceConstants.UPLOAD_SUCCESS, "数据上传成功");
		}

		@Override
		public void exceptionMessage(int exceptEvent) {
			Logger.d(TAG, "exceptEvent: " + exceptEvent);
			String msgresult = "";
			int exceptionCode = -1;
			switch (exceptEvent) {
			case 4:
				msgresult = "年龄不在正确范围之内";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 5:
				msgresult = "手环正在进行其他操作，请稍后在试";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 6:
				msgresult = "步数不在正确范围之内";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 7:
				msgresult = "获取实时数据失败";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 105:
				msgresult = "蓝牙出现错误，请检查手机蓝牙";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 107:
				msgresult = "手环连接超时";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			case 304:
				msgresult = "";

				break;
			case 303:
				msgresult = "";

				break;
			case 301://
				msgresult="";
			case 99:
				msgresult = "";

				break;
			default:
				msgresult = "手环连接失败";
				exceptionCode = DeviceConstants.EXCEPTION_CONNECT;
				break;
			}
			if (!msgresult.equals("")) {
				mBaseCallBack.exception(exceptionCode, msgresult);
			}
		}

		@Override
		public void fatBurnRecord(Date time, int heartRate, int fatBurn, int trainingZone) {

		}

		@Override
		public void fwUpdateInProgress(boolean transfer) {

		}

		@Override
		public void fwUpdatePercent(int percent) {

		}

		@Override
		public void fwUpdateReport(int result, int size, int checksum, int transfer) {

		}

		@Override
		public void pedometryRecord(Date time, int steps, int calories, int distance, int activeCalories, int stepBPM,
				int energy, int mode, int sleepPhase, int sedentaryTime, int walkingTime, int runningTime) {

		}

		@Override
		public void potentialBond(String code, String sn, String devName) {
			tgBleManager.takeBond();
		}

		@Override
		public void sleepRecord(Date time, int phase, int code) {

		}

		@Override
		public void sleepResults(int arg0, Date arg1, Date arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
				int arg9, int arg10, int arg11) {

		}

		@Override
		public void sleepSmoothData(int arg0, Date[] arg1, int[] arg2) {

		}

		@Override
		public void transferInProgress(boolean transfer) {

		}

		@Override
		public void transferPercent(int percent) {
			Logger.i(TAG, "Sync percentage: " + percent);
			if (percent > 100) {
				percent = 100;
			}
			mBaseCallBack.pedoDataPercent(percent);
		}

		// 获取运动数据
		@Override
		public void transferReport(int result, int ped_avail, int ped_recv, int ekg_avail, int ekg_recv, int sleep_avail,
				int sleep_recv, int diag_avail, int diag_recv) {
			Logger.i(TAG, "sync result: " + result);
			if (result == 0) {
				PedometorSession session = (PedometorSession) tgBleManager.getPedometorSession();
				Logger.d("cjz", "tgBleManager.getPedometorSession()");
				List<PedometorSubData> subDataList = session.pedSession;
				Logger.d("cjz", "subDataList = session.pedSession");
				PedometorListInfo pedoData = new PedometorListInfo();

				PedoDetailInfo detailDataInfo = new PedoDetailInfo();

				for (PedometorSubData pedSub : subDataList) {
					if (pedSub.flagtypeScale.equals("D")) {
						Logger.i(TAG, "flagtypeScale:" + pedSub.flagtypeScale);
						List<BasePedometorSummary> basePedList = pedSub.data;
						for (BasePedometorSummary basePed : basePedList) {
							PedometorDataInfo dataPedometor = new PedometorDataInfo();
							// 实时运-动数据大
							if (bluetoothPedo != null
									&& Integer.parseInt(basePed.stepNum) <= Integer.parseInt(bluetoothPedo.stepNum)) {
								dataPedometor.stepNum = bluetoothPedo.stepNum;
								dataPedometor.date = bluetoothPedo.date;
								dataPedometor.cal = bluetoothPedo.cal;
								dataPedometor.distance = bluetoothPedo.distance;
								dataPedometor.strength2 = bluetoothPedo.strength2;
							} else {
								dataPedometor.stepNum = basePed.stepNum;
								dataPedometor.date = basePed.date;
								dataPedometor.cal = basePed.cal;
								dataPedometor.distance = basePed.distance;
								dataPedometor.strength2 = basePed.strength1;
							}
							dataPedometor.power = basePed.power;
							// dataPedometor.strength2 = basePed.strength1;
							// 运动时长是差量

							dataPedometor.strength3 = basePed.strength2;
							dataPedometor.strength1 = basePed.strength3;
							dataPedometor.strength4 = basePed.strength4;
							dataPedometor.deviceId = mDeviceId;
							dataPedometor.createtime = DateFormatUtils.ChangeFormat(dataPedometor.date,
									FormatType.DateWithUnderline, FormatType.DateLong);
							pedoData.datavalue.add(dataPedometor);
							Logger.d(TAG, "获取数据" + dataPedometor.stepNum);
						}
					}
					if (pedSub.flagtypeScale.equals("H")) {
						// 处理详细包
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
								if (detailData.start_time.length() == 2 && detailData.start_time.substring(0, 1).equals("0")) {
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
					}
				}
				ArrayList<PedoDetailInfo> PedoDetailInfoList = new ArrayList<PedoDetailInfo>();
				PedoDetailInfoList.add(detailDataInfo);
				mBaseCallBack.pedoDataReceived(pedoData, PedoDetailInfoList);
				
				tgBleManager.eraseData(); // wy
			} else {
				mBaseCallBack.exception(DeviceConstants.EXCEPTION_CONNECT, "获取手环数据失败");
			}
		}
	};

	/**
	 * 保存蓝牙设备令牌
	 * 
	 * @return
	 * @return int
	 */
	private int saveDeviceTokenInfo(String token, String number, String version) {
		final String deviceToken = token;
		final String deviceNumber = number;
		final String deviceVersion = version;
		try {
			new Thread() {
				public void run() {
					String userId = PreferencesUtils.getString(mContext, SharedPreferredKey.USERUID, "");
					String deviceId = "01" + PreferencesUtils.getString(mContext, SharedPreferredKey.DEVICE_ADDRESS, "");
					SaveDeviceToken saveBack = new SaveDeviceToken();
					int back = DataSyn.getInstance().saveDeviceToken(userId, deviceId, deviceNumber, deviceToken, deviceVersion,
							saveBack);

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
	 * 固件升级
	 */
	@Override
	public void updateOTAData() {
		// TODO Auto-generated method stub

	}
	
}
