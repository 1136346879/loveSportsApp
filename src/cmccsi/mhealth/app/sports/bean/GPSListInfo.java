package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GPSListInfo extends BaseNetItem implements Parcelable {
	public int getIsUpload() {
		return isUpload;
	}
	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getCal() {
		return cal;
	}
	public void setCal(float cal) {
		this.cal = cal;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getStepNum() {
		return stepNum;
	}
	public void setStepNum(String stepNum) {
		this.stepNum = stepNum;
	}
	public String getDurationperkm() {
		return durationperkm;
	}
	public void setDurationperkm(String durationperkm) {
		this.durationperkm = durationperkm;
	}
	public String getSpeedmax() {
		return speedmax;
	}
	public void setSpeedmax(String speedmax) {
		this.speedmax = speedmax;
	}
	public String getSpeedmin() {
		return speedmin;
	}
	public void setSpeedmin(String speedmin) {
		this.speedmin = speedmin;
	}
	public String getClimbsum() {
		return climbsum;
	}
	public void setClimbsum(String climbsum) {
		this.climbsum = climbsum;
	}
	public int getSporttype() {
		return sporttype;
	}
	public void setSporttype(int sporttype) {
		this.sporttype = sporttype;
	}

	private String starttime= "0";
	private float speed = 0.0f;
	private float distance = 0.0f;
	private float cal = 0.0f;
	private String duration = "0";
	private String stepNum= "0";
	private String durationperkm= "0";
	private String speedmax= "0";
	private String speedmin= "0";
	private String climbsum= "0";
	private int sporttype = -1;
	private int isUpload;//0 已上传，1未上传
	
	
	
	public GPSListInfo(){
//		geoPoint = new ArrayList<GeoPoint>();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	
	public GPSListInfo(Parcel in) {
		starttime = in.readString();
		duration = in.readString();
		stepNum = in.readString();
		durationperkm = in.readString();
		speedmax = in.readString();
		speedmin = in.readString();
		climbsum = in.readString();
		speed = in.readFloat();
		distance = in.readFloat();
		cal = in.readFloat();
		sporttype = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(starttime);
		dest.writeString(duration);
		dest.writeString(stepNum);
		dest.writeString(durationperkm);
		dest.writeString(speedmax);
		dest.writeString(speedmin);
		dest.writeString(climbsum);
		dest.writeFloat(speed);
		dest.writeFloat(distance);
		dest.writeFloat(cal);
		dest.writeInt(sporttype);
	}
	
	public static final Parcelable.Creator<RunType> CREATOR = new Parcelable.Creator<RunType>() {
		public RunType createFromParcel(Parcel in) {
			return new RunType(in);
		}

		public RunType[] newArray(int size) {
			return new RunType[size];
		}
	};



	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		GPSListInfo data = (GPSListInfo) bni;
		status = data.status;
		reason = data.reason;
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		if(bni ==null)
			return false;
		return true;
	}
	
}
