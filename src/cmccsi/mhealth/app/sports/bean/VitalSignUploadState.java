package cmccsi.mhealth.app.sports.bean;


/**
 * 用于生理数据包
 * @author zy
 *
 */
public class VitalSignUploadState extends BaseNetItem{
	public String dataType;
	public VitalSignUploadStateSuccess datavalue;
	
	public void setValue(VitalSignUploadState date) {
		this.dataType = date.dataType;
		this.datavalue = date.datavalue;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((VitalSignUploadState) bni);
		}
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		return !(datavalue==null || "".equals(datavalue.getUpdateTime()));
	}
	
}
