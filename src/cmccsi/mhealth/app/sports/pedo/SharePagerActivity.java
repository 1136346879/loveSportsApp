package cmccsi.mhealth.app.sports.pedo;


import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
/**
 * 进入分享页面
 * @author Kane
 *
 */
public class SharePagerActivity extends Activity {
	
	private ImageButton ib_back;
	private ImageButton ib_share;
	private WebView wb_share;
	private PedometorDataInfo currentPedo;
	private String step;
	private String kcal;
	private String curTime;
	private String distance;
	private String avatarURL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sharepager);
		
		currentPedo = (PedometorDataInfo)getIntent().getParcelableExtra("currentPedo"); 
		curTime = getIntent().getStringExtra("mDisplayDate");
		Log.e("LKH", "mDisplayDate ："+curTime);
		initView();
		initWebView();
		share();
		}

	public void initView(){
		ib_back = (ImageButton) findViewById(R.id.imagebutton_back);
		ib_share = (ImageButton) findViewById(R.id.imagebutton_share);
		ib_share.setVisibility(View.INVISIBLE);
		wb_share = (WebView) findViewById(R.id.webview_share);
		
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wb_share.canGoBack()){
					wb_share.goBack();
				}else{
					SharePagerActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				}
				
			}
		});
		ib_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				share();
			}
		});
	}
	
	public void share(){
		ShareWeiXin share = new ShareWeiXin(SharePagerActivity.this, null, wb_share);
		share.Shared();
	}
	private void initWebView() {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
		Date curDate = new  Date(System.currentTimeMillis());//获取当前时间      
		
	   avatarURL = PreferencesUtils.getString(this,
				SharedPreferredKey.AVATAR, "lq3hao");
	   
	   Log.e("lkh", "userId : "+avatarURL);
		if(currentPedo != null){
			step = currentPedo.stepNum;
			kcal = currentPedo.cal;
			distance = currentPedo.distance;
		}else{
			step = "0";
			kcal = "0";
			distance = "0"; 
		}
		if(curTime == null){
			curTime = formatter.format(curDate);
		}
		Log.e("lkh","step:"+step);
		Log.e("lkh","kcal:"+kcal);
		Log.e("lkh","curTime:"+curTime);
		Log.e("lkh","distance:"+distance);
		String url =" file:///android_asset/shareWeiXin.htm"
				+ "?userId=lq3hao&step="+step+"&"
				+ "kcal="+kcal+"&m="+distance+"&time="+curTime+"&target=oracle&picurl="+avatarURL;
		
		WebSettings settings = wb_share.getSettings();
		settings.setJavaScriptEnabled(true);
		
		//設置屏幕自適應
		//settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		wb_share.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
			}
		});
		wb_share.loadUrl(url);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 200) {
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.e("lkh","KEYCODE_BACK");
		}
		return super.onKeyDown(keyCode, event);
	}
}
