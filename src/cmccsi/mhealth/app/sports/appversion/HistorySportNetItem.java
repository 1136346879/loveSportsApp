package cmccsi.mhealth.app.sports.appversion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HistorySportNetItem implements Serializable {
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;

	public HistorySportNetItem() {
	}

	public String status = "";
	public String uid = "";
	public String timetype = "";
	public String startTime = "";
	public String endTime = "";
	public String totalStep = "";
	public String totalCal = "";
	public String totalDistance = "";
	public ArrayList<NetItem> datavalue;
}

class NetItem {
	public String stepNum = "";
	public String date = "";

	public String getStepNum() {
		return stepNum;
	}

	public void setStepNum(String stepNum) {
		this.stepNum = stepNum;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static ArrayList<NetItem> getclazz2(String json) {
		if (json == null) {
			return null;
		}
		try {
			Gson gson = new Gson();
			ArrayList<NetItem> lists = new ArrayList<NetItem>();
			lists = gson.fromJson(json, new TypeToken<List<NetItem>>() {
			}.getType());
			return lists;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
