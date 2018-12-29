package cmccsi.mhealth.app.sports.common.utils;

import java.lang.reflect.Field;
import java.util.Date;

public class ReflectUtils {

	public static boolean isBaseDateType(Field field) {
		Class<?> clazz = field.getType();
		return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
				|| clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
				|| clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(Boolean.class)
				|| clazz.equals(Date.class) || clazz.equals(java.util.Date.class) || clazz.equals(java.sql.Date.class)
				|| clazz.isPrimitive();
	}

	public static String getFieldName(Field field) {
		return field.getName();
	}

	@SuppressWarnings("unused")
	public static StackTraceElement getCallerMethodName() {
		StackTraceElement callerTraceElement = null;
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		return stacks[4];
	}
}
