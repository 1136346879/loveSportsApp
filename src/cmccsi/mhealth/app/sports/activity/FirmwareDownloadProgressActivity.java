package cmccsi.mhealth.app.sports.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.view.DownLoadApkProgress;
import cmccsi.mhealth.app.sports.R;

/**
 * 固件下载功能类
 * @type FirmwareDownloadProgressActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午10:07:28
 */
public class FirmwareDownloadProgressActivity extends BaseActivity {

	private static final String TAG = "FirmwareDownloadProgressActivity";

	private static final int DOWNLOAD_SUCCESSED = 2;
	private static final int DOWNLOAD_FAILED = 3;
	private static final int DOWNLOAD_PRCENT = 1;
	private static final int DOWNLOAD_SET_SIZE = 0;

	int internet;// 0未无网络，1，2有网

	Boolean update = false;
	// 远程下载用到的变量
	private String mCurrentFilePath = "";
	private String mStrURL = "";
	private String mFileNa = "";
	private String mFileFullName = "";
	private String mUriPath = Config.UPDATE_SERVER + "SportHealth.apk";
	private DownLoadApkProgress mMyProgress = null;
	private int mProgressMax = 0; // 文件的长度
	private int mDownLoadFileSize; // 总共下载了多少字节
	private TextView mTvMessage;
	private TextView mTvPrcent;

	private Thread mThreadDownload;
	private boolean mIsInterruptted;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 判断当先线程是否被中断没有的话返回false，终端的话返回true
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case DOWNLOAD_SET_SIZE:
					mMyProgress.setMax(mProgressMax);
					mTvPrcent.setText("0%");
				case DOWNLOAD_PRCENT:
					mMyProgress.setProgress(mDownLoadFileSize);
					int prcent = mDownLoadFileSize / mProgressMax;
					mTvPrcent.setText(prcent + "%");
					break;
				case DOWNLOAD_SUCCESSED:
					Intent intent = new Intent();
					intent.putExtra("result", "download");
					setResult(RESULT_OK, intent);
					// ToastUtils.showToast_L(getApplicationContext(),
					// "固件下载完成，请再次点击按钮更新固件");
					FirmwareDownloadProgressActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
					break;
				case DOWNLOAD_FAILED:
					ToastUtils.showToast_L(getApplicationContext(), getResources().getString(R.string.firmwaredownloadprogressactivity_downloadmsg));
					FirmwareDownloadProgressActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
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
		setContentView(R.layout.activity_progress_new);

		// 将Activity设置成diglog窗口德形式，点击空白处是不会消失的
		FirmwareDownloadProgressActivity.this.setFinishOnTouchOutside(false);

		findView();

		Random tmprand = new Random();
		mUriPath = getIntent().getStringExtra("downloadsite") + "?code=" + tmprand.nextInt();
		mStrURL = mUriPath;
		Logger.i(TAG, "--- " + getIntent().getStringExtra("downloadsite"));
		Logger.i(TAG, "--- " + mStrURL);
		/* 取得欲下载文件名称 */
		mFileNa = mStrURL.substring(mStrURL.lastIndexOf("/") + 1, mStrURL.lastIndexOf("."));
		mFileFullName = mFileNa + ".bin";

		mTvMessage.setText(getResources().getString(R.string.firmwaredownloadprogressactivity_downloadprogress));
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
		mTvMessage = (TextView) findViewById(R.id.tv_progress_message);
		mTvPrcent = (TextView) findViewById(R.id.tv_progress_prcent);
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
		// 判断用户输入时否是合法的url
		if (!URLUtil.isNetworkUrl(strPath)) {
			// url不正确,退出下载
			Logger.i(TAG, "--- url不正确,退出下载");
			sendMsg(DOWNLOAD_FAILED);
		} else {
			/* 取得URL */
			URL myURL = new URL(strPath + "?x=" + Math.random());
			/* 创建连接 */
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();

			mProgressMax = conn.getContentLength();
			sendMsg(DOWNLOAD_SET_SIZE);

			if (mProgressMax == -1 || conn.getResponseCode() != 200) {
				// 文件不存在41
				Logger.i(TAG, "--- 文件不存在");
				sendMsg(DOWNLOAD_FAILED);

			} else {

				/* InputStream 下载文件 */
				InputStream is = conn.getInputStream();

				if (is == null) {
					Logger.i(TAG, "--- conn.getInputStream() is null");
					sendMsg(DOWNLOAD_FAILED);
				}
				/* 创建临时文件夹 */
				File myTempFileDir = new File(Environment.getExternalStorageDirectory() + "/adlDownload");
				if (!myTempFileDir.exists()) {
					myTempFileDir.mkdirs();
				}

				/* 创建临时文件 */
				File myTempFile = new File(myTempFileDir, mFileFullName);
				myTempFile.createNewFile();
				if (!myTempFile.isFile()) {
					Logger.i(TAG, "--- 文件创建不成功");
					sendMsg(DOWNLOAD_FAILED);
				} else {
					/* 将文件写入暂存盘 */
					FileOutputStream fos = new FileOutputStream(myTempFile);
					Logger.i(TAG, "--- filseoutput");
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
						Logger.i(TAG, "---下载完成");
						sendMsg(2);// 下载完成
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

	/* 自定义删除文件方法 */
	@SuppressWarnings("unused")
	private void delFile(String strFileName) {
		File myFile = new File(strFileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}
}