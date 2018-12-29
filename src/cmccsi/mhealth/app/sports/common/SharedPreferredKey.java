package cmccsi.mhealth.app.sports.common;

/**
 * key
 * @author qjj
 * 
 *@注意：常量使用大写方式
 */
public class SharedPreferredKey {
	public static final String SHARED_NAME = "config";
	public static final String SHARED_PHONENAME = "PHONESTEP";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	
	
	//个人信息===========================================
	public static final String PASSWORD = "PASSWORD";
	public static final String PHONENUM = "PHONENUM";
	public static final String LOGIN_NAME = "LOGIN_NAME";
	
	public static final String USERUID = "userUid";
	public static final String USERID = "userId";
	public static final String NAME = "name";
	public static final String NICK_NAME = "nickname";//昵称
	public static final String WEIGHT = "weight";
	public static final String HEIGHT = "height";
	public static final String GENDER = "gender";//性别 1男 
	public static final String BIRTHDAY = "birthday";
	public static final String SCORE = "score";
	public static final String AVATAR = "avarta";
	public static final String TARGET_WEIGHT = "targetweight";
	public static final String TARGET_STEP = "targetstep";
	public static final String SLOGAN = "slogan";//心情短语
	public static final String GROUP_NAME = "groupname";
	public static final String CORPORATION = "corporation";
	public static final String CLUB_ID = "clubid";
	public static final String ORG_ID = "orgid";
	public static final String COUNTY_ID = "countyId";
	public static final String CITY_ID = "cityid";
	public static final String PROVINCE_ID = "provinceid";
	public static final String COUNTY_NAME = "countyname";
	public static final String CITY_NAME = "cityname";
	public static final String PROVINCE_NAME = "provincename";
	
	public static final String GROUP_NAME_1 = "groupname";
	public static final String CORPORATION_1 = "corporation";
	public static final String CLUB_ID_1 = "clubid";
	public static final String GOAL_TYPE="goalType";
    public static final String LATEST_RATE = "latestRate";
    public static final String CURRENT_DISTANCE = "currentDistance";
	//==================================================
	public static final String FRIEND_GETTIME = "friendgettime";
	//==================================================
	public static final String SELECTED_SERVER = "selectedserver";
	public static final String SERVER_VERSION = "serverversion";
	
	public static final String SERVER_NAME = "server_name";//服务器名称
	public static final String TIMER = "timer";//计时器
	
	public static final String START_TIME = "start_time";//计时器
	public static final String START_TIME1 = "start_time1";//计时器
	public static final String START_CAL1 = "start_cal1";//计时器
	public static final String START_STOP = "start_stop";//map 暂停和开始
//	public static final String HISTORY_START_TIME = "history_start_time";//计时器
	public static final String APPVERNAME = "versionName";
	public static final String STEPLENGTH = "STEPLENGTH";
	public static final String PEDO_UPDATE_TIME = "pedo_update_time";
	public static final String POSITION = "position";//手环佩戴位置
	public static final String SPORT_HOUR = "sporthour";//运动闹钟时
	public static final String SPORT_MINUTE = "sportminute";//运动闹钟分
	public static final String SLEEP_HOUR = "sleephour";//睡眠闹钟时
	public static final String SLEEP_MINUTE = "sleepminute";//睡眠闹钟分
	public static final String DAYS = "days";//睡眠闹钟重复
	public static final String DAYS2 = "days2";//运动闹钟重复
	public static final String DEVICE_ADDRESS = "deviceaddress";//设备地址
	public static final String DEVICE_TYPE = "devicetype";//设备类型 0手机 1计步器 2手环
	public static final String DEVICE_ID = "deviceid";//设备ID
	public static final String DEVICE_NAME = "devicename";//设备名称
	public static final String DEVICE_MODEL = "devicemodel";//设备型号
	public static final String HAVE_BRACELET_DEVICE="havebracelet";//是否含有手环设备
	public static final String ENABLE_SPORT = "enable_sport";//运动闹钟开关
	public static final String ENABLE_SLEEP = "enable_sleep";//睡眠闹钟开关
	public static final String DAYS_SLEEP = "days1";//睡眠闹钟重复
	public static final String IS_FIRSTPEDO = "isfirstpedo";//是否第一次进入运动界面
	public static final String STEP_SWITCH_STATUS = "stepswitchstatus";//运动开关状态
	public static final String SLEEP_UPDATE_SIGN = "sleepUpdateSign";//运动开关状态
	public static final String DEVICE_TOKEN = "deviceToken";//设备token
	public static final String DEVICE_NUMBER = "deviceNumber";//设备SN
	public static final String DEVICE_VERSION = "deviceVersion";//设备版本号
	
	public static final String SETTING_BOOT_RUN = "setting_boot_run";//设备版本号

	public static final String IS_FIRST_INSTALL = "isfirstinstall";//设备版本号
	public static final String IS_STEPOPEN = "isStepOpen";//是否开启手机计步
	/*计步器中的数据*/
	public static final String PHONESTEP_STEP = "step";
	public static final String PHONESTEP_CALORY = "calory";
	public static final String PHONESTEP_DISTANCE = "distance";
	public static final String PHONESTEP_TIME = "time";
	public static final String PHONESTEP_EXERCISE_INTENSITY_NORMALLY = "exercise_intensity_normally";
	public static final String PHONESTEP_EXERCISE_INTENSITY_FAIRLY = "exercise_intensity_fairly";
	public static final String PHONESTEP_EXERCISE_INTENSITY_VERY = "exercise_intensity_very";
}
