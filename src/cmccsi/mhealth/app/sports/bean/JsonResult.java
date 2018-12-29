package cmccsi.mhealth.app.sports.bean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonResult<T> extends BaseNetItem{

	/**
	 * 返回结果状态:1成功0失败 缺省值为1
	 */
	private String resultCode = "3";
	/**
	 * 返回提示信息
	 */
	private String resultMsg = "";
	/**
	 * 数据分页信息
	 */
	private PageInfo pageInfo;
	/**
	 * 同步时间
	 */
	private Date syncDate;
	/**
	 * 单条数据信息
	 */
	private T data;
	/**
	 * 多条数据信息
	 */
	private List<T> dataList;
	/**
	 * 其他需要返回的数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
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
