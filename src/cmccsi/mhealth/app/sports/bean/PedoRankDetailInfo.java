package cmccsi.mhealth.app.sports.bean;

import java.io.Serializable;


/**
 * 运动排名信息存储bean
 * 
 * @type PedoRankDetailInfo TODO
 * @author shaoting.chen
 * @time 2015年3月13日下午3:14:27
 */
public class PedoRankDetailInfo implements Serializable{
	/**  */
	private static final long serialVersionUID = 1L;

	public static String TAG = "AreaInfo";

	// public String status = "SUCCESS";
	public int rank; // 名次
	public String name = null; // 用户名或市县区名
	public String group = null; // 所属区域
	public int step; //步数
	public String dayCount; //
	public String type;
	public int level;
	public int _id;
	public String date;
	public int rankGroup; //排行类别 0-区域排名 1-企业排名

	public PedoRankDetailInfo(int rank, String name, String group, int step, String dayCount, String type, int level, int rankGroup, String date) {
		this.rank = rank;
		this.name = name;
		this.group = group;
		this.step = step;
		this.dayCount = dayCount;
		this.type = type;
		this.level = level;
		this.rankGroup = rankGroup;
		this.date = date;
	}
	public PedoRankDetailInfo() {
		
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public String getDayCount() {
		return dayCount;
	}
	public void setDayCount(String dayCount) {
		this.dayCount = dayCount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getRankGroup() {
		return rankGroup;
	}
	public void setRankGroup(int rankGroup) {
		this.rankGroup = rankGroup;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
}
