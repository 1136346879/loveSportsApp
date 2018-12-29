package cmccsi.mhealth.app.sports.service;

import cmccsi.mhealth.app.sports.activity.PreLoadAPKUpdateProgressActivity;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.net.UpdateSoftWareTools;
import cmccsi.mhealth.app.sports.R;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 版本更新服务类
 * @type UpdateVersionService
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午10:13:25
 */
public class UpdateVersionService extends Service {
	
	private static final String TAG = "UpdateVersionService";
	
	private String mVerName = "";
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String m = (String) msg.obj;
			switch (msg.what) {
			case 0:
				updateAPK();
				break;
			case 1:
//				ToastUtils.showToast(getApplicationContext(), m);
				stopSelf();
				break;
			case 2:
				ToastUtils.showToast(getApplicationContext(), m);
				stopSelf();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Logger.i(TAG, "---onCreate");
		// 版本号
		PackageManager packageManager = this.getPackageManager();
		try {
			PackageInfo info2 = packageManager.getPackageInfo(this.getPackageName(), 0);
			if (info2 != null && info2.versionName != null) {
				mVerName = info2.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.i(TAG, "---onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Logger.i(TAG, "---onStartCommand");
		
		updateVersion();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateVersion() {
		/* 更新判断 */
		new Thread() {
			@Override
			public void run() {
				if (NetworkTool.isOnline(getApplicationContext())) {
//					mVerName = Config.getVerName(mActivity);
					if (UpdateSoftWareTools.isNewVersionAvaliable(mVerName)) {
						Message message = Message.obtain();
						message.what = 0;
						mHandler.sendMessageDelayed(message, 2000);
					} else {
						Message message = Message.obtain();
						message.what = 1;
						message.obj = "已经是最新版本";
						mHandler.sendMessage(message);
					}
				} else {
					Message message = Message.obtain();
					message.what = 2;
					message.obj = "服务器繁忙";
					mHandler.sendMessage(message);
				}
			};
		}.start();
	}
	
	/* 更新apk */
	private void updateAPK() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本: \t");
		sb.append(mVerName + "\n");
		sb.append("发现新版本:\t");
		sb.append(UpdateSoftWareTools.newVerName + "\n");
		sb.append("更新说明:\t\n");
		sb.append(UpdateSoftWareTools.newVerInfo + "\n");
		sb.append("\n是否更新?");

		final Dialog dialog;
		String dialogHeadText = "更新提示";
		String dialogContent = sb.toString();

		dialog = new Dialog(this, R.style.dialog_withStatusBar);
		dialog.setContentView(R.layout.dialog_delete);
		((TextView) dialog.findViewById(R.id.Introduction_delete)).setText(dialogHeadText);
		((TextView) dialog.findViewById(R.id.Introduction_delete_detail)).setText(dialogContent);
		dialog.findViewById(R.id.btn_dialog_delete_yes).setVisibility(View.GONE);
		dialog.findViewById(R.id.ll_dialog_delete_buttons).setVisibility(View.VISIBLE);

		dialog.findViewById(R.id.btn_dialog_delete_sure).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				// 0未无网络，1，2有网
				int internet = NetworkTool.getNetworkState(getApplicationContext());
				if (internet != 0) {
					Intent intent = new Intent();
					intent.putExtra("downloadsite", UpdateSoftWareTools.download);
					intent.setClass(getApplicationContext(), PreLoadAPKUpdateProgressActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(intent);
				} else {
					ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
				}
				stopSelf();
			}
		});
		dialog.findViewById(R.id.btn_dialog_delete_cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				stopSelf();
			}
		});
		
		//在dialog  show方法之前添加如下代码，表示该dialog是一个系统的dialog
	    dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));  
		dialog.show();
	}

}
