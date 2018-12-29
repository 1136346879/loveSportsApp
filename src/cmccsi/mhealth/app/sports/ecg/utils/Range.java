package cmccsi.mhealth.app.sports.ecg.utils;

/**
 * 表示一段时间
 * @author Lianxw
 *
 */
public class Range {
	// 开始时间
	private String startTime;
	// 结束时间
	private String endTime;

	public void setValue(String start, String end) {
		this.startTime = start;
		this.endTime = end;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getStartDate() {
		if(this.startTime!=null) {
			if(this.startTime.length()>10){
				return this.startTime.substring(0,10);
			}
			return this.startTime;
		}
		return null;
	}
	
	public String getEndDate() {
		if(this.endTime!=null) {
			if(this.endTime.length()>10){
				return this.endTime.substring(0,10);
			}
			return this.endTime;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Range [startTime=" + startTime + ", endTime=" + endTime + "]";
	}
}
