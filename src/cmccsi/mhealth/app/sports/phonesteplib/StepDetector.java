package cmccsi.mhealth.app.sports.phonesteplib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * �����ǵ�ʵ��ǲ�ʹ��
 * 
 * @author luckchoudog
 */
public class StepDetector implements SensorEventListener {
	/**
	 * �����ǵ���
	 */
	private static StepDetector stepDetector = null;
	/**
	 * �����ǵ�ʵ���ϵͳ��ȡ
	 */
	private SensorManager mSensorManager;
	/**
	 * �����ǵ��������xyz����ƫ��ֵ
	 */
	private float[] mAcc;
	/**
	 * �������Ƿ���ʹ��
	 */
	private boolean isStepDetectorRun = false;

	private StepDetector(Context context) {
		mAcc = new float[3];
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	public static StepDetector getStepDetetorInstance(Context context) {
		if (null == stepDetector) {
			stepDetector = new StepDetector(context);
		}
		return stepDetector;
	}

	/**
	 * ��ʼʹ�������ǣ�����˵��ʼ�ǲ�����
	 */
	public void startStepDetector() {
		isStepDetectorRun = true;
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * ֹͣ���������Ǳ仯
	 */
	public void stopStepDetector() {
		isStepDetectorRun = false;
		mSensorManager.unregisterListener(this);
	}

	/**
	 * ���������ٶ�
	 */
	public List<Integer> getAcc() {
		List<Integer> result = new ArrayList<Integer>();
		float[] temp = mAcc.clone();
		for (int i = 0; i < 3; i++) {
			int b = (int) (temp[i] / 9.8 / 2 * 128);
			result.add(b);
		}
		return result;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * ȥ����Чƫ���������ƫ������С������
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		for (int i = 0; i < 3; i++) {
			if (values[i] > 2 * 9.8) {
				mAcc[i] = (float) (2 * 9.8);
			} else if (values[i] < -2 * 9.8) {
				mAcc[i] = (float) (-2 * 9.8);
			} else {
				mAcc[i] = values[i];
			}
		}
	}
	public boolean getIsStepDetectorRun(){
		return isStepDetectorRun;
	}
}
