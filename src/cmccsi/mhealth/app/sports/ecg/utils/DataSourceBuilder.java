package cmccsi.mhealth.app.sports.ecg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xclcharts.chart.PointD;

import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.common.MathUtil;

public class DataSourceBuilder {
	private static final long DAY_MILLSECONDS = 1000 * 60 * 60 * 24;
	private static final long WEEK_MILLSECONDS = 1000 * 60 * 60 * 24 * 7;
	private static final SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private static final SimpleDateFormat SDF_DAY = new SimpleDateFormat(
			"MM/dd", Locale.getDefault());
	private List<DataECG> source;
	private int chartType;
	private int rangeType;
	private Range range;
	private List<String> map = new ArrayList<String>();
	
	public DataSourceBuilder(Range range,int chartType,int rangeType, List<DataECG> source) {
		this.source = source;
		this.chartType = chartType;
		this.rangeType = rangeType;
		this.range = range;
	}
	
	public ChartDataSource build() {
		List<PointD> data = new ArrayList<PointD>();
		double max = 0;

		for (int i = 0; i < source.size(); i++) {
			DataECG ecg = source.get(i);
			String valStr = chartType == 1 ? ecg.data.hr
					: (chartType == 2 ? ecg.data.hrv : ecg.data.mood);
			PointD p = createPoint(valStr,ecg.data.date);
			if(p!=null) {
				data.add(p);
				max = p.y > max ? p.y : max;
			}
		}
		int maxVal = MathUtil.ceil(max);
		ChartDataSource datasource = new ChartDataSource();
		datasource.setLables(getLabels());
		datasource.setMaxX(getLabelMax());
		datasource.setMaxY(maxVal);
		datasource.setPoints(data);
//		datasource.setDateMap(map);
		datasource.setDateList(map);
		return datasource;
	}
	
	private int getLabelMax() {
		int rst = 240;
		switch (rangeType) {
		case RangeUtil.RANGE_TYPE_TODAY:
			rst = 250;
			break;
		case RangeUtil.RANGE_TYPE_WEEK:
			rst = 100;
			break;
		case RangeUtil.RANGE_TYPE_MONTH:
			rst = 330;
			break;
		case RangeUtil.RANGE_TYPE_SEMIANNUAL:
			rst = 280;
			break;
		}
		return rst;
	}
	
	private PointD createPoint(String valStr, String timeStr) {
		PointD p = null;
		switch (rangeType) {
		case RangeUtil.RANGE_TYPE_TODAY:
			p = createDayData(valStr, timeStr);
			break;
		case RangeUtil.RANGE_TYPE_WEEK:
			p = createWeekData(valStr, timeStr);
			break;
		case RangeUtil.RANGE_TYPE_MONTH:
			p = createMonthData(valStr, timeStr);
			break;
		case RangeUtil.RANGE_TYPE_SEMIANNUAL:
			p =  createSemiAnnualData(valStr, timeStr);
			break;
		}
		map.add(timeStr);
		return p;
	}

	
	private List<String> getLabels() {
		Calendar cal = Calendar.getInstance();
		try {
			Date start = SDF.parse(range.getStartTime());
			cal.setTime(start);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String[] labels = null;
		switch (rangeType) {
		case RangeUtil.RANGE_TYPE_TODAY:
			labels = new String[25];
			Arrays.fill(labels, "");
			labels[1] = "1";
			for(int i=4;i<25;i+=4) {
				labels[i] =String.valueOf(i);
			}
			break;
		case RangeUtil.RANGE_TYPE_WEEK:
			labels = new String[10];
			Arrays.fill(labels, "");
			for(int i=1;i<10;i++) {
				labels[i] = SDF_DAY.format(cal.getTime());
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
			break;
		case RangeUtil.RANGE_TYPE_MONTH:
			labels = new String[40];
			Arrays.fill(labels, "");
			for(int i=1;i<40;i+=4) {
				labels[i] = SDF_DAY.format(cal.getTime());
				cal.add(Calendar.DAY_OF_YEAR, 4);
			}
			break;
		case RangeUtil.RANGE_TYPE_SEMIANNUAL:
			labels = new String[30];
			Arrays.fill(labels, "");
			for(int i=1;i<30;i+=4) {
				labels[i] = SDF_DAY.format(cal.getTime());
				cal.add(Calendar.WEEK_OF_YEAR
						, 4);
			}
			break;
		}
		return Arrays.asList(labels);
	}
	
	private PointD createDayData(String valStr, String timeStr) {
		Calendar cal = Calendar.getInstance();
		double val = Double.valueOf(valStr);
		try {
			Date time = SDF.parse(timeStr);
			cal.setTime(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int x = cal.get(Calendar.HOUR_OF_DAY);
		return new PointD(x*10, val);
	}

	private PointD createWeekData(String valStr, String timeStr) {
		return getDayDiffPoint(valStr, timeStr);
	}
	
	private PointD createMonthData(String valStr,String timeStr) {
		return getDayDiffPoint(valStr, timeStr);
	}
	
	private PointD createSemiAnnualData(String valStr,String timeStr) {
		double val = Double.valueOf(valStr);
		double diff =0 ;
		try {
			Date time1 = SDF.parse(range.getStartTime());
			Date time2 = SDF.parse(timeStr);
			diff = (double)(time2.getTime()-time1.getTime())/WEEK_MILLSECONDS;
			diff++;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new PointD(diff*10, val);
	}

	private PointD getDayDiffPoint(String valStr, String timeStr) {
		String start = range.getStartTime();
		long day = 0;
		double val = Double.valueOf(valStr);
		try {
			Date time1 = SDF.parse(start);
			Date time2 = SDF.parse(timeStr);
			day = (time2.getTime() - time1.getTime()) /DAY_MILLSECONDS;
			day++;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new PointD(day*10, val);
	}
}
