package cmccsi.mhealth.app.sports.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.R;

/**
 * 刷新控制view 
 */
public class DownFlashView extends LinearLayout {

	private static final String TAG = "LILITH";
	private Scroller scroller;
	private View refreshView;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = (int) -(60*Common.getDensity(getContext()));
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;
	private RefreshListener refreshListener;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	public int mState;

//	private String downTextString;
//	private String releaseTextString;

	// private Long refreshTime = null;
	private int lastX;
	private int lastY;
	// 拉动标记
	// 是否可刷新标记
//	private boolean isRefreshEnabled = true;
    // 在刷新中标记
	public boolean isRefreshing = false;

	private Context mContext;
	private RotateAnimation mAnimation;
	private RotateAnimation mReverseAnimation;

	public DownFlashView(Context context) {
		super(context);
		mContext = context;

	}

	public DownFlashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		// 滑动对象，
		scroller = new Scroller(mContext);

		// 刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(R.layout.refresh_top_item, null);
		// 指示器view
		refreshIndicatorView = (ImageView) refreshView.findViewById(R.id.indicator);
		refreshIndicatorView.setImageResource(R.drawable.arrow);
		// 刷新bar
		bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		bar.setVisibility(View.GONE);
		// 下拉显示text
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);
		// 下来显示时间
		timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);

		LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int) (60*Common.getDensity(getContext())));
		lp.topMargin = refreshTargetTop;
		addView(refreshView, lp);
//		downTextString = mContext.getResources().getString(R.string.basicInfo);
//		releaseTextString = mContext.getResources().getString(R.string.update_day_title);

		mAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setDuration(250);
		mAnimation.setFillAfter(true);

		mReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setDuration(250);
		mReverseAnimation.setFillAfter(true);
	}

	/**
	 * 设置上次刷新时间
	 * 
	 * @param time
	 */
	public void setLastRefreshTimeText() {
		 timeTextView.setText(Common.getDateAsM_d(new Date().getTime()));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isRefreshing)
			return false;
		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录下y坐标
			lastY = y;
			LinearLayout.LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
			lp.topMargin = refreshTargetTop;
			break;

		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE");
			// y移动坐标
			int m = y - lastY;
			doMovement(m);
			// 记录下此刻y坐标
			this.lastY = y;
			break;

		case MotionEvent.ACTION_UP:
			Log.i(TAG, "ACTION_UP");
			fling(0);
			break;
		}
		return true;
	}

	/**
	 * up事件处理
	 */
	public void fling(int max) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		if (max == 60)
			lp.topMargin = max;
		Log.i(TAG, "fling()" + lp.topMargin);
		if (lp.topMargin > 0) {// 拉到了触发可刷新事件
			refresh();
		} else {
			returnInitState();
		}
	}

	public void returnInitState() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		scroller.startScroll(0, lp.topMargin, 0, refreshTargetTop, 700);
		invalidate();
	}

	public void refresh() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		scroller.startScroll(0, lp.topMargin, 0, 0 - (lp.topMargin),500);
		invalidate();
		setState(STATE_REFRESHING);
		if (refreshListener != null && !isRefreshing) {
			refreshListener.onRefresh(this);
			isRefreshing = true;
		}
	}

	/**
     * 
     */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
			if(i <= refreshTargetTop){
				i = refreshTargetTop;
				isRefreshing = false;
			}
			lp.topMargin = i;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * 下拉move事件处理
	 * 
	 * @param moveY
	 */
	public void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		Logger.d(TAG, "lp.topMargin -----------> " + lp.topMargin);
		if(lp.topMargin>100){
			return;
		}
		if (moveY > 0) {
			// 获取view的上边距
			float f1 = lp.topMargin;
			float f2 = moveY * 0.5f;
			int i = (int) (f1 + f2);
			// 修改上边距
			lp.topMargin = i;
			// 修改后刷新
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();
		} else {
			float f1 = lp.topMargin;
			int i = (int) (f1 + moveY * 0.9F);
			if (i >= refreshTargetTop) {
				lp.topMargin = i;
				// 修改后刷新
				refreshView.setLayoutParams(lp);
				refreshView.invalidate();
				invalidate();
			} else {

			}
		}

		if (lp.topMargin > 0) {
			setState(STATE_READY);
		} else {
			setState(STATE_NORMAL);
		}

	}

	public void setRefreshEnabled(boolean b) {
//		this.isRefreshEnabled = b;
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 结束刷新事件
	 */
	public void finishRefresh() {
		setLastRefreshTimeText();
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, -i + refreshTargetTop, 600);
		invalidate();
		setState(STATE_NORMAL);
	}
	
	public void startRefreshDirectly(){
		setState(STATE_REFRESHING);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		lp.topMargin = 0;
		this.refreshView.setLayoutParams(lp);
		this.refreshView.invalidate();
		if (refreshListener != null && !isRefreshing) {
			refreshListener.onRefresh(this);
			isRefreshing = true;
		}
	}
	
	public void startRefreshDirectlyNoRefresh(){
		setState(STATE_REFRESHING);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		lp.topMargin = 0;
		this.refreshView.setLayoutParams(lp);
		this.refreshView.invalidate();
	}

	private boolean canScroll() {
		// TODO Auto-generated method stub
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3 && ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}
	
	public void setState(int state) {
		if (state == mState)
			return;
		if (state == STATE_REFRESHING) { // 显示进度
			bar.setVisibility(View.VISIBLE);
			downTextView.setText(getResources().getString(R.string.xlistview_header_hint_loading));
		} else { // 显示箭头图片
			refreshIndicatorView.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
		}
		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				downTextView.setText(getResources().getString(R.string.xlistview_header_hint_normal));
				refreshIndicatorView.clearAnimation();
				refreshIndicatorView.startAnimation(mReverseAnimation);
			}
			if (mState == STATE_REFRESHING) {
				downTextView.setText("");
			}
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				downTextView.setText(getResources().getString(R.string.xlistview_header_hint_ready));
				refreshIndicatorView.clearAnimation();
				refreshIndicatorView.startAnimation(mAnimation);
			}
			break;
		case STATE_REFRESHING:
			refreshIndicatorView.setVisibility(View.GONE);
			refreshIndicatorView.clearAnimation();
			break;
		default:
		}

		mState = state;
	}

	public void setText(String str)
	{
		bar.setVisibility(View.VISIBLE);
		downTextView.setText(str);
	}
	
	/**
	 * 刷新监听接口
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		public void onRefresh(DownFlashView view);
	}

}
