package cmccsi.mhealth.app.sports.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛个人信息条目
 * 
 * @author zy
 * 
 */
public class RaceMemberData implements Parcelable {
	public String sex;
	public String memberstepname;
	public String memberstepvalue;
	public String groupname;
	public String groupid;
	public String memberphone;
	public String membername;
	public String avatar;
	public String memberseq;
    public String nickname;
	/**   
     * nickname   
     *   
     * @return the nickname   
     * @since   CodingExample Ver(编码范例查看) 1.0   
     */
    
    public String getNickname() {
        return nickname;
    }

    /**   
     * @param nickname the nickname to set   
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMemberstepname() {
		return memberstepname;
	}

	public void setMemberstepname(String memberstepname) {
		this.memberstepname = memberstepname;
	}

	public String getMemberstepvalue() {
		return memberstepvalue;
	}

	public void setMemberstepvalue(String memberstepvalue) {
		this.memberstepvalue = memberstepvalue;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getMemberphone() {
		return memberphone;
	}

	public void setMemberphone(String memberphone) {
		this.memberphone = memberphone;
	}

	public String getMembername() {
		return membername;
	}

	public void setMembername(String membername) {
		this.membername = membername;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMemberseq() {
		return memberseq;
	}

	public void setMemberseq(String memberseq) {
		this.memberseq = memberseq;
	}

	public int compare(RaceMemberData compare) {
		int x, y;
		try {
			x = Integer.parseInt(memberseq);
			y = Integer.parseInt(compare.memberseq);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		if (x > y)
			return -1;
		else if (x < y)
			return 0;
		else
			return -1;
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
