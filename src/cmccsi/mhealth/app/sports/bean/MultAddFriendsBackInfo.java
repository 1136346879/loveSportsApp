package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class MultAddFriendsBackInfo extends BaseNetItem implements Parcelable{
	public static String TAG = "AddFriendInfo";
	public String message = "";
	public String friendNumbers = "";
	public String sendNumbers = "";
	
	public MultAddFriendsBackInfo() {
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return reason.length() > 0;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		MultAddFriendsBackInfo data = (MultAddFriendsBackInfo) bni;
		status = data.status;
		reason = data.reason;
		message = data.message;
		friendNumbers = data.friendNumbers;
		sendNumbers = data.sendNumbers;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFriendNumbers() {
		return friendNumbers;
	}

	public void setFriendNumbers(String friendNumbers) {
		this.friendNumbers = friendNumbers;
	}

	public String getSendNumbers() {
		return sendNumbers;
	}

	public void setSendNumbers(String sendNumbers) {
		this.sendNumbers = sendNumbers;
	}
	
}
