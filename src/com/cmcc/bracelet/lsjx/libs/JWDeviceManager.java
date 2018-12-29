package com.cmcc.bracelet.lsjx.libs;

import java.io.File;
import java.util.List;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import cmccsi.mhealth.app.sports.common.utils.LogUtils;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.device.AbstractJWDevice;
import cmccsi.mhealth.app.sports.device.DeviceConstants;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 * 从动关系的手环操作类，外部主要调用此类进行对手环的操作。
 */
public class JWDeviceManager extends AbstractJWDevice {
	private static final String TAG = JWDeviceManager.class.getSimpleName();
	public static Context mContext;
	private LSJXBleApi bleApi;
	private Handler mHandler;
	private String mStartTime,mEndTime;
	private static JWDeviceManager mJwDeviceManager;
	/**
	 * 是否开启实时显示设备数据 true:开启实时 false:关闭实时（默认状态）
	 */
	public static boolean isStartRealTime = false;

	private JWDeviceManager() {
	}

	public static JWDeviceManager getInstance(Context context) {
		mContext = context;
		if (null == mJwDeviceManager) {
			mJwDeviceManager = new JWDeviceManager();
			mJwDeviceManager.bleApi = LSJXBleApi.getInstance(mContext);
			mJwDeviceManager.mHandler = new Handler();
		}
		return mJwDeviceManager;

	}

	/**
	 * 连接设备
	 */
	@Override
	public void connect(String address) {
		bleApi.doConnect(address);
	}

	/**
	 * 断开设备
	 */
	@Override
	public void disConnect() {
		bleApi.disconnect();
	}

	/**
	 * 同步数据 格式为： 2015-05-26 15:30:00
	 */
	@Override
	public void syncData(String startTime, String endTime) {
		this.mStartTime = startTime;
		this.mEndTime = endTime;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				BluetoothGattService gattService = bleApi.getGattService();
				if (gattService == null) {
					JWDeviceManager.getInstance(mContext).getBaseCallBack()
							.exception(DeviceConstants.CONNECTED_FAIL, "手环连接失败");
					return;
				}
//				bleApi.getHistory();
				bleApi.getSyncData(mStartTime,mEndTime);
			}
		}, Config.SECONDNUM);

	}

	/**
	 * 清除设备数据
	 */
	@Override
	public void clearDeviceData() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				BluetoothGattService gattService = bleApi.getGattService();
				if (gattService == null) {
					JWDeviceManager.getInstance(mContext).getBaseCallBack()
							.exception(DeviceConstants.CONNECTED_FAIL, "手环连接失败");
					return;
				}
				bleApi.delAll();
			}
		}, Config.SECONDNUM);

		try {
			DbUtils dbUtils = DbUtils.create(mContext);
			List<LSJXMsg> stus = dbUtils.findAll(Selector.from(LSJXMsg.class).where("date", "<",
					(Long.parseLong(Protocol.getStringDate())) / 10000));
			dbUtils.deleteAll(stus);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w(TAG, "数据库删除失败");
		}
	}

	/**
	 * 获取链接状态 0：无连接 1连接中 2连接
	 */
	@Override
	public int getDeviceStatus() {
		return bleApi.mConnectionState;
	}

	/**
	 * app设置CallBack
	 */
	@Override
	public void setCallBack(BaseCallBack cb) {
		this.cb = cb;
	}

	/**
	 * 由App设置的BaseCallBack，供BLE设备使用
	 */
	private BaseCallBack cb = null;

	/**
	 * 获取app设置的BaseCallBack，一般由BLE设备使用，注意非空判断
	 * 
	 * @return 由App设置的BaseCallBack
	 */
	public BaseCallBack getBaseCallBack() {
		return cb;
	}

	@Override
	public void startRealTime() {
		isStartRealTime = true;
	}

	@Override
	public void stopRealTime() {
		isStartRealTime = false;
	}

	/**
	 * 升级固件
	 */
	@Override
	public void updateOTAData(final String fw_file_path) {
		// TODO Auto-generated method stub
//		final String fw_file_path = "/storage/emulated/0/Android/data/com.test.ble.ota/files/DingDang_201_ota_150521.bin";
		File current = new File(fw_file_path);
		if (!current.exists()) {
			LogUtils.e("|", ":No such file or directory"); 
		}
		if (!current.canRead()) {
			LogUtils.e("|",  ":No permission to open " + fw_file_path);
		}
		LSJXBleApi.progress = 0;
		LSJXBleApi.is_update = true;
		
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run() {
				bleApi.startOtaUpdate(fw_file_path);
				}						
			},1000);
	}
	
//	public void setMsgRoad() {
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				BluetoothGattService gattService = bleApi.getGattService();
//				if (gattService == null) {
//					JWDeviceManager.getInstance(mContext).getBaseCallBack()
//							.exception(DeviceConstants.CONNECTED_FAIL, "未连接到Ble设备");
//					return;
//				}
//				bleApi.setCharacteristicNotification(gattService.getCharacteristics().get(1), true);
//			}
//		}, Config.SECONDNUM);
//	}
	
	public String getDeviceInfo(){

		if(StringUtils.isBlank(Protocol.DEVICE_MODEL)){
			return null;
		}
		return Protocol.DEVICE_MODEL;
	}
}
