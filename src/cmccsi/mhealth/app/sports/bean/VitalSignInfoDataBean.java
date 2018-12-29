package cmccsi.mhealth.app.sports.bean;
/**
 * 用于生理数据
 * @author zy
 *
 */
public class VitalSignInfoDataBean {
	private String value;
	private String MeasureDate;
	private String MeasureDateByLongStyle;
	private String datetype;
	private String EditTime;
	private String EditTimeByLongStyle;
	public String getMeasureDateByLongStyle() {
		return MeasureDateByLongStyle;
	}
	public void setMeasureDateByLongStyle(String measureDateByLongStyle) {
		MeasureDateByLongStyle = measureDateByLongStyle;
	}
	public String getEditTimeByLongStyle() {
		return EditTimeByLongStyle;
	}
	public void setEditTimeByLongStyle(String editTimeByLongStyle) {
		EditTimeByLongStyle = editTimeByLongStyle;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMeasureDate() {
		return MeasureDate;
	}
	public void setMeasureDate(String measureDate) {
		MeasureDate = measureDate;
	}
	public String getDatetype() {
		return datetype;
	}
	public void setDatetype(String datetype) {
		this.datetype = datetype;
	}
	public String getEditTime() {
		return EditTime;
	}
	public void setEditTime(String editTime) {
		EditTime = editTime;
	}
	
}
