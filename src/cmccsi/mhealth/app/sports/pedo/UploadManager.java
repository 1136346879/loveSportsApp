package cmccsi.mhealth.app.sports.pedo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;
import cmccsi.mhealth.app.sports.bean.DataDetailPedo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.SimplePostInfo;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.net.PedometerJsonCreat;

/**
 * 上传数据 没有启动线程 自己启动
 * @type UploadManager
 * TODO
 * @author jiazhi.cao
 * @time 2015-3-20下午3:21:06
 */
public class UploadManager {
	 private static String myGwUrl = Config.PEDO_UPLOAD_URL;

	 public static boolean uploadPedo(PedometorDataInfo data)
	 {
		 if(data==null)
		 {
			 return true;
		 }
		 String collectDate = "";
		 PedometerJsonCreat mJsonCreat = new PedometerJsonCreat();			
			List<NameValuePair> mUploadlist = new ArrayList<NameValuePair>();
			collectDate = data.createtime.substring(0, 10);

			mJsonCreat.appPedJson(data.stepNum, data.cal, data.distance,
					data.createtime, data.strength2, data.strength3, data.strength4,
					"70");
			mJsonCreat.httpJson(collectDate, data.deviceId, "stepCount");
			mUploadlist = mJsonCreat.jsonsend();
			Log.v("Service", "发送的数据：" + mUploadlist.toString());
			SimplePostInfo info = new SimplePostInfo();			
			int result= DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadlist);
			return (result==0);
	 }
	 
	 public static boolean uploadBlePedo(PedometorDataInfo data)
	 {
		 if(data==null)
		 {
			 return true;
		 }
		 String collectDate = "";
		 PedometerJsonCreat mJsonCreat = new PedometerJsonCreat();			
			List<NameValuePair> mUploadlist = new ArrayList<NameValuePair>();
			collectDate = data.createtime.substring(0, 10);

			mJsonCreat.appPedJson(data.stepNum, data.cal, data.distance,
					data.createtime, data.strength2, data.strength3, data.strength4,
					"70");
			mJsonCreat.httpJson(collectDate, data.deviceId,data.deviceType, "stepCount");
			mUploadlist = mJsonCreat.jsonsend();
			Log.v("Service", "发送的数据：" + mUploadlist.toString());
			SimplePostInfo info = new SimplePostInfo();			
			int result= DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadlist);
			return (result==0);
	 }
	 
	 public static boolean uploadBlePedoDetail(PedoDetailInfo pedoDetailList,String deviceId,String devicePara)
	 {
		 if(pedoDetailList==null)
		 {
			 return true;
		 }
		 boolean result=true;
		 for (DataDetailPedo detaiPedo : pedoDetailList.datavalue) {
			String collectDate=DateFormatUtils.ChangeFormat(pedoDetailList.date, FormatType.DateShot, FormatType.DateWithUnderline);
			PedometerJsonCreat dJsonCreat = new PedometerJsonCreat();
			List<NameValuePair> mUploadDetailList = new ArrayList<NameValuePair>();
			dJsonCreat.appDetailJson(detaiPedo.start_time, detaiPedo.snp5, detaiPedo.knp5, collectDate);
			dJsonCreat.httpJsonDetail(collectDate, deviceId, devicePara,"stepDetail");
			mUploadDetailList = dJsonCreat.jsonsend();
			Log.v("Service", "发送的数据：" + mUploadDetailList.toString());
			SimplePostInfo info = new SimplePostInfo();
			int dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadDetailList);
			if (dataFromPHP != 0) {
				result = false;
			}
		 }
		 return result;
	 }
	 
	 public static boolean uploadPedoDetail(PedoDetailInfo pedoDetailList,String deviceId)
	 {
		 if(pedoDetailList==null)
		 {
			 return true;
		 }
		 boolean result=true;
		 for (DataDetailPedo detaiPedo : pedoDetailList.datavalue) {
			String collectDate=DateFormatUtils.ChangeFormat(pedoDetailList.date, FormatType.DateShot, FormatType.DateWithUnderline);
			PedometerJsonCreat dJsonCreat = new PedometerJsonCreat();
			List<NameValuePair> mUploadDetailList = new ArrayList<NameValuePair>();
			dJsonCreat.appDetailJson(detaiPedo.start_time, detaiPedo.snp5, detaiPedo.knp5, collectDate);
			dJsonCreat.httpJsonDetail(collectDate, deviceId,"stepDetail");
			mUploadDetailList = dJsonCreat.jsonsend();
			Log.v("Service", "发送的数据：" + mUploadDetailList.toString());
			SimplePostInfo info = new SimplePostInfo();
			int dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadDetailList);
			if (dataFromPHP != 0) {
				result = false;
			}
		 }
		 return result;
	 }
	 
	 public static boolean uploadBlePedoDetail(PedoDetailInfo pedoDetailList,String deviceId)
	 {
		 if(pedoDetailList==null)
		 {
			 return true;
		 }
		 boolean result=true;
		 for (DataDetailPedo detaiPedo : pedoDetailList.datavalue) {
			String collectDate=DateFormatUtils.ChangeFormat(pedoDetailList.date, FormatType.DateShot, FormatType.DateWithUnderline);
			PedometerJsonCreat dJsonCreat = new PedometerJsonCreat();
			List<NameValuePair> mUploadDetailList = new ArrayList<NameValuePair>();
			dJsonCreat.appDetailJson(detaiPedo.start_time, detaiPedo.snp5, detaiPedo.knp5, collectDate);
			dJsonCreat.httpJsonDetail(collectDate, deviceId, "stepDetail");
			mUploadDetailList = dJsonCreat.jsonsend();
			Log.v("Service", "发送的数据：" + mUploadDetailList.toString());
			SimplePostInfo info = new SimplePostInfo();
			int dataFromPHP = DataSyn.getInstance().postDataFromNet(myGwUrl, info, mUploadDetailList);
			if (dataFromPHP != 0) {
				result = false;
			}
		 }
		 return result;
	 }
}
