package cmccsi.mhealth.app.sports.bean;

public class SaveDeviceToken extends BaseNetItem {
	public String message;


	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		SaveDeviceToken data = (SaveDeviceToken) bni;
		status = data.status;
		message = data.message;
	}


	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return false;
	}

}
