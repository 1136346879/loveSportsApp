package cmccsi.mhealth.app.sports.bean;

import android.util.Log;

public class GroupInfo {
	public static String TAG = "GroupInfo";
	public String groupid = "8";
	
	/**
	 * 班组名称
	 */
	public String groupname = "11"; // 01
	/**
	 * 班组排序号
	 */
	public String groupseq;
	/**
	 * 班组平均7天运动里程
	 */
	public String group7avgdist;
	/**
	 * 班组平均7天运动步数
	 */
	public String group7avgstep;
	/**
	 * 班组总运动里程
	 */
	public String groupscore;
	/**
	 * 组信息保留字1
	 */
	//TODO 班组总人数
	public String groupinforev1;
	/**
	 * 组信息保留字2
	 */
	public String groupinforev2;
	
	/**
	 * 比较两个数据的顺序
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compare(GroupInfo compare) {
		int x, y;
		try {
			x = Integer.parseInt(groupseq);
			y = Integer.parseInt(compare.groupseq);
		} catch (Exception e) {
			Log.e(TAG, "parse error");
			return -1;
		}

		if (x > y)
			return -1;
		else if (x < y)
			return 0;
		else
			return -1;
	}
	
}
