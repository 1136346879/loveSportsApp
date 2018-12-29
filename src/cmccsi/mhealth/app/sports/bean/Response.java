package cmccsi.mhealth.app.sports.bean;

public class Response extends BaseNetItem {

	private int resultCode;
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if(bni!=null) {
			Response resp =(Response)bni;
			this.reason = resp.reason;
			this.status = resp.status;
			this.resultCode = resp.resultCode;
		}

	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return reason.length() > 0;
	}

}
