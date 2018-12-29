package cmccsi.mhealth.app.sports.device;

import java.util.ArrayList;

import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import android.content.Context;

/**
 * 计步器设备
 * @type PedometorDevice
 * TODO
 * @author shaoting.chen
 * @time 2015年5月4日下午3:30:03
 */
public class PedometorDevice extends AbstractMobileDevice{

	private String TAG = "PedometorDevice";
	
	private Context mContext;

	private BaseDeviceInterface.BaseCallBack mBaseCallBack;
	
	public PedometorDevice(Context context){
		this.mContext = context;
	}
	@Override
	public void connect(String address) {
		// TODO Auto-generated method stub
		mBaseCallBack.connected(DeviceConstants.CONNECTED_SUCCESS, "连接成功");
	}

	@Override
	public void disConnect() {
		// TODO Auto-generated method stub
		mBaseCallBack.disConnected();
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

}
