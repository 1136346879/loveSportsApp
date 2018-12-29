package cmccsi.mhealth.app.sports.view;

import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * ʹ��ʱ��ؽ�bitmap���ݽ�����view�ڲ�����decode bitmap
 * 
 * @author zy
 * 
 */
public class CenterRollingBallECG extends View {

	private Bitmap mBallPic;
	private Bitmap mYelPoint;
	// ����
	private int score = 0;
	private int maxScore = 0;
	private int angle = -90;
	private int Alpha = 255;
	private int firstLayer = Color.parseColor("#ff6834");
	private int secondLayer = Color.rgb(253, 230, 34);
	private int thirdLayer = Color.rgb(244, 153, 17);
	private float Radius =0.94f;
	private float LocationX =0.95f;
	private float LocationY =0.95f;
	private boolean ringEnable=true;
	private int[] colorArray = new int[] { Color.rgb(203, 203, 203),
			firstLayer, secondLayer, thirdLayer, };
	
	//����͸����
	public void setAlpha(int Alpha) {
		this.Alpha = Alpha;
	}
	//����ring
			public void setRing(boolean ringEnable) {
				this.ringEnable = ringEnable;
			}
	//����λ��
		public void setLocationX(float LocationX) {
			this.LocationX = LocationX;
		}
		//����λ��
		public void setLocationY(float LocationY) {
			this.LocationY = LocationY;
		}
	//���ð뾶
	public void setRadius(float Radius) {
		this.Radius = Radius;
	}
	
	// ���ýǶ�
	public void setAngle(int angle) {
		this.angle = angle;
	}

	// ���õ�һ����ɫ
	public void setFirstLayer(int firstLayer) {
		this.firstLayer = firstLayer;
	}

	// ���õڶ�����ɫ
	public void setSecondLayer(int secondLayer) {
		this.secondLayer = secondLayer;
	}

	// ���õ�������ɫ
	public void SetThirdLayer(int thirdLayer) {
		this.thirdLayer = thirdLayer;
	}

	/**
	 * ���÷���
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public void setPics(Bitmap ball, Bitmap point) {
		this.mYelPoint = point;
		this.mBallPic = ball;
	}

	/**
	 * �ػ�
	 */
	public void reDraw() {
		postInvalidate();
	}

	public CenterRollingBallECG(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CenterRollingBallECG(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ��ͨ���캯������Ҫ�ֶ�����setPics �Լ�����setScore��Ȼ��ִ��reDraw
	 * 
	 * @param context
	 */
	public CenterRollingBallECG(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBallPic == null || mYelPoint == null) {
			return;
		}
		if (maxScore == 0)
			maxScore = 10000;
		int nanbai = score / maxScore;
		float bfb = score / (float) maxScore;
		bfb = nanbai > 2 ? 1 : bfb - nanbai;
		nanbai = nanbai > 2 ? 2 : nanbai;

		Paint paint = new Paint();// ���ʶ��������
		paint.setAntiAlias(true);// �������

		float ScaleX = (float) getHeight() / mBallPic.getHeight() * LocationY;
		float ScaleY = (float) getWidth() / mBallPic.getWidth() * LocationX;
		System.out.println("getHeight()"+getHeight()+"/t/t/t mBallPic.getHeight() "+mBallPic.getHeight()+"ScaleX"+ScaleX);
		System.out.println("*********************");
		System.out.println("getWidth()"+getWidth()+"/t/t/t mBallPic.getWidth()"+mBallPic.getWidth()+"ScaleY"+ScaleY);
		float finalX = mBallPic.getWidth() * 1.05f * ScaleX;
		float finalY = mBallPic.getHeight() * 1.05f * ScaleY;
		if(ringEnable)
		{
		 // �ڶ���Բ��
		 paint.setColor(colorArray[nanbai + 1]);
		 paint.setAlpha(Alpha);
		 canvas.drawArc(new RectF(0, 0, finalX, finalY), angle, 360 * bfb,
		 true,
		 paint);
		 // ��һ��Բ��
		 paint.setColor(colorArray[nanbai]);
		 paint.setAlpha(Alpha);
		 canvas.drawArc(new RectF(0, 0, finalX, finalY), 360 * bfb + angle,
		 360 - 360 * bfb, true, paint);
		}
		float PicX = mBallPic.getWidth() * 0.025f * ScaleX;
		float PicY = mBallPic.getHeight() * 0.025f * ScaleY;
		float yPicX = finalX / 2 - mYelPoint.getHeight() / 2 * 0.55f;
		float yPicY = finalY / 2 - mYelPoint.getWidth() / 2 * 0.55f;

		float sx = (float) (finalX / 2 * Math
				.sin((bfb * 360 + (angle - 90)) / 180 * 3.14159));
		float sy = (float) (finalY / 2 * Math
				.cos((bfb * 360 + (angle - 90)) / 180 * 3.14159));

//		canvas.drawBitmap(setScale(mBallPic, (float) getHeight() / mBallPic.getHeight(), (float) getWidth() / mBallPic.getWidth()), 0, 0, paint);
//		canvas.drawBitmap(setScale(mYelPoint, 0.55f, 0.55f),
//				yPicX - sx * Radius, yPicY + sy * Radius, paint);
	}

	public Bitmap setScale(Bitmap bitmap, float scalex, float scaley) {
		Matrix matrix = new Matrix();
		matrix.postScale(scalex, scaley);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

	}
}
