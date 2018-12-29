package cmccsi.mhealth.app.sports.device;

/**
 * 手机计步设备抽象类
 * 
 * @type AbstractMobileDevice
 * TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:34:35
 */
public abstract class AbstractMobileDevice implements BaseDeviceInterface{
	
	@Override
	public abstract void connect(String address);

	@Override
	public abstract void disConnect();

	/**
	 * 同步数据
	 */
	@Override
	public abstract void syncData(String startTime, String endTime);

	/**
	 * 获取设备状态
	 */
	@Override
	public abstract int getDeviceStatus();

	@Override
	public abstract void setCallBack(BaseCallBack cb);

	
	/************以下方法 此设备不需要实现或者使用**************/
	@Override
	public void startRealTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRealTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRealTimeEKG() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRealTimeEKG() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearDeviceData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOTAData() {
		// TODO Auto-generated method stub
		
	}
}
