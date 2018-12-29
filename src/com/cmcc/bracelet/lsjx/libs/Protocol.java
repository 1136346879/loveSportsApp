package com.cmcc.bracelet.lsjx.libs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.util.Log;
import cmccsi.mhealth.app.sports.bean.DataDetailPedo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.utils.LogUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;

public class Protocol {
	private static String TAG = "lsjx_Protocol";
	public static final byte PRO_HEAD_StepOfReaTimeData = (byte) 0x81; // 计步实时数据
	public static final byte PRO_HEAD_ShortSleep = (byte) 0x82; // 短睡
	public static final byte PRO_HEAD_AntiLost = (byte) 0x83; // 防丢
	public static final byte PRO_HEAD_TimeTheAlarmClock = (byte) 0x84; // 时间、闹钟
	public static final byte PRO_HEAD_CallsToRemind = (byte) 0x85; // 来电提醒
	public static final byte PRO_HEAD_HisDataSyncStepAndSleep = (byte) 0x90; // 历史数据同步计步、睡眠
	public static final byte PRO_HEAD_PersonalInfoSysEnvSetting = (byte) 0xB0; // 个人信系统环境设置
	public static final byte PRO_HEAD_BlueConSetting = (byte) 0xE0; // 蓝牙连接相关设置
	public static final byte PRO_HEAD_DevInfo = (byte) 0xF0; // 设备信息
	public static final byte PRO_HEAD_TheBattery = (byte) 0xF1; // 电池电量
	public static final byte PRO_HEAD_FirmwareUpgrade = (byte) 0xF2; // 固件升级
	public static final byte PRO_HEAD_LogInformation = (byte) 0xF3; // 日志信息
	public static final byte PRO_HEAD_TestItem = (byte) 0xF4; // 测试条目
	public static final byte PRO_HEAD_SynchronousMeterStep_SynchronousSleep = (byte) 0x90; // 历史数据同步----计步、睡眠

	public static final byte PRO_ID0 = (byte) 0x80; // 空包
	public static final byte PRO_ID1 = (byte) 0x81; // ble历史数据的起始包 &
													// ble设备上传计步器实时值 & 电池电量
	public static final byte PRO_ID2 = (byte) 0x82; // ble历史数据的数据包 &
													// ble设备上传计步目标值
	public static final byte PRO_ID3 = (byte) 0x83; // ble历史数据的结束包 &
													// ble设上传运动模式实时值(1Byte 有符号数)
	public static final byte PRO_ID4 = (byte) 0x84;// ble固件版本值

	// static int last_sleep = 0;// 睡眠
	// static int last_exercise_time = 0; // 运动时间
	// /**
	// * 获取历史数据——获取一定时间段的包的包数
	// */
	// private static int sumDataPacket = 0; // 这段时间内的数据包数量

	/**
	 * 实时的当天总步数
	 */
	public static int REAL_TIME_STEP_NUMBER = 0;
	/**
	 * 实时的当天消耗的卡路里
	 */
	public static int REAL_TIME_CALORIE = 0;
	/**
	 * 实时的当天运动范围
	 */
	public static int REAL_TIME_RANGE = 0;
	/**
	 * 实时的当天运动总时间
	 */
	public static int REAL_TIME_EXERCISE_TIME = 0;
	/**
	 * 设备的电量
	 */
	static public int BATTLY_QUANTITY = 0;
	/**
	 * 设备的固件版本
	 */
	static public String DEVICE_FIRMWARE_VERSION = "";
	/**
	 * 设备的型号
	 */
	static public String DEVICE_MODEL = "";
	/**
	 * 电池状态 0未充电 1充电
	 */
	static public int BATTLY_STATE;
	/**
	 * 自定义的标记，是否已经获取完历史数据
	 */
	private static boolean returnHistory = false;
	/**
	 * 运动简包 实时显示的或者今天的运动简包
	 */
	private static PedometorDataInfo nowInfo = new PedometorDataInfo();
	/**
	 * 同步历史数据——运动简包集合
	 */
	private static PedometorListInfo pedometorListInfoList = new PedometorListInfo();
	/**
	 * 同步数据的运动详包集合
	 */
	private static ArrayList<PedoDetailInfo> pedoDetailInfoList = new ArrayList<PedoDetailInfo>();
	/**
	 * 获取历史数据——获取一定时间段的包的包数
	 */
	private static int historyBagSum = 0;
	/**
	 * 获取历史数据——获取的当前包是总包数的第几个
	 */
	private static int startTimeStampInt = 0; // 当前数据包的序号
	/**
	 * 获取历史数据——获取到的数据包的开始时间
	 */
	private static int endTimeStampInt = 0;

	/**
	 * 获取历史数据——获取到的数据包的结束时间
	 */
	private static int historyBagNumber = 0;
	/**
	 * 获取到的历史数据
	 */
	private static List<LSJXMsg> metorListResout = new ArrayList<LSJXMsg>();

	/**
	 * 自定义的中间变量 是否出现异常情况
	 */
	// private static boolean isErrorToken = false;
	/**
	 * 获取手环数据的百分比
	 */
	private static int percent = 0;

	// 解析从ble设备中返回的数据包
	public static void dispatchPack(int head, byte[] data, JWDeviceManager jwDeviceManager) {
		int id = data[0];
		int[] idata = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			idata[i] = data[i] & 0xff;
		}
		returnHistory = false;
		// 获取当前时间
		Calendar mCalendar = Calendar.getInstance();
		int minute = mCalendar.get(Calendar.MINUTE);
		int timeTemp = minute % 5;
		// DbUtils dbUtils = DbUtils.create(JWDeviceManager.mContext);
		switch (head) {
		case PRO_HEAD_DevInfo: {
			if (id == PRO_ID1) {
				String firewareVer = (char) (idata[1]) + "" + (char) (idata[2]) + "" + (char) (idata[3]);
				String hardwareVer = (char) (idata[4]) + "" + (char) (idata[5]) + "" + (char) (idata[6]);
				DEVICE_FIRMWARE_VERSION = hardwareVer;
				Log.e("---固件版本---", "固件版本  " + firewareVer);
				Log.e("---硬件版本---", "硬件版本  " + hardwareVer);
			}

			if (id == PRO_ID4) {
//				DEVICE_FIRMWARE_VERSION = Integer.toString(idata[1] & 0xFF) + "." + Integer.toString(idata[2] & 0xFF);
				DEVICE_MODEL = Integer.toString(idata[3] & 0xFF);
//				Log.e("固件版本为", DEVICE_FIRMWARE_VERSION);
				Log.e("---手环型号---", DEVICE_MODEL);
			}
		}
			break;
		// -------------------------------
		case PRO_HEAD_SynchronousMeterStep_SynchronousSleep: {
			Log.e("PRO_HEAD_SynchronousMeterStep_SynchronousSleep——————data.length", data.length + "");
			switch (id) {
			case PRO_ID0: {
				Log.e(TAG, "空包----没有历史数据");
				jwDeviceManager.getBaseCallBack().pedoDataPercent(100);
				returnHistory = true;
			}
				break;
			case PRO_ID1: {
				if (data.length == 12) {
					startTimeStampInt = (data[1] & 0xff) + ((data[2] & 0xff) << 8) + ((data[3] & 0xff) << 16)
							+ ((data[4] & 0xff) << 24);
					endTimeStampInt = (data[5] & 0xff) + ((data[6] & 0xff) << 8) + ((data[7] & 0xff) << 16)
							+ ((data[8] & 0xff) << 24);
					historyBagSum = (idata[10] << 8) | idata[9]; // 这段时间内的数据包数量
					Log.e(TAG, timeStamp2Date(startTimeStampInt + ""));
					Log.e(TAG, timeStamp2Date(endTimeStampInt + ""));
					Log.e(TAG, historyBagSum + "");
				}
			}
				break;
			case PRO_ID2:
				if (data.length == 14) { // 一共14byte，前面2byte是id
					// 得到每2byte间隔的数据
					historyBagNumber = (idata[2] << 8) | idata[1];
					int step_number = (idata[4] << 8) | idata[3];
					int calorie = (idata[6] << 8) | idata[5];
					int range = (idata[8] << 8) | idata[7];
					int sleep = (idata[10] << 8) | idata[9];
					int exercise_time = (idata[12] << 8) | idata[11];
					String createTime = timeStamp2Date((startTimeStampInt + (historyBagNumber - 1) * 5 * 60) + "");
					LSJXMsg msg = new LSJXMsg();
					msg.setDate(Integer.parseInt(createTime.substring(0, 8)));
					msg.setHour(Integer.parseInt(createTime.substring(8, 10)));
					msg.setMinute(Integer.parseInt(createTime.substring(10, 12)));
					msg.setCarlories(calorie);
					msg.setStepNum(step_number);
					msg.setRange(range);
					msg.setExercise_time(exercise_time);
					Log.e(TAG, "current_data_packetNumber " + historyBagNumber);
					Log.e(TAG, "step_number " + step_number);
					Log.e(TAG, "calorie " + calorie);
					Log.e(TAG, "range " + range);
					Log.e(TAG, "sleep " + sleep);
					Log.e(TAG, "exercise_time" + exercise_time);
					Log.e(TAG, "createTime" + createTime);
					// 同步数据 进度
					metorListResout.add(msg);
					percent = (historyBagNumber * 100) / historyBagSum;
					jwDeviceManager.getBaseCallBack().pedoDataPercent(percent);
				}
				break;
			case PRO_ID3:
				if (data.length == 4) {
					Log.e(TAG, "ok");
					returnHistory = true;
				}
				break;
			default:
				break;
			}// end of switch (id) {
		} // end of case PRO_HEAD_SynchronousMeterStep_SynchronousSleep: {
			break;

		// -----------------------------------
		case PRO_HEAD_StepOfReaTimeData: {
			Log.e("计步数据", "");
			switch (id) {
			case PRO_ID1:
				// ble设备上传计步器实时值（8Bytes 无符号数）
				if (data.length == 10) {
					// 得到每2byte间隔的数据
					REAL_TIME_STEP_NUMBER = (idata[2] << 8) | idata[1];
					REAL_TIME_CALORIE = (idata[4] << 8) | idata[3];
					REAL_TIME_RANGE = (idata[6] << 8) | idata[5];
					REAL_TIME_EXERCISE_TIME = (idata[8] << 8) | idata[7];
					// TODO 实时的数据（今天的走的步数）
					nowInfo.stepNum = REAL_TIME_STEP_NUMBER + "";
					nowInfo.cal = REAL_TIME_CALORIE + "";
					nowInfo.distance = REAL_TIME_RANGE + "";
					nowInfo.strength2 = REAL_TIME_EXERCISE_TIME + "";
					Log.e(TAG, "step_number " + REAL_TIME_STEP_NUMBER);
					Log.e(TAG, "calorie" + REAL_TIME_CALORIE);
					Log.e(TAG, "range " + REAL_TIME_RANGE);
					Log.e(TAG, "exercise_time" + REAL_TIME_EXERCISE_TIME);
				}
				break;
			case PRO_ID2:
				// ble设备上传计步目标值
				break;
			case PRO_ID3:
				// ble设上传运动模式实时值(1Byte 有符号数)
				break;
			}// end switch(id)
		}// end case PRO_HEAD_StepOfReaTimeData: {
			break;
		case PRO_HEAD_TheBattery: {
			switch (id) {
			case PRO_ID1:
				// 获取手环电量 TODO 有错误
				Log.e("---battly_status---", "battly_status " + (idata[1] & 0xFF));
				Log.e("---battly_quantity---", "battly_quantity " + (idata[2] & 0xFF));
				BATTLY_STATE = (idata[1] & 0xFF);
				BATTLY_QUANTITY = (idata[2] & 0xFF);
				if (BATTLY_QUANTITY <= 20 && null != jwDeviceManager) {
					jwDeviceManager.getBaseCallBack().exception(DeviceConstants.CONNECTED_FAIL,
							"手环电量为" + BATTLY_QUANTITY + "%，电池电量低请充电！");
				}
				break;
			}
		}
			break;

		default:
			break;
		}

		// // 删除设备数据
		// if (!isErrorToken) {
		// jwDeviceManager.clearDeviceData();
		// }
		// 实时数据更新（今天的所有数据）
		if (JWDeviceManager.isStartRealTime) {
			jwDeviceManager.getBaseCallBack().realTimeDataReceived(nowInfo);
		}
		// 历史数据的更新
		if (returnHistory) {
			if (isTwoDaysSame(startTimeStampInt, endTimeStampInt)) {
				returnAppMsg(0, metorListResout.size());
			} else {
				int todayBagCount = getTodayBagCount();
				if (todayBagCount > historyBagSum) {
					returnAppMsg(0, metorListResout.size());
				} else {
					int days = (historyBagSum - todayBagCount) / 288;
					for (int i = 0; i < days; i++) {
						returnAppMsg(todayBagCount + 288 * i, todayBagCount + 288 * (i + 1));
					}
					returnAppMsg(todayBagCount + 288 * days, metorListResout.size());
				}
			}
			System.err.println(pedometorListInfoList.datavalue.size() + "+++++++++++++");
			System.err.println(pedoDetailInfoList.size() + "+++++++++++++");
			jwDeviceManager.getBaseCallBack().pedoDataReceived(pedometorListInfoList, pedoDetailInfoList);
			pedometorListInfoList.datavalue.clear();
			pedoDetailInfoList.clear();
			metorListResout.clear();
		}
		// TODO
	}

	/**
	 * 通过传入的包list，存储回掉时用到的详包和简包
	 */
	private static void returnAppMsg(int num1, int num2) {
		// TODO Auto-generated method stub
		if (null == metorListResout || 0 == metorListResout.size() || num1 > metorListResout.size()
				|| num2 > metorListResout.size() || num1 == num2) {
			return;
		}
		PedoDetailInfo detailList = new PedoDetailInfo();
		ArrayList<DataDetailPedo> dataDetailPedoDatavalue = new ArrayList<DataDetailPedo>();
		// 详包
		detailList.date = metorListResout.get(num1).getDate() + "";
		String[][] knpTest = new String[24][12];
		String[][] snpTest = new String[24][12];
		int cal = 0, stepNum = 0, distance = 0, strength2 = 0;
		for (int j = num1; j < num2; j++) {
			LSJXMsg itemLSLsjxMsg = metorListResout.get(j);
			knpTest[itemLSLsjxMsg.getHour()][itemLSLsjxMsg.getMinute() / 5] = itemLSLsjxMsg.getCarlories() + "";
			snpTest[itemLSLsjxMsg.getHour()][itemLSLsjxMsg.getMinute() / 5] = itemLSLsjxMsg.getStepNum() + "";
			cal += metorListResout.get(j).getCarlories();
			stepNum += metorListResout.get(j).getStepNum();
			distance += metorListResout.get(j).getRange();
			strength2 += metorListResout.get(j).getExercise_time();
		}
		for (int j = 0; j < 24; j++) {
			DataDetailPedo item = new DataDetailPedo();
			item.start_time = j + "";
			for (int k = 0; k < 12; k++) {
				if (null == knpTest[j][k] || "".equals(knpTest[j][k])) {
					if (k == 11) {
						item.knp5 += "0";
						item.snp5 += "0";
					} else {
						item.knp5 += "0,";
						item.snp5 += "0,";
					}
				} else {
					if (k == 11) {
						item.knp5 += knpTest[j][k];
						item.snp5 += snpTest[j][k];
					} else {
						item.knp5 += (knpTest[j][k] + ",");
						item.snp5 += (snpTest[j][k] + ",");
					}
				}
			}
			if (!"0,0,0,0,0,0,0,0,0,0,0,0".equals(item.knp5)) {
				dataDetailPedoDatavalue.add(item);
			}
		}
		detailList.datavalue = dataDetailPedoDatavalue;
		// 简包
		// PedometorListInfo metorList = new PedometorListInfo();
		// ArrayList<PedometorDataInfo> pedometorDataInfoDatavalue = new
		// ArrayList<PedometorDataInfo>();
		// 简包
		PedometorDataInfo itemtemp = new PedometorDataInfo();
		itemtemp.cal = cal + "";
		itemtemp.stepNum = stepNum + "";
		itemtemp.distance = distance + "";
		itemtemp.strength2 = strength2 + "";
		itemtemp.date = detailList.date.substring(0, 4) + "-" + detailList.date.substring(4, 6) + "-"
				+ detailList.date.substring(6, 8);
		itemtemp.createtime = itemtemp.date + " 01:59:00";
		// pedometorDataInfoDatavalue.add(itemtemp);
		// metorList.datavalue = pedometorDataInfoDatavalue;
		// 存储list简包详包
		pedometorListInfoList.datavalue.add(itemtemp);// 简包
		pedoDetailInfoList.add(detailList);// 详包

	}

	// 将字节写出函数
	public static byte[] writeByte(byte header, byte[] data) {
		byte[] encodeData = writeProtocolDataBytes(data);
		final byte[] byte_send = new byte[3 + encodeData.length];
		byte_send[0] = header;
		byte_send[1] = (byte) (encodeData.length + 1);
		for (int i = 2; i < encodeData.length + 2; i++) {
			byte_send[i] = encodeData[i - 2];
		}
		fillCheckSumByte(byte_send);
		boolean isValid = isCheckSumValid(byte_send);
		if (!isValid) {
			throw new RuntimeException("runtime_exception");
		}
		return byte_send;
	}

	// 数据发送移位函数
	public static byte[] writeProtocolDataBytes(byte[] bytes) {
		int n = 0;
		int i, j, leastBit = 0;
		int bit7 = 0;
		int count = (bytes.length * 8 + 7 - 1) / 7;
		byte[] d = new byte[count];

		for (i = 0; i < bytes.length; i++) {
			for (j = 0; j < 8; j++) {
				leastBit = (bytes[i] >> j) & 0x01;
				d[n] |= leastBit << (bit7++);
				if (7 == bit7) {
					bit7 = 0;
					n++;
				}
			}
		}
		return d;
	}

	// 将字节写出函数
	public static void readByte(byte[] data, JWDeviceManager jwDeviceManager) {
		System.out.println("-----readByte--data---" + data);
		if (false == isCheckSumValid(data)) {
			Log.e("isCheckSumValid result.", "isCheckSumValid false.");
		}
		// Protocol.printHexString(data);
		int data_length = data[1]; // 得到数据的长度
		// byte[] decodeData = new byte[data.length - 2]; // 减掉一个head
		byte[] decodeData = new byte[data_length]; // 减掉一个head
		for (int index = 0; index < data_length; index++) {
			decodeData[index] = data[index + 2];// 只需要去掉head
		}
		final byte[] recv_data = readProtocolDataBytes(decodeData);
		int head = data[0]; // head
		dispatchPack(head, recv_data, jwDeviceManager);
		// 这里如果还有数据包，那就是电量的
		if (data.length > data_length + 2) {
			int battry_head = data[data_length + 2];
			int battry_data_length = data[data_length + 3];
			byte[] battry_decodeData = new byte[battry_data_length]; // 减去包头和长度
			for (int index = 0; index < battry_data_length; index++) {
				battry_decodeData[index] = data[index + (data_length + 2) + 2];// 只需要去掉head
			}
			final byte[] recv_battry_data = readProtocolDataBytes(battry_decodeData);
			dispatchPack(battry_head, recv_battry_data, jwDeviceManager);
		}
	}

	// 数据接收提取函数
	public static byte[] readProtocolDataBytes(byte[] s) {
		int n = 0;
		int bit8 = 0;
		int i, j;
		int leastBit = 0;
		int length = s.length;
		int count = length * 7 / 8;
		byte[] d = new byte[length];
		for (i = 0; i < length; i++) {
			for (j = 0; j < 7; j++) {
				leastBit = (s[i] >> j) & 0x01;
				d[n] |= leastBit << (bit8++);
				if (8 == bit8) {
					bit8 = 0;
					n++;
				}
			}
		}
		byte[] rt = new byte[count];
		System.arraycopy(d, 0, rt, 0, count);
		return rt;
	}

	// 跳过其中几个字节的函数
	public static byte[] stripByte(byte[] buf) {
		byte[] data = new byte[buf.length - 3];
		for (int i = 0; i < buf.length - 3; i++) {
			data[i] = buf[i + 2];
		}
		return data;
	}

	// 填发送端校验填充函数
	public static void fillCheckSumByte(byte[] buf) {
		byte checksum;
		int i;
		checksum = 0;
		for (i = 0; i < buf.length - 1; i++)
			checksum = (byte) (checksum + buf[i]);
		checksum = (byte) (((~checksum) + 1) & 0x7F);
		buf[buf.length - 1] = checksum;
	}

	// 接收端的校验函数
	public static boolean isCheckSumValid(byte[] rcvBuff) {
		byte checksum;
		int i = 0;
		checksum = rcvBuff[0];
		for (i = 1; i < rcvBuff.length; i++) {
			if (rcvBuff[i] >= 0x80)
				return false;
			checksum = (byte) (checksum + rcvBuff[i]);
		}
		checksum &= 0x7F;
		if (0 == checksum)
			return true;
		return false;
	}

	// 将指定byte数组以16进制的形式打印到控制台
	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			// LogUtils.e("", (hex.toUpperCase() + ""));
		}
	}

	/**
	 * 时间前推或后推分钟,其中fzh表示分钟数，保证时间字符格式为yyyyMMddHHmm
	 * 
	 * @param sj1
	 *            时间字符格式为yyyyMMddHHmm
	 * @param sj1
	 *            fzh表示为前移或后延的分钟数
	 */
	public static String getPreTime(String sj1, String fzh) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(fzh) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}

	/**
	 * 获取现在时间字符串
	 * 
	 * @return 返回字符串格式 yyyyMMddHHmm
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 
	 * @param seconds
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String timeStamp2Date(String seconds) {
		if (null == seconds || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		String format = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds + "000")));

	}

	/**
	 * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	 * 
	 * @param st1
	 *            "HH:MM"的格式
	 * @param st2
	 *            "HH:MM"的格式
	 * @return 两个时间间的分钟
	 */
	private String getTwoHour(String st1, String st2) {
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		int y = Integer.parseInt(kk[0]) - Integer.parseInt(jj[0]);
		int u = Integer.parseInt(kk[1]) - Integer.parseInt(jj[1]);
		if (y > 0)
			return y * 60 + u + "";
		else if (y < 0)
			return -(y * 60 + u) + "";
		else {
			if (u >= 0) {
				return u + "";
			} else {
				return -u + "";
			}
		}
	}

	/**
	 * 得到二个日期间的间隔天数，保证时间字符格式为yyyy-MM-dd
	 * 
	 * @param sj1
	 *            时间字符格式为yyyy-MM-dd
	 * @param sj2
	 *            时间字符格式为yyyy-MM-dd
	 * @return 两个日期间的天数
	 */
	private String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		long day = 0;
		try {
			java.util.Date date = myFormatter.parse(sj1);
			java.util.Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/**
	 * 判断获取的数据的开始时间和结束时间是否是同一天
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private static boolean isTwoDaysSame(int startTime, int endTime) {
		String startTimeStr = timeStamp2Date(startTime + "");
		String endTimeStr = timeStamp2Date(endTime + "");
		if (startTimeStr.substring(0, 8).equals(endTimeStr.substring(0, 8))) {
			return true;
		}
		return false;
	}

	private static int getTodayBagCount() {
		String endTimeStr = timeStamp2Date(endTimeStampInt + "");
		System.out.println("----endTimeStr---------" + endTimeStr);
		int hour = Integer.parseInt(endTimeStr.substring(8, 10));
		int minute = Integer.parseInt(endTimeStr.substring(10, 12));
		return hour * 12 + minute / 5;
	}
	// private int getPackageCount(String sj1, String sj2){}
}