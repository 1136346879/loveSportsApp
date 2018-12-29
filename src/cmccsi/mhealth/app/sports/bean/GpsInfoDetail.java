package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GpsInfoDetail extends BaseNetItem implements Parcelable {
	
	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getCal() {
		return cal;
	}

	public void setCal(float cal) {
		this.cal = cal;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getDetailtime() {
		return detailtime;
	}

	public void setDetailtime(String detailtime) {
		this.detailtime = detailtime;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getIsStopPoint() {
		return isPause;
	}

	public void setIsStopPoint(int isStopPoint) {
		this.isPause = isStopPoint;
	}
	
	private double latitude;
	private double longtitude;
	private String starttime;
	private String detailtime;
	private float speed;
	private double altitude;
	private double distance;
	private int isPause = 0;//是否是暂停的点 1暂停的点  0默认正常的点
	private float cal;
	
	@Override
	public String toString() {
//		String s = "gpstrack #2013-04-01_10:28:00#60#45#30#45@ ";
		String s = "gpstrack #"+detailtime+"#"+latitude+"#"+longtitude+"#"+0+"#"+speed+"#"+isPause;
		return s;
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

	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		GpsInfoDetail data = (GpsInfoDetail) bni;
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
