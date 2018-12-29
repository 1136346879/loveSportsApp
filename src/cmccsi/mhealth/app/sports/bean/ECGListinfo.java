package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class ECGListinfo extends BaseNetItem {
	
	public List<ECGSummary> datavalue;
	
	public  ECGListinfo() {
		datavalue=new ArrayList<ECGSummary>();
	}

	@Override
	public void setValue(BaseNetItem bni) {
		 if (null == bni) {
	            return;
	        }
		 ECGListinfo data = (ECGListinfo) bni;
	     status = data.status;
	     reason = data.reason;
	     datavalue.addAll(data.datavalue);

	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ECGListinfo info = (ECGListinfo) bni;
        if (info.datavalue == null) {
            return false;
        }
		return false;
	}

}
