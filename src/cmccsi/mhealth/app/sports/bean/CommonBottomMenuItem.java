package cmccsi.mhealth.app.sports.bean;

public class CommonBottomMenuItem {
	private int id;
	private String menuName;
	private int menuIcon;

	public CommonBottomMenuItem(int id, String menuName, int menuIcon) {
		this.id = id;
		this.menuName = menuName;
		this.menuIcon = menuIcon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public int getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(int menuIcon) {
		this.menuIcon = menuIcon;
	}

}
