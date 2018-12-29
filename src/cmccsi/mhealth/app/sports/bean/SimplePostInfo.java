package cmccsi.mhealth.app.sports.bean;

public class SimplePostInfo extends BaseNetItem {

	public String info="";
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		SimplePostInfo ecginfo=(SimplePostInfo)bni;
		this.info=ecginfo.info;
		this.status=ecginfo.status;
		this.reason=ecginfo.reason;
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		SimplePostInfo ecginfo=(SimplePostInfo)bni;
		if(ecginfo==null)
		{
			return false;
		}
		return true;
		
	}
}
