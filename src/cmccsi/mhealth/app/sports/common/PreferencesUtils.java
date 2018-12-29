/*
 * Copyright 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cmccsi.mhealth.app.sports.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {

	/**
	 * remove a preference key
	 * @param context
	 * 			the context
	 * @param key
	 * 			the key id
	 */
	public static void removeSp(Context context, String key) {
		Editor editor = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE).edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * Gets a preference key
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 */
	public static String getKey(Context context, int keyId) {
		return context.getString(keyId);
	}

	/**
	 * Gets a boolean preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param defaultValue
	 *            the default value
	 */
	public static boolean getBoolean(Context context, String key, boolean defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * Sets a boolean preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param value
	 *            the value
	 */
	@SuppressLint("CommitPrefEdits")
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Gets an integer preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param defaultValue
	 *            the default value
	 */
	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, defaultValue);
	}

	/**
	 * Sets an integer preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param value
	 *            the value
	 */
	@SuppressLint("CommitPrefEdits")
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
		// ApiAdapterFactory.getApiAdapter().applyPreferenceChanges(editor);
	}

	/**
	 * Gets a long preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 */
	public static long getLong(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, -1);
	}

	/**
	 * Sets a long preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param value
	 *            the value
	 */
	@SuppressLint("CommitPrefEdits")
	public static void putLong(Context context, String key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
		// ApiAdapterFactory.getApiAdapter().applyPreferenceChanges(editor);
	}

	/**
	 * Gets a string preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param defaultValue
	 *            default value
	 */
	public static String getString(Context context, String key, String defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defaultValue);
	}
	
	/**
	 *  获取账号
	 * @param context
	 * @return
	 */
	public static String getPhoneNum(Context context){
		return PreferencesUtils.getString(context, SharedPreferredKey.PHONENUM, null);
	}
	/**
	 * 获取密码
	 * @param context
	 * @return
	 */
	public static String getPhonePwd(Context context){
		return PreferencesUtils.getString(context, SharedPreferredKey.PASSWORD, null);
	}

	/**
	 * 获取多个sharedprefence的String
	 * 
	 * @param context
	 * @param defaultValue
	 * @param arrKey
	 *            可变参数
	 * @return
	 */
	public static List<String> getStringArr(Context context, String defaultValue, String... arrKey) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < arrKey.length; i++) {
			list.add(sharedPreferences.getString(arrKey[i], defaultValue));
		}
		return list;
	}

	/**
	 * Sets a string preference value.
	 * 
	 * @param context
	 *            the context
	 * @param keyId
	 *            the key id
	 * @param value
	 *            the value
	 */
	@SuppressLint("CommitPrefEdits")
	public static void putString(Context context, String keyId, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(keyId, value);
		editor.commit();
		// ApiAdapterFactory.getApiAdapter().applyPreferenceChanges(editor);
	}
	
	/**
	 * 保存SharedPreferences
	 * @param context
	 * @param input 输入
	 */
	public static void putString(Context context,Map<String, String> input)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		for (String key : input.keySet()) {
			editor.putString(key, input.get(key));
		}
		editor.commit();
	}
}
