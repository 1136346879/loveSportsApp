package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.common.Logger;

public class GroupMemberPkInfo extends BaseNetItem {
	public static String TAG = "GroupMemberPkInfo";
	//public String status = "SUCCESS";
	public List<GroupMemberInfo> groupmember; // 班组信息列表(多个班组的信息)

	public GroupMemberPkInfo() {
		groupmember = new ArrayList<GroupMemberInfo>();
	}

	public void setValue(GroupMemberPkInfo data) {
		this.status = data.status;
		this.groupmember = data.groupmember;
	}

	public void initialDate() {
		// TODO 运动强度是否需要初始化
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = groupmember.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = groupmember.get(j).compare(groupmember.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				GroupMemberInfo data = groupmember.get(i);
				groupmember.set(i, groupmember.get(k));
				groupmember.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			this.setValue((GroupMemberPkInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		GroupMemberPkInfo info = (GroupMemberPkInfo)bni;
		if(info.groupmember == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
}
