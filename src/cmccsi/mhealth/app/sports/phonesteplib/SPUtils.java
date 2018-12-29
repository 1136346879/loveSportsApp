package cmccsi.mhealth.app.sports.phonesteplib;

import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的工具类
 * 
 * @author luckchoudog
 */
public class SPUtils {

	private static SharedPreferences sp;

	private SPUtils() {
	}

	/**
	 * 获取SPUtils对象实例
	 * 
	 * @param context
	 *            上下文
	 * @return SPUtils对象实例
	 */
	public static SPUtils getInstance(Context context) {
		if (null == sp) {
			sp = context.getSharedPreferences(SharedPreferredKey.SHARED_PHONENAME, Context.MODE_PRIVATE);
		}
		return new SPUtils();
	}

	/**
	 * 是否包含某个字段
	 * 
	 * @param key
	 *            字段名称
	 * @return false表示没有，true表示有
	 */
	public static boolean hasKey(String key) {
		if (null == sp)
			return false;
		return sp.contains(key);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @return 这个字段的内容，没有返回null
	 */
	public String getString(String key) {
		return sp.getString(key, null);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @param defaultValue
	 *            默认值
	 * @return 这个字段的内容，没有返回默认值
	 */
	public String getString(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	/**
	 * 设置某个字段的内容（增加和修改）
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            字段内容
	 */
	public void putString(String key, String value) {
		sp.edit().putString(key, value).commit();
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @return 这个字段的内容，没有返回false
	 */
	public boolean getBoolean(String key) {
		return sp.getBoolean(key, false);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @param defaultValue
	 *            默认值
	 * @return 这个字段的内容，没有返回默认值
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	/**
	 * 设置某个字段的内容（增加和修改）
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            字段内容
	 */
	public void putBoolean(String key, boolean value) {
		sp.edit().putBoolean(key, value).commit();
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @return 这个字段的内容，没有返回0
	 */
	public int getInt(String key) {
		return sp.getInt(key, 0);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @param defaultValue
	 *            默认值
	 * @return 这个字段的内容，没有返回默认值
	 */
	public int getInt(String key, int defaultValue) {
		return sp.getInt(key, defaultValue);
	}

	/**
	 * 设置某个字段的内容（增加和修改）
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            字段内容
	 */
	public void putInt(String key, int value) {
		sp.edit().putInt(key, value).commit();
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @return 这个字段的内容，没有返回0
	 */
	public float getFloat(String key) {
		return sp.getFloat(key, 0);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @param defaultValue
	 *            默认值
	 * @return 这个字段的内容，没有返回默认值
	 */
	public float getFloat(String key, float defaultValue) {
		return sp.getFloat(key, defaultValue);
	}

	/**
	 * 设置某个字段的内容（增加和修改）
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            字段内容
	 */
	public void putFloat(String key, float value) {
		sp.edit().putFloat(key, value).commit();
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @return 这个字段的内容，没有返回0
	 */
	public long getLong(String key) {
		return sp.getLong(key, 0);
	}

	/**
	 * 获取某个字段的内容
	 * 
	 * @param key
	 *            字段名称
	 * @param defaultValue
	 *            默认值
	 * @return 这个字段的内容，没有返回默认值
	 */
	public long getLong(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}

	/**
	 * 设置某个字段的内容（增加和修改）
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            字段内容
	 */
	public void putLong(String key, long value) {
		sp.edit().putLong(key, value).commit();
	}

	/**
	 * 移除某个字段的内容
	 * 
	 * @param key字段名称
	 */
	public void removeData(String key) {
		sp.edit().remove(key).commit();
	}

	/**
	 * 清空整个数据
	 */
	public void clearData() {
		sp.edit().clear().commit();
	}
}