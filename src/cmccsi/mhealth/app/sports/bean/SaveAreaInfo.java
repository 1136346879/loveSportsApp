package cmccsi.mhealth.app.sports.bean;

public class SaveAreaInfo extends BaseNetItem {
	
	public String message;


	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		SaveAreaInfo data = (SaveAreaInfo) bni;
		status = data.status;
		message = data.message;
	}


	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return false;
	}

}
