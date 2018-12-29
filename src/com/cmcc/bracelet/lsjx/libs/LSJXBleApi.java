package com.cmcc.bracelet.lsjx.libs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.device.DeviceConstants;

import com.quintic.libota.BluetoothLeInterface;
import com.quintic.libota.bleGlobalVariables.otaResult;
import com.quintic.libota.otaManager;

/**
 * 单例模式，手环真正的API
 */
public class LSJXBleApi {
	private static final String TAG = LSJXBleApi.class.getSimpleName();
	private Context mContext;
	private Handler mHandler;

	/**
	 * BLE蓝牙设备的地址
	 */
	private String mBluetoothDeviceAddress = "";
	/**
	 * BLE蓝牙设备的地址
	 */
	private String wantConnectDeviceAddress = "";
	/**
	 * 蓝牙适配器
	 */
	private BluetoothAdapter mBluetoothAdapter;
	/**
	 * 周边蓝牙设备特性
	 */
	private BluetoothGattCharacteristic gattCharacteristic;

	/**
	 * 扫描到的所有BLE设备
	 */
	private ArrayList<BluetoothDevice> mBleDevices = null;
	/**
	 * 中央BluetoothGatt
	 */
	private BluetoothGatt mBluetoothGatt;
	/**
	 * 是否处在扫描设备的过程中 true：正在扫描周边设备 false：没有扫描周边设备
	 */
	private boolean mScanning = false;

	/**
	 * 如果开启扫描周边设备，10秒后自动关闭扫描
	 */
	private static final long SCAN_PERIOD = 10000;
	/**
	 * 特定的UUID
	 */
	final static UUID UUID_TOUCHUAN_RECEIVE = UUID.fromString(LSJXGattAttributes.TOUCHUAN_RECEIVE);;
	/**
	 * app与ble设备之间的连接状态 0：没有连接 1：连接中 2已经连接
	 */
	public int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	/**
	 * BLE扫描回调
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	/**
	 * 中央BluetoothGatt回掉
	 */
	private BluetoothGattCallback mGattCallback;

	private byte[] mData = null;
	private BluetoothGattService mGattService = null;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	public final static String ACTION_GATT_CONNECTED = "com.example.bledemo.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bledemo.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bledemo.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bledemo.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bledemo.EXTRA_DATA";

	@SuppressLint("InlinedApi")
	private LSJXBleApi() {
	}

	private static LSJXBleApi mLsjxBleApi = null;

	/**
	 * 获取实例
	 * 
	 * @param context
	 *            上下文
	 * @return 本类的实例
	 */
	public static LSJXBleApi getInstance(Context context) {
		if (null == mLsjxBleApi) {
			mLsjxBleApi = new LSJXBleApi();
		}
		mLsjxBleApi.init(context);
		return mLsjxBleApi;
	}

	/**
	 * 初始化设备
	 * 
	 * @param context
	 */
	@SuppressLint("InlinedApi")
	private void init(Context context) {
		mContext = context;
		// 检查系统是否支持BLE
		if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(mContext, "系统不支持手环设备", Toast.LENGTH_SHORT).show();
			return;
		}

		// 获取适配器
		final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(mContext, "无法获取蓝牙适配器", Toast.LENGTH_SHORT).show();
			return;
		}
		// 之接控制开启蓝牙
		mBluetoothAdapter.enable();
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
				mBleDevices.add(device);
				if (wantConnectDeviceAddress.equals(device.getAddress())) {
					stopScan();
					connectByAddress(wantConnectDeviceAddress);
				}
			}
		};

		mGattCallback = new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					mConnectionState = STATE_CONNECTED;
					mBluetoothGatt.discoverServices();
					JWDeviceManager.getInstance(mContext).getBaseCallBack()
							.connected(DeviceConstants.CONNECTED_SUCCESS, "手环连接成功");
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					mConnectionState = STATE_DISCONNECTED;
					JWDeviceManager.getInstance(mContext).getBaseCallBack().disConnected();
				}
			}

			// 其中，discoverService方式是异步的，它的回调方法是下面代码中的onServiceDiscovered。
			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					displayGattServices(mBluetoothGatt.getServices());
				} else {
					Log.w(TAG, "onServicesDiscovered received: " + status);
				}
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					updateData(characteristic);
				}
			}

			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				// TODO Auto-generated method stub
				if (is_update) { // 固件升级
					if (status == BluetoothGatt.GATT_SUCCESS) {
						updateManager.notifyWriteDataCompleted();
					}
				} else {
					super.onCharacteristicWrite(gatt, characteristic, status);
				}
			}

			// 如果notificaiton方式对于某个Characteristic是enable的，
			// 那么当设备上的这个Characteristic改变时，手机上的onCharacteristicChanged()
			// 回调就会被促发。如下所示：
			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
				if (is_update) {
					byte[] notifyData = characteristic.getValue();
					updateManager.otaGetResult(notifyData);
				} else {
					updateData(characteristic);
				}
			}
		};

		mBleDevices = new ArrayList<BluetoothDevice>();
		mHandler = new Handler();
	}

	/**
	 * 开始扫描周边蓝牙设备
	 */
	public void startScan() {
		if (mBluetoothAdapter != null)
			scanLeDevice(true);
	}

	private void connectByAddress(String address) {
		if (connectBleAddress(address)) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					BluetoothGattService gattService = getGattService();
					if (gattService == null) {
						return;
					}
					setting();
					// setCharacteristicNotification(gattService.getCharacteristics().get(1),
					// true);
				}
			}, Config.SECONDNUM);
		}
	}

	/**
	 * 停止扫描周边设备
	 */
	public void stopScan() {
		if (mBluetoothAdapter != null)
			scanLeDevice(false);
	}

	/**
	 * 获取所有周边扫描到的蓝牙设备
	 * 
	 * @return
	 */
	public ArrayList<BluetoothDevice> getBleDevices() {
		return mBleDevices;
	}

	public boolean isScanning() {
		return mScanning;
	}

	/**
	 * 通过蓝牙实例连接蓝牙设备（通过实例获取蓝牙地址）
	 * 
	 * @param dev
	 *            蓝牙设备实例
	 * @return 是否成功连接成功 true：成功连接 false：连接失败
	 */
	public boolean connectBleDevice(BluetoothDevice dev) {
		if (mBluetoothAdapter == null || dev == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		String address = dev.getAddress();
		return connectBleAddress(address);
	}

	/**
	 * 通过蓝牙地址连接蓝牙设备
	 * 
	 * @param address
	 *            例如（08:7C:BE:29:92:A5）蓝牙地址
	 * @return 是否成功连接成功 true：成功连接 false：连接失败
	 */
	public boolean connectBleAddress(String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				mBluetoothGatt = device.connectGatt(MapApplication.getInstance(), false, mGattCallback);
				return null;
			}
		}.execute();
		// mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mConnectionState = STATE_CONNECTING;
		mBluetoothDeviceAddress = address;
		return true;
	}

	/**
	 * 断开与远程设备的GATT连接。
	 */
	public void disconnect() {
		if (null == mBluetoothAdapter || null == mBluetoothGatt || null == mGattService) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			mConnectionState = STATE_DISCONNECTED;
			return;
		}
		setCharacteristicNotification(mGattService.getCharacteristics().get(1), false);
		mBluetoothGatt.disconnect();
		mConnectionState = STATE_DISCONNECTED;
		close();
	}

	/**
	 * 关闭GATT Client端。
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mConnectionState = STATE_DISCONNECTED;
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (null == characteristic || null == mBluetoothGatt) {
			return false;
		}
		boolean flag = mBluetoothGatt.writeCharacteristic(characteristic);
		return flag;
	}

	public boolean setValue(BluetoothGattCharacteristic characteristic, byte[] value) {
		characteristic.setValue(value);
		return writeCharacteristic(characteristic);
	}

	/**
	 * 收到GATT通知 如果设备主动给手机发信息，则可以通过notification的方式，这种方式不用手机去轮询地读设备上的数据。
	 * 手机可以用如下方式给设备设置notification功能
	 * 
	 * @param characteristic
	 * @param enabled
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		if (UUID_TOUCHUAN_RECEIVE.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
					.fromString(LSJXGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	/**
	 * 与手环连接后，首先要做的事——app向ble设备同步系统时间
	 */
	public void setting() {
		// 先发送时间进行同步，看看得到的结果
		Calendar mCalendar = Calendar.getInstance();
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		int second = mCalendar.get(Calendar.SECOND);
		// byte[0]表示id，id为0x01表示app向ble设备同步系统时间
		final byte[] byte_send = new byte[] { 1, (byte) year, (byte) (year >> 8), (byte) month, (byte) day, (byte) hour,
				(byte) minute, (byte) second };
		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_TimeTheAlarmClock, byte_send));
			}
		}, 200);

		SharedPreferences sp = mContext.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		/* *
		 * 设置身高、体重、性别
		 */
		byte sex;
		if ("1".equals(sp.getString(SharedPreferredKey.GENDER, "1"))) {
			sex = (byte) 0x00;// 男
		} else {
			sex = (byte) 0x01;// 女
		}
		double userHeight, userweight;
		userHeight = Double.parseDouble(sp.getString(SharedPreferredKey.HEIGHT, "175"));
		userweight = Double.parseDouble(sp.getString(SharedPreferredKey.WEIGHT, "70"));
		final byte[] user_info_data = new byte[] { 1, (byte) userHeight, (byte) userweight, (byte) sex, (byte) 0x00, (byte) 0x00 };
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, user_info_data));
			}
		}, 200);
		/* *
		 * 设置手环佩戴模式
		 */
		byte bandOnRightOrLeftData;
		if ("左手".equals(sp.getString(SharedPreferredKey.POSITION, "左手"))) {
			bandOnRightOrLeftData = (byte) 0x00;// 左
		} else {
			bandOnRightOrLeftData = (byte) 0x01;// 右
		}
		final byte[] bandOnRightOrLeft = new byte[] { 6, bandOnRightOrLeftData };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, bandOnRightOrLeft));
			}
		}, 200);
		/* *
		 * 设置目标数
		 */
		int userTargetStep = Integer.parseInt(sp.getString(SharedPreferredKey.TARGET_STEP, "175"));
		final byte[] goalSteps = new byte[] { 2, (byte) userTargetStep, (byte) 0xffffffff };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_StepOfReaTimeData, goalSteps));
			}
		}, 200);
		/* *
		 * 设置出生日期
		 */
		int userBirthdayYear, userBirthdayMonth, userBirthdayDay;
		String userBirthday = sp.getString(SharedPreferredKey.BIRTHDAY, "1990-01-01");
		userBirthdayYear = Integer.parseInt(userBirthday.substring(0, 4));
		userBirthdayMonth = Integer.parseInt(userBirthday.substring(5, 7));
		userBirthdayDay = Integer.parseInt(userBirthday.substring(8, 10));
		final byte[] birthday = new byte[] { 8, (byte) userBirthdayYear, (byte) userBirthdayMonth, (byte) userBirthdayDay };
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, birthday));
			}
		}, 200);
		/**
		 * 设置运动闹铃
		 */
		if (sp.getBoolean(SharedPreferredKey.ENABLE_SPORT, false)) {
			int sportRepeat = sp.getInt(SharedPreferredKey.DAYS2, -1);
			int sportHour = sp.getInt(SharedPreferredKey.SPORT_HOUR, 17);
			int sportMin = sp.getInt(SharedPreferredKey.SPORT_MINUTE, 30);
			sportHour = sportHour > 24 ? 17 : sportHour;
			int[] days = { 0, 0, 0, 0, 0, 0, 0 };
			if (sportRepeat != -1) {
				String code = Integer.toBinaryString(sportRepeat);
				if (code.length() < 8) {
					int differ = 8 - code.length();
					for (int x = 0; x < differ; x++) {
						code = "0" + code;
					}
				}
				code = code.substring(0, code.length() - 1);
				char[] chars = code.toCharArray();
				for (int i = 0; i < 7; i++) {
					if (chars[i] == '1') {
						days[i] = (i + 1);
					}
				}
			}
			setSportClock(days, sportHour, sportMin); // 15:15
		} else {
			setSportClockClose();
		}
		/**
		 * 设置睡眠闹铃
		 */
		if (sp.getBoolean(SharedPreferredKey.ENABLE_SLEEP, false)) {
			int sleepRepeat = sp.getInt(SharedPreferredKey.DAYS, -1);
			int sleepHour = sp.getInt(SharedPreferredKey.SLEEP_HOUR, 17);
			int sleepMin = sp.getInt(SharedPreferredKey.SLEEP_MINUTE, 30);
			sleepHour = sleepHour > 24 ? 17 : sleepHour;
			int[] days = { 0, 0, 0, 0, 0, 0, 0 };
			if (sleepRepeat != -1) {
				String code = Integer.toBinaryString(sleepRepeat);
				if (code.length() < 8) {
					int differ = 8 - code.length();
					for (int x = 0; x < differ; x++) {
						code = "0" + code;
					}
				}
				code = code.substring(0, code.length() - 1);
				char[] chars = code.toCharArray();
				for (int i = 0; i < 7; i++) {
					if (chars[i] == '1') {
						days[i] = (i + 1);
					}
				}
			}
			setSleepClock(days, sleepHour, sleepMin);
		} else {
			setSleepClockClose();
		}
		getDeviceInfo();
		getDeviceVersion();
	}

	/**
	 * 获取原始数据（从BLE设备中）
	 * 
	 * @return
	 */
	public byte[] getData() {
		return mData;
	}

	/**
	 * 获取周边蓝牙服务
	 */
	public BluetoothGattService getGattService() {
		return mGattService;
	}

	/**
	 * 扫描设备
	 * 
	 * @param enable
	 *            true开始扫描，false停止扫描
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private void updateData(final BluetoothGattCharacteristic characteristic) {
		mData = characteristic.getValue();
		Log.e("updateData", "" + mData);
		// 这是直接把没经过解析的数据给存放到StringBuilder中
		final StringBuilder stringBuilder = new StringBuilder(mData.length);
		for (byte byteChar : mData)
			stringBuilder.append(String.format("%02X ", byteChar));
		// 得到的data进行解析，看看是否是自己的数据
		Protocol.readByte(mData, JWDeviceManager.getInstance(mContext));
	}

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		Log.e("displayGattServices", "displayGattServices");// --------------------------------------------------------
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "Unknown service";
		String unknownCharaString = "Unknown characteristic";
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (final BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME, LSJXGattAttributes.lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (int i = 0; i < gattCharacteristics.size(); i++) {
				BluetoothGattCharacteristic gattCharacteristic = gattCharacteristics.get(i);
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME, LSJXGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);

				// 透传操作模块
				// 判断蓝牙设备中是否存在数据通道服务与write Characteristic
				// TOUCHUAN_DEVICE = "0000fff0-0000-1000-8000-00805f9b34fb"
				// TOUCHUAN_SEND = "0000fff1-0000-1000-8000-00805f9b34fb"
				if ((LSJXGattAttributes.TOUCHUAN_DEVICE).equals(gattService.getUuid().toString())
						&& (LSJXGattAttributes.TOUCHUAN_SEND).equals(gattCharacteristic.getUuid().toString())) {
					System.out.println("-------初始mGattService----------");
					mGattService = gattService;
					setting();
				}
			}
			if (null != mGattService) {
				if (mGattService.getCharacteristics().size() > 0) {
					setCharacteristicNotification(mGattService.getCharacteristics().get(1), true);
				}
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
	}

	/**
	 * 获取链接后的BLE设备蓝牙地址
	 * 
	 * @return BLE设备蓝牙地址
	 */
	public String getBleAddr() {
		if (mConnectionState != STATE_DISCONNECTED) {
			return mBluetoothDeviceAddress;
		} else {
			return "";
		}
	}

	/**
	 * 同步历史数据
	 */
	public void getHistory() {
		final byte[] historical_data_synchronization_step = new byte[] { 1 };
		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic,
						Protocol.writeByte(Protocol.PRO_HEAD_HisDataSyncStepAndSleep, historical_data_synchronization_step));
			}
		}, Config.SECONDNUM);
	}

	/**
	 * 删除BLE设备所有数据
	 */
	public void delAll() {
		System.out.println("--------delAll---------");
		final byte[] byte_send_alarm_clock = new byte[] { 2, (byte) 0x01 };
		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_HisDataSyncStepAndSleep, byte_send_alarm_clock));
			}
		}, Config.SECONDNUM);
	}

	/**
	 * 新方案通过扫描周边设备连接想要连接的设备
	 * 
	 * @param address
	 *            想要连接的设备地址
	 */
	public void doConnect(String address) {
		if ("".equals(address) || null == address) {
			return;
		}
		if (mConnectionState != STATE_DISCONNECTED) {
			return;
		}
		this.wantConnectDeviceAddress = address;
		if (cmccsi.mhealth.app.sports.common.Config.ISALONE) {
			connectByAddress(address);
		} else {
			if (!mScanning) {
				startScan();
			}
		}
	}

	/**
	 * 设置用户的信息
	 * 
	 * @param user_info
	 */
	public void setUserInfo(UserInfo user_info) {
		// 保险起见，在操作的时候要先进行setting操作
		setting();
		/* *
		 * 设置身高、体重、性别
		 */
		byte sex;
		if (false == user_info.isFemale()) {
			sex = (byte) 0x00;// 男
		} else {
			sex = (byte) 0x01;// 女
		}

		final byte[] user_info_data = new byte[] { 1, (byte) user_info.getHeight(), (byte) user_info.getWeight(), (byte) sex,
				(byte) 0x00, (byte) 0x00 };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, user_info_data));
			}
		}, 200);

		/* *
		 * 设置手环佩戴模式
		 */
		byte bandOnRightOrLeftData;
		if (false == user_info.isBandOnRight()) {
			bandOnRightOrLeftData = (byte) 0x00;// 左
		} else {
			bandOnRightOrLeftData = (byte) 0x01;// 右
		}
		final byte[] bandOnRightOrLeft = new byte[] { 6, bandOnRightOrLeftData };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, bandOnRightOrLeft));
			}
		}, 200);

		/* *
		 * 设置出生日期
		 */
		final byte[] birthday = new byte[] { 8, (byte) user_info.getBirthYear(), (byte) user_info.getBirthMonth(),
				(byte) user_info.getBirthDay() };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_PersonalInfoSysEnvSetting, birthday));
			}
		}, 200);

		/* *
		 * 设置目标数
		 */
		final byte[] goalSteps = new byte[] { 2, (byte) user_info.getGoalSteps(), (byte) 0xffffffff };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_StepOfReaTimeData, goalSteps));
			}
		}, 200);

		/* *
		 * 设置运动闹钟
		 */
		byte set_sport_time = (byte) 0x01 | (byte) 0x02 | (byte) 0x04 | (byte) 0x08 | (byte) 0x10 | (byte) 0x20 | (byte) 0x40;

		final byte[] byte_send_set_sport_time_clock = new byte[] { 2, (byte) 0x01, (byte) set_sport_time,
				(byte) user_info.getGoalDurationHour(), (byte) user_info.getGoalDurationMinute() };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic,
						Protocol.writeByte(Protocol.PRO_HEAD_TimeTheAlarmClock, byte_send_set_sport_time_clock));
			}
		}, 200);

		/* *
		 * 设置睡眠闹钟
		 */
		byte set_time = (byte) 0x01 | (byte) 0x02 | (byte) 0x04 | (byte) 0x08 | (byte) 0x10 | (byte) 0x20 | (byte) 0x40;
		final byte[] byte_send_set_sleep_time_clock = new byte[] { 2, (byte) 0x01, (byte) set_time,
				(byte) user_info.getAlarmHour(), (byte) user_info.getAlarmMinute() };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic,
						Protocol.writeByte(Protocol.PRO_HEAD_TimeTheAlarmClock, byte_send_set_sleep_time_clock));
			}
		}, 200);
	}

	/**
	 * 获取手环的固件版本值、手环类型
	 */
	public void getDeviceInfo() {
		// 获取手环的固件版本值、手环型号
		System.out.println("------getDeviceInfo--------");
		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);
		final byte[] byte_send_get_device_model = new byte[] { 2 };
		// 获取手环的型号
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_DevInfo, byte_send_get_device_model));
			}
		}, 1200);
	}

	// 获取手环的固件版本值
	public void getDeviceVersion() {
		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);

		final byte[] byte_send_get_device_model = new byte[] { 1 };
		// 获取手环的固件版本值
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_DevInfo, byte_send_get_device_model));
			}
		}, 200);

	}

	/************** 设置闹铃的 ************/
	private final static byte SPORTCLOCKNUM = 0x01;
	private final static byte SLEEPCLOCKNUM = 0x02;

	/**
	 * 设置闹钟
	 */
	private void setClock(byte clockNum, int[] day, int hour, int min) {

		byte set_sport_day = 0x00;
		final byte[] byte_send_set_sport_time_clock;

		for (int index = 0; index < day.length; index++) {
			switch (day[index]) {
			case 1:
				set_sport_day = (byte) (set_sport_day | (byte) 0x02);
				break;
			case 2:
				set_sport_day = (byte) (set_sport_day | (byte) 0x04);
				break;
			case 3:
				set_sport_day = (byte) (set_sport_day | (byte) 0x08);
				break;
			case 4:
				set_sport_day = (byte) (set_sport_day | (byte) 0x10);
				break;
			case 5:
				set_sport_day = (byte) (set_sport_day | (byte) 0x20);
				break;
			case 6:
				set_sport_day = (byte) (set_sport_day | (byte) 0x40);
				break;
			case 7:
				set_sport_day = (byte) (set_sport_day | (byte) 0x01);
				break;
			}
		}
		byte_send_set_sport_time_clock = new byte[] { 2, clockNum, (byte) set_sport_day, (byte) hour, (byte) min };
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic,
						Protocol.writeByte(Protocol.PRO_HEAD_TimeTheAlarmClock, byte_send_set_sport_time_clock));
			}
		}, 600);
	}

	/**
	 * 关闭闹钟
	 */
	private void setClockClose(byte clockNum) {
		final byte[] byte_send_set_sport_time_clock = new byte[] { 2, clockNum, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setValue(gattCharacteristic,
						Protocol.writeByte(Protocol.PRO_HEAD_TimeTheAlarmClock, byte_send_set_sport_time_clock));
			}
		}, 200);
	}

	/**
	 * 关闭运动闹钟
	 */
	public void setSportClockClose() {
		setClockClose(SPORTCLOCKNUM);
	}

	/**
	 * 关闭睡眠闹钟
	 */
	public void setSleepClockClose() {
		setClockClose(SLEEPCLOCKNUM);
	}

	/**
	 * 设置运动闹钟 使用方法： int[] day = {3, 5, 7}; // 星期3 5 日
	 * mLSJXBleApi.setSportClock(day, 15, 15); // 15:15
	 */
	public void setSportClock(final int[] day, final int hour, final int min) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setClock(SPORTCLOCKNUM, day, hour, min);
			}
		}, 400);
	}

	/**
	 * 设置睡眠闹钟 使用方法： int[] day = {3, 5, 7}; // 星期3 5 日
	 * mLSJXBleApi.setSleepClock(day, 15, 15); // 15:15
	 */
	public void setSleepClock(final int[] day, final int hour, final int min) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setClock(SLEEPCLOCKNUM, day, hour, min);
			}
		}, 200);
	}

	// 固件更新部分
	/**
	 * 固件升级的进度
	 */
	public static int progress = 0; // 进度
	/**
	 * 设置为false则推出更新
	 */
	public static boolean is_update = false; // 设置为false则退出更新
	// 固件升级需要
	private otaManager updateManager = new otaManager();

	// 开始ota更新
	public void startOtaUpdate(String filename) {
		updateInstance ins = new updateInstance();
		ins.bleInterfaceInit(mBluetoothGatt);
		if (updateManager.otaStart(filename, ins) == otaResult.OTA_RESULT_SUCCESS) {
			Thread updateThread = new Thread(update);
			updateThread.start();
		}
	}

	// 固件更新的线程
	private Runnable update = new Runnable() {
		public void run() {
			int[] extra = new int[8];
			otaResult ret = otaResult.OTA_RESULT_SUCCESS;
			while (is_update) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!Thread.currentThread().isInterrupted()) {
					// otaResult ret = updateManager.otaGetProcess(extra);
					if (ret == otaResult.OTA_RESULT_SUCCESS) {
						ret = updateManager.otaGetProcess(extra);
						progress = extra[0]; // 百分比
						Log.e("progress:", progress + "");
						if (progress >= 100) {
							updateManager.otaStop();
							is_update = false;
						}
					} else {
						updateManager.otaStop();
						is_update = false;
						Log.e("progress:", otaError2String(ret));
					}
				}
			}
		}
	};

	// ota错误转换为字符串
	/**
	 * 将错误信息转换为文字
	 * 
	 * @param ret
	 *            错误码
	 * @return
	 */
	private static String otaError2String(otaResult ret) {
		switch (ret) {
		case OTA_RESULT_SUCCESS:
			return "SUCCESS";
		case OTA_RESULT_PKT_CHECKSUM_ERROR:
			return "Transmission is failed,firmware checksum error";
		case OTA_RESULT_PKT_LEN_ERROR:
			return "Transmission is failed,packet length error";
		case OTA_RESULT_DEVICE_NOT_SUPPORT_OTA:
			return "The OTA function is disabled by the server";
		case OTA_RESULT_FW_SIZE_ERROR:
			return "Transmission is failed,firmware file size error";
		case OTA_RESULT_FW_VERIFY_ERROR:
			return "Transmission is failed,verify failed";
		case OTA_RESULT_OPEN_FIRMWAREFILE_ERROR:
			return "Open firmware file failed";
		case OTA_RESULT_META_RESPONSE_TIMEOUT:
			return "Wait meta packet response timeout";
		case OTA_RESULT_DATA_RESPONSE_TIMEOUT:
			return "Wait data packet response timeout";
		case OTA_RESULT_SEND_META_ERROR:
			return "Send meta data error";
		case OTA_RESULT_RECEIVED_INVALID_PACKET:
			return "Transmission is failed,received invalid packet";
		case OTA_RESULT_INVALID_ARGUMENT:
		default:
			return "Unknown error";
		}
	}

	private class updateInstance extends BluetoothLeInterface {
		@Override
		public boolean bleInterfaceInit(BluetoothGatt bluetoothGatt) {
			return super.bleInterfaceInit(bluetoothGatt);
		}
	}

	/**
	 * 日期格式字符串转换成时间戳，使用例子：
	 * 
	 * <pre>
	 * String startDate = &quot;2015-05-23 15:25:00&quot;;
	 * String endDate = &quot;2015-05-23 15:45:00&quot;;
	 * mBle.getSyncData(startDate, endDate);
	 * </pre>
	 * 
	 * @param startDate
	 *            开始时间
	 * @param startDate
	 *            结束时间
	 * @param format
	 *            如：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public void getSyncData(String startDate, String endDate) {
		System.out.println("is same day = " + startDate.substring(8, 10).equals(endDate.substring(8, 10)) + " ; ");
		if (!startDate.substring(8, 10).equals(endDate.substring(8, 10))) {
			startDate = startDate.substring(0, 10) + " 00:00:00";
		}
		String startTimeStamp = date2TimeStamp(startDate, "yyyy-MM-dd HH:mm:ss");
		String endTimeStamp = date2TimeStamp(endDate, "yyyy-MM-dd HH:mm:ss");
		int startTimeStampInt = Integer.parseInt(startTimeStamp);
		int endTimeStampInt = Integer.parseInt(endTimeStamp);
		System.out.println("startTimeStampInt = " + startTimeStampInt + " ; ");
		System.out.println("endTimeStampInt = " + endTimeStampInt + " ; ");
		// byte[0]表示id，id为0x01表示app向ble设备同步系统时间
		final byte[] byte_send = new byte[] { 1, (byte) startTimeStampInt, (byte) (startTimeStampInt >> 8),
				(byte) (startTimeStampInt >> 16), (byte) (startTimeStampInt >> 24), (byte) (endTimeStampInt),
				(byte) (endTimeStampInt >> 8), (byte) (endTimeStampInt >> 16), (byte) (endTimeStampInt >> 24) };

		BluetoothGattService gattService = getGattService();
		gattCharacteristic = gattService.getCharacteristics().get(0);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				System.out.println("------is here-------");
				setValue(gattCharacteristic, Protocol.writeByte(Protocol.PRO_HEAD_HisDataSyncStepAndSleep, byte_send));
			}
		}, 200);
	}

	/**
	 * 日期格式字符串转换成时间戳
	 * 
	 * @param date
	 *            字符串日期
	 * @param format
	 *            如：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String date2TimeStamp(String date_str, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return String.valueOf(sdf.parse(date_str).getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
