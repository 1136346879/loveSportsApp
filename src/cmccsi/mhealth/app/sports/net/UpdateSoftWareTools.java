package cmccsi.mhealth.app.sports.net;

import cmccsi.mhealth.app.sports.bean.UpdateVersionJson;

public class UpdateSoftWareTools {
	public static String newVerInfo = "";
	public static String newVerName = "";
	public static int newVerCode = 0;
	public static String download = "";

	public static boolean getServerVerCode() {
		UpdateVersionJson reqData = new UpdateVersionJson();
		if (0 != DataSyn.getInstance().updateVersion(reqData)) {
			newVerCode = -1;
			newVerName = "";
			return false;
		}

		if (null != reqData) {
			newVerCode = Integer.valueOf(reqData.verCode);
			newVerName = reqData.verName;
			newVerInfo = reqData.updateInfo;
			download = reqData.download;
		}

		return true;
	}
	
	public static boolean isNewVersionAvaliable(String currentVersion){
	    UpdateVersionJson reqData = new UpdateVersionJson();
        if (0 != DataSyn.getInstance().updateVersion(reqData)) {
            newVerCode = -1;
            newVerName = "";
            return false;
        }

        if (null != reqData) {
        	newVerCode = Integer.valueOf(reqData.verCode);
            newVerName = reqData.verName;
            newVerInfo = reqData.updateInfo;
            download = reqData.download;
        }
        
        String[] newVersionArr = newVerName.split("\\.");
        String[] oldVersionArr = currentVersion.split("\\.");
        if (newVersionArr.length > oldVersionArr.length) {
            return true;
        }
        
        for (int i = 0; i < newVersionArr.length; i++) {
            if (compareNumber(newVersionArr[i], oldVersionArr[i]) ==1) {
                return true;
            }else if (compareNumber(newVersionArr[i], oldVersionArr[i]) ==-1) {
                return false;
            }
        }

        return false;
	}
	
	/**
	 * 比较两个数字大小。当num1大于num2时，返回1；当num1等于num2时，返回0；当num1小于num2时，返回-1。
	 * @param num1
	 * @param num2
	 * @return
	 */
	private static int compareNumber(String num1,String num2){
	    int i1 = Integer.parseInt(num1);
	    int i2 = Integer.parseInt(num2);
        if (i1 > i2) {
            return 1;
        }else if (i1 == i2) {
            return 0;
        }else {
            return -1;
        }
	}

}
