package cmccsi.mhealth.app.sports.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cmccsi.mhealth.app.sports.net.NetworkTool;

public abstract class BaseWebViewFragment extends BaseFragment implements WebAppInterface{
	
	private static WebView rootWebView;
	protected String rootURL;
	
	protected void logicMethod() {
		rootWebView = setWebView();
		
		/****************************************WebView页面缓存的设置************************/
		WebSettings settings = rootWebView.getSettings();
		rootWebView.setHapticFeedbackEnabled(true);
		//开启JavaScript设置
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小，我设的是8M
		settings.setAllowFileAccess(true);           // 可以读取文件缓存
		// 应用可以有缓存
		settings.setAppCacheEnabled(true);
		String appCacheDir = rootWebView.getContext().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCacheDir);
		// 应用可以有数据库
		settings.setDatabaseEnabled(true);
		settings.setDatabasePath("/data/data/" + rootWebView.getContext().getPackageName() + "/databases/");  //TODO 
		
		rootWebView.addJavascriptInterface(this, "demo");
		rootWebView.requestFocus();
		if(NetworkTool.getNetworkState(rootWebView.getContext()) == NetworkTool.NONE){
			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}else{
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);   // 默认使用缓存
		}
		/**********************************************************************************/
		
		rootWebView.setWebViewClient(new WebViewClient(){
			
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
				//TODO 开始加载页面
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				//TODO 页面读取完毕,取消进度的显示
				super.onPageFinished(view, url);
			}
		});
		
		rootURL = setURL();
		rootWebView.loadUrl(rootURL);
	}
	
	protected abstract WebView setWebView();
	
	protected abstract String setURL();
	
	protected WebView getWebView(){
		if(rootWebView != null){
			return rootWebView;
		}
		throw new IllegalArgumentException("WebView为null");
	}
	
	@Override
	public void javaCallJS() {
		getWebView().loadUrl("javascript:nativeToWeb()");
	}

	@Override
	public void javaCallJS(String data) {
		getWebView().loadUrl("javascript:nativeToWeb('"+data+"')");
	}
	
	@Override
	public void jsCallJava() {}

	@Override
	public void jsCallJava(String data) {}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
