package cmccsi.mhealth.app.sports.appversion;

import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.activity.MainFragmentActivity2;
import cmccsi.mhealth.app.sports.activity.PreLoadForgetPwdActivity;
import cmccsi.mhealth.app.sports.activity.WebViewActivity;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.service.StepService_GPS;
import cmccsi.mhealth.app.sports.service.UpdateVersionService;
import cmccsi.mhealth.app.sports.R;

public class PreLoadLoginActivity extends BaseActivity implements
		OnClickListener {
	private static String TAG = "LoginActivity";

	private LinearLayout mLinearLayoutPhone;
	private RelativeLayout mLinearLayoutPassword;
	private TextView mTextViewTitle;
	private EditText mEditTelphone, mEditPassword;
	private CheckBox eCheckBoxRemember;
	private CheckBox mCheckBoxAuto;

	private TextView mTextViewForgetPassword;
	// private Editor mSharedata;

	private String mPhoneNum;
	private String mPassword;

    /**
     * 0 为默认 即账号不相等或者为第一次登录 1为账户密码与所输相同
     */
	private int mFlagLogin = 0;
    /**
     * 网络状态码 0： 无网络， 1：wifi 2:GPS/3G
     */
	private int internet;

	private ImageView mBtnOk;

	private Boolean mCheckdRemember = false;
	private Boolean mCheckdAuto = false;

	private TextView mTextViewRegister;

	private SharedPreferences info;
	
	private String mOldUserUid="";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取得活动的preferences对象.
		info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		setContentView(R.layout.activity_login);

		initViews();
		checkNetworkState();
		
		boolean checkAuto = false;
        boolean checkRem = false;
		if (info != null) {
            mPhoneNum = info.getString(SharedPreferredKey.LOGIN_NAME, null); // 拿到电话号码
            mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
            checkAuto = info.getBoolean("checkdAuto", false); // 自动登录
            checkRem = info.getBoolean("checkdRemember", false); // 记住密码
		}

		if (mPhoneNum != null && mPassword != null) {

			mEditTelphone.setText(mPhoneNum.toString().trim());
			mCheckBoxAuto.setChecked(mCheckdAuto);
            
            if (checkRem && checkAuto) {
                // 自动登陆
				Intent intent = new Intent();
				PreferencesUtils.putBoolean(PreLoadLoginActivity.this, "newLogin", true);
				Bundle bundl = new Bundle();
				bundl.putBoolean("isLogin", true);
				intentActivity(PreLoadLoginActivity.this, MainFragmentActivity2.class, bundl, true);
				startActivity(intent);
				PreLoadLoginActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.silde_out_right);
			} else if (checkRem && !checkAuto) {// 判断是否记住密码
				Logger.i(TAG, "telephone=" + mPhoneNum + "pwd=" + mPassword);
				mEditTelphone.setText(mPhoneNum.toString().trim());
				eCheckBoxRemember.setChecked(true);
				mEditPassword.setText(mPassword.toString().trim());
				mEditTelphone.setFocusable(true);
				mEditTelphone.requestFocus();
				onFocusChange(mEditTelphone.isFocusable(), mEditTelphone);
			} 
		} else if(mPhoneNum != null && mPassword == null){
			eCheckBoxRemember.setChecked(false);
			mEditTelphone.setText(mPhoneNum.toString().trim());
			mEditPassword.setText("");
			// 密码为空，获取焦点
            mEditPassword.setFocusable(true);
            mEditPassword.requestFocus();
            onFocusChange(true, mEditPassword);
		} else{
			mEditTelphone.setFocusable(true);
			mEditTelphone.requestFocus();
			onFocusChange(mEditTelphone.isFocusable(), mEditTelphone);
		}
		tipExit();
	}

	void initViews() {
        mTextViewTitle = (TextView) findViewById(R.id.textView_title);
        mTextViewTitle.setText(R.string.app_name);

		mLinearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayout_phone);
		mLinearLayoutPassword = (RelativeLayout) findViewById(R.id.linearLayout_password);
		mEditTelphone = (EditText) findViewById(R.id.edittelphone);
		
		mEditPassword = (EditText) findViewById(R.id.editPassword);
		mEditTelphone.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mLinearLayoutPhone
							.setBackgroundResource(R.drawable.login_incur);
				else
					mLinearLayoutPhone
							.setBackgroundResource(R.drawable.login_input);
			}
		});

		mEditPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mLinearLayoutPassword
							.setBackgroundResource(R.drawable.login_incur);
				else
					mLinearLayoutPassword
							.setBackgroundResource(R.drawable.login_input);
			}
		});

		eCheckBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
		eCheckBoxRemember.setOnClickListener(this);
		mCheckBoxAuto = (CheckBox) findViewById(R.id.checkBoxAuto);
		mCheckBoxAuto.setOnClickListener(this);
		eCheckBoxRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(!isChecked){
					SharedPreferences info = getSharedPreferences(
							SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
					Editor editorShare = info.edit();
					if(info.getString(SharedPreferredKey.PASSWORD, null) != null){
						editorShare.remove(SharedPreferredKey.PASSWORD);
						editorShare.commit();
					}
				}
			}
		});
		
		
		mBtnOk = (ImageView) findViewById(R.id.bt_OK);
		// bar = (ProgressBar) findViewById(R.id.progressBar_login);

		mBtnOk.setOnClickListener(this);

        // 设置手机号改变的监听事件 -->清空密码框密码
		if (mEditTelphone.getText().toString().trim() != null) {
			mEditTelphone.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (mPassword != null) {
                        // 当记住密码时，更改账号同时清除密码
						mEditPassword.setText(null);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
		}

		mTextViewForgetPassword = findView(R.id.textView_forgetpwd);
		mTextViewForgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTextViewForgetPassword.setOnClickListener(this);

		mTextViewRegister = findView(R.id.textView_register);
		mTextViewRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTextViewRegister.setOnClickListener(this);
	}

	private void checkNetworkState() {
		internet = NetworkTool.getNetworkState(PreLoadLoginActivity.this);
		Logger.i(TAG, "internet=" + internet);
		if (internet == 0) {
			messagesManager(Constants.MESSAGE_INTERNET_NONE);
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("提示");
            // 设置自定义对话框的样式
            dlg.setPositiveButton("设置网络", //
                    new DialogInterface.OnClickListener() // 设置事件监听
					{
						public void onClick(DialogInterface dialog,
								int whichButton) {
                            // 跳转到网络设置
							Intent wifiSettingsIntent = new Intent(
									"android.settings.WIFI_SETTINGS");
							startActivity(wifiSettingsIntent);
						}
					})
                    .setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
                            }).create().show();// 创建
		}
	}



    /**
     * HttpClient get请求方式访问网络.
     * 
     * @param strCreatTime
     *            请求的时间
     * @param phoneNum
     *            电话号码
     * @param password
     *            密码
     * @return
     * @throws Exception
     */
	public void getPedometorInfo(String strCreatTime, String phoneNum,
			String password) {// http://10.2.48.66:8000/accounts/13810411683/apps/pedometer?psw=wxf&date=20121002
		mOldUserUid=PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
		int res = DataSyn.getInstance().loginAuth(phoneNum, password, this);
		Logger.i(TAG, "res == " + res);
		if (res == 0) {
			loginSuccess(phoneNum, password);
			new Thread(new InitAccountRunnable()).start();
		} else if (res == -1) {
            // 给Handle发消息
			dismiss();
			messagesManager(Constants.MESSAGE_INTERNET_ERROR);
		} else if (res == 1) {
			dismiss();
			messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
		} else if (res == 2) {
            // 给Handle发消息
			dismiss();
			messagesManager(Constants.MESSAGE_PASSWORD_ERROE);
		} else if (res == 3) {
            // 用戶未激活
			// dismiss();
			dismiss();
			Editor editorShare = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
					.edit();
            /* 登录成功并且首次安装则清楚数据保存登录信息 */

			// 储存账户信息
			editorShare.putString(SharedPreferredKey.LOGIN_NAME, phoneNum);
			if (eCheckBoxRemember.isChecked()) {
				mCheckdRemember = true;
				editorShare.putString(SharedPreferredKey.PASSWORD, password);
			} else {
				mCheckdRemember = false;
			}
			mCheckdAuto = mCheckBoxAuto.isChecked();
			editorShare.putBoolean("checkdAuto", mCheckdAuto);
			editorShare.putBoolean("checkdRemember", mCheckdRemember);
			Logger.i(TAG, "---getPedometorInfo mPhoneNum " + phoneNum);
	        Logger.i(TAG, "---getPedometorInfo mPassword " + password);
			editorShare.commit();

			messagesManager(Constants.MESSAGE_NOT_ACTIVITY);
			String selectedserver = PreferencesUtils.getString(this,
					SharedPreferredKey.SERVER_NAME, null);
			// String selectedserver = "111.11.29.83:8099/data_new";
			String url = "http://" + selectedserver
					+ "/account.do?action=PhoneActive&userPhone=" + phoneNum
					+ "&password=" + password + "&" + new Random().nextInt();
			Log.i("gengqi", url);
			Intent intent = new Intent();
			intent.putExtra("UserInfo", url);
            intent.putExtra("title", "用户激活");
			intent.setClass(getApplicationContext(), WebViewActivity.class);
			startActivityForResult(intent, 200);
			// intentActivity(LoginActivity.this, WebViewActivity.class,
			// bundle);
		} else if (res == 4) {
            // 没有账号
			dismiss();
			Logger.i(TAG, "account didnt exist in this server,trying next");
		} else if (res == 5) {
            // 密码错误
			dismiss();
			messagesManager(Constants.MESSAGE_PASSWORD_ERROE);
		} else if (res == 6) {
            // 网络找不到
			dismiss();
			messagesManager(Constants.MESSAGE_INTERNET_NONE);
		} else if (res == 7) {
            // 手机号或密码错误
			dismiss();
			messagesManager(Constants.MESSAGE_PHONE_PASSWORD_EXCEPTION);
		} else if (res == 8) {
            // 获取服务器列表失败
			dismiss();
			messagesManager(Constants.MESSAGE_GET_SERVERLIST_FAILED);
		} else {
            // 网络找不到
			dismiss();
			messagesManager(Constants.MESSAGE_LOGIN_FALSE);
		}
		if (res != 0) {

			Editor infoEditor = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
					.edit();
			infoEditor.putInt("INSTALL", 0);
			infoEditor.commit();
		}
		mBtnOk.setClickable(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 200) {
			SharedPreferences info = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, 0);
			if (info != null) {
                mPhoneNum = info.getString(SharedPreferredKey.LOGIN_NAME, null); // 拿到电话号码
                mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
			}
			loginSuccess(mPhoneNum, mPassword);
		}
		if (resultCode == Activity.RESULT_OK && requestCode == 300) {
		}
	}

    /**
     * 登陆成功
     * 
     * @param phoneNum
     * @param password
     */
	private void loginSuccess(String phoneNum, String password) {
		
		Logger.i(TAG, "--- loginSuccess");

		Editor editorShare = getSharedPreferences(
				SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();

        /* 登录成功并且首次安装则清楚数据保存登录信息 */
		SharedPreferences info = getSharedPreferences(
				SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		int install = info.getInt("INSTALL", -1);
		String strServerIP = info.getString(SharedPreferredKey.SERVER_NAME,
				null);
		String strServerVersion = info.getString(
				SharedPreferredKey.SERVER_VERSION, "2");
		int verCode = Config.getVerCode(this);
		if (install != verCode) {
			editorShare.putInt("INSTALL", verCode);
			editorShare.putString("GROUP_UPDATE_TIME", null);
			editorShare.putString("GROUP_UPDATE_VERSION", null);
			editorShare.putString("INPK_UPDATE_TIME_RACE", null);
		}
		// 储存账户信息
		editorShare.putString(SharedPreferredKey.LOGIN_NAME, phoneNum);
		if (eCheckBoxRemember.isChecked()) {
			mCheckdRemember = true;
			editorShare.putString(SharedPreferredKey.PASSWORD, password);
		} else {
			mCheckdRemember = false;
		}
		mCheckdAuto = mCheckBoxAuto.isChecked();
		editorShare.putBoolean("checkdAuto", mCheckdAuto);
		editorShare.putBoolean("checkdRemember", mCheckdRemember);
		Logger.i(TAG, "---loginSuccess mPhoneNum " + phoneNum);
        Logger.i(TAG, "---loginSuccess mPassword " + password);
		
        // 首次登录或更换账号登录 先清空sharedpreference
		if (mFlagLogin == 0) {
			int newSetting = info.getInt("NEW_SETTING", 0);
			editorShare.putInt("NEW_SETTING", newSetting);
			editorShare.putString(SharedPreferredKey.SERVER_NAME, strServerIP);
			editorShare.putString(SharedPreferredKey.SERVER_VERSION,
					strServerVersion);
			editorShare.putBoolean("BSHOWGUID", false);

		}
		editorShare.putInt("fromLogin", mFlagLogin);
		editorShare.commit();

		Intent intent = new Intent(this, UpdateVersionService.class);
		startService(intent);
	}

	@Override
	public synchronized void onClick(View view) {
		switch (view.getId()) {
		case R.id.checkBoxRemember:
            // 取消记住密码则取消自动登录
			if (!eCheckBoxRemember.isChecked()) {
				mCheckBoxAuto.setChecked(false);
			}
			break;
		case R.id.checkBoxAuto:
            // 选择自动登录则选择记住密码则
			if (mCheckBoxAuto.isChecked()) {
				eCheckBoxRemember.setChecked(true);
			}
			break;
		case R.id.textView_forgetpwd:
			Intent intent = new Intent(this, PreLoadForgetPwdActivity.class);
			startActivity(intent);
			break;
		case R.id.textView_register:

			Intent intent1 = new Intent();
			intent1.setClass(getApplicationContext(), RegisteActivity.class);
			startActivityForResult(intent1, 300);
			break;
		case R.id.bt_OK:
//			final String telephone = Common.getNumber(mEditTelphone.getText()
//					.toString().trim());
			final String telephone = (mEditTelphone.getText().toString().trim());
			final String pwd = mEditPassword.getText().toString().trim();

			SharedPreferences sp = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, 0);
			Editor edit = sp.edit();
			edit.remove("selectedserver");
			edit.commit();
			DataSyn.strHttpURL = "";
			DataSyn.avatarHttpURL = "";

			if (TextUtils.isEmpty(telephone)) {
				messagesManager(Constants.MESSAGE_PHONE_ISEMPTY);
			} else if (TextUtils.isEmpty(pwd)) {
				messagesManager(Constants.MESSAGE_PASSWORD_ISEMPTY);
			} else if (telephone.length() > 30) {
				messagesManager(Constants.MESSAGE_PHONE_ISTOOLEN);
			} else if (pwd.length() > 50) {
				messagesManager(Constants.MESSAGE_PASSWORD_ISTOOLEN);
			} else if (telephone.equals(mPhoneNum) && pwd.equals(mPassword)
                    && mCheckdRemember) { // 自动登录
                // 换账号了 清除旧账号的私人数据
				Editor editorShare = getSharedPreferences(
						SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
						.edit();
                // 有网络的本地登录
				editorShare.putString(SharedPreferredKey.LOGIN_NAME, telephone);
				editorShare.putString(SharedPreferredKey.PASSWORD, pwd);

				if (eCheckBoxRemember.isChecked()) {
					mCheckdRemember = true;
				} else {
					mCheckdRemember = false;
				}
				if (mCheckBoxAuto.isChecked()) {
					mCheckdAuto = true;
				} else {
					mCheckdAuto = false;
				}
				editorShare.putBoolean("checkdAuto", mCheckdAuto);
				editorShare.putBoolean("checkdRemember", mCheckdRemember);
				editorShare.commit();
                // 匹配成功，直接本地登录

				Bundle bundl = new Bundle();
				bundl.putBoolean("isLogin", true);
				intentActivity(PreLoadLoginActivity.this, MainFragmentActivity2.class, bundl, true);
            } else { // 非自动登录
				if (NetworkTool.getNetworkState(this) != 0) {
                    // 有网在线登录
                    // 登录

					Editor edit2 = sp.edit();
					edit2.putString(SharedPreferredKey.SERVER_NAME, "");
					edit2.commit();
					mBtnOk.setClickable(false);
                    showProgressDialog(getResources().getString(R.string.text_wait), this);
					new Thread() {
						public void run() {
                            // login 进入判断 0账号不等更新 1账号相等不更新
							if (mPhoneNum == null || mPassword == null) {
                                // sharedprefences里去数据，如果都为null则认为是首次登录
								mFlagLogin = 0;
							} else if (mPhoneNum.equals(telephone)
									&& mPassword.equals(pwd)) {
                                // 账号相等
								mFlagLogin = 1;
							} else {
                                // 账号不等
								mFlagLogin = 0;
							}
                            // 有網絡直接登录
							String time = Common.getDateAsYYYYMMDD(new Date()
									.getTime());
							try {
								getPedometorInfo(time, telephone, pwd);
							} catch (Exception e) {
								mBtnOk.setClickable(true);
								messagesManager(Constants.MESSAGE_LOGIN_FALSE);
								e.printStackTrace();
							}
						};
					}.start();
				} else {
                    // 无网络，可以登录进入查看上一次的数据，但是不可以更新
                    Logger.i(TAG, "没有网络" + telephone + "::" + mPhoneNum + "::"
							+ pwd + "::" + mPassword);

					if (telephone.equals(mPhoneNum) && pwd.equals(mPassword)) {
						messagesManager(Constants.MESSAGE_INTERNET_NONE);
                        // 跳转主界面

						Bundle bundl = new Bundle();
						bundl.putBoolean("isLogin", true);
						intentActivity(PreLoadLoginActivity.this, MainFragmentActivity2.class, bundl, true);
					} else {
						Logger.i(TAG, "NOInternet_login=false");
						messagesManager(Constants.MESSAGE_INTERNET_NONE);
					}
				}
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Editor infoEditor = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
					.edit();
			infoEditor.putInt("INSTALL", 0);
			infoEditor.commit();
			this.finish();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.silde_out_right);
			return true;
		}
		return false;
	}

    
	private String userUid;
	private static final int WHAT_LOAD_FINISHED = 1001;
	/**
	 * 初始化user
	 * 
	 * @version 1.0.0
	 * @author Xiao
	 */
	private class InitAccountRunnable implements Runnable {

		@Override
		public void run() {
			Context context = PreLoadLoginActivity.this;
			userUid = PreferencesUtils.getString(context, SharedPreferredKey.USERUID, "");
			Logger.i(TAG, "--------------userUid--------------: " + userUid + " " +mOldUserUid);
			String versionName = getVersion();
			if ( StringUtils.isNotBlank(mOldUserUid) && !mOldUserUid.toString().equals(userUid.toString())) {
				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				Editor editorShare = info.edit();
				editorShare.clear();
				editorShare.commit();
				clearServise();
				Common.clearDatabases(PreLoadLoginActivity.this);
			}
			//版本升级删除数据库数据不删除运动数据
			if (!PreferencesUtils.getString(context, SharedPreferredKey.APPVERNAME, "").equals(versionName)) {
				PreferencesUtils.putString(context, SharedPreferredKey.APPVERNAME, versionName);
				clearServise();
				Common.clearDatabasesWithoutPedo(PreLoadLoginActivity.this);
			}

			DataSyn dataSyn = DataSyn.getInstance();

			dataSyn.loadServerInfo(context);
			dataSyn.loadUserInfoNotInThread(context);
			// dataSyn.loadClubId(context);
			DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
			int resout = dataSyn.getDeviceListData(deviceListInfo);
//			if (resout == 0) {
				mHandler.sendEmptyMessage(WHAT_LOAD_FINISHED);
//			}
		}
	}
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_LOAD_FINISHED:
				restoreDeviceInfo();
				dismiss();
				Bundle bundl = new Bundle();
				bundl.putBoolean("isLogin", true);
				intentActivity(PreLoadLoginActivity.this, MainFragmentActivity2.class, bundl, true);
			default:
				break;
			}
		};
	};
	/**
	 * 保存设备信息到 sharedpreference中
	 */
	private void restoreDeviceInfo() {
		String Address = "";
		DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
		DeviceInfo deviceInfo_now = null;
		boolean have_bracelet_device = false;
		System.out.println("---deviceListInfo.datavalue----"+deviceListInfo.datavalue.size());
		for (DeviceInfo deviceInfo : deviceListInfo.datavalue) {
			// 判断是否有手环设备
			if (Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				have_bracelet_device = true;
				Address = deviceInfo.deviceSerial.substring(2);
				if (deviceInfo != null) {
					Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					editor.putString(SharedPreferredKey.DEVICE_TOKEN, deviceInfo.deviceToken);
					editor.putString(SharedPreferredKey.DEVICE_NUMBER, deviceInfo.deviceNumber);
					editor.putString(SharedPreferredKey.DEVICE_VERSION, deviceInfo.deviceVersion);
					editor.commit();
				}
			}
			// 获取当前绑定设备
			if (Integer.valueOf(deviceInfo.isUsed) == 1) {
				deviceInfo_now = deviceInfo;
			}
		}
		PreferencesUtils.putBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, have_bracelet_device);
		PreferencesUtils.putString(this, SharedPreferredKey.DEVICE_ADDRESS, Address);
		if (deviceInfo_now != null) {
			Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(SharedPreferredKey.DEVICE_ID, deviceInfo_now.deviceSerial);
			System.out.println("---------DEVICE_ID------------"+deviceInfo_now.deviceSerial);
			editor.putString(SharedPreferredKey.DEVICE_NAME, deviceInfo_now.productName);
			editor.putString(SharedPreferredKey.DEVICE_MODEL, deviceInfo_now.productPara);
			editor.putInt(SharedPreferredKey.DEVICE_TYPE,
					Common.getDeviceType(deviceInfo_now.deviceSerial, deviceInfo_now.productPara));
			editor.commit();
		} else {
		}
	}
	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private void clearServise() {
		if (StepService_GPS.isRunning) {

			stopService(new Intent().setClass(this, StepService_GPS.class));
		}
		if (Common.isStepServiceRunning(this)) {
			Logger.d("cjz", "Stop step service");
			Intent it = new Intent(Config.PHONESTEP_STOP_NOSAVE_ACTION);
			sendBroadcast(it);
		}
	}
}