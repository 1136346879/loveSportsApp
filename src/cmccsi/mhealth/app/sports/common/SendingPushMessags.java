package cmccsi.mhealth.app.sports.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.net.DataSyn;

public class SendingPushMessags {
	private List<NameValuePair> list;

	public void sendMessage(String msg, String targetuser, String myphone) {
		String content = "{\"n_title\":\"您有新的消息！\",\"n_content\":\""+ msg +"\"}";
		list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("sendno", "1"));
		list.add(new BasicNameValuePair("app_key", "a5d3df20fd1050e29e53fe32"));
		list.add(new BasicNameValuePair("receiver_type", "3"));
		list.add(new BasicNameValuePair("receiver_value", StringUtils.toMD5(targetuser)));
		list.add(new BasicNameValuePair("msg_type", "1"));
		list.add(new BasicNameValuePair("msg_content", content));
//		list.add(new BasicNameValuePair("override_msg_id", StringUtils.toMD5(myphone)));
		list.add(new BasicNameValuePair("platform", "android"));
		list.add(new BasicNameValuePair("verification_code", getVerificationCode(StringUtils.toMD5(targetuser))));
		BackInfo bi = new BackInfo();
		DataSyn.getInstance().postDataFromNet("http://api.jpush.cn:8800/v2/push", bi, list);
		Logger.d("TS Sending", StringUtils.toMD5(targetuser));
	}

	private String getVerificationCode(String targetuser) {
		int sendno = 1;
		int receiverType = 3;
		String receiverValue = targetuser;
		String masterSecret = "a7c10f12dbf3c0a4013ca15a";
		String input = String.valueOf(sendno) + receiverType + receiverValue + masterSecret;
		return StringUtils.toMD5(input);
	}
}
