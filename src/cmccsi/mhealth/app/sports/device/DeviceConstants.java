package cmccsi.mhealth.app.sports.device;

/**
 * 设备相关的一些常量
 * 
 * @type DeviceConstants
 * TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:48:08
 */
public class DeviceConstants {
	
	/************* 设备类型分类****************/
	public static final int DEVICE_MOBILE_STEP = 0; //手机计步器
	public static final int DEVICE_PEDOMETER = 10; //计步器
	public static final int DEVICE_BRACLETE_BEATBAND = 20; //神念手环设备
	public static final int DEVICE_BRACLETE_JW = 21; //叮当手环设备
	public static final int DEVICE_BRACLETE_JW201 = 22; //叮当手环设备

	
	/************* 设备回调状态****************/
	/**
	 *  连接成功
	 */
	public static final int CONNECTED_SUCCESS = 2001;

	/**
	 *  连接失败
	 */
	public static final int CONNECTED_FAIL = 2002;
	
	/**
	 *  需要reset设备
	 */
	public static final int DEVICE_RESET = 2003;
	
	/**
	 *  连接设备超时
	 */
	public static final int CONNECTED_TIMEOUT = 2004;
	
	/**
	 *  异常：设备异常抛出
	 */
	public static final int EXCEPTION_CONNECT = 2010;
	
	/**
	 *  数据上传成功
	 */
	public static final int UPLOAD_SUCCESS = 2021;
	
	/**
	 *  数据上传失败
	 */
	public static final int UPLOAD_FAIL = 2022;
	/**
	 *  数据获取进度
	 */
	public static final int TRANSPORT_PERCENT = 2023;
	
	/**
	 *  实时运动数据获取
	 */
	public static final int REALTIME_PEDO = 2024;
	
	/**
	 *  普通提示
	 */
	public static final int NOMAL_MESSAGE = 2025;
	
	/**
	 *  普通提示
	 */
	public static final int DEVICE_POWER = 2026;
	
	/**
	 *  设备切换成功
	 */
	public static final int DEVCIE_SWITCH_SUCCESS = 2031;
	/**
	 *  设备切换失败
	 */
	public static final int DEVCIE_SWITCH_FAIL = 2032;
	
	/**
	 *  心境状态信息
	 */
	public static final int ECG_DEVICE_STATUS = 2040;
	/**
	 *  心境数据信息
	 */
	public static final int ECG_DEVICE_DATARECEIVED = 2041;
	/**
	 *  停止心境测量
	 */
	public static final int ECG_DEVICE_STOP = 2042;
	/**
	 *  停止心境测量
	 */
	public static final int MSG_FW_TRANSFER_PERCENT = 2051;
	/**
	 *  停止心境测量
	 */
	public static final int MSG_FW_TRANSFER_REPORT = 2052;

}
