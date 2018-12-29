package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class ListGPSData extends BaseNetItem{
	
	public int currentPage;
	public int totalPage;
	public List<GPSListInfo> datavalue;
	
	public ListGPSData(){
		datavalue = new ArrayList<GPSListInfo>();
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		ListGPSData data = (ListGPSData) bni;
		this.datavalue = data.datavalue;
		this.currentPage=data.currentPage;
		this.totalPage=data.totalPage;
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
