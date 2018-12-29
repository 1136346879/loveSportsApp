package cmccsi.mhealth.app.sports.pedo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.MainFragmentActivity2;
import cmccsi.mhealth.app.sports.appversion.HistorySportActivity;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.app.sports.bean.GoalInfo;
import cmccsi.mhealth.app.sports.bean.GoalNetInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.PedometorUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.device.DeviceFactory;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.phonesteplib.StepController;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.tabhost.TabBaseFragment;
import cmccsi.mhealth.app.sports.view.CenterRollingBall;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.DownFlashView;
import cmccsi.mhealth.app.sports.view.PedoCalProcess;
import cmccsi.mhealth.app.sports.view.PopMenu;
import cmccsi.mhealth.app.sports.view.DownFlashView.RefreshListener;
import cmccsi.mhealth.app.sports.R;


public class PedometerActivityTest extends BaseActivity implements OnClickListener, RefreshListener {
	private final String TAG = "PedometerActivityTest";
	private final String UPDATERESULT = "updateresult";
	private final int REQUEST_OPEN_BT_CODE=99;

	private TextView mTv_Duration;// 运动时长
	private TextView mTv_distance;// 距离
	private TextView mTv_stepnumofday;// 步数
	private TextView mTv_percentstep;// 步数百分比
	private TextView mTv_currentEquipment; // 当前设备
	private TextView mTv_displaydate;// 当前显示日期
	private TextView tv_showGoal;// 距离成就

	private ImageView historySport_imageView;// 左翻页
	private ImageView mIv_leftday;// 左翻页
	private ImageView mIv_rightday;// 右翻页
	private ImageView iv_showGoal;// 成就图片
	private ImageView mIv_reset;
	private ImageView mIv_bloothDisconnect;//手环断开连接
	private GestureDetector mGestureDetector;

	private Button mBtn_cal_achievement;// 卡路里成就切换
	private Button mBtn_distance_achievement;// 距离成就切换

	private RelativeLayout mRl_distance_achievement;// 距离成就
	private RelativeLayout mRelativeLayoutProgress;// 圆形进度图片

	private LinearLayout mLl_cal_achievement;// 卡路里成就显示
	private LinearLayout mTopLayout;

	private PopMenu mPopmenu_more;// 更多按钮
	private PedoCalProcess mPedoCalProcess;// 卡路里成就
	private DownFlashView mRefreshableView;

	private String mDeviceId = "";// 设备id
	private int mDeviceType = 0;// 设备类型
	private int mTargetStep = 10000;// 目标步数

	private PedometorDataInfo currentPedo;// 当前页面显示的数据
	private PedometorDataInfo bluetoothPedo;// 当前页面显示的数据
	private Date mDisplayDate;// 展示日期
	private Date mToday;//当天日期 为了判断跨天
	
	private boolean isupdate=true;//下拉是否刷新 专用于手环
	private boolean isFirstOnstart=true;//onStart中连接设备，除第一次  为下载数据后连接
	private DeviceFactory mDeviceManager;
	
	private int bluetoothState=0;//蓝牙设备连接状态 0不是蓝牙设备 1已连接 2已断开

	private int mDateDiff=0;//翻页日期 判断前翻30天提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedo);

		initView();
		mDateDiff=0;
		mDisplayDate = new Date(System.currentTimeMillis());
		mToday = new Date(System.currentTimeMillis());
		setPercentPedoData(0, 1000);
		
		mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0);
		mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
		showCurrentEquipment(mDeviceType);
		if(mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
		{
			bluetoothState=2;
			setBloothDisconnectState(false,true); 
		}
		mDeviceManager=DeviceFactory.getDeviceInstance(this, mbleAlertHandler);
		mRefreshableView.startRefreshDirectly();
	}
	public PedometerActivityTest() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onStart() {
		super.onStart();
		Logger.d("cjz", "PedometerActivityTest onStart");
		try {
			if(!DateFormatUtils.isToday(mToday)){
				mDisplayDate=new Date(System.currentTimeMillis());
				mToday=new Date(System.currentTimeMillis());
				displayPedoData(null);				
			}
			setLeftAndRightDisplayStatus();
			if(mDeviceType!=PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0))
			{
				isupdate=true;
				Logger.e("cjz", "设备 changed");				
				mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0);
				mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
				showCurrentEquipment(mDeviceType);				
				//切换设备后重新查询
				currentPedo = queryPedometor(mDeviceId, new Date(System.currentTimeMillis()));
				displayPedoData(currentPedo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(mDeviceType!=DeviceConstants.DEVICE_BRACLETE_BEATBAND
				&&mDeviceType!=DeviceConstants.DEVICE_BRACLETE_JW
				&&mDeviceType!=DeviceConstants.DEVICE_BRACLETE_JW201
				&&DateFormatUtils.isToday(mDisplayDate))
		{
			bluetoothState=0;
			setBloothDisconnectState(true,false);
			bluetoothPedo=null;
		}else{
			setBloothDisconnectState(false,true);
		}
		if(!isFirstOnstart){
			mDeviceManager.initDevice(mDeviceType);
			mDeviceManager.connect(mDeviceId);
		}else{
			isFirstOnstart=false;
		}
		// 距离成就
		GoalInfo info = GoalInfo.getInstance(this);
		iv_showGoal.setImageResource(info.type.getImgRes());
		tv_showGoal.setText(info.getGoalReportInTime(this));
		new LoadAchievementTask().execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.i("PedometerActivity", "---onResume");
		//绑定Service  
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		mDeviceManager.disConnect();
	}

	/**
	 * 初始化页面控件 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:22:22
	 */
	private void initView() {
		ConstantsBitmaps.initRunPics(this);// 圆形进度相关图片
		mRelativeLayoutProgress = (RelativeLayout) findViewById(R.id.Progress_center_rote1);
		// 数据展示
		mTv_Duration = (TextView) findViewById(R.id.tv_Duration);
		mTv_distance = (TextView) findViewById(R.id.tv_distance);
		mTv_stepnumofday = (TextView) findViewById(R.id.tv_stepnumofday);
		mTv_percentstep = (TextView) findViewById(R.id.tv_percentstep);
		mTv_currentEquipment = (TextView) findViewById(R.id.tv_currentEquipment);
		mTv_displaydate = (TextView) findViewById(R.id.tv_displaydate);
		// 左右翻页
		mIv_leftday = (ImageView) findViewById(R.id.iv_leftday);
		mIv_leftday.setOnClickListener(this);
		mIv_rightday = (ImageView) findViewById(R.id.iv_rightday);
		mIv_rightday.setOnClickListener(this);
		historySport_imageView=(ImageView) findViewById(R.id.historySport_imageView);// 回到当日页面
		historySport_imageView.setOnClickListener(this);
		mIv_reset=(ImageView) findViewById(R.id.iv_reset);// 回到当日页面
		mIv_reset.setVisibility(View.INVISIBLE);
		mIv_reset.setOnClickListener(this);
		
		// 运动成就
		mBtn_cal_achievement = (Button) findViewById(R.id.btn_cal_achievement);
		mBtn_cal_achievement.setOnClickListener(this);
		mBtn_distance_achievement = (Button) findViewById(R.id.btn_distance_achievement);
		mBtn_distance_achievement.setOnClickListener(this);
		mRl_distance_achievement = (RelativeLayout) findViewById(R.id.rl_distance_achievement);
		mLl_cal_achievement = (LinearLayout) findViewById(R.id.ll_cal_achievement);
		
		mIv_bloothDisconnect=(ImageView)findViewById(R.id.ig_disconnect);
		mIv_bloothDisconnect.setOnClickListener(this);
		// 返回键
		ImageButton mImageButtonBack = (ImageButton) findViewById(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(this);
		//标题
		TextView tv_Title=(TextView)findViewById(R.id.textView_title);
		tv_Title.setText(getResources().getString(R.string.pedometer_title));
		// 更多键
		mTopLayout = (LinearLayout) findViewById(R.id.ll_pedomain);
		initMorePopmenu();
		ImageButton mImageButtonUpdate = (ImageButton) findViewById(R.id.imageButton_title);
		mImageButtonUpdate.setBackgroundResource(R.drawable.menu_more);
		mImageButtonUpdate.setVisibility(View.VISIBLE);
		mImageButtonUpdate.setOnClickListener(this);
		// 距离成就
		iv_showGoal = (ImageView) findViewById(R.id.iv_showGoal);
		iv_showGoal.setOnClickListener(this);
		tv_showGoal = (TextView) findViewById(R.id.tv_showGoal);
		// 卡路里成就
		initPedoCalProcess();

		mRefreshableView = (DownFlashView) findViewById(R.id.refresh_root);
		mRefreshableView.setRefreshListener(this);
		
		FrameLayout mFl_pedoprogress=(FrameLayout)findViewById(R.id.fl_pedoprogress);
		mGestureDetector=new GestureDetector(getBaseContext(),new PedoGestureListener());
		mFl_pedoprogress.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				return mGestureDetector.onTouchEvent(event);
			}
		});
		
		
	}

	/**
	 * 初始化更多菜单列表 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午10:09:34
	 */
	private void initMorePopmenu() {
		List<CommonBottomMenuItem> menulist = new ArrayList<CommonBottomMenuItem>();
		menulist.add(new CommonBottomMenuItem(1, "分享运动简报", R.drawable.menuitem_share));
		menulist.add(new CommonBottomMenuItem(1, "切换设备", R.drawable.menuitem_changequipment));
		mPopmenu_more = new PopMenu(this, menulist);
		mPopmenu_more.setOnItemClickListener(menuItemClick);
	}

	/**
	 * 初始化卡路里成就进度 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:16:44
	 */
	private void initPedoCalProcess() {
		mPedoCalProcess = (PedoCalProcess) findViewById(R.id.pcp_pedocalprocess);
		mPedoCalProcess.setProcess(0);
		mPedoCalProcess.setExplainText(PedometorUtils.calorieToFoodDescription(0));
		mPedoCalProcess.setProcessPicture(R.drawable.cal_archive_process);
        int[] CoordinateX={25,45,65,85};
        mPedoCalProcess.setCoordinateX(CoordinateX);
        int[] normalPictures={R.drawable.cal_achieve1_normal,R.drawable.cal_achieve2_normal
        		,R.drawable.cal_achieve3_normal,R.drawable.cal_achieve4_normal}	;
        mPedoCalProcess.setNormalMilePictures(normalPictures);
        int[] overPictures={R.drawable.cal_achieve1_press,R.drawable.cal_achieve2_press
        		,R.drawable.cal_achieve3_press,R.drawable.cal_achieve4_press,}	;
        mPedoCalProcess.setOverMilePictures(overPictures);
        mPedoCalProcess.setExplainPictures(R.drawable.cal_archive_explain);
	}

	/**
	 * 更新运动数据 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:36:50
	 */
	private void updatePedoData() {
		new Thread() {
			public void run() {
				// 取当前数据库最后一条
				PedometorDataInfo data = PedoController.GetPedoController(getBaseContext()).getLatestPedometer(
						mDeviceId);

				String startDate = "";
				String endDate = DateFormatUtils
						.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot);
				if (data == null)// 数据库没有数据则更新n天
				{
					startDate = DateFormatUtils.AddDays(endDate, -30, FormatType.DateShot);
				} else// 有数据则更新数据库时间到当前时间的数据
				{
					startDate = DateFormatUtils.ChangeFormat(data.createtime, FormatType.DateLong, FormatType.DateShot);
				}

				PedometorListInfo reqData = new PedometorListInfo();
				int result = DataSyn.getInstance().getPedoInfoByTimeArea(null, null, startDate, endDate, reqData);
				if (result == 0) {
					PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(reqData);
				}
				Bundle bdl = new Bundle();
				bdl.putInt(UPDATERESULT, result);
				Message msg = new Message();
				msg.what = 0;
				msg.setData(bdl);
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * ui线程处理
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try{
			switch (msg.what) {
			case 0:// 更新完毕
				if (msg.getData().getInt(UPDATERESULT) == 0)// 成功
				{
//					Toast.makeText(getBaseContext(), getResources().getString(R.string.phonestep_uploadsuccess),
//							Toast.LENGTH_SHORT).show();
					currentPedo = queryPedometor(mDeviceId, new Date());
					if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
							||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
							||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
							&&bluetoothPedo!=null)
					{
						displayPedoData(bluetoothPedo);
					}
					else
					{
						displayPedoData(currentPedo);
					}
				} else {
					currentPedo = queryPedometor(mDeviceId, new Date());
					displayPedoData(currentPedo);
					Toast.makeText(getBaseContext(), getResources().getString(R.string.phonestep_uploadfailed),
							Toast.LENGTH_SHORT).show();
				}
				mDeviceManager.initDevice(mDeviceType);
				mDeviceManager.connect(mDeviceId);
				mRefreshableView.finishRefresh();
				break;
			case 1:
//				mIv_bloothDisconnect.setAnimation(null);
				setBloothDisconnectState(true,true);
				mRefreshableView.finishRefresh();
				break;
			default:
				break;
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	/**
	 * 展示运动数据 TODO
	 * 
	 * @param data
	 *            运动实体
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:18:48
	 */
	private void displayPedoData(PedometorDataInfo data) {
		try {
			String displayDate = DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithDiagonalNoYear);
			displayDate = displayDate + "(" + Common.GetWeekStr(mDisplayDate.getDay()) + ")";
			mTv_displaydate.setText(displayDate);

			mTargetStep = Integer.parseInt(PreferencesUtils.getString(this, SharedPreferredKey.TARGET_STEP, "10000"));

			String today = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()),
					FormatType.DateWithUnderline);
			if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning
					&& DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline).equals(today))// 手机计步开启状态从service中取数
			{
				
				String stepTime = formatHHMMfromSec(StepService.todayTotalTime);
				setNomalPedoData(stepTime, "0", String.valueOf(StepService.todayTotalCal),
						String.valueOf(StepService.todayTotalDistance));
				setPercentPedoData(StepService.todayTotalStep, mTargetStep);
				setCalArchivement(StepService.todayTotalCal);
				data.stepNum=String.valueOf(StepService.todayTotalStep);
				data.cal=String.valueOf(StepService.todayTotalCal);
				data.distance=String.valueOf(StepService.todayTotalDistance);
			} else {
				setNomalPedoData(data);
				if (data != null) {
					String stepSum = data.stepNum;
					try
					{
						setPercentPedoData(Integer.parseInt(stepSum), mTargetStep);
						setCalArchivement(Integer.parseInt(data.cal));
					}
					catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
				} 
				else
				{
					setPercentPedoData(0,mTargetStep);
					setCalArchivement(0);
				}
				
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示基本运动数据 TODO
	 * 
	 * @param data
	 *            运动数据实体
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:12:16
	 */
	private void setNomalPedoData(PedometorDataInfo data) {
		if (data != null) {
//			int intStepTime = getStepTimeFromPedometor(data);
			data.strength3=data.strength3==""?"0":data.strength3;
			data.strength4=data.strength4==""?"0":data.strength4;
			int tempstepTime=Integer.parseInt(data.strength2)+Integer.parseInt(data.strength3)+Integer.parseInt(data.strength4);
			String strStepTime = formatHHMMfromSec(tempstepTime);
			setNomalPedoData(strStepTime, data.yxbssum, data.cal, data.distance);
		} else {
			mTv_Duration.setText("00:00");
			mTv_distance.setText("0");
		}
	}

	/**
	 * 显示基本运动数据 TODO
	 * 
	 * @param steptime
	 *            时长
	 * @param yxbs
	 *            有效步数
	 * @param cal
	 *            卡路里
	 * @param distance
	 *            距离
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午3:32:29
	 */
	private void setNomalPedoData(String steptime, String yxbs, String cal, String distance) {
		mTv_Duration.setText(steptime);
		mTv_distance.setText(distance);

	}

	/**
	 * 设置运动百分比圆环与步数 TODO
	 * 
	 * @param value
	 *            步数
	 * @param max
	 *            最大步数
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午3:35:20
	 */
	private void setPercentPedoData(int value, int max) {
		CenterRollingBall mCenterRollingBall = new CenterRollingBall(this);
		mCenterRollingBall.setPics(ConstantsBitmaps.mBitmapBgCenterRound, ConstantsBitmaps.mBitmapPointRound);
		mCenterRollingBall.setMaxScore(max);
		mCenterRollingBall.setScore(value);
		mCenterRollingBall.showEndBall(true);
		mCenterRollingBall.showFrontArc(true);
		mCenterRollingBall.setCenterOffest(20);
		mCenterRollingBall.setAngelOffest(-120);
		mCenterRollingBall.invalidate();
		if(mCenterRollingBall!=null)
		{
			mRelativeLayoutProgress.removeAllViews();
			mRelativeLayoutProgress.addView(mCenterRollingBall);
		}
		int val = 0;
		if (max != 0) {
			val = value * 100 / max;
		}
		mTv_percentstep.setText(val + "%");
		mTv_stepnumofday.setText(String.valueOf(value));
	}

	/**
	 * 查询运动数据 TODO
	 * 
	 * @param deviceId
	 *            设备ID
	 * @param date
	 *            日期
	 * @return null
	 * @return DataPedometor 运动数据
	 * @author jiazhi.cao
	 * @time 下午3:46:39
	 */
	private PedometorDataInfo queryPedometor(String deviceId, Date date) {
		PedometorDataInfo data = null;
		data = PedoController.GetPedoController(this).getPedometerByDay(deviceId, date);
		return data;
	}

	/**
	 * 运动时间展示格式化 TODO
	 * 
	 * @param sec
	 *            秒
	 * @return
	 * @return String HH:MM格式
	 * @author jiazhi.cao
	 * @time 下午3:17:23
	 */
	private String formatHHMMfromSec(int sec) {
		String result = "";
		try {
			int stepTime = (int) Math.floor(sec / 60);
			result = String.format("%1$02d:%2$02d", (int) Math.floor(stepTime / 60), (int) stepTime % 60);
		} catch (NullPointerException enull) {
			enull.printStackTrace();
		} catch (IllegalFormatException eill) {
			eill.printStackTrace();
		}
		return result;
	}


	/**
	 * 显示设备信息 TODO
	 *
	 * @param deviceType
	 *            设备类型
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午6:03:11
	 */
	private void showCurrentEquipment(int deviceType) {
		String discription=Common.getDeviceDisplayName(deviceType);
		if(!discription.equals("")){
			mTv_currentEquipment.setText(discription+"计步");
		}else{
			mTv_currentEquipment.setText("");
		}
	}

	/**
	 * 开启手机计步 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:10:42
	 */
	private void startStepService() {
		StepController mStepController = new StepController();
		mStepController.setContext(getBaseContext());
		mStepController.startStepService(Config.SC_ACTION);
	}

	/**
	 * 设置显示运动数据的左箭头右箭头状态 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:05:11
	 */
	private void setLeftAndRightDisplayStatus() {
		// 当前显示数据日期小于系统日期显示右箭头
		String now = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateWithUnderline);
		String displayday = DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline);
		if (DateFormatUtils.compare_date(displayday, now) == 1) {
			mIv_rightday.setVisibility(View.VISIBLE);
			mIv_reset.setVisibility(View.VISIBLE);
		} else {
			mIv_rightday.setVisibility(View.INVISIBLE);
			mIv_reset.setVisibility(View.INVISIBLE);
		}
		// 当前显示数据日期大于本地库最早日期 显示左箭头
	}

	/**
	 * 设置卡路里成就 TODO
	 * 
	 * @param cal
	 *            卡路里
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:19:56
	 */
	private void setCalArchivement(int cal)
	{
		int calprocess=PedometorUtils.getCalpersent(mPedoCalProcess.getCoordinateX(),cal);
		Logger.i("cjz", "cal:"+cal+"  calprocess:"+calprocess);
		String calmessage=PedometorUtils.calorieToFoodDescription(cal);
		mPedoCalProcess.setExplainText(calmessage);
		mPedoCalProcess.setProcess(calprocess);
		mPedoCalProcess.invalidate();
	}

	@Override
	public void onRefresh(DownFlashView view) {
		if(DateFormatUtils.isToday(mDisplayDate))
		{
			if(isupdate)
			{
				if(mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
						||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
						||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201
						||mDeviceType==DeviceConstants.DEVICE_MOBILE_STEP)
				{
					isupdate=false;
				}
				updatePedoData();
			}
			else
			{
				mRefreshableView.setText("开始同步数据");
//				PedometorDataInfo data = PedoController.GetPedoController(getBaseContext()).getLatestPedometer(
//						mDeviceId);
				String startDate="";
				String endDate=DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
				//补传
				//不补传
				startDate=DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline)+" 00:00:00";
//				mDeviceManagerService.syncData(startDate, endDate);
//				mDeviceManager.initDevice(mDeviceType);
				mDeviceManager.syncData(startDate, endDate);;
				
			}
		}
		else
		{
			mRefreshableView.finishRefresh();
		}
	}
	
	private Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DeviceConstants.CONNECTED_SUCCESS:
//				ToastUtils.showToast(getBaseContext(), "手环连接成功");
				setBloothDisconnectState(true,false);
//				mDeviceManagerService.startRealTime();
				mDeviceManager.startRealTime();
				break;
			case DeviceConstants.CONNECTED_FAIL:
				Bundle edataconectfail = msg.getData();
				if(null!=edataconectfail&&edataconectfail.getString("CONNECTED_FAIL")!=null
						&&!edataconectfail.getString("CONNECTED_FAIL").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edataconectfail.getString("CONNECTED_FAIL"));
				}
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.DEVICE_RESET:
				Bundle edatareset = msg.getData();
				if(null!=edatareset&&!edatareset.getString("MSG_EXCEPT").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edatareset.getString("MSG_RESET"));
				}
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.EXCEPTION_CONNECT:
				Bundle edata = msg.getData();
				if(edata!=null&&edata.getString("EXCEPTION_CONNECT")!=null
						&&!edata.getString("EXCEPTION_CONNECT").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edata.getString("EXCEPTION_CONNECT"));
				}
				
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.TRANSPORT_PERCENT:
				Bundle data = msg.getData();
				mRefreshableView.setText(data.getString("MSG_TRANSPORT"));
				break;
				
			case DeviceConstants.NOMAL_MESSAGE:
				Bundle data1 = msg.getData();
				mRefreshableView.setText(data1.getString("MSG_STATUS"));
				break;
				
			case DeviceConstants.DEVICE_POWER:
				Bundle data6 = msg.getData();
				int PowerPercent = data6.getInt("MSG_POWER");
				ToastUtils.showToast(getBaseContext(), "手环电量：" + PowerPercent + "，电量较低");
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.UPLOAD_SUCCESS:
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.UPLOAD_FAIL:
				ToastUtils.showToast(getBaseContext(), "数据上传失败");
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.REALTIME_PEDO:
				Bundle data10 = msg.getData();
				if(bluetoothPedo==null)
				{
					bluetoothPedo=new PedometorDataInfo();
					bluetoothPedo.createtime=DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
					bluetoothPedo.date=DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline);
					bluetoothPedo.deviceId=mDeviceId;
				}
				if(!data10.getString("STEP").equals(bluetoothPedo.stepNum))
				{
					
					bluetoothPedo.stepNum=data10.getString("STEP");
					bluetoothPedo.cal=data10.getString("CAL");
					bluetoothPedo.distance=data10.getString("DISTANCE");
					bluetoothPedo.strength2=data10.getString("STEPTIME");
					Logger.d("cjz", "已实时 步数："+bluetoothPedo.stepNum);
					if(DateFormatUtils.isToday(mDisplayDate))
					{
						displayPedoData(bluetoothPedo);
					}
					currentPedo=bluetoothPedo;//为了分享
				}
				break;
			default:
				setBloothDisconnectState(true,true);
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:// 返回
			backToMain();

			break;
		case R.id.imageButton_title:// 更多
			mPopmenu_more.showAsDropDown(v);
			break;
		case R.id.btn_cal_achievement://卡路里成就
			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_pedo_cal),null, null, null);
			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_pedo_distance_normal),null, null, null );
			mBtn_cal_achievement.setTextColor(getResources().getColor(R.color.coffee));
			mBtn_distance_achievement.setTextColor(getResources().getColor(R.color.gray0));
			mRl_distance_achievement.setVisibility(View.GONE);
			mLl_cal_achievement.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_distance_achievement://距离成就
			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_pedo_cal_normal),null, null, null );
			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_pedo_distance),null, null, null );
			mBtn_cal_achievement.setTextColor(getResources().getColor(R.color.gray0));
			mBtn_distance_achievement.setTextColor(getResources().getColor(R.color.coffee));
			mRl_distance_achievement.setVisibility(View.VISIBLE);
			mLl_cal_achievement.setVisibility(View.GONE);
			break;
		case R.id.iv_showGoal:// 距离成就
			Intent intent = new Intent();
			intent.putExtra("intent", 13);
			intent.setClass(getBaseContext(), TabBaseFragment.class);
			startActivity(intent);
			break;
		case R.id.iv_leftday:// 左翻页
			displayPedoDataAdd(-1);
			break;
		case R.id.iv_rightday:// 右翻页
			displayPedoDataAdd(1);
			break;
		case R.id.iv_reset:// 回到当日
			mDisplayDate = new Date(System.currentTimeMillis());
			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
			if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
					||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
					||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
					&&bluetoothPedo!=null)
			{
				displayPedoData(bluetoothPedo);
			}
			else
			{
				displayPedoData(currentPedo);
			}
			mDateDiff=0;
			showCurrentEquipment(mDeviceType);
			setLeftAndRightDisplayStatus();
			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				if(bluetoothState==2){
					setBloothDisconnectState(true, true);
				}else{
					setBloothDisconnectState(true, false);
				}
			}
			break;
		case R.id.ig_disconnect:
			if(checkBlueEnabled()) //未打开蓝牙，才需要打开蓝牙  
			{			
				setBloothDisconnectState(false, true);
//				mDeviceManagerService.connect(mDeviceId);
				mDeviceManager.initDevice(mDeviceType);
				mDeviceManager.connect(mDeviceId);
			}			
			break;
		case R.id.historySport_imageView:
			Intent historyIntent = new Intent(getBaseContext(), HistorySportActivity.class);
			startActivity(historyIntent);
			
			break;
		default:
			break;
		}

	}
	
	/**
	 * 运动数据翻页
	 * TODO
	 * @param diff 天数差
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:01:18
	 */
	private void displayPedoDataAdd(int diff)
	{
		if(DateFormatUtils.DateToString(mDisplayDate, FormatType.DateShot)
				.equals(DateFormatUtils.DateToString(new Date(), FormatType.DateShot))
				&&diff>0)//滑动到尽头不允许向后滑动
		{
			return;
		}
		mDateDiff+=diff;
		//前翻30天 提示用户
		if(mDateDiff<-30){
			mDateDiff-=diff;
			showHistoryAlert();
			return;
		}
		mDisplayDate = DateFormatUtils.AddDays(mDisplayDate, diff);
		if(DateFormatUtils.isToday(mDisplayDate))
		{
			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
			showCurrentEquipment(mDeviceType);
		}
		else
		{
			//显示最后一条记录不论设备
			currentPedo=PedoController.GetPedoController(this).getLatestPedometerOfAllByDay(mDisplayDate);
			if(currentPedo!=null)
			{
				showCurrentEquipment(Common.getDeviceType(currentPedo.deviceId, currentPedo.deviceType));
			}else{
				showCurrentEquipment(mDeviceType);
			}
		}

		if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
				&&DateFormatUtils.isToday(mDisplayDate)&&bluetoothPedo!=null)
		{
			displayPedoData(bluetoothPedo);
		}
		else
		{
			displayPedoData(currentPedo);
		}
		setLeftAndRightDisplayStatus();
		if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
				|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
				|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
			if(bluetoothState==2){
				setBloothDisconnectState(true, true);
			}else{
				setBloothDisconnectState(true, false);
			}
		}
	}
	
	/**
	 * 蓝牙手环断开标志
	 * @param isStop 是否停止动画
	 * @param isShow 是否显示标志
	 */
	private void setBloothDisconnectState(boolean isStop, boolean isShow) {

		if (isStop) {
			mIv_bloothDisconnect.setAnimation(null);
		} else {
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.progess_round);
			// 使用ImageView显示动画
			mIv_bloothDisconnect.startAnimation(hyperspaceJumpAnimation);
		}
		if (isShow && DateFormatUtils.isToday(mDisplayDate)) {
			mIv_bloothDisconnect.setVisibility(View.VISIBLE);
		} else {
			mIv_bloothDisconnect.setVisibility(View.GONE);
		}

	}

	/**
	 * TODO 返回键监听
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 * @return boolean
	 * @author zhangfengjuan
	 * @time 下午2:08:47
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backToMain();
			return false;
		}
		return false;
	}

	/**
	 * TODO 跳转进入MainFragmentActivity
	 * 
	 * @return void
	 * @author zhangfengjuan
	 * @time 下午2:11:50
	 */
	private void backToMain() {
		if (isInLauncher()) {
			finish();
		} else {
			Intent intent = new Intent(this, MainFragmentActivity2.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
		}
	}

	/**
	 * TODO 栈中是否包含MainFragmentActivity
	 * 
	 * @return
	 * @return boolean
	 * @author zhangfengjuan
	 * @time 下午2:08:14
	 */
	private boolean isInLauncher() {
		if (BaseActivity.allActivity.contains(MainFragmentActivity2.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 更多菜单点击事件
	 */
	private OnItemClickListener menuItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:// 分享
				Intent preShareIntent = new Intent(PedometerActivityTest.this,SharePagerActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putParcelable("currentPedo", currentPedo);
				preShareIntent.putExtras(mBundle);
				preShareIntent.putExtra("mDisplayDate", DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline));
				startActivity(preShareIntent);
				break;
			case 1:// 设备切换
				//存一下实时蓝牙数据便于上传
				if(bluetoothPedo!=null)
				{
					PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(bluetoothPedo, false);
				}
				Intent tempit = new Intent(cmccsi.mhealth.app.sports.pedo.PedometerActivityTest.this,
						cmccsi.mhealth.app.sports.ecg.activity.DeviceSettingActivityTest.class);
				tempit.putExtra("sampletitle", getString(R.string.textview_binddevice));
				tempit.putExtra(SharedPreferredKey.PASSWORD, PreferencesUtils.getPhonePwd(getBaseContext()));
				tempit.putExtra(SharedPreferredKey.PHONENUM, PreferencesUtils.getPhoneNum(getBaseContext()));
				startActivity(tempit);
				break;
			default:
				break;
			}
			mPopmenu_more.dismiss();
		}
	};
	
	class PedoGestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {

			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			DisplayMetrics dm = new DisplayMetrics();
//	        WindowManager windowManager = (WindowManager) PedometerActivity.this.getSystemService(Context.WINDOW_SERVICE);
//	        windowManager.getDefaultDisplay().getMetrics(dm);
			float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
//            //限制必须得划过屏幕的1/4才能算划过
//            float x_limit = dm.widthPixels / 7;
//            float y_limit = dm.heightPixels / 7;
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            if(x_abs >= y_abs){
            	// 左滑动
    			if ((e1.getX() - e2.getX()) > 20 && Math.abs(velocityX) > 0) {
    				displayPedoDataAdd(1);
    			}// 右滑动
    			else if ((e2.getX() - e1.getX()) > 20 && Math.abs(velocityX) > 0) {
    				displayPedoDataAdd(-1);
    			}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
	}
	
	///*****************以下为手环代码*************************************
	////TODO Auto-generated catch block

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	
	private class LoadAchievementTask extends AsyncTask<String, Integer, GoalInfo>{

        @Override
        protected GoalInfo doInBackground(String... params) {
            GoalNetInfo goalNetInfo = new GoalNetInfo();
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            int res = DataSyn.getInstance().getGoalData("2014-10-01",dateFormatter.format(date), goalNetInfo);
            GoalInfo goalInfo = null;
            if (res == 0) {
                goalInfo = GoalInfo.getInstanse(goalNetInfo);
                restoreGoalInfo2SP(goalNetInfo);
            } else {
                goalInfo = GoalInfo.getInstance(getBaseContext());
            }
            return goalInfo;
        }

        /**
         * 把goal数据存储到SharedPreferences中
         * 
         * @param goalNetInfo
         */
        private void restoreGoalInfo2SP(GoalNetInfo goalNetInfo) {
            PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.GOAL_TYPE, goalNetInfo.goalinfo.goal);
            PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.LATEST_RATE, goalNetInfo.goalinfo.rate);
            PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.CURRENT_DISTANCE, goalNetInfo.goalinfo.distance);
        }
        
        @Override
        protected void onPostExecute(GoalInfo result) {
            super.onPostExecute(result);
            iv_showGoal.setImageResource(result.type.getImgRes());
            tv_showGoal.setText(result.getGoalReportInTime(getBaseContext()));
        }
        
    }
	
	/**
	 * 是否可用的版本 （4.3）
	 * 
	 * @return 4.3及以上为真
	 */
	private boolean isVersionUseable() {
		if (Build.VERSION.SDK_INT < 18) {
			Toast.makeText(getBaseContext(), "蓝牙智能手环要求Android4.3以上版本，您的手机版本过低！", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_OPEN_BT_CODE){
			switch (resultCode) {
			case RESULT_OK:
				if(isVersionUseable()){
					//连接当前设备
//					mDeviceManagerService.connect(mDeviceId);
					mDeviceManager.initDevice(mDeviceType);
					mDeviceManager.connect(mDeviceId);
				}
				break;
			case RESULT_CANCELED:
				setBloothDisconnectState(true,true);
				break;
			default:
				break;
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
			startActivityForResult(enableBtIntent, REQUEST_OPEN_BT_CODE);
			return false;
		}
		return true;
	}

	/**
	 * 显示查看历史运动数据提示
	 */
	private void showHistoryAlert(){
		String[] buttons = { "确定", "", "取消" };
		CommonAskDialog mAskDialog = CommonAskDialog.create("查看更多运动数据，请到历史数据统计页面。", buttons, false, true);
		mAskDialog.setAlertIconVisible(-1);
		mAskDialog.setOnDialogCloseListener(new CommonAskDialog.OnDialogCloseListener() {
			@Override
			public void onClick(int which) {
				if (which == CommonAskDialog.BUTTON_OK) {
					Intent historyIntent = new Intent(getBaseContext(), HistorySportActivity.class);
					startActivity(historyIntent);
				}
			}
		});
		mAskDialog.show(getSupportFragmentManager(), "CommonAskDialog");
	}
}
