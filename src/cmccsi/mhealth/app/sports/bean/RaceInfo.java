package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RaceInfo extends BaseNetItem implements Parcelable {
	public List<RaceData> racelistinfo;
	public String lastid;

	public void setValue(RaceInfo date) {
		this.status = date.status;
		this.racelistinfo = date.racelistinfo;
		this.lastid = date.lastid;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((RaceInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return racelistinfo != null;
	}

	public RaceInfo() {
		racelistinfo = new ArrayList<RaceData>();
	}
	public RaceInfo(Parcel in) {
		racelistinfo = in.readArrayList(RaceInfo.class.getClassLoader());
		lastid = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeList(racelistinfo);
		out.writeString(lastid);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public RaceInfo createFromParcel(Parcel in) {
			return new RaceInfo(in);
		}

		@Override
		public RaceInfo[] newArray(int size) {
			return new RaceInfo[size];
		}
	};

}
