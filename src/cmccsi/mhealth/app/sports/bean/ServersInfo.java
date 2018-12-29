package cmccsi.mhealth.app.sports.bean;

public class ServersInfo {
	private String serverName;
	public String contactfile;
	public String groupfile;
	public String serverversion = "2";

	public String getServerversion() {
		return serverversion;
	}

	public void setServerversion(String serverversion) {
		this.serverversion = serverversion;
	}

	public String getGroupfile() {
		return groupfile;
	}

	public void setGroupfile(String groupfile) {
		this.groupfile = groupfile;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getContactfile() {
		return contactfile;
	}

	public void setContactfile(String contactfile) {
		this.contactfile = contactfile;
	}
}
