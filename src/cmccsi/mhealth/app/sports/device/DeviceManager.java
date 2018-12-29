package cmccsi.mhealth.app.sports.device;

import android.content.Context;

import com.cmcc.bracelet.lsjx.libs.JWDeviceManager;

public class DeviceManager{
	private BaseDeviceInterface dManager = null;
	
	private static DeviceManager mDeviceManager  = null;
	
	private Context mContext;
	
	private DeviceManager(Context context){
		this.mContext = context;
	}
	
	public static DeviceManager getInstance(Context context){
		if(mDeviceManager == null){
			mDeviceManager = new DeviceManager(context);
		}
		return mDeviceManager;
	}

	/**
	 * 通过设备编号获取设备实例
	 * 
	 * @param deviceCode
	 * @return
	 */
	public BaseDeviceInterface getDeviceInstance(int deviceCode) {

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
		return dManager;

	}
}
