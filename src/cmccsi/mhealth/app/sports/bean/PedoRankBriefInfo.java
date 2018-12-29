package cmccsi.mhealth.app.sports.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class PedoRankBriefInfo extends BaseNetItem implements Serializable{
	
	/**  */
	private static final long serialVersionUID = 1L;
	public ArrayList<PedoRankDetailInfo> rankList;
	public int level; //展示组织级别
	public String areaName; // 地区名称
	public String membername; //用于展示的名称
	public int memberrank; //用于展示的名次
	public int memberstep; //用于展示的步数
	public String dayCount; //
	public String type;
	public int _id;
	public String date;
	public int rankGroup; //排行类别 0-区域排名 1-企业排名
	
	public PedoRankBriefInfo() {
		rankList= new ArrayList<PedoRankDetailInfo>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if (rankList.size() > 0) {
			return true;
		}
		return false;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		PedoRankBriefInfo data = (PedoRankBriefInfo) bni;
		level = data.level;
		areaName = data.areaName;
		membername = data.membername;
		memberrank = data.memberrank;
		memberstep = data.memberstep;
		dayCount = data.dayCount;
		type = data.type;
		date =data.date;
		rankGroup = data.rankGroup;
		rankList = data.rankList;
	}

	public ArrayList<PedoRankDetailInfo> getRankList() {
		return rankList;
	}

	public void setRankList(ArrayList<PedoRankDetailInfo> rankList) {
		this.rankList = rankList;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	

	public String getMembername() {
		return membername;
	}

	public void setMembername(String membername) {
		this.membername = membername;
	}

	public int getMemberrank() {
		return memberrank;
	}

	public void setMemberrank(int memberrank) {
		this.memberrank = memberrank;
	}

	public int getMemberstep() {
		return memberstep;
	}

	public void setMemberstep(int memberstep) {
		this.memberstep = memberstep;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getRankGroup() {
		return rankGroup;
	}

	public void setRankGroup(int rankGroup) {
		this.rankGroup = rankGroup;
	}
	
	

}
