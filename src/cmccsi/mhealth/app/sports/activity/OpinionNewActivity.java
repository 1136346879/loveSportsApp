package cmccsi.mhealth.app.sports.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.appversion.WebServiceManage;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.R;

/**
 * 新提意见页面
 * 
 * @author 志伟
 *
 */
public class OpinionNewActivity extends BaseActivity {
	private Button submitOpinion; // 提交按钮
	private Spinner mSpinner;
	private EditText title;
	private EditText content;
	private EditText phone;
	private String titleString;
	private String contentString;
	private String phoneString;
	private int feedbackTypeDict;
	private PutNetMsgThread putNetMsgThread = null; // 获取网络数据的线程
	private String USERUID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_new);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 返回键处理
		BaseBackKey("意见反馈", OpinionNewActivity.this);
		// 新建意见处理
		submitOpinion = (Button) findViewById(R.id.button_add);
		submitOpinion.setText("提交");
		submitOpinion.setTextSize(18);
		submitOpinion.setTextColor(Color.WHITE);
		submitOpinion.setBackgroundColor(Color.rgb(122, 175, 59));// color="#7aaf3b"
		submitOpinion.setVisibility(View.VISIBLE);
		submitOpinion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chick();
			}

		});
		mSpinner = (Spinner) findViewById(R.id.opinion_new_typedict);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.opinion_spinner_item);
		String level[] = { "请选择意见类型", "改进建议", "业务咨询", "问题投诉" };
		for (int i = 0; i < level.length; i++) {
			adapter.add(level[i]);
		}
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(R.layout.opinion_simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				feedbackTypeDict = position - 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				feedbackTypeDict = -1;
			}
		});
		title = (EditText) findViewById(R.id.opinion_new_title);
		content = (EditText) findViewById(R.id.opinion_new_content);
		phone = (EditText) findViewById(R.id.opinion_new_phone);
	}

	private void chick() {
		if (-1 == feedbackTypeDict) {
			Toast.makeText(getApplicationContext(), "请选择意见类型！", Toast.LENGTH_LONG).show();
			return;
		}
		titleString = title.getText().toString().trim();
		if (null == titleString || "".equals(titleString)) {
			Toast.makeText(getApplicationContext(), "请输入意见标题！", Toast.LENGTH_LONG).show();
			return;
		}
		contentString = content.getText().toString().trim();
		if (null == contentString || "".equals(contentString)) {
			Toast.makeText(getApplicationContext(), "请输入意见内容！", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			phoneString = phone.getText().toString().trim();
		} catch (Exception e) {
			phoneString = "";
		}
		// 显示loading，按返回不消失
		showProgressDialog(getResources().getString(R.string.text_wait), OpinionNewActivity.this);
		// 获取当前用户信息
		USERUID = PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
		if (null != putNetMsgThread) {
			putNetMsgThread.stopMyThread();
			putNetMsgThread = null;
		}
		putNetMsgThread = new PutNetMsgThread();
		putNetMsgThread.start();
		opinionHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null != opinionHandle) {
					if (null != putNetMsgThread) {
						putNetMsgThread.stopMyThread();
						putNetMsgThread = null;
						opinionHandle.sendEmptyMessage(error1);
					}
				}
			}
		}, 20 * 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 线程的例子
	 * 
	 * @author luckchoudog
	 */
	class PutNetMsgThread extends Thread {
		private boolean isThreadRun = false;

		public PutNetMsgThread() {
		}

		public boolean isThreadRuning() {
			return isThreadRun;
		}

		public void stopMyThread() {
			if (putNetMsgThread == null) {
				return;
			}
			PutNetMsgThread tmpThread = putNetMsgThread;
			putNetMsgThread = null;
			if (tmpThread != null) {
				tmpThread.interrupt();
				isThreadRun = false;
			}
		}

		public void run() {
			if (putNetMsgThread == null) {
				return; // stopped before started.
			}
			try {
				isThreadRun = true;
				HashMap<String, String> arg = new HashMap<String, String>();
				arg.put("uid", USERUID);
				arg.put("feedbackTitle", titleString);
				arg.put("feedbackContent", contentString);
				arg.put("feedbackTypeDict", feedbackTypeDict + "");
				arg.put("contactInfo", phoneString);
				String json = WebServiceManage.post(arg, 7);
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
					opinionHandle.sendEmptyMessage(success);
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
			if (null != putNetMsgThread) {
				putNetMsgThread.stopMyThread();
				putNetMsgThread = null;
			}
			switch (msg.what) {
			case error1:
				ToastUtils.showToast(OpinionNewActivity.this, R.string.MESSAGE_INTERNET_ERROR);
				break;
			case error2:
				Toast.makeText(OpinionNewActivity.this, msg.obj + "", Toast.LENGTH_LONG).show();
				break;
			case success:
				Log.e("opinion", "success ");
				OpinionNewActivity.this.finish();
				break;

			default:
				break;
			}
		}

	};
}
