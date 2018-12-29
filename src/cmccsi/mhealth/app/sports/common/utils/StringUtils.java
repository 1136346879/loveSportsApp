package cmccsi.mhealth.app.sports.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0 || cs.equals("null")) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(CharSequence cs) {
		return !StringUtils.isBlank(cs);
	}

	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return all blank or all not blank && equals
	 */
	public static boolean isSame(String str1, String str2) {
		if (StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) {
			return true;
		}
		if (!StringUtils.isBlank(str1) && !StringUtils.isBlank(str2) && str1.equals(str2)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否是电话号码
	 * 
	 * @param str
	 * @return true or false
	 */
	public static boolean isPhone(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断字符串是否是邮箱
	 * 
	 * @param str
	 * @return true or false
	 */
	public static boolean isEmailAddress(String str) {
		Pattern pattern = Pattern.compile("^([0-9a-zA-Z\\._-])+@([0-9a-zA-Z]+\\.)+([a-zA-Z])+$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断字符串是否是数字和字母
	 * 
	 * @param str
	 * @return true or false
	 */
	public static boolean isDigitChar(String str) {
		Pattern pattern = Pattern.compile("([0-9a-zA-Z])+");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 将返回的距离结果数据，优化成显示数据
	 * 
	 * @param distance
	 * @return
	 */
	public static String parseDistance(String distance) {
		String result = "";
		if (distance != null) {
			Float dis = Float.valueOf(distance) * 1000;
			result = String.valueOf(dis);
			result = result.substring(0, result.indexOf(".")) + "米";
		}
		return result;
	}

	/**
	 * 校验有效电话号码
	 * 
	 * @param telNumber
	 * @return
	 */
	public static String getValidTelNumber(String telNumber) {
		String str = telNumber.replace(" ", "");
		str = str.replace("-", "");
		str = str.replace("+86", "");
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(str);
		return m.matches() ? str : null;
	}

	/**
	 * 去掉字符串中标点符号及其他特殊符号
	 * 
	 * @param
	 * @return String
	 */
	public static String format(String s) {
		String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
		return str;
	}
	

	/**
	 * 过滤String字符串前后空格
	 * 
	 * @param str
	 * @return
	 */
	public static String trimInnerSpaceStr(String str) {
		str = str.trim();
		while (str.startsWith(" ")) {
			str = str.substring(1, str.length()).trim();
		}
		while (str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1).trim();
		}

		return str;
	}

}
