package cmccsi.mhealth.app.sports.bean;

import cmccsi.mhealth.app.sports.R;
import android.os.Parcel;
import android.os.Parcelable;

public class RunType implements Parcelable {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getImage_type() {
		return image_type;
	}

	public String getTextview_type() {
		return textview_type;
	}

	private int id;
	private int image_type;
	private String textview_type;

	public RunType(int image_type, String textview_type,int id) {
		super();
		this.image_type = image_type;
		this.textview_type = textview_type;
		this.id = id;
	}
	
	public static TYPE ID2String(int id){
		switch (id) {
		case 1:
			return TYPE.walk;
		case 2:
			return TYPE.run;
		case 3:
			return TYPE.bike;
		default:
			break;
		}
		return TYPE.walk;
	}
	
	public static int ID2Image(int id ,int isLoad){
		if(isLoad == 0){
			switch (id) {
			case 1:
				return R.drawable.img_walk;
			case 2:
				return R.drawable.img_run;
			case 3:
				return R.drawable.img_ride;
			default:
				break;
			}
		}else{
			switch (id) {
			case 1:
				return R.drawable.img_walk_unload;
			case 2:
				return R.drawable.img_run_unload;
			case 3:
				return R.drawable.img_ride_unload;
			default:
				break;
			}
		}
		
		return R.drawable.img_walk;
	}

	public static enum TYPE{
		walk,run,bike;
	}
	
	public RunType(Parcel in) {
		image_type = in.readInt();
		textview_type = in.readString();
		id = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(image_type);
		dest.writeString(textview_type);
		dest.writeInt(id);
	}

	public static final Parcelable.Creator<RunType> CREATOR = new Parcelable.Creator<RunType>() {
		public RunType createFromParcel(Parcel in) {
			return new RunType(in);
		}

		public RunType[] newArray(int size) {
			return new RunType[size];
		}
	};
}
