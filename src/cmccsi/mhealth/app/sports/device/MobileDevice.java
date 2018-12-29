package cmccsi.mhealth.app.sports.device;

import java.util.ArrayList;

import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.phonesteplib.StepController;
import cmccsi.mhealth.app.sports.service.StepService;
import cmccsi.mhealth.app.sports.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 手机计步设备实现类
 * @type MobileDevice
 * TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:34:10
 */
public class MobileDevice extends AbstractMobileDevice {
	
	private String TAG = "MobileDevice";
	
	private Context mContext;

	private BaseDeviceInterface.BaseCallBack mBaseCallBack;
	
	public MobileDevice(Context context){
		this.mContext = context;
	}

	@Override
	public void connect(String address) {
		// TODO Auto-generated method stub
		startPhoneStep();
		mBaseCallBack.connected(DeviceConstants.CONNECTED_SUCCESS, "连接成功");
	}

	@Override
	public void disConnect() {
		// TODO Auto-generated method stub
		stopPhoneStep();
	}

	@Override
	public void syncData(String startTime, String endTime) {
		// TODO Auto-generated method stub
		mBaseCallBack.pedoDataReceived(new PedometorListInfo(), new ArrayList<PedoDetailInfo>());
	}

	@Override
	public int getDeviceStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCallBack(BaseCallBack cb) {
		// TODO Auto-generated method stub
		this.mBaseCallBack = cb;
	}
	
	/**
	 * 开启手机计步
	 */
	private void startPhoneStep() {
		StepController mStepController = new StepController();
		mStepController.setContext(mContext);
		mStepController.startStepService(Config.SC_ACTION);
	}
	/**
	 * 停止手机计步
	 */
	private void stopPhoneStep() {
//		Intent it = new Intent(Config.PHONESTEP_STOP_ACTION);
//		mContext.sendBroadcast(it);
		StepController mStepController = new StepController();
		mStepController.setContext(mContext);
		mStepController.setStopAction(Config.PHONESTEP_STOP_ACTION);
		mStepController.stopStepService();
		Logger.d(TAG, "stopPhoneStep--------");
	}
	
	/**
	 * 手机计步广播
	 */
	@SuppressWarnings("unused")
	private class StepServiceUploadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Config.UPLOADSTATUS_ACTION)
					|| intent.getAction().equals(Config.PHONESTEP_STARTUPLOAD_ACTION)) { // 停止手机计步并上传数据成功后接收到的广播

				String result = "";
				result = intent.getStringExtra(StepService.UPLOAD_STATUS);
				if (result != null && !result.equals("")) {
//					ToastUtils.showToast(DeviceSettingActivity.this, result);
				}
				if (!result.equals(mContext.getResources().getString(R.string.phonestep_uploading))) {
					Logger.i(TAG, result + " 结束完了，开始更改sendMsgToChangeDevice");
					
					mBaseCallBack.disConnected();
				}

			}else if (Config.STEP_SENDING_ACTION.equals(intent.getAction())) { // 接收实时数据广播
				PedometorDataInfo pedo = new PedometorDataInfo();
				Bundle data = intent.getExtras();
				
				pedo.cal = String.valueOf(data.getInt("CALORIE_ALL_DAY"));
				pedo.distance = String.valueOf(data.getInt("DISTANCE_ALL_DAY"));
				pedo.strength2 = String.valueOf(data.getInt("DURATION_ALL_DAY"));
				pedo.stepNum = String.valueOf(data.getInt("STEP_ALL_DAY"));
				
				mBaseCallBack.realTimeDataReceived(pedo);
				
			}
		}

	}	

}
