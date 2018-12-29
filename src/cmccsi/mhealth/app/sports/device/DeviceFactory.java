package cmccsi.mhealth.app.sports.device;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.device.BaseDeviceInterface.BaseCallBack;
import cmccsi.mhealth.app.sports.pedo.PedoController;

import com.cmcc.bracelet.lsjx.libs.JWDeviceManager;

public class DeviceFactory {
	
	private static String TAG="DeviceFactory";
	
	private BaseDeviceInterface dManager = null;
	private Context mContext;
	private Handler mHandlerActivity;
	private Handler mHandlerTemp = new Handler();
	private Bundle mBundle = new Bundle();
	private int mDeviceType=-1;
	
	private DeviceFactory(Context context,Handler handler){
		this.mContext=context;
		this.mHandlerActivity=handler;
	}
	
	/**
	 * 通过设备编号获取设备实例
	 * 
	 */
	public static DeviceFactory getDeviceInstance(Context context,Handler handler) {
		return new DeviceFactory(context,handler);		
	}
	
	/**
	 * 初始化设备类型
	 * @param deviceCode DeviceConstants中设备类型的一种
	 */
	public void initDevice(int deviceCode){
		mDeviceType=deviceCode;
		switch (deviceCode) {
		case DeviceConstants.DEVICE_MOBILE_STEP: //手机计步设备
			dManager = (BaseDeviceInterface) new MobileDevice(mContext);
			break;
		case DeviceConstants.DEVICE_BRACLETE_BEATBAND: //神念手环设备
			dManager = (BaseDeviceInterface) new BeatBandDevice(mContext);
			break;
		case DeviceConstants.DEVICE_BRACLETE_JW: //叮当手环设备
			dManager = (BaseDeviceInterface) JWDeviceManager.getInstance(mContext);
			break;
		case DeviceConstants.DEVICE_PEDOMETER: //计步器
			dManager = (BaseDeviceInterface) new PedometorDevice(mContext);
			break;
		case DeviceConstants.DEVICE_BRACLETE_JW201: //叮当手环设备201
			dManager = (BaseDeviceInterface) new PedometorDevice(mContext);
			break;
		default:
			break;
		}
		dManager.setCallBack(mBaseCallBack);
	}
	
	/**
	 * 连接设备
	 * @param deviceID 设备ID
	 */
	public void connect(String deviceID) {
		Logger.i(TAG, "---connect");
		if(deviceID==null||deviceID.equals("")||deviceID.length()<3){
			Message message;
			message = Message.obtain(mHandlerActivity, DeviceConstants.EXCEPTION_CONNECT);
			mBundle.putString("EXCEPTION_CONNECT", "设备ID不合法");
			message.setData(mBundle);
			message.sendToTarget();
		}
		mHandlerTemp.postDelayed(_connect_timeout, 1000*30);
		dManager.connect(deviceID.substring(2));
	}

	/**
	 * 断开设备连接
	 */
	public void disConnect() {
		Logger.i(TAG, "---disConnect");
		dManager.disConnect();
	}

	/**
	 * 获取设备数据
	 * @param startTime 开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime 结束时间 yyyy-MM-dd HH:mm:ss
	 */
	public void syncData(String startTime, String endTime) {
		dManager.syncData(startTime, endTime);
	}

	/**
	 * 获取设备状态
	 * @return 设备状态
	 */
	public int getDeviceStatus() {
		return dManager.getDeviceStatus();
	}

	public void startRealTime() {
		dManager.startRealTime();
	}

	public void stopRealTime() {
		dManager.stopRealTime();
	}

	public void startRealTimeEKG() {
		dManager.startRealTimeEKG();
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
		dManager.stopRealTimeEKG();
	}
	/**
	 * 擦除设备历史数据
	 */
	public void clearDeviceData(){
		Logger.i(TAG, "---clearDeviceData");
		dManager.clearDeviceData();
	}
	
	private BaseDeviceInterface.BaseCallBack mBaseCallBack = new BaseCallBack(){

		@Override
		public void connected(int code, String msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "connected -- " + code);
			mHandlerTemp.removeCallbacks(_connect_timeout);
			Message message;
			message = Message.obtain(mHandlerActivity, code);			
			mBundle.putString("CONNECTED_SUCCESS", msg);
			message.setData(mBundle);
			message.sendToTarget();
		}

		@Override
		public void disConnected() {
			// TODO Auto-generated method stub
			Logger.d(TAG, "BaseCallBack -- " + "disConnected");
//            dManager.disConnect();
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
				PedoController.GetPedoController(mContext).insertOrUpdatePedometer(data);
				for (PedoDetailInfo pedoDetailInfo : detail) {
					MHealthProviderMetaData.GetMHealthProvider(mContext).updatePedoDetailData(pedoDetailInfo);
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
			message = Message.obtain(mHandlerActivity, code);
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
			message = Message.obtain(mHandlerActivity, DeviceConstants.CONNECTED_FAIL);
			mBundle.putString("CONNECTED_FAIL", "连接失败");
			message.setData(mBundle);
			message.sendToTarget();
		}
	};

}
