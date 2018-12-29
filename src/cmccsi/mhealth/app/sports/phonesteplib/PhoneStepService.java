package cmccsi.mhealth.app.sports.phonesteplib;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;

/**
 * 主要的手机记步服务，在下列TODO中返回通过手机记录的信息
 * 
 * @author 志伟
 *
 */
public class PhoneStepService extends Service {
	/**
	 * 自己控制的线程去获取手机陀螺仪或者加速器信息
	 */
	private volatile GetAccThread myThread = null;
	private StepDetector mDetector;
	private UserInstance userInstance;
	private SPUtils spUtils;

	@Override
	public void onCreate() {
		// ---------------用户信息收集 start--------------------------------------
		userInstance = new UserInstance();
		userInstance.setGender(0);
		userInstance.setAge(22);
		userInstance.setHeight(155);
		userInstance.setWeight(66.5);
		spUtils = SPUtils.getInstance(getApplicationContext());
		// ---------------用户信息收集 end--------------------------------------
		// --------------记步线程逻辑处理 start-----------------------
		mDetector = StepDetector.getStepDetetorInstance(getApplicationContext());
		mDetector.startStepDetector();
		if (null != myThread) {
			myThread.stopMyThread();
			myThread = null;
		}
		myThread = new GetAccThread(mDetector, userInstance);
		myThread.setDaemon(true);
		myThread.start();
		// --------------记步线程逻辑处理 end-----------------------
		if (ProtectionService.isServiceRunning(getApplicationContext())) {
			Intent service = new Intent(PhoneStepService.this, ProtectionService.class);
			startService(service);
		}
		// -------------不死服务逻辑处理 end---------------------
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == myThread || null == mDetector) {
			// ---------------用户信息收集 start--------------------------------------
			userInstance = new UserInstance();
			userInstance.setGender(0);
			userInstance.setAge(22);
			userInstance.setHeight(155);
			userInstance.setWeight(66.5);
			// ---------------用户信息收集 end--------------------------------------
			mDetector = StepDetector.getStepDetetorInstance(getApplicationContext());
			mDetector.startStepDetector();
			myThread = new GetAccThread(mDetector, userInstance);
			myThread.setDaemon(true);
			myThread.start();
		}
		flags = START_REDELIVER_INTENT;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.e("test", "onDestroy");
		if (null != myThread) {
			myThread.stopMyThread();
			myThread = null;
		}
		if (null != mDetector) {
			mDetector.stopStepDetector();
			mDetector = null;
		}
		if (ProtectionService.isServiceRunning(getApplicationContext())) {
			Intent service = new Intent(PhoneStepService.this, ProtectionService.class);
			startService(service);
		}
		super.onDestroy();
	}

	/**
	 * 线程的例子
	 * 
	 * @author luckchoudog
	 */
	class GetAccThread extends Thread {
		private StepDetector mDetector;
		private UserInstance userInstance;
		private int[] mAcc = new int[75];// 从陀螺仪中获取到的数据
		private int[] ft = new int[8];
		private int[] ltemp = new int[20];
		private int cnt = 0;
		private int samp = 0;
		private int ltemp_index = 0;
		private int startflag = 0;
		private int max_in20 = -1000;
		private int min_in20 = 1073741823;
		private int max = -1000;
		private int min = 1073741823;
		private int thres = 1000;
		private int oldthres = 1000;
		private int threchangeflag = 0;
		private int timecount = 0;
		private int vppchangecount = 0;
		private int vpp = 0;
		private int delta_vpp = 0;
		private int new_fixed = 0;
		private int old_fixed = 0;
		private int mode = 0;
		private int lastmode = 0;
		private int lastsearch = 0;
		private int searchcount = 0;//
		private int samp1 = 0;
		private int search_count = 5;// 连续运动10步后，开始正式记步
		private int last_step = 0;
		// private float fdistance;
		/**
		 * 从线程开始启动到现在走的步数、消耗的卡路里、距离、运动时间、运动过程中的运动强度
		 */
		private int step = 0;
		private double calory = 0;
		private double distance = 0;
		private double time = 0;
		private int exercise_intensity_normally = 0;
		private int exercise_intensity_fairly = 0;
		private int exercise_intensity_very = 0;
		/*
		 * 每一步的运动强度，记录各个运动强度的步数
		 */
		private int EXERCISE_INTENSITY_MIN = 5;// 感应器运动强度小于5，舍去
		private int EXERCISE_INTENSITY_MAX = 45;// 感应器运动强度大于45，舍去

		public GetAccThread(final StepDetector mDetector, UserInstance userInstance) {
			this.mDetector = mDetector;
			this.userInstance = userInstance;
			for (int i = 0; i < 4; i++) {
				ft[i] = 4096;
			}
			for (int i = 0; i < 20; i++) {
				ltemp[i] = 0;
			}
			// 初始化计步器数据
			step = spUtils.getInt(SharedPreferredKey.PHONESTEP_STEP, 0);
			calory = spUtils.getFloat(SharedPreferredKey.PHONESTEP_CALORY, 0);
			distance = spUtils.getFloat(SharedPreferredKey.PHONESTEP_DISTANCE, 0);
			time = spUtils.getFloat(SharedPreferredKey.PHONESTEP_TIME, 0);
			exercise_intensity_normally = spUtils.getInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_NORMALLY, 0);
			exercise_intensity_fairly = spUtils.getInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_FAIRLY, 0);
			exercise_intensity_very = spUtils.getInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_VERY, 0);
		}

		public void stopMyThread() {
			GetAccThread tmpThread = myThread;
			myThread = null;
			if (tmpThread != null) {
				tmpThread.interrupt();
			}
		}

		public void run() {
			if (myThread == null) {
				return; // stopped before started.
			}
			try {
				int data_g;
				int max_data = -1, min_data = 9999;
				while (true) {
					for (int i = 0; i < 3; i++) {
						mAcc[cnt++] = mDetector.getAcc().get(i);
					}
					if (cnt >= 45) {
						cnt = 0;
						for (int i = 0; i < 15; i++) {
							data_g = (int) (mAcc[i * 3 + 0] * mAcc[i * 3 + 0] + mAcc[i * 3 + 1] * mAcc[i * 3 + 1] + mAcc[i * 3 + 2]
									* mAcc[i * 3 + 2]);
							if (data_g > max_data) {
								max_data = data_g;
							}
							if (data_g < min_data) {
								min_data = data_g;
							}
							step_process(userInstance.getGender(), userInstance.getAge(), userInstance.getHeight(),
									userInstance.getWeight(), data_g);
							if (i % 5 == 0) {
								i++;
							}
						}
						if (max_data - min_data < 300) {
							if (mDetector.getIsStepDetectorRun()) {
								mDetector.stopStepDetector();
							}
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									if (!mDetector.getIsStepDetectorRun()) {
										mDetector.startStepDetector();
										Log.v("test", "awake!");
									}
								}
							}, 2000);
							Log.v("test", "fall asleep!");
							sleep(40);
							continue;
						}
						// TODO
						// 返回数据step步数、calory卡路里、distance运动距离、time运动时间
						//exercise_intensity_normally运动强度(一般)、exercise_intensity_fairly运动强度(中等)
						//exercise_intensity_very运动强度(剧烈)
						Log.v("test", "step:" + step + " calory:" + calory + " distance:" + distance + " time:" + time
								+ " lightly:" + exercise_intensity_normally + " fairly:" + exercise_intensity_fairly + " very:"
								+ exercise_intensity_very);
						storageData();//保存数据
					}
					sleep(40);
					Thread.yield();
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("Stopped by ifInterruptedStop()");
					}
				}
			} catch (Throwable t) {
				Log.v("test", "-----------线程干掉---重启线程---------" + t);
				storageData();
				if (null != myThread) {
					myThread.stopMyThread();
					myThread = null;
				}
				myThread = new GetAccThread(mDetector, userInstance);
				myThread.setDaemon(true);
				myThread.start();
			}
		}

		private void storageData() {
			spUtils.putInt(SharedPreferredKey.PHONESTEP_STEP, step);
			spUtils.putFloat(SharedPreferredKey.PHONESTEP_CALORY, (float) calory);
			spUtils.putFloat(SharedPreferredKey.PHONESTEP_DISTANCE, (float) distance);
			spUtils.putFloat(SharedPreferredKey.PHONESTEP_TIME, (float) time);
			spUtils.putInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_NORMALLY, exercise_intensity_normally);
			spUtils.putInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_FAIRLY, exercise_intensity_fairly);
			spUtils.putInt(SharedPreferredKey.PHONESTEP_EXERCISE_INTENSITY_VERY, exercise_intensity_very);
		}

		/**
		 * 运动过程，需要传入从陀螺仪中解析的数据
		 * 
		 * @param gender
		 *            性别：1男，2女
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param weight
		 *            体重
		 * @param data_g
		 *            从陀螺仪中解析的数据
		 */
		private void step_process(int gender, int age, double height, double weight, int data_g) {
			int result;
			double chstep_len = 0;
			double rmr = 0;
			double cdif = 0;
			double bmr = 0;
			ft[0] = ft[1];
			ft[1] = ft[2];
			ft[2] = ft[3];
			ft[3] = ft[4];
			ft[4] = data_g;
			result = ft[0] + 2 * ft[1] + 3 * ft[2] + 2 * ft[3] + ft[4];
			samp++;
			if (samp >= 80) {
				samp = 60;
			}
			if (samp <= 20) {
				ltemp[ltemp_index] = result;
				if (ltemp_index < 19) {
					ltemp_index++;
				}
			} else {
				for (int i = 0; i < 19; i++) {
					ltemp[i] = ltemp[i + 1];
				}
				ltemp[19] = result;
				if (samp >= 51) {
					startflag = 1;
				}
				if (samp >= 31) {
					max_in20 = (ltemp[19] > max_in20) ? ltemp[19] : max_in20;
					min_in20 = (ltemp[19] < min_in20) ? ltemp[19] : min_in20;
					max = (ltemp[19] > max) ? ltemp[19] : max;
					min = (ltemp[19] < min) ? ltemp[19] : min;
					vppchangecount++;
					samp1++;
					if (samp1 > 19) {
						vpp = max_in20 - min_in20;
						max_in20 = -1000;
						min_in20 = 1073741823;
						samp1 = 0;
						oldthres = thres;
						thres = (4 * max + 3 * min) / 7;
						// thres = (max + min) / 2;
						delta_vpp = max - min;
						if (delta_vpp > 14560) {
							if (delta_vpp > 57600) {
								thres = (int) ((max + min) / 2.6);
							} else {
								thres = (int) ((max + min) / 2.1);
							}
						}
						threchangeflag = 1;
						max = -1000;
						min = 1073741823;
						vppchangecount = 0;
						int dif = step - last_step;
						if (dif > 0 && lastmode == 0)
							dif = searchcount - lastsearch;
						if (dif > 0)
							lastmode = 1;
						else
							lastmode = 0;
						if (dif <= 10 && dif > 0) {
							last_step = step;
							lastsearch = searchcount;
						} else {
							last_step = step;
							lastsearch = searchcount;
						}
					}
				}
				if (startflag == 1) {
					timecount++;
				}
				if (!(threchangeflag == 1 && vppchangecount < 5)) {
					threchangeflag = 0;
				}
				if (startflag != 0 && vpp >= 14600)// 8832)
				{
					old_fixed = ltemp[9];
					new_fixed = ltemp[10];
					if ((old_fixed < thres && thres < new_fixed)
							|| (threchangeflag == 1 && old_fixed <= oldthres && oldthres <= new_fixed)) {
						if (timecount >= EXERCISE_INTENSITY_MIN && timecount <= EXERCISE_INTENSITY_MAX) {
							int localmax = 0;
							int localmin = 0;
							localmin = ltemp[9];
							localmax = ltemp[9];
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 + locali] < ltemp[9 + locali - 1]) {
									break;
								} else
									localmax = ltemp[9 + locali];
							}
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 - locali] > ltemp[9 - locali + 1]) {
									break;
								} else
									localmin = ltemp[9 - locali];
							}
							if (localmax - localmin < 5700)// 6760)
								return;
							if (mode == 1) {
								step++;
								bmr = balCaculate(gender, age, height, weight);
								rmr = calCaculate(age, height, 1, ltemp);
								calory += rmr * bmr;
								/* 距离的计算 */
								cdif = (float) (result / 10000.0);
								chstep_len = steplen(gender, age, height, cdif);
								distance += (chstep_len / 100.0);
								if (cdif < 10) {
									exercise_intensity_normally += 1;
									time += 0.55;
								} else if (cdif <= 17) {
									exercise_intensity_fairly += 1;
									time += 0.5;
								} else {
									exercise_intensity_very += 1;
									time += 0.45;
								}
							} else {
								if (searchcount == 0) {
									searchcount = 2;
								} else {
									searchcount++;
								}
								if (searchcount >= search_count) {
									mode = 1;
									step += searchcount;
									cdif = (float) (result / 10000.0);
									chstep_len = steplen(gender, age, height, cdif);
									distance = (distance + (search_count * chstep_len / 100.0));
									bmr = balCaculate(gender, age, height, weight);
									rmr = calCaculate(age, height, search_count, ltemp);
									calory += rmr * bmr;
									exercise_intensity_normally += search_count;
									time += (search_count * 0.5);
								}
							}
							timecount = 0;
						} else {
							mode = 0;
							searchcount = 0;
							timecount = 0;
						}
					}
				}
			}
		}

		/**
		 * 根据性别年龄身高，陀螺仪的运动强度算每一步的步长
		 * 
		 * @param gender
		 *            性别
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param cdif
		 *            陀螺仪的运动强度
		 * @return 步长
		 */
		private double steplen(int gender, int age, double height, double cdif) {
			double fheight = 0, chstep_len = 0;
			fheight = (float) height;
			if (gender == 0) {
				if (cdif <= 4)
					chstep_len = (float) ((0.2 + 0.05 * cdif) * fheight);
				else if (cdif <= 5.1)
					chstep_len = (float) ((0.25 + 0.05 * cdif) * fheight);
				else
					chstep_len = (float) ((0.3 + 0.05 * cdif) * fheight);
			} else {
				if (cdif <= 4)
					chstep_len = (float) ((0.2 + 0.05 * cdif) * fheight);
				else if (cdif <= 5.6)
					chstep_len = (float) ((0.22 + 0.05 * cdif) * fheight);
				else
					chstep_len = (float) ((0.24 + 0.05 * cdif) * fheight);
			}
			chstep_len = chstep_len > 98 ? 98 : chstep_len;
			chstep_len = chstep_len < 50 ? 50 : chstep_len;
			return chstep_len;

		}

		/**
		 * 计算卡路里消耗，根据年龄身高和一定时间内的运动强度和步数
		 */
		private double calCaculate(int age, double height, int dif, int[] ltemp2) {
			int accsum, acctmp;
			double rmr = 0, cstep_len = 0;
			int i;
			final int BUFLEN = 20;
			accsum = 0;
			for (i = 0; i < BUFLEN; i++) {
				accsum += ltemp[i];
			}
			accsum = accsum / BUFLEN;
			acctmp = accsum / 167 - 222;
			if (acctmp > 0)
				rmr = (float) Math.sqrt(acctmp);
			else
				rmr = 0;
			rmr = rmr > 0 ? rmr : 0;
			if (dif < 4)
				cstep_len = (float) 0.56;
			else if (dif < 5)
				cstep_len = (float) 0.64;
			else
				cstep_len = (float) 0.72;
			rmr = rmr + (cstep_len * (float) (dif * dif));
			return rmr;
		}

		/**
		 * 建模数据算法，根据性别，年龄，身高，体重等建模
		 * 
		 * @param gender
		 *            性别：1男，2女
		 * @param age
		 *            年龄
		 * @param height
		 *            身高
		 * @param weight
		 *            体重
		 * @return 模型系数
		 */
		private double balCaculate(int gender, int age, double height, double weight) {
			double bmr = 0;
			double bsa = 0;
			if (gender == 1) {
				bmr = 134 * weight + 48 * height - 57 * age + 883;
				bsa = 61 * height + 127 * weight - 698;
			} else {
				bmr = 92 * weight + 31 * height - 43 * age + 4476;
				bsa = 59 * height + 126 * weight - 461;
			}
			bmr = bmr * bsa / 500000 / 100000;
			return bmr;
		}
	}

	/**
	 * 
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param className
	 *            ：判断的服务名字：包名+类名
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(PhoneStepService.class.getName()) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
