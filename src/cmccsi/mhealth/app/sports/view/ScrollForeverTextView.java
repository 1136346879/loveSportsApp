/********************************************
 * 文件名		：ScrollForeverTextView.java
 * 版本信息	：1.00
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-5-25 下午3:57:13   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-5-25 下午3:57:13  
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollForeverTextView extends TextView {

	public ScrollForeverTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	// 滚动
	public boolean isFocused() {
		return true;
	}
}
