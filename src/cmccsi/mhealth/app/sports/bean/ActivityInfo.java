/********************************************
 * 文件名		：ActivityInfo.java
 * 版本信息	：1.00
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-3-25 下午3:57:33   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-3-25 下午3:57:33  
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
 * 活动信息列表的类
 * 
 * @version
 * 
 */
public class ActivityInfo extends BaseNetItem {

	public static String TAG = "ActivityInfo";

	//public String status = "SUCCESS";
	public String dataType = "activeinfo"; // 数据类型

	public List<ListActivity> activitynow; // 进行中的活动信息列表(最多3个，)

	public List<ListActivity> activityfinish; // 刚结束的活动信息(1个)
	public List<ListActivity> activityfuture; // 推荐未开始的活动信息(1个)

	public int activitynownum = 0; // 进行中的活动数目
	public int activityoldnum = 0; // 曾经参加过的活动数目
	public int activityfuturenum = 0; // 可以参加过的活动数目

	public ActivityInfo() {
		activitynow = new ArrayList<ListActivity>();
		activityfinish = new ArrayList<ListActivity>();
		activityfuture = new ArrayList<ListActivity>();
	}

	public void setValue(ActivityInfo data) {
		this.status = data.status;
		this.activitynow = data.activitynow;
		this.activityfinish = data.activityfinish;
		this.activityfuture = data.activityfuture;

		this.activitynownum = data.activitynownum;
		this.activityoldnum = data.activityoldnum;
		this.activityfuturenum = data.activityfuturenum;
	}

	public void initialData() {
		// TODO 运动强度是否需要初始化
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = activitynow.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = activitynow.get(j).compare(activitynow.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				ListActivity data = activitynow.get(i);
				activitynow.set(i, activitynow.get(k));
				activitynow.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			this.setValue((ActivityInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ActivityInfo info = (ActivityInfo)bni;
		if(info.activityfuture == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
}