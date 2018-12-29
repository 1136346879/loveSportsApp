package cmccsi.mhealth.app.sports.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtils {
	/**
	 * 格式类型
	 * @type FormatType
	 * TODO
	 * @author jiazhi.cao
	 * @time 2015-3-9上午11:36:38
	 */
	public enum FormatType{
		/**
		 * yyyy-MM-dd HH:mm:ss
		 */
		DateLong,
		/**
		 * yyyyMMdd
		 */
		DateShot,
		/**
		 * yyyy-MM-dd
		 */
		DateWithUnderline,
		/**
		 * yyyy/MM/dd 
		 */
		DateWithDiagonal,
		/**
		 * M月d日
		 */
		DateWithDiagonalNoYear
	}
	
	/**
	 * 获取format 类型
	 * TODO
	 * @param type format类型
	 * @return
	 * @return String
	 * @author jiazhi.cao
	 * @time 下午1:52:26
	 */
	private static String getFormatType(FormatType type)
	{
		String result="";
		switch (type) {
		case DateLong:
			result="yyyy-MM-dd HH:mm:ss";
			break;
		case DateShot:
			result="yyyyMMdd";
			break;
		case DateWithUnderline:
			result="yyyy-MM-dd";
			break;
		case DateWithDiagonal:
			result="yyyy/MM/dd";
			break;
		case DateWithDiagonalNoYear:
			result="M月d日";
			break;
		default:
			result="yyyy-MM-dd HH:mm:ss";
			break;
		}
		return result;
	}
	
	/**
	 * 日期时间格式化
	 * TODO
	 * @param date 日期
	 * @param type 格式枚举
	 * @return ""
	 * @return String 格式化时间
	 * @author jiazhi.cao
	 * @time 下午1:55:57
	 */
	public static String DateToString(Date date,FormatType type) {
		String result="";
		SimpleDateFormat sf=new SimpleDateFormat(getFormatType(type), Locale.getDefault());
		result=sf.format(date);
		return result;
	}
	
	/**
	 * 日期时间格式化
	 * TODO
	 * @param second Millis
	 * @param type 格式枚举
	 * @return
	 * @return String 格式化时间
	 * @author jiazhi.cao
	 * @time 下午2:08:13
	 */
	public static String MillisToString(long second,FormatType type) {
		String result="";
		Date date=new Date(second);
		DateToString(date, type);
		return result;
	}
	
	/**
	 * 改变时间格式
	 * TODO
	 * @param date 时间
	 * @param oldtype 旧时间格式类型
	 * @param newtype 新时间格式类型
	 * @return
	 * @return String
	 * @author jiazhi.cao
	 * @time 下午2:23:24
	 */
	public static String ChangeFormat(String date,FormatType oldtype,FormatType newtype)
	{
		
		String result ="";
		SimpleDateFormat sf=new SimpleDateFormat(getFormatType(oldtype), Locale.getDefault());
		SimpleDateFormat newsf=new SimpleDateFormat(getFormatType(newtype), Locale.getDefault());
		try {
			Date tempdate= sf.parse(date);
			result=newsf.format(tempdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 增加天数
	 * TODO
	 * @param date 输入日期
	 * @param diff 差距天数
	 * @param type 输入日期格式
	 * @return
	 * @return String 输出原格式日期
	 * @author jiazhi.cao
	 * @time 下午2:47:28
	 */
	public static String AddDays(String date,int diff,FormatType type)
	{
		String result="";
		try
		{
			if(diff==0)
			{
				return date;
			}
			SimpleDateFormat sf=new SimpleDateFormat(getFormatType(type));
			Date tempdate=sf.parse(date);			
			Calendar cr=Calendar.getInstance();
			cr.setTime(tempdate);
			cr.add(Calendar.DAY_OF_YEAR, diff);
			result=sf.format(cr.getTime());
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	/**
	 * 增加天数
	 * TODO
	 * @param date 输入日期
	 * @param diff 差距天数
	 * @param type 输入日期格式
	 * @return
	 * @return String 输出原格式日期
	 * @author jiazhi.cao
	 * @time 下午2:47:28
	 */
	public static Date AddDays(Date date,int diff)
	{
		if(diff==0)
			{
				return date;
			}			
			Calendar cr=Calendar.getInstance();
			cr.setTime(date);
			cr.add(Calendar.DAY_OF_YEAR, diff);
			return cr.getTime();

	}
	
	/**
	 * 字符串转日期
	 * TODO
	 * @param date 字符串
	 * @param type 日期格式
	 * @return
	 * @return Date 日期
	 * @author jiazhi.cao
	 * @time 下午4:12:09
	 */
	public static Date StringToDate(String date,FormatType type)
	{
		Date resultDate=null;
		SimpleDateFormat sf=new SimpleDateFormat(getFormatType(type));
		try {
			resultDate= sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultDate;
	}
	
	/**
	 * 日期比较
	 * TODO
	 * @param DATE1 日期1
	 * @param DATE2 日期2
	 * @return int 1<2返回1 1>2返回-1 1=2返回0
	 * @return 
	 * @author jiazhi.cao
	 * @time 下午5:01:04
	 */
	public static int compare_date(String DATE1, String DATE2) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.before(dt2)) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.after(dt2)) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

	/**
	 * 比较日期是否是今天
	 * TODO
	 * @param date
	 * @return
	 * @return boolean
	 * @author jiazhi.cao
	 * @time 上午10:11:59
	 */
	public static boolean isToday(Date date) {
		String inputDate=DateToString(date, FormatType.DateShot);
		String Today=DateToString(new Date(), FormatType.DateShot);
		return inputDate.equals(Today);
	}
}
