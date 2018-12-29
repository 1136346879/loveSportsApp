package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.common.Logger;

public class TempCodeInfo extends BaseNetItem {

	private static final String TAG = "TempCodeInfo";
	public String tempcode = "";
	public String limittime = "";
	public String result = "";
	public String selectserver;

	private void setValue(TempCodeInfo data) {
		this.status = data.status;
		tempcode = data.tempcode;
		limittime = data.limittime;
		result = data.result;
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		TempCodeInfo info = (TempCodeInfo)bni;
		if(info.limittime == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (bni != null)
			this.setValue((TempCodeInfo) bni);
	}

}
