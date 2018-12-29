package cmccsi.mhealth.app.sports.common.utils;

import android.util.Log;
import cmccsi.mhealth.app.sports.common.Constants;

public class LogUtils {

	public static void d(String content) {
		if (!Constants.debugD)
			return;
		StackTraceElement caller = ReflectUtils.getCallerMethodName();
//		String tag = generateTag(caller);
		String tag = "lxl";
		Log.d(tag, content);
	}

	public static void d(String tag, String content) {
		if (!Constants.debugD)
			return;
		Log.d(tag, content);
	}

	public static void e(String content) {
		if (!Constants.debugE)
			return;
		StackTraceElement caller = ReflectUtils.getCallerMethodName();
		String tag = generateTag(caller);
		Log.e(tag, content);
	}

	public static void e(String tag, String content) {
		if (!Constants.debugE)
			return;
		Log.e(tag, content);
	}

	public static void i(String content) {
		if (!Constants.debugI)
			return;
		StackTraceElement caller = ReflectUtils.getCallerMethodName();
		String tag = generateTag(caller);
		Log.i(tag, content);
	}

	public static void i(String tag, String content) {
		if (!Constants.debugI)
			return;
		Log.i(tag, content);
	}

	public static void v(String content) {
		if (!Constants.debugV)
			return;
		StackTraceElement caller = ReflectUtils.getCallerMethodName();
		String tag = generateTag(caller);
		Log.v(tag, content);
	}

	public static void v(String tag, String content) {
		if (!Constants.debugV)
			return;
		Log.v(tag, content);
	}

	public static void w(String content) {
		if (!Constants.debugW)
			return;
		StackTraceElement caller = ReflectUtils.getCallerMethodName();
		String tag = generateTag(caller);
		Log.w(tag, content);
	}

	public static void w(String tag, String content) {
		if (!Constants.debugW)
			return;
		Log.w(tag, content);
	}

	private static String generateTag(StackTraceElement caller) {
		String tag = "%s[%s, %d]";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		return tag;
	}

}
