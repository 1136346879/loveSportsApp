package cmccsi.mhealth.app.sports.ecg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import cmccsi.mhealth.app.sports.bean.DataECG;

public class ECGDataFilter {
	
	private static final String DAY_START_SUFFIX = " 00:00:00";
	private static final String HOUR_START_SUFFIX = ":00:00";
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.getDefault());
	private static final SimpleDateFormat SDF_HOUR = new SimpleDateFormat(
			"yyyy-MM-dd HH", Locale.getDefault());
	private static Pattern PATTERN_DATETIME = Pattern
			.compile("^\\d{4}(-\\d{2}){2}\\s\\d{2}:\\d{2}:\\d{2}$");
	
	/**
	 * 根据查询类型 对数据进行过滤 先决条件：ecgDataList中的数据按date属性进行升序排列；
	 * ecgDataList中数据的date属性形式是yyyy-MM-dd HH:mm:ss
	 * 
	 * @param rangeType
	 *            查询类型
	 * @param ecgDataList
	 *            要进行过滤的数据
	 * @return
	 */
	public List<DataECG> filter(int rangeType, List<DataECG> ecgDataList) {
		if(ecgDataList==null ||ecgDataList.size()==0 ) {
			return null;
		}
		List<DataECG> rst = null;
		switch (rangeType) {
		case RangeUtil.RANGE_TYPE_TODAY:
			rst = filterToday(ecgDataList);
//			rst = ecgDataList;
			break;
		case RangeUtil.RANGE_TYPE_WEEK:
			rst = filterWeek(ecgDataList);
//			rst = ecgDataList;
			break;
		case RangeUtil.RANGE_TYPE_MONTH:
			rst = filterMonth(ecgDataList);
//			rst = ecgDataList;
			break;
		case RangeUtil.RANGE_TYPE_SEMIANNUAL:
			rst = filterSemiannual(ecgDataList);
//			rst = ecgDataList;
			break;
		}
		return rst;
	}

	/**
	 * 过滤半年的数据。规则：取每周最后一次数据
	 * 
	 * @param ecgDataList
	 * @return
	 */
	private List<DataECG> filterSemiannual(List<DataECG> ecgDataList) {
		String checkDate = null;
		DataECG temp = null;
		Calendar cal = Calendar.getInstance();
		List<DataECG> rst = new ArrayList<DataECG>();
		for (DataECG data : ecgDataList) {
			if (data.data.date == null) {
				continue;
			}
			if (!PATTERN_DATETIME.matcher(data.data.date).matches()) {
				throw new IllegalArgumentException("错误的日期格式");
			}
			if (checkDate == null) {
				try {
					cal.setTime(SDF_DATE.parse(data.data.date));
					int dow = cal.get(Calendar.DAY_OF_WEEK);
					cal.add(Calendar.DAY_OF_WEEK, 8 - (dow+1));
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				checkDate = SDF_DATE.format(cal.getTime()) + DAY_START_SUFFIX;
				temp = data;
			}
			if (data.data.date.compareTo(checkDate) >= 0) {
				rst.add(temp);
				try {
					Date dt =	SDF_DATE.parse(data.data.date);
					cal.setTime(dt);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int dow = cal.get(Calendar.DAY_OF_WEEK);
				if (0==(dow-1)) {
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}else{
					cal.add(Calendar.DAY_OF_YEAR, 8 - (dow-1));
				}
				checkDate = SDF_DATE.format(cal.getTime())+DAY_START_SUFFIX;
			}
			temp = data;
		}
		rst.add(ecgDataList.get(ecgDataList.size() - 1));
		return rst;
	}

	/**
	 * 过滤一月数据。规则：取每天最后一次数据
	 * 
	 * @param ecgDataList
	 * @return
	 */
	private List<DataECG> filterMonth(List<DataECG> ecgDataList) {
		return filterLastDataPerDay(ecgDataList);
	}

	/**
	 * 过滤一周数据。规则：取每天最后一次数据
	 * 
	 * @param ecgDataList
	 * @return
	 */
	private List<DataECG> filterWeek(List<DataECG> ecgDataList) {
		return filterLastDataPerDay(ecgDataList);
	}

	/**
	 * 过滤列表，保留每天最后一次记录
	 * 
	 * @param ecgDataList
	 * @return
	 */
	private List<DataECG> filterLastDataPerDay(List<DataECG> ecgDataList) {
		String checkDate = null;
		DataECG temp = null;
		Calendar cal = Calendar.getInstance();
		List<DataECG> rst = new ArrayList<DataECG>();
		for (DataECG data : ecgDataList) {
			if (data.data.date == null) {
				continue;
			}
			if (!PATTERN_DATETIME.matcher(data.data.date).matches()) {
				throw new IllegalArgumentException("错误的日期格式");
			}
			if (checkDate == null) {
				try {
					cal.setTime(SDF_DATE.parse(data.data.date));
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				cal.add(Calendar.DAY_OF_YEAR, 1);
				checkDate = SDF_DATE.format(cal.getTime()) + DAY_START_SUFFIX;
				temp = data;
			}
			if (data.data.date.compareTo(checkDate) >= 0) {
				rst.add(temp);
				checkDate = plusOneDay(data.data.date,cal);
			}
			temp = data;
		}
		rst.add(ecgDataList.get(ecgDataList.size() - 1));
		return rst;
	}
	
	/**
	 * 过滤一天数据。规则：一个小时测量多次取最后一次
	 * 
	 * @param ecgDataList
	 * @return
	 */
	private List<DataECG> filterToday(List<DataECG> ecgDataList) {
		String checkDate = null;
		DataECG temp = null;
		Calendar cal = Calendar.getInstance();
		List<DataECG> rst = new ArrayList<DataECG>();
		for (DataECG data : ecgDataList) {
			if (data.data.date == null) {
				continue;
			}
			if (!PATTERN_DATETIME.matcher(data.data.date).matches()) {
				throw new IllegalArgumentException("错误的日期格式");
			}
			if (checkDate == null) {
				try {
					cal.setTime(SDF_HOUR.parse(data.data.date));
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				cal.add(Calendar.HOUR_OF_DAY, 1);
				checkDate = SDF_HOUR.format(cal.getTime()) + HOUR_START_SUFFIX;
				temp = data;
			}
			if (data.data.date.compareTo(checkDate) >= 0) {
				rst.add(temp);
				cal.add(Calendar.HOUR_OF_DAY, 1);
				checkDate = SDF_HOUR.format(cal.getTime()) + HOUR_START_SUFFIX;
			}
			temp = data;
		}
		rst.add(ecgDataList.get(ecgDataList.size() - 1));
		return rst;
	}
	
	private String plusOneDay(String date, Calendar cal) {
		try {
			Date date1 = SDF_DATE.parse(date);
			cal.setTime(date1);
		} catch (ParseException e) {
		}
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return SDF_DATE.format(cal.getTime()) + DAY_START_SUFFIX;
	}

}
