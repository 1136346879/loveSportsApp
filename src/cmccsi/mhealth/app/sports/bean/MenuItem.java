package cmccsi.mhealth.app.sports.bean;

public class MenuItem {
	private int id;
	private int drawablesBlack;
	private int drawablesLight;
	private String menuName;
	private int clubid;
	private int viewType;
	
	public static final int NORMAL_ITEM = 0;
	public static final int CHILD_ITEM = 1;
	public static final int GROUP_TITLE = 2;
	
	public MenuItem(int id, int drawablesBlack, int drawablesLight, String menuName, int clubid, int viewType) {
		super();
		this.id = id;
		this.drawablesBlack = drawablesBlack;
		this.drawablesLight = drawablesLight;
		this.menuName = menuName;
		this.clubid = clubid;
		this.viewType = viewType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDrawablesBlack() {
		return drawablesBlack;
	}
	public void setDrawablesBlack(int drawablesBlack) {
		this.drawablesBlack = drawablesBlack;
	}
	public int getDrawablesLight() {
		return drawablesLight;
	}
	public void setDrawablesLight(int drawablesLight) {
		this.drawablesLight = drawablesLight;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getClubid() {
		return clubid;
	}
	public void setClubid(int clubid) {
		this.clubid = clubid;
	}
	public int getViewType() {
		return viewType;
	}
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}


}
