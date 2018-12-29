package cmccsi.mhealth.app.sports.phonesteplib;

import cmccsi.mhealth.app.sports.service.ProtectionService;
import cmccsi.mhealth.app.sports.service.StepService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class StepController {
	private Context context;
	private String mAction = "";
	public static final int STEPS_MSG = 1;
	public static final int PACE_MSG = 2;
	public static final int DISTANCE_MSG = 3;
	public static final int SPEED_MSG = 4;
	public static final int CALORIES_MSG = 5;
	public static final int TEST_MSG = 99;

	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setStopAction(String action) {
		this.mAction = action;
	}

	public void startStepService(String action, Bundle extras) {
		// mAction=action;
		// Intent i=new Intent(context,StepService.class);
		// i.putExtras(extras);
		// context.startService(i);
		// Log.d("testing", "trying startStepService");
		Intent mServiceIntent;
		mServiceIntent = new Intent(context, ProtectionService.class);
		context.startService(mServiceIntent);
		Log.d("testing", "trying startStepService");
		if (!StepService.isRunning) {
			Intent service = new Intent(context, StepService.class);
			context.startService(service);
		}
	}

	public void startStepService(String action) {
		// mAction = action;
		// Intent i = new Intent(context, StepService.class);
		// context.startService(i);
		// Log.d("testing", "trying startStepServiceWithoutNotification");
		Intent mServiceIntent;
		mServiceIntent = new Intent(context, ProtectionService.class);
		context.startService(mServiceIntent);
		Log.d("testing", "trying startStepService");
		if (!StepService.isRunning) {
			Intent service = new Intent(context, StepService.class);
			context.startService(service);
		}

	}

	public void stopStepService() {
		ProtectionService.stopService();
//		context.stopService( new Intent(context, ProtectionService.class));
		if (!mAction.equals("")) {
//			unbindStepService();
			context.sendBroadcast(new Intent(mAction));
		}
		Log.d("testing", "trying stopStepService mAction="+mAction);
	}

	public void stopStepServiceWithoutBindService() {
		ProtectionService.stopService();
//		context.stopService( new Intent(context, ProtectionService.class));
		if (!mAction.equals("")) {
			context.sendBroadcast(new Intent(mAction));
			Log.d("testing", "trying stopStepServiceWithoutBindService");
		}
	}

//	public void bindStepService(String action) {
//		context.bindService(new Intent(action), mConnection, Context.BIND_AUTO_CREATE);
//	}
//
//	public void unbindStepService() {
//		context.unbindService(mConnection);
//	}

//	private ServiceConnection mConnection = new ServiceConnection() {
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// mService = ((StepService.StepBinder) service).getService();
//			// mService.registerCallback(mCallback);
//
//		}
//
//		public void onServiceDisconnected(ComponentName className) {
//		}
//	};

}
