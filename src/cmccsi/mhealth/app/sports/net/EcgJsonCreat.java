package cmccsi.mhealth.app.sports.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


/**   
* @项目名称：BLEECGApp3_CLL   
* @类名称：JsonCreat   
* @类描述：按照DADS 心电 http协议 将数据封装   
* @创建人：
* @创建时间：2014-4-16 下午3:17:20   
* @修改人：WB
* @修改时间：2014-4-16 下午3:17:20   
* @修改备注：   
* @version
*/
public class EcgJsonCreat {
	private static String key_data = "data";
	private static String key_datas = "datas";
	
	private static String key_company = "company";
	private static String key_password = "password";
	
	private static String mCompany = "cmcc";
	private static String mPassword = "MHealthV1";
	
	private static String key_apptype = "appType";
	private static String key_deviceid = "deviceId";
	private static String key_datatype = "dataType";
	private static String key_collectdate = "collectDate";
	private static String key_datavalue = "dataValue";

	private static String appType = "WSXD";
	private static String dataType = "ECG";
	
	private static String key_rawdata= "rawData";
	private static String key_hr= "hr";
	private static String key_isaf = "isAF";
	private static String key_detailedresults= "detailedResults";
	private static String key_simpleresult= "simpleResult";
	private static String key_measureTime = "measureTime";
	
	private static JSONArray mappJson;
	private static JSONObject mhttpJson;
	private static JSONObject mgwJson;
	private static List<NameValuePair> mlist;
	
	
	
	
	/**   
	* appJson(应用参数dataValue字段封装)  
	* TODO(这里描述这个方法适用条件 – 可选)  
	* TODO(这里描述这个方法的执行流程 – 可选)   
	* TODO(这里描述这个方法的使用方法 – 可选)   
	* TODO(这里描述这个方法的注意事项 – 可选)   
	* @param 
	* @return void
	* @Exception 异常对象   
	* @创建人：
	* @创建时间：2014-4-17 上午9:14:23   
	* @修改人：WB
	* @修改时间：2014-4-17 上午9:14:23
	*/
	/*示例
	 * "dataValue":[{“measureTime”:"2013-01-01 08:00:00"},{“rawData”:“3008,3008,3026
	 * ,
	 * 3056,3081,3111,3136,3104,3071,3042,3010,2992,2985,2976,2960,2944,2934,2929
	 * ,
	 * 2930,2932,2927,2920,2912,2904,2906,2915,2922,2924,2915,2907,2914,2930,2930
	 * ,
	 * 2937,2942,2927”},{"hr":"60"},{“waveForm”:”波形质量过差”},{“heartRate”:”心率正常”},{
	 * “
	 * isArrhythmia”:”心律失常”},{“stIsNormal”:”ST段正常”},{“isAF”:”房颤”},{"detailedResults"
	 * :"12608,20728,AF;"},{"simpleResult":"正常窦性心律"}]
	 */
	public void appJson(String rawData, String hr, String isAF,
			String detailedResults, String simpleResult, String measureTime
			,String hrv,String mood) {
		// Json对象数组
		mappJson = new JSONArray();
		mappJson.put(myJsonCreat(key_measureTime, measureTime));
		mappJson.put(myJsonCreat(key_rawdata, rawData));
		mappJson.put(myJsonCreat(key_hr, hr));
		mappJson.put(myJsonCreat("waveForm", ""));
		mappJson.put(myJsonCreat("heartRate", ""));
		mappJson.put(myJsonCreat("isArrhythmia", ""));
		mappJson.put(myJsonCreat("stIsNormal", ""));
		mappJson.put(myJsonCreat(key_isaf, isAF));
		mappJson.put(myJsonCreat(key_detailedresults, detailedResults));
		mappJson.put(myJsonCreat(key_simpleresult, simpleResult));
		mappJson.put(myJsonCreat("hrv", hrv));
		mappJson.put(myJsonCreat("mood", mood));
	}
	
	/**   
	* httpJson(系统参数封装)  
	* TODO(这里描述这个方法适用条件 – 可选)  
	* TODO(这里描述这个方法的执行流程 – 可选)   
	* TODO(这里描述这个方法的使用方法 – 可选)   
	* TODO(这里描述这个方法的注意事项 – 可选)   
	* @param 
	* @return void
	* @Exception 异常对象   
	* @创建人：
	* @创建时间：2014-4-17 上午9:18:29   
	* @修改人：WB
	* @修改时间：2014-4-17 上午9:18:29
	*/
	public void httpJson(String collectDate, String deviceId) {
		mhttpJson = new JSONObject();
		mgwJson = new JSONObject();

		try {
			mhttpJson.put(key_apptype, appType);
			mhttpJson.put(key_deviceid, deviceId);
			mhttpJson.put(key_collectdate, collectDate);
			mhttpJson.put(key_datatype, dataType);
			mhttpJson.put(key_datavalue, mappJson);
			mgwJson.put(key_company, mCompany);
			mgwJson.put(key_password, mPassword);
			mgwJson.put(key_data, mhttpJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**   
	* jsonsend(postDataFromNet方法参数封装)  
	* TODO(这里描述这个方法适用条件 – 可选)  
	* TODO(这里描述这个方法的执行流程 – 可选)   
	* TODO(这里描述这个方法的使用方法 – 可选)   
	* TODO(这里描述这个方法的注意事项 – 可选)   
	* @param 
	* @return List<NameValuePair>
	* @Exception 异常对象   
	* @创建人：
	* @创建时间：2014-4-17 上午9:19:45   
	* @修改人：WB
	* @修改时间：2014-4-17 上午9:19:45
	*/
	public List<NameValuePair> jsonsend () {
		mlist = new ArrayList<NameValuePair>();
		
		mlist.add(new BasicNameValuePair(key_datas,mgwJson.toString()));
		
		return mlist;
		
	}
	
	/**   
	* myJsonCreat(Json对象创建)  
	* TODO(这里描述这个方法适用条件 – 可选)  
	* TODO(这里描述这个方法的执行流程 – 可选)   
	* TODO(这里描述这个方法的使用方法 – 可选)   
	* TODO(这里描述这个方法的注意事项 – 可选)   
	* @param 
	* @return JSONObject
	* @Exception 异常对象   
	* @创建人：
	* @创建时间：2014-4-17 上午9:21:27   
	* @修改人：WB
	* @修改时间：2014-4-17 上午9:21:27
	*/
	public JSONObject myJsonCreat (String key, String value) {
		if(value==null)
		{
			value="";
		}
		 JSONObject myJsonObject = new JSONObject();
		 try {
			myJsonObject.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 return myJsonObject;		
	}
	
}