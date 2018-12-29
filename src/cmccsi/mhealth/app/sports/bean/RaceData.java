package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛基本信息条目
 * 
 * @author zy
 * 
 */
public class RaceData implements Parcelable {
	public String racename;
	public String founderphone;
	public String foundername;
	public String starttime;
	public String endtime;
	public String type;
	public String raceid;
	public String racedetail;
	public String membernum;
	public String titlepicurl;
	public String started;
	
	public String getRacedetail() {
		return racedetail;
	}

	public void setRacedetail(String racedetail) {
		this.racedetail = racedetail;
	}

	public String getStarted() {
		return started;
	}

	public void setStarted(String started) {
		this.started = started;
	}

	public String getRaceid() {
		return raceid;
	}

	public void setRaceid(String raceid) {
		this.raceid = raceid;
	}

	public String getRacename() {
		return racename;
	}

	public void setRacename(String racename) {
		this.racename = racename;
	}

	public String getFounderphone() {
		return founderphone;
	}

	public void setFounderphone(String founderphone) {
		this.founderphone = founderphone;
	}

	public String getFoundername() {
		return foundername;
	}

	public void setFoundername(String foundername) {
		this.foundername = foundername;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMembernum() {
		return membernum;
	}

	public void setMembernum(String membernum) {
		this.membernum = membernum;
	}

	public String getTitlepicurl() {
		return titlepicurl;
	}

	public void setTitlepicurl(String titlepicurl) {
		this.titlepicurl = titlepicurl;
	}

	public RaceData() {
	}

	public RaceData(Parcel in) {
		racename = in.readString();
		founderphone = in.readString();
		foundername = in.readString();
		starttime = in.readString();
		endtime = in.readString();
		started = in.readString();
		type = in.readString();
		raceid = in.readString();
		racedetail = in.readString();
		membernum = in.readString();
		titlepicurl = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(racename);
		out.writeString(founderphone);
		out.writeString(foundername);
		out.writeString(starttime);
		out.writeString(endtime);
		out.writeString(started);
		out.writeString(type);
		out.writeString(raceid);
		out.writeString(racedetail);
		out.writeString(membernum);
		out.writeString(titlepicurl);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public RaceData createFromParcel(Parcel in) {
			return new RaceData(in);
		}

		@Override
		public RaceData[] newArray(int size) {
			return new RaceData[size];
		}
	};

}
