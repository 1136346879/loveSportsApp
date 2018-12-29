package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户的基础信息
 * @author qjj
 *
 */
public class UserBaseInfo {
    public int userid;
    public String phonenum;
	public String name;//张三
	public String nickname;//xx
	public String weight="60";//xx
	public String height="170";//xx
	public String gender="1";//xx
	public String birthday;//xx
	public String score;//xx
	public String avarta;//xx
	public String targetweight;//xx
	public String targetstep;//xx
	public List<UserCompanyInfo> clubarray;
	public LocationInfo locationInfo;
	
	public UserBaseInfo(){
		clubarray = new ArrayList<UserCompanyInfo>();
		locationInfo = new LocationInfo();
	}
}
