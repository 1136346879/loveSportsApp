package cmccsi.mhealth.app.sports.view;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.R;

/**
 * 使用时务必将bitmap传递进来，view内部不会decode bitmap
 * 
 * @author zy
 * 
 */
public class ScoreBarView extends View {

	private Bitmap mLeftPic;
	private Bitmap mRunPic;
	private int maxValue = 100000;
	// 分数
	private int score = 0;
	private int acturallyscore = 0;
	// 字体样式
    private Typeface typeface;
    private int[]    colors         = new int[] { Color.rgb(229, 80, 122),
            Color.rgb(147, 140, 0), Color.rgb(1, 188, 207),
            Color.rgb(191, 108, 186), Color.rgb(122, 175, 59),
            Color.rgb(68, 153, 220) };
    private Random mRandom = new Random();

    public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * 设置分数
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
		this.acturallyscore = score;
	}

	/**
	 * 手动设置图片，接下来可以setScore() 或者直接使用reDraw()
	 * 
	 * @param leftPic
	 *            左边的图片
	 * @param runPic
	 *            小人图片
	 */
	public void setPics(Bitmap leftPic, Bitmap runPic) {
		this.mRunPic = runPic;
		this.mLeftPic = leftPic;
	}
	
	/**
	 * 手动设置图片，接下来可以setScore() 或者直接使用reDraw()
	 * @param runPic 右边的图片
	 */
	public void setPics( Bitmap runPic) {
	    setPics(BitmapFactory.decodeResource(getResources(), R.drawable.scoreview_left), runPic);
    }
	/**
	 * 重绘
	 */
	public void reDraw() {
		invalidate();
	}

	public ScoreBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScoreBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 普通构造函数，需要手动设置setPics 以及分数setScore，然后执行reDraw
	 * 
	 * @param context
	 */
	public ScoreBarView(Context context) {
		super(context);
	}

	/**
	 * 构造参数中直接设置图片类型，以及步数。
	 * 
	 * @param context
	 *            上下文
	 * @param score
	 *            步数
	 * @param leftPic
	 *            左边的图片
	 * @param runPic
	 *            小人图片
	 */
	public ScoreBarView(Context context, int score, Bitmap leftPic, Bitmap runPic) {
		super(context);
		this.mRunPic = runPic;
		this.mLeftPic = leftPic;
		this.score = score;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// make the view the original height + indicator height size
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLeftPic == null || mRunPic == null) {
			return;
		}

		float leftScale = (float) getHeight() / mLeftPic.getHeight();
		float rightScale = (float) getHeight() / mRunPic.getHeight();

		float Width = this.getWidth() - mLeftPic.getWidth() * leftScale - mRunPic.getWidth() * rightScale;
		float Bl1 = 0;
		float Bl2 = 0;
		
		maxValue = maxValue >100000 ? 100000 : maxValue;
		maxValue = maxValue >0 ? maxValue : 10;
//		maxValue = maxValue < 1000 ? 1000 : maxValue;

		score = score > maxValue ? maxValue : score;
		score = score < 0 ? 0 : score;

		// 10000以内的比例为60% 此外分到的比例为40%
		
		float divideten = maxValue/10f;
		
		if (score <= divideten) {
			Bl1 = score / divideten * 0.2f + 0.2f;
		} else {
			Bl1 = 0.4f;
			Bl2 = (score - divideten) / (maxValue - divideten) * 0.5f;
		}

		Paint paint = new Paint();
		paint.setColor(colors[mRandom.nextInt(colors.length)]);// 设置中间图块颜色
		float density = Common.getDensity(getContext());
		paint.setTextSize((int) ((24 * rightScale + 20) * density / 2f));// 字体大小

//		canvas.drawBitmap(setScale(mLeftPic, rightScale), 0, 0, paint);// 画左边图片
//		canvas.drawBitmap(setScale(mRunPic, rightScale),10+ (Bl1 + Bl2) * Width + mLeftPic.getWidth() * leftScale, 0, paint);// 画小人图片

        // 中间矩形的绘制
        RectF r = new RectF(0,
                mRunPic.getHeight() * rightScale*5f/46f+0.5f, (Bl1 + Bl2)
                        * Width + mLeftPic.getWidth() * leftScale,
                mRunPic.getHeight() * rightScale);
        canvas.drawRect(r, paint);

		/*// 步数字体的绘制
		paint.setColor(Color.rgb(255, 255, 255));
		paint.setAntiAlias(true);

		if (typeface != null) {
			paint.setTypeface(typeface);
		}
		String StepScore = acturallyscore + "";
		canvas.drawText(StepScore, mLeftPic.getWidth() * leftScale - 8, (mRunPic.getHeight() * rightScale + 18 * leftScale + 8) / 2f+4, paint);
        */
	}

	public Bitmap setScale(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}
}
