package cmccsi.mhealth.app.sports.bean;

/**
 * 存储好友联系人信息
 * @type ContactInfo
 * TODO
 * @author shaoting.chen
 * @time 2015年6月2日下午2:53:54
 */
public class ContactInfo{
	
	public String userid = null; // 
	public String uid = null; //
	public String phonenumber;
	public String phonename;
	public String isFriend; //0：不是好友    1：是好友    2: 已发送请求
	public boolean checked = false;

	public ContactInfo(String userid, String uid, String phonenumber, String phonename, String isFriend, boolean checked) {
		this.userid = userid;
		this.uid = uid;
		this.phonenumber = phonenumber;
		this.phonename = phonename;
		this.isFriend = isFriend;
		this.checked = checked;
	}
	public ContactInfo(String userid, String uid, String phonenumber, String isFriend) {
		this.userid = userid;
		this.uid = uid;
		this.phonenumber = phonenumber;
		this.isFriend = isFriend;
	}
	public ContactInfo() {
		
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getPhonename() {
		return phonename;
	}
	public void setPhonename(String phonename) {
		this.phonename = phonename;
	}
	public String getIsFriend() {
		return isFriend;
	}
	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	

}
