package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import cmccsi.mhealth.app.sports.adapter.ContactAdapter;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.ContactInfo;
import cmccsi.mhealth.app.sports.bean.ContactListInfo;
import cmccsi.mhealth.app.sports.bean.MultAddFriendsBackInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ContactUtil;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

/**
 * 匹配通讯录添加好友
 * 
 * @type MatchContactActivity TODO
 * @author shaoting.chen
 * @time 2015年6月2日下午2:20:49
 */
public class MatchContactActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private static final String TAG = "MatchContactActivity";

	private ListView mLvContacts;
	private List<ContactInfo> mContactList;
	private ContactAdapter mContactAdapter = null;
	private List<ContactInfo> mMobileContactList;

	private CustomProgressDialog progressDialog;

	private String mPhonenumbers;

	private List<Integer> mIndexs = new ArrayList<Integer>();

	private Activity mActivity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match_contact);

		initViews();

		checkPhonenumbers(getUserId());
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
		if (mContactAdapter != null) {
			mContactAdapter.clear();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_match_contact_add:
			if (mContactAdapter != null) {
				List<ContactInfo> contactList = mContactAdapter.getList();
				StringBuffer sb_ids = new StringBuffer();
				for (int i = 0; i < contactList.size(); i++) {
					if (contactList.get(i).isChecked() && contactList.get(i).getIsFriend().equals("0")) {
						sb_ids.append(contactList.get(i).getUserid()).append(",");
						mIndexs.add(i);
					}
				}
				if (sb_ids.length() > 0) {
					addFriendsByPhonenumbers(getUserId(), sb_ids.toString().substring(0, sb_ids.length() - 1));
				} else {
					ToastUtils.showToast(getApplicationContext(), "请选择要添加好友");
				}
			}

			break;
		case R.id.btn_match_contact_cancle:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	private void initViews() {

		BaseBackKey("匹配通讯录", this);

		mContactList = new ArrayList<ContactInfo>();
		mLvContacts = (ListView) findViewById(R.id.lv_match_contact);
		mLvContacts.setOnItemClickListener(this);

		Button btnAdd = (Button) findViewById(R.id.btn_match_contact_add);
		btnAdd.setOnClickListener(this);
		Button btnCancle = (Button) findViewById(R.id.btn_match_contact_cancle);
		btnCancle.setOnClickListener(this);
	}

	private void initList() {

		mContactAdapter = new ContactAdapter(this, mContactList);
		mLvContacts.setAdapter(mContactAdapter);
	}

	/***
	 * 匹配通讯录
	 */
	private void checkPhonenumbers(String userid) {
		try {
			new CheckPhoneNumbersTask().execute(userid);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/***
	 * 获取本地通讯录
	 */
	private String getContactsFromMobile() {
//		mMobileContactList = new ArrayList<ContactInfo>();
//		for(int i = 0; i < 500; i++){
//			ContactInfo ci = new ContactInfo("", "", "18631146735", "陈少亭" + i, "", false);
//			mMobileContactList.add(ci);
//		}
		
		try {
			mMobileContactList = ContactUtil.getContactList(mActivity);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

		StringBuffer phonenumbers = new StringBuffer();
		if (mMobileContactList.size() > 0) {
			for (int i = 0; i < mMobileContactList.size(); i++) {
				phonenumbers.append(mMobileContactList.get(i).getPhonenumber()).append(",");
			}
		}
		if (phonenumbers.length() > 0) {
			Logger.i(TAG, "---phonenumbers " + phonenumbers);
			return phonenumbers.toString().substring(0, phonenumbers.length() - 1);
		}
		return null;
	}

	/**
	 * 给爱动力用户添加用户名
	 */
	private void combineList(List<ContactInfo> contactList, List<ContactInfo> mobileContactList) {
		if (contactList.size() > 0 && mobileContactList.size() > 0) {
			for (int i = 0; i < contactList.size(); i++) {
//				Logger.i(TAG, "---number:" + contactList.get(i).getPhonenumber());
				for (int j = 0; j < mobileContactList.size(); j++) {
					if (contactList.get(i).getPhonenumber().equals(mobileContactList.get(j).getPhonenumber())) {
						contactList.get(i).setPhonename(mobileContactList.get(j).getPhonename());
					}
				}
			}
//			for (int j = 0; j < mobileContactList.size(); j++) {
//				Logger.i(TAG, "---number:" + mobileContactList.get(j).getPhonenumber() + "--name:" + mobileContactList.get(j).getPhonename());
//			}
		}
	}

	/**
	 * 批量添加好友
	 */
	private void addFriendsByPhonenumbers(String userid, String ids) {
		Logger.i(TAG, "---userid" + userid);
		Logger.i(TAG, "---phonenumbers" + ids);
		try {
			new AddFriendsByPhonenumbersTask().execute(userid, ids);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 获取userid
	 */
	private String getUserId() {
		return PreferencesUtils.getString(this, SharedPreferredKey.USERID, "");
	}

	/**
	 * 批量添加好友，未全部成功
	 */
	private void noAddAllSuccess(String friendNumbers, String sendNumbers) {

		if (mContactAdapter != null) {
			try {
				mContactAdapter.noAddAllSuccess(friendNumbers, sendNumbers, mIndexs);
				mContactAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * 匹配通讯录
	 */
	private class CheckPhoneNumbersTask extends AsyncTask<String, Null, Integer> {

		public CheckPhoneNumbersTask() {
			showProgress(getResources().getString(R.string.text_wait));
		}

		@Override
		protected Integer doInBackground(String... params) {
			ContactListInfo data = new ContactListInfo();
			if (StringUtils.isBlank(mPhonenumbers)) {
				mPhonenumbers = getContactsFromMobile();
			}
			int result = 1;
			if (StringUtils.isNotBlank(params[0])) {
				result = DataSyn.getInstance().checkPhonenumbers(params[0], mPhonenumbers, data);
			}

			if (data != null && data.datavalue.size() > 0) {
				mContactList = data.datavalue;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			close();
			if (result == 0) {
				combineList(mContactList, mMobileContactList);
				initList();
			} else {
				ToastUtils.showToast(getApplicationContext(), "匹配失败");
			}
		}
	}

	/**
	 * 批量添加好友
	 */
	private class AddFriendsByPhonenumbersTask extends AsyncTask<String, Null, MultAddFriendsBackInfo> {

		public AddFriendsByPhonenumbersTask() {
			showProgress(getResources().getString(R.string.text_wait));
		}

		@Override
		protected MultAddFriendsBackInfo doInBackground(String... params) {
			MultAddFriendsBackInfo data = new MultAddFriendsBackInfo();
			int result = 1;
			if (StringUtils.isNotBlank(params[0])) {
				result = DataSyn.getInstance().addFriendsByPhonenumbers(params[0], params[1], data);
			}

			if (result == 0) {
				return data;
			}

			return null;
		}

		@Override
		protected void onPostExecute(MultAddFriendsBackInfo result) {
			super.onPostExecute(result);
			close();
			if (result != null) {
				if (result.status.equals("SUCCESS")) {
					if (StringUtils.isBlank(result.friendNumbers) && StringUtils.isBlank(result.sendNumbers)) {
						ToastUtils.showToast(getApplicationContext(), "添加成功");
						mActivity.finish();
					} else {
//						ToastUtils.showToast(getApplicationContext(), "部分好友添加失败");
						noAddAllSuccess(result.friendNumbers, result.sendNumbers);
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "添加失败");
				}

			} else {
				ToastUtils.showToast(getApplicationContext(), "添加失败");
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
