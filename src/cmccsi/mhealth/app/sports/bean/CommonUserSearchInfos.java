package cmccsi.mhealth.app.sports.bean;

public class CommonUserSearchInfos {
	private String phone;
	private String name;
	private String groupname;
	private String email;
	private String pinyin;
	private String quanpin;
	private boolean sex;//true = 男
	public String getQuanpin() {
		return quanpin;
	}
	public void setQuanpin(String quanpin) {
		this.quanpin = quanpin;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public boolean getSex() {
		return sex;
	}
	public void setSex(String sex) {
		if("0".equals(sex)){
			this.sex = false;
		}else{
			this.sex = true;
		}
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
}
