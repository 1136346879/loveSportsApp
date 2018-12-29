/********************************************
 * 文件名		：ActivityMedalInfo.java
 * 版本信息	：1.00
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-4-19 下午3:17:16   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-4-19 下午3:17:16  
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 项目名称：iShangTrunk 类名称：ActivityMedalInfo 类描述： 创建人：Gaofei - 高飞 创建时间：2013-4-19
 * 下午3:17:16 修改人：Gaofei - 高飞 修改时间：2013-4-19 下午3:17:16 修改备注：
 * 
 * @version
 * 
 */
public class ActivityMedalInfo extends BaseNetItem {

	public static String TAG = "ActivityMedalInfo";

	//public String status = "SUCCESS";
	public String dataType = null;
	public String activityid = "-1";
	public ActivityDetailData datavalue = null;
	public List<UserRankInfo> userRank = null;
	public List<GroupRankInfo> groupRank = null;
	
	public ActivityMedalInfo(){
		userRank = new  ArrayList<UserRankInfo>();
		groupRank = new  ArrayList<GroupRankInfo>();
	}

	public void setValue(ActivityMedalInfo data) {
		this.reason = data.reason;
		this.status = data.status;
		this.dataType = data.dataType;
		this.datavalue = data.datavalue;
		this.userRank = data.userRank;
		this.groupRank = data.groupRank;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((ActivityMedalInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ActivityMedalInfo info = (ActivityMedalInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

}
