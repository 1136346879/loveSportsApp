package cmccsi.mhealth.app.sports.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * 手机记步服务的保护服务，在关闭服务的时候重启服务
 * 
 * @author luckchoudog
 *
 */
public class ProtectionService extends IntentService {
	
	private static boolean checkStepService=true;
	
	public ProtectionService(String name) {
		
		super(name);
		checkStepService=true;
		// TODO Auto-generated constructor stub
	}

	public ProtectionService() {
		this("ProtectionService");
	}

	/**
	 * 
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param className
	 *            ：判断的服务名字：包名+类名
	 * @return true 在运行 false 不在运行
	 */
	public static void stopService() {
		checkStepService=false;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		while (checkStepService) {
			
			if (!StepService.isServiceRunning(getApplicationContext())) {
				Intent service = new Intent(ProtectionService.this, StepService.class);
				startService(service);
			}
			try{
				Thread.sleep(60*1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
