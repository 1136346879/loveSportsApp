package cmccsi.mhealth.app.sports.bean;
public class PageInfo extends BaseNetItem{
	private int pageIndex;
	private int pageSize;
	private int totalNum;
	private int totalPages;
	
	public PageInfo() {
		super();
	}

	public PageInfo(int pageIndex, int pageSize, int totalNum) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalNum = totalNum;
	}
	
	public PageInfo(int pageIndex, int pageSize, int totalNum,int totalPages) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalNum = totalNum;
		this.totalPages = totalPages;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return false;
	}
	

}