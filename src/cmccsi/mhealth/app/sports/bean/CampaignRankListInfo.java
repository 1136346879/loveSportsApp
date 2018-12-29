package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class CampaignRankListInfo extends BaseNetItem {
	
	public List<RankListInfo_new> userRank;
	public String message;
	
	public CampaignRankListInfo() {
		userRank= new ArrayList<RankListInfo_new>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if (userRank != null) {
			return true;
		}
		return false;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		CampaignRankListInfo data = (CampaignRankListInfo) bni;
		status = data.status;
		message = data.message;
		userRank = data.userRank;
	}

}
