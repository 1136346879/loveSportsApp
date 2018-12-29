package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;

/**
 * 好友联系人列表
 * @type ContactListInfo
 * TODO
 * @author shaoting.chen
 * @time 2015年6月2日下午3:03:53
 */
public class ContactListInfo extends BaseNetItem {
	
	public ArrayList<ContactInfo> datavalue;
	public String message;
	
	public ContactListInfo() {
		datavalue= new ArrayList<ContactInfo>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if (datavalue.size() > 0) {
			return true;
		}
		return false;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		ContactListInfo data = (ContactListInfo) bni;
		status = data.status;
		message = data.message;
		datavalue = data.datavalue;
	}

}
