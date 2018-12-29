package cmccsi.mhealth.app.sports.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

/**
 * 展示服务器界面的webview公共类
 * @type WebViewActivity
 * TODO
 * @author shaoting.chen
 * @time 2015年10月27日上午10:32:33
 */
public class WebViewActivity extends BaseActivity {
	private static final String TAG = "WebViewActivity";
	private TextView mTextViewTitle;
	private WebView mWebView;
	private CustomProgressDialog mProgressDialog = null;
	private String strURL="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_help);
		initView();
		logic();
	}

	private void logic() {
		showProgressDialog(getResources().getString(R.string.text_wait));
		mWebView = (WebView) findViewById(R.id.webview_help);
		// 设置WebView属性，能够执行Javascript脚本
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 让网页自适应屏幕宽度
//		WebSettings webSettings = mWebView.getSettings(); // webView:
		// 类WebView的实例
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// 设置可以支持缩放
		// webSettings.setSupportZoom(true);
//		webSettings.setBuiltInZoomControls(true); // 设置出现缩放工具
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 当前页面加载网页
				if (url.indexOf("tel:") < 0) {// 页面上有数字会导致连接电话
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// Toast.makeText(WebViewActivity.this, "加载完成", 0).show();
				dismiss();
				super.onPageFinished(view, url);
			}

		});
		mWebView.addJavascriptInterface(this, "demo");
		mWebView.requestFocus();
		// 加载需要显示的网页
		Intent intent = getIntent();
		strURL = intent.getStringExtra("UserInfo");
		
		String title = intent.getStringExtra("title");
		mTextViewTitle.setText(title);
		Logger.i(TAG, "---strURL " + strURL);
		
		// 1、判断是否有网或者URL为空
		if (NetworkTool.getNetworkState(getApplicationContext()) != 0 && StringUtils.isNotBlank(strURL)) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					final boolean checded = checkURL(strURL);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 2、判断URL是否正确
							if (checded) {
								mWebView.loadUrl(strURL);
							} else {
								dismiss();
								ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
							}
						}
					});
				}
			}).start();
		} else {
			dismiss();
			ToastUtils.showToast(getApplicationContext(), R.string.MESSAGE_INTERNET_ERROR);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.requestFocus();
	};

	Handler handler = new Handler();
	private ImageButton mBack;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mWebView.canGoBack()){
				mWebView.goBack();
			}else{
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
			Log.i("onkeydowm", mWebView.isFocusable() + "");
			// goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}

	private void initView() {
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);

		mBack = (ImageButton) findViewById(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()){
					mWebView.goBack();
				}else{
					WebViewActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				}
				
			}
		});
	}

	
	/**
	 * js信息提示调此方法
	 */
	public void aMessage(String i) {
		System.out.println("----------aMessage-------------------");
		Toast.makeText(WebViewActivity.this, i, 0).show();
	}
	
	public void clickOnAndroid(){
		Toast.makeText(WebViewActivity.this,"激活..", 0).show();
	}

	/**
	 * js调此方法显示dialog
	 * 
	 * @param msg
	 */
	public void showProgressDialog(String msg) {
		if ((!isFinishing()) && (this.mProgressDialog == null)) {
			//this.mProgressDialog = new ProgressDialog(this);
			this.mProgressDialog = CustomProgressDialog.createDialog(this);  
		}
		this.mProgressDialog.setMessage(msg);
		this.mProgressDialog.show();
	}

	/**
	 * js调此方法显示dialog
	 */
	protected void dismiss() {
		if ((!isFinishing()) && (this.mProgressDialog != null)) {
			this.mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy", " --- onDestroy");
		// 清楚缓存
//		mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
	}

	/**
	 * js激活成功调此方法
	 */
	public void finsh() {
		setResult(RESULT_OK);
		WebViewActivity.this.finish();
		if(!strURL.contains("phoneReg")){
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
		}else{
		    Toast.makeText(WebViewActivity.this, "注册成功，请登录", 0).show();
		}
	}
	
	/**
	 * 检查URL是否存在（可正确联通并获取返回值200ok）
	 * TODO
	 * @param url
	 * @return
	 * @return boolean
	 * @author shaoting.chen
	 * @time 上午10:33:38
	 */
	private boolean checkURL(String url){
		boolean value=false;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			int code = conn.getResponseCode();
			Logger.i(TAG, "---URL CODE" + code);
			if (code != 200) {
				value = false;
			} else {
				value = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
}
