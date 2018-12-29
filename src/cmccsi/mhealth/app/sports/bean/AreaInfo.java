package cmccsi.mhealth.app.sports.bean;


/**
 * 区域信息存储bean
 * 
 * @type AreaInfo TODO
 * @author shaoting.chen
 * @time 2015年3月2日下午3:14:27
 */
public class AreaInfo{
	public static String TAG = "AreaInfo";

	// public String status = "SUCCESS";
	public int orgId; // 区域ID--唯一标识
	public String orgName = null; // 区域名称

	public AreaInfo(int orgId, String orgName) {
		this.orgId = orgId;
		this.orgName = orgName;
	}
	public AreaInfo() {
		
	}
	public int getOrgId() {
		return orgId;
	}
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
