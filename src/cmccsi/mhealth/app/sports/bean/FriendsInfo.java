package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import cmccsi.mhealth.app.sports.common.Logger;

public class FriendsInfo extends BaseNetItem implements Parcelable {
	public static String TAG = "OrgnizeMemberPKInfo";
	//public String status = "SUCCESS";
	public List<OrgnizeMemberInfo> friendslist; // 班组信息列表(多个班组的信息)

	public FriendsInfo() {
		friendslist = new ArrayList<OrgnizeMemberInfo>();
	}

	public void setValue(FriendsInfo data) {
		this.status = data.status;
		this.friendslist = data.friendslist;
	}

	public void initialDate() {
		// TODO 运动强度是否需要初始化
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = friendslist.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = friendslist.get(j).compare(friendslist.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				OrgnizeMemberInfo data = friendslist.get(i);
				friendslist.set(i, friendslist.get(k));
				friendslist.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni) {
			this.setValue((FriendsInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		FriendsInfo info = (FriendsInfo)bni;
		if(info.friendslist == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
