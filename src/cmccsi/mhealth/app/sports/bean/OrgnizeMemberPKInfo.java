package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import cmccsi.mhealth.app.sports.common.Logger;

public class OrgnizeMemberPKInfo extends BaseNetItem implements Parcelable {
	public static String TAG = "OrgnizeMemberPKInfo";
	//public String status = "SUCCESS";
	public List<OrgnizeMemberInfo> orgnizemember; // 班组信息列表(多个班组的信息)

	public OrgnizeMemberPKInfo() {
		orgnizemember = new ArrayList<OrgnizeMemberInfo>();
	}

	public void setValue(OrgnizeMemberPKInfo data) {
		this.status = data.status;
		this.orgnizemember = data.orgnizemember;
	}

	public void initialDate() {
		// TODO 运动强度是否需要初始化
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = orgnizemember.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = orgnizemember.get(j).compare(orgnizemember.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				OrgnizeMemberInfo data = orgnizemember.get(i);
				orgnizemember.set(i, orgnizemember.get(k));
				orgnizemember.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni) {
			this.setValue((OrgnizeMemberPKInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		OrgnizeMemberPKInfo info = (OrgnizeMemberPKInfo)bni;
		if(info.orgnizemember == null){
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
