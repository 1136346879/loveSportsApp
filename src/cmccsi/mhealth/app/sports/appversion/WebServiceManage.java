package cmccsi.mhealth.app.sports.appversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cmccsi.mhealth.app.sports.common.Config;

/**
 * 使用方法
 * 
 * <pre>
 * private Map result;
 * HashMap arg = new HashMap();
 * arg.put(dev, Utils.getDeviceId(mContext));// 添加键值参数
 * result = G09AR_GetUserInfo.parse(WebServiceManage.get(arg, 9, true));// 获取数据并解析
 * </pre>
 * 
 * @author luckchoudog
 *
 */
public class WebServiceManage {
	// private static String web =
	// "http://jiankang.10086.cn/CmccPhr/openClientApi.do?action=";
	private static String web = "http://" + Config.SERVER_NAME + "openClientApi.do?action=";
//	private static String web = "http://" + "10.118.15.172/CmccPhr/"+ "openClientApi.do?action=";

	public static String post(Map<String, String> arg, int count) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String[] aList = argList[count - 1];
		for (int i = 0; i < aList.length; i++) {
			String thisArg = arg.get(aList[i]);
			params.add(new BasicNameValuePair(aList[i], thisArg));
		}
		String php = WebReader.doPost(web + serviceList[count - 1], params);
		return php;
	}

	public static String get(Map<String, String> arg, int count) {
		String[] aList = argList[count - 1];
		String params = "";
		for (int i = 0; i < aList.length; i++) {
			params = params + ("&" + aList[i] + "=" + arg.get(aList[i]));
		}
		String php = WebReader.doGet(web + serviceList[count - 1] + params);
		return php;
	}

	public static String[] serviceList = { "resetPassword", // 用户找回密码 1
			"sendResetVerifyCode", // 发送密码重置短信验证码2
			"checkVerifyCode", // 验证短信验证码3
			"sendRegisterVerifyCode",// 发送注册短信验证码4
			"userRegister",// 用户注册5
			"getFeedbackList",// 7.4 获取意见反馈信息6
			"addFeedback",// 7.3 提交意见反馈信息7
			"getSportHistory",//4.3	运动历史统计8
			"getSportHistory",//4.3	运动历史统计9
			"getSportHistory",//4.3	运动历史统计10
	};
	public static String[][] argList = { { "uid", "userPwd", "tokenMD5" }, // 用户找回密码
			{ "uid", "phoneNumber" },// 发送密码重置短信验证码
			{ "uid", "phoneNumber", "verifyCode", "type" }, // 验证短信验证码
			{ "uid", "phoneNumber" }, // 发送注册短信验证码
			{ "uid", "password", "phoneNumber", "username", "verifyCode" }, // 用户注册
			{ "uid", "page" },// 7.4 获取意见反馈信息
			{ "uid", "feedbackTitle", "feedbackContent", "feedbackTypeDict", "contactInfo" },// 7.3 提交意见反馈信息
			{ "uid"},//4.3	运动历史统计8
			{ "uid", "timeType"},//4.3	运动历史统计9
			{ "uid", "timeType", "endTime"},//4.3	运动历史统计10
	};
}