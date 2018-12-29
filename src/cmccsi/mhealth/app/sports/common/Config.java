/********************************************
 * 文件名		：Config.java
 * 版本信息	：1.00
 * 作者		：Gaofei - 高飞
 * 创建日期	：2013-1-8
 * 修改日期	：2013-3-12
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import cmccsi.mhealth.app.sports.R;

/**   
*    
* 项目名称：iShangTrunk   
* 类名称：Config   
* 类描述：   
* 创建人：Gaofei - 高飞    
* 创建时间：2012-1-8 下午6:49:14   
* 修改人：Gaofei - 高飞   
* 修改时间：2012-1-8 下午6:49:14   
* 修改备注：   
* @version    
*    
*/
public class Config {
	/**
	 * 是否将错误信息发送到邮件  true表示发送，false表示不发送
	 */
	public static final boolean isMail = true;
	/**
	 * 是否是独立版的apk，影响到页面的跳转，true表示为是独立版的，false表示为非独立版。
	 */
	public  static final boolean ISALONE = true;
	
//	//一拖三的action 清单文件里记得改啊
	public final static String SC_ACTION = "cmccsi.mhealth.portal.sports.service.StepService";
	public static final String STEP_SENDING_ACTION = "cmccsi.mhealth.portal.sports.STEP_SENDING";
	public static final String UPLOADSTATUS_ACTION = "cmccsi.mhealth.portal.sports.STEP_UPLOADSTATUS";
	public static final String PHONESTEP_STOP_ACTION = "cmccsi.mhealth.portal.sports.PHONESTEP_STOP_ACTION";
	public static final String PHONESTEP_STARTUPLOAD_ACTION = "cmccsi.mhealth.portal.sports.phonestep_startupload_action";
	public static final String PHONESTEP_UPLOAD_ACTION = "cmccsi.mhealth.portal.sports.phonestep_upload_action";
	public static final String PHONESTEP_STOP_NOSAVE_ACTION = "cmccsi.mhealth.portal.sports.phonestep_stop_nosave_action";
	//独立的action 清单文件里记得改啊
//	public final static String SC_ACTION = "cmccsi.mhealth.app.sports.service.StepService";
//	public static final String STEP_SENDING_ACTION = "cmccsi.mhealth.app.sports.STEP_SENDING";
//	public static final String UPLOADSTATUS_ACTION = "cmccsi.mhealth.app.sports.STEP_UPLOADSTATUS";
//	public static final String PHONESTEP_STOP_ACTION = "cmccsi.mhealth.app.sports.PHONESTEP_STOP_ACTION";
//	public static final String PHONESTEP_STARTUPLOAD_ACTION = "cmccsi.mhealth.app.sports.phonestep_startupload_action";
//	public static final String PHONESTEP_UPLOAD_ACTION = "cmccsi.mhealth.app.sports.phonestep_upload_action";
//	public static final String PHONESTEP_STOP_NOSAVE_ACTION = "cmccsi.mhealth.app.sports.phonestep_stop_nosave_action";
	
	private static final String TAG = SharedPreferredKey.SHARED_NAME;
	
    // 自动更新配置常量参数
    public static final String UPDATE_SERVER = "http://mhealth.cmri.cn/iactivity/app/"; // 下载目录
    public static final String RACE_PIC_SERVER_ROOT = "http://223.202.47.138/iactivityp/"; // 比赛图下载目录
    public static final String RACE_TITLE_PIC = "image/cimg/";// temp比赛图目录

	public static final String SERVER_NAME = "health.10086.cn/CmccPhr/";//正式环境
	public static final String SERVER_NAME_LOGIN = "http://health.10086.cn/healthcare_service_restful/s/auth/verifyLogin";//正式环境
//    public static final String SERVER_NAME = "112.33.1.160:81/CmccPhr/";//测试环境
//    public static final String SERVER_NAME_LOGIN = "http://112.33.1.160:81/healthcare_service_restful/s/auth/verifyLogin";//测试环境
	
    
    public static final String SERVER_DESTINY = "http://"+SERVER_NAME+"app/";

    public static final String UPDATE_APKNAME = "SportHealth.apk"; // 下载文件名
    public static final String UPDATE_VERJSON = "versioninfo.json"; // 查询文本
                                                                    // uft-8
//	public static final String SERVER_LIST = "serverlist.json";
	public static final String CONTACT_LIST = "contact_cmri.json";
    public static final String UPDATE_SAVENAME = "updateapksamples.apk"; // 保存文件名称
	
	
	public static final String DATAS_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Datas.txt";// +MD5.getMD5(url));
	public static final String RECORD_URL = Environment.getExternalStorageDirectory() + "/ishang_image/CallSms.txt";// +MD5.getMD5(url));
	public static final String ERRORLOG_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Errorlogs/";// +MD5.getMD5(url));
	public static final String PEDO_UPLOAD_URL = "http://112.33.1.61:8081/DADS_HTTP/service/uploadSportsDatas"; // 上传运动数据
	public static final String ECG_UPLOAD_URL = "http://112.33.1.61:8081/DADS_HTTP/service/uploadEcgDatas"; // 上传心电数据
//	public static final String PEDO_UPLOAD_URL = "http://112.33.1.161:8081/DADS_HTTP/service/uploadSportsDatas"; // 上传运动数据//测试环境
//	public static final String ECG_UPLOAD_URL = "http://112.33.1.161:8081/DADS_HTTP/service/uploadEcgDatas"; // 上传心电数据//测试环境
	
	
	/**   
	* getVerCode(获取整数短版本号) 
	* @param  	context  上下文
	* @return 	int 整数短版本号   
	*/
	public static int getVerCode(Context context) {
		int verCode = -1;
		if (context != null) {
			try {
				PackageInfo info = context.getPackageManager().getPackageInfo(
						"cmccsi.mhealth.portal.sports", 0);
				if (info != null)
					verCode = info.versionCode;
			} catch (NameNotFoundException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return verCode;
	}
	
	/**   
	* getVerName(获取详细版本号：1.1.0.130313) 
	* @param  	context  上下文
	* @return 	String 详细版本号   
	*/
	public static String getVerName(Context context) {
		String verName = "";
		try {
			if(Config.ISALONE){
				verName = context.getPackageManager().getPackageInfo(
						"cmccsi.mhealth.app.sports", 0).versionName;
			}else{
				verName = context.getPackageManager().getPackageInfo(
						"cmccsi.mhealth.portal.sports", 0).versionName;
			}
			
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;	

	}

}
