package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 用于承接返回的contact信息
 *
 */
public class ContectGroupInfo extends BaseNetItem {
	public static String TAG = "ContectGroupInfo";
	public String vision;
	public ArrayList<ContectGroupData> datavalue;
	
	public ContectGroupInfo() {
		datavalue = new ArrayList<ContectGroupData>();
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		ContectGroupInfo info = (ContectGroupInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		ContectGroupInfo data = (ContectGroupInfo) bni;
		datavalue = data.datavalue;
		vision = data.vision;
	}
}
