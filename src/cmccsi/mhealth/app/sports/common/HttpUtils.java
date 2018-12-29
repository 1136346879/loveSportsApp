package cmccsi.mhealth.app.sports.common;


import org.apache.http.NameValuePair;


public class HttpUtils {

	public static String postByHttpClient(String strUrl,
			NameValuePair... nameValuePairs) {
		return CustomHttpClient.PostFromWebByHttpClient(strUrl,nameValuePairs);
	}

	public static String getByHttpClient(String strUrl,
			NameValuePair... nameValuePairs) throws Exception {
		return CustomHttpClient.getFromWebByHttpClient(strUrl, nameValuePairs);
	}
}
