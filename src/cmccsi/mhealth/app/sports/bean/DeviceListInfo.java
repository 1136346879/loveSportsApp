package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class DeviceListInfo extends BaseNetItem{
    private static String TAG = DeviceListInfo.class.getSimpleName();
    public List<DeviceInfo> datavalue;
    private static  DeviceListInfo devicelist;
    
    public static DeviceListInfo getInstance(){
    	if(devicelist==null)
    	{
    		devicelist=new DeviceListInfo();
    	}
    	return devicelist;
    }
    private DeviceListInfo() {
        datavalue = new ArrayList<DeviceInfo>();
    }

    @Override
    public void setValue(BaseNetItem bni) {
        if (null == bni) {
            return;
        }
        DeviceListInfo data = (DeviceListInfo) bni;
        status = data.status;
        reason = data.reason;
        datavalue = data.datavalue;
    }

    @Override
    public boolean isValueData(BaseNetItem bni) {
        DeviceListInfo info = (DeviceListInfo) bni;
        if (info.datavalue == null) {
            Log.d(TAG,"data is null");
            return false;
        }
        return true;
    }

}
