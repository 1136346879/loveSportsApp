package cmccsi.mhealth.app.sports.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;

public class SimplePost {
	private static final String TAG = "SimplePost";
	private static final String STATUS = "status";
	private static HttpClient mHttpClient;
	public static final int SIMPLENET_SUCCESS = 0;
	public static final int SIMPLENET_FAIL = 1;

	public SimplePost() {

	}

	/**
	 * post上传数据
	 * 
	 * @param URL
	 *            地址
	 * @param map
	 *            参数的集合
	 * @param context
	 */
	public static void iploadData(String URL, Map<String, String> map, Context context, Handler handler) {
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			listvp.add(pair);
		}
		simplePost(URL, listvp, context, handler);
	}

	private static void simplePost(String URL, List<NameValuePair> listvp, Context context, Handler handler) {
		Message msg = new Message();
		Object obj = null;
		try {
			if (NetworkTool.getNetworkState(context) == 0) {
				msg.what = SIMPLENET_FAIL;
				obj = context.getResources().getString(R.string.MESSAGE_INTERNET_ERROR);
			} else {
				Logger.i("POST", URL);
				HttpPost httpPost = new HttpPost(URL);
				HttpEntity requestHttpEntity = new UrlEncodedFormEntity(listvp, "UTF-8");
				httpPost.setEntity(requestHttpEntity);
				if (mHttpClient == null) {
					mHttpClient = new DefaultHttpClient();
					// 设置8秒请求超时
					mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
				}
				HttpResponse response = mHttpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
					Logger.i("JSON", jsonStr);
					if (!TextUtils.isEmpty(jsonStr)) {
						JSONObject jsonObject = new JSONObject(jsonStr);
						String status = jsonObject.getString(STATUS);
						if (status != null && status.equals(SharedPreferredKey.SUCCESS)) {
							msg.what = SIMPLENET_SUCCESS;
							obj = "更新成功";
						} else {
							msg.what = SIMPLENET_FAIL;
							obj = jsonObject.get("reason");
						}
					}
				}else{
					msg.what = SIMPLENET_FAIL;
				}
			}
		} catch (JSONException e) {
			msg.what = SIMPLENET_FAIL;
			e.printStackTrace();
		} catch (ParseException e) {
			msg.what = SIMPLENET_FAIL;
			e.printStackTrace();
		} catch (IOException e) {
			msg.what = SIMPLENET_FAIL;
			e.printStackTrace();
		}catch (Exception e) {
			msg.what = SIMPLENET_FAIL;
			e.printStackTrace();
		} finally {
			if (obj == null)
				obj = context.getResources().getString(R.string.MESSAGE_INTERNET_ERROR);
			msg.obj = obj;
			handler.sendMessage(msg);
		}
	}
}
