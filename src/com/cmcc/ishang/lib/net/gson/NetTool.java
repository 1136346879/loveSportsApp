package com.cmcc.ishang.lib.net.gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class NetTool {
	private HttpClient mHttpClient;
	private static NetTool instance = new NetTool();
	
	private NetTool() {
		mHttpClient = new DefaultHttpClient();
		mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
	}
	
	public void setConnectTimeOut(Object value){
		mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, value);
	}
	public void setSoTimeOut(Object value){
		mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, value);
	}
	
	/**
	 * 提供实例 默认CONNECTION_TIMEOUT以及SO_TIMEOUT均为十秒
	 * @return
	 */
	public static NetTool getInstance() {
		return instance;
	}
	
	/**
	 * post方法获取
	 * @param queryStr	url语句
	 * @param reqData	承接类
	 * @param list		参数集合
	 * @return	返回字符串 SUCCESS 则成功  其他则是错误描述
	 */
	public String postDataFromNet(String queryStr, BaseNetItem reqData, List<NameValuePair> list) {
		synchronized (mHttpClient) {
			HttpEntity entity = httpClientExecutePost(queryStr, list);

			if (null == entity) {
				return Responses.ENTITY_NONE;
			}

			InputStream instream = null;
			try {
				instream = entity.getContent();
				if (null == instream) {
					return Responses.ERROR_ENTITY_GETCONTENT;
				}
				BufferedReader read = new BufferedReader(new InputStreamReader(
	                    instream));
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            try {
	                while ((line = read.readLine()) != null) {
	                    sb.append(line + "/n");
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }

	            Log.e("yd", queryStr + "  data :" + sb.toString());
				JsonReader reader = new JsonReader(new InputStreamReader(instream, "UTF-8"));
				Gson gson = new Gson();
				BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
				reader.close();

				if (null == rawData) {
					return Responses.RAWDATA_NONE;
				}

				if (!rawData.isValueData(rawData)) {
					return Responses.RAWDATA_NOT_VALUABLE;
				}

				reqData.setValue(rawData);

				if (!rawData.status.equals("SUCCESS"))
					return Responses.NOT_SUCCESS;

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				return Responses.ERROR_JSE;
			} catch (IOException ex) {
				ex.printStackTrace();
				return Responses.ERROR_IO;
			} finally {
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return Responses.ERROR;
				}
			}
		}
		return Responses.SUCCESS;
	}

	/**
	 * @param queryStr
	 * @param list
	 * @return
	 */
	private HttpEntity httpClientExecutePost(String queryStr, List<NameValuePair> list) {
		HttpEntity httpEntity = null;
		HttpPost httpPost = new HttpPost(queryStr);
		try {
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(list,HTTP.UTF_8);
			httpPost.setEntity(requestHttpEntity);
			HttpResponse response = mHttpClient.execute(httpPost);
			httpEntity = response.getEntity();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			httpPost.abort();
		}

		return httpEntity;
	}
	/**
	 * get方法获取
	 * @param queryStr
	 * @param reqData
	 * @return 返回字符串 SUCCESS 则成功  其他则是错误描述
	 */
	public String getDataFromNet(String queryStr, BaseNetItem reqData) {
   		synchronized (mHttpClient) {
			HttpEntity entity = httpClientExecuteGet(queryStr);
			if (null == entity) {
				return Responses.ENTITY_NONE;
			}

			InputStream instream = null;
			try {
				instream = entity.getContent();
				if (null == instream) {
					return Responses.ERROR_ENTITY_GETCONTENT;
				}

				JsonReader reader = new JsonReader(new InputStreamReader(instream, "UTF-8"));
				Gson gson = new Gson();
				BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
				reader.close();

				if (null == rawData) {
					return Responses.RAWDATA_NONE;
				}

				if (!rawData.isValueData(rawData)) {
					return Responses.RAWDATA_NOT_VALUABLE;
				}

				reqData.setValue(rawData);

				if (!rawData.status.equals("SUCCESS"))
					return Responses.NOT_SUCCESS;

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				return Responses.ERROR_JSE;
			} catch (IOException ex) {
				ex.printStackTrace();
				return Responses.ERROR_IO;
			} finally {
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return Responses.ERROR;
				}
			}
		}
		return Responses.SUCCESS;
	}

	private HttpEntity httpClientExecuteGet(String queryStr) {
		HttpGet httpget = new HttpGet(queryStr);
		HttpEntity entity = null;

		try {
			HttpResponse response = mHttpClient.execute(httpget);
			entity = response.getEntity();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			httpget.abort();
		}

		return entity;
	}

}
