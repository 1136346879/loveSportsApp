package cmccsi.mhealth.app.sports.device;

/**
 * BeatBand设备抽象类
 * 
 * @type AbstractBeatBandDevice
 * TODO
 * @author shaoting.chen
 * @time 2015年4月20日上午9:37:49
 */
public abstract class AbstractBeatBandDevice implements BaseDeviceInterface{
	
	/**
	 * 连接设备（包括绑定）
	 */
	@Override
	public abstract void connect(String address);

	/**
	 * 断开设备
	 */
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

	/**
	 * 开启实时数据
	 */
	@Override
	public abstract void startRealTime();

	/**
	 * 停止实时数据传输
	 */
	@Override
	public abstract void stopRealTime();

	/**
	 * 开启心电实时数据
	 */
	@Override
	public abstract void startRealTimeEKG();

	@Override
	public abstract void stopRealTimeEKG();

	/**
	 * 擦除设备历史数据
	 */
	@Override
	public abstract void clearDeviceData();

	@Override
	public abstract void setCallBack(BaseCallBack cb);

	/**
	 * 升级固件
	 */
	@Override
	public abstract void updateOTAData();
}
