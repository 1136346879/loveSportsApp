package cmccsi.mhealth.app.sports.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.R;

public class SettingAboutActivity extends BaseActivity {
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		BaseBackKey("关于", this);
		mImageView = findView(R.id.setting_about);
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingAboutActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		mImageView.measure(w, h);
        double iv_ratio = 1.0 * mImageView.getMeasuredWidth()
                * mImageView.getMeasuredHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg_about);
        double img_ratio = 1.0 * bitmap.getWidth() / bitmap.getHeight();
        if (iv_ratio > img_ratio) {
            mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        }else if (iv_ratio<img_ratio) {
            mImageView.setScaleType(ScaleType.CENTER_CROP);
        }else {
            mImageView.setScaleType(ScaleType.FIT_XY);
        }
        mImageView.setImageBitmap(bitmap);
	}
}
