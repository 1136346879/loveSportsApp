package cmccsi.mhealth.app.sports.bean;

import java.util.List;
/**
 * 用于生理数据包
 * @author zy
 *
 */
public class VitalSigInfoData{
	public String DownloadTime; //最新下拉时间
	public List<VitalSignInfoDataBean> DataArray;
	public String getDownloadTime() {
		return DownloadTime;
	}
	public void setDownloadTime(String downloadTime) {
		DownloadTime = downloadTime;
	}
	public List<VitalSignInfoDataBean> getDataArray() {
		return DataArray;
	}
	public void setDataArray(List<VitalSignInfoDataBean> dataArray) {
		DataArray = dataArray;
	}

	
	
}
