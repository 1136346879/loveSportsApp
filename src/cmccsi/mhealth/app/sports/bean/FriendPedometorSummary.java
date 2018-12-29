/********************************************
 * File Name：PedometorInfo.java
 * Version	：1.00
 * Author	：Gaofei - 高飞
 * Date		：2012-10-30
 * LastModify：2012-10-30
 * Functon	：功能描述
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 上传friend简要包数据
 * 
 */
public class FriendPedometorSummary extends BaseNetItem implements Parcelable {
	public static String TAG = "FriendPedometorInfo";

	public List<PedometorSummary> friendsinfo;

	public FriendPedometorSummary() {
		friendsinfo = new ArrayList<PedometorSummary>();
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		FriendPedometorSummary info = (FriendPedometorSummary) bni;
		if (info.friendsinfo == null) {
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		FriendPedometorSummary data = (FriendPedometorSummary) bni;
		status = data.status;
		reason = data.reason;
		friendsinfo = data.friendsinfo;
	}

	public void initialDate() {
		sortFriendPedometorSummary();
	};
	
	private void sortFriendPedometorSummary() {
		int size = friendsinfo.size();
		if (size <= 0)
			return;
		for (int i = 0; i < size; i++) {
			int k = i;
			for (int j = i + 1; j < size; j++) {
				int result = friendsinfo.get(j).compareDate(friendsinfo.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				PedometorSummary data = friendsinfo.get(i);
				friendsinfo.set(i, friendsinfo.get(k));
				friendsinfo.set(k, data);
			}
		}
	}

	public FriendPedometorSummary(Parcel in) {
		friendsinfo = in.readArrayList(PedometorSummary.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeList(friendsinfo);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public FriendPedometorSummary createFromParcel(Parcel in) {
			return new FriendPedometorSummary(in);
		}

		@Override
		public FriendPedometorSummary[] newArray(int size) {
			return new FriendPedometorSummary[size];
		}
	};
}
