package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailData_new extends BaseNetItem {

	public int id=0;
	public String company_code="";
	public int company_id=0;
	public String company_name="";
	public int aimstep=0;
	public int topValue=0;
	public String name="";
	public String description="";
	public String startdate="";
	public String enddate="";
	public List<MedalInfo_new> medalinfo=new ArrayList<MedalInfo_new>();
	public List<LevelInfo> levelList=new ArrayList<LevelInfo>();
	public List<RankListInfo_new> userRank = new ArrayList<RankListInfo_new>();
	public List<RankListInfo_new> groupRank = new ArrayList<RankListInfo_new>();
	
	@Override
	public void setValue(BaseNetItem bni) {
		ActivityDetailData_new data=(ActivityDetailData_new)bni;
		this.id=data.id;
		this.company_code=data.company_code;
		this.company_id=data.company_id;
		this.company_name=data.company_name;
		this.aimstep=data.aimstep;
		this.topValue=data.topValue;
		this.name=data.name;
		this.description=data.description;
		this.startdate=data.startdate;
		this.enddate=data.enddate;
		this.medalinfo=data.medalinfo;
		this.levelList=data.levelList;
		this.userRank=data.userRank;
		this.groupRank=data.groupRank;
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ActivityDetailData_new data=(ActivityDetailData_new)bni;
		
		return data.levelList==null;
	}

}
