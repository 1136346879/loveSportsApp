package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class DetailGPSData extends BaseNetItem{
	
	public String dataType;
	public String phoneNum;
	public List<GpsInfoDetail> datavalue;
	
	public DetailGPSData(){
		datavalue = new ArrayList<GpsInfoDetail>();
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		DetailGPSData data = (DetailGPSData) bni;
		this.datavalue = data.datavalue;
		super.status = data.status;
		super.reason = data.reason;
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if(bni ==null)
			return false;
		return true;
	}

}
