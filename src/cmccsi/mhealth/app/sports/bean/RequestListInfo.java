package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestListInfo extends BaseNetItem implements Parcelable{
	
	public static String TAG = "FriendRequestListInfo";
	public List<RequestData> dataValue;

	public RequestListInfo() {
		dataValue = new ArrayList<RequestData>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return true;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		RequestListInfo data = (RequestListInfo) bni;
		status = data.status;
		dataValue = data.dataValue;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
