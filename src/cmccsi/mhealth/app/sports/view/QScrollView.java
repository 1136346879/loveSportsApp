package cmccsi.mhealth.app.sports.view;

//**import cmccsi.mhealth.portal.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.Logger;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 解决scrollview（外）与HorizontalScrollView（内）嵌套时产生的卡顿现象
 * 
 * @author zy
 * 
 */
public class QScrollView extends ScrollView {
	private float mDownPosX = 0;
	private float mDownPosY = 0;
	@SuppressWarnings("unused")
	private final static String TAG = "QScrollView";

	public QScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();

		final int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mDownPosX = x;
			mDownPosY = y;

			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaX = Math.abs(x - mDownPosX);
			final float deltaY = Math.abs(y - mDownPosY);

			if (y - mDownPosY > 0) {
				if (this.getScrollY() == 0) {
					Logger.d(TAG, "at top");
					return false;
				}
			}

			if (deltaX > deltaY / 2) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}
}