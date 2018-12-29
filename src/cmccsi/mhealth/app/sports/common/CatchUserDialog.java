package cmccsi.mhealth.app.sports.common;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.bean.FindFriendInfo;
import cmccsi.mhealth.app.sports.bean.FriendSearchItem;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;
/**
 * 搜索user的dialog
 * 步骤：
 * mCatchDialog = new CatchUserDialog(RaceInvite.this, mPhoneNum, mPassword);<br>
 * mCatchDialog.startCapture("邀请");<br>
 * mCatchDialog.setOnUserCapturedListener(new UserCapturedListener() {...}<br>
 * @author zy
 *
 */
public class CatchUserDialog {
	// 显示邀请dialog
	private Dialog customDialog;
	private Button mInvite;
	private EditText inputPhone;
	private Button mCancel;
	private boolean mAdding;
	private TextView tvtip;
	private String targetphone;
	private ImageView mFriendAvatar;
	private FindFriendInfo mFFreqData;
	private String FdName;
	private String FdAvatar;

	private Context context;

	private String mPhoneNum;
	private String mPassword;

	private UserCapturedListener listener;

	public final static int CAPTURE_SUCCESS = 200;
	public final static int CAPTURE_FAIL = 201;

	private final static int FIND_SUCCESS = 0;
	private final static int FIND_FAIL = 1;
	private final static int NET_PROBLEM = 2;
	private final static int FIND_MYSELF = 3;
	private Handler handle = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case FIND_SUCCESS:
				mInvite.setClickable(true);
				mInvite.setTextColor(Color.WHITE);
				mInvite.setBackgroundResource(R.drawable.sample_usercatch_button_ok_bg);
				ImageUtil.getInstance().loadBitmap(mFriendAvatar, DataSyn.avatarHttpURL + FdAvatar, null, 0);
				tvtip.setText(FdName);
				break;
			case FIND_FAIL:
				resetFriendInfo();
				tvtip.setText("没有找到这个人哦，请重试~");
				break;
			case FIND_MYSELF:
                resetFriendInfo();
                String avatarUrl=PreferencesUtils.getString(context, SharedPreferredKey.AVATAR, null);
                ImageUtil.getInstance().loadBitmap(mFriendAvatar, avatarUrl, null, 0);
                tvtip.setText(context
                        .getString(R.string.text_addfriend_find_myself));
			    break;
			case NET_PROBLEM:
				if (listener != null) {
                    listener.onCapturedUser(CAPTURE_FAIL, context.getResources().getString(R.string.MESSAGE_INTERNET_ERROR), null,
                            null, null);
				}
				resetFriendInfo();
				break;
			}
		}
	};

	public CatchUserDialog(Context context, String mPhoneNum) {
		this.context = context;
		this.mPhoneNum = mPhoneNum;
	}
	
	public CatchUserDialog(Context context,String mPhoneNum,String mPassword){
	    this(context, mPhoneNum);
	}

	private void resetFriendInfo() {
	    FdName = "";
        FdAvatar = "";
		mInvite.setClickable(false);	
		mInvite.setTextColor(context.getResources().getColor(R.color.green_friend));
		mInvite.setBackgroundResource(R.drawable.btn_canclefriend);
		mFriendAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.tab_add_friend));
		mFriendAvatar.setBackgroundResource(R.drawable.tab_add_friend);
		tvtip.setText("");
	}

	public void startCapture(String okbtnStr) {
		FdName = null;
		FdAvatar = null;

		customDialog = new Dialog(context, R.style.dialog_withStatusBar);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCanceledOnTouchOutside(true);
		View viewDialog = View.inflate(context, R.layout.alertdialog_addfriend, null);

		inputPhone = (EditText) viewDialog.findViewById(R.id.af_etn_keyword);
		inputPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() == 11) {
					tvtip.setText("查找中...");
					tvtip.setVisibility(View.VISIBLE);
					Common.collapseSoftInputMethod(context, inputPhone);// 关闭软键盘
					targetphone = s.toString().trim();
					findFriend(targetphone);// 搜！
				}else if (s.toString().trim().length() == 0) {
				    resetFriendInfo();
                }
			}
		});

		mCancel = (Button) viewDialog.findViewById(R.id.af_btn_canclefriend);
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mAdding)
					customDialog.dismiss();
			}
		});
		mInvite = (Button) viewDialog.findViewById(R.id.af_btn_addfriend);
		mInvite.setText(okbtnStr);
		mInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCapturedUser(CAPTURE_SUCCESS, null, FdName, FdAvatar.split("\\.")[0], targetphone);
				}
				customDialog.dismiss();
			}
		});
		mInvite.setClickable(false);

		mFriendAvatar = (ImageView) viewDialog.findViewById(R.id.af_avatar_icon);
		mFriendAvatar.setBackgroundResource(R.drawable.tab_add_friend);
		
		TextView cmtdsd = (TextView) viewDialog.findViewById(R.id.af_click_me_to_dismiss_search_dialog);
		cmtdsd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!mAdding)
					customDialog.dismiss();
			}
		});

		tvtip = (TextView) viewDialog.findViewById(R.id.af_tip);
		tvtip.setVisibility(View.INVISIBLE);

		Window window = customDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 
		window.setWindowAnimations(R.style.mystylebottom); // 添加动画

		viewDialog.setBackgroundColor(Color.TRANSPARENT);
		customDialog.setContentView(viewDialog);
		customDialog.show();

	}

	// 添加前首先查找是否存在用户
	private void findFriend(final String str) {
	    mFriendAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.unknowfriend));
	    if (str.equals(mPhoneNum)) {
            handle.sendEmptyMessage(FIND_MYSELF);
            return;
        }
		new Thread() {
			public void run() {
				mFFreqData = new FindFriendInfo();
				int suc = DataSyn.getInstance().findFriendById(mPhoneNum, mPassword, str, mFFreqData);
				if (suc == 0) {
					List<FriendSearchItem> ffis = mFFreqData.dataValue;
					if (ffis != null && ffis.size() > 0) {
						FriendSearchItem ffi = ffis.get(0);
						FdName = ffi.name;
						FdAvatar = ffi.avatar;
						handle.sendEmptyMessage(FIND_SUCCESS);
					} else {
						handle.sendEmptyMessage(FIND_FAIL);
					}
				} else if ("FAILURE".equals(mFFreqData.status)) {
					handle.sendEmptyMessage(FIND_FAIL);
				} else {
					handle.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
	}

	public interface UserCapturedListener {
		abstract void onCapturedUser(int state, String reason, String name, String avatar, String targetphone);
	}

	public void setOnUserCapturedListener(UserCapturedListener listener) {
		this.listener = listener;
	}
}
