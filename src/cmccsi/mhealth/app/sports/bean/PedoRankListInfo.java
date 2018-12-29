package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

public class PedoRankListInfo extends BaseNetItem {
	
	public ArrayList<PedoRankBriefInfo> datavalue;
	public String message;
	
	public PedoRankListInfo() {
		datavalue= new ArrayList<PedoRankBriefInfo>();
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
		PedoRankListInfo data = (PedoRankListInfo) bni;
		status = data.status;
		message = data.message;
		datavalue = data.datavalue;
	}

}
