package cmccsi.mhealth.app.sports.device;

import java.util.List;

import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;

/**
 * 设备基础属性
 * @type BaseDeviceInterface
 * TODO
 * @author shaoting.chen
 * @time 2015年4月16日下午2:35:33
 */
public interface BaseDeviceInterface {
	
	/**
	 * 连接设备（包括绑定）
	 */
	public void connect(String address);
	
	/**
	 * 断开设备
	 */
	public void disConnect();
	
	/**
	 * 同步数据（历史数据）
	 */
	public void syncData(String startTime, String endTime);
	
	/**
	 * 获取设备状态（连接状态）
	 */
	public int getDeviceStatus();
	
	/**
	 * 开启设备运动数据实时传输
	 */
	public void startRealTime();
	
	/**
	 * 停止设备运动数据实时传输
	 */
	public void stopRealTime();
	
	/**
	 * 开启设备心电数据实时传输
	 */
    public void startRealTimeEKG();
	
	/**
	 * 停止设备心电数据实时传输
	 */
	public void stopRealTimeEKG();
	/**
	 * 升级固件
	 */
	public void updateOTAData();
	
	/**
	 * 清楚设备数据
	 * TODO
	 * @return void
	 * @author shaoting.chen
	 * @time 下午4:03:08
	 */
	public void clearDeviceData();
	
	public void setCallBack(BaseCallBack cb);
	
	/**
	 * 设备数据回调方法
	 * @type BaseCallBack
	 * TODO
	 * @author shaoting.chen
	 * @time 2015年4月17日上午9:16:11
	 */
	public interface BaseCallBack {
		
		/**
		 * 连接成功 回调
		 * @param code 0-success 1-fail
		 */
		public void connected(int code, String msg); 
		
		/**
		 * 断开连接
		 */
		public void disConnected();
		/**
		 * 同步数据 进度
		 */
		public void pedoDataPercent(int percent);

		/**
		 * 运动数据同步 数据回调
		 * data1 简包数据
		 * data2祥包数据
		 */
		public void pedoDataReceived(PedometorListInfo data1, List<PedoDetailInfo> data2);
			
		/**
		 * 运动实时数据 数据回调
		 */
		public void realTimeDataReceived(PedometorDataInfo data);
		
		/**
		 * 心电实时数据 数据回调
		 */
        public void realTimeEKGDataReceived(int key, Object data);
		
        /**
    	 * 心电停止测量
    	 */
		public void ekgStop(int result, int finalHR);
		
		/**
		 * 心电数据同步 数据回调
		 */
		public void ekgDataReceived(int key, Object data);
		
		/**
		 * 异常回调
		 */
		public void exception(int code, String msg);
	}

}
