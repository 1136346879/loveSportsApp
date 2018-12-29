package cmccsi.mhealth.app.sports.activity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.appversion.PreLoadLoginActivity;
import cmccsi.mhealth.app.sports.appversion.WebServiceManage;
import cmccsi.mhealth.app.sports.basic.AuthUtl;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.UpdatePasswordInfo;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.TestBase;
import cmccsi.mhealth.app.sports.R;
//**import android.widget.TextView;
/**
 * 重新设定密码
 * @author zy
 *
 */
public class PreLoadResetPwdActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ResetPwdActivity";
	private String mPhoneNum;
	private String mNewPwd;
	
	private String mStatus;
	private String mTempCode;
	
	private EditText mEditTextPassword;
	private EditText mEditTextPasswordAgain;
	
	private Button forget_UpdatePwd_btn_setting;//确认按钮
	private ImageButton button_input_bg_back;//返回按钮
	
	protected String mResultStatus;
	private String netTokenMD5;
	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpwd);
		Intent intent = getIntent();
		netTokenMD5 = intent.getStringExtra("TokenMD5");
		uid = intent.getStringExtra("Uid");
		System.out.println("--------netTokenMD5------"+netTokenMD5);
		System.out.println("--------uid------"+uid);
	}
	//初始化
	@SuppressWarnings("deprecation")
	private void initviews() {
		TextView mTextViewTitleBar = (TextView) findViewById(R.id.textView_title);
		mTextViewTitleBar.setText("密码重置");
		mEditTextPassword = (EditText) findViewById(R.id.newpwd_edit_setting);
		mEditTextPasswordAgain = (EditText) findViewById(R.id.newpwd_ag_edit_setting);

		forget_UpdatePwd_btn_setting = (Button) findViewById(R.id.forget_UpdatePwd_btn_setting);
		button_input_bg_back = (ImageButton) findViewById(R.id.button_input_bg_back);
		button_input_bg_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		forget_UpdatePwd_btn_setting.setOnClickListener(this);
		button_input_bg_back.setOnClickListener(this);
		button_input_bg_back.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onResume() {
		initviews();
		super.onResume();
	}
	//重新设定密码的方法
	private void PasswordResetMessage() {
		String pwd = mEditTextPassword.getText().toString();
		String pwdAgain = mEditTextPasswordAgain.getText().toString();

		if (pwd == null || pwd.equals("")) {
//			BaseToast("密码不能为空");
			Toast.makeText(PreLoadResetPwdActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if (!pwd.equals(pwdAgain)) {
//			BaseToast("两次密码不一致");
			Toast.makeText(PreLoadResetPwdActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
			return;
		}
		if(pwd.length()<6 || pwd.length() > 18){
//			BaseToast("输入的密码最小六位，最大十八位");
			Toast.makeText(PreLoadResetPwdActivity.this, "输入的密码最小六位，最大十八位", Toast.LENGTH_LONG).show();
			return;
		}
		mNewPwd = pwd;
		new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					mNewPwd =TestBase.calculateUserPassword(AuthUtl.HASH_METHOD_SHA, mNewPwd);
					System.out.println("------加密串 修改-----"+mNewPwd);
					HashMap<String, String> arg = new HashMap<String, String>();
					arg.put("uid", uid);
					arg.put("userPwd", mNewPwd);
					arg.put("tokenMD5", netTokenMD5);
					String json1 = WebServiceManage.post(arg, 1);
					System.out.println("------修改密码--resetPassword-----"+json1);
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
						Intent intent = new Intent(PreLoadResetPwdActivity.this, PreLoadLoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						if (BaseActivity.allActivity.size()>2) {
							BaseActivity.allActivity.get(allActivity.size()-2).finish();
						}
						PreLoadResetPwdActivity.this.finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}.start();
//		new AsyncTask<Integer, Void, Integer>() {
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//			}
//
//			protected Integer doInBackground(Integer... params) {
//				UpdatePasswordInfo reqData = new UpdatePasswordInfo();
//				int result = DataSyn.getInstance().passwordReset(mPhoneNum, mTempCode, mNewPwd, reqData);
//				if (result == 0) {
//					mStatus = reqData.status;
//					mResultStatus = reqData.reason;
//					Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
//					sharedata.putString(SharedPreferredKey.PASSWORD, mNewPwd);
//					sharedata.commit();
//				}
//				return result;
//			}
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				switch (result) {
//				case 0:
//					if (mStatus != null && mStatus.equals("SUCCESS")) {
//						BaseToast("密码修改成功");
//					} else
//						BaseToast("密码修改失败,请重新获取验证码");
//					finish();
//					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
//					break;
//				case 1:
//					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
//					break;
//				case 2:
//					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
//					break;
//				case -1:
//					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
//					break;
//				default:
//					break;
//				}
//			}
//		}.execute();
	}
	//点击响应
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_UpdatePwd_btn_setting:
			//更新密码
			PasswordResetMessage();
			break;
		case R.id.button_input_bg_back:
			//返回主页
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			break;
		}
		
	}
	private static final int error1 = 10001;
	private static final int error2 = 10002;
	private static final int error3 = 10003;
	private static final int error4 = 10004;
	private static final int error5 = 10006;
	private Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case error1:
				Toast.makeText(PreLoadResetPwdActivity.this, "获取验证码失败，请稍后再试", Toast.LENGTH_LONG).show();
				break;
			case error2:
				ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
				break;
			case error3:
				if (null == msg.obj) {
					Toast.makeText(PreLoadResetPwdActivity.this, "验证码验证失败", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(PreLoadResetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			case error4:
				Toast.makeText(PreLoadResetPwdActivity.this, "手机号和用户名不匹配", Toast.LENGTH_LONG).show();
				break;
			case error5:
				if (null == msg.obj) {
					Toast.makeText(PreLoadResetPwdActivity.this, "获取验证码错误", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(PreLoadResetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
			default:
				break;
			}
		}
	};
}
