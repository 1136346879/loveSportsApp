package cmccsi.mhealth.app.sports.tabhost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.activity.OpinionListActivity;
import cmccsi.mhealth.app.sports.activity.PreLoadAPKUpdateProgressActivity;
import cmccsi.mhealth.app.sports.activity.SettingAboutActivity;
import cmccsi.mhealth.app.sports.activity.SettingArea;
import cmccsi.mhealth.app.sports.activity.SettingTargetStepActivity;
import cmccsi.mhealth.app.sports.activity.SettingTargetWeightActivity;
import cmccsi.mhealth.app.sports.activity.WebViewActivity;
import cmccsi.mhealth.app.sports.activity.avatar.CropImageActivity;
import cmccsi.mhealth.app.sports.activity.avatar.CropUtil;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.UpdatePasswordInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Encrypt;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.UploadUtil;
import cmccsi.mhealth.app.sports.common.UploadUtil.OnUploadProcessListener;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.ecg.activity.BraceletSettingActivity;
import cmccsi.mhealth.app.sports.ecg.activity.DeviceSettingActivityTest;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.net.UpdateSoftWareTools;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.pedo.TgbleManagerNeuro;
import cmccsi.mhealth.app.sports.pedo.UploadManager;
import cmccsi.mhealth.app.sports.phonesteplib.StepController;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.service.StepService_GPS;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.RoundAngleImageView;
import cmccsi.mhealth.app.sports.view.WiperSwitch;
import cmccsi.mhealth.app.sports.view.WiperSwitch.OnChangedListener;
import cmccsi.mhealth.app.sports.R;
//import cn.jpush.android.api.JPushInterface;















import com.cmcc.bracelet.lsjx.libs.DingdangSettingActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@SuppressLint("ValidFragment")
public class SettingFragment extends BaseFragment implements OnClickListener, OnUploadProcessListener {

	private static final String ImgUrl = Environment.getExternalStorageDirectory() + "/ishang_image";// +MD5.getMD5(url));

	protected static final String TAG = "SettingActivity";
	private int mRelativeLayoutIds[] = { R.id.setting_sport_target,
	/* R.id.setting_face, */R.id.setting_aboutIS, R.id.setting_update, R.id.setting_feedback,
			R.id.setting_exit, R.id.rlayout_setting_help,R.id.rl_setting_clear,
			R.id.rlayout_setting_device,
			/* R.id.setting_height, */R.id.setting_target_weight, R.id.rl_step_switch, R.id.rl_bracelet_setting };

	private TextView mTextViewSettingPhoneNum, mTextViewSettingSportTargetNum, mTextViewSettingNyGroup;
	private TextView mTextViewSettingWeight, mTextViewSettingStepLen;

	private TextView mTextViewVersion;
	private String mPhoneNum;
	private String mMembername;
	private RelativeLayout mRelativeLayout;
	private RelativeLayout mWeightBar;
	private RelativeLayout mHeightBar;
	private RelativeLayout mBindDeviceBar;
	private RelativeLayout mRelativeLayoutArea;
	private RelativeLayout mRl_step_switch;
	private RelativeLayout mrl_bracelet_setting;
	private RelativeLayout setting_problem;
	private TextView mTvAreaDetail;
	private TextView mTvArea;

	private TextView mTextViewNewPwd;

	private RoundAngleImageView mImageViewAvatar;
	// **private Bitmap mImageBitmap;

	private int mVerCode = 0;// Config.getVerCode(this);
	private String mVerName = "";// Config.getVerName(this);
	private CustomProgressDialog mProgressDialog;
	private StepService_GPS mService;
	// TODO
	private static String requestURL;
	// **private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	private final String[] items = new String[] { "选择本地图片", "拍照" };
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 3000;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3001;
	// **private static final int RESULT_REQUEST_CODE = 3002;
	private static final int PHOTO_CROP_REQUEST_CODE = 3003;
	private static final int AREA_SETTING_ACTIVITY_REQUEST_CODE = 3004;
	// **private static final String PATH_IMAGE =
	// Environment.getExternalStorageDirectory() + "/iShang/";

	private static String mFileImagePath;
	private static String mFileImagePathDexed;

	private TextView mTextViewTitle;

	private ImageButton mBack;
	private final int mFlag;

	private TextView mTextViewBirthday;

	private TextView mTextViewWeight;

	private WiperSwitch mSw_startStep;
	private WiperSwitch mSw_bootRun;

	public SettingFragment(int flag) {
		mFlag = flag;
	}

	public SettingFragment() {
		mFlag = -1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, getClass().getSimpleName() + "onCreateView");
		View view = inflater.inflate(R.layout.activity_setting, container, false);
		mRelativeLayout = (RelativeLayout) view.findViewById(R.id.setting_update);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		int deviceType = PreferencesUtils.getInt(getActivity(), SharedPreferredKey.DEVICE_TYPE, -1);
		if (deviceType == DeviceConstants.DEVICE_MOBILE_STEP) {
			mRl_step_switch.setVisibility(View.VISIBLE);
			mrl_bracelet_setting.setVisibility(View.GONE);
		} else if (deviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || deviceType == DeviceConstants.DEVICE_BRACLETE_JW201
				|| deviceType == DeviceConstants.DEVICE_BRACLETE_JW) {
			mRl_step_switch.setVisibility(View.GONE);
			mrl_bracelet_setting.setVisibility(View.VISIBLE);
		} else {
			mRl_step_switch.setVisibility(View.GONE);
			mrl_bracelet_setting.setVisibility(View.GONE);
		}

		//手机计步开关
		if (StepService.isRunning) {
			mSw_startStep.setChecked(true);
		} else {
			mSw_startStep.setChecked(false);
		}
		//开机启动设置按钮
		if(PreferencesUtils.getBoolean(mActivity, SharedPreferredKey.SETTING_BOOT_RUN, true)){
			mSw_bootRun.setChecked(true);
		}else{
			mSw_bootRun.setChecked(false);
		}
	}

	private void setValueFromShare() {
		// 版本号
		PackageManager packageManager = mActivity.getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(mActivity.getPackageName(), 0);
			if (info != null && info.versionName != null) {
				mTextViewVersion.setText("当前版本:" + "  " + info.versionName);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		// String target = sharedPreferences.getString("TARGET", "10000");
		mTextViewSettingSportTargetNum.setText(sharedPreferences.getString(SharedPreferredKey.TARGET_STEP, "10000") + " （步）");
		String phone = sharedPreferences.getString(SharedPreferredKey.PHONENUM, "");
		mTextViewSettingPhoneNum.setText("手机号码：" + phone);
		mPhoneNum = phone;
		mTextViewSettingNyGroup.setText("所 属 组：" + sharedPreferences.getString(SharedPreferredKey.GROUP_NAME, "")); // 设置所属组
		mHeightTextView.setText("身高：" + sharedPreferences.getString(SharedPreferredKey.HEIGHT, "0") + " （厘米）");
		mNicknameTextView.setText("昵称：" + sharedPreferences.getString(SharedPreferredKey.NICK_NAME, ""));
		mTargetTextView.setText("体重目标：" + sharedPreferences.getString(SharedPreferredKey.TARGET_WEIGHT, "") + " （千克）");
		mTextViewBirthday.setText(getString(R.string.string_birthday)
				+ sharedPreferences.getString(SharedPreferredKey.BIRTHDAY, "0000-00-00"));
		mTextViewWeight.setText("体重：" + sharedPreferences.getString(SharedPreferredKey.WEIGHT, "0.0") + " （千克）");

		mSex = sharedPreferences.getString(SharedPreferredKey.GENDER, null);// 性别
		String name = sp.getString(SharedPreferredKey.NAME, null);
		if (mSex != null && mSex.equals("0")) {
			mImageViewAvatar.setImageResource(R.drawable.p);
		} else {
			mImageViewAvatar.setImageResource(R.drawable.a);
		}
		getAvatar();
	}

	private Drawable getImageAsync(ImageView holder, String url) {
		return getImageAsync(holder, url, null);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag) {
		return getImageAsync(holder, url, null, 0);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag, int mode) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
	}

	/**
	 * 获取头像
	 */
	private void getAvatar() {
		mAvatarName = getSharedPreferences().getString(SharedPreferredKey.AVATAR, null);
		Logger.d(TAG, "mAvatarName == ------->" + mAvatarName);
		// 本地SD卡路径
		if (null != mAvatarName) {
			getImageAsync(mImageViewAvatar, mAvatarName);
			// String DexedAvatarName = Encrypt.getMD5Str(mAvatarName);
			// String mAvatarPath = Environment.getExternalStorageDirectory()
			// + "/ishang_image/" + DexedAvatarName;
			// Logger.i(TAG, "mAvatarPath==" + mAvatarPath);
			// FileInputStream is;
			// try {
			// is = new FileInputStream(mAvatarPath);
			// Bitmap bitmap = BitmapFactory.decodeStream(is);
			// if (bitmap != null) {
			// mImageViewAvatar.setImageBitmap(bitmap);
			// }
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		String serverversion = sp.getString(SharedPreferredKey.SERVER_VERSION, "2");
		mNicknameTextView = findView(R.id.nickname_textview);
		mHeightTextView = findView(R.id.height_textview);
		mTargetTextView = findView(R.id.targetWeight_textview);
		mTextViewBirthday = findView(R.id.textview_birthday);

		mTextViewWeight = findView(R.id.textview_weight);

		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setOnClickListener(this);

		for (int i = 0; i < mRelativeLayoutIds.length; i++) {
			RelativeLayout rlList = findView(mRelativeLayoutIds[i]);
			rlList.setOnClickListener(this);
		}

		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.main_setting);

		mTextViewSettingNyGroup = findView(R.id.setting_my_groupname);
		mTextViewVersion = findView(R.id.tv_version);
		mTextViewSettingSportTargetNum = findView(R.id.setting_sport_target_num);

		mRelativeLayoutArea = findView(R.id.setting_area);
		mRelativeLayoutArea.setOnClickListener(this);

		mTvAreaDetail = findView(R.id.tv_area_detail);
		mTvArea = findView(R.id.textview_area);
		changeAreaInfoText();

		mTextViewSettingPhoneNum = findView(R.id.setting_phone_num);
		mWeightBar = findView(R.id.setting_target_weight);
		mHeightBar = findView(R.id.setting_height);
		mBindDeviceBar = findView(R.id.rlayout_setting_device);
		if (!"2".equals(serverversion)) {
			mWeightBar.setVisibility(View.GONE);
			mHeightBar.setVisibility(View.GONE);
			mBindDeviceBar.setVisibility(View.GONE);
		}

		mImageViewAvatar = findView(R.id.imageview_avatar);

		mProgressDialog = CustomProgressDialog.createDialog(mActivity);
		;
		if (mFlag == 0) {
			openDialogSetFaceImage();
		}

		mRl_step_switch = findView(R.id.rl_step_switch);
		setting_problem = findView(R.id.setting_problem);
		setting_problem.setOnClickListener(this);
		mSw_startStep = (WiperSwitch) findView(R.id.sw_startStep);
		// mSw_startStep.setEnabled(false);
		mSw_startStep.setOnChangedListener(stepchangeListener);
		
		mSw_bootRun = (WiperSwitch) findView(R.id.sw_setting_boot_run);
		// mSw_startStep.setEnabled(false);
		mSw_bootRun.setOnChangedListener(stepchangeListener);
		// 钃濈墮鎵嬬幆璁剧疆
		mrl_bracelet_setting = findView(R.id.rl_bracelet_setting);
		mrl_bracelet_setting.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		// UMFeedbackService.enableNewReplyNotification(this,
		// NotificationType.NotificationBar);
		// UMFeedbackService.enableNewReplyNotification(this,
		// NotificationType.AlertDialog);
		setValueFromShare();
	}

	/**
	 * 压缩图片并缓存到存储卡，startActivityForResult方式调用剪切程序
	 * 
	 * @param uri
	 *            图片uri
	 * @param cachePath
	 *            缓存路径
	 * @param isRoate
	 *            是否翻转
	 */
	private void cropPhoto(Uri uri, String cachePath, int nRoate) {
		// 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
		File file = CropUtil.makeTempFile(mActivity, uri, cachePath, nRoate);
		if (file != null && file.exists()) {
			// 调用CropImage类对图片进行剪切
			Intent intent = new Intent(mActivity, CropImageActivity.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", 1);
			extras.putString("cachePath", cachePath);
			extras.putInt("aspectY", 1);
			intent.putExtra("outputX", 256);
			intent.putExtra("outputY", 256);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			intent.putExtras(extras);
			startActivityForResult(intent, PHOTO_CROP_REQUEST_CODE);
			mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.silde_out_left);
		} else {
			messagesManager(Constants.MESSAGE_CROP_FAILED);
		}
	}

	public void openDialogSetFaceImage() {
		new AlertDialog.Builder(mActivity).setTitle("设置头像").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 从相册选择图片
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*");
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
					mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.silde_out_left);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (Common.existSDcard()) {
						File mediaStorageDir = new File(ImgUrl);
						if (!mediaStorageDir.exists()) {
							if (!mediaStorageDir.mkdirs()) {
								Logger.e(TAG, "failed to create directory");
								return;
							}
						}
						if (mAvatarName != null) {
							mFileImagePath = mediaStorageDir.getPath() + File.separator + mAvatarName;
							mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(mAvatarName);
						} else {
							mFileImagePath = mediaStorageDir.getPath() + File.separator + mPhoneNum;
							mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(mPhoneNum);
						}
						File mediaFile = new File(mFileImagePathDexed + "_temp");
						if (mediaFile.exists()) {
							mediaFile.delete();
						}

						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
					}
					startActivityForResult(intentFromCapture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Logger.i(TAG, "----onActivityResult");
		// super.onActivityResult(requestCode, resultCode, data);
		ExifInterface exif;
		int exifOrientation;
		if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
			// 本地图片上传
			Uri originalUri = data.getData(); // 获得图片的uri
			if (originalUri != null && Common.existSDcard()) {
				// File mediaStorageDir = new
				// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				// Constants.APP_NAME);
				File mediaStorageDir = new File(ImgUrl);
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						Logger.e(TAG, "failed to create directory");
						return;
					}
				}
				// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				// .format(new Date());
				if (mAvatarName != null) {
					String bitmapName = mAvatarName.substring(mAvatarName.lastIndexOf("/") + 1);
					mFileImagePath = mediaStorageDir.getPath() + File.separator + bitmapName;
					mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(bitmapName);
				} else {
					mFileImagePath = mediaStorageDir.getPath() + File.separator + mPhoneNum;
					mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(mPhoneNum);
					// mFileImagePath = mediaStorageDir.getPath() +
					// File.separator + mPhoneNum+".jpg";
				}
				// path 当你设置自己的路径时，需要判断这个文件目录是否存在
				// File file = new File(path);
				// if (!file.exists()) {
				// file.mkdirs();
				// }
				int nRoate = 0;
				try {
					exif = new ExifInterface(mFileImagePathDexed + "_temp");
					exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
					if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
						nRoate = 90;
					} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
						nRoate = 180;
					} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
						nRoate = 270;
					}
					Logger.e(TAG, nRoate + "");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Since API Level 5
				cropPhoto(originalUri, mFileImagePathDexed + "_temp", nRoate);

				// Intent intent = new Intent(this, CropImageActivity.class);
				// Bundle extras = new Bundle();
				// extras.putString("circleCrop", "true");
				// extras.putInt("aspectX", 1);
				// extras.putString("cachePath", mFileImagePath);
				// extras.putInt("aspectY", 1);
				// intent.putExtra("outputX", 64);
				// intent.putExtra("outputY", 64);
				// intent.setDataAndType(originalUri, "image/*");
				// intent.putExtras(extras);
				// startActivityForResult(intent, PHOTO_CROP_REQUEST_CODE);

			} else {
				messagesManager(Constants.MESSAGE_CROP_FAILED);
			}
		} else if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			// Bundle extras = intent.getExtras();
			// mImageBitmap = (Bitmap) extras.get("data");
			// if (mFileUri != null) {
			// String cachePath = PATH_IMAGE + IMAGE_FILE_NAME;
			// File file = new File(cachePath);
			// if (!file.exists()) {
			// file.mkdirs();
			// }
			// cachePath = mFileImagePath;
			// path 当你设置自己的路径时，需要判断这个文件目录是否存在
			// File file = new File(path);
			// if (!file.exists()) {
			// file.mkdirs();
			// }

			int nRoate = 0;
			try {
				exif = new ExifInterface(mFileImagePathDexed + "_temp");
				exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
				if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
					nRoate = 90;
				} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
					nRoate = 180;
				} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
					nRoate = 270;
				}
				Logger.e(TAG, nRoate + "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Since API Level 5
			cropPhoto(Uri.fromFile(new File(mFileImagePathDexed + "_temp")), mFileImagePathDexed + "_temp", nRoate);
			// cropPhoto(Uri.fromFile(new File(mFileImagePath)), mFileImagePath,
			// true);
			// } else {
			// messagesManager(Constants.MESSAGE_CROP_FAILED);
			// }

		} else if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CROP_REQUEST_CODE) {
			try {
				Bundle extras = data.getExtras();
				String photoPath = extras.getString("cachePath");

				if (photoPath != null) {
					handler.sendEmptyMessage(TO_UPLOAD_FILE);
				} else {
					BaseToast("上传的文件路径出错");
				}

			} catch (Exception e) {
				e.printStackTrace();
				messagesManager(Constants.MESSAGE_CROP_FAILED);
			}
		} else if (resultCode == Activity.RESULT_OK && requestCode == AREA_SETTING_ACTIVITY_REQUEST_CODE) {
			changeAreaInfoText();
		}
	}

	/**
	 * 改变区域信息显示 TODO
	 * 
	 * @return void
	 * @author shaoting.chen
	 * @time 下午3:45:40
	 */
	private void changeAreaInfoText() {
		String province = PreferencesUtils.getString(getActivity(), SharedPreferredKey.PROVINCE_NAME, "");
		String city = PreferencesUtils.getString(getActivity(), SharedPreferredKey.CITY_NAME, "");
		String county = PreferencesUtils.getString(getActivity(), SharedPreferredKey.COUNTY_NAME, "");
		if (!TextUtils.isEmpty(county) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(province)) {
			mTvArea.setTextColor(getActivity().getResources().getColor(R.color.black));
			mTvAreaDetail.setText(province + "-" + city + "-" + county);
			mTvAreaDetail.setTextColor(getActivity().getResources().getColor(R.color.black));
		}
	}

	StepService_GPS.OnMyLocationChangeListener changeListener = new StepService_GPS.OnMyLocationChangeListener() {

		@Override
		public void timer(String timer) {

		}

		@Override
		public void change(GpsInfoDetail GPSInfo) {
		}

		@Override
		public void changeAltitude(double altitude) {
			if (altitude != 0) {
				if (altitude > 10) {
				} else {
				}
			}
		}

		@Override
		public void gpsIntensity(int intensity) {
			// TODO Auto-generated method stub
			
		}
	};
	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService_GPS.myBind) service).getService();
			mService.registerCallback(changeListener);
			// mService.getType(mType);
			mService.requeatNotify();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void clearServise() {
		if (StepService_GPS.isRunning) {
			getActivity().bindService(new Intent(getActivity(), StepService_GPS.class), mConnection,
					Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
			Intent i = new Intent();
			i.setClass(getActivity(), StepService_GPS.class);
			getActivity().startService(i);

			// stopTimer();
			getActivity().unbindService(mConnection);
			mActivity.stopService(new Intent().setClass(mActivity, StepService_GPS.class));
		}
		if (StepService.isRunning) {
			StepController mStepController = new StepController();
			mStepController.setContext(getActivity());
			mStepController.setStopAction(Config.PHONESTEP_STOP_NOSAVE_ACTION);
			mStepController.stopStepService();
//			Intent it = new Intent(Config.PHONESTEP_STOP_NOSAVE_ACTION);
//			mActivity.sendBroadcast(it);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			if (Build.VERSION.SDK_INT > 17) {
				TgbleManagerNeuro tgble = TgbleManagerNeuro.getSingleInstance(getActivity().getApplicationContext());
				tgble.cancleUploadTime();
			}
			getActivity().finish();
			break;
		case R.id.setting_face:
			openDialogSetFaceImage();
			break;
		case R.id.setting_sport_target:
			intent.setClass(mActivity, SettingTargetStepActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_target_weight:
			intent.setClass(mActivity, SettingTargetWeightActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_aboutIS:
			intent.setClass(mActivity, SettingAboutActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_exit:
			clearServise();
			//结束手机已上传数据
			if(PreferencesUtils.getInt(mActivity, SharedPreferredKey.DEVICE_TYPE, 0)!=DeviceConstants.DEVICE_MOBILE_STEP){
				new Thread(new Runnable() {					
					@Override
					public void run() {
						String mDeviceId=PreferencesUtils.getString(getActivity(), SharedPreferredKey.DEVICE_ID, "");
						PedometorDataInfo pedo= PedoController.GetPedoController(getActivity()).getLatestPedometer(mDeviceId);
						UploadManager.uploadBlePedo(pedo);						
					}
				}).start();
			}
			Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();

			// sharedata.putBoolean("checkdRemember", false);
			sharedata.putBoolean("checkdAuto", false);
			sharedata.putString(SharedPreferredKey.SERVER_NAME, null);
			sharedata.putString("GROUP_UPDATE_VERSION", null);
			sharedata.putString("INPK_UPDATE_TIME_RACE", null);
			sharedata.remove("DLStarttime");
			sharedata.remove("ULStarttime");
			// sharedata.remove("BSHOWGUID");
			sharedata.putInt("INSTALL", -1);
			Common.clearDatabases(getActivity());
			sharedata.commit(); 
			
			mActivity.finish();
			MapApplication.getInstance().finishActivity();
			break;
		case R.id.setting_update:
			mRelativeLayout.setClickable(false);
			updateVersion();
			break;
		case R.id.setting_feedback:
			intent.setClass(mActivity, OpinionListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_setting_clear:
			Common.clearDatabases(getActivity());
			ToastUtils.showToast(mActivity, "清除完成");
			break;
		case R.id.rlayout_setting_help:
			Logger.d(TAG, "load location html");
			intent.setClass(mActivity, SettingAboutActivity.class);
			startActivity(intent);
			break;
		case R.id.rlayout_setting_device:
			intent.setClass(mActivity, DeviceSettingActivityTest.class);
			// intent.setClass(mActivity, SettingDeviceBindActivity_old.class);
			// intent.setClass(mActivity, SettingDeviceBindActivity.class);
			intent.putExtra("sampletitle", getString(R.string.textview_binddevice));
			// TODO
			intent.putExtra(SharedPreferredKey.PASSWORD,
					PreferencesUtils.getString(getActivity(), SharedPreferredKey.PASSWORD, ""));
			intent.putExtra(SharedPreferredKey.PHONENUM, mPhoneNum);
			startActivity(intent);
			break;

		case R.id.setting_area:
			Intent _intent = new Intent(mActivity, SettingArea.class);
			startActivityForResult(_intent, AREA_SETTING_ACTIVITY_REQUEST_CODE);
			break;
		case R.id.rl_bracelet_setting:
			int deviceType = PreferencesUtils.getInt(getActivity(), SharedPreferredKey.DEVICE_TYPE, 0);
			if (deviceType == DeviceConstants.DEVICE_BRACLETE_JW || deviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				intent.setClass(mActivity, DingdangSettingActivity.class);
				startActivity(intent);
			} else if (deviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				intent.setClass(mActivity, BraceletSettingActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.setting_problem:
			String url ="http://"+Config.SERVER_NAME+"openClientApi.do?action=questionList";
			Intent intent1 = new Intent();
			intent1.putExtra("UserInfo", url);
            intent1.putExtra("title", "常见问题");
			intent1.setClass(getActivity().getApplicationContext(), WebViewActivity.class);
			startActivity(intent1);
		
		default:
			break;
		}
	}

	private void updateVersion() {
		/* 更新判断 */
		new Thread() {
			@Override
			public void run() {
				if (NetworkTool.isOnline(mActivity)) {
					mVerName = Config.getVerName(mActivity);
					if (UpdateSoftWareTools.isNewVersionAvaliable(mVerName)) {
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								updateAPK();
							}
						});
					} else {
						mRelativeLayout.setClickable(true);
						messagesManager(Constants.MESSAGE_UPDATED_VERSION);
					}
				} else {
					mRelativeLayout.setClickable(true);
					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
				}
			};
		}.start();
	}

	

	/* 更新apk */
	private void updateAPK() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:\t");
		sb.append(mVerName + "\n");
		sb.append("发现新版本:\t");
		sb.append(UpdateSoftWareTools.newVerName + "\n");
		sb.append("更新说明:\t");
		sb.append(UpdateSoftWareTools.newVerInfo + "\n");
		sb.append("是否更新?");

		// 更新
		new AlertDialog.Builder(mActivity).setTitle("更新提示").setMessage(sb.toString()).setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				mRelativeLayout.setClickable(true);
			}
		})
		// .setMessage("发现新版本，是否更新")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						// 0未无网络，1，2有网
						mRelativeLayout.setClickable(true);
						int internet = NetworkTool.getNetworkState(mActivity);
						if (internet != 0) {
							Intent intent = new Intent();
							intent.putExtra("downloadsite", UpdateSoftWareTools.download);
							intent.setClass(mActivity, PreLoadAPKUpdateProgressActivity.class);
							startActivity(intent);
						} else {
							ToastUtils.showToast(getActivity(), R.string.MESSAGE_INTERNET_ERROR);
						}

					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mRelativeLayout.setClickable(true);
					}
				}).show();
	}

	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;

	@Override
	public void onUploadProcess(int uploadSize) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg);
	}

	@Override
	public void initUpload(int fileSize) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	private void toUploadFile() {
		// uploadImageResult.setText("正在上传中...");
		mProgressDialog.setMessage(getResources().getString(R.string.settingfragmentapp_nowupload));
		mProgressDialog.show();
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, "");
		String password = info.getString(SharedPreferredKey.PASSWORD, "");
		params.put("userid", mPhoneNum);
		params.put("psw", password);
		requestURL = DataSyn.strHttpURL + "uploadAvatar";
		uploadUtil.uploadFile(mFileImagePathDexed + "_temp", fileKey, requestURL, params);
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			case UPLOAD_INIT_PROCESS:
				// progressBar.setMax(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				// progressBar.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
				// String result = "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj +
				// "\n耗时：" + UploadUtil.getRequestTime() + "秒";
				// uploadImageResult.setText(result);
				if (msg.arg1 == UploadUtil.UPLOAD_SUCCESS_CODE) {
					Gson gson = new Gson();
					try {
						UpdatePasswordInfo reqResult = gson.fromJson(msg.obj.toString(), UpdatePasswordInfo.class);

						if (reqResult.status.equals("SUCCESS")) {

							File newFile = new File(mFileImagePathDexed + "_temp");
							File oldFile = new File(mFileImagePathDexed);

							try {
								Common.copyUseChannel(newFile, oldFile);
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							Editor editorData = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
							String bitmapName = mFileImagePath.substring(mFileImagePath.lastIndexOf("/") + 1);
							Logger.d(TAG, "bitmapName == ---->" + bitmapName);
							editorData.putString(SharedPreferredKey.AVATAR, bitmapName);
							editorData.commit();

							HashMap<String, Bitmap> sHardBitmapCache = ImageUtil.getInstance().getSHardBitmapCache();
							// sHardBitmapCache.remove(Encrypt.getMD5Str(bitmapName));
							sHardBitmapCache.clear();
							FileInputStream is;
							try {
								is = new FileInputStream(mFileImagePathDexed);
								Bitmap bitmap = BitmapFactory.decodeStream(is);
								if (bitmap != null)
									mImageViewAvatar.setImageBitmap(bitmap);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							messagesManager(Constants.MESSAGE_AVARAR_SET_SUCCESS);

						} else {
							BaseToast(reqResult.reason);
						}
					} catch (JsonSyntaxException ex) {
						ex.printStackTrace();
						ToastUtils.showToast(getActivity(), R.string.MESSAGE_INTERNET_ERROR);
					}
				} else if (msg.arg1 == UploadUtil.UPLOAD_FILE_NOT_EXISTS_CODE) {
					BaseToast("上传的文件不存在");
				} else if (msg.arg1 == UploadUtil.UPLOAD_SERVER_ERROR_CODE) {
					BaseToast("服务器繁忙请稍等在试");
				}

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private String mAvatarName;

	private String mSex;

	private LinearLayout mLinearLayoutHeight;

	private LinearLayout mLinearLayoutTargetWeight;

	private TextView mHeightTextView;

	private TextView mTargetTextView;
	private TextView mNicknameTextView;

	@Override
	public void findViews() {
		initView();
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}

	private OnChangedListener stepchangeListener = new OnChangedListener() {
		@Override
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
			switch (wiperSwitch.getId()) {
			case R.id.sw_startStep:
				try {
					if (checkState) {
						StepController mStepController = new StepController();
						mStepController.setContext(getActivity());
						mStepController.startStepService(Config.SC_ACTION);
						PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.IS_STEPOPEN, true);
					} else {
						StepController mStepController = new StepController();
						mStepController.setContext(getActivity());
						mStepController.setStopAction(Config.PHONESTEP_STOP_ACTION);
						mStepController.stopStepService();
						PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.IS_STEPOPEN, false);
//						Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
//						getActivity().sendBroadcast(it);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
            case R.id.sw_setting_boot_run:
            	try {
    				if (checkState) {
    					PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.SETTING_BOOT_RUN, true);
    				} else {
    					PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.SETTING_BOOT_RUN, false);
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
				break;

			default:
				break;
			}
			
		}
	};
}
