package cmccsi.mhealth.app.sports.errorhandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;

//import cn.jpush.android.api.JPushInterface;

public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log tag */
	public static final String TAG = "CrashHandler";
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = false;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;

	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private StringBuilder mDeviceCrashInfo = new StringBuilder();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (Config.isMail) {
			sendMail(ex);
		}
		handleException(ex);
		if (mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			for (Activity activity : BaseActivity.allActivity) {
				activity.finish();
			}
			System.exit(0);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		final String msg = ex.getLocalizedMessage();
		// 收集设备信息
		collectCrashDeviceInfo(mContext);
		// 保存错误报告文件
		saveCrashInfoToFile(ex);
		// 发送错误报告到服务器
		// sendCrashReportsToServer(mContext);
		// 使用 Toast 来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		// TODO 使用HTTP Post 发送错误报告到服务器
		// 这里不再详述,开发者可以根据OPhoneSDN上的其他网络操作
		// 教程来提交错误报告
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private void saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.append("\r\n" + result);

		try {
			long timestamp = System.currentTimeMillis();
			Common.wirteStringToSdAfterCreateDirs(Config.ERRORLOG_URL, "log_" + Common.getDateAsYYYYMMDDHHMMSS(timestamp)
					+ ".txt", mDeviceCrashInfo.toString());
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.append("\r\n" + VERSION_NAME + " : " + pi.versionName == null ? "未设定" : pi.versionName);
				mDeviceCrashInfo.append("\r\n" + VERSION_CODE + " : " + pi.versionCode);
				mDeviceCrashInfo.append("\r\n" + "userName : " + PreferencesUtils.getString(mContext, SharedPreferredKey.PHONENUM,""));
			}
			
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.append("\r\n" + field.getName() + " : " + field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}

		}

	}

	/**
	 * 发送邮件
	 * 
	 * @param exception
	 */
	private void sendMail(final Throwable exception) {
		new Thread() {
			public void run() {
				try {
					EmailSender sender = new EmailSender();
					// 设置服务器地址和端口，网上搜的到
					sender.setProperties("smtp.139.com", "25");
					// 分别设置发件人，邮件标题和文本内容
					sender.setMessage("13810618235@139.com", "哎呀妈呀！出错啦", collectDeviceInfo(exception));
					// 设置收件人
					sender.setReceiver(new String[] {"13810618235@139.com"});
					// 这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
					// if (Configs.REDIRECTTO_FILE) {
					// sender.addAttachment(mContext.getFilesDir() +
					// File.separator + Configs.LOGFILEPREFIX + ".log");
					// }
					// 发送邮件
					sender.sendEmail("smtp.139.com", "13810618235@139.com", "zyqt332211");
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private String collectDeviceInfo(Throwable exception) {
		String version = "";
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			version = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		String deviceInfo = "手机型号：" + android.os.Build.MODEL + " \r\n" + "系统版本：" + android.os.Build.VERSION.RELEASE + " \r\n"
				+ "系统SDK版本号" + android.os.Build.VERSION.SDK_INT + " \r\n" + "程序版本：" + version + " \r\n" 
				+"\r\n" + "userName : " + PreferencesUtils.getString(mContext, SharedPreferredKey.PHONENUM,"")
				+ sw.toString();
		
		return deviceInfo;
	}
}