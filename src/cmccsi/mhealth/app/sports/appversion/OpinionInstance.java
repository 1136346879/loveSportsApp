package cmccsi.mhealth.app.sports.appversion;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OpinionInstance implements Serializable {
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = OpinionInstance.class.hashCode();
	private String feedbackId;// 意见反馈id
	private int feedbackTypeDict;// 意见反馈类型
	private String feedbackTitle;// 意见反馈标题
	private String feedbackContent;// 意见反馈内容
	private String createTime;// 创建时间
	private int replyMark;// 回复标志
	private String replyContent;// 回复内容
	private String replyTime;// 回复时间

	public String getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
	}

	public int getFeedbackTypeDict() {
		return feedbackTypeDict;
	}

	public void setFeedbackTypeDict(int feedbackTypeDict) {
		this.feedbackTypeDict = feedbackTypeDict;
	}

	public String getFeedbackTitle() {
		return feedbackTitle;
	}

	public void setFeedbackTitle(String feedbackTitle) {
		this.feedbackTitle = feedbackTitle;
	}

	public String getFeedbackContent() {
		return feedbackContent;
	}

	public void setFeedbackContent(String feedbackContent) {
		this.feedbackContent = feedbackContent;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getReplyMark() {
		return replyMark;
	}

	public void setReplyMark(int replyMark) {
		this.replyMark = replyMark;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}

	public static OpinionInstance paseGetFeedbackList(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}
		OpinionInstance opinion = new OpinionInstance();
		try {
			opinion.setFeedbackId(jsonObject.getString("feedbackId"));
			opinion.setFeedbackTypeDict(Integer.parseInt(jsonObject.getString("feedbackTypeDict")));
			opinion.setFeedbackTitle(jsonObject.getString("feedbackTitle"));
			opinion.setFeedbackContent(jsonObject.getString("feedbackContent"));
			opinion.setCreateTime(jsonObject.getString("createTime"));
			opinion.setReplyMark(Integer.parseInt(jsonObject.getString("replyMark")));
			opinion.setReplyContent(jsonObject.getString("replyContent"));
			opinion.setReplyTime(jsonObject.getString("replyTime"));
			Log.e("opinion", "feedbackId item = " + jsonObject.getString("feedbackId"));
			Log.e("opinion", "feedbackTypeDict item = " + jsonObject.getString("feedbackTypeDict"));
			Log.e("opinion", "feedbackTitle item = " + jsonObject.getString("feedbackTitle"));
			Log.e("opinion", "feedbackContent item = " + jsonObject.getString("feedbackContent"));
			Log.e("opinion", "createTime item = " + jsonObject.getString("createTime"));
			Log.e("opinion", "replyContent item = " + jsonObject.getString("replyContent"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return opinion;
	}

	public static OpinionInstance paseGetFeedbackList(String json) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return paseGetFeedbackList(jsonObject);
	}
}
