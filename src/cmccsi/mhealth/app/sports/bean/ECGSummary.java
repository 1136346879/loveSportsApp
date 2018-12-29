/********************************************
 * File Name：sss.java
 * Version	：1.00
 * Author	：Gaofei - 高飞
 * Date		：2012-10-31
 * LastModify：2012-10-31
 * Functon	：功能描述
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类 sss company: 中国移动研究院普适计算与通信研究中心
 * 
 * @author by:gaofei-高飞
 * @date 2012-10-31 下午9:59:38 version 1.0 describe:
 */

public class ECGSummary implements Parcelable {
	public String power = "60";
	public String hr = "50";
	public String rawdata = "30";
	public String hrv = "0";     // energy_consumption
	public String rr_interval = "0";   // step_num
	public String mood = "0";
	public String trainingzone = "0";
	public String date = "0";

	public ECGSummary(Parcel in) {
		power = in.readString();
		hr = in.readString();
		rawdata = in.readString();
		hrv = in.readString();
		rr_interval = in.readString();
		mood = in.readString();
		trainingzone = in.readString();
		date = in.readString();
	}
	
	public ECGSummary(){
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(power);
		out.writeString(hrv);
		out.writeString(rr_interval);
		out.writeString(mood);
		out.writeString(trainingzone);
		out.writeString(date);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ECGSummary createFromParcel(Parcel in) {
			return new ECGSummary(in);
		}

		@Override
		public ECGSummary[] newArray(int size) {
			return new ECGSummary[size];
		}
	};
	
	public int compareDate(ECGSummary data) {
		if (data == null)
			return -1;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		Date self_createtime = null;
		Date compare_createtime = null;
		try {
			self_createtime = df.parse(this.date);
			compare_createtime = df.parse(data.date);
			long self_one = self_createtime.getTime();
			long compare_one = compare_createtime.getTime();

			if (self_one > compare_one)
				return 1;
			else if (self_one < compare_one)
				return 0;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	@Override
	public String toString() {
		return "PedometorSummary [power=" + power + ", hr=" + hr
				+ ", rawdata=" + rawdata + ", hrv=" + hrv + ", rr_interval=" + rr_interval
				+ ", mood=" + mood + ", trainingzone=" + trainingzone
				+ ", date=" + date
				+ "]";
	}
	
	
}