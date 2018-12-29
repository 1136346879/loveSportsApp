package cmccsi.mhealth.app.sports.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import cmccsi.mhealth.app.sports.bean.DataDetailPedo;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.SimplePostInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.PedometerJsonCreat;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.phonesteplib.SPUtils;
import cmccsi.mhealth.app.sports.phonesteplib.StepDetector;
import cmccsi.mhealth.app.sports.phonesteplib.UserInstance;
import cmccsi.mhealth.app.sports.R;

public class StepService extends Service {

	private final IBinder mBinder = new StepBinder();
	private StepDetector mDetector;
	public int[] mAcc = new int[75];
	private GetAccThread1 mGetAccThread;
	private PowerManager.WakeLock wakeLock;
	private UserInstance userInstance;
	int last_Step = 0;

	// =================================================
	private NotificationManager nm;
	// action

	// public static final String STEP_RECEIVED_ACTION_ON =
	// "com.cmcc.ishang.step.STEP_RECEIVED_ACTION_ON";
	// public static final String STEP_RECEIVED_ACTION_STOP =
	// "com.cmcc.ishang.step.STEP_RECEIVED_ACTION_STOP";

	// public static final String RESUME_PEDOACTIVITY =
	// "cmccsi.mhealth.app.sports.RESUME_PEDOACTIVITY";
	// 发送消息的数据
	public final static String TARGET_STEP = "targetStep";
	public final static String TODAY_STEP = "todayStep";
	public final static String TODAY_TIME = "todaytime";
	public final static String TODAY_CAL = "todaycal";
	public final static String TODAY_DISTANCE = "todaydistance";
	public final static String UPLOAD_STATUS = "updatestatus";
	public final static String UPLOAD_STATUS_SUCCESS = "updatestatus";

	public static boolean isRunning = false;

	private int targetStep = 10000;
	private int todayTime = 0;// 今天的数据
	private int todayStep = 0;
	private int todayCal = 0;
	private int todayDistance = 0;
	public int lastUpdateStep = 0;// 当次的数据
	public static int todayTotalStep = 0;// 真实的当次的数据
	public static int todayTotalCal = 0;// 真实的当次的卡路里
	public static int todayTotalDistance = 0;// 真实的当次的距离数据
	public static int todayTotalTime = 0;// 真实的当次的时长数据
	private int mMinuts = 0;// 卡一分钟判断
	// private int runTime = 0;// 运动时长(从开启累加)

	private PedometorDataInfo todayPedoInfo;// 开启计步时当天计步简包总数
	private StopPhoneStepReceiver receiver;// 计步停止监听
	private Notification noti;// 通知

	private Date currentdate;

	private int laststepdetail = 0;
	private int lastcaldetail = 0;
	private SPUtils spUtils;
	private int tempMinuts;// 生成随机数

	private ArrayList<Integer> snp5ls;// 详包每小时每五分钟的步数
	private ArrayList<Integer> knp5ls;// 详包每小时每五分钟的卡路里
	private PedoDetailInfo runPedoDetailInfo;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// String from = "";
		// if (intent != null) {
		// from = intent.getStringExtra("from");
		// }
		// if (StringUtils.isNotBlank(from) &&
		// from.equals("BootCompletedReceiver")) {
		// String userUid = PreferencesUtils.getString(this,
		// SharedPreferredKey.USERUID, "");
		// if (StringUtils.isNotBlank(userUid)) {
		// mGetAccThread.step = 0;
		// mGetAccThread.calory = 0;
		// mGetAccThread.distance = 0;
		// mGetAccThread.exercise_intensity_normally = 0;
		// mGetAccThread.exercise_intensity_fairly = 0;
		// mGetAccThread.exercise_intensity_very = 0;
		// new Thread(new InitAccountRunnable(this, userUid)).start();
		// } else {
		// this.stopSelf();
		// }
		// }
		// mDetector.startStepDetector();
		// Log.d("zwli", "onStartCommand~~1");
		// initRunPedoData();
		// targetStep =
		// Integer.valueOf(getSharedPreferences(SharedPreferredKey.SHARED_NAME,
		// 0).getString(
		// SharedPreferredKey.TARGET_STEP, "10000"));
		// String percent = (int) Math.ceil((todayStep) * 100 / targetStep) +
		// "";
		// showNotification(todayStep + "", todayCal + "", percent);
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(System.currentTimeMillis());
		// mMinuts = calendar.get(Calendar.MINUTE);
		// tempMinuts = (int) (Math.random() * 58 + 1);// 生成随机数
		// isRunning = true;
		// Log.d("zwli", "onStartCommand~~2");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if(null==wakeLock){
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Config.SC_ACTION);
			wakeLock.setReferenceCounted(false);
			wakeLock.acquire();
		}
		return START_REDELIVER_INTENT;
	}

	/**
	 * duration : integer array contains 3 elements.[lightly,fairly,very] unit:
	 * seconds.
	 * 
	 */
	private void sendDataByBoardcast(int step, float calory, int distance, int lightly, int fairly, int very) {
		// Log.d("zwli", "~~start  " + runTime);
		try {
			// t2 = new Time();
			// t2.setToNow();
			// runTime++;
			if (last_Step != mGetAccThread.step) {
				int stepTime = (int) Math.floor(mGetAccThread.time);// 对标结果
				int tempStep = mGetAccThread.step;
				int tempCal = (int) Math.floor(mGetAccThread.calory);// 千卡对标结果
				int tempDistance = (int) mGetAccThread.distance;
				// 今天总数据
				todayTotalStep = todayStep + tempStep;
				todayTotalCal = todayCal + tempCal;
				todayTotalDistance = todayDistance + tempDistance;
				todayTotalTime = todayTime + stepTime;

				Intent intent = new Intent(Config.STEP_SENDING_ACTION);
				intent.putExtra("STEP_ALL_DAY", todayTotalStep);
				intent.putExtra("CALORIE_ALL_DAY", todayTotalCal);// Calorie
				intent.putExtra("DISTANCE_ALL_DAY", todayTotalDistance);
				intent.putExtra("LV2_ALL_DAY", mGetAccThread.exercise_intensity_normally);
				intent.putExtra("LV3_ALL_DAY", mGetAccThread.exercise_intensity_fairly);
				intent.putExtra("LV4_ALL_DAY", mGetAccThread.exercise_intensity_very);
				// intent.putExtra("YXBS_ALL_DAY", ());
				intent.putExtra("DURATION_ALL_DAY", todayTotalTime);
				// intent.putExtra("DURATION_ALL_DAY", sum_lv2 + sum_lv3 +
				// sum_lv4);//
				sendBroadcast(intent);
				last_Step = mGetAccThread.step;
				targetStep = Integer.valueOf(getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0).getString(
						SharedPreferredKey.TARGET_STEP, "10000"));
				String percent = (int) Math.ceil(todayTotalStep * 100 / targetStep) + "";
				notifyNotification(todayTotalStep + "", todayTotalCal + "", percent);
				Logger.d("cjz", "step:laststepdetail:step-laststepdetail " + todayTotalStep + ":" + laststepdetail + ":"
						+ (todayTotalStep - laststepdetail));
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			// hjn add
			// 运动的时候本方法每一秒执行一次，不运动的时候每四秒执行一次
			// 为了做到无论运动还是不运动，下面的每个方法都能走到
			if (mMinuts == calendar.get(Calendar.MINUTE))// 下面的方法一分钟执行一次就够了
			{
				return;
			} else {
				// 每次检测包是不是当天的 老有手机跨天不清零 受不了了
				if (!todayPedoInfo.date.equals(DateFormatUtils.DateToString(calendar.getTime(), FormatType.DateWithUnderline))) {
					clearPedoData();
				}
				Logger.d("cjz", "minuts:" + mMinuts + " : " + calendar.get(Calendar.MINUTE));
				mMinuts = calendar.get(Calendar.MINUTE);
				AddPedoData(todayPedoInfo, todayTotalStep, todayTotalCal, todayTotalDistance, todayTotalTime);
				insertSumPedoData(todayPedoInfo);
			}
			if (calendar.get(Calendar.HOUR_OF_DAY) % 2 == 0 && calendar.get(Calendar.MINUTE) == tempMinuts)// 时间到达两小时加随机数分钟上传一次数据
			{
				insertAndUploadPedo();
			}

			// ============add start=============
			if (calendar.get(Calendar.MINUTE) % 5 == 0)// 5分钟添加详细包数据
			{

				addStepPerFiveMinute(todayTotalStep, todayTotalCal);
				if (calendar.get(Calendar.MINUTE) == 0) {
					insertPedoDetail(runPedoDetailInfo);
					initNewPedoDetail();

				}
			}
			// =============add end============
			if (calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) == 59) {
				insertAndUploadPedo();
			}
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
				Logger.d("cjz", "clearPedoData 重置数据");
				clearPedoData();
				// runTime = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		String from = "";
		if (intent != null) {
			from = intent.getStringExtra("from");
		}
		if (StringUtils.isNotBlank(from) && from.equals("BootCompletedReceiver")) {
			String userUid = PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
			if (StringUtils.isNotBlank(userUid)) {
				mGetAccThread.step = 0;
				mGetAccThread.calory = 0;
				mGetAccThread.distance = 0;
				mGetAccThread.exercise_intensity_normally = 0;
				mGetAccThread.exercise_intensity_fairly = 0;
				mGetAccThread.exercise_intensity_very = 0;
				new Thread(new InitAccountRunnable(this, userUid)).start();
			} else {
				this.stopSelf();
			}
		}
		mDetector.startStepDetector();
		Log.d("zwli", "onStartCommand~~1");
		initRunPedoData();
		targetStep = Integer.valueOf(getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0).getString(
				SharedPreferredKey.TARGET_STEP, "10000"));
		String percent = (int) Math.ceil((todayStep) * 100 / targetStep) + "";
		showNotification(todayStep + "", todayCal + "", percent);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		mMinuts = calendar.get(Calendar.MINUTE);
		tempMinuts = (int) (Math.random() * 58 + 1);// 生成随机数
		isRunning = true;
		Log.d("zwli", "onStartCommand~~2");
		return mBinder;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class StepBinder extends Binder {
		public StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.d("cjz", "StepService onCreate");
		super.onCreate();
		spUtils = SPUtils.getInstance(getApplicationContext());
		userInstance = new UserInstance();
		userInstance.setGender(Integer.parseInt(spUtils.getString(SharedPreferredKey.GENDER, "1")));
		userInstance.setAge(22);
		userInstance.setHeight(Integer.parseInt(spUtils.getString(SharedPreferredKey.HEIGHT, "170")));
		userInstance.setWeight(Integer.parseInt(spUtils.getString(SharedPreferredKey.WEIGHT, "65")));
		// 注册停止手机计步接收器
		receiver = new StopPhoneStepReceiver();
		IntentFilter itentf = new IntentFilter();
		itentf.addAction(Config.PHONESTEP_STOP_ACTION);
		itentf.addAction(Config.PHONESTEP_UPLOAD_ACTION);
		itentf.addAction(Config.PHONESTEP_STOP_NOSAVE_ACTION);
		registerReceiver(receiver, itentf);

		mDetector = StepDetector.getStepDetetorInstance(getApplicationContext());
		mDetector.startStepDetector();
		if (null != mGetAccThread) {
			mGetAccThread.stopMyThread();
			mGetAccThread = null;
		}
		mGetAccThread = new GetAccThread1(mDetector, userInstance);

		// t = new Time();
		// t.setToNow();
		mGetAccThread.step = 0;
		mGetAccThread.calory = 0;
		mGetAccThread.distance = 0;
		mGetAccThread.exercise_intensity_normally = 0;
		mGetAccThread.exercise_intensity_fairly = 0;
		mGetAccThread.exercise_intensity_very = 0;

		Logger.d("cjz", "mGetAccThread.step:" + mGetAccThread.step);

		mGetAccThread.setDaemon(true);
		mGetAccThread.start();

		// /////////////////////////////////////////////
		Log.d("zwli", "onStartCommand~~1");
		initRunPedoData();
		targetStep = Integer.valueOf(getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0).getString(
				SharedPreferredKey.TARGET_STEP, "10000"));
		String percent = (int) Math.ceil((todayStep) * 100 / targetStep) + "";
		showNotification(todayStep + "", todayCal + "", percent);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		mMinuts = calendar.get(Calendar.MINUTE);
		tempMinuts = (int) (Math.random() * 58 + 1);// 生成随机数
		isRunning = true;
		Log.d("zwli", "onStartCommand~~2");
	}

	public interface ICallback {
		public void stepsChanged(int value);

		public void caloriesChanged(float value);

		public void distanceChanged(int value);

		public void testFunction(int value);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDetector.stopStepDetector();
		// nm.cancel(R.string.app_name);
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
		Log.d("testing", "StepService onDestroy");
		if (nm != null) {
			nm.cancelAll();
		}
		try {
			if (null != receiver) {
				unregisterReceiver(receiver);
				receiver = null;
			}
			if (null != mGetAccThread) {
				mGetAccThread.stopMyThread();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ************************************************************
	// 以上为原型代码
	/**
	 * 更改通知
	 * 
	 * @param step
	 *            步数数字
	 * @param cal
	 *            卡路里数字
	 * @param percent
	 *            目标完成百分比数字
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(String step, String cal, String percent) {
		RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.notification_phone_step);
		remoteView.setTextViewText(R.id.tv_stepsum, step + "步");
		remoteView.setTextViewText(R.id.tv_cal, cal + "千卡");
		remoteView.setTextViewText(R.id.tv_percent, percent + "%");
		Intent intent = new Intent(cmccsi.mhealth.app.sports.service.StepService.this,
				cmccsi.mhealth.app.sports.pedo.PedometerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(cmccsi.mhealth.app.sports.service.StepService.this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		noti = new Notification.Builder(this).setSmallIcon(R.drawable.icon_notify_ticker).setContent(remoteView)
				.setContentIntent(contentIntent).getNotification();
		noti.flags = Notification.FLAG_NO_CLEAR;
		startForeground(2, noti);

	}

	/**
	 * 通知更新
	 * 
	 * @param step
	 *            步数
	 * @param cal
	 *            卡路里
	 * @param percent
	 *            目标完成百分比
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:15:16
	 */
	private void notifyNotification(String step, String cal, String percent) {
		if (noti == null) {
			showNotification(step, cal, percent);
		}
		// 用原来的会卡死
		RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.notification_phone_step);
		remoteView.setTextViewText(R.id.tv_stepsum, step + "步");
		remoteView.setTextViewText(R.id.tv_cal, cal + "千卡");
		remoteView.setTextViewText(R.id.tv_percent, percent + "%");
		noti.contentView = remoteView;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(2, noti);
	}

	public void notifyNotificationPercent() {
		targetStep = Integer.valueOf(getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0).getString(
				SharedPreferredKey.TARGET_STEP, "10000"));
		String percent = (int) Math.ceil(todayTotalStep * 100 / targetStep) + "";
		if (noti == null) {
			showNotification(todayTotalStep + "", todayTotalCal + "", percent);
		}
		// 用原来的会卡死
		RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.notification_phone_step);
		remoteView.setTextViewText(R.id.tv_stepsum, todayTotalStep + "步");
		remoteView.setTextViewText(R.id.tv_cal, todayTotalCal + "千卡");

		remoteView.setTextViewText(R.id.tv_percent, percent + "%");
		noti.contentView = remoteView;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(2, noti);
	}

	/**
	 * 停止通知
	 */
	private void stopNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 取消的只是当前Context的Notification
		mNotificationManager.cancel(2);
	}

	/**
	 * 初始化简要包和当天数据
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:16:02
	 */
	private void initRunPedoData() {
		currentdate = new Date(System.currentTimeMillis());
		deviceId = PreferencesUtils.getString(getBaseContext(), SharedPreferredKey.DEVICE_ID, "");
		todayPedoInfo = PedoController.GetPedoController(getBaseContext()).getPedometerByDay(deviceId, currentdate);
		if (todayPedoInfo == null) {
			todayPedoInfo = new PedometorDataInfo();
			todayPedoInfo.distance = "0";
			todayPedoInfo.cal = "0";
			todayPedoInfo.stepNum = "0";
			todayPedoInfo.strength1 = "0";
			todayPedoInfo.strength2 = "0";
		}
		todayPedoInfo.createtime = DateFormatUtils.DateToString(currentdate, FormatType.DateLong);
		todayPedoInfo.date = DateFormatUtils.DateToString(currentdate, FormatType.DateWithUnderline);
		todayPedoInfo.deviceId = deviceId;
		initTodayPedoData(todayPedoInfo);

		initNewPedoDetail();
	}

	/**
	 * 初始化当天已运动的数据
	 * 
	 * @param data
	 *            运动数据
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:46:30
	 */
	private void initTodayPedoData(PedometorDataInfo todaydata) {
		try {
			todayStep = Integer.parseInt(todaydata.stepNum);
			todayCal = Integer.parseInt(todaydata.cal);
			todayDistance = Integer.parseInt(todaydata.distance);
			todayTime = getStepTimeFromPedometor(todaydata);
			todayTotalStep = Integer.parseInt(todaydata.stepNum);
			todayTotalCal = Integer.parseInt(todaydata.cal);
			todayTotalDistance = Integer.parseInt(todaydata.distance);
			todayTotalTime = getStepTimeFromPedometor(todaydata);
			laststepdetail = todayStep;
			lastcaldetail = todayCal;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 累加数据到简要包数据
	 * 
	 * @param pedo
	 *            简要包
	 * @param stepSum
	 *            总步数
	 * @param stepCal
	 *            总卡路里
	 * @param stepDistance
	 *            总距离
	 */
	private void AddPedoData(PedometorDataInfo pedo, int stepSum, int stepCal, int stepDistance, int stepTime) {
		if (pedo == null) {
			return;
		}
		// pedo.createtime = DateFormatUtils.DateToString(new Date(),
		// FormatType.DateLong);
		// pedo.date = DateFormatUtils.DateToString(new Date(),
		// FormatType.DateWithUnderline);
		pedo.stepNum = stepSum + "";
		pedo.cal = stepCal + "";
		pedo.distance = stepDistance + "";
		pedo.strength2 = stepTime + "";
		pedo.strength3 = "0";
		pedo.strength4 = "0";
	}

	/**
	 * 插入简要包
	 * 
	 * @param Pedo
	 *            运动数据
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:21:18
	 */
	private void insertSumPedoData(PedometorDataInfo Pedo) {
		PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(Pedo, true);
		Logger.d("cjz", "手机计步存数据啦");
	}

	private List<NameValuePair> mUploadlist;
	private List<NameValuePair> mUploadDetailList;
	private int dataFromPHP;
	private String deviceId = "";
	private String myGwUrl = Config.PEDO_UPLOAD_URL;

	// "http://111.11.29.83:8099/DADS_HTTP/service/uploadSportsDatas";
	// Bundle mBundle=new Bundle();
	/**
	 * 上传运动简要包
	 * 
	 * @param stepSum
	 * @param calSum
	 * @param distanceSum
	 * @param recordtime
	 */
	public void uploadPed(PedometorDataInfo tempPedo) {
		if (tempPedo == null) {
			return;
		}
		// String log_StepUpload=DateFormatUtils.DateToString(new Date(),
		// FormatType.DateLong)+"||"
		// +tempPedo.createtime+"|"+tempPedo.date+"|"+tempPedo.stepNum+"|"+tempPedo.cal+"|"
		// +tempPedo.distance+"|"+tempPedo.strength2+"|"+deviceId+"\r\n";
		// Common.wirteStringToSdAfterCreateDirs(Config.ERRORLOG_URL,
		// "log_StepUpload" + ".txt",log_StepUpload );

		PedometerJsonCreat mJsonCreat = new PedometerJsonCreat();

		mUploadlist = new ArrayList<NameValuePair>();

		String weight = PreferencesUtils.getString(this, SharedPreferredKey.WEIGHT, "60");

		mJsonCreat.appPedJson(tempPedo.stepNum, tempPedo.cal, tempPedo.distance, tempPedo.createtime, tempPedo.strength2,
				tempPedo.strength3, tempPedo.strength4, weight);
		mJsonCreat.httpJsonWithVersin(tempPedo.date, deviceId, "stepCount", getVersion());
		mUploadlist = mJsonCreat.jsonsend();
		Log.v("Service", "发送的数据：" + mUploadlist.toString());
		new Thread() {
			public void run() {
				SimplePostInfo info = new SimplePostInfo();
				dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadlist);

				Log.v("Service", "简要包上传完成" + dataFromPHP);

				Intent intent = new Intent(Config.UPLOADSTATUS_ACTION);
				if (dataFromPHP == 0) {
					intent.putExtra(UPLOAD_STATUS, getResources().getString(R.string.phonestep_uploadsuccess));
				} else {
					intent.putExtra(UPLOAD_STATUS, getResources().getString(R.string.phonestep_uploadfailed));
				}

				sendBroadcast(intent);
			};
		}.start();
	}

	/**
	 * 计步service接收
	 * 
	 * @type StopPhoneStepReceiver
	 * @author jiazhi.cao
	 * @time 2015-3-13下午2:08:56
	 */
	private class StopPhoneStepReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Config.PHONESTEP_STOP_ACTION)) {
				// 存储上传数据停止服务
				try {
					// 本次没有产生运动数据则不存储上传数据
					sendUpLoadIntent();
					insertAndUploadPedo();
					Logger.i("stepservice", "停止手机计步");

				} catch (Exception e) {
					Intent it = new Intent(Config.UPLOADSTATUS_ACTION);
					it.putExtra(UPLOAD_STATUS, getResources().getString(R.string.phonestep_uploadfailed));
					sendBroadcast(it);
					e.printStackTrace();
				} finally {
					isRunning = false;
					stopNotification();
					stopSelf();
				}
			} else if (intent.getAction().equals(Config.PHONESTEP_UPLOAD_ACTION)) {
				// 存储上传数据停止服务
				try {
					// 本次没有产生运动数据则不存储上传数据
					sendUpLoadIntent();
					insertAndUploadPedo();

				} catch (Exception e) {
					Intent it = new Intent(Config.UPLOADSTATUS_ACTION);
					intent.putExtra(UPLOAD_STATUS, getResources().getString(R.string.phonestep_uploadfailed));
					sendBroadcast(it);
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Config.PHONESTEP_STOP_NOSAVE_ACTION)) {
				// 存储上传数据停止服务
				try {
					// 本次没有产生运动数据则不存储上传数据

					sendUpLoadIntent();
					AddPedoData(todayPedoInfo, todayTotalStep, todayTotalCal, todayTotalDistance, todayTotalTime);
					uploadData();

				} catch (Exception e) {
					Intent it = new Intent(Config.UPLOADSTATUS_ACTION);
					intent.putExtra(UPLOAD_STATUS, getResources().getString(R.string.phonestep_uploadfailed));
					sendBroadcast(it);
					e.printStackTrace();
				} finally {
					isRunning = false;
					stopNotification();
					stopSelf();
				}
			}

		}

	}

	/**
	 * 发送无数据上传通知
	 */
	private void sendUpLoadIntent() {
		sendIntentBroadcast(Config.PHONESTEP_STARTUPLOAD_ACTION, UPLOAD_STATUS,
				getResources().getString(R.string.phonestep_uploading));
	}

	/**
	 * 发送broadcast
	 * 
	 * @param action
	 * @param message
	 */
	private void sendIntentBroadcast(String action, String messagekey, String message) {
		Intent it = new Intent(action);
		it.putExtra(messagekey, message);
		sendBroadcast(it);
	}

	/**
	 * 取运动时长
	 * 
	 * @param data
	 *            运动实体
	 * @return
	 * @return String HH:MM格式
	 * @author jiazhi.cao
	 * @time 下午3:21:12
	 */
	private int getStepTimeFromPedometor(PedometorDataInfo data) {
		int result = 0;
		try {
			result = Integer.parseInt(data.strength2 == null ? "0" : data.strength2)
					+ Integer.parseInt(data.strength3 == null ? "0" : data.strength3)
					+ Integer.parseInt(data.strength4 == null ? "0" : data.strength4);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 清空相关记录数据
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午1:59:16
	 */
	private void clearPedoData() {
		todayTime = 0;// 今天的数据
		todayStep = 0;
		todayCal = 0;
		todayDistance = 0;
		lastUpdateStep = 0;// 当次的数据
		todayTotalStep = 0;// 真实的当次的数据
		todayTotalCal = 0;// 真实的当次的卡路里
		todayTotalDistance = 0;// 真实的当次的距离数据
		todayTotalTime = 0;// 真实的当次的时长数据
		// runTime = 0;// 运动时长(从开启累加)
		tempMinuts = (int) (Math.random() * 58 + 1);// 生成随机数
		currentdate = new Date(System.currentTimeMillis());
		todayPedoInfo = new PedometorDataInfo();
		todayPedoInfo.distance = "0";
		todayPedoInfo.cal = "0";
		todayPedoInfo.stepNum = "0";
		todayPedoInfo.strength1 = "0";
		todayPedoInfo.strength2 = "0";
		todayPedoInfo.createtime = DateFormatUtils.DateToString(currentdate, FormatType.DateLong);
		todayPedoInfo.date = DateFormatUtils.DateToString(currentdate, FormatType.DateWithUnderline);
		todayPedoInfo.deviceId = deviceId;

		laststepdetail = 0;
		lastcaldetail = 0;
		initNewPedoDetail();
		// *****************研究院的初始化**************************
		mGetAccThread.step = 0;
		mGetAccThread.calory = 0;
		mGetAccThread.distance = 0;
		mGetAccThread.time=0;
		mGetAccThread.exercise_intensity_normally = 0;
		mGetAccThread.exercise_intensity_fairly = 0;
		mGetAccThread.exercise_intensity_very = 0;

		notifyNotification("0", "0", "0");
	}

	/**
	 * 每小时详细包累加
	 * 
	 * @param pedoDetailInfo
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:44:07
	 */
	private void insertPedoDetail(PedoDetailInfo pedoDetailInfo) {
		DataDetailPedo tempdetail = pedoDetailInfo.datavalue.get(0);
		tempdetail.level2p5 = "0,0,0,0,0,0,0,0,0,0,0,0";
		tempdetail.level3p5 = "0,0,0,0,0,0,0,0,0,0,0,0";
		for (int i = 0; null != snp5ls && i < snp5ls.size() - 1; i++) {
			tempdetail.snp5 += snp5ls.get(i) + ",";
			tempdetail.knp5 += knp5ls.get(i) + ",";
		}
		if (null != snp5ls && snp5ls.size() > 0) {
			tempdetail.snp5 += snp5ls.get(snp5ls.size() - 1) + "";
			tempdetail.knp5 += knp5ls.get(snp5ls.size() - 1) + "";
		}
		// if (tempdetail.knp5 != null && tempdetail.knp5.length() > 0) {
		// if (tempdetail.knp5.endsWith(",")) {
		// tempdetail.knp5 = tempdetail.knp5.substring(0,
		// tempdetail.knp5.length() - 1);
		// }
		// }
		// if (tempdetail.snp5 != null && tempdetail.snp5.length() > 0) {
		// if (tempdetail.snp5.endsWith(",")) {
		// tempdetail.snp5 = tempdetail.snp5.substring(0,
		// tempdetail.snp5.length() - 1);
		// }
		// }
		snp5ls.clear();
		knp5ls.clear();
		snp5ls = null;
		knp5ls = null;

		if (tempdetail.snp5.equals("0,0,0,0,0,0,0,0,0,0,0,0")) {
			return;
		}
		// pedoDetailInfo.datavalue.add(tempdetail);
		PedoController.GetPedoController(this).insertOrUpdatePedoDetail(deviceId, pedoDetailInfo);
		Logger.d("cjz", tempdetail.start_time + "|" + tempdetail.level2p5 + "|" + tempdetail.snp5 + "|" + tempdetail.knp5);
	}

	/**
	 * 初始化每小时每五分钟的详包数据
	 */
	private void initNewPedoDetail() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		runPedoDetailInfo = new PedoDetailInfo();
		runPedoDetailInfo.date = DateFormatUtils.DateToString(currentdate, FormatType.DateShot);
		DataDetailPedo tempdetail = new DataDetailPedo();
		tempdetail.start_time = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		runPedoDetailInfo.datavalue.add(tempdetail);
		if (null != snp5ls) {
			snp5ls.clear();
			snp5ls = null;
		}
		if (null != knp5ls) {
			knp5ls.clear();
			knp5ls = null;
		}
		snp5ls = new ArrayList<Integer>();
		knp5ls = new ArrayList<Integer>();
		int tempMinutes = calendar.get(Calendar.MINUTE);
		int temp = (int) Math.floor(tempMinutes / 5);
		for (int i = 0; i < temp; i++) {
			snp5ls.add(0);
			knp5ls.add(0);
		}
	}

	/**
	 * 计算5分钟祥包
	 * 
	 * @param tempStep
	 * @param tempCal
	 */
	private void addStepPerFiveMinute(int stepSum, int stepCal) {

		int tempStep = stepSum - laststepdetail;
		int tempCal = stepCal - lastcaldetail;
		laststepdetail = stepSum;
		lastcaldetail = stepCal;

		snp5ls.add(tempStep);
		knp5ls.add(tempCal);
		// Log.d("tttttttttt", snp5ls.toString() + "=====" + knp5ls.toString());
	}

	/**
	 * 保存并上传简要包数据
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:17:59
	 */
	private void insertAndUploadPedo() {
		AddPedoData(todayPedoInfo, todayTotalStep, todayTotalCal, todayTotalDistance, todayTotalTime);
		insertSumPedoData(todayPedoInfo);
		// uploadPed(todayPedoInfo);
		uploadData();
	}

	/**
	 * 上传运动简要包 \ 上传详情包，取三个小时内的数据进行上传，如果遇到夸天情况，只上传本天内的数据
	 * 
	 * @return
	 */
	private void uploadData() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentdate);
		int nowhour = calendar.get(Calendar.HOUR_OF_DAY);
		uploadPed(todayPedoInfo);
		PedoDetailInfo detail = PedoController.GetPedoController(getBaseContext()).getPedoDetailByDay(currentdate, deviceId,
				nowhour - 3, nowhour);
		// boolean pedoResult = UploadManager.uploadPedo(todayPedoInfo);

		uploadPedDetail(detail, deviceId);

		// boolean pedoDetailResult = UploadManager.uploadPedoDetail(detail,
		// deviceId);
		// return pedoResult && pedoDetailResult;
		// return pedoDetailResult;
	}

	// =============add start=============
	/**
	 * 上传运动详细包
	 * 
	 * @param pedoDetailList
	 * @param deviceId
	 */
	public void uploadPedDetail(PedoDetailInfo pedoDetailList, String deviceId) {
		if (null == pedoDetailList || null == deviceId) {
			return;
		}
		for (DataDetailPedo detaiPedo : pedoDetailList.datavalue) {
			String collectDate = DateFormatUtils.ChangeFormat(pedoDetailList.date, FormatType.DateShot,
					FormatType.DateWithUnderline);
			PedometerJsonCreat dJsonCreat = new PedometerJsonCreat();
			mUploadDetailList = new ArrayList<NameValuePair>();
			dJsonCreat.appDetailJson(detaiPedo.start_time, detaiPedo.snp5, detaiPedo.knp5, collectDate);
			dJsonCreat.httpJsonDetailWithVersion(collectDate, deviceId, "stepDetail", getVersion());
			mUploadDetailList = dJsonCreat.jsonsend();
			Log.e("Service", "发送的数据：" + mUploadDetailList.toString());
			new Thread() {
				public void run() {
					SimplePostInfo info = new SimplePostInfo();
					dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadDetailList);
					// Intent intent = new Intent(UPLOADSTATUS_ACTION);
					// if (dataFromPHP == 0) {
					// intent.putExtra(
					// UPLOAD_STATUS,
					// getResources().getString(
					// R.string.phonestep_uploadsuccess));
					// } else {
					// intent.putExtra(
					// UPLOAD_STATUS,
					// getResources().getString(
					// R.string.phonestep_uploadfailed));
					// }
					// sendBroadcast(intent);
				};
			}.start();
		}

	}

	// =============add end=============

	/**
	 * 初始化user
	 * 
	 * @version 1.0.0
	 * @author Xiao
	 */
	private class InitAccountRunnable implements Runnable {
		private Context context;
		private String userUid;

		public InitAccountRunnable(Context context, String userUid) {
			this.context = context;
			this.userUid = userUid;
		}

		@Override
		public void run() {

			DataSyn dataSyn = DataSyn.getInstance();
			Log.d("cmccsi.mhealth.portal.sports", "userUid = " + userUid);
			dataSyn.setUserUid(userUid);
			dataSyn.loadServerInfo(context);
			dataSyn.loadUserInfoNotInThread(context);
			// dataSyn.loadClubId(context);
			DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
			dataSyn.getDeviceListData(deviceListInfo);

			PreferencesUtils.putString(context, SharedPreferredKey.APPVERNAME, Config.getVerName(context));
			restoreDeviceInfo();
		}
	}

	/**
	 * 保存设备信息到 sharedpreference中
	 */
	private void restoreDeviceInfo() {
		String Address = "";
		DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
		DeviceInfo deviceInfo_now = null;
		boolean have_bracelet_device = false;
		for (DeviceInfo deviceInfo : deviceListInfo.datavalue) {
			// 判断是否有手环设备
			if (Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				have_bracelet_device = true;
				Address = deviceInfo.deviceSerial.substring(2);
				if (deviceInfo != null) {
					Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					editor.putString(SharedPreferredKey.DEVICE_TOKEN, deviceInfo.deviceToken);
					editor.putString(SharedPreferredKey.DEVICE_NUMBER, deviceInfo.deviceNumber);
					editor.putString(SharedPreferredKey.DEVICE_VERSION, deviceInfo.deviceVersion);
					editor.commit();
				}
			}
			// 获取当前绑定设备
			if (Integer.valueOf(deviceInfo.isUsed) == 1) {
				deviceInfo_now = deviceInfo;
			}
		}

		PreferencesUtils.putBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, have_bracelet_device);
		PreferencesUtils.putString(this, SharedPreferredKey.DEVICE_ADDRESS, Address);
		if (deviceInfo_now != null) {
			if (Common.getDeviceType(deviceInfo_now.deviceSerial, deviceInfo_now.productPara) != DeviceConstants.DEVICE_MOBILE_STEP) {
				this.stopSelf();
			}
			Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(SharedPreferredKey.DEVICE_ID, deviceInfo_now.deviceSerial);
			editor.putString(SharedPreferredKey.DEVICE_NAME, deviceInfo_now.productName);
			editor.putString(SharedPreferredKey.DEVICE_MODEL, deviceInfo_now.productPara);
			editor.putInt(SharedPreferredKey.DEVICE_TYPE,
					Common.getDeviceType(deviceInfo_now.deviceSerial, deviceInfo_now.productPara));
			editor.commit();
		} else {
			Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(SharedPreferredKey.DEVICE_ID, null);
			// editor.putString(SharedPreferredKey.DEVICE_NAME, null);
			// editor.putString(SharedPreferredKey.DEVICE_MODEL, null);
			// editor.putInt(SharedPreferredKey.DEVICE_TYPE,
			// Common.getDeviceType(null));
			editor.commit();
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// ////////////////////////////////////////修改的记步算法////////////////////////////////////////////////
	/**
	 * 线程的例子
	 * 
	 * @author luckchoudog
	 */
	class GetAccThread1 extends Thread {
		private StepDetector mDetector;
		private UserInstance userInstance;
		private int[] mAcc = new int[75];// 从陀螺仪中获取到的数据
		private int[] ft = new int[8];
		private int[] ltemp = new int[20];
		private int cnt = 0;
		private int samp = 0;
		private int ltemp_index = 0;
		private int startflag = 0;
		private int max_in20 = -1000;
		private int min_in20 = 1073741823;
		private int max = -1000;
		private int min = 1073741823;
		private int thres = 1000;
		private int oldthres = 1000;
		private int threchangeflag = 0;
		private int timecount = 0;
		private int vppchangecount = 0;
		private int vpp = 0;
		private int delta_vpp = 0;
		private int new_fixed = 0;
		private int old_fixed = 0;
		private int mode = 0;
		private int lastmode = 0;
		private int lastsearch = 0;
		private int searchcount = 0;//
		private int samp1 = 0;
		private int search_count = 5;// 连续运动10步后，开始正式记步
		private int last_step = 0;
		// private float fdistance;
		/**
		 * 从线程开始启动到现在走的步数、消耗的卡路里、距离、运动时间、运动过程中的运动强度
		 */
		private int step = 0;
		private double calory = 0;
		private double distance = 0;
		private double time = 0;
		private int exercise_intensity_normally = 0;
		private int exercise_intensity_fairly = 0;
		private int exercise_intensity_very = 0;
		/*
		 * 每一步的运动强度，记录各个运动强度的步数
		 */
		private int EXERCISE_INTENSITY_MIN = 5;// 感应器运动强度小于5，舍去
		private int EXERCISE_INTENSITY_MAX = 45;// 感应器运动强度大于45，舍去

		public GetAccThread1(final StepDetector mDetector, UserInstance userInstance) {
			this.mDetector = mDetector;
			this.userInstance = userInstance;
			for (int i = 0; i < 4; i++) {
				ft[i] = 4096;
			}
			for (int i = 0; i < 20; i++) {
				ltemp[i] = 0;
			}
			// 初始化计步器数据
			step = 0;
			calory = 0;
			distance = 0;
			time = 0;
			exercise_intensity_normally = 0;
			exercise_intensity_fairly = 0;
			exercise_intensity_very = 0;
		}

		public void stopMyThread() {
			GetAccThread1 tmpThread = mGetAccThread;
			mGetAccThread = null;
			if (tmpThread != null) {
				tmpThread.interrupt();
			}
		}

		private Timer mTimer = new Timer(); 
		public void run() {
			if (mGetAccThread == null) {
				return; // stopped before started.
			}
			try {
				int data_g;
				int max_data , min_data;
				while (true) {
					max_data = -1;
					min_data = 9999;
					for (int i = 0; i < 3; i++) {
						mAcc[cnt++] = mDetector.getAcc().get(i);
					}
					if (cnt >= 45) {
						cnt = 0;
						for (int i = 0; i < 15; i++) {
							data_g = (int) (mAcc[i * 3 + 0] * mAcc[i * 3 + 0] + mAcc[i * 3 + 1] * mAcc[i * 3 + 1] + mAcc[i * 3 + 2]
									* mAcc[i * 3 + 2]);
							if (data_g > max_data) {
								max_data = data_g;
							}
							if (data_g < min_data) { 
								min_data = data_g;
							}
							float xi =  max_data - min_data;
							step_process(userInstance.getGender(), userInstance.getAge(), userInstance.getHeight(),
									userInstance.getWeight(), data_g);
							data_g = 0; 
							if (i % 5 == 0) {
								i++;
							}
						}
						if ( (max_data - min_data) < 300) {
							float xi =  max_data - min_data;
							if (mDetector.getIsStepDetectorRun()) {
								mDetector.stopStepDetector();
								//延时2000ms后启动陀螺仪 
								mTimer.schedule(new TimerTask() {
									@Override
									public void run() {
										if (!mDetector.getIsStepDetectorRun()) {
											mDetector.startStepDetector();
											Log.e("test", "awake!");
										}
									}
								}, 2000);
							}
							sleep(1000);
							Log.e("test", "fall asleep!");
							continue;
						}
						// TODO
						// 返回数据step步数、calory卡路里、distance运动距离、time运动时间
						// exercise_intensity_normally运动强度(一般)、exercise_intensity_fairly运动强度(中等)
						// exercise_intensity_very运动强度(剧烈)
						Log.v("test", "step:" + step + " calory:" + calory + " distance:" + distance + " time:" + time
								+ " lightly:" + exercise_intensity_normally + " fairly:" + exercise_intensity_fairly + " very:"
								+ exercise_intensity_very);
						sendDataByBoardcast((int) step, (int) calory, (int) distance, (int) exercise_intensity_normally,
								(int) exercise_intensity_fairly, (int) exercise_intensity_very);
					}
					sleep(40);
					Thread.yield();
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("Stopped by ifInterruptedStop()");
					}
				}
			} catch (Throwable t) {
				Log.v("test", "-----------线程干掉---重启线程---------" + t);
//				if (null != mGetAccThread) {
//					mGetAccThread.stopMyThread();
//					mGetAccThread = null;
//				}
//				mGetAccThread = new GetAccThread1(mDetector, userInstance);
//				mGetAccThread.setDaemon(true);
//				mGetAccThread.start();
			}
		}

		/**
		 * 运动过程，需要传入从陀螺仪中解析的数据
		 * 
		 * @param gender
		 *            性别：1男，2女
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param weight
		 *            体重
		 * @param data_g
		 *            从陀螺仪中解析的数据
		 */
		private void step_process(int gender, int age, double height, double weight, int data_g) {
			int result;
			double chstep_len = 0;
			double rmr = 0;
			double cdif = 0;
			double bmr = 0;
			ft[0] = ft[1];
			ft[1] = ft[2];
			ft[2] = ft[3];
			ft[3] = ft[4];
			ft[4] = data_g;
			result = ft[0] + 2 * ft[1] + 3 * ft[2] + 2 * ft[3] + ft[4];
			samp++;
			if (samp >= 80) {
				samp = 60;
			}
			if (samp <= 20) {
				ltemp[ltemp_index] = result;
				if (ltemp_index < 19) {
					ltemp_index++;
				}
			} else {
				for (int i = 0; i < 19; i++) {
					ltemp[i] = ltemp[i + 1];
				}
				ltemp[19] = result;
				if (samp >= 51) {
					startflag = 1;
				}
				if (samp >= 31) {
					max_in20 = (ltemp[19] > max_in20) ? ltemp[19] : max_in20;
					min_in20 = (ltemp[19] < min_in20) ? ltemp[19] : min_in20;
					max = (ltemp[19] > max) ? ltemp[19] : max;
					min = (ltemp[19] < min) ? ltemp[19] : min;
					vppchangecount++;
					samp1++;
					if (samp1 > 19) {
						vpp = max_in20 - min_in20;
						max_in20 = -1000;
						min_in20 = 1073741823;
						samp1 = 0;
						oldthres = thres;
						thres = (4 * max + 3 * min) / 7;
						// thres = (max + min) / 2;
						delta_vpp = max - min;
						if (delta_vpp > 14560) {
							if (delta_vpp > 57600) {
								thres = (int) ((max + min) / 2.6);
							} else {
								thres = (int) ((max + min) / 2.1);
							}
						}
						threchangeflag = 1;
						max = -1000;
						min = 1073741823;
						vppchangecount = 0;
						int dif = step - last_step;
						if (dif > 0 && lastmode == 0)
							dif = searchcount - lastsearch;
						if (dif > 0)
							lastmode = 1;
						else
							lastmode = 0;
						if (dif <= 10 && dif > 0) {
							last_step = step;
							lastsearch = searchcount;
						} else {
							last_step = step;
							lastsearch = searchcount;
						}
					}
				}
				if (startflag == 1) {
					timecount++;
				}
				if (!(threchangeflag == 1 && vppchangecount < 5)) {
					threchangeflag = 0;
				}
				if (startflag != 0 && vpp >= 14600)// 8832)
				{
					old_fixed = ltemp[9];
					new_fixed = ltemp[10];
					if ((old_fixed < thres && thres < new_fixed)
							|| (threchangeflag == 1 && old_fixed <= oldthres && oldthres <= new_fixed)) {
						if (timecount >= EXERCISE_INTENSITY_MIN && timecount <= EXERCISE_INTENSITY_MAX) {
							int localmax = 0;
							int localmin = 0;
							localmin = ltemp[9];
							localmax = ltemp[9];
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 + locali] < ltemp[9 + locali - 1]) {
									break;
								} else
									localmax = ltemp[9 + locali];
							}
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 - locali] > ltemp[9 - locali + 1]) {
									break;
								} else
									localmin = ltemp[9 - locali];
							}
							if (localmax - localmin < 5700)// 6760)
								return;
							if (mode == 1) {
								step++;
								bmr = balCaculate(gender, age, height, weight);
								rmr = calCaculate(age, height, 1, ltemp);
								calory += rmr * bmr;
								/* 距离的计算 */
								cdif = (float) (result / 10000.0);
								chstep_len = steplen(gender, age, height, cdif);
								distance += (chstep_len / 100.0);
								if (cdif < 10) {
									exercise_intensity_normally += 1;
									time += 0.55;
								} else if (cdif <= 17) {
									exercise_intensity_fairly += 1;
									time += 0.5;
								} else {
									exercise_intensity_very += 1;
									time += 0.45;
								}
							} else {
								if (searchcount == 0) {
									searchcount = 2;
								} else {
									searchcount++;
								}
								if (searchcount >= search_count) {
									mode = 1;
									step += searchcount;
									cdif = (float) (result / 10000.0);
									chstep_len = steplen(gender, age, height, cdif);
									distance = (distance + (search_count * chstep_len / 100.0));
									bmr = balCaculate(gender, age, height, weight);
									rmr = calCaculate(age, height, search_count, ltemp);
									calory += rmr * bmr;
									exercise_intensity_normally += search_count;
									time += (search_count * 0.5);
								}
							}
							timecount = 0;
						} else {
							mode = 0;
							searchcount = 0;
							timecount = 0;
						}
					}
				}
			}
		}

		/**
		 * 根据性别年龄身高，陀螺仪的运动强度算每一步的步长
		 * 
		 * @param gender
		 *            性别
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param cdif
		 *            陀螺仪的运动强度
		 * @return 步长
		 */
		private double steplen(int gender, int age, double height, double cdif) {
			double fheight = 0, chstep_len = 0;
			fheight = (float) height;
			if (gender == 0) {
				if (cdif <= 4)
					chstep_len = (float) ((0.2 + 0.05 * cdif) * fheight);
				else if (cdif <= 5.1)
					chstep_len = (float) ((0.25 + 0.05 * cdif) * fheight);
				else
					chstep_len = (float) ((0.3 + 0.05 * cdif) * fheight);
			} else {
				if (cdif <= 4)
					chstep_len = (float) ((0.2 + 0.05 * cdif) * fheight);
				else if (cdif <= 5.6)
					chstep_len = (float) ((0.22 + 0.05 * cdif) * fheight);
				else
					chstep_len = (float) ((0.24 + 0.05 * cdif) * fheight);
			}
			chstep_len = chstep_len > 98 ? 98 : chstep_len;
			chstep_len = chstep_len < 50 ? 50 : chstep_len;
			return chstep_len;

		}

		/**
		 * 计算卡路里消耗，根据年龄身高和一定时间内的运动强度和步数
		 */
		private double calCaculate(int age, double height, int dif, int[] ltemp2) {
			int accsum, acctmp;
			double rmr = 0, cstep_len = 0;
			int i;
			final int BUFLEN = 20;
			accsum = 0;
			for (i = 0; i < BUFLEN; i++) {
				accsum += ltemp[i];
			}
			accsum = accsum / BUFLEN;
			acctmp = accsum / 167 - 222;
			if (acctmp > 0)
				rmr = (float) Math.sqrt(acctmp);
			else
				rmr = 0;
			rmr = rmr > 0 ? rmr : 0;
			if (dif < 4)
				cstep_len = (float) 0.56;
			else if (dif < 5)
				cstep_len = (float) 0.64;
			else
				cstep_len = (float) 0.72;
			rmr = rmr + (cstep_len * (float) (dif * dif));
			return rmr;
		}

		/**
		 * 建模数据算法，根据性别，年龄，身高，体重等建模
		 * 
		 * @param gender
		 *            性别：1男，2女
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param weight
		 *            体重
		 * @return 模型系数
		 */
		private double balCaculate(int gender, int age, double height, double weight) {
			double bmr = 0;
			double bsa = 0;
			if (gender == 1) {
				bmr = 134 * weight + 48 * height - 57 * age + 883;
				bsa = 61 * height + 127 * weight - 698;
			} else {
				bmr = 92 * weight + 31 * height - 43 * age + 4476;
				bsa = 59 * height + 126 * weight - 461;
			}
			bmr = bmr * bsa / 500000 / 100000;
			return bmr;
		}
	}

	/**
	 * 
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param className
	 *            ：判断的服务名字：包名+类名
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext) {
//		boolean isRunning = false;
//		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
//		if (!(serviceList.size() > 0)) {
//			return false;
//		}
//		for (int i = 0; i < serviceList.size(); i++) {
//			if (serviceList.get(i).service.getClassName().equals(StepService.class.getName()) == true) {
//				isRunning = true;
//				break;
//			}
//		}
		
		return isRunning;
	}
}
