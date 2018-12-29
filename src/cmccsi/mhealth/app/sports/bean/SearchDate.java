package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchDate extends RankingDate implements Parcelable {
	public String groupname;
	public String groupid;
	public String sex;
	
	public SearchDate(){
		super();
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		return super.isValueData(bni);
	}
	
	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		SearchDate date = (SearchDate)bni;
		this.avatar = date.avatar;
		this.sex = date.sex;
		this.membername = date.membername;
		this.groupname = date.groupname;
		this.status = date.status;
		this.member7seq = date.member7seq;
		this.member1seq = date.member1seq;
		this.member1info = date.member1info;
		this.group7seq = date.group7seq;
		this.group7info = date.group7info;
		this.group1seq = date.group1seq;
		this.group1info = date.group1info;
		this.groupid = date.groupid;
		this.groupmember7seq = date.groupmember7seq;
		this.groupmember1seq = date.groupmember1seq;
		this.member7info = date.member7info;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
