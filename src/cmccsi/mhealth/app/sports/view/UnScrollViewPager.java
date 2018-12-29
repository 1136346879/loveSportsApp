package cmccsi.mhealth.app.sports.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UnScrollViewPager extends ViewPager {
	
	public UnScrollViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public UnScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

}
