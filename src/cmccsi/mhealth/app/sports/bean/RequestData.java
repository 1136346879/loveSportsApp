package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestData implements Parcelable {
	public String requesttype;
	public String phonenum;
	public String name;
	public String msg;
	public String raceid;
	public String racetype;
	public String avatar;
	public boolean isoldmsgs = false;

	public boolean isIsoldmsgs() {
		return isoldmsgs;
	}

	public String getAvatar() {
		return avatar;
	}



	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}



	public void setIsoldmsgs(boolean isoldmsgs) {
		this.isoldmsgs = isoldmsgs;
	}

	public String getRequesttype() {
		return requesttype;
	}

	public void setRequesttype(String requesttype) {
		this.requesttype = requesttype;
	}

	public String getRaceid() {
		return raceid;
	}

	public void setRaceid(String raceid) {
		this.raceid = raceid;
	}

	public String getRacetype() {
		return racetype;
	}

	public void setRacetype(String racetype) {
		this.racetype = racetype;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
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
