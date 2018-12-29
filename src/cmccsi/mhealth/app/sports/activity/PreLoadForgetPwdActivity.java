package cmccsi.mhealth.app.sports.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.appversion.RegisteActivity;
import cmccsi.mhealth.app.sports.appversion.WebServiceManage;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.UpdatePasswordInfo;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;

//**
@SuppressLint("HandlerLeak")
public class PreLoadForgetPwdActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ForgetPwdActivity";

	// private LinearLayout mLinearLayoutNewPwd;
	private EditText mEditTextPhoneNum;
	private EditText userNameEditText;
	// private EditText mEditTextPassword;
	// private EditText mEditTextPasswordAgain;
	private EditText mEditTextTempCode;
	private TextView mTextViewTitleBar;

	private Button mButtonSend;
	private Button mButtonOk;
	private String netTokenMD5;

	// private Button mButtonSetPwd;

	private String mPhoneNum;
	private String userName;
	private String mStatus;
	// ** private String mResultStatus;
	private String mTempCode;
	private String mTempCodeForIntent;
	// ** private String mLimitTime;
	private String mNewPwd;

	private int mResultNet;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 验证成功时执行
			case 0:
				Intent intent = new Intent();
				intent.setClass(PreLoadForgetPwdActivity.this, PreLoadResetPwdActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("mTempCode", mTempCodeForIntent);
				bundle.putString("mPhoneNum", mPhoneNum);
				intent.putExtra("fpa_infos", bundle);

				startActivity(intent);
				PreLoadForgetPwdActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.silde_out_left);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgetpwd);
		SharedPreferences data = getSharedPreferences(SharedPreferredKey.SHARED_NAME, MODE_PRIVATE);
		mPhoneNum = data.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mEditTextPhoneNum = findView(R.id.edittext_phonenum);
		if (mPhoneNum != null)
			mEditTextPhoneNum.setText(mPhoneNum);
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		// mLinearLayoutNewPwd = findView(R.id.linearlayout_newpwd);
		// mLinearLayoutNewPwd.setVisibility(View.INVISIBLE);
		userNameEditText = findView(R.id.edittext_username);
		mEditTextPhoneNum = findView(R.id.edittext_phonenum);
		mEditTextTempCode = findView(R.id.edittext_tempcode);
		// mEditTextPassword = findView(R.id.newpwd_edit_setting);
		// mEditTextPasswordAgain = findView(R.id.newpwd_ag_edit_setting);
		mButtonSend = findView(R.id.button_send);
		mButtonOk = findView(R.id.button_ok);
		// mButtonSetPwd = findView(R.id.UpdatePwd_btn_setting);

		mButtonSend.setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		// mButtonSetPwd.setOnClickListener(this);

		mTextViewTitleBar = (TextView) findViewById(R.id.textView_title);
		mTextViewTitleBar.setText("忘记密码");
		ImageButton back = (ImageButton) findViewById(R.id.button_input_bg_back);
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
			}
		});
		back.setVisibility(View.VISIBLE);
	}

	private void sendPwdMessage() {
		if (mPhoneNum == null || "".equals(mPhoneNum)) {
			Toast.makeText(this, "手机号不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if (userName == null || "".equals(userName)) {
			Toast.makeText(this, "账号不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					// TODO Auto-generated method stub
					super.run();
					HashMap<String, String> arg = new HashMap<String, String>();
					arg.put("uid", userName);
					arg.put("phoneNumber", mPhoneNum);
					String json1 = WebServiceManage.get(arg, 2);
					System.out.println("-----------发送密码重置短信验证码+++:" + json1);
					if (null == json1) {
						timeHandler.sendEmptyMessage(error1);
						return;
					}
					JSONObject jsonObject = new JSONObject(json1);
					String s = jsonObject.getString("status");
					if (s == null || "FAILURE".equals(s)) {
						Message message = new Message();
						message.what = error5;
						message.obj = jsonObject.getString("message");
						timeHandler.sendMessage(message);
						return;
					} else if ("SUCCESS".equals(s)) {
						netTokenMD5 = jsonObject.getString("tokenMD5");
						timeHandler.sendEmptyMessage(timeHandlerInt);
					} else {
						timeHandler.sendEmptyMessage(error1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public synchronized void onClick(View view) {
		switch (view.getId()) {
		// 密码重设部分
		// case R.id.UpdatePwd_btn_setting:
		// PasswordResetMessage();
		// break;
		case R.id.button_send:
			mEditTextPhoneNum.invalidate();
			mPhoneNum = mEditTextPhoneNum.getText().toString();
			Logger.d(TAG, "mEditTextPhoneNum==" + mPhoneNum);
			userName = userNameEditText.getText().toString().trim();
			sendPwdMessage();
			break;
		case R.id.button_ok:
			// 判断短信验证码
			verifyCodeAndJump();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		initViews();
		mEditTextPhoneNum.invalidate();
		mPhoneNum = mEditTextPhoneNum.getText().toString().trim();
		mTempCodeForIntent = mEditTextTempCode.getText().toString().trim();
		super.onResume();
	}

	private void verifyCodeAndJump() {
		mTempCodeForIntent = mEditTextTempCode.getText().toString().trim();
		if (mPhoneNum == null || "".equals(mPhoneNum)) {
			Toast.makeText(PreLoadForgetPwdActivity.this, "请先输入手机号并获取验证码", Toast.LENGTH_LONG).show();
			return;
		}
		if (null == mTempCodeForIntent || "".equals(mTempCodeForIntent)) {
			Toast.makeText(PreLoadForgetPwdActivity.this, "请先输入验证码", Toast.LENGTH_LONG).show();
			return;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					HashMap<String, String> arg = new HashMap<String, String>();
					arg.put("uid", userName);
					arg.put("phoneNumber", mPhoneNum);
					arg.put("verifyCode", mTempCodeForIntent);
					arg.put("type", "2");
					String json1 = WebServiceManage.get(arg, 3);
					System.out.println("----验证短信验证码---------"+json1);
					if (null == json1) {
						timeHandler.sendEmptyMessage(error2);
						return;
					}
					JSONObject jsonObject = new JSONObject(json1);
					String s = jsonObject.getString("status");
					if (s == null || s.equals("FAILURE")) {
						Message message = new Message();
						message.what = error3;
						message.obj = jsonObject.getString("message");
						timeHandler.sendMessage(message);
						return;
					} else if (s.equals("SUCCESS")) {
						Intent intent = new Intent(PreLoadForgetPwdActivity.this, PreLoadResetPwdActivity.class);
						intent.putExtra("TokenMD5", netTokenMD5);
						intent.putExtra("Uid", userName);
						startActivity(intent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static final int timeHandlerInt = 10000;
	private static final int error1 = 10001;
	private static final int error2 = 10002;
	private static final int error3 = 10003;
	private static final int error4 = 10004;
	private static final int ok = 10005;
	private static final int error5 = 10006;
	private int timeCount = 60;
	private Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case timeHandlerInt:
				if (--timeCount > 0) {
					mButtonSend.setText(timeCount + "秒后重试");
					mButtonSend.setClickable(false);
					mButtonSend.setBackgroundColor(Color.rgb(193, 222, 131));
					timeHandler.sendEmptyMessageDelayed(timeHandlerInt, 1000);
				} else {
					mButtonSend.setText("获取验证码");
					mButtonSend.setClickable(true);
					mButtonSend.setBackgroundColor(Color.rgb(212, 207, 43));
					timeCount = 60;
				}
				break;
			case error1:
				Toast.makeText(PreLoadForgetPwdActivity.this, "获取验证码失败，请稍后再试", Toast.LENGTH_LONG).show();
				break;
			case error2:
				ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
				break;
			case error3:
				if (null == msg.obj) {
					Toast.makeText(PreLoadForgetPwdActivity.this, "验证码验证失败", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(PreLoadForgetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			case error4:
				Toast.makeText(PreLoadForgetPwdActivity.this, "手机号和用户名不匹配", Toast.LENGTH_LONG).show();
				break;
			case error5:
				if (null == msg.obj) {
					Toast.makeText(PreLoadForgetPwdActivity.this, "获取验证码错误", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(PreLoadForgetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
}
