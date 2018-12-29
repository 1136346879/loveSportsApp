package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

public class ClubListInfo extends BaseNetItem {
	public static String TAG = "ClubListInfo";
	public List<ClubData> clublist;

	public ClubListInfo() {
		clublist= new ArrayList<ClubData>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		if (clublist.size() > 0) {
			return true;
		}
		return false;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		ClubListInfo data = (ClubListInfo) bni;
		status = data.status;
		clublist = data.clublist;
	}
}
