package cmccsi.mhealth.app.sports.bean;


public class AcceptFriendRequestInfo extends BaseNetItem {
	public static String TAG = "ClubListInfo";
	public String result;

	public AcceptFriendRequestInfo() {
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return true;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		AcceptFriendRequestInfo data = (AcceptFriendRequestInfo) bni;
		status = data.status;
		result = data.result;
	}
}
