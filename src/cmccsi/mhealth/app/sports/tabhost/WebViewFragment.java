package cmccsi.mhealth.app.sports.tabhost;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.ShowProgressDialog;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.R;

public class WebViewFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "WebViewFragment";
	private static final int MODIFYED_CALCENDAR_SUCCESS = 10000;
    private static final String TYPENAME[] = { "测血糖", "测血压", "测体重" };
    public static final String ADD_TYPE_NAME[] = { "口服", "注射", "测血糖", "测血压",
            "测体重", "饮食", "运动" };

	private WebView mWebView;
	private String URL;
	private boolean b;
	private String mDate;
	private TextView mTextView;
	private Date currentDatePoint;
	private boolean bIsSubPage = false;
	private TextView mTextViewTimeShowing;
	private View vTimeView;
	private Boolean isNeedCalender = true;
	private ImageButton mBack;
	private ImageView iv_loaderror;
	private boolean errorFlag ;
	private CustomProgressDialog dialog;
	public WebViewFragment(String url, Boolean isNeedCalender) {
		this.URL = url;
        Log.d("knowledge", "知识   " + url);
		this.isNeedCalender = isNeedCalender;
	}

	public void setIsNeedCalender(Boolean isNeedCalender) {
		this.isNeedCalender = isNeedCalender;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_webview, container,
				false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dialog = CustomProgressDialog.createDialog(getActivity(), true);
		dialog.show();
	}
	
	@Override
	public void findViews() {
		// LinearLayout ll_cal = findView(R.id.webview_calender);
		// ll_cal.setVisibility(isNeedCalender?View.VISIBLE:View.GONE);
//		mBack = findView(R.id.button_input_bg_back);
//		mBack.setOnClickListener(this);
//		mBack.setBackgroundResource(R.drawable.my_button_back);
//		mBack.setVisibility(View.VISIBLE);

        iv_loaderror = findView(R.id.iv_loaderror);
		mTextViewTimeShowing = findView(R.id.af_timeshowingtext);

        String frt = sp.getString("top10_reflesh_time", "还未刷新");
		mTextViewTimeShowing.setText(frt);
		vTimeView = (View) findView(R.id.af_timeshowing);
		mWebView = findView(R.id.fragment_webview);
		mWebView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
						mWebView.goBack();
						return true;
				    }
				return false;
			}
		});
		// mLinealayout_time = findView(R.id.linealayout_time);
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mImageButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mImageButtonBack.setVisibility(View.INVISIBLE);
				/*if (bIsSubPage) {
					mImageButtonBack
							.setBackgroundResource(R.drawable.my_button_back);
					bIsSubPage = false;
					// old gengqi
					// mWebView.loadUrl("http://mhealth.cmri.cn/iactivity/account.do?action=knowledge");
					mWebView.loadUrl("http://111.11.29.83:8099/data_new/account.do?action=knowledge");*/
				if(mWebView.canGoBack()){
					mWebView.goBack();
					Logger.i("zhishi","aaaaaa");
				} else {
					getActivity().finish();
				}
			}
		});
		// titleRight.setVisibility(View.VISIBLE);
		// titleRight.setOnClickListener(this);
		// mTextViewImg = findView(R.id.textview_title_add);
		// mTextViewImg.setVisibility(View.GONE);
		// titleRight.setBackgroundResource(R.drawable.img_add);
		//
		// mButtonTime = findView(R.id.imgbn_time);
		// mButtonPreDate = findView(R.id.imgbn_predate);
		// mButtonNextDate = findView(R.id.imgbn_nextdate);
		// mButtonNextDate.setVisibility(View.INVISIBLE);
		//
		// mTextView = findView(R.id.txtv_time);
	}

	@Override
	public void clickListner() {
		// mLinealayout_time.setOnClickListener(this);
		// mButtonPreDate.setOnClickListener(this);
		// mButtonNextDate.setOnClickListener(this);

        // gengqi 改过
		// showMenu();
	}

	@Override
	public void loadLogic() {
	    if (!NetworkTool.isOnline(mActivity)) {
	    	iv_loaderror.setVisibility(View.VISIBLE);
//	    	BaseToast("网络异常，请检查网络设置");
        }
        //ShowProgressDialog.showProgressDialog("正在加载", mActivity);
        // 设置WebView属性，能够执行Javascript脚本
		// if (currentDatePoint == null) {
		// currentDatePoint = new Date();
		// }
		// mTextView.setText(Common.getDate2Time(currentDatePoint,
		// "yyyy-MM-dd"));
		WebSettings settings = mWebView.getSettings();
		mWebView.setHapticFeedbackEnabled(true);
		settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小，我设的是8M
		String appCacheDir = getActivity().getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCacheDir);
		settings.setAllowFileAccess(true);
		settings.setDatabasePath("/data/data/"
				+ mWebView.getContext().getPackageName() + "/databases/");
		settings.setJavaScriptEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		mWebView.addJavascriptInterface(this, "demo");
		mWebView.requestFocus();
		if (NetworkTool.getNetworkState(getActivity()) == 0)
			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		else
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 当前页面加载网页
                if (url.indexOf("tel:") < 0) {// 页面上有数字会导致连接电话
					view.loadUrl(url);
				}
				if (url.contains("ydjbzs_") || url.contains("ydzsk_")
						|| url.contains("hwfx_") || url.contains("rcydzd_")
                        || url.contains("ydcf_")) {// 页面上有数字会导致连接电话
					view.loadUrl(url);
					mImageButtonBack
							.setBackgroundResource(R.drawable.my_button_back);
					bIsSubPage = true;
					// mImageButtonBack.setBackgroundResource(R.drawable.slidemenu_button);

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
//                ShowProgressDialog.dismiss();
				if(dialog!=null && dialog.isShowing()) {
					dialog.dismiss();
				}
				super.onPageFinished(view, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
			        String description, String failingUrl) {
                view.stopLoading();
                view.clearView();
			}

		});
        // 加载需要显示的网页
		if (URL.contains("knowledge")) {
            mTextViewTitle.setText("知识");
			vTimeView.setVisibility(View.INVISIBLE);
			mTextViewTimeShowing.setVisibility(View.GONE);
			vTimeView.setVisibility(View.GONE);

		} else if (URL.contains("friend")) {
            mTextViewTitle.setText("好友");
		} else {

			vTimeView.setVisibility(View.VISIBLE);
            mTextViewTitle.setText("每日达人");
            String sdf = new SimpleDateFormat("yyyy年MM月dd日(全球爱动力用户的前十名)")
					.format(new Date().getTime() - 86400000);
			Editor edit = sp.edit();
			edit.putString("top10_reflesh_time", sdf);
			edit.commit();
			mTextViewTimeShowing.setText(sdf);
		}
		System.out.println(URL);
		mWebView.loadUrl(URL);
	}

	public String js() {
        Toast.makeText(getActivity(), "js 调用客户端", 1).show();
		return "YES";
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("onDestroy", " --- onDestroy");
	}

	public String selectDate() {
		if (b) {
			b = false;
			return mDate;
		} else {
			return "";
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == FragmentActivity.RESULT_OK && requestCode == 200) {
			long sdate = data.getExtras().getLong("time");
			currentDatePoint.setTime(sdate);
			if (currentDatePoint.getDate() == (new Date()).getDate()) {
				// mButtonNextDate.setVisibility(View.INVISIBLE);
			} else {
				// mButtonNextDate.setVisibility(View.VISIBLE);
			}
			String time = Common.getCurrentDayLongTime(sdate);
			mTextView.setText(time);
			String URL = "javascript:nativeToWeb('0;" + time + "')";
			mWebView.loadUrl(URL);
		} else if (resultCode == FragmentActivity.RESULT_OK
				&& requestCode == 201) {
			String url = "javascript:nativeToWeb('0;')";
			mWebView.loadUrl(url);
		}
	}

	private void modifyDay(boolean pre) {
		// if((currentDatePoint.getDate()==(new Date()).getDate())&&(!pre))
		// {
		// return;
		// }
		Calendar modiCal = Calendar.getInstance();
		modiCal.setTime(currentDatePoint);

		modiCal.add(Calendar.DAY_OF_MONTH, pre ? -1 : 1);
		currentDatePoint.setTime(modiCal.getTimeInMillis());
		handler.sendEmptyMessage(MODIFYED_CALCENDAR_SUCCESS);
	}

	private Handler handler = new Handler() {
		// public void dispatchMessage(android.os.Message msg) {
		// Bundle data = msg.getData();
		// String typeName = data.getString("typeName");
		// switch (msg.what) {
		// case 0:
		// int p_id = data.getInt("id");
		// for (int i = 0; i < ADD_TYPE_NAME.length; i++) {
		// if (ADD_TYPE_NAME[i].equals(typeName)) {
		// jumpToIndexAddingPath(i,p_id);
		// }
		// }
		// break;
		// case 1:
		// for (int i = 0; i < TYPENAME.length; i++) {
		// if (TYPENAME[i].equals(typeName)) {
		// Fragment newContent = new VitalSignFragment("", i);
		// MainCenterActivity fca = (MainCenterActivity) getActivity();
		// fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment)
		// newContent);
		// Editor edit = sp.edit();
		// fca.setMenuSelectedItem(2);
		// edit.putInt("mSelected", 2);
		// edit.commit();
		// }
		// }
		// break;
		// case MODIFYED_CALCENDAR_SUCCESS:
		// mTextView.setText(Common.getDate2Time(currentDatePoint,
		// "yyyy-MM-dd"));
		// String time =
		// Common.getCurrentDayLongTime(currentDatePoint.getTime());
		// String URL = "javascript:nativeToWeb('0;" + time + "')";
		// mWebView.loadUrl(URL);
		// break;
		// }
		// };
	};

	public void jumpToIndexAddingPath(int position, int id) {
		Intent intent;
		// switch (position) {
		// case 1:
		// intent = new Intent();
		// intent.setClass(mActivity, SelectDateActivity.class);
		// startActivity(intent);
		// mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		// break;
		// case 2:
		// startActivityForResult(new Intent(mActivity,
		// UpdateBloodSugarActivity.class).putExtra("type",0).putExtra("id",
		// id), 200);
		// // intentActivity(AddScheduleActivity.this,
		// // AddBloodSugarActivity.class, null, false);
		// mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		// break;
		// case 3:
		// intent = new Intent();
		// intent.setClass(mActivity, AddBloodActivity.class);
		// startActivity(intent);
		// mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		// break;
		// default:
		// break;
		// }
		// intent.putExtra("UserInfo",
		// "http://phr.cmri.cn/datav1/client.do?action=medicine&phone=18310993809&type="
		// + position);
		//
		// intent.putExtra("title", ADD_TYPE_NAME[position]);
		// intent.putExtra("show_right", "true");
		//
		// startActivity(intent);
		mActivity.overridePendingTransition(R.anim.slide_in_right,
				R.anim.silde_out_left);
		//
	}

	public void scheduleToTrend(String typeName, int arg, int id) {
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString("typeName", typeName);
		data.putInt("id", id);
		msg.setData(data);
		msg.what = arg;
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button_input_bg_back) {
			Logger.i("zhishi", mWebView.canGoBack()+"");
			if(mWebView.canGoBack()){
				mWebView.goBack();
				Logger.i("zhishi","aaaaaa");
			}
			else
			{
				getActivity().finish();
			}
		}
	}

}
