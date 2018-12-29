package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

public class AreaListInfo extends BaseNetItem {
	
	public ArrayList<AreaInfo> datavalue;
	public String message;
	
	public AreaListInfo() {
		datavalue= new ArrayList<AreaInfo>();
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
		AreaListInfo data = (AreaListInfo) bni;
		status = data.status;
		message = data.message;
		datavalue = data.datavalue;
	}

}
