package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 用于承接返回的登陆信息
 *
 */
public class LoginInfo extends BaseNetItem {
	public static String TAG = "LoginInfo";
	public String dataType;
	public ArrayList<ServersInfo> datavalue;
	public String date;
	public String phoneNum;
	
	public LoginInfo() {
		datavalue = new ArrayList<ServersInfo>();
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		LoginInfo info = (LoginInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		LoginInfo data = (LoginInfo) bni;
		status = data.status;
		reason = data.reason;
		dataType = data.dataType;
		datavalue = data.datavalue;
		date = data.date;
		phoneNum = data.phoneNum;
	}
}
