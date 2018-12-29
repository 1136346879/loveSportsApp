package cmccsi.mhealth.app.sports.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class HorizontalMarqueeTextView extends TextView {

    public HorizontalMarqueeTextView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public HorizontalMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalMarqueeTextView(Context context) {
        super(context);
    }

    @Override
    @ExportedProperty(category = "focus")
    public boolean isFocused() {
        return true;
    }
}
