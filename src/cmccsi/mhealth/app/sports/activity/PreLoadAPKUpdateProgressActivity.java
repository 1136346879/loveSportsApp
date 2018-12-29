package cmccsi.mhealth.app.sports.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.view.DownLoadApkProgress;
import cmccsi.mhealth.app.sports.R;

/**
 * 应用升级下载功能类
 * @type PreLoadAPKUpdateProgressActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午10:08:34
 */
public class PreLoadAPKUpdateProgressActivity extends BaseActivity {
	int internet;// 0未无网络，1，2有网
	Boolean update = false;
	// 远程下载用到的变量
	private String mCurrentFilePath = "";
//**	private String mCurrentTempFilePath = "";
	private String mStrURL = "";
	private String mFileEx = "";
	private String mFileNa = "";
	private String mUriPath = Config.UPDATE_SERVER + "SportHealth.apk";
	private DownLoadApkProgress mMyProgress = null;
	// private Intent mIntent = null;
	private int mProgressMax = 0; // 文件的长度
	private int mDownLoadFileSize; // 总共下载了多少字节
	private TextView mTextViewFileName;
	private TextView mTextViewFilelength;

	private Thread mThreadDownload;
	private boolean mIsInterruptted;


	Handler mHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
			//判断当先线程是否被中断没有的话返回false，终端的话返回true
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					mMyProgress.setMax(mProgressMax);
					if (mProgressMax > 0) {
						double leng = mProgressMax / 1024 / 1024;
						mTextViewFilelength.setText("文件大小：" + leng + "M");
					}
				case 1:
					mMyProgress.setProgress(mDownLoadFileSize);
					break;
				case 2:
					Toast.makeText(PreLoadAPKUpdateProgressActivity.this, "文件下载完成", 1)
							.show();
					PreLoadAPKUpdateProgressActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
					break;
				}
			}
			super.handleMessage(msg);
		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressupdate);
		//将Activity设置成diglog窗口德形式，点击空白处是不会消失的
		PreLoadAPKUpdateProgressActivity.this.setFinishOnTouchOutside(false);
		findView();
		Random tmprand = new Random();
		mUriPath = getIntent().getStringExtra("downloadsite") + "?code="
				+ tmprand.nextInt();
		mStrURL = mUriPath;
		/* 取得欲安装程序之文件名称 */
		// mFileEx = mStrURL.substring(mStrURL.lastIndexOf(".") + 1,
		// mStrURL.length())
		// .toLowerCase();
		mFileEx = "apk";
		mFileNa = mStrURL.substring(mStrURL.lastIndexOf("/") + 1,
				mStrURL.lastIndexOf("."));
		mTextViewFileName.setText("文件名："+mFileNa+".apk");
		getFile(mUriPath);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mIsInterruptted = true;

			this.finish();
			return true;
		}
		return false;
	}

	private void findView() {
		// 
		mMyProgress = (DownLoadApkProgress) findViewById(R.id.pgsBar);
		mTextViewFileName = (TextView) findViewById(R.id.tv_fileName);
		mTextViewFilelength = (TextView) findViewById(R.id.tv_fileLength);
	}

	/** 处理下载URL文件自定义函数 */
	private void getFile(final String strPath) {
		try {
			if (strPath.equals(mCurrentFilePath)) {
				getDataSource(strPath);
			}
			mCurrentFilePath = strPath;
			// 开启子线程
			Runnable r = new Runnable() {
				@Override
                public void run() {
					try {
						getDataSource(strPath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			mThreadDownload = new Thread(r);
			mThreadDownload.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 取得远程文件 */
	private void getDataSource(String strPath) throws Exception {
		//判断用户输入时否是合法的url
		if (!URLUtil.isNetworkUrl(strPath)) {
			// url不正确
		} else {
			/* 取得URL */
			URL myURL = new URL(strPath + "?x=" + Math.random());
			/* 创建连接 */
			URLConnection conn = myURL.openConnection();
			conn.connect();
			mProgressMax = conn.getContentLength();
			sendMsg(0);
			if (mProgressMax == -1) {
				// 文件不存在41
			} else {
				/* InputStream 下载文件 */
				InputStream is = conn.getInputStream();
				if (is == null) {
					throw new RuntimeException("stream is null");
				}
				/* 创建临时文件 */
				File myTempFile = new File(
						Environment.getExternalStorageDirectory(), mFileNa
								+ "." + mFileEx);
				// if (myTempFile.exists()) {
				// // 文件名存在了
				// sendMsg(2);
				// openFile(myTempFile);
				// } else
				{
					/* 取得暂存盘路径 */
//					mCurrentTempFilePath = myTempFile.getAbsolutePath();
					/* 将文件写入暂存盘 */
					FileOutputStream fos = new FileOutputStream(myTempFile);
					byte buf[] = new byte[1024];

					mIsInterruptted = false;
					do {
						int numread = is.read(buf);
						if (numread <= 0) {
							break;
						}
						mDownLoadFileSize += numread;
						fos.write(buf, 0, numread);
						sendMsg(1); // 更新
					} while (!mIsInterruptted);

					if (!mIsInterruptted) {
						sendMsg(2);// 下载完成
						/* 打开文件进行安装 */
						openFile(myTempFile);
					}

					try {
						is.close();
						fos.close();
					} catch (Exception ex) {

					}
				}
			}
		}
	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		mHandler.sendMessage(msg);
	}

	/* 在手机上打开文件的method */
	private void openFile(File f) {
		// 安装新APK 修改配置代码
		Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
				.edit();
		sharedata.putInt("INSTALL", 1); // 默认为0表示安装后不进行任何操作 为1则需要进行相关数据库操作
		sharedata.commit();

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		// String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f),
				"application/vnd.android.package-archive");
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}

	/* 判断文件MimeType的method */
	// private String getMIMEType(File f) {
	// String type = "";
	// String fName = f.getName();
	// /* 取得扩展名 */
	// String end = fName
	// .substring(fName.lastIndexOf(".") + 1, fName.length())
	// .toLowerCase();
	//
	// /* 依扩展名的类型决定MimeType */
	// if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
	// || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
	// type = "audio";
	// } else if (end.equals("3gp") || end.equals("mp4")) {
	// type = "video";
	// } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
	// || end.equals("jpeg") || end.equals("bmp")) {
	// type = "image";
	// } else if (end.equals("apk")) {
	// /* android.permission.INSTALL_PACKAGES */
	// type = "application/vnd.android.package-archive";
	// } else {
	// type = "*";
	// }
	// /* 如果无法直接打开，就跳出软件列表给用户选择 */
	// if (end.equals("apk")) {
	// } else {
	// type += "/*";
	// }
	// return type;
	// }

	/* 自定义删除文件方法 */
	@SuppressWarnings("unused")
	private void delFile(String strFileName) {
		File myFile = new File(strFileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}
}