package cmccsi.mhealth.app.sports.appversion;

import android.content.Context;

/**
 * 根据资源的名字获取其ID值，做包时需要用到
 */
public class MResource {
	/**
	 * 利用反射获取资源的id值，此工程中使用的id值为第三方代码中的资源，所以需将此工程中使用到的资源文件拷贝到第三方代码中。
	 * 
	 * @param context
	 *            上下文
	 * @param className
	 *            资源所属类别，例如id、drawable、layout、style等
	 * @param name
	 *            资源的名称
	 * @return
	 */
	public static int getIdByName(Context context, String className, String name) {
		String packageName = context.getPackageName();
		Class<?> r = null;
		int id = 0;
		try {
			r = Class.forName(packageName + ".R");
			Class<?>[] classes = r.getClasses();
			Class<?> desireClass = null;
			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}
			if (desireClass != null)
				id = desireClass.getField(name).getInt(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 针对styleable中自定义的attr，获取styleable的int[]
	 * 
	 * @param context
	 * @param name
	 *            styleable名称
	 * @return
	 */
	public static int[] getIdsByName(Context context, String name) {
		String packageName = context.getPackageName();
		Class<?> r = null;
		int[] id = {};
		try {
			r = Class.forName(packageName + ".R");
			Class<?>[] classes = r.getClasses();
			Class<?> desireClass = null;
			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals("styleable")) {
					desireClass = classes[i];
					break;
				}
			}
			if (desireClass != null)
				id = (int[]) desireClass.getField(name).get(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return id;
	}
}