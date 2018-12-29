package cmccsi.mhealth.app.sports.activity;

import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.FindFriendInfo;
import cmccsi.mhealth.app.sports.bean.FriendSearchItem;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

/**
 * 手动添加好友
 * @type HandAddContactActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年6月2日下午6:01:24
 */
public class HandAddContactActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "HandAddContactActivity";
	
	private CustomProgressDialog progressDialog;
	
	private LinearLayout mLlAdd;
	private RelativeLayout mRlItem;
	private ImageView mIvPhoto;
	private TextView mTvName;
	private TextView mTvNubmer;
	private EditText mEtNumber;
	
	private String mNumber = "1"; 
	private String mNumber_temp = "2";
	private FriendSearchItem mFriendSearchItem;
	
	private boolean isSendMsg = false; //是否已经发送添加当前好友请求

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friend_search);

		initViews();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_friend_search_add:
			if(StringUtils.isNotBlank(mNumber)){
				addFriendsByPhonenumber(mNumber);
            }
			break;
		case R.id.btn_friend_search_cancle:
            this.finish();
			break;
		case R.id.btn_friend_search:
			if(!mNumber_temp.equals(mNumber)){
				mNumber = mEtNumber.getText().toString();
	            if(mNumber.length() == 11){
	            	searchFriend(mNumber);
	            }
			}
			break;

		default:
			break;
		}
	}

	private void initViews() {
		
		BaseBackKey(getResources().getString(R.string.handaddcontactactivity_title), this);	

		Button btnAdd = (Button) findViewById(R.id.btn_friend_search_add);
		btnAdd.setOnClickListener(this);
		Button btnCancle = (Button) findViewById(R.id.btn_friend_search_cancle);
		btnCancle.setOnClickListener(this);
		Button btnSearch = (Button) findViewById(R.id.btn_friend_search);
		btnSearch.setOnClickListener(this);
		
		mLlAdd = (LinearLayout) findViewById(R.id.ll_search_friend_add);
		mRlItem = (RelativeLayout) findViewById(R.id.rl_search_friend_item);
		mIvPhoto = (ImageView) findViewById(R.id.iv_search_friend_image);
		mTvName = (TextView) findViewById(R.id.tv_search_friend_name);
		mTvNubmer = (TextView) findViewById(R.id.tv_search_friend_mobile);
		mEtNumber = (EditText) findViewById(R.id.et_friend_search_number);
		mEtNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				mNumber_temp = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mNumber_temp = s.toString();
			}
		});
	}
	
	/**
	 * 搜索完成后页面调整
	 */
	private void searchSuccess(boolean success, FriendSearchItem friendSearchItem){
		if(success){
			mLlAdd.setVisibility(View.VISIBLE);
			mRlItem.setVisibility(View.VISIBLE);
			ImageUtil.getInstance().loadBitmap(mIvPhoto, mFriendSearchItem.avatar);
			mTvName.setText(mFriendSearchItem.name);
			mTvNubmer.setText(mNumber);
		}else{
			mLlAdd.setVisibility(View.GONE);
			mRlItem.setVisibility(View.GONE);
		}
	}

	/***
	 * 查询好友
	 */
	private void searchFriend(String phonenumber){
		try {
			new CheckPhoneNumbersTask().execute(phonenumber);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	
	/**
	 * 添加好友
	 */
	private void addFriendsByPhonenumber(String phonenumber){
		if(!isSendMsg){
			try {
				new AddPhoneNumbersTask().execute(phonenumber);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}else{
			ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.handaddcontactactivity_sendmsg));
		}
	}
	
	/**
	 * 查询用户
	 */
	private class CheckPhoneNumbersTask extends AsyncTask<String, Null, Integer> {
		
		public CheckPhoneNumbersTask() {
			showProgress(getResources().getString(R.string.handaddcontactactivity_searchfriend));
		}

		@Override
		protected Integer doInBackground(String... params) {
			FindFriendInfo mFFreqData = new FindFriendInfo();
			int suc = DataSyn.getInstance().findFriendById("", "", params[0], mFFreqData);
			if (suc == 0) {
				List<FriendSearchItem> ffis = mFFreqData.dataValue;
				if (ffis != null && ffis.size() > 0) {
					mFriendSearchItem = ffis.get(0);
				} else {
					return 2;
				}
			} else if ("FAILURE".equals(mFFreqData.status)) {
				return 2;
			} else {
				return 1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			close();
			if (result == 0) {
				searchSuccess(true, mFriendSearchItem);
			} else if(result == 2){
				ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.handaddcontactactivity_searchnofriend));
				searchSuccess(false, null);
			} else{
				ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.handaddcontactactivity_searchfalse));
				searchSuccess(false, null);
			}
		}
	}
	/**
	 * 添加好友
	 */
	private class AddPhoneNumbersTask extends AsyncTask<String, Null, Integer> {
		
		private String errorMsg = getResources().getString(R.string.handaddcontactactivity_sendmsgfalse);
		
		public AddPhoneNumbersTask() {
			showProgress(getResources().getString(R.string.handaddcontactactivity_addfriend));
		}

		@Override
		protected Integer doInBackground(String... params) {
			BackInfo AfireqData = new BackInfo();
            int suc = DataSyn.getInstance().addFriendById(params[0], AfireqData);
            Logger.i(TAG, "---reason " + AfireqData.reason);
            if (suc == 0 && "等待对方同意".equals(AfireqData.reason)) {
                return 0;
            } else if ("FAILURE".equals(AfireqData.status) && !AfireqData.reason.equals("")) {
            	
            	errorMsg = AfireqData.reason;
            	Logger.i(TAG, "---errorMsg " + errorMsg);
                return 1;
            }else{
            	return 1;
            }
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			close();
			if (result == 0) {
				isSendMsg = true;
				ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.handaddcontactactivity_sendmsg));
			}else{
				ToastUtils.showToast(getApplicationContext(), errorMsg);
			}
		}
	}
	/**
	 * 加载提示
	 */
	private void showProgress(String msg) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = CustomProgressDialog.createDialog(this);
		progressDialog.setMessage(msg);
		progressDialog.show();
	}
	/**
	 * 关闭加载提示
	 */
	private void close() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
