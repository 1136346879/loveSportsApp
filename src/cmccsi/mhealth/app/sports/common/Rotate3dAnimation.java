package cmccsi.mhealth.app.sports.common;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 
 * @Description 布局3D翻转
 * @author lixishuang
 * @date 2012-9-12下午4:26:06
 * @version V1.0
 */
public class Rotate3dAnimation extends Animation {
	// ????角度
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;

	/**
	 * 创建????绕Y轴旋转的3D效果，旋转点??D空间的中心点(centerX,centerY) 该效果由起始角度和终止角度决??
	 * 在旋转开始时，Z轴被定义????旋转到屏幕里面的深度值，该??可固定，也可以随时间变化
	 * 
	 * @param fromDegrees
	 *            ????角度
	 * @param toDegrees
	 *            终止角度
	 * @param centerX
	 *            2D空间X坐标
	 * @param centerY
	 *            2D空间Y坐标
	 * @param depthZ
	 *            旋转到里面的深度
	 * @param reverse
	 *            如果可旋转则为true
	 */
	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ,
			boolean reverse) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		// Camera类是用来实现绕Y轴旋转后透视投影??
		mCamera = new Camera();
	}

	// 生成Transformation
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		// 生成中间角度
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (mReverse) {
			// camera.translate对矩阵进行平移变换操??
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degrees);
		// 取得变换后的矩阵
		camera.getMatrix(matrix);
		// camera.restore进行旋转
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
