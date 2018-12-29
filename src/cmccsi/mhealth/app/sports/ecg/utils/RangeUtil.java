package cmccsi.mhealth.app.sports.ecg.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RangeUtil {
	// 当天
	public static final int RANGE_TYPE_TODAY = 1;
	// 一周
	public static final int RANGE_TYPE_WEEK = 2;
	// 一个月
	public static final int RANGE_TYPE_MONTH = 3;
	// 半年
	public static final int RANGE_TYPE_SEMIANNUAL = 4;

	private static final String DAY_START_SUFFIX = " 00:00:00";
	private static final String DAY_END_SUFFIX = " 23:59:59";
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.getDefault());

	/**
	 * 根据查询的方式 计算对应的时间段
	 * 
	 * @param rangeType
	 *            查询方式
	 * @return 时间段
	 */
	public static Range getRange(int rangeType) {
		if (rangeType < 1 || rangeType > 4) {
			throw new IllegalArgumentException("Illegal Range");
		}
		Range rst = new Range();
		Calendar cal = Calendar.getInstance();
		rst.setEndTime(SDF_DATE.format(cal.getTime()) + DAY_START_SUFFIX);
		switch (rangeType) {
		case RANGE_TYPE_TODAY:
			rst.setEndTime(SDF_DATE.format(cal.getTime()) + DAY_END_SUFFIX);
			break;
		case RANGE_TYPE_WEEK:
			cal.add(Calendar.WEEK_OF_YEAR, -1);
			break;
		case RANGE_TYPE_MONTH:
			cal.add(Calendar.MONTH, -1);
			break;
		case RANGE_TYPE_SEMIANNUAL:
			cal.add(Calendar.MONTH, -6);
			break;
		}
		rst.setStartTime(SDF_DATE.format(cal.getTime())+DAY_START_SUFFIX);
		return rst;
	}
}
