package cmccsi.mhealth.app.sports.bean;

public abstract class BaseNetItem {
	public String status = "UNSET";
	public String reason = "";

	public abstract void setValue(BaseNetItem bni);
	public abstract boolean isValueData(BaseNetItem bni);

	public void initialDate() {
	}
}
