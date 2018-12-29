package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 获取个人排名的总数
 * 
 * @version
 * 
 */
public class UpdatePasswordInfo extends BaseNetItem {
	public static String TAG = "UpdatePasswordInfo";

	public void setValue(UpdatePasswordInfo data) {
		this.status = data.status;
		this.reason = data.reason;
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		UpdatePasswordInfo info = (UpdatePasswordInfo)bni;
		if(info.reason == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}


	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((UpdatePasswordInfo) bni);
	}

}
