package cmccsi.mhealth.app.sports.broadcast;

import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机启动 广播接收
 * 
 * @type BootCompletedReceiver TODO
 * @author shaoting.chen
 * @time 2015年6月8日上午10:22:39
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
        if(PreferencesUtils.getBoolean(context, SharedPreferredKey.SETTING_BOOT_RUN, true)){
        	if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
    			// better delay some time.
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			Intent _intent = new Intent(context, cmccsi.mhealth.app.sports.service.ProtectionService.class);
//    			_intent.putExtra("from", "BootCompletedReceiver");
//    			_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startService(_intent);
    		}
		}
	}

}
