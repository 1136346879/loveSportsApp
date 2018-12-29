package cmccsi.mhealth.app.sports.appversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.StrictMode;
import android.util.Log;

public class WebReader {
	public static String doGet(String url) {
		System.out.println("---doGet-url------" + url);
		Log.e("~~~~doGet~~~~~", "---doGet-url------" + url);
		getHttpClient();
		try {
			HttpGet httpget = new HttpGet(url);
			InputStream fr = httpClient.execute(httpget).getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(fr, "utf-8"));
			String temp = br.readLine();
			StringBuffer result = new StringBuffer("");
			result.append(temp);
			while ((temp = br.readLine()) != null) {
				result.append(temp);
			}
			fr.close();
			br.close();
			return result.toString();
		} catch (Exception ee) {
			return null;
		}
	}

	public static String doPost(String url, List<NameValuePair> params) {
		System.out.println("--doPost--url------" + url);
		Log.e("~~~~doPost~~~~~", "--doPost--url------" + url);
		getHttpClient();
		HttpPost httpRequest = new HttpPost(url);
		String strResult = "doPostError";
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = null;
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			} else {
				strResult = "Error Response1:" + httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			if (e != null && e.getMessage() != null) {
				strResult = "Error Response2:" + e.getMessage().toString();
			} else {
				strResult = "Error Response2:ClientProtocolException";
			}
			e.printStackTrace();
		} catch (IOException e) {
			if (e != null && e.getMessage() != null) {
				strResult = "Error Response3:" + e.getMessage().toString();
			} else {
				strResult = "Error Response3:IOException";
			}
			e.printStackTrace();
		} catch (Exception e) {
			if (e != null && e.getMessage() != null) {
				strResult = "Error Response4:" + e.getMessage().toString();
			} else {
				strResult = "Error Response4:Exception";
			}
			e.printStackTrace();
		}
		return strResult;
	}

	private static HttpParams httpParams;
	private static HttpClient httpClient;

	public static HttpClient getHttpClient() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion > 8) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
		}
		StrictMode
				.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		if (null == httpParams || null == httpClient) {
			httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpClientParams.setRedirecting(httpParams, true);
			httpClient = new DefaultHttpClient(httpParams);
		}
		return httpClient;
	}
}
