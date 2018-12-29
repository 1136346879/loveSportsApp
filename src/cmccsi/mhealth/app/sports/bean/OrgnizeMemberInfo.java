package cmccsi.mhealth.app.sports.bean;

import android.util.Log;

public class OrgnizeMemberInfo {
	public static String TAG = "OrgnizeMemberInfo";

	/**
	 * 成员名称
	 */
	public String membername = "支撑成员"; // 01
	/**
	 * 班组名称
	 */
	public String groupname = "支撑组"; // 01
	/**
	 * 成员在班组内排序号
	 */
	public String memberseq;

	public String rankCountOfToday = "";
	/**
	 * 成员在班组内平均7天运动里程
	 */
	public String member7avgdist;
	/**
	 * 成员在班组内平均7天运动步数
	 */
	public String member7avgstep;
	/**
	 * 成员在班组内总运动里程
	 */
	public String memberscore;
	/**
	 * 成员在班组内信息保留字1
	 */
	// TODO 班组总人数
	public String memberinforev1 = "1";// 0 女 1男

	/**
	 * 成员在班组内信息保留字2
	 */
	public String memberinforev2;
	/**
	 * 对象的手机号（账号）
	 */
	public String friendphone;
	/**
	 * 头像id
	 */
	public String avatar;

	/**
	 * 比较两个数据的顺序
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compare(OrgnizeMemberInfo compare) {
		int x, y;
		try {
			x = Integer.parseInt(memberseq);
			y = Integer.parseInt(compare.memberseq);
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
