/********************************************
 * 文件名		：ActivityMedalInfo.java
 * 版本信息	：1.00
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-4-19 下午3:07:29   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-4-19 下午3:07:29  
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;

import java.util.ArrayList;
import java.util.List;

/**   
 *    
 * 项目名称：iShangTrunk   
 * 类名称：ActivityMedalInfo   
 * 类描述：   
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-4-19 下午3:07:29   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-4-19 下午3:07:29   
 * 修改备注：   
 * @version    
 *    
 */
public class ActivityDetailData {
	public String myname;//= "高飞";
	public String mygroup;//="平台开发"; 
	public String myuserrank;
	public String mygrouprank;
	public String avgstep;//= "8864"; 
	public String ratescore;//="80"; 
	public String hitduration;//="13"; 
	public String groupavgstep;//= "8864"; 
	public String groupratescore;//="80"; 
	public String wcl="";
	public String groupwcl="";
	public String aimStep="";

	public List<MedalInfo>  medalinfo;
	public ActivityDetailData(){
		medalinfo = new  ArrayList<MedalInfo>();
	}
	public void setValue(ActivityDetailData data) {
		this.myname = data.myname;
		this.mygroup = data.mygroup;
		this.avgstep = data.avgstep;
		this.myuserrank=data.myuserrank;
		this.mygrouprank=data.mygrouprank;
		this.ratescore = data.ratescore;
		this.hitduration = data.hitduration;
		this.groupavgstep = data.groupavgstep;
		this.groupratescore = data.groupratescore;
		this.medalinfo = data.medalinfo;
	}

}
