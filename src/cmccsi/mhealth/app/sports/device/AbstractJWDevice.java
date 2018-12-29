package cmccsi.mhealth.app.sports.device;

/**
 * JW手环设备抽象类
 * 
 * @type AbstractJWDevice
 * TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:38:09
 */
public abstract class AbstractJWDevice implements BaseDeviceInterface{

	/**
	 * 连接设备（包括绑定）
	 */
	public abstract void connect(String address);
	/**
	 * 断开设备
	 */
	public abstract void disConnect();
	
	/**
	 * 同步数据（历史数据）
	 */
	public abstract void syncData(String startTime,String endTime);
	
	/**
	 * 获取设备状态（连接状态）
	 */
	public abstract int getDeviceStatus();
	
	/**
	 * 开启设备运动数据实时传输
	 */
	public abstract void startRealTime() ;
	
	/**
	 * 停止设备运动数据实时传输
	 */
	public abstract void stopRealTime();

	@Override
	public abstract void setCallBack(BaseCallBack cb);
	
	/**
	 * 清楚设备数据
	 */
	@Override
	public abstract void clearDeviceData();
	
	/**
	 * 升级固件
	 */
	public void updateOTAData(String file_path) {
		// TODO Auto-generated method stub
		
	}
	/************以下方法 此设备不需要实现或者使用**************/
	
	@Override
	public void startRealTimeEKG(){
		
	}
	@Override
	public void updateOTAData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stopRealTimeEKG() {
		// TODO Auto-generated method stub
		
	}
}
