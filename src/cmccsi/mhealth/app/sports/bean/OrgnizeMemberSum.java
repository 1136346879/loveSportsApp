package cmccsi.mhealth.app.sports.bean;

/**
 * 项目名称：i-shang0130 类名称：GroupMemberSum 类描述： 获取个人排名的总数 创建人：Qiujunjie - 邱俊杰
 * 创建时间：2013-3-18 下午2:27:52 修改人：Qiujunjie - 邱俊杰 修改时间：2013-3-18 下午2:27:52 修改备注：
 * 
 * @version
 * 
 */
public class OrgnizeMemberSum extends BaseNetItem {
	public static String TAG = "GroupMemberSum";
	//public String status = "SUCCESS";
	public int orgnizememsum;

	public void setValue(OrgnizeMemberSum data) {
		this.status = data.status;
		this.orgnizememsum = data.orgnizememsum;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((OrgnizeMemberSum) bni);
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
//		OrgnizeMemberSum info = (OrgnizeMemberSum)bni;
//		if(info.orgnizememsum == 0)
//			return false;
		return true;
	}
}
