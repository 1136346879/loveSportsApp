/********************************************
 * File Name：UserRegInfo.java
 * Version	：1.00
 * Author	：Gaofei - 高飞
 * Date		：2012-4-16
 * LastModify：2012-4-16
 * Functon	：功能描述
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;


/**
 * 类 UserRegInfo company: 中国移动研究院普适计算与通信研究中心
 * 
 * @author by:gaofei-高飞
 * @date 2012-4-16 下午8:29:44 version 1.0 describe:
 */
public class UserRegInfo extends BaseNetItem{
	public UserBaseInfo personprofile;
	
	public UserRegInfo() {
		super();
		personprofile = new UserBaseInfo();
	}
	@Override
	public void setValue(BaseNetItem bni) {
		UserRegInfo info = (UserRegInfo)bni;
		this.personprofile = info.personprofile;
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return true;
	}
}
