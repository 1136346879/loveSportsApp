package cmccsi.mhealth.app.sports.tabhost;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.activity.MainFragmentActivity;
import cmccsi.mhealth.app.sports.adapter.MessageAdapter;
import cmccsi.mhealth.app.sports.adapter.MessageAdapter.ListItemClickHelp;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.FriendsInfo;
import cmccsi.mhealth.app.sports.bean.RequestData;
import cmccsi.mhealth.app.sports.bean.RequestListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;

public class MessageFragment extends BackHandledFragment implements OnClickListener, IXListViewListener, ListItemClickHelp {
	private final static String TAG = "MessageFragment";

	private TextView mTextViewTitle;// 标题栏
	private ImageView mBack;// 侧滑按钮

	private XListView mMainListView;
	private MessageAdapter mMyAMAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_message, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	private void initView() {
		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.message_title);

		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// getActivity().finish();
				backToMain();
			}
		});

		mMainListView = findView(R.id.am_main_listview);
		mMainListView.setXListViewListener(this);
		mMainListView.setPullLoadEnable(false);
	}

	/**
	 * TODO 跳转进入MainFragmentActivity
	 * 
	 * @return void
	 * @author zhangfengjuan
	 * @time 下午2:11:50
	 */
	private void backToMain() {
		if (isInLauncher()) {
			System.out.println("------11111111111------");
			getActivity().finish();
		} else {
			System.out.println("------2222222222222------");
			Intent intent = new Intent(mActivity, MainFragmentActivity.class);
			// Intent intent = new Intent(this, MainFragmentActivity2.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
		}
	}

	/**
	 * TODO 栈中是否包含MainFragmentActivity
	 * 
	 * @return
	 * @return boolean
	 * @author zhangfengjuan
	 * @time 下午2:08:14
	 */
	private boolean isInLauncher() {
//		ActivityManager mAm = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> taskList = mAm.getRunningTasks(100);
//		for (RunningTaskInfo rti : taskList) {
//			String name = rti.baseActivity.getClassName();
//			String name2 = rti.topActivity.getClassName();
//			if (name.equals("cmccsi.mhealth.portal.sports.tabhost.TabBaseFragment")
//					|| name2.equals("cmccsi.mhealth.portal.sports.tabhost.TabBaseFragment")) {
//				return true;
//			}
//			// if
//			// (name.equals("cmccsi.mhealth.app.sports.activity.MainFragmentActivity2")
//			// ||
//			// name2.equals("cmccsi.mhealth.app.sports.activity.MainFragmentActivity2"))
//			// {
//			// return true;
//			// }
//		}
		System.out.println("----BaseActivity.allActivity-------"+BaseActivity.allActivity.size());
		System.out.println("----BaseActivity.allActivity-------"+BaseActivity.allActivity.toString());
		for (Activity activity : BaseActivity.allActivity) {
			System.out.println("----------:"+activity.getLocalClassName());
			if (activity.getLocalClassName().equals("activity.MainFragmentActivity")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:

			break;
		}
	}

	@Override
	public void findViews() {
		initView();
		setMessages(MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyOldMsgs());
		mMainListView.startLoading(Common.getDensity(mActivity) * 60);
	}

	private RequestListInfo mFriendReqData;// 好友请求列表
	private RequestListInfo mRaceReqData;// 比赛请求列表

	private void getMessages() {
		new Thread() {
			public void run() {
				mFriendReqData = new RequestListInfo();
				mRaceReqData = new RequestListInfo();
				String pn = sp.getString(SharedPreferredKey.PHONENUM, "");
				String pwd = sp.getString(SharedPreferredKey.PASSWORD, "");
				int suc = DataSyn.getInstance().getFriendRequestList(pn, pwd, mFriendReqData);
				suc *= DataSyn.getInstance().getRaceInvitedRequestList(pn, pwd, mRaceReqData);
				if (suc == 0) {
					handler.sendEmptyMessage(GET_MESSAGE_SUCCESS);
				} else {
					handler.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
	}

	// 静态int
	private static final int GET_MESSAGE_SUCCESS = 0; // 查找好友成功
	private static final int NET_PROBLEM = 1;// 网络问题
	private static final int ACCEPT_SUCCESS = 2;// 网络问题
	private static final int NO_MESSAGE = 3;// 网络问题
	private CustomProgressDialog mProgressDialog;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			Bundle data = msg.getData();
			BackInfo bi = data.getParcelable("backinfo");
			switch (msg.what) {
			case GET_MESSAGE_SUCCESS:
				setMessages();
				break;
			case NET_PROBLEM:
				ToastUtils.showToast(getActivity(), R.string.MESSAGE_INTERNET_ERROR);
				resetXListView();
				break;
			case ACCEPT_SUCCESS:
				BaseToast(bi.reason == null ? "操作成功" : bi.reason, 5);
				RequestData requestData = data.getParcelable("requestdata");
				// TODO 区别是否加入成功
				if ("加入成功".equals(bi.reason) || "好友关系已经建立".equals(bi.reason)) {
					if ("好友关系已经建立".equals(bi.reason)) {
						Editor edit = sp.edit();
						edit.remove(SharedPreferredKey.FRIEND_GETTIME);
						edit.commit();
					}
					MHealthProviderMetaData.GetMHealthProvider(mActivity).OldMsgsInsertValue(requestData);
				}
				getMessages();
				break;
			case NO_MESSAGE:
				BaseToast("目前您还没有消息哦！", 5);
				break;
			}
		}
	};

	// 设置信息
	private void setMessages() {
		setMessages(null);
	}

	private void setMessages(List<RequestData> existDatas) {
		resetXListView();
		RequestListInfo allReqData = new RequestListInfo();
		if (existDatas == null) {
			if (mFriendReqData != null && mFriendReqData.dataValue.size() > 0) {
				allReqData.dataValue.addAll(mFriendReqData.dataValue);
			}
			if (mRaceReqData != null && mRaceReqData.dataValue.size() > 0) {
				allReqData.dataValue.addAll(mRaceReqData.dataValue);
			}

			List<RequestData> myOldMsgs = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyOldMsgs();
			allReqData.dataValue.addAll(myOldMsgs);
			if (allReqData.dataValue.size() == 0) {
				handler.sendEmptyMessage(NO_MESSAGE);
			}
		} else {
			allReqData.dataValue.addAll(existDatas);
		}
		if ((mFriendReqData != null && mFriendReqData.dataValue.size() > 0)
				|| (mRaceReqData != null && mRaceReqData.dataValue.size() > 0)) {
			PreferencesUtils.putBoolean(mActivity, "ISHAVENEWMSG", true);
		} else {
			PreferencesUtils.putBoolean(mActivity, "ISHAVENEWMSG", false);
		}
		if (mMyAMAdapter == null) {
			mMyAMAdapter = new MessageAdapter(mActivity, allReqData, MessageFragment.this);
			mMainListView.setAdapter(mMyAMAdapter);
		} else {
			mMyAMAdapter.setFrliReqData(allReqData, MessageFragment.this);
			mMyAMAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void clickListner() {
	}

	@Override
	public void loadLogic() {
	}

	private void acceptFriendRequest(final RequestData requestData) {
		mProgressDialog = Common.initProgressDialog("建立关系中...", mActivity);
		new Thread() {
			public void run() {
				BackInfo bi = new BackInfo();
				String pn = sp.getString(SharedPreferredKey.PHONENUM, "");
				String pwd = sp.getString(SharedPreferredKey.PASSWORD, "");
				int suc = DataSyn.getInstance().acceptRequest(pn, pwd, requestData.getPhonenum(), bi);
				if (suc == 0) {
					FriendsInfo mOrgnizeMemberPKInfo = new FriendsInfo();
					int res = DataSyn.getInstance().getFriendsList(mOrgnizeMemberPKInfo);
					if (res == 0) {
						MHealthProviderMetaData mMHealthProvider = MHealthProviderMetaData.GetMHealthProvider(getActivity());
						mMHealthProvider.deleteMyFriend();
						mMHealthProvider.FriendInsertValue(mOrgnizeMemberPKInfo.friendslist);
						Message msg = Message.obtain();
						Bundle data = new Bundle();
						data.putParcelable("backinfo", bi);
						data.putParcelable("requestdata", requestData);
						msg.setData(data);
						msg.what = ACCEPT_SUCCESS;
						handler.sendMessage(msg);
					}
				} else {
					handler.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
	}

	private void acceptRaceRequest(final RequestData requestData) {
		mProgressDialog = Common.initProgressDialog("加入中...", mActivity);
		new Thread() {
			public void run() {
				BackInfo bi = new BackInfo();
				String pn = sp.getString("PHONENUM", "");
				String pwd = sp.getString("PASSWORD", "");
				int suc = DataSyn.getInstance().acceptRaceInvitingRequest(pn, pwd, requestData.getRaceid(), "", bi);
				if (suc == 0) {
					Message msg = Message.obtain();
					Bundle data = new Bundle();
					data.putParcelable("backinfo", bi);
					data.putParcelable("requestdata", requestData);
					msg.setData(data);
					msg.what = ACCEPT_SUCCESS;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	public void onRefresh() {
		getMessages();
	}

	private void resetXListView() {
		mMainListView.stopRefresh();
		mMainListView.stopLoadMore();
		mMainListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	@Override
	public void onClick(RequestData requestData) {
		if (!requestData.isIsoldmsgs()) {
			if (TextUtils.isEmpty(requestData.getRaceid())) {
				acceptFriendRequest(requestData);
			} else {
				acceptRaceRequest(requestData);
			}
		}
	}

	private boolean hadIntercept;

	@Override
	protected boolean onBackPressed() {
		// TODO Auto-generated method stub
		if (hadIntercept) {
			return false;
		} else {
			System.out.println("--------anlefanhui -------");
			hadIntercept = true;
			backToMain();
			return true;
		}
	}

}
