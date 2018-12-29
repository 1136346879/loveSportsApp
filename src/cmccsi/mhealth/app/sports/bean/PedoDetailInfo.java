package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.common.Logger;

public class PedoDetailInfo extends BaseNetItem {
	public static String TAG = "PedoDetailInfo";

	// public String status = "SUCCESS";
	public String datatype = "1";
	public List<DataDetailPedo> datavalue;
	public String date = "20121002";
	public String phoneNum = "13881657386";

	public PedoDetailInfo() {
		datavalue = new ArrayList<DataDetailPedo>();
	}

	public void setValue(PedoDetailInfo data) {
		this.status = data.status;
		this.datatype = data.datatype;
		this.datavalue = data.datavalue;
		this.date = data.date;
		this.phoneNum = data.phoneNum;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null == bni)
			return;
		setValue((PedoDetailInfo) bni);
	}

	public void initialDate() {
		// TODO 运动强度是否需要初始化
		sortDataPedometor();
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		PedoDetailInfo info = (PedoDetailInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	private void sortDataPedometor() {
		int size = datavalue.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = datavalue.get(j)
						.compareStartTime(datavalue.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				DataDetailPedo data = datavalue.get(i);
				datavalue.set(i, datavalue.get(k));
				datavalue.set(k, data);
			}
		}
	}
}
