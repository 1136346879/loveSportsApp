package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.adapter.OpinionListAdapter;
import cmccsi.mhealth.app.sports.appversion.OpinionInstance;
import cmccsi.mhealth.app.sports.appversion.WebServiceManage;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;

/**
 * 意见列表页面，显示用户提过的所有问题，可以进入新建意见页面
 * 
 * @author 志伟
 *
 */
public class OpinionListActivity extends BaseActivity {
	private Button newOpinion; // 新建按钮
	private LinearLayout noOpinion;
	private XListView opinions; // 意见列表
	private int OPINIONLISTACTIVITYRESOUTNUM = 0;// 新建意见返回。
	private GetNetMsgThread getNetMsgThread = null; // 获取网络数据的线程
	private int page = 1; // 显示的为第几页
	private String USERUID = "";
	private ArrayList<OpinionInstance> opinionList = new ArrayList<OpinionInstance>(); // 获取到的数据
	private OpinionListAdapter opioionAdapter;
	public static String OPINIONLISTITEM = "OPINIONLISTITEM";
	//加载过程中不能再下拉刷新 lkh
	private boolean canLoadMore = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_list);
		initView();
		getOpinionListFromNet();
	}

	/**
	 * 从网端获取意见列表
	 */
	private void getOpinionListFromNet() {
		// 显示loading，按返回不消失
		showProgressDialog(getResources().getString(R.string.text_wait), OpinionListActivity.this);
		// 获取当前用户信息
		USERUID = PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
		if (null != getNetMsgThread) {
			getNetMsgThread.stopMyThread();
			getNetMsgThread = null;
		}
		getNetMsgThread = new GetNetMsgThread();
		getNetMsgThread.start();
		opinionHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null != getNetMsgThread) {
					opinionHandle.sendEmptyMessage(error1);
				}
			}
		}, 20 * 1000);
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		// 返回键处理
		BaseBackKey("意见反馈", OpinionListActivity.this);
		// 新建意见处理
		newOpinion = (Button) findViewById(R.id.button_add);
		newOpinion.setText("新建");
		newOpinion.setTextSize(18);
		newOpinion.setTextColor(Color.WHITE);
		newOpinion.setBackgroundColor(Color.rgb(122, 175, 59));// 8FC41C
		newOpinion.setVisibility(View.VISIBLE);
		newOpinion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OpinionListActivity.this, OpinionNewActivity.class);
				OpinionListActivity.this.startActivityForResult(intent, OPINIONLISTACTIVITYRESOUTNUM);
			}
		});
		// 无建议显示
		noOpinion = (LinearLayout) findViewById(R.id.opinion_list_nomsg);
		noOpinion.setVisibility(View.GONE);
		// 建议列表操作
		opinions = (XListView) findViewById(R.id.opinion_list_msgs);
		opioionAdapter = new OpinionListAdapter(getApplicationContext(), opinionList);
		opinions.setAdapter(opioionAdapter);
		opinions.setPullRefreshEnable(false);
		opinions.setPullLoadEnable(true);
		opinions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.e("opinion", "position = " + position);
				if (position - 1 > opinionList.size() && null == opinionList.get(position - 1)) {
					return;
				}
				Intent intent = new Intent(OpinionListActivity.this, OpinionViewActivity.class);
				intent.putExtra(OPINIONLISTITEM, opinionList.get(position - 1));
				startActivity(intent);
			}
		});
		opinions.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
			}

			@Override
			public void onLoadMore() {
				if(canLoadMore){
					//可以加载更多
					canLoadMore = false;
					// TODO Auto-generated method stub
					page++;
					USERUID = PreferencesUtils.getString(getApplicationContext(), SharedPreferredKey.USERUID, "");
					if (null != getNetMsgThread) {
						getNetMsgThread.stopMyThread();
						getNetMsgThread = null;
					}
					getNetMsgThread = new GetNetMsgThread();
					getNetMsgThread.start();
					opinionHandle.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (null != opinionHandle) {
								if (null != getNetMsgThread) {
									getNetMsgThread.stopMyThread();
									getNetMsgThread = null;
									opinionHandle.sendEmptyMessage(error1);
								}
							}
						}
					}, 20 * 1000);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("opinion", "resultCode = " + resultCode);
		if (resultCode == OPINIONLISTACTIVITYRESOUTNUM) {
			page = 1;
			opinionList.clear();
			opinions.setPullLoadEnable(true);
			getOpinionListFromNet();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		opinionList.clear();
		if (null != getNetMsgThread) {
			getNetMsgThread.stopMyThread();
		}
		page = 1;
	}

	/**
	 * 线程的例子
	 * 
	 * @author luckchoudog
	 */
	class GetNetMsgThread extends Thread {
		private boolean isThreadRun = false;

		public GetNetMsgThread() {
		}

		public boolean isThreadRuning() {
			return isThreadRun;
		}

		public void stopMyThread() {
			if (getNetMsgThread == null) {
				return;
			}
			GetNetMsgThread tmpThread = getNetMsgThread;
			getNetMsgThread = null;
			if (tmpThread != null) {
				tmpThread.interrupt();
				isThreadRun = false;
			}
		}

		public void run() {
			//opinions.setPullLoadEnable(false);
			if (getNetMsgThread == null) {
				return; // stopped before started.
			}
			try {
				isThreadRun = true;
				HashMap<String, String> arg = new HashMap<String, String>();
				arg.put("uid", USERUID);
				arg.put("page", page + "");
				String json = WebServiceManage.get(arg, 6);
				// json =
				// "{\"status\": \"SUCCESS\",      \"message\": \"意见反馈信息查询成功\",      \"page\": \"1\",      \"datavalue\": [         {             \"feedbackId\": \"432\",              \"feedbackTypeDict\": \"2\",              \"feedbackTitle\": \"爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更爱动力不能更\",              \"feedbackContent\": \"不知道为什么每次只要已更新不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本不知道为什么每次只要已更新就会卡死，H5版本就会卡死，H5版本\",              \"createTime\": \"2015-09-08 16:13:45\",              \"replyMark\": \"0\",              \"replyContent\": \"\",              \"replyTime\": \"\"          },          {             \"feedbackId\": \"431\",              \"feedbackTypeDict\": \"2\",              \"feedbackTitle\": \"计步算法在M812不好使\",              \"feedbackContent\": \"手机计步在M812手机上锁屏不计步\",              \"createTime\": \"2015-09-08 16:13:45\",              \"replyMark\": \"1\",              \"replyContent\": \"会尽快解决\",              \"replyTime\": \"2015-09-09 16:13:45\"          },          {             \"feedbackId\": \"430\",              \"feedbackTypeDict\": \"2\",              \"feedbackTitle\": \"爱动力不能更\",              \"feedbackContent\": \"不知道为什么每次只要已更新就会卡死，H5版本\",              \"createTime\": \"2015-09-08 16:13:45\",              \"replyMark\": \"0\",              \"replyContent\": \"\",              \"replyTime\": \"\"          }     ] } ";
				Log.e("opinion", "json = " + json);
				if (null == json || "".equals(json)) {
					opinionHandle.sendEmptyMessage(error1);// 网络错误
					return;
				}

				JSONObject jsonObject = new JSONObject(json);
				String s = jsonObject.getString("status");
				Log.e("opinion", "s = " + s);
				if (s == null || s.equals("FAILURE")) {// 返回失败
					Message message = new Message();
					message.what = error2;
					message.obj = jsonObject.getString("message");
					opinionHandle.sendMessage(message);
					return;
				} else if (s.equals("SUCCESS")) {// 成功
					JSONArray array = new JSONArray(jsonObject.getString("datavalue"));
					Log.e("opinion", "array = " + array.length());
					for (int i = 0; i < array.length(); i++) {
						JSONObject item = (JSONObject) array.opt(i);
						Log.e("opinion", "array item = " + item);
						opinionList.add(OpinionInstance.paseGetFeedbackList(item));
					}
					Message message = new Message();
					message.what = success;
					message.obj = array.length();
					opinionHandle.sendMessage(message);
				}
				Thread.yield();
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Stopped by ifInterruptedStop()");
				}
			} catch (Throwable t) {
				isThreadRun = false;
				System.out.println("-----------线程干掉------------" + t);
			}
		}
	}

	private static final int error1 = 10000;// 网络错误
	private static final int error2 = 10002;// 返回失败
	private static final int success = 10003;// 返回成功
	@SuppressLint("HandlerLeak")
	private Handler opinionHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismiss();
			opinions.stopLoadMore();
			if (null != getNetMsgThread) {
				getNetMsgThread.stopMyThread();
				getNetMsgThread = null;
			}
			switch (msg.what) {
			case error1:
				if (0 == opinionList.size()) {
					noOpinion.setVisibility(View.GONE);
					opinions.setVisibility(View.GONE);
				} 
				ToastUtils.showToast(OpinionListActivity.this, R.string.MESSAGE_INTERNET_ERROR);
				break;
			case error2:
				if (0 == opinionList.size()) {
					noOpinion.setVisibility(View.VISIBLE);
				}
				Toast.makeText(OpinionListActivity.this, msg.obj + "", Toast.LENGTH_LONG).show();
				break;
			case success:
				// TODO 意见列表接受成功
				if (0 == opinionList.size()) {
					noOpinion.setVisibility(View.VISIBLE);
				} else {
					noOpinion.setVisibility(View.GONE);
					opioionAdapter.setDeviceList(opinionList);
					if ((Integer) msg.obj < 10) {
						opinions.setPullLoadEnable(false);
					}
//					else{
//						opinions.setPullLoadEnable(true);
//					}
				}
				canLoadMore = true;
				Log.e("opinion", "success ");
				break;

			default:
				break;
			}
		}

	};
}
