/********************************************
 * 文件名		：ActivityDetail.java
 * 版本信息	：1.00
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-3-25 下午4:01:07   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-3-25 下午4:01:07  
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
 *******************************************/
package cmccsi.mhealth.app.sports.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**   
 *    
 * 项目名称：iShangTrunk   
 * 类名称：ActivityDetail   
 * 类描述：   活动详细信息
 * 创建人：Gaofei - 高飞
 * 创建时间：2013-3-25 下午4:01:07   
 * 修改人：Gaofei - 高飞
 * 修改时间：2013-3-25 下午4:01:07   
 * 修改备注：   
 * @version    
 *    
 */
public class ListActivity {
	public static String TAG = "ActivityDetail";
	
	public String activityid = "0";
	public String activityname = "共铸个人健康赛";
	public String activityslogan = "天天争第一";
	public String isfirstday = "1";
	public String activitytype = "-1";
	public String activitystart = "20130301";
	public String activityend = "20130401";
	public String company_name = "中国移动通信有限公司研究院";
	public String aimstep = "8000";
	public String personnum = "30";
	public String personseq = "8";
	public String groupnum = "5";
	public String groupseq = "2";
	public int isJoin=0;//是否参加  0：未参加 1：已参加
	public int isRanked=0;//是否有排名数据  0：无数据 1：有数据


	  /**
   * 比较两个数据的顺序
   * 
   * @param compare
   * @return biger 1 small 0 error -1
   */
  public int compare(ListActivity compare) {
    long dayOfSecondsx, dayOfSecondsy;
    try {

      Date EndDate = df_yyyyMMdd.parse(activityend);
      dayOfSecondsx = EndDate.getTime();
      EndDate = df_yyyyMMdd.parse(compare.activityend);
      dayOfSecondsy = EndDate.getTime();

    } catch (Exception e) {
      Log.e(TAG, "parse error");
      return -1;
    }

    if (dayOfSecondsx > dayOfSecondsy)
      return 1;
    else if (dayOfSecondsx < dayOfSecondsy)
      return 0;
    else
      return -1;
  }

  // **
  private SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
}