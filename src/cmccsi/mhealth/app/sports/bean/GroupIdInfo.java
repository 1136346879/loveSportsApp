package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.common.Logger;

public class GroupIdInfo extends BaseNetItem {
	public static String TAG = "GroupIdInfo";

	//public String status = "SUCCESS";
	public String groupid = "null"; // 班组ID--唯一标识
	public String groupname = "null"; // 班组ID--唯一标识

	public void setValue(GroupIdInfo data) {
		this.status = data.status;
		this.groupid = data.groupid;
		this.groupname = data.groupname;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((GroupIdInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		GroupIdInfo info = (GroupIdInfo)bni;
		if(info.groupid == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

}
