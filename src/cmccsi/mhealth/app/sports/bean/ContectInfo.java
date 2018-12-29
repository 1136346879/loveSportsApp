package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 用于承接返回的contact信息
 *
 */
public class ContectInfo extends BaseNetItem {
	public static String TAG = "ContectInfo";
	public String vision;
	public ArrayList<ContectData> datavalue;
	
	public ContectInfo() {
		datavalue = new ArrayList<ContectData>();
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		ContectInfo info = (ContectInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		ContectInfo data = (ContectInfo) bni;
		datavalue = data.datavalue;
		vision = data.vision;
	}
}
