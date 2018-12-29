package cmccsi.mhealth.app.sports.bean;

public class ClubData {
	public int clubid;
	public String clubname;

	public ClubData(int clubid, String clubname) {
		this.clubid = clubid;
		this.clubname = clubname;
	}
	public ClubData(String clubid, String clubname) {
		this.clubid = Integer.parseInt(clubid);
		this.clubname = clubname;
	}

	public ClubData() {
	}

	public int getClubid() {
		return clubid;
	}

	public void setClubid(int clubid) {
		this.clubid = clubid;
	}

	public String getClubname() {
		return clubname;
	}

	public void setClubname(String clubname) {
		this.clubname = clubname;
	}

}
