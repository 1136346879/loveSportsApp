package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.common.Logger;

public class GroupRankUpdateVersion extends BaseNetItem {

	private static final String TAG = "GroupRankUpdateVersion";
	public String result = "";

	private void setValue(GroupRankUpdateVersion data) {
		this.status = data.status;
		result = data.result;
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		GroupRankUpdateVersion info = (GroupRankUpdateVersion)bni;
		if(info.result == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (bni != null)
			this.setValue((GroupRankUpdateVersion) bni);
	}

}
