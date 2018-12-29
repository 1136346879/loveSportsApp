package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 类名称：获取个人排名的名次 类描述： 获取个人排名的总数
 * 
 * @version
 * 
 */
public class OrgnizememSeq extends BaseNetItem {
	public static String TAG = "OrgnizememSeq";
	//public String status = "SUCCESS";
	public String pedorgnizememberseq;
	public OrgnizeMemberInfo memberinfo;

	public void setValue(OrgnizememSeq data) {
		this.status = data.status;
		this.pedorgnizememberseq = data.pedorgnizememberseq;
		this.memberinfo = data.memberinfo;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((OrgnizememSeq) bni);
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		OrgnizememSeq info = (OrgnizememSeq)bni;
		if(info.memberinfo == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
}
