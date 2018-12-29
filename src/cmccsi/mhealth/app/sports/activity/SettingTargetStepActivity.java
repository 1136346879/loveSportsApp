package cmccsi.mhealth.app.sports.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.SaveAreaInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.SimpleNet;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.R;

public class SettingTargetStepActivity extends BaseActivity {

	private String TAG = "SettingTargetStepActivity";

	private Context mContext = this;
	private EditText mEditTextTargetNum;
	private Button mButtonOK;
	private StepService mService;
	private boolean isChanged = false;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Logger.i(TAG, "---onServiceConnected");
			mService = ((StepService.StepBinder) service).getService();
			mService.notifyNotificationPercent();
		}

		public void onServiceDisconnected(ComponentName className) {
			Logger.i(TAG, "---onServiceDisconnected");
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismiss();
			switch (msg.what) {
			case 0:
				UiView();
				break;
			case 1:
				BaseToast("同步失败");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_target);
		mEditTextTargetNum = (EditText) findViewById(R.id.setting_num);

		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String targetnum = sharedPreferences.getString(SharedPreferredKey.TARGET_STEP, "10000");
		mEditTextTargetNum.setText(targetnum);
		mEditTextTargetNum.setFocusable(true);
		mEditTextTargetNum.requestFocus();
		onFocusChange(true, mEditTextTargetNum);

		mButtonOK = (Button) findViewById(R.id.setting_targer_ok);

		BaseBackKey("设置运动目标", this);
		mButtonOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String updateTarget = mEditTextTargetNum.getText().toString();
				// Regex
				if (updateTarget.matches("^[1-9]\\d{3,4}$")) {
					showProgressDialog(getString(R.string.text_wait), SettingTargetStepActivity.this);
					new Thread() {
						public void run() {
							try {
//								String URL = "http://"
//										+ PreferencesUtils.getString(SettingTargetStepActivity.this,
//												SharedPreferredKey.SERVER_NAME, null)
//										+ "/openClientApi.do?action=settargetvalue&userid=" + DataSyn.getInstance().forSetting()
//										+ "&value=" + mEditTextTargetNum.getText().toString().trim() + "&type=target_step";
//								Log.d("targetstep", "URL  " + URL);
//								SimpleNet.simpleGet(URL, mHandler, mContext);
								SaveAreaInfo temp=new SaveAreaInfo();
								int result = DataSyn.getInstance().saveTargetStep(mEditTextTargetNum.getText().toString().trim(), temp);
								Message msg = new Message();
								if (result==0) {
									msg.what = 0;
								} else {
									msg.what = 1;
								}
								mHandler.sendMessage(msg);
							} catch (Exception e) {
								e.printStackTrace();
								ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
							} finally {

							}
						}
					}.start();
				} else {
					BaseToast("请输入1000-99999之间的整数");
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.i(TAG, "---onDestroy " + isChanged);
		if(isChanged){
			unbindService(mConnection);	
		}
	}

	private void UiView() {
		// 绑定Service
		Intent _intent = new Intent(this, StepService.class);
		
		bindService(_intent, mConnection, Context.BIND_AUTO_CREATE);
		isChanged = true;
		BaseToast("目标步数同步成功！");
		// Editor sharedata =
		// getSharedPreferences(SharedPreferredKey.SHARED_NAME,
		// Context.MODE_PRIVATE).edit();
		// sharedata.putString(SharedPreferredKey.TARGET_STEP,
		// mEditTextTargetNum
		// .getText().toString().trim());
		// sharedata.commit();
		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		sharedPreferences.edit().putString(SharedPreferredKey.TARGET_STEP, mEditTextTargetNum.getText().toString()).commit();
		SettingTargetStepActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
	}

}
