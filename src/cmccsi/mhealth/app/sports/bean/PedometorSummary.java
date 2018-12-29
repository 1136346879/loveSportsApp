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

public class PedometorSummary implements Parcelable {
	public String power = "60";
	public String weight = "50";
	public String step = "30";
	public String cal = "0.0"; // energy_consumption
	public String stepNum = "0"; // step_num
	public String distance = "0";
	public String strength1 = "0";
	public String strength2 = "0";   
	public String strength3 = "0";
	public String strength4 = "0";
	public String transType = "1"; // 1:自动 2:手动
	public String yxbssum = "0";
	public String yxbs = "0";
	public String date = "0";
	public String deviceId="";

	public PedometorSummary(Parcel in) {
		power = in.readString();
		weight = in.readString();
		step = in.readString();
		cal = in.readString();
		stepNum = in.readString();
		distance = in.readString();
		strength1 = in.readString();
		strength2 = in.readString();
		strength3 = in.readString();
		strength4 = in.readString();
		transType = in.readString();
		yxbssum = in.readString();
		yxbs = in.readString();
		date = in.readString();
		deviceId=in.readString();
	}
	public PedometorSummary(){
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(power);
		out.writeString(weight);
		out.writeString(step);
		out.writeString(cal);
		out.writeString(stepNum);
		out.writeString(distance);
		out.writeString(strength1);
		out.writeString(strength2);
		out.writeString(strength3);
		out.writeString(strength4);
		out.writeString(transType);
		out.writeString(yxbssum);
		out.writeString(yxbs);
		out.writeString(date);
		out.writeString(deviceId);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public PedometorSummary createFromParcel(Parcel in) {
			return new PedometorSummary(in);
		}

		@Override
		public PedometorSummary[] newArray(int size) {
			return new PedometorSummary[size];
		}
	};
	
	public int compareDate(PedometorSummary data) {
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
}