package cmccsi.mhealth.app.sports.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cmccsi.mhealth.app.sports.R;

public class RoundAngleImageView extends ImageView {

	private float xRadius;
	private float yRadius;

	public RoundAngleImageView(Context context) {
		super(context);
	}

	public float getxRadius() {
		return xRadius;
	}

	public void setxRadius(float xRadius) {
		this.xRadius = xRadius;
	}

	public float getyRadius() {
		return yRadius;
	}

	public void setyRadius(float yRadius) {
		this.yRadius = yRadius;
	}

	public RoundAngleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
		xRadius = typeArray.getDimension(R.styleable.RoundAngleImageView_roundWidth, 0);
		yRadius = typeArray.getDimension(R.styleable.RoundAngleImageView_roundHeight, 0);
		typeArray.recycle();
	}

	public RoundAngleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		BitmapShader shader;
		BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
		if (bitmapDrawable != null) {
			shader = new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			// 设置映射否则图片显示不全
			RectF rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
			int width = bitmapDrawable.getBitmap().getWidth();
			int height = bitmapDrawable.getBitmap().getHeight();
			RectF src = new RectF(0.0f, 0.0f, width, height);
			Matrix matrix = new Matrix();
			matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);
			shader.setLocalMatrix(matrix);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(shader);
			canvas.drawRoundRect(rect, xRadius, yRadius, paint);
		}
	}

}