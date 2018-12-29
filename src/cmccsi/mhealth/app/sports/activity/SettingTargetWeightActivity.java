package cmccsi.mhealth.app.sports.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.SimpleNet;

public class SettingTargetWeightActivity extends BaseActivity {
	private EditText mEditText;
	private Context mContext = this;
	private String targetWeight;    //用户输入的目标体重
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismiss();
			switch (msg.what) {
			case SimpleNet.SIMPLENET_SUCCESS:
				UiView();
				break;
			case SimpleNet.SIMPLENET_FAIL:
				BaseToast(msg.obj + "");
				break;
			case 10001:
                BaseToast("请输入30到120之间的数值", 8);
				break;
            case 10002:
                BaseToast("请输入30到120之间的数值", 8);
                break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_targetweight);
		mEditText = (EditText) findViewById(R.id.edittext_input_your_weight);
		Button button = (Button) findViewById(R.id.button_set_weight);
		mEditText.setText(PreferencesUtils.getString(this, SharedPreferredKey.TARGET_WEIGHT, ""));
		mEditText.setFocusable(true);
		mEditText.requestFocus();
        BaseBackKey("设置体重目标", this);
		onFocusChange(true, mEditText);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                showProgressDialog(getString(R.string.text_wait), SettingTargetWeightActivity.this);
                new Thread() {
					public void run() {
                    	targetWeight = mEditText.getText().toString().trim();
                        if (targetWeight.length() < 2
                                || targetWeight.trim()
                                        .length() > 3) {
                            mHandler.sendEmptyMessage(10001);
                            return;
                        }

                        int weight = Integer.parseInt(targetWeight);
                        if (weight > 120 || weight < 30) {
                            mHandler.sendEmptyMessage(10002);
                            return;
                        }
                        targetWeight=String.valueOf(weight);

                        String URL = "http://"
                                + PreferencesUtils.getString(
                                        SettingTargetWeightActivity.this,
                                        SharedPreferredKey.SERVER_NAME, null)
                                + "/openClientApi.do?action=settargetvalue&userid="
                                + DataSyn.getInstance().forSetting()
                                + "&value="
                                + targetWeight
                                + "&type=target_weight";
                        Log.d("targetweight", "ul  " + URL);
                        SimpleNet.simpleGet(URL, mHandler, mContext);
                    };
                }.start();
            }
        });
    }

	public void UiView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
                BaseToast("体重同步成功！");
				PreferencesUtils.putString(SettingTargetWeightActivity.this, SharedPreferredKey.TARGET_WEIGHT, targetWeight);
				SettingTargetWeightActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}
	//去除体重中的首位0
	public String moveFirstZero(String startStr){
		if(startStr.substring(0, 1).equals("0"))
		startStr = startStr.substring(1);
		return startStr;
	}	

}
