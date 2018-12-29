package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

public class DeviceTypeListInfo extends BaseNetItem {
	
	public ArrayList<DeviceTypeInfo> datavalue;
	public String message;
	
	public DeviceTypeListInfo() {
		datavalue= new ArrayList<DeviceTypeInfo>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if (datavalue.size() > 0) {
			return true;
		}
		return false;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		DeviceTypeListInfo data = (DeviceTypeListInfo) bni;
		status = data.status;
		message = data.message;
		datavalue = data.datavalue;
	}

}
