package cmccsi.mhealth.app.sports.device;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.DeviceInfo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.BaseDeviceInterface.BaseCallBack;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.pedo.PedoController;
import cmccsi.mhealth.app.sports.pedo.UploadManager;

/**
 * 设备管理服务（所有设备的切换、数据上传、数据获取等功能）
 * @type DeviceManagerService
 * TODO
 * @author shaoting.chen
 * @time 2015年10月21日上午10:02:34
 */
public class DeviceManagerService extends Service {
	
	private static final String TAG = "DeviceManagerService";
	
	private BaseDeviceInterface mSelectedDevice = null;
	
	private BaseDeviceInterface mOldDevice = null;
	
	private DeviceInfo mOldDeviceInfo;
	
	private DeviceInfo mNewDeviceInfo;
	
	private Context mContext = DeviceManagerService.this;
	
	/**
	 * activity Handler
	 */
	private Handler mHandlerActivity;
	
	private boolean isSwicthDevice = false; //是否在切换设备
	
	private Handler mHandlerService = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Message message;
			switch(msg.what){
			case DeviceConstants.CONNECTED_SUCCESS:
				Logger.d(TAG, "---设备链接成功！");

			    break;
			case DeviceConstants.CONNECTED_FAIL:
				Logger.d(TAG, "---设备链接失败！");
				isSwicthDevice = false;
				disConnect();
				message = Message.obtain(mHandlerActivity, DeviceConstants.DEVCIE_SWITCH_FAIL);
				mBundle.putString("DEVCIE_SWITCH_FAIL", "切换失败");
				message.setData(mBundle);
				message.sendToTarget();
//			    ToastUtils.showToast(getBaseContext(), "切换失败！");
				break;
			case DeviceConstants.EXCEPTION_CONNECT:
				Logger.d(TAG, "---设备链接失败！");
				isSwicthDevice = false;
				disConnect();
				message = Message.obtain(mHandlerActivity, DeviceConstants.DEVCIE_SWITCH_FAIL);
				mBundle.putString("DEVCIE_SWITCH_FAIL", msg.getData().getString("EXCEPTION_CONNECT"));
				message.setData(mBundle);
				message.sendToTarget();
//			    ToastUtils.showToast(getBaseContext(), "切换失败！");
				break;
			case DeviceConstants.UPLOAD_SUCCESS:
				Logger.d(TAG, "---上传设备数据成功");
				
				if(isSwicthDevice){
					mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
					//切换设备：2、连接新设备
					if(Common.getDeviceType(mNewDeviceInfo.deviceSerial, mNewDeviceInfo.productPara)==DeviceConstants.DEVICE_MOBILE_STEP){
						mOldDevice = mSelectedDevice;
						mSelectedDevice = null;
						mSelectedDevice = DeviceManager.getInstance(getApplicationContext()).getDeviceInstance(
								Common.getDeviceType(mNewDeviceInfo.deviceSerial, mNewDeviceInfo.productPara));
						mSelectedDevice.setCallBack(mBaseCallBack);
					}
					
					
//					mHandlerTemp.postDelayed(_connect_timeout, 1000*10);
//					connect(mNewDeviceInfo.deviceSerial);
					//切换设备：3、上传修改设备信息
					new UplaodBindedDeviceTask().execute();
				}else{
					message = Message.obtain(mHandlerActivity, DeviceConstants.UPLOAD_SUCCESS);
					mBundle.putString("UPLOAD_SUCCESS", "上传数据成功");
					message.setData(mBundle);
					message.sendToTarget();
				}
//				clearDeviceData();
				break;
			case DeviceConstants.UPLOAD_FAIL:
				Logger.d(TAG, "---上传设备数据失败");
				isSwicthDevice = false;
				if(isSwicthDevice){
					message = Message.obtain(mHandlerActivity, DeviceConstants.DEVCIE_SWITCH_FAIL);
					mBundle.putString("DEVCIE_SWITCH_FAIL", "切换失败");
					message.setData(mBundle);
					message.sendToTarget();
				}else{
					message = Message.obtain(mHandlerActivity, DeviceConstants.UPLOAD_FAIL);
					mBundle.putString("UPLOAD_FAIL", "上传数据成功");
					message.setData(mBundle);
					message.sendToTarget();
				}
				
				break;
			default:
				break;
			}
		}
		
	};
	
	private Bundle mBundle = new Bundle();
	
	private BaseDeviceInterface.BaseCallBack mBaseCallBack = new BaseCallBack(){

		@Override
		public void connected(int code, String msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "connected -- " + code);
			mHandlerTemp.removeCallbacks(_connect_timeout);
			Message message;
			if(isSwicthDevice){
				message = Message.obtain(mHandlerService, code);
			}else{
				message = Message.obtain(mHandlerActivity, code);
			}
			
			mBundle.putString("CONNECTED_SUCCESS", msg);
			message.setData(mBundle);
			message.sendToTarget();
		}

		@Override
		public void disConnected() {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "disConnected");
//            mSelectedDevice.disConnect();
		}

		@Override
		public void pedoDataPercent(int percent) {
			// TODO Auto-generated method stub
			Message msg = Message.obtain(mHandlerActivity, DeviceConstants.TRANSPORT_PERCENT);
			mBundle.putString("TRANSPORT_PERCENT", "传输进度:" + percent + "%");
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void pedoDataReceived(PedometorListInfo data,List<PedoDetailInfo> detail) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "pedoDataReceived -- ");
			if(data != null){
				// 保存到本地数据库
				PedoController.GetPedoController(getApplicationContext()).insertOrUpdatePedometer(data);
				for (PedoDetailInfo pedoDetailInfo : detail) {
					MHealthProviderMetaData.GetMHealthProvider(getApplicationContext()).updatePedoDetailData(pedoDetailInfo);
				}
				//上传到服务器
//				new UploadPedoAsk(data.datavalue, detail).execute();
			}
		}

		@Override
		public void realTimeDataReceived(PedometorDataInfo data) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "realTimeDataReceived -- ");
			Message msg = Message.obtain(mHandlerActivity, DeviceConstants.REALTIME_PEDO);
			mBundle.putString("STEP", data.stepNum);
			mBundle.putString("CAL", data.cal);
			mBundle.putString("DISTANCE", data.distance);
			try{
				mBundle.putString("STEPTIME", String.valueOf(Integer.parseInt(data.strength2)+Integer.parseInt(data.strength3)
						+Integer.parseInt(data.strength4)));
			}catch(NumberFormatException e){
				mBundle.putString("STEPTIME", data.strength2);
			}
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void realTimeEKGDataReceived(int key, Object data) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "realTimeEKGDataReceived -- ");
		}

		@Override
		public void ekgStop(int result, int finalHR) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "ekgStop -- ");
			if(mHandlerActivity != null) {
				Message msg=Message.obtain(mHandlerActivity, DeviceConstants.ECG_DEVICE_STOP);
				mBundle.putInt("EKGSTOP_RESULT", result);
				mBundle.putInt("EKGSTOP_FINALHR", finalHR);
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void ekgDataReceived(int key, Object data) {
			// TODO Auto-generated method stub
			if(mHandlerActivity != null) {
				Message msg=Message.obtain(mHandlerActivity, DeviceConstants.ECG_DEVICE_DATARECEIVED);
				Bundle tempbundle=new Bundle();
				tempbundle.putInt("EKGSTOP_key", key);
				msg.obj = data;
				msg.setData(tempbundle);
				msg.sendToTarget();
			}
		}

		@Override
		public void exception(int code, String msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "exception -- " + msg);
			Message message;
			if(isSwicthDevice){
				message = Message.obtain(mHandlerService, code);
			}else{
				message = Message.obtain(mHandlerActivity, code);
			}
			
			mBundle.putString("EXCEPTION_CONNECT", msg);
			message.setData(mBundle);
			message.sendToTarget();
		}
	};
	//上传数据超时
	private Runnable _bracelet_upload_timeout = new Runnable() {
		@Override
		public void run() {
			mHandlerTemp.removeCallbacks(_bracelet_upload_timeout);
			Logger.i(TAG, "---上传手环数据  超时，开始上传本地手环数据");
			
		}
	};
	// 连接设备超时
	private Runnable _connect_timeout = new Runnable() {
		@Override
		public void run() {
			mHandlerTemp.removeCallbacks(_connect_timeout);
			Logger.i(TAG, "---连接设备超时");
			disConnect();

			Message message;
			if(isSwicthDevice){
				message = Message.obtain(mHandlerService, DeviceConstants.CONNECTED_FAIL);
			}else{
				message = Message.obtain(mHandlerActivity, DeviceConstants.CONNECTED_FAIL);
			}
			mBundle.putString("CONNECTED_FAIL", "连接失败");
			message.setData(mBundle);
			message.sendToTarget();
		}
	};
	private Handler mHandlerTemp = new Handler();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Logger.i(TAG, "---onBind");
		int deviceCode = PreferencesUtils.getInt(getApplicationContext(), SharedPreferredKey.DEVICE_TYPE, 0);
//		if (deviceCode == DeviceConstants.DEVICE_MOBILE_STEP) {	
			mSelectedDevice = DeviceManager.getInstance(getApplicationContext()).getDeviceInstance(deviceCode);
			mSelectedDevice.setCallBack(mBaseCallBack);
//		}
		return new DeviceBind();
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO Auto-generated method stub
		super.unbindService(conn);
		Logger.i(TAG, "---unbindService");
	}
	
	@Override
	public void onCreate() {	
		// TODO Auto-generated method stub
		super.onCreate();
		Logger.i(TAG, "---onCreate");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.i(TAG, "---onDestroy");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Logger.i(TAG, "---onStartCommand");
//		return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
    
    /**
     * 切换设备
     * TODO
     * @param odlDevice 0
     * @param deviceCode 设备类型
     * @param address 设备MAC地址
     * @return void
     * @author shaoting.chen
     * @time 下午3:20:12
     */
    public void switchDevice(DeviceInfo oldDeviceInfo, DeviceInfo newDeviceInfo) {
    	isSwicthDevice = true; //正在切换设备
    	this.mOldDeviceInfo = oldDeviceInfo;
    	this.mNewDeviceInfo = newDeviceInfo;
        //切换设备：1、上传设备数据(本地数据)
    	mHandlerTemp.postDelayed(_bracelet_upload_timeout, 1000*15);
    	new UploadBraceletDataTask(mOldDeviceInfo.deviceSerial).execute();
    }


	/*****************************************BaseDeviceInterface设备基础属性--begin*******************************************************/

	public void connect(String address) {
		Logger.i(TAG, "---connect");
		mHandlerTemp.postDelayed(_connect_timeout, 1000*30);
		mSelectedDevice.connect(address.substring(2));
	}

	public void disConnect() {
		Logger.i(TAG, "---disConnect");
		mSelectedDevice.disConnect();
	}

	public void syncData(String startTime, String endTime) {
		mSelectedDevice.syncData(startTime, endTime);
	}

	public int getDeviceStatus() {
		return mSelectedDevice.getDeviceStatus();
	}

	public void startRealTime() {
		mSelectedDevice.startRealTime();
	}

	public void stopRealTime() {
		mSelectedDevice.stopRealTime();
	}

	public void startRealTimeEKG() {
		mSelectedDevice.startRealTimeEKG();
		if(mHandlerActivity!=null)
		{
			Message msg = Message.obtain(mHandlerActivity, DeviceConstants.ECG_DEVICE_STATUS);
			String position=PreferencesUtils.getString(mContext, SharedPreferredKey.POSITION, "左手");
			if(position.equals("右手")){
				mBundle.putString("ECG_DEVICE_STATUS", "正在测量，请"+position+"佩戴手环，左手食指放在金属片上");
			}else{
				mBundle.putString("ECG_DEVICE_STATUS", "正在测量，请"+position+"佩戴手环，右手食指放在金属片上");
			}				
			msg.setData(mBundle);
			msg.sendToTarget();
		}
	}

	public void stopRealTimeEKG() {
		mSelectedDevice.stopRealTimeEKG();
	}
	/**
	 * 擦除设备历史数据
	 */
	public void clearDeviceData(){
		Logger.i(TAG, "---clearDeviceData");
		mOldDevice.clearDeviceData();
		mOldDevice.disConnect();
	}
	
//	public void updateFirmware(String fileUrl, String fileName){
//		if(StringUtils.isNotBlank(fileUrl) && StringUtils.isNotBlank(fileName)){
//			mSelectedDevice.fwDown(fileUrl, fileName);
//		}
//	}
//	public String getFwVersion(){
//		return mSelectedDevice.getFwVersion();
//	}

	/*****************************************BaseDeviceInterface设备基础属性--end*******************************************************/

	public class DeviceBind extends Binder {
		
		public DeviceManagerService getService(Handler handler) {
			mHandlerActivity = handler;
			return DeviceManagerService.this;
		}
	}
	
	/**
	 * 上传设备数据
	 * @type UploadPedoAsk
	 * TODO
	 * @author shaoting.chen
	 * @time 2015年4月20日上午11:13:47
	 */
	class UploadPedoAsk extends AsyncTask<Null, Null, Integer> {

		private List<PedometorDataInfo> pedolist;
		private PedoDetailInfo pedoDetail;

		public UploadPedoAsk(List<PedometorDataInfo> pedolist, PedoDetailInfo pedoDetail) {
			this.pedolist = pedolist;
			this.pedoDetail = pedoDetail;
		}

		@Override
		protected Integer doInBackground(Null... params) {
			int isSucess = 0;// 0成功 -1失败 1没有数据
			try {
				if (pedolist.size() == 0) {
					return 1;
				}
				for (PedometorDataInfo pedo : pedolist) {
					if(!UploadManager.uploadPedo(pedo))
					{
						isSucess=-1;
					}
				}
				if(!UploadManager.uploadPedoDetail(pedoDetail, pedolist.get(0).deviceId))
				{
					isSucess=-1;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// BaseToast("上传"+e.getMessage());
				return -1;
			}
			return isSucess;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);	
			if(mHandlerActivity!=null)
			{
				Message msg;
				switch (result) {
				case 0:
					msg = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_SUCCESS);
					mBundle.putString("UPLOAD_SUCCESS", "数据上传成功");
					break;
				case -1:
					msg = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_FAIL);
					mBundle.putString("UPLOAD_FAIL", "数据上传失败");
					break;
				case 1:
					msg = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_SUCCESS);
					mBundle.putString("UPLOAD_SUCCESS", "当前数据已同步");
					break;
				default:
					msg = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_SUCCESS);
					mBundle.putString("UPLOAD_SUCCESS", "当前数据已同步");
					break;
				}
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
	}
	
	/**
	 * 提交本地手环数据
	 */
	private class UploadBraceletDataTask extends AsyncTask<Null, Null, Integer> {
		private String deviceSerial;
		public UploadBraceletDataTask(String deviceSerial) {
//			showProgress("上传本地手环数据...");
			this.deviceSerial = deviceSerial;
		}

		@Override
		protected Integer doInBackground(Null... params) {
			PedometorDataInfo data = null;
			data = PedoController.GetPedoController(getApplicationContext()).getPedometerByDay(deviceSerial,
					new Date(System.currentTimeMillis()));

			if(data != null){
				return (UploadManager.uploadPedo(data)) ? 0 : 1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Message message;
			if (result == 0) {	
				message = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_SUCCESS);
				mBundle.putString("UPLOAD_SUCCESS", "数据上传成功");

			} else {
				message = Message.obtain(mHandlerService, DeviceConstants.UPLOAD_FAIL);
				mBundle.putString("UPLOAD_FAIL", "数据上传失败");
			}
			message.setData(mBundle);
			message.sendToTarget();
		}
	}
	
	/**
	 * 上传设备信息
	 */
	private class UplaodBindedDeviceTask extends AsyncTask<Null, Null, Integer> {
		
		BackInfo uploadResult;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Null... params) {
			if (!NetworkTool.isOnline(getApplicationContext())) {
				return -1;
			}
			uploadResult = new BackInfo();
			Logger.i("cjz", "UplaodBindedDeviceTask");
			return DataSyn.getInstance().uploadBindDeviceInfo(mOldDeviceInfo, mNewDeviceInfo, uploadResult);

		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Message message;
			if (result == 0) {
				Logger.i(TAG, "---上传设备信息成功");
				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				Editor editor = info.edit();
				editor.putString(SharedPreferredKey.DEVICE_ID, mNewDeviceInfo.deviceSerial);
				editor.putString(SharedPreferredKey.DEVICE_ADDRESS, mNewDeviceInfo.deviceSerial.substring(2));
				editor.putInt(SharedPreferredKey.DEVICE_TYPE, Common.getDeviceType(mNewDeviceInfo.deviceSerial, mNewDeviceInfo.productPara));
				editor.putString(SharedPreferredKey.DEVICE_NAME, mNewDeviceInfo.productName);
				editor.putString(SharedPreferredKey.DEVICE_MODEL, mNewDeviceInfo.productPara);
				editor.commit();
				
				message = Message.obtain(mHandlerActivity, DeviceConstants.DEVCIE_SWITCH_SUCCESS);
				mBundle.putString("DEVCIE_SWITCH_SUCCESS", "切换成功");
			} else {
				Logger.i(TAG, "---上传设备信息失败");
				String resultStr = "切换失败";
				if (result == 500) {
					resultStr = uploadResult.reason;
				}else {
					Logger.i("cjz", "网络异常！");
					resultStr = mContext.getResources().getString(R.string.MESSAGE_INTERNET_ERROR);
				}
				message = Message.obtain(mHandlerActivity, DeviceConstants.DEVCIE_SWITCH_FAIL);
				mBundle.putString("DEVCIE_SWITCH_FAIL", resultStr);

			}
			message.setData(mBundle);
			message.sendToTarget();
			isSwicthDevice = false;
		}
	}

}
