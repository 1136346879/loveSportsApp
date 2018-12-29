package cmccsi.mhealth.app.sports.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.appversion.OpinionInstance;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.R;

public class OpinionViewActivity extends BaseActivity {
	private Intent fromFather;
	private OpinionInstance opinionInstance;
	private LinearLayout replyLayout;
	private TextView feedbackTitle;
	private TextView feedbackContent;
	private TextView createTime;
	private TextView replyContent;
	private TextView replyTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_view);
		fromFather = getIntent();
		opinionInstance = (OpinionInstance) fromFather.getSerializableExtra(OpinionListActivity.OPINIONLISTITEM);
		if (null == opinionInstance) {
			Toast.makeText(getApplicationContext(), "信息错误！", Toast.LENGTH_LONG).show();
		} else {
			initView();
			initData();
		}
	}

	private void initData() {
		feedbackTitle.setText(opinionInstance.getFeedbackTitle());
		feedbackContent.setText(opinionInstance.getFeedbackContent());
		createTime.setText(opinionInstance.getCreateTime());
		if (1 == opinionInstance.getReplyMark()) {
			replyLayout.setVisibility(View.VISIBLE);
			replyContent.setText(opinionInstance.getReplyContent());
			replyTime.setText(opinionInstance.getReplyTime());
		} else {
			replyLayout.setVisibility(View.GONE);
		}
	}

	private void initView() {
		// 返回键处理
		BaseBackKey("意见反馈", OpinionViewActivity.this);
		replyLayout = (LinearLayout) findViewById(R.id.opinion_view_reply);
		feedbackTitle = (TextView) findViewById(R.id.opinion_view_titel);
		feedbackContent = (TextView) findViewById(R.id.opinion_view_feedbackContent);
		createTime = (TextView) findViewById(R.id.opinion_view_feedbackTime);
		replyContent = (TextView) findViewById(R.id.opinion_view_Content);
		replyTime = (TextView) findViewById(R.id.opinion_view_replyTime);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		opinionInstance = null;
	}

}
