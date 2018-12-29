package cmccsi.mhealth.app.sports.bean;

import java.util.List;


public class FindFriendInfo extends BaseNetItem {
	public List<FriendSearchItem> dataValue;
	public void setValue(FindFriendInfo date) {
		this.status = date.status;
		this.dataValue = date.dataValue;
		this.reason = date.reason;
	}
	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((FindFriendInfo) bni);
		}
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		return true;
	}

}
