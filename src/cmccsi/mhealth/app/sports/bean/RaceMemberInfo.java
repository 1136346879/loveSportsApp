package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class RaceMemberInfo extends BaseNetItem implements Parcelable{
	public List<RaceMemberData> racemember;
	public String alreadyin;

	public void setValue(RaceMemberInfo date) {
		this.status = date.status;
		this.racemember = date.racemember;
		this.alreadyin = date.alreadyin;
	}

	public RaceMemberInfo() {
		racemember = new ArrayList<RaceMemberData>();
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((RaceMemberInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return racemember != null;
	}

	public void initialDate() {
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = racemember.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			for (int j = i + 1; j < size; j++) {
				int result = racemember.get(j).compare(racemember.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {
				RaceMemberData data = racemember.get(i);
				racemember.set(i, racemember.get(k));
				racemember.set(k, data);
			}
		}
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
