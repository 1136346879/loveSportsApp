package cmccsi.mhealth.app.sports.bean;

import android.util.Log;

public class DataDetailPedo {
	public static String TAG = "DataDetailPedo";
	/**
	 * 记录开始时间
	 */
	public String start_time = "11"; // 01
	/**
	 * 每五分钟的步数
	 */
	public String snp5="";
	/**
	 * 每五分钟的卡路里消耗
	 */
	public String knp5="";
	/**
	 * 每五分钟的运动强度2时间（秒）
	 */
	public String level2p5="";
	/**
	 * 每五分钟的运动强度3时间（秒）
	 */
	public String level3p5="";
	/**
	 * 每五分钟的运动强度4时间（秒）
	 */
	public String level4p5="";
	/**
	 * 每五分钟的加速度（秒）
	 */
	public String yuanp5="";

	public String snyxp5="";

	public int getStepNumSum() {
		int sum = 0;
		String[] stepNums = snp5.split(",");
		for (int i = 0; i < stepNums.length; i++) {
			sum += Integer.parseInt(stepNums[i]);
		}
		return sum;
	}

	public int getSNYXP5Sum() {
		int sum = 0;
		if (snyxp5 == null || snyxp5.equals(""))
			return 0;
		String[] snyxp5s = snyxp5.split(",");
		for (int i = 0; i < snyxp5s.length; i++) {
			sum += Integer.parseInt(snyxp5s[i]);
		}
		return sum;
	}

	/**
	 * 获取一小时运动强度时间和 秒
	 * 
	 * @param level
	 *            运动强度 从2开始
	 * @return
	 */
	public int getStrengthSum(int level) {
		String levelp;
		switch (level) {
		case 2:
			levelp = level2p5;
			break;
		case 3:
			levelp = level3p5;
			break;
		case 4:
			levelp = level4p5;
			break;
		default:
			levelp = level2p5;
			break;
		}
		int sum = 0;
		String[] levels = levelp.split(",");
		for (int i = 0; i < levels.length; i++) {
			sum += Integer.parseInt(levels[i]);
		}
		return sum;
	}

	/**
	 * 比较两个数据的开始时间
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compareStartTime(DataDetailPedo compare) {
		int this_hour, compare_hour;
		try {
			this_hour = Integer.parseInt(this.start_time);
			compare_hour = Integer.parseInt(compare.start_time);
		} catch (Exception e) {
			Log.e(TAG, "parse error");
			return -1;
		}

		if (this_hour > compare_hour)
			return 1;
		else if (this_hour < compare_hour)
			return 0;
		else
			return -1;
	}
}
