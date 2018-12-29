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

import cmccsi.mhealth.app.sports.common.Logger;

/**
 * 
 * 上传简要包数据
 * 
 */
public class PedometorInfo extends BaseNetItem {
	public static String TAG = "PedometorInfo";

	// public String status = "SUCCESS";
	public String dataType = "1";
	public List<DataPedometor> datavalue;
	public String date = "20121002";
	public String phoneNum = "13881657386";
	public String targetstep;

	public PedometorInfo() {
		datavalue = new ArrayList<DataPedometor>();
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		PedometorInfo info = (PedometorInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		PedometorInfo data = (PedometorInfo) bni;

		status = data.status;
		reason = data.reason;

		dataType = data.dataType;
		datavalue = data.datavalue;
		date = data.date;
		phoneNum = data.phoneNum;
		targetstep = data.targetstep;
	}

	public void initialDate() {
		try {
//			Logger.i(TAG, "step is now on ---> "+1);
//			Logger.i(TAG, "datavalue=="+datavalue.toString());
//			Logger.i(TAG, "step is now on ---> "+2);
//			Logger.i(TAG, "datavalue.get(0)=="+datavalue.get(0).toString());
//			Logger.i(TAG, "datavalue.get(0).createtime=="+datavalue.get(0).createtime.toString());
//			Logger.i(TAG, "step is now on ---> "+3);
			
			// 初始化时间
			for (int i = 0; i < datavalue.size(); i++) {
				datavalue.get(i).createtime = datavalue.get(i).createtime.replace(
						"T", " ").replace("Z", "");
				// datavalue.get(i)。data。cal = datavalue.get(i)。data。cal;
				// 运动强度信息统一乘2
				// datavalue.get(i).data.strength2 = datavalue.get(i).data.strength2
				// * 2;
				// datavalue.get(i).data.strength3 = datavalue.get(i).data.strength3
				// * 2;
				// datavalue.get(i).data.strength4 = datavalue.get(i).data.strength4
				// * 2;
			}
		} catch (Exception e) {
//			Logger.i(TAG, "step is now on ---> "+4);
			e.printStackTrace();
		}
//		Logger.i(TAG, "step is now on ---> "+5);
		// 按照时间排序
		sortDataPedometor();
	}

	private void sortDataPedometor() {
		int size = datavalue.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = datavalue.get(j).compareCreatetime(
						datavalue.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				DataPedometor data = datavalue.get(i);
				datavalue.set(i, datavalue.get(k));
				datavalue.set(k, data);
			}
		}
	}
}
