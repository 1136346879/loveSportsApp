package cmccsi.mhealth.app.sports.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import android.os.Build;


/**   
* @项目名称：BLEECGApp3_CLL   
* @类名称：JsonCreat   
* @类描述：按照DADS 心电 http协议 将数据封�?  
* @创建人：
* @创建时间�?014-4-16 下午3:17:20   
* @修改人：WB
* @修改时间�?014-4-16 下午3:17:20   
* @修改备注�?  
* @version
*/
public class PedometerJsonCreat {
	private static String key_data = "data";
	private static String key_datas = "datas";
	
	private static String key_company = "company";
	private static String key_password = "password";
	
	private static String mCompany = "cmcc";
	private static String mPassword = "MHealthV1";
	
	private static String key_apptype = "appType";
	private static String key_deviceid = "deviceId";
	private static String key_deviceType = "deviceType";
	private static String key_datatype = "dataType";
	private static String key_collectdate = "collectDate";
	private static String key_datavalue = "dataValue";

	private static String appType = "WSYD";
	//private static String dataType = "step";
	
	private static JSONArray mappJson;
	private static JSONArray PedmappJson;
	private static JSONObject mhttpJson;
	private static JSONObject mgwJson;
	private static List<NameValuePair> mlist;
	
	
	
	
	/**   
	* appJson(应用参数dataValue字段封装)  
	* TODO(这里描述这个方法适用条件 �?可�?)  
	* TODO(这里描述这个方法的执行流�?�?可�?)   
	* TODO(这里描述这个方法的使用方�?�?可�?)   
	* TODO(这里描述这个方法的注意事�?�?可�?)   
	* @param 
	* @return void
	* @Exception 异常对象   
	* @创建人：
	* @创建时间�?014-4-17 上午9:14:23   
	* @修改人：WB
	* @修改时间�?014-4-17 上午9:14:23
	*/
	/*示例
	 * "dataValue":[{“measureTime�?"2013-01-01 08:00:00"},{“rawData�?�?008,3008,3026
	 * ,
	 * 3056,3081,3111,3136,3104,3071,3042,3010,2992,2985,2976,2960,2944,2934,2929
	 * ,
	 * 2930,2932,2927,2920,2912,2904,2906,2915,2922,2924,2915,2907,2914,2930,2930
	 * ,
	 * 2937,2942,2927”},{"hr":"60"},{“waveForm�?”波形质量过差�?},{“heartRate�?”心率正常�?},{
	 * �?
	 * isArrhythmia�?”心律失常�?},{“stIsNormal�?”ST段正常�?},{“isAF�?”房颤�?},{"detailedResults"
	 * :"12608,20728,AF;"},{"simpleResult":"正常窦�?心律"}]
	 */
	public void appDetailJson(String hour, String snp5, String knp5, String measureTime) {
		// Json对象数组
		mappJson = new JSONArray();
		
		mappJson.put(myJsonCreat("snp5", snp5));
		mappJson.put(myJsonCreat("knp5", knp5));
		mappJson.put(myJsonCreat("level2p5", "0,0,0,0,0,0,0,0,0,0,0,0"));
		mappJson.put(myJsonCreat("level3p5", "0,0,0,0,0,0,0,0,0,0,0,0"));
		mappJson.put(myJsonCreat("level4p5", "0,0,0,0,0,0,0,0,0,0,0,0"));
		mappJson.put(myJsonCreat("yuanp5", "0,0,0,0,0,0,0,0,0,0,0,0"));
		mappJson.put(myJsonCreat("hour", hour));
		mappJson.put(myJsonCreat("measureTime", measureTime));
	}
	/**
	 * 生成运动简要包Jason
	 * @param stepSum 步数
	 * @param calSum 卡路里	
	 * @param distanceSum 距离
	 * @param measureTime 测试时间
	 * @param stepTime 运动时长 秒
	 */
	public void appPedJson(String stepSum, String calSum, String distanceSum, String measureTime,
			String strengthTwo,String strengthThree,String strengthFour,String weight) {
		// Json对象数组
		PedmappJson = new JSONArray();
		PedmappJson.put(myJsonCreat("stepSum", stepSum));
		PedmappJson.put(myJsonCreat("calSum", calSum));
		PedmappJson.put(myJsonCreat("distanceSum", distanceSum));
		PedmappJson.put(myJsonCreat("yxbsSum", "0"));
		PedmappJson.put(myJsonCreat("weight", weight));
		PedmappJson.put(myJsonCreat("stride", "0"));
		PedmappJson.put(myJsonCreat("degreeOne", strengthTwo));
		PedmappJson.put(myJsonCreat("degreeTwo", strengthTwo));
		PedmappJson.put(myJsonCreat("degreeThree", strengthThree));
		PedmappJson.put(myJsonCreat("degreeFour", strengthFour));
		PedmappJson.put(myJsonCreat("uploadType", "1"));
		PedmappJson.put(myJsonCreat("measureTime", measureTime));
	}
	/**   
	* httpJson(系统参数封装)  
	* TODO(这里描述这个方法适用条件 �?可�?)  
	* TODO(这里描述这个方法的执行流�?�?可�?)   
	* TODO(这里描述这个方法的使用方�?�?可�?)   
	* TODO(这里描述这个方法的注意事�?�?可�?)   
	* @param 
	* @return void
	* @Exception 异常对象   
	* @创建人：
	* @创建时间�?014-4-17 上午9:18:29   
	* @修改人：WB
	* @修改时间�?014-4-17 上午9:18:29
	*/
	public void httpJsonWithVersin(String collectDate, String deviceId,String dataType,String version) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, PedmappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", "")+"-version-"+version);
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void httpJson(String collectDate, String deviceId,String dataType) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, PedmappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", ""));
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**   
	* httpJson(系统参数封装)  
	* TODO(这里描述这个方法适用条件 �?可�?)  
	* TODO(这里描述这个方法的执行流�?�?可�?)   
	* TODO(这里描述这个方法的使用方�?�?可�?)   
	* TODO(这里描述这个方法的注意事�?�?可�?)   
	* @param 
	* @return void
	* @Exception 异常对象   
	* @创建人：
	* @创建时间�?014-4-17 上午9:18:29   
	* @修改人：WB
	* @修改时间�?014-4-17 上午9:18:29
	*/
	public void httpJson(String collectDate, String deviceId,String devicePara,String dataType) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_deviceType, devicePara);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, PedmappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", ""));
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void httpJsonDetail(String collectDate, String deviceId,String dataType) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, mappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", ""));
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void httpJsonDetailWithVersion(String collectDate, String deviceId,String dataType,String version) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, mappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", "")+"-version-"+version);
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void httpJsonDetail(String collectDate, String deviceId,String devicePara,String dataType) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_deviceType, devicePara);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, mappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, Build.MODEL.replace(" ", ""));
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**   
	* jsonsend(postDataFromNet方法参数封装)  
	* TODO(这里描述这个方法适用条件 �?可�?)  
	* TODO(这里描述这个方法的执行流�?�?可�?)   
	* TODO(这里描述这个方法的使用方�?�?可�?)   
	* TODO(这里描述这个方法的注意事�?�?可�?)   
	* @param 
	* @return List<NameValuePair>
	* @Exception 异常对象   
	* @创建人：
	* @创建时间�?014-4-17 上午9:19:45   
	* @修改人：WB
	* @修改时间�?014-4-17 上午9:19:45
	*/
	public List<NameValuePair> jsonsend () {
		mlist = new ArrayList<NameValuePair>();
		
		mlist.add(new BasicNameValuePair(key_datas,mgwJson.toString()));
		
		return mlist;
		
	}
	
	/**   
	* myJsonCreat(Json对象创建)  
	* TODO(这里描述这个方法适用条件 �?可�?)  
	* TODO(这里描述这个方法的执行流�?�?可�?)   
	* TODO(这里描述这个方法的使用方�?�?可�?)   
	* TODO(这里描述这个方法的注意事�?�?可�?)   
	* @param 
	* @return JSONObject
	* @Exception 异常对象   
	* @创建人：
	* @创建时间�?014-4-17 上午9:21:27   
	* @修改人：WB
	* @修改时间�?014-4-17 上午9:21:27
	*/
	public JSONObject myJsonCreat (String key, String value) {
		 JSONObject myJsonObject = new JSONObject();
		 try {
			myJsonObject.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return myJsonObject;		
	}
	
}