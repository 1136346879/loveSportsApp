package cmccsi.mhealth.app.sports.basic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;

public class SampleActivity extends BaseActivity {
	protected TextView mTextViewTitle;// 标题栏
	protected ImageView mBack;// 侧滑按钮
	protected SharedPreferences sp;
	
	protected String mPhoneNum; //手机号
	protected String mPassword;//密码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		super.onCreate(savedInstanceState);
		initSampleViews();
		initClickers();
	}
	//初始化通用view
	private void initSampleViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
		mTextViewTitle.setText(getIntent().getExtras().getString("sampletitle"));
		mBack = (ImageView) findViewById(R.id.button_input_bg_back);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setVisibility(View.VISIBLE);
	}
	//初始化点击事件
	protected void initClickers() {
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SampleActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}
	
	private Toast mToast = null;

	protected void BaseToast(String msg) {
		BaseToast(msg, 0);
	}
	protected void BaseToast(String msg, int l) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), msg, l);
		} else {
			mToast.setText(msg);
			mToast.setDuration(l);
		}
		mToast.show();
	}
	
	private void loadNessesaryInfo() {
		mPhoneNum = sp.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
	}
	
	@Override
	protected void onResume() {
		loadNessesaryInfo();
		String selectedserver = sp.getString(SharedPreferredKey.SERVER_NAME, "");
		if (null != selectedserver && !"".equals(selectedserver)) {
			DataSyn.setStrHttpURL("http://" + selectedserver + "openClientApi.do?action=");
			DataSyn.setAvatarHttpURL("http://" + selectedserver + "UserAvatar/");
		}
		ConstantsBitmaps.initRunPics(SampleActivity.this);
		super.onResume();
	}

}
