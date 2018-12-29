package cmccsi.mhealth.app.sports.basic;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;


public abstract class BaseFragment extends Fragment {
	private final static String TAG = "BaseFragment";

	protected FragmentActivity mActivity;
	protected View mView;
	protected SharedPreferences mSharedInfo;
	protected SharedPreferences sp;

	protected OnFragmentDestroyListener dlistener;

	/**
	 * @author qjj
	 * @创建时间：2013-8-28 上午9:39:33
	 * @修改人：qjj
	 * @修改时间：2013-8-28 上午9:39:33
	 */
	public abstract void findViews();

	/**
	 * @创建人：qjj
	 * @创建时间：2013-8-28 上午9:40:26
	 * @修改人：qjj
	 * @修改时间：2013-8-28 上午9:40:26
	 */
	public abstract void clickListner();

	/**
	 * @loadLogic(逻辑处理) qjj
	 * @创建时间：2013-8-28 上午9:40:48
	 * @修改人：qjj
	 * @修改时间：2013-8-28 上午9:40:48
	 */
	public abstract void loadLogic();

	public BaseFragment() {
		super();
	}

	private Toast mToast = null;

	protected TextView mTextViewTitle;

	protected ImageView mImageButtonBack;

	protected void BaseToast(String msg) {
		BaseToast(msg, 0);
	}

	protected void BaseToast(String msg, int time) {
		if (mToast == null) {
			mToast = Toast.makeText(mActivity, msg, time);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	protected SharedPreferences getSharedPreferences(String name, int mode) {
		if (mSharedInfo == null)
			mSharedInfo = mActivity.getSharedPreferences(name, mode);
		return mSharedInfo;
	}

	public SharedPreferences getSharedPreferences() {
		if (mSharedInfo == null)
			mSharedInfo = mActivity.getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, 0);
		return mSharedInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		mView = container;
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			initView();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void initView() {
		sp = getSharedPreferences();
		mTextViewTitle = findView(R.id.textView_title);
		mImageButtonBack = findView(R.id.button_input_bg_back);
		findViews();
		clickListner();
		loadLogic();
	}

	/**
	 * 自定义Toast
	 * 
	 * @param s为传入的字符串
	 */
	protected void toast(String s) {
		Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
	}

	public Activity getMyActivity() {
		return mActivity;
	}

	/**
	 * messagesManager(子线程调用此方法可直接Toast而无需要handle发消息) what 消息code
	 */
	protected void messagesManager(int what) {
		Message message = Message.obtain();
		message.what = what;
		mHandler.sendMessage(message);
	}

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String m = "";
			m = mActivity.getString(msg.what);
			toast(String.valueOf(m));
		};
	};

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
		return (T) mView.findViewById(id);
	}

	public XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setPanEnabled(false, false);// 锁住上下 左右移动
		renderer.setZoomEnabled(false, false);

		renderer.setLabelsColor(Color.BLACK);// x y 坐标颜色
		renderer.setAxesColor(Color.BLACK); // x y 轴颜色
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0)); // 背景透明
		renderer.setMargins(new int[] { Common.dip2px(getMyActivity(), 10),
				Common.dip2px(getMyActivity(), 5),
				Common.dip2px(getMyActivity(), 10),
				Common.dip2px(getMyActivity(), 5) });

		renderer.setAxisTitleTextSize(Common.dip2px(getMyActivity(), 8));
		renderer.setChartTitleTextSize(Common.dip2px(getMyActivity(), 8));
		renderer.setLegendTextSize(Common.dip2px(getMyActivity(), 13));
		renderer.setLabelsTextSize(Common.dip2px(getMyActivity(), 13));

		renderer.setShowGridX(true);
		renderer.setGridColor(Color.GRAY);

		renderer.setYLabels(Common.dip2px(getMyActivity(), 3));
		renderer.setYLabelsAlign(Align.LEFT); // ? y轴标尺向右
		renderer.setShowAxes(false);

		return renderer;
	}

	@Override
	public void onResume() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			String selectedserver = sp.getString(
					SharedPreferredKey.SERVER_NAME, "");
			if (null != selectedserver && !"".equals(selectedserver)) {
				DataSyn.setStrHttpURL("http://" + selectedserver
						+ "openClientApi.do?action=");
				DataSyn.setAvatarHttpURL("http://" + selectedserver
						+ "UserAvatar/");
			}
//			/**
//			 * 友盟服务端反馈信息监听
//			 */
//			UMFeedbackService.enableNewReplyNotification(mActivity,
//					NotificationType.NotificationBar);
		}
			super.onResume();
		
	}

	@Override
	public void onDestroy() {
		if (dlistener != null) {
			dlistener.onDestroy();
		}
//		if (this instanceof RankFragment) {
//			RankFragment.mCurrIndex = 0;
//			RankFragment.mCurrType = 0;
//		} else if (this instanceof PedometorFragment) {
//			PedometorFragment pbf = (PedometorFragment) this;
//			Editor edit = sp.edit();
//			edit.putString("PreDayWeekMonth", pbf.mPreDay + "#" + pbf.mPreWeek
//					+ "#" + pbf.mPreMonth);
//			edit.commit();
//		}
		super.onDestroy();
	}

	public interface OnFragmentDestroyListener {
		abstract void onDestroy();
	}

	public void setOnFragmentDestroyListener(OnFragmentDestroyListener dlistener) {
		this.dlistener = dlistener;
	}

	public String getMyName() {
		String mMembername = getSharedPreferences().getString(
				SharedPreferredKey.NAME, "");
		return mMembername;
	}

	public String getAvater() {
		return getSharedPreferences()
				.getString(SharedPreferredKey.AVATAR, null);
	}

	public String getPhoneNum() {
		return getSharedPreferences().getString(SharedPreferredKey.PHONENUM,
				null);
	}

	public String getGender() {
		return getSharedPreferences()
				.getString(SharedPreferredKey.GENDER, null);
	}
}
